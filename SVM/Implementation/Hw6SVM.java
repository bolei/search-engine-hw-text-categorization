package SVM.Implementation;

import java.io.FileInputStream;
import java.util.Properties;

import edu.cmu.lti.searchengine.hw5.MiscHelper;
import edu.cmu.lti.searchengine.hw5.svm.SvmModel;

public class Hw6SVM {

	public static int NUM_FEATURES = 44;

	private static String configFile = "SVM/DATA.TXT";
	private static final String SVM_LOCATION = "/home/bolei/Works/data/11641-hw5/svm_light";
	private static final String MODEL_FOLDER = "zzz_svm_model";
	private static final String RESULT_FOLDER = "zzz_svm_prediction";
	private static final String PREPROCESS_TRAIN_FOLDER = "zzz_svm_preprocess_train";
	private static final String PREPROCESS_TEST_FOLDER = "zzz_svm_preprocess_test";

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			configFile = args[0];
		}
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));

		float c = Float.parseFloat(prop.getProperty("c"));
		String rawTrainFile = prop.getProperty("train");
		String rawTestFile = prop.getProperty("test");

		// preprocess the raw train file, new train file goes to trainFile
		MiscHelper.touchDir(PREPROCESS_TRAIN_FOLDER);
		String trainFile = PREPROCESS_TRAIN_FOLDER + "/newtrain.txt";
		Hw6SVMDataPreprocessor preprocessor = new Hw6SVMDataPreprocessor();
		preprocessor.preprocess(rawTrainFile, trainFile, false, -1);

		// preprocess test file, remove "qid:xxx"
		MiscHelper.touchDir(PREPROCESS_TEST_FOLDER);
		String testFile = PREPROCESS_TEST_FOLDER + "/newteset.txt";
		preprocessor.cleanQid(rawTestFile, testFile);

		SvmModel mt = new SvmModel(SVM_LOCATION, new String[] { "-c",
				Float.toString(c), "-b", "0", "-m", "2048" });
		mt.trainAndTest(c, trainFile, MODEL_FOLDER, testFile, RESULT_FOLDER);
	}
}
