package edu.cmu.lti.searchengine.hw5;

import java.io.PrintStream;

public abstract class AbstractModel {
	protected AbstractOneVsRestClassifierAdapter classifier;

	protected String trainFileNamePrefix = "train-target-";
	protected String modelFileNamePrefix = "model-target-";
	protected String modelFolderNamePrefix = "model-";
	protected String resultFolderNamePrefix = "prediction-";
	protected String resultFilePrefix = "svm-prediction-class-";

	/**
	 * 
	 * Train and test for one-VS-rest classification
	 * 
	 * */
	public void trainAndTest(int numClasses, float c, PrintStream finalOut,
			String trainFilefolder, String modelParentFolder, String testFile,
			String resultParentFolder) throws Exception {
		String modelFolder = modelParentFolder + "/" + modelFolderNamePrefix
				+ c;
		MiscHelper.touchDir(modelFolder);
		// train the model
		long begin = System.currentTimeMillis();
		classifier.trainOneVsRest(trainFileNamePrefix, trainFilefolder,
				numClasses, modelFileNamePrefix, modelFolder, c);
		long end = System.currentTimeMillis();
		System.err.println(String.format("c=%f, train time:%d", c,
				(end - begin)));

		// make prediction
		String resultFolder = resultParentFolder + "/" + resultFolderNamePrefix
				+ c;
		MiscHelper.touchDir(resultFolder);
		classifier.testOneVsRest(testFile, modelFolder, modelFileNamePrefix,
				numClasses, resultFolder, resultFilePrefix, finalOut);

	}

	/**
	 * 
	 * Train and test for binary classification
	 * 
	 * @throws Exception
	 * 
	 */
	public void trainAndTest(float c, String trainFile, String modelFolder,
			String testFile, String resultFile) throws Exception {

		MiscHelper.touchDir(modelFolder);

		String modelFile = modelFolder + "/model-" + c + ".txt";

		long begin = System.currentTimeMillis();
		// train the model
		classifier.train(trainFile, modelFile, c);
		long end = System.currentTimeMillis();
		System.err.println(String.format("time spent:%d", (end - begin)));

		// make prediction
		classifier.classify(testFile, modelFile, resultFile);
	}
}
