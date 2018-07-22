#define LEVEL INFO

#define TRACE 0
#define DEBUG 1
#define INFO 2
#define OFF 3

/*
   Print function
*/
void printSignal() {
  if (!log(TRACE)) return;

  Serial.println(Signal);
}

/*
   Print function
*/
void printThreshold(int threshold, int meanMin, int meanMax) {
  if (!log(INFO)) return;

  Serial.print("New threshold: ");
  Serial.print(threshold);
  Serial.print(" - (");
  Serial.print(meanMin);
  Serial.print("/");
  Serial.print(meanMax);
  Serial.println(")");
}

/*
   Print function
*/
void printSensorError(int meanMin, int meanMax) {
  if (!log(INFO)) return;

  Serial.print("Sensor error - (");
  Serial.print(meanMin);
  Serial.print("/");
  Serial.print(meanMax);
  Serial.println(")");
}

/*
   Print function
*/
void printHeartBeat() {
  if (!log(DEBUG)) return;

  static int n = 0;

  Serial.print("New heartbeat - ");
  Serial.println(n);
  n++;
}

/*
   Print function
*/
void printIBIs(short lastIBI, short secondToLastIBI, short thirdToLastIBI) {
  if (!log(OFF)) return;

  Serial.println("❤️ !!Heartbeat detected!! ❤️");
  Serial.print("1: ");
  Serial.print(lastIBI);
  Serial.print(" - 2: ");
  Serial.print(secondToLastIBI);
  Serial.print(" - 3: ");
  Serial.println(thirdToLastIBI);
}

/*
   Print function
*/
void printDiscardedIBI(short IBI) {
  if (!log(DEBUG)) return;

  Serial.print("DISCARD IBI: ");
  Serial.println(IBI);
}

/*
   Print function
*/
void printMeanOfLast3(short meanOfLast3) {
  if (!log(OFF)) return;

  Serial.print("Mean of last three heartbeats: ");
  Serial.println(meanOfLast3);
}

/*
   Print function
*/
void printMeanOfLast10(short meanOfLast10) {
  if (!log(OFF)) return;

  Serial.print("Mean of last ten heartbeats: ");
  Serial.println(meanOfLast10);
}

/*
   Print function
*/
void printIBIs() {
  if (!log(DEBUG)) return;

  Serial.print(" -> [");
  for (int j = 0; j < 10 - 1; j++) {
    Serial.print(IBI10[j]);
    Serial.print(", ");
  }
  Serial.print(IBI10[10 - 1]);
  Serial.print("]");
  Serial.println();

  Serial.print(" -> [");
  for (int j = 0; j < 3 - 1; j++) {
    Serial.print(IBI3[j]);
    Serial.print(", ");
  }
  Serial.print(IBI3[3 - 1]);
  Serial.print("]");
  Serial.println();
}

/*
   Print function
*/
void printFibrillation(int fibrillation) {
  if (!log(OFF)) return;

  if (fibrillation) {
    Serial.println("FIBRILLATION");
  } else {
    Serial.println("NO FIBRILLATION");
  }
}

bool log(int l) {
  return l >= LEVEL;
}
