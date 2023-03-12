package byss;
/* <p> Descripción: Prácticas de Seguridad </p>
 * <p> DISIT de la UEx </p>
 * @author Lorenzo M. Martínez Bravo
 * @version 1.0, 2022
 */

import util.Algoritmo;

import java.util.Arrays;

/**
 * <p>Define opciones de proteccion de mensajes con algoritmos criptograficos, asi
 * como constantes (arrays) con los nombres de los diferentes tipos de algoritmos
 * utilizables.
 * </p>
 */
public class Options {
	/**
	 * Constantes que representan las diferentes operaciones criptograficas aplicables a un objeto:
	 * -Cifrado simétrico
	 * -Hash/Mac
	 * -Cifrado de clave publica
	 * -Firma digital
	 */
	public final static byte OP_NONE = 0;
	public final static byte OP_SYMMETRIC_CIPHER = 1;
	public final static byte OP_HASH_MAC = 10;
	public final static byte OP_PUBLIC_CIPHER = 20;
	public final static byte OP_SIGNED = 30;
	/**
	 * Ningun algoritmo
	 */
	public final static String OP_NONE_ALGORITHM = "none";
	/**
	 * Nombres de algoritmos de cifrado simétrico PBE
	 */
	public final static String PBEAlgorithms[] = Arrays.stream(Algoritmo.getListOfAlgorithms()).map(Algoritmo::getAlgorithm).toArray(String[]::new);
	/**
	 * Nombres de algoritmos de cifrado simétrico, para generación de claves:
	 */
	public final static String KeyAlgorithms[] = {"AES", "ARCFOUR", "DESede"};
	/**
	 * Nombres compuestos de algoritmos para cifrado simétrico, incluyendo Modo de operaciópn y Modo de Relleno:
	 * Algoritmo/ModoOperacion/ModoRelleno
	 */
	public final static String SymmetrcialAlgorithms[] = {"AES/ECB/PKCS5PADDING", "AES/CBC/PKCS5PADDING", "ARCFOUR/ECB/NOPADDING", "DESede/CBC/PKCS5PADDING"};
	/**
	 * Rangos de tamaños de claves soportados para cada algoritmo
	 */
	public final static String algorithmSizeKey[] = {"AES", "128", "256",
			"ARCFOUR", "40", "1024",
			"DESede", "112", "168"};
	/**
	 * Nombres de algoritmos para cifrado simétrico de claves
	 */
	public final static String KeyWrapAlgorithms[] = {"PBEWithMD5AndDES"};

	/**
	 * Devuelve el rango del tamaño de las claves de un agoritmo dado, según la Tabla definida.
	 *
	 * @param algorithm Nombre del algoritmo
	 * @return array de int (min, max)
	 */
	public static int[] getKeySizeRange(String algorithm) {
		int[] range = {0, 0};
		for (int i = 0; i < algorithmSizeKey.length; )
			if (algorithmSizeKey[i].equals(algorithm)) {
				range[0] = Integer.parseInt(algorithmSizeKey[i + 1]);
				range[1] = Integer.parseInt(algorithmSizeKey[i + 2]);
				return range;
			} else
				i += 3;
		return range;
	}

	/**
	 * Extrae el nombre del algoritmo de cifrado de un nombre compuesto. Por ejemplo, "AES/ECB/NoPadding", retornaría "AES".
	 *
	 * @param algorithm algoritmo compuesto a comprobar
	 * @return Nombre del algoritmo de cifrado, sin los modos
	 */
	public static String extractAlgorithmName(String algorithm) {
		String[] parts = algorithm.split("/");
		return parts[0];
	}

	/**
	 * Comprueba si un algoritmo pertenece a una lista (tipo de algoritmos)
	 *
	 * @param type      lista de algoritmos
	 * @param algorithm algoritmo a comprobar
	 * @return <code>true</code> el algoritmo pertenece al tipo, o <code>false</code> en caso contrario.
	 */
	public static boolean isTypeAlgorithm(String[] type, String algorithm) {
		int i = search(type, algorithm);
		return (i != -1);
	}

	/**
	 * Busca un String en un array de String
	 *
	 * @param stringList lista de Strings
	 * @param item       String a buscar
	 * @return posicion de <code>item</code> en <code>stringList</code> si la encuentra, -1 en caso contrario
	 */
	public static int search(String stringList[], String item) {
		int i;
		for (i = stringList.length - 1; i != -1; i--)
			if (stringList[i].compareTo(item) == 0)
				break;
		return i;
	}
}
