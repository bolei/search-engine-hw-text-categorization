package edu.cmu.lti.searchengine.hw5;

import java.io.File;
import java.io.PrintStream;

import edu.cmu.lti.searchengine.hw5.svm.SvmModel;

public class Hw5SvmMain {
	public static void main(String[] args) throws Exception {
		int numClasses = 17;
		float[] cArr = new float[] { 0.0001f, 0.001f, 0.01f, 0.1f, 1f, 10f,
				50f, 100f };
		String svmLocation = "/home/bolei/Works/data/11641-hw5/svm_light";
		String trainFilefolder = "/home/bolei/Works/data/11641-hw5/data/one-vs-rest-train";
		String modelParentFolder = "/home/bolei/Works/data/11641-hw5/data/model/svm";
		String testFile = "/home/bolei/Works/data/11641-hw5/data/citeseer.test.ltc.svm";
		String resultParentFolder = "/home/bolei/Works/data/11641-hw5/data/prediction/svm";

		PrintStream finalOut = null;
		for (float c : cArr) {
			SvmModel mt = new SvmModel(svmLocation, new String[] { "-c",
					Float.toString(c) });
			String finalResultFile = "/home/bolei/Works/data/11641-hw5/data"
					+ "/prediction/svm/finalResult-" + c;
			try {
				finalOut = new PrintStream(new File(finalResultFile));
				mt.trainAndTest(numClasses, c, finalOut, trainFilefolder,
						modelParentFolder, testFile, resultParentFolder);
			} finally {
				MiscHelper.closeOutputStream(finalOut);
			}
		}
	}
}
