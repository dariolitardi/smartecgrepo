//basic metric interface
package com.dario.smartecg.knn;

public interface Metric {
	double getDistance(Record s, Record e);
}
