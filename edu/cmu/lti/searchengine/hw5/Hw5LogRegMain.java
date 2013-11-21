package edu.cmu.lti.searchengine.hw5;

import java.io.PrintStream;
import java.util.Properties;

import edu.cmu.lti.searchengine.hw5.logreg.LogRegModel;

public class Hw5LogRegMain {

	private static String trainOutFolder = "./one-vs-rest-train";
	private static String modelparentFolder = "./one-vs-rest-model";
	private static String resultParentFolder = "./one-vs-rest-results";

	public static void doMain(String train, String test, float c,
			Properties prop) throws Exception {

		MiscHelper.touchDir(trainOutFolder);
		MiscHelper.touchDir(modelparentFolder);
		MiscHelper.touchDir(resultParentFolder);

		// preprocess the training file
		TrainingDataPreprocessor tp = new TrainingDataPreprocessor();
		int numClasses = TrainingDataPreprocessor.getNumClasses(train);
		tp.preprocess(train, trainOutFolder, numClasses, 0);
		int numFeatures = TrainingDataPreprocessor.getNumberFeatures(train);

		PrintStream finalOut = null;
		try {
			finalOut = new PrintStream(System.out);
			AbstractModel lr = new LogRegModel(prop, numFeatures);

			lr.trainAndTest(numClasses, c, System.out, trainOutFolder,
					modelparentFolder, test, resultParentFolder);
		} finally {
			MiscHelper.closeOutputStream(finalOut);
		}
	}

}
