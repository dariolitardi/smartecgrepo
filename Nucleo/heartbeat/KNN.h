// calculate the class of a set of attributes using KNN
int performKNN(float *data, int classesNum[], float dato[], int k, int rows, int columns);


// calculate the most common type: Fashion
int mostFrequentClass(int classes[], int k);


// extract the first N
void extractFirstN(float data[], float firstK[], int classes[], int kClasses[], int k);


// calculate Euclidean distance between two points
float euclideanDistance(float pt1[], float pt2[], int columns);


// calculate Euclidean distance between a point and the database
void euclideanDistanceFromDatabase(float pt1[], float aux[], float *instances, int rows, int columns);


// sort distances Ascending
void sort(float data[], int classes[], int classesNo[], int rows);
