package util;

import byss.Header;

import java.io.File;
import java.io.FileInputStream;

/**
 * Clase de utilidad para obtener el algoritmo leyendo la cabecera de un fichero
 *
 * @author Santiago Hernández
 */
public class AlgType {
	/**
	 * Lee la cabecera de un fichero y devuelve el tipo de algoritmo que
	 * indica la cabecera según se ha usado para cifrar de forma simétrica,
	 * asimétrica, firma o no existe ninguna cabecera.
	 *
	 * @param f Fichero a leer
	 * @return Tipo de algoritmo {@link Algoritmo#PBE}, {@link Algoritmo#PKEY},
	 * {@link Algoritmo#SIGN} o -1 ni no se ha usado ningún algoritmo, no se reconoce
	 * o no dispone de cabecera.
	 * @throws Exception Si ocurre algún error.
	 */
	public static int getType(File f) throws Exception {
		Header h = new Header();
		if (h.load(new FileInputStream(f)))
			return Algoritmo.get(h.getAlgorithm1()).getType();
		else
			return -1;
	}
}
//TODO cambiar a Header.OP en lugar de Algoritmo
