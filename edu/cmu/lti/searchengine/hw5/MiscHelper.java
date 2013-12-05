package edu.cmu.lti.searchengine.hw5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;

public class MiscHelper {

	public static void touchDir(String folderName) {
		File folder = new File(folderName);
		if (folder.exists() == false) {
			folder.mkdir();
		}
	}

	public static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
				reader = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
				writer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeOutputStream(PrintStream finalOut) {
		if (finalOut != null) {
			try {
				finalOut.close();
				finalOut = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void catFile(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader br = null;

		try {
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} finally {
			MiscHelper.closeReader(br);
		}
	}
}
