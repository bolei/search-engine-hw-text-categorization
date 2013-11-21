import java.io.FileInputStream;
import java.util.Properties;

import edu.cmu.lti.searchengine.hw5.Hw5LogRegMain;

public class MyRun {

	private static String configFile = "DATA.TXT";
	private static String logregConfigFile = "logreg-config.txt";

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			configFile = args[0];
		}
		Properties prop = new Properties();
		prop.load(new FileInputStream(configFile));
		Properties logregConfig = new Properties();
		logregConfig.load(new FileInputStream(logregConfigFile));

		Hw5LogRegMain.doMain(prop.getProperty("train"),
				prop.getProperty("test"),
				Float.parseFloat(prop.getProperty("c")), logregConfig);
	}
}
