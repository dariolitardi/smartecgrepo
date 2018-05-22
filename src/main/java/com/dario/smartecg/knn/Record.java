//Basic Record class
package com.dario.smartecg.knn;

public class Record {
	double[] attributes;
	int classLabel;

	Record(double[] attributes, int classLabel){
		this.attributes = attributes;
		this.classLabel = classLabel;
	}
}
