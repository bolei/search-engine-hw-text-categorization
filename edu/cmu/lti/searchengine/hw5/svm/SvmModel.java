package edu.cmu.lti.searchengine.hw5.svm;

import edu.cmu.lti.searchengine.hw5.AbstractModel;

public class SvmModel extends AbstractModel {

	public SvmModel(String svmLocation, int numFeatures) {
		classifier = new SvmAdapter(svmLocation, numFeatures);
	}

}
