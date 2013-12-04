package LOGISTIC.Implementation;

import java.io.FileInputStream;
import java.util.Properties;

import SVM.Implementation.Hw6SVMDataPreprocessor;
import edu.cmu.lti.searchengine.hw5.MiscHelper;
import edu.cmu.lti.searchengine.hw5.logreg.LogRegModel;

public class Hw6LogReg {
	private static String configFile = "LOGISTIC/DATA.TXT";

	private static final String LOGREG_CONFIG_FILE = "LOGISTIC/logreg-config.txt";

	public static final int NUM_FEATURES = 44;

	private static final String MODEL_FOLDER = "zzz_logreg_model";
	private static final String RESULT_FOLDER = "zzz_logreg_prediction";
	private static final String PREPROCESS_TRAIN_FOLDER = "zzz_logreg_preprocess_train";
	private static final String PREPROCESS_TEST_FOLDER = "zzz_logreg_preprocess_test";

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			configFile = args[0];
		}
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));

		Properties prop2 = new Properties();
		prop2.load(new FileInputStream(LOGREG_CONFIG_FILE));
		prop.putAll(prop2);

		float c = Float.parseFloat(prop.getProperty("c"));
		String rawTrainFile = prop.getProperty("train");
		String rawTestFile = prop.getProperty("test");

		// preprocess the raw train file, new train file goes to trainFile
		MiscHelper.touchDir(PREPROCESS_TRAIN_FOLDER);
		String trainFile = PREPROCESS_TRAIN_FOLDER + "/newtrain.txt";
		Hw6SVMDataPreprocessor preprocessor = new Hw6SVMDataPreprocessor();
		preprocessor.preprocess(rawTrainFile, trainFile, true, 0);

		// preprocess test file, remove "qid:xxx"
		MiscHelper.touchDir(PREPROCESS_TEST_FOLDER);
		String testFile = PREPROCESS_TEST_FOLDER + "/newteset.txt";
		preprocessor.cleanQid(rawTestFile, testFile);

		LogRegModel lrm = new LogRegModel(prop, NUM_FEATURES);
		lrm.trainAndTest(c, trainFile, MODEL_FOLDER, testFile, RESULT_FOLDER);

	}
}
