package edu.cmu.lti.searchengine.hw5.logreg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import LOGISTIC.Implementation.Hw6LogReg;
import edu.cmu.lti.searchengine.hw5.AbstractOneVsRestClassifierAdapter;
import edu.cmu.lti.searchengine.hw5.MiscHelper;

public class LogRegAdapter extends AbstractOneVsRestClassifierAdapter {

	private double epsilon;
	private double precision;

	public LogRegAdapter(int numFeatures, double epsilon, double precision) {
		super(numFeatures);
		this.epsilon = epsilon;
		this.precision = precision;
	}

	@Override
	public void train(String trainingFile, String modelFile, float c)
			throws Exception {
		ArrayList<Integer> allLabels = new ArrayList<Integer>();
		ArrayList<HashMap<Integer, Float>> allTraining = new ArrayList<HashMap<Integer, Float>>();

		FileReader fr = new FileReader(trainingFile);
		BufferedReader br = null;

		// load training data into memory
		try {
			br = new BufferedReader(fr);
			String line;
			HashMap<Integer, Float> featureVec;
			int label;
			while ((line = br.readLine()) != null) {
				label = Integer.parseInt(line.substring(0, line.indexOf(' ')));
				featureVec = getFeatureVector(line);
				allLabels.add(label);
				allTraining.add(featureVec);
			}
		} finally {
			MiscHelper.closeReader(br);
		}

		// weight vector, w_0 is the weightVector[0]
		// init it with some random value
		Random rand = new Random();
		float[] weightVector = new float[numFeatures + 1];
		for (int i = 0; i < weightVector.length; i++) {
			weightVector[i] = (float) (rand.nextFloat() * Math.pow(-1,
					rand.nextInt() % 2));
		}

		double[] gradientVector = new double[numFeatures + 1];

		int numInstances = allLabels.size();
		double oldObj = Integer.MIN_VALUE;
		double newObj = calculateObjectFunction(weightVector, allTraining,
				allLabels, c);

		double sigmoid;
		while (newObj - oldObj > precision) { // convergence

			double objective = 0d;
			for (int i = 0; i < numInstances; i++) { // for each instance
				// calculate gradient
				sigmoid = getSigmoid(weightVector, allTraining.get(i));
				if (sigmoid == 0 || sigmoid == 1) {
					continue;
				}
				objective += allLabels.get(i) * Math.log(sigmoid)
						+ (1 - allLabels.get(i)) * Math.log(1 - sigmoid);
				for (int j = 0; j < gradientVector.length; j++) {
					if (j == 0) {
						gradientVector[0] = (allLabels.get(i) - sigmoid)
								* getFeatureValue(allTraining.get(i), 0);
					} else {
						gradientVector[j] = (allLabels.get(i) - sigmoid)
								* getFeatureValue(allTraining.get(i), j) - c
								* weightVector[j];
					}
					// update weightVector
					weightVector[j] += epsilon * gradientVector[j];
				}
			}
			// calculate ||w||, ignore w_0
			double wMode = 0;
			for (int i = 1; i < weightVector.length; i++) {
				wMode += Math.pow(weightVector[i], 2);
			}
			oldObj = newObj;
			newObj = objective - 0.5 * c * wMode;
		}

		// dump model into file
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(modelFile);
			pw.println(Arrays.toString(weightVector));
		} finally {
			MiscHelper.closeWriter(pw);
		}

	}

	private HashMap<Integer, Float> getFeatureVector(String line) {
		// remove #info
		int tmp = line.indexOf('#');
		if (tmp >= 0) {
			line = line.substring(0, tmp);
		}

		String[] strArr = line.split(" ");
		HashMap<Integer, Float> instance = new LinkedHashMap<Integer, Float>();
		for (int i = 1; i < strArr.length; i++) {
			instance.put(Integer.parseInt(strArr[i].split(":")[0]),
					Float.parseFloat(strArr[i].split(":")[1]));
		}
		return instance;
	}

	private float getFeatureValue(HashMap<Integer, Float> featureVec,
			int feature) {
		return featureVec.containsKey(feature) ? featureVec.get(feature) : 0;
	}

	private double calculateObjectFunction(float[] weightVector,
			ArrayList<HashMap<Integer, Float>> allTraining,
			ArrayList<Integer> allLabels, float c) {

		int numInstances = allLabels.size();
		int label;
		double sigmoid;
		double object = 0d;
		for (int i = 0; i < numInstances; i++) {
			sigmoid = getSigmoid(weightVector, allTraining.get(i));
			if (sigmoid == 0 || sigmoid == 1) {
				continue;
			}
			label = allLabels.get(i);
			object += label * Math.log(sigmoid) + (1 - label)
					* Math.log(1 - sigmoid);
		}

		// calculate ||w||, ignore w_0
		double wMode = 0;
		for (int i = 1; i < weightVector.length; i++) {
			wMode += Math.pow(weightVector[i], 2);
		}
		return object - 0.5 * c * wMode;
	}

	private double getSigmoid(float[] weightVector,
			HashMap<Integer, Float> featureVector) {

		// calculate dot product
		double dotpdt = 0f;
		for (Entry<Integer, Float> entry : featureVector.entrySet()) {
			if (entry.getKey() >= weightVector.length) {
				// skip unseen feature
				continue;
			}
			dotpdt += weightVector[entry.getKey()] * entry.getValue();
		}
		return 1 / (1 + Math.exp(-dotpdt));
	}

	private float[] parseWeightVector(String line) {
		String[] strArr = line.substring(1, line.length() - 1).split(",");
		float[] weightVector = new float[strArr.length];

		for (int i = 0; i < weightVector.length; i++) {
			weightVector[i] = Float.parseFloat(strArr[i].trim());
		}
		return weightVector;
	}

	@Override
	public void classify(String testFile, String modelFile, String resultFile)
			throws Exception {
		FileReader fin = null;
		BufferedReader bin = null;
		float[] weightVector = null;
		try {
			// load weigthVector
			fin = new FileReader(modelFile);
			bin = new BufferedReader(fin);
			weightVector = parseWeightVector(bin.readLine());
		} finally {
			MiscHelper.closeReader(bin);
		}

		PrintWriter out = null;
		try {
			// do prediction
			fin = new FileReader(testFile);
			bin = new BufferedReader(fin);
			out = new PrintWriter(resultFile);
			String line = null;
			HashMap<Integer, Float> featureVec;

			float prediciton;
			// for each instance, make prediction
			while ((line = bin.readLine()) != null) {
				featureVec = getFeatureVector(line);
				prediciton = (float) getSigmoid(weightVector, featureVec);
				out.println(prediciton);
			}
		} finally {
			MiscHelper.closeReader(bin);
			MiscHelper.closeWriter(out);
		}
	}

	public static void main(String[] args) throws Exception {
		String trainFile = "zzz_logreg_preprocess_train/newtrain.txt";
		String modelFile = "/home/bolei/Desktop/logreg-tmp-model.txt";
		float c = 100;
		int numFeatures = Hw6LogReg.NUM_FEATURES;
		LogRegAdapter lr = new LogRegAdapter(numFeatures, 1E-1, 1E-4);

		lr.train(trainFile, modelFile, c);
	}
}
