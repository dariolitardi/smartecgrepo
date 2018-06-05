#include <SPI.h>
#include <SPBTLE_RF.h>
#include "sensor_service.h"

#define PIN_BLE_SPI_MOSI   (11)
#define PIN_BLE_SPI_MISO   (12)
#define PIN_BLE_SPI_SCK    (3)

#define PIN_BLE_SPI_nCS    (A1)
#define PIN_BLE_SPI_RESET  (7)
#define PIN_BLE_SPI_IRQ    (A0)

#define PIN_BLE_LED    (0xFF)

#define RETRY 5000

// Configure BTLE_SPI
SPIClass BTLE_SPI(PIN_BLE_SPI_MOSI, PIN_BLE_SPI_MISO, PIN_BLE_SPI_SCK);

// Configure BTLE pins
SPBTLERFClass BTLE(&BTLE_SPI, PIN_BLE_SPI_nCS, PIN_BLE_SPI_IRQ, PIN_BLE_SPI_RESET, PIN_BLE_LED);

const char *name = "BlueNRG";
uint8_t SERVER_BDADDR[] = {0x12, 0x34, 0x00, 0xE1, 0x80, 0x03};

Heartbeat heartbeat_data;
//end bluetooth

#define PIN 2

#define MAX 2000
#define MIN -1
#define SHORT_PAUSE 2000
#define LONG_PAUSE 300000//5 minutes

#define MIN_MAX_SIZE 3
#define IBI_THRESHOLD 3000

#define NOISE_THRESHOLD 15

int Signal;

unsigned long pause = SHORT_PAUSE;

int minValues[MIN_MAX_SIZE];
int maxValues[MIN_MAX_SIZE];

unsigned long lastTimeThreshold = 0;
int threshold = 0;
int goodValues = 0;
bool goodThreshold = false;

bool detected = false;

unsigned long now = 0;

unsigned long previousTimeIBI = 0;
unsigned long lastTimeIBI = 0;

int IBI10[10];
int i = 0;

int IBI3[3];
int j = 0;

short ERR = 0;
short meanOfLast3 = 0;
short lastIBI = 0;
short secondToLastIBI = 0;
short thirdToLastIBI = 0;
short meanOfLast10 = 0;

int noise = 0;

void setup() {
  Serial.begin(9600);

  startHeartbeatService();

  resetMinMax();

  pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
  BTLE.update();

  Signal = analogRead(PIN);
  printSignal();

  setMinMax();
  setNewThreshold();

  if (goodThreshold) {
    detectHeartBeat();
  }

  delay(20);
}

void startHeartbeatService() {
  bool initialized = false;
  do {
    if (BTLE.begin() == SPBTLERF_ERROR) {
      Serial.println("Bluetooth module configuration error!");
      delay(RETRY);
    } else {
      Serial.println("Bluetooth module configuration completed!");
      initialized = true;
    }
  } while (!initialized);

  initialized = false;
  do {
    if (SensorService.begin(name, SERVER_BDADDR)) {
      Serial.println("Sensor service configuration error!");
      delay(RETRY);
    } else {
      Serial.println("Sensor service configuration completed!");
      initialized = true;
    }
  } while (!initialized);

  initialized = false;
  do {
    if (SensorService.Add_Heartbeat_Service() != BLE_STATUS_SUCCESS) {
      Serial.println("Error while adding Heartbeat service.");
      delay(RETRY);
    } else {
      Serial.println("Heart service added successfully.");
      initialized = true;
    }
  } while (!initialized);
}

void setNewThreshold() {
  now = millis();

  if (now - lastTimeThreshold >= pause || (lastIBI > 0 && now - lastTimeIBI >= IBI_THRESHOLD)) {
    lastTimeThreshold = now;

    if (SensorService.isConnected() == FALSE) {
      SensorService.setConnectable();
    }

    int meanMin = meanMinMax(minValues, MAX);
    int meanMax = meanMinMax(maxValues, MIN);

    if (meanMin + 40 < meanMax && meanMin >= 300) {
      goodThreshold = true;
      threshold = meanMax - 35;

      printThreshold(threshold, meanMin, meanMax);

    } else {
      goodValues = 0; //reset arrays of IBI
      i = 0; //reset IBI10
      j = 0; //reset IBI3
      goodThreshold = false;
      previousTimeIBI = 0;

      ERR = 1;

      meanOfLast3 = 0;
      lastIBI = 0;
      secondToLastIBI = 0;
      thirdToLastIBI = 0;
      meanOfLast10 = 0;

      pause = SHORT_PAUSE;

      printSensorError(meanMin, meanMax);
      sendToBluetooth();
    }

    resetMinMax();
  }
}

void detectHeartBeat() {
  if (Signal >= threshold) {
    if (!detected) {
      detected = true;

      printHeartBeat();

      if (getIBI()) {
        digitalWrite(LED_BUILTIN, HIGH);
        getMeans(lastIBI);
        ERR = 0;
        sendToBluetooth();
      }

    } else {
      digitalWrite(LED_BUILTIN, LOW);
    }
    noise = 0;
  } else {
    if (noise > NOISE_THRESHOLD) {
      detected = false;
      noise = 0;
    }
    noise++;
    digitalWrite(LED_BUILTIN, LOW);
  }
}

bool getIBI() {
  lastTimeIBI = millis();

  bool accept = false;

  if (previousTimeIBI > 0) {
    thirdToLastIBI = secondToLastIBI;
    secondToLastIBI = lastIBI;
    lastIBI = lastTimeIBI - previousTimeIBI;

    accept = lastIBI <= IBI_THRESHOLD;

    if (accept) {
      printIBIs(lastIBI, secondToLastIBI, thirdToLastIBI);
    } else {
      printDiscardedIBI(lastIBI);
    }
  }

  previousTimeIBI = lastTimeIBI;

  return accept;
}

void getMeans(short lastIBI) {
  goodValues++;

  IBI3[j] = lastIBI;
  j = j == (3 - 1) ? 0 : (j + 1);

  IBI10[i] = lastIBI;
  i = i == (10 - 1) ? 0 : (i + 1);

  printIBIs();

  if (goodValues >= 3) {
    meanOfLast3 = 60000 / meanLast3Beats();
    printMeanOfLast3(meanOfLast3);

    if (goodValues >= 10) {
      pause = LONG_PAUSE;
      meanOfLast10 = 60000 / meanLast10Beats();
      printMeanOfLast10(meanOfLast10);
    }
  }
}

void sendToBluetooth() {
  if (SensorService.isConnected() == TRUE) {

    heartbeat_data.ERR = ERR;
    heartbeat_data.meanOfLast3 = meanOfLast3;
    heartbeat_data.lastIBI = lastIBI;
    heartbeat_data.secondToLastIBI = secondToLastIBI;
    heartbeat_data.thirdToLastIBI = thirdToLastIBI;
    heartbeat_data.meanOfLast10 = meanOfLast10;

    SensorService.Heartbeat_Notify(&heartbeat_data);
  }
}
