package edu.cmu.lti.searchengine.hw5.svm;

import edu.cmu.lti.searchengine.hw5.AbstractModel;

public class SvmModel extends AbstractModel {

	public SvmModel(String svmLocation, String[] options) {
		classifier = new SvmAdapter(svmLocation, options);
	}

}
