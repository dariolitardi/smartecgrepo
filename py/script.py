#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed May 16 17:33:00 2018

@author: david
"""

import pandas as pd
import numpy as np

file = pd.read_csv("dataset.txt",header=None,index_col=None,sep='\t')

mat = file.iloc[: ,:].values #matrice con x e y    

n = len(mat)  

n = len(mat) 

X = np.zeros((n,len(mat[0])-2))
y = np.zeros(n)
for i in range(n):
    for j in range(len(mat[i])-2):
        X[i][j] = mat[i][j]
    
    if (mat [i][len(mat[i])-1]==1):
        y[i] = 1
    else:
        y[i] = 0

# Normalize the input data in [0, 1]
import sklearn.preprocessing
scaler = sklearn.preprocessing.MinMaxScaler()
X = scaler.fit_transform(X)

# Split the data in train and test sets
import sklearn.model_selection
Xtrn, Xtst, ytrn, ytst = sklearn.model_selection.train_test_split(X, y, test_size=0.25)

from sklearn.neighbors import KNeighborsClassifier
knn = KNeighborsClassifier(n_neighbors=5)
knn.fit(Xtrn, ytrn)
print("knn " + str(knn.score(Xtst, ytst)) )

from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier

svc = SVC(kernel = 'linear')
svc.fit(Xtrn, ytrn)
print("svm " + str(svc.score(Xtst, ytst)) )

import sklearn.linear_model
logreg = sklearn.linear_model.LogisticRegression()
logreg.fit(Xtrn, ytrn)
print("logreg " + str(logreg.score(Xtst, ytst)) )

import sklearn.neural_network
nnet = sklearn.neural_network.MLPClassifier(hidden_layer_sizes=(65), solver='sgd', learning_rate_init=0.1,max_iter = 500)
nnet.fit(Xtrn, ytrn)
nnetVal = print("nnet " + str(nnet.score(Xtst, ytst)))
 
clf = RandomForestClassifier(max_depth=2, random_state=0)
clf.fit(Xtrn, ytrn)
print("RandomF " + str(clf.score(Xtst, ytst)) )

from sklearn.decomposition import PCA

pca = PCA(n_components= 3)
Xtrn_pca = pca.fit_transform(Xtrn)
Xtst_pca = pca.transform(Xtst)
clf = RandomForestClassifier(max_depth=2, random_state=0)
clf.fit(Xtrn_pca, ytrn)
print("Random F pca " + str(clf.score(Xtst_pca, ytst)) )
