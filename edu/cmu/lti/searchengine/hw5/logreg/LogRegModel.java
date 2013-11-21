package edu.cmu.lti.searchengine.hw5.logreg;

import java.util.Properties;

import edu.cmu.lti.searchengine.hw5.AbstractModel;

public class LogRegModel extends AbstractModel {
	public LogRegModel(Properties prop, int numFeatures) {
		double epsilon = Double.parseDouble(prop.getProperty("epsilon"));
		double precision = Double.parseDouble(prop.getProperty("precision"));
		classifier = new LogRegAdapter(numFeatures, epsilon, precision);
	}
}
