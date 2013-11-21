package edu.cmu.lti.searchengine.hw5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class TrainingDataPreprocessor {

	// find out the number of classes
	public static int getNumClasses(String trainingFileName) throws IOException {
		FileReader fr = new FileReader(trainingFileName);
		BufferedReader br = null;
		int numClasses = 1;
		try {
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				int spacePos = line.indexOf(' ');
				int cat = Integer.parseInt(line.substring(0, spacePos));
				if (cat > numClasses) {
					numClasses = cat;
				}
			}

		} finally {
			MiscHelper.closeReader(br);
		}
		return numClasses;
	}

	public static int getNumberFeatures(String trainingFileName)
			throws IOException {
		FileReader fr = new FileReader(trainingFileName);
		BufferedReader br = null;
		int numFeatures = 1;
		try {
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] strArr = line.split(" ");
				for (int i = 1; i < strArr.length; i++) {
					Integer fnum = Integer.parseInt(strArr[i].split(":")[0]);
					if (numFeatures < fnum) {
						numFeatures = fnum;
					}
				}
			}

		} finally {
			MiscHelper.closeReader(br);
		}
		return numFeatures;
	}

	// for each class, create a training file,
	// write into outFolder
	public void preprocess(String trainingFileName, String outFolderName,
			int numClasses, int negVal) throws IOException {

		for (int i = 1; i <= numClasses; i++) {

			FileReader fr = new FileReader(trainingFileName);
			BufferedReader br = null;

			File outFile = new File(outFolderName, "train-target-" + i);
			FileWriter fw = new FileWriter(outFile);
			PrintWriter bw = null;

			try {
				br = new BufferedReader(fr);
				bw = new PrintWriter(fw);
				String line;
				while ((line = br.readLine()) != null) {
					int spacePos = line.indexOf(' ');
					int cat = Integer.parseInt(line.substring(0, spacePos));
					if (cat == i) {
						cat = 1;
					} else {
						cat = negVal;
					}
					bw.println(cat + line.substring(spacePos));
				}

			} finally {
				MiscHelper.closeReader(br);
				MiscHelper.closeWriter(bw);
			}
		}
	}

	public static void main(String[] args) throws IOException {

		Properties config = new Properties();
		String configFile;
		if (args.length >= 1) {
			configFile = args[0];
			config.load(new FileReader(configFile));
		} else {
			configFile = "/part2-preprocess.config";
			config.load(ClassLoader.class.getResourceAsStream(configFile));
		}
		String trainingFileName = config.getProperty("training");
		String outFolderName = config.getProperty("outFolder");

		TrainingDataPreprocessor tp = new TrainingDataPreprocessor();
		int numClasses = getNumClasses(trainingFileName);
		tp.preprocess(trainingFileName, outFolderName, numClasses, -1);
	}
}
