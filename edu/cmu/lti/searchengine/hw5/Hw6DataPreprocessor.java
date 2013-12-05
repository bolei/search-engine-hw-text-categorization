package edu.cmu.lti.searchengine.hw5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import SVM.Implementation.Hw6SVM;


public class Hw6DataPreprocessor {

	/**
	 * removes query id from each line
	 * */
	public void preprocessTest(String rawTestFileName, String outTestFileName)
			throws IOException {
		FileReader fr = new FileReader(rawTestFileName);
		BufferedReader br = null;

		File outFile = new File(outTestFileName);
		FileWriter fw = new FileWriter(outFile);
		PrintWriter bw = null;

		try {
			br = new BufferedReader(fr);
			bw = new PrintWriter(fw);
			String line;
			double[] features;
			double value;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty() == true) {
					continue;
				}
				String[] strArr = line.split("[\\s|#|=]+");
				bw.print(strArr[0]);
				bw.print(" ");
				features = new double[Hw6SVM.NUM_FEATURES];
				for (int i = 2; i < strArr.length - 2; i++) {
					String[] keyValus = strArr[i].split(":");
					value = Double.parseDouble(keyValus[1]);
					features[i - 2] = value;
				}
				features = normalizeVector(features);
				for (int i = 0; i < Hw6SVM.NUM_FEATURES; i++) {
					bw.print(i + 1);
					bw.print(":");
					bw.print(features[i]);
					bw.print(' ');
				}
				bw.println();
			}
		} finally {
			MiscHelper.closeReader(br);
			MiscHelper.closeWriter(bw);
		}

	}

	/**
	 * 
	 * Calculate features for RankSVM
	 * 
	 * */
	public void preprocessTrain(String rawTrainingFileName,
			String outTrainingFileName, boolean includeNeg, int negLabelVal)
			throws IOException {
		FileReader fr = new FileReader(rawTrainingFileName);
		BufferedReader br = null;

		File outFile = new File(outTrainingFileName);
		FileWriter fw = new FileWriter(outFile);
		PrintWriter bw = null;

		LinkedHashMap<Integer, QueryTrain> trainingData = new LinkedHashMap<Integer, QueryTrain>();
		LinkedList<FeatureAndLable> matrixV = new LinkedList<FeatureAndLable>();
		try {
			br = new BufferedReader(fr);
			bw = new PrintWriter(fw);
			String line;

			// load data into memory
			QueryTrain qt;
			double[] features;
			double value;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				String[] strArr = line.split("[\\s|#|=]+");
				int queryId;
				try {
					queryId = Integer.parseInt((strArr[1].split(":"))[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("line=" + line);
					throw e;
				}
				if (trainingData.containsKey(queryId) == false) {
					trainingData.put(queryId, new QueryTrain());
				}
				int docId = Integer.parseInt(strArr[strArr.length - 1]);
				qt = trainingData.get(queryId);
				features = new double[Hw6SVM.NUM_FEATURES];
				for (int i = 2; i < strArr.length - 2; i++) {
					String[] keyValus = strArr[i].split(":");
					value = Double.parseDouble(keyValus[1]);
					features[i - 2] = value;
				}

				// System.out.println(String.format("==>features:\n%s\n",
				// Arrays.toString(features)));
				if (Integer.parseInt(strArr[0]) > 0) {
					// positive point
					qt.positiveTrain.put(docId, features);
				} else {
					// negative point
					qt.negativeTrain.put(docId, features);
				}
			}

			// generate matrix V
			for (Entry<Integer, QueryTrain> entry : trainingData.entrySet()) {
				matrixV.addAll(entry.getValue().calculatePairwiseVectors(
						includeNeg));
			}

			// output to file
			for (FeatureAndLable fl : matrixV) {
				if (fl.label == true) {
					bw.print(1);
				} else {
					bw.print(negLabelVal);
				}
				bw.print(' ');
				for (int i = 0; i < Hw6SVM.NUM_FEATURES; i++) {
					bw.print(i + 1);
					bw.print(':');
					bw.print(fl.features[i]);
					bw.print(' ');
				}
				bw.println();
			}

		} finally {
			MiscHelper.closeReader(br);
			MiscHelper.closeWriter(bw);
		}
	}

	private class FeatureAndLable {
		boolean label;
		double[] features;

		FeatureAndLable(boolean lbl, double[] feat) {
			this.label = lbl;
			this.features = feat;
		}
	}

	private class QueryTrain {

		// <docId, <featureId, value>>
		private HashMap<Integer, double[]> positiveTrain = new HashMap<Integer, double[]>();
		private HashMap<Integer, double[]> negativeTrain = new HashMap<Integer, double[]>();

		LinkedList<FeatureAndLable> calculatePairwiseVectors(boolean includeNeg) {
			LinkedList<FeatureAndLable> vectorVs = new LinkedList<FeatureAndLable>();
			double[] v, posVec, negVec;
			for (Entry<Integer, double[]> posEntry : positiveTrain.entrySet()) {
				posVec = normalizeVector(posEntry.getValue());

				for (Entry<Integer, double[]> negEntry : negativeTrain
						.entrySet()) {
					negVec = normalizeVector(negEntry.getValue());
					v = new double[Hw6SVM.NUM_FEATURES];
					for (int i = 0; i < Hw6SVM.NUM_FEATURES; i++) {
						v[i] = posVec[i] - negVec[i];
					}
					vectorVs.add(new FeatureAndLable(true, v));
	
					// should include negative data
					if (includeNeg == true) {
						v = new double[Hw6SVM.NUM_FEATURES];
						for (int i = 0; i < Hw6SVM.NUM_FEATURES; i++) {
							v[i] = negVec[i] - posVec[i];
						}
						vectorVs.add(new FeatureAndLable(false, v));
					}
				}
			}
			return vectorVs;
		}

	}

	private double[] normalizeVector(double[] vector) {
		double[] newVec = new double[vector.length];
		if (vector.length <= 0) {
			return Arrays.copyOf(vector, vector.length);
		}
		double mod = 0;
		for (double f : vector) {
			mod += f * f;
		}
		mod = Math.sqrt(mod);

		if (mod != 0) {
			for (int i = 0; i < vector.length; i++) {
				newVec[i] = vector[i] / mod;
			}
		}
		return newVec;
	}

	public static void main(String[] args) throws IOException {
		String inputFile = "/home/bolei/Desktop/data/tmp_train.txt";
		String outputFile = "/home/bolei/Desktop/data/tmp.txt";
		new Hw6DataPreprocessor().preprocessTrain(inputFile, outputFile, false,
				-1);
	}
}
