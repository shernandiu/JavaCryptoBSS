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
	private static final Map<String, Keys> mapOfKeys = new HashMap<>();
	private static final File keyDirectory = new File(Keys.PATH);
	private static final Set<File> trackedFiles = new HashSet<>();
	private static final Set<File> badFiles = new HashSet<>();

	static {
		if (!keyDirectory.exists())
			keyDirectory.mkdir();
	}

	public static void update() throws FileAlreadyExistsException {
		if (!keyDirectory.isDirectory())
			throw new FileAlreadyExistsException("File with name of key folder");

		File[] files = keyDirectory.listFiles(pathname -> pathname.getName().matches(".*" + Keys.EXTENSION + "$"));

		assert files != null;
		for (File f : files) {              // add all not tracked files.
			if (trackedFiles.contains(f))   // ignore faulty if not adviced before.
				continue;
			try {
				Keys k = new Keys(f);
				listOfKeys.add(k);
				mapOfKeys.put(k.toString(), k);
				trackedFiles.add(f);
			} catch (Exception e) {
				if (!badFiles.contains(f)) {
					Logger.add_error("Error al cargar el fichero: " + f.getName());
					badFiles.add(f);
				}
			}
		}
	}

	public static List<Keys> getListOfKeys() throws FileAlreadyExistsException {
		update();
		return listOfKeys;
	}

	public static Keys get(String name) {
		return mapOfKeys.get(name);
	}

	public static void addKey(Keys k) {
		listOfKeys.add(k);
		trackedFiles.add(k.getFile());
		mapOfKeys.put(k.toString(), k);
	}

	public static void main(String[] args) {
		KeyStore.getDefaultType();
	}
}
