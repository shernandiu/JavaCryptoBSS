package util;

import gui.Logger;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.security.KeyStore;
import java.util.*;

/**
 * Clase usada para almacenar el conjunto de claves y mantener el control de las claves generadas
 *
 * @author Santiago Hernández
 */
public class KeysStore {
	private static final Map<String, Keys> mapOfKeys = new HashMap<>();
	private static final File keyDirectory = new File(Keys.PATH);
	private static final Set<File> trackedFiles = new HashSet<>();
	private static final Set<File> badFiles = new HashSet<>();
	public static List<Keys> listOfKeys = new ArrayList<>();

	static {
		if (!keyDirectory.exists())
			keyDirectory.mkdir();
	}

	/**
	 * Escanea el directorio de claves buscando ficheros con claves no rastreadas y carga las claves nuevas.
	 *
	 * @throws FileAlreadyExistsException Si existe un fichero con el mismo nombre que el directorio de claves
	 */
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

	/**
	 * Devuelve una lista con todos los pares de claves en el directorio de claves
	 * actualizando automáticamente el conjunto mediante {@link #update()}
	 *
	 * @return Lista con los pares de claves disponibles
	 * @throws FileAlreadyExistsException Si existe un fichero con el mismo nombre que el directorio de claves
	 */
	public static List<Keys> getListOfKeys() throws FileAlreadyExistsException {
		update();
		return listOfKeys;
	}

	/**
	 * Devuelve el objeto de un par de claves en base a su nombre.
	 *
	 * @param name Nombre de la clave a obtener
	 * @return Objeto con el par de claves o null si no existe.
	 */
	public static Keys get(String name) {
		return mapOfKeys.get(name);
	}

	/**
	 * Añade un nuevo par de claves al conjunto de claves rastreadas.
	 *
	 * @param k Par de claves a añadir
	 */
	public static void addKey(Keys k) {
		listOfKeys.add(k);
		trackedFiles.add(k.getFile());
		mapOfKeys.put(k.toString(), k);
	}

	public static void main(String[] args) {
		KeyStore.getDefaultType();
	}
}
