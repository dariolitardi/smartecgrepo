#include "KNN.h"
#include <math.h>

// calculate the class of a set of attributes using KNN

/*
	data = training set
	classesNum = traing set output
	d = testing value 
	k = number of k
	rows, columns = number of row and columns of training set 
*/
int performKNN(float *data, int classesNum[], float d[], int k, int rows, int columns) {
  float temp2[rows]; //data distance sorted for extracting the first k
  float firstK[k]; //first k near to data
  int classes[rows]; // temporal copy of classesNum used for sort temp2
  int kClasses[k]; //first k near to data (the output classes)

  //calculate the ecludean distance of d from the database
  euclideanDistanceFromDatabase(d, temp2, (float*)data, rows, columns);
  //sort distances Ascending
  sort(temp2, classes, classesNum, rows);
  //calculate the k near
  extractFirstN(temp2, firstK, classes, kClasses, k);

  // calculate Fashion
  int cont = 0, cont2 = 0, pos = 0, num = 0, i = 0;
  int frec[k], best = 0, bestPosition = 0, temp[k];

  // initialize the frequency counter
  for (i = 0; i < k; i++) {
    frec[k] = 0;
  }

  // check repetitions of each number
  for (cont = 0; cont < k; cont++) {
    num = classes[cont];
    pos = cont;

    for (cont2 = 0; cont2 < k; cont2++) {
      if (classes[cont2] == num) {
        temp[pos]++;
      }
    }
  }

  best = temp[0];
  bestPosition = 0;

  for (cont = 0; cont < k; cont++) {
    if (temp[cont] > best) {
      bestPosition = cont;
      best = temp[cont];
    }
  }

  return classes[bestPosition];
}

// calculate the most common type: Fashion
int mostFrequentClass(int classes[], int k) {

  int cont = 0, cont2 = 0, pos = 0, num = 0, i = 0;
  int freq[k], best = 0, bestPosition = 0, temp[k];

  // initialize the frequency counter
  for (i = 0; i < k; i++) {
    freq[k] = 0;
  }

  // check repetitions of each number
  for (cont = 0; cont < k; cont++) {
    num = classes[cont];
    pos = cont;

    for (cont2 = 0; cont2 < k; cont2++) {
      if (classes[cont2] == num) {
        temp[pos]++;
      }
    }
  }

  best = temp[0];
  bestPosition = 0;

  for (cont = 0; cont < k; cont++) {
    if (temp[cont] > best) {
      bestPosition = cont;
      best = temp[cont];
    }
  }

  return classes[bestPosition];
}

// extract the first N
void extractFirstN(float data[], float firstK[], int classes[], int kClasses[], int k) {
  for (int i = 0; i < k; i++) {
    firstK[i] = data[i];
    kClasses[i] = classes[i];
  }
}

// calculate Euclidean distance between two points
float euclideanDistance(float pt1[], float pt2[], int columns) {
  int i;
  float sum = 0;

  for (i = 0; i < columns; i++) {
    sum = pow(pt1[i] - pt2[i], 2) + sum;
  }

  return sqrt(sum);
}

// calculate Euclidean distance between a point and the database
 /*
	pt1 = testing value (d value)
	temp = temporal value with distance
	instances = training dataset
 */
void euclideanDistanceFromDatabase(float pt1[], float temp[], float *instances, int rows, int columns) {
  int i = 0, j = 0;
  float pt2[columns];

  for (i = 0; i < rows; i++) {
    for (j = 0; j < columns; j++) {
      pt2[j] = instances[i * columns + j];
    }
    temp[i] = euclideanDistance(pt1, pt2, columns);
  }
}

// sort distances Ascending
/*
	data = data to sort (temp2)
	classes = copy of the original classes target
	classesNo = training set target
*/
void sort(float data[], int classes[], int classesNo[], int rows) {
  int i = 1, j = 1, f = 1, temp[2];

  // create a copy of the original classes
  for (i = 0; i < rows; i++) {
    classes[i] = classesNo[i];
  }

  // sorting
  for (i = 1; (i <= rows) && f; i++) {
    f = 0;
    for (j = 0; j < (rows - 1); j++) {
      if (data[j + 1] < data[j]) { // descending order >, ascending order <
        temp[0] = data[j];    temp[1] = classes[j];
        data[j] = data[j + 1]; classes[j] = classes[j + 1];
        data[j + 1] = temp[0];  classes[j + 1] = temp[1];
        f = 1;
      }
    }
  }
}
