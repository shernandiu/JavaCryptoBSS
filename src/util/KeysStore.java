package util;

import gui.Logger;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class KeysStore {
	public static List<Keys> listOfKeys = new ArrayList<>();
	private static File keyDirectory = new File(Keys.PATH);
	private static Set<File> trackedFiles = new HashSet<>();
	private static Set<File> badFiles = new HashSet<>();

	static {
		if (!keyDirectory.exists())
			keyDirectory.mkdir();
	}

	private static void update() throws FileAlreadyExistsException {
		if (!keyDirectory.isDirectory())
			throw new FileAlreadyExistsException("File with name of key folder");

		File[] files = keyDirectory.listFiles(pathname -> pathname.getName().matches(".*" + Keys.EXTENSION + "$"));

		assert files != null;
		for (File f : files) {              // add all not tracked files.
			if (trackedFiles.contains(f))   // ignore faulty if not adviced before.
				continue;
			try {
				listOfKeys.add(new Keys(f));
				trackedFiles.add(f);
			} catch (Exception e) {
				if (!badFiles.contains(f)) {
//					Logger.add_error("Error al cargar el fichero: " + f.getName());
					badFiles.add(f);
				}
			}
		}
	}

	public static List<Keys> getListOfKeys() throws FileAlreadyExistsException {
		update();
		return listOfKeys;
	}

	public static void main(String[] args) {
		KeyStore.getDefaultType();
	}
}
