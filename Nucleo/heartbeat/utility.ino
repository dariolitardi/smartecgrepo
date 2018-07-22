/*
  Function to store the maximum values and the minimum values
*/
void setMinMax() {
  for (int j = 0; j < MIN_MAX_SIZE; j++) {
    if (Signal > maxValues[j]) {
      maxValues[j] = Signal;
      break;
    }
  }

  for (int j = 0; j < MIN_MAX_SIZE; j++) {
    if (Signal < minValues[j]) {
      minValues[j] = Signal;
      break;
    }
  }
}

/*
  Function to reset the array that store the maximum and minimum values
*/
void resetMinMax() {
  for (int j = 0; j < MIN_MAX_SIZE; j++) {
    minValues[j] = MAX;
    maxValues[j] = MIN;
  }
}

/*
  Function to evaluate the mean of the IBI10 array
*/
int meanMinMax(int a[], int discard) {
  int sum = 0;
  int len = 0;
  for (int j = 0; j < MIN_MAX_SIZE; j++) {
    if (a[j] != discard) {
      sum += a[j];
      len++;
    }
  }
  return sum / len;
}

/*
  Function to evaluate the mean of the IBI10 array
*/
int meanLast10Beats() {
  int sum = 0;
  for (int j = 0; j < 10; j++) sum += IBI10[j];
  return sum / 10;
}

/*
  Function to evaluate the mean of the IBI3 array
*/
int meanLast3Beats() {
  int sum = 0;
  for (int j = 0; j < 3; j++) sum += IBI3[j];
  return sum / 3;
}
