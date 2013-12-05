package edu.cmu.lti.searchengine.hw5.svm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;

import edu.cmu.lti.searchengine.hw5.AbstractOneVsRestClassifierAdapter;
import edu.cmu.lti.searchengine.hw5.MiscHelper;

public class SvmAdapter extends AbstractOneVsRestClassifierAdapter {

	// folder that contains svm_light
	private static String SVM_LOCATION;

	private String[] options;

	public SvmAdapter(String svmLocation, String[] options) {
		SVM_LOCATION = svmLocation;
		this.options = options;
	}

	@Override
	public void train(String trainingFile, String modelFile, float c)
			throws Exception {
		LinkedList<String> command = new LinkedList<String>();
		command.add(SVM_LOCATION + "/svm_learn");
		Collections.addAll(command, options);
		command.add(trainingFile);
		command.add(modelFile);
		final Process proc = Runtime.getRuntime().exec(
				command.toArray(new String[] {}));
		Thread erroutConsumer = new Thread(new ProcessOutputConsumer(
				proc.getErrorStream()));
		Thread stdoutConsumer = new Thread(new ProcessOutputConsumer(
				proc.getInputStream()));
		erroutConsumer.start();
		stdoutConsumer.start();
		// wait for the process to complete
		proc.waitFor();
	}

	@Override
	public void classify(String testFile, String modelFile, String resultFile)
			throws Exception {
		final Process proc = Runtime.getRuntime().exec(
				new String[] { SVM_LOCATION + "/svm_classify", testFile,
						modelFile, resultFile });
		Thread erroutConsumer = new Thread(new ProcessOutputConsumer(
				proc.getErrorStream()));
		Thread stdoutConsumer = new Thread(new ProcessOutputConsumer(
				proc.getInputStream()));
		erroutConsumer.start();
		stdoutConsumer.start();
		// wait for the process to complete
		proc.waitFor();
	}

	private class ProcessOutputConsumer implements Runnable {
		InputStream is;

		public ProcessOutputConsumer(InputStream is) {
			this.is = is;
		}

		@Override
		public void run() {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = null;
			try {
				br = new BufferedReader(isr);
				while ((br.readLine()) != null)
					; // do not output anything
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				MiscHelper.closeReader(br);
			}

		}
	}
}
