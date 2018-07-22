#include "dataset.h"
#include "KNN.h"

/* Bluetooth module configuration */
#include <SPI.h> // for the bluetooth module
#include <SPBTLE_RF.h> // for the bluetooth module
#include "sensor_service.h" // for the bluetooth module

#define PIN_BLE_SPI_MOSI   (11) // for the bluetooth module
#define PIN_BLE_SPI_MISO   (12) // for the bluetooth module
#define PIN_BLE_SPI_SCK    (3) // for the bluetooth module

#define PIN_BLE_SPI_nCS    (A1) // for the bluetooth module
#define PIN_BLE_SPI_RESET  (7) // for the bluetooth module
#define PIN_BLE_SPI_IRQ    (A0) // for the bluetooth module

#define PIN_BLE_LED    (0xFF) // for the bluetooth module

#define RETRY 5000 // for the bluetooth module

// Configure BTLE_SPI
SPIClass BTLE_SPI(PIN_BLE_SPI_MOSI, PIN_BLE_SPI_MISO, PIN_BLE_SPI_SCK); // for the bluetooth module

// Configure BTLE pins
SPBTLERFClass BTLE(&BTLE_SPI, PIN_BLE_SPI_nCS, PIN_BLE_SPI_IRQ, PIN_BLE_SPI_RESET, PIN_BLE_LED); // for the bluetooth module

const char *name = "BlueNRG"; // for the bluetooth module
uint8_t SERVER_BDADDR[] = {0x12, 0x34, 0x00, 0xE1, 0x80, 0x03}; // for the bluetooth module

Heartbeat heartbeat_data; // for the bluetooth module
/* End of the bluetooth module configuration */

#define PIN 2

#define MAX 2000
#define MIN -1
#define SHORT_PAUSE 2000
#define LONG_PAUSE 300000 // 5 minutes

#define MIN_MAX_SIZE 3
#define IBI_THRESHOLD 3000

#define NOISE_THRESHOLD 15

// store the signal obtained from the heartbeat sensor
int Signal;

// the interval of time after we begin to look for a new threshold
unsigned long pause = SHORT_PAUSE;

// store the 3 minimum values returned from the heartbeat sensor
// we use 3 values and not just 1 to reduce noise
int minValues[MIN_MAX_SIZE];

// store the 3 maximum values returned from the heartbeat sensor
// we use 3 values and not just 1 to reduce noise
int maxValues[MIN_MAX_SIZE];

// store the time we set the last threshold
unsigned long lastTimeThreshold = 0;

// threshold value
int threshold = 0;

// number of beats detected
int goodValues = 0;

// true if the threshold is set
bool goodThreshold = false;

// is true when a beat is detected
bool detected = false;

// store the current time
unsigned long now = 0;

// time in ms of the second to last detectedbeat
unsigned long previousTimeIBI = 0;

// time in ms of the last detected beat
unsigned long lastTimeIBI = 0;

// array of the last 10 beats
int IBI10[10];
int i = 0; // index

// array of the last 3 beats
int IBI3[3];
int j = 0; // index

// when a beat is detected this value is 0, otherwise is 1
short ERR = 0;

// required to check fibrillation:
short meanOfLast3 = 0; // The average of the last three beats
short lastIBI = 0; // The last beat
short secondToLastIBI = 0; // The second to last beat
short thirdToLastIBI = 0; // The third to last beat
short meanOfLast10 = 0; // The average of the last ten beats

// store the result of the knn algorithm
int fibrillation;

// used to avoid noise when we need to detect a new beat
int noise = 0;

/*
  The setup function
*/
void setup() {
  Serial.begin(9600);

  // Bluetooth module initialization
  startHeartbeatService();

  // initialize the minimum value and the maximum value
  resetMinMax();

  // initialize the builtin led so it can be turned on when a beat is detected
  pinMode(LED_BUILTIN, OUTPUT);
}

/*
  The loop function
*/
void loop() {
  BTLE.update();

  // read signal from the heartbeat sensor
  Signal = analogRead(PIN);
  // print the signal value in the serial monitor (for debugging purposes)
  printSignal();

  // store the minimum and the maximum vales returned from the sensor
  setMinMax();

  // set a new threshold (used to detect a new beat)
  setNewThreshold();

  // if the threshold is set
  if (goodThreshold) {
    // start to detect beats
    detectHeartBeat();
  }

  delay(20);
}

/*
  Function that initializes the Bluetooth module
*/
void startHeartbeatService() {
  bool initialized = false;
  do {
    if (BTLE.begin() == SPBTLERF_ERROR) {
      Serial.println("Bluetooth module configuration error!");
      delay(RETRY); // if there is an error retry in 5 seconds
    } else {
      Serial.println("Bluetooth module configuration completed!");
      initialized = true;
    }
  } while (!initialized);

  initialized = false;
  do {
    if (SensorService.begin(name, SERVER_BDADDR)) {
      Serial.println("Sensor service configuration error!");
      delay(RETRY); // if there is an error retry in 5 seconds
    } else {
      Serial.println("Sensor service configuration completed!");
      initialized = true;
    }
  } while (!initialized);

  initialized = false;
  do {
    if (SensorService.Add_Heartbeat_Service() != BLE_STATUS_SUCCESS) {
      Serial.println("Error while adding Heartbeat service.");
      delay(RETRY); // if there is an error retry in 5 seconds
    } else {
      Serial.println("Heart service added successfully.");
      initialized = true;
    }
  } while (!initialized);
}

/*
  Function that looks for a new threshold every two seconds;
*/
void setNewThreshold() {
  now = millis();

  // if the time passed from the last time a new threshold is greater than the value 'pause', and
  // if
  if (now - lastTimeThreshold >= pause || (lastIBI > 0 && now - lastTimeIBI >= IBI_THRESHOLD)) {
    // update the time when we set the last threshold
    lastTimeThreshold = now;

    // if the bluetooth module is not connected to a client set the module in discoverable mode
    if (SensorService.isConnected() == FALSE) {
      SensorService.setConnectable();
    }

    // evaluate minimum value starting from the 'minValues' array
    int meanMin = meanMinMax(minValues, MAX);

    // evaluate the maximum value starting from the 'maxValues' array
    int meanMax = meanMinMax(maxValues, MIN);

    // if the difference between min and max is greater than 40 and the min value is
    // greater than 300 then we can set a new threshold.
    // Otherwise the the algorithm is not able to detect beats
    if (meanMin + 40 < meanMax && meanMin >= 300) {
      // a threshold is set
      goodThreshold = true;
      // update threshold value
      threshold = meanMax - 35;

      // print the new threshold value, the new mimumum value and the new maxium value in the serial monitor (for debugging purposes)
      printThreshold(threshold, meanMin, meanMax);

    } else {
      /* reset all the values since there is a sensor error*/
      ERR = 1;

      goodValues = 0; // reset number of detected beats
      i = 0; //reset index of array 'IBI10'
      j = 0; //reset index of array 'IBI3'
      goodThreshold = false; // reset threshold
      previousTimeIBI = 0;

      meanOfLast3 = 0;
      lastIBI = 0;
      secondToLastIBI = 0;
      thirdToLastIBI = 0;
      meanOfLast10 = 0;

      // reduce the pause to a smaller value because we have to look for a new threshold
      pause = SHORT_PAUSE;

      // print the error in the serial monitor (for debugging purposes)
      printSensorError(meanMin, meanMax);

      // inform the bluetooth client about the sensor error
      sendToBluetooth();
    }

    // reset the mimimum and the maximum values
    resetMinMax();
  }
}

/*
  Function that detects a new beat
*/
void detectHeartBeat() {

  // if the Signal value is equal of greather than the threshold value then we get a new beat
  if (Signal >= threshold) {

    // if we have not detected any beat
    if (!detected) {
      detected = true;

      // print the number of the detected beat (for debugging purposes)
      printHeartBeat();

      // if the last beat has not been discarded (it is not an error of the sensor)
      if (getIBI()) {

        // turn on the buildin led
        digitalWrite(LED_BUILTIN, HIGH);

        // evaluate the means
        getMeans(lastIBI);

        // there is no sensor error
        ERR = 0;

        // perform the knn to check fibrillation
        checkFibrillation();

        //send the data to the Bluetooth client (if connected to a client)
        sendToBluetooth();
      }

    } else {

      // turn off the buildin led
      digitalWrite(LED_BUILTIN, LOW);

    }

    // reset the noise value
    noise = 0;

  } else {

    // if the sensor return 15 values that are smaller than the threshold, reset the detect value to detect a new beat
    // we need 15 values and not just 1 because the sensor can be noisy
    if (noise > NOISE_THRESHOLD) {// NOISE_THRESHOLD = 15
      // reset the
      detected = false;
      noise = 0;
    }

    // increase the noise value
    noise++;

    // turn off the buildin led
    digitalWrite(LED_BUILTIN, LOW);
  }
}

/*
  Function that updates:
    the value of the last beat
    the value of the second to last beat
    the value of the third to last beat
*/
bool getIBI() {

  // get the time in ms
  lastTimeIBI = millis();

  bool accept = false;

  // if this is not the first beat
  if (previousTimeIBI > 0) {

    // update third to last beat value
    thirdToLastIBI = secondToLastIBI;

    // update second to last beat value
    secondToLastIBI = lastIBI;

    // update last beat value
    lastIBI = lastTimeIBI - previousTimeIBI;

    // if the time in ms of the last beat is less
    // than a threshold, it's likely that it's a sensor error so discard the value
    accept = lastIBI <= IBI_THRESHOLD;

    // print the beats in ms in the serial monitor (for debugging purposes)
    if (accept) {
      // print the last beat, the second to last beat, and the third to last beat in the serial monitor (for debugging purposes)
      printIBIs(lastIBI, secondToLastIBI, thirdToLastIBI);
    } else {
      // print pnly the discarded beat in ms in the serial monitor (for debugging purposes)
      printDiscardedIBI(lastIBI);
    }
  }

  // update the previous beat value
  previousTimeIBI = lastTimeIBI;

  return accept;
}

/*
  Function that:
    store the last beat
    evaluate the mean of the last 3 beats
    evaluate the mean of the last 10 beats
*/
void getMeans(short lastIBI) {

  // increase the number of beats detected
  goodValues++;

  // add the last beat to the array of the last 3 beats
  IBI3[j] = lastIBI;
  j = j == (3 - 1) ? 0 : (j + 1);

  // add the last beat to the array of the last 10 beats
  IBI10[i] = lastIBI;
  i = i == (10 - 1) ? 0 : (i + 1);

  // print in the serial monitor the time in ms from the previous beat to the last beat (for debugging purposes)
  printIBIs();

  // if at least 3 beats are detected
  if (goodValues >= 3) {

    // evaluate the mean of the last 3, using the 'IBI3' array
    meanOfLast3 = meanLast3Beats();

    // print the result in the serial monitor (for debugging purposes)
    printMeanOfLast3(meanOfLast3);

    // if at least 10 beats are detected
    if (goodValues >= 10) {

      // the threshold value is good (because we detected 10 beats),
      // so we don't need to look for a new threshold -> extend the time to look for the next threshold
      pause = LONG_PAUSE;

      // evaluate the mean of the last 10, using the 'IBI10' array
      meanOfLast10 = meanLast10Beats();

      // print the result in the serial monitor (for debugging purposes)
      printMeanOfLast10(meanOfLast10);
    }
  }
}

/*
   Function that checks the fibrillation using the KNN algorithm
*/
void checkFibrillation() {

  // to check fibrillation 10 heartbeats are required, so return if we have less than 10 values
  if (meanOfLast10 == 0) {
    fibrillation = 0;
    return;
  }

  //create an array of the five values required to check fibrillation and perform KNN
  double hb[columns] = {meanOfLast3, lastIBI, secondToLastIBI, thirdToLastIBI, meanOfLast10};

  // set the fibrillation value to 1 if is the knn classified the last beat as fibrillation, otherwise is 0
  fibrillation = performKNN((double*) instances, classes, hb, 1, rows, columns);

  // print the result in the serial monitor (for debugging purposes)
  printFibrillation(fibrillation);
}

/*
  Function that sends some data to the client via Bluetooth
*/
void sendToBluetooth() {

  // if the bluetooth module is connected to a client send data to it
  if (SensorService.isConnected() == TRUE) {

    /* values sent to the client */

    // when a beat is detected this value is 0, otherwise is 1
    heartbeat_data.ERR = ERR;

    // beats per minute (is evalueted using the last 10 beats)
    heartbeat_data.bpm = 60000 / meanOfLast10;

    // 1 if is the knn classified the last beat as fibrillation, otherwise is 0
    heartbeat_data.fibrillation = fibrillation;


    //send the data to the Bluetooth client
    SensorService.Heartbeat_Notify(&heartbeat_data);
  }
}
