package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileOpener {
	private final File file;

	public FileOpener(File file) {
		this.file = file;
	}

	public BufferedReader readFile() {
		try {
			return new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
