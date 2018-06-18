// calculate the class of a set of attributes using KNN
int performKNN(double *data, int classesNum[], double dato[], int k, int rows, int columns);


// calculate the most common type: Fashion
int mostFrequentClass(int classes[], int k);


// extract the first N
void extractFirstN(double data[], double firstK[], int classes[], int kClasses[], int k);


// calculate Euclidean distance between two points
double euclideanDistance(double pt1[], double pt2[], int columns);


// calculate Euclidean distance between a point and the database
void euclideanDistanceFromDatabase(double pt1[], double aux[], double *instances, int rows, int columns);


// sort distances Ascending
void sort(double data[], int classes[], int classesNo[], int rows);
