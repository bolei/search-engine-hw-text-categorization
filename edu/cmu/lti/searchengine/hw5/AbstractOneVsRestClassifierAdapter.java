package edu.cmu.lti.searchengine.hw5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class AbstractOneVsRestClassifierAdapter {

	protected int numFeatures;

	public AbstractOneVsRestClassifierAdapter(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public AbstractOneVsRestClassifierAdapter() {
	}

	public void trainOneVsRest(String trainFileNamePrefix,
			String trainFilefolder, int numClasses, String modelFileNamePrefix,
			String modelOutFolder, float c) throws Exception {
		for (int i = 1; i <= numClasses; i++) {
			String trainingFile = trainFilefolder + "/" + trainFileNamePrefix
					+ i;
			String modelFile = modelOutFolder + "/" + modelFileNamePrefix + i;
			train(trainingFile, modelFile, c);
		}
	}

	public void testOneVsRest(String testFile, String modelFolder,
			String modelFilePrefix, int numClasses, String resultFolder,
			String resultFilePrefix, PrintStream finalResultOut)
			throws Exception {

		// make prediction for each class
		for (int i = 1; i <= numClasses; i++) {
			String modelFile = modelFolder + "/" + modelFilePrefix + i;
			String resultFile = resultFolder + "/" + resultFilePrefix + i;
			classify(testFile, modelFile, resultFile);
		}

		// combine results, make final prediction
		Float[][] predictions = new Float[numClasses][];
		LinkedList<Float> tmp = null;
		FileReader fin;
		BufferedReader brin = null;
		String line;
		int exampleCount = -1;

		// gather prediction for all classifiers
		for (int i = 1; i <= numClasses; i++) {
			String resultFile = resultFolder + "/" + resultFilePrefix + i;
			if (tmp == null) {
				tmp = new LinkedList<Float>();
			} else {
				tmp.clear();
			}
			try {
				fin = new FileReader(resultFile);
				brin = new BufferedReader(fin);
				while ((line = brin.readLine()) != null) {
					tmp.add(Float.parseFloat(line));
				}
				predictions[i - 1] = tmp.toArray(new Float[] {});
				if (exampleCount == -1) { // not initialized
					exampleCount = predictions[i - 1].length;
				} else if (exampleCount != predictions[i - 1].length) {
					throw new RuntimeException(String.format(
							"instance count error: before=%d, current=%d",
							exampleCount, predictions[i - 1].length));
				}
			} finally {
				MiscHelper.closeReader(brin);
			}
		}

		// find the most confident classification, choose that as prediction
		int[] finalPrediction = new int[exampleCount];
		Arrays.fill(finalPrediction, 1);
		Float[] bestScores = predictions[0];
		for (int i = 2; i <= numClasses; i++) {
			for (int j = 0; j < finalPrediction.length; j++) {
				if (predictions[i - 1][j] > bestScores[j]) {
					finalPrediction[j] = i;
					bestScores[j] = predictions[i - 1][j];
				}
			}
		}

		// load the true labels
		int[] trueLables = new int[exampleCount];
		try {
			fin = new FileReader(testFile);
			brin = new BufferedReader(fin);
			int i = 0;
			while ((line = brin.readLine()) != null) {
				trueLables[i] = Integer.parseInt(line.substring(0,
						line.indexOf(' ')));
				i++;
			}
		} finally {
			MiscHelper.closeReader(brin);
		}

		// dump out the result
		for (int i = 0; i < exampleCount; i++) {
			finalResultOut.print(finalPrediction[i]);
			finalResultOut.print(' ');
			finalResultOut.println(trueLables[i]);
		}
	}

	public abstract void train(String trainingFile, String modelFile, float c)
			throws Exception;

	public abstract void classify(String testFile, String modelFile,
			String resultFile) throws Exception;
}
