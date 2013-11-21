package edu.cmu.lti.searchengine.hw5.svm;

import edu.cmu.lti.searchengine.hw5.AbstractOneVsRestClassifierAdapter;

public class SvmAdapter extends AbstractOneVsRestClassifierAdapter {

	// folder that contains svm_light
	private static String SVM_LOCATION;

	public SvmAdapter(String svmLocation, int numFeatures) {
		super(numFeatures);
		SVM_LOCATION = svmLocation;
	}

	@Override
	public void train(String trainingFile, String modelFile, float c)
			throws Exception {
		final Process proc = Runtime.getRuntime().exec(
				new String[] { SVM_LOCATION + "/svm_learn", "-c",
						Float.toString(c), trainingFile, modelFile });

		// wait for the process to complete
		proc.waitFor();
	}

	@Override
	public void classify(String testFile, String modelFile, String resultFile)
			throws Exception {
		final Process proc = Runtime.getRuntime().exec(
				new String[] { SVM_LOCATION + "/svm_classify", testFile,
						modelFile, resultFile });

		// wait for the process to complete
		proc.waitFor();
	}
}
