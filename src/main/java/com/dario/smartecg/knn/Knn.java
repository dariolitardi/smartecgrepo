package com.dario.smartecg.knn;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Knn {

	private TrainRecord[] trainingSet;

	public Knn(InputStream trainingFile) {
		this.trainingSet = FileManager.readTrainFile(trainingFile);
	}

	public int prediction(double[] heartbeat) {

		if (heartbeat.length != 5) {
			System.out.println("Error heartbeat!");
			return 0;
		}
		Metric metric;
		metric = new EuclideanDistance();

		TestRecord testingSet;
		testingSet = new TestRecord(heartbeat, 0);
		TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet, 3, metric);
		int classLabel = classify(neighbors);
		return classLabel;

	}

	// Find K nearest neighbors of testRecord within trainingSet
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord, int K, Metric metric) {
		int NumOfTrainingSet = trainingSet.length;
		assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";

		//Update KNN: take the case when testRecord has multiple neighbors with the same distance into consideration
		//Solution: Update the size of container holding the neighbors
		TrainRecord[] neighbors = new TrainRecord[K];

		//initialization, put the first K trainRecords into the above arrayList
		int index;
		for (index = 0; index < K; index++) {
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}

		//go through the remaining records in the trainingSet to find K nearest neighbors
		for (index = K; index < NumOfTrainingSet; index++) {
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);

			//get the index of the neighbor with the largest distance to testRecord
			int maxIndex = 0;
			for (int i = 1; i < K; i++) {
				if (neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}

			//add the current trainingSet[index] into neighbors if applicable
			if (neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}

		return neighbors;
	}

	// Get the class label by using neighbors
	static int classify(TrainRecord[] neighbors) {
		//construct a HashMap to store <classLabel, weight>
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int num = neighbors.length;

		for (int index = 0; index < num; index++) {
			TrainRecord temp = neighbors[index];
			int key = temp.classLabel;

			//if this classLabel does not exist in the HashMap, put <key, 1/(temp.distance)> into the HashMap
			if (!map.containsKey(key))
				map.put(key, 1 / temp.distance);

				//else, update the HashMap by adding the weight associating with that key
			else {
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}

		//Find the most likely label
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();

		//go through the HashMap by using keys
		//and find the key with the highest weights
		while (it.hasNext()) {
			int label = it.next();
			double value = map.get(label);
			if (value > maxSimilarity) {
				maxSimilarity = value;
				returnLabel = label;
			}
		}

		return returnLabel;
	}

	static String extractGroupName(String filePath) {
		StringBuilder groupName = new StringBuilder();
		for (int i = 15; i < filePath.length(); i++) {
			if (filePath.charAt(i) != '_')
				groupName.append(filePath.charAt(i));
			else
				break;
		}

		return groupName.toString();
	}
}
