# SmartECG  

A system to detect fibrillations using a **heartbeat** sensor, like a heart rate monitor. The user is notified if he is affected by **atrial fibrillation**.
Atrial fibrillation is an abnormal heart rhythm characterized by **rapid and irregular beating** of the atria. Often it starts as brief periods of abnormal beating which become longer and possibly constant over time.

## Components
### Hardware
* STM32 Nucleo F401RE board
* Pulse sensor
* Bluetooth module X-NUCLEO-IDB05A1

### Software
* Arduino IDE
* Android Studio

### Architecture
<img src="pictures/architecture.png" width="500" />  

### Connections
**STM32 Nucleo board connected to the pulse sensor and the bluetooth module**
<img src="pictures/connections.jpg" width="300" />  

## How does SmartECG work
The STM32 Nucleo board detects heartbeats through the pulse sensor. They are pre-processed and then elaborated from a machine learning algorithm. 
The result is sent via Bluetooth to the android application that shows the BPM (beats-per-minute) in the main screen and notify the user when it is affected by atrial fibrillation. The Machine Learning algorithm used is KNN.

### How does KNN work?
In the classification setting, the K-nearest neighbor algorithm essentially boils down to forming a majority vote between the K most similar instances to a given “unseen” observation. Similarity is defined according to a distance metric between two data points. A popular choice is the Euclidean distance given by
                                                  
but other measures can be more suitable for a given setting and include the Manhattan, Chebyshev and Hamming distance.

More formally, given a positive integer K, an unseen observation x and a similarity metric d, KNN classifier performs the following two steps:

It runs through the whole dataset computing d between x and each training observation. We’ll call the K points in the training data that are closest to x the set A. Note that K is usually odd to prevent tie situations.

It then estimates the conditional probability for each class, that is, the fraction of points in A with that given class label. (Note I(x) is the indicator function which evaluates to 1 when the argument x is true and 0 otherwise)

P(y=j|X=x)=1K∑i∈AI(y(i)=j)
Finally, our input x gets assigned to the class with the largest probability.

In this project, the KNN algorithm is implemented on the STM32 Nucleo board.

The KNN memorizes the training dataset, indeed it doesn't learn a discriminative function from the training data as the traditional machine learning algorithm. This sometimes makes KNN much more efficient in circumstances where traditional algorithms fail.
All the dataset is stored inside the STM32-board in a data structure. The board then always do comparisons between the new data received from the sensor, and its internal dataset. The KNN implemented on the board uses the Euclidean distance as distance metric to determine the correct result. 

The KNN algorithm can be summarized by the following steps: 
1. Choose the number of k and a distance metric. 
1. Find the k nearest neighbors of the sample that we want to classify.
1. Assign the class label by majority vote. 

<img src="pictures/knn.png" width="300" />

The picture illustrates how a new data point “?” is assigned to the triangle class label based on majority voting among its k nearest neighbors. In the example K = 5.

The dataset we used has been preprocessed according to the procedures described in the following article:

*Mar, T., Zaunseder, S., Martinnez, J. P., Llamedo, M., & Poll, R. (August 01, 2011). Optimization of ECG Classification by Means of Feature Selection. Ieee Transactions on Biomedical Engineering, 58, 8, 2168-2177.*

As stated in the article we pre-processed the data calculating the following values:

1. The average of the last three beats
1. The last beat
1. The second to last beat
1. The third to last beat
1. The average of the last ten beats

We need these values because fibrillation is detected only when the intervals between two beats corresponding to the electrical activation of the ventricles are completely irregular without following a repetitive pattern.

The choice to use the KNN is due to the excellent results obtained by its executions during the testing phase. We compared different machine learning algorithms writing a script in Python (available at this [link](py/script.py)) It uses the [scikit-learn library](http://scikit-learn.org/stable/index.html). 

Below is a list of all the machine learning algorithms tested with the results obtained:

| Algorithm | Accuracy |
|:----------|:------|
| KNN       | 0.957 |
| SVM       | 0.908 |
| Random Forest | 0.935 |
| Random Forest with PCA preprocessing | 0.941 |
| Logistic Regression | 0.907 |
| Neural Network | 0.928 |

<img src="pictures/knn_results.png" width="500" />

As we can see from the graph, the KNN achieves the best results.  
The accuracy of prediction is always between 94% and 97%.  

<img src="pictures/knn_correctness.png" width="500" />

The image shows an execution of the training set, and where the algorithm predicts correctly the fibrillation.

## Code
* [STM Nucleo-F401RE Board](./Nucleo)  
* [Android application](./Android)  


## Android application

**User profile creation**  
<img src="pictures/profile_activity.png" height="300" /> 

**Connection to a bluetooth device**  
<img src="pictures/scan_activity.png" height="300" />

**The home screen shows the BPM (beats-per-minute) and sends a notification when fibrillation is detected**  
<img src="pictures/home_activity.png" height="300" />  


## How to compile the code
In order to compile the code for the Nucleo Board you have to add STM32 boards support to Arduino IDE.  
You can find the tutorial at this link: https://github.com/stm32duino/wiki/wiki/Getting-Started

## Links

### Presentation on Slideshare  
* https://www.slideshare.net/DarioLitardi/presentazione-finale-102892504

### LinkedIn contacts
* [Andrea Lisanti](https://www.linkedin.com/in/andrea-lisanti)  
* [Dario Litardi](https://www.linkedin.com/in/dario-litardi-84851915b/)  
* [David Buscema](https://www.linkedin.com/in/david-buscema)

### Other useful links
* [STM32 Nucleo-F401RE](http://www.st.com/en/evaluation-tools/nucleo-f401re.html)  
* [X-Nucleo-IDB04A1](http://www.st.com/en/ecosystems/x-nucleo-idb04a1.html)  
* [Pulse sensor](https://pulsesensor.com/)

