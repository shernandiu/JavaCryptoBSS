package util;

import byss.Header;
import gui.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

public class AlgType {
	public static int getType(File f) throws Exception {
		Header h = new Header();
		if (h.load(new FileInputStream(f)))
			return Algoritmo.get(h.getAlgorithm1()).getType();
		else
			return -1;
	}
}
