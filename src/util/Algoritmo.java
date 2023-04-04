package util;

import byssSYM.Options;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Clase para almacenar los diferentes tipos de algoritmos disponibles en la JVM y sus nombres comunes.
 *
 * @author Santiago Hernández
 */
public class Algoritmo {
	private static final int PBE = 0;
	private static final int PKEY = 1;
	private static final int SIGN = 2;

	public static final Algoritmo[] list_alg;                              //lista con todos los algoritmos disponibles
	public static final Algoritmo[] list_PBE_alg;                          //lista con todos los algoritmos de PBE disponibles
	public static final Algoritmo[] list_PKEY_alg;                         //lista con todos los algoritmos de clave pública disponibles
	public static final Map<String, Algoritmo> alg_map = new HashMap<>();  // diccionario con el par nombre->algoritmo

	static {
		/* Obtención de los algoritmos disponibles
		 * Filtrando los algoritmos de tipo SecretKey,
		 *  PBE y sin Hmac (al requerir un IV al desencriptar) */
		String[] alg_str_list = Arrays.stream(Security.getProviders())
				.flatMap(provider -> provider.getServices().stream())
				.filter(service -> "SecretKeyFactory".equals(service.getType()))
				.map(Provider.Service::getAlgorithm).filter(e -> e.startsWith("PBE")).filter(e -> !e.contains("Hmac")).toArray(String[]::new);

		list_PBE_alg = new Algoritmo[alg_str_list.length];
		for (int i = 0; i < alg_str_list.length; i++) {
			String str = alg_str_list[i];
			list_PBE_alg[i] = new Algoritmo(str, str.replace("With", " con ").replace("And", " & ").replaceFirst("_(.*)", " $1 bits"), PBE);
		}

		list_PKEY_alg = new Algoritmo[Options.publicAlgorithms.length];
		for (int i = 0; i < list_PKEY_alg.length; i++) {
			String str = Options.publicAlgorithms[i];
			list_PKEY_alg[i] = new Algoritmo(str, str.replaceFirst("/", " con ").replaceFirst("/", " & ").replaceFirst("_(.*)", " $1 bits"), PKEY);
		}

		list_alg = new Algoritmo[list_PBE_alg.length + list_PKEY_alg.length];
		// rellenar la lista de algoritmos
		int i = 0;
		for (Algoritmo a : list_PBE_alg) {
			list_alg[i] = a;
			i++;
		}
		for (Algoritmo a : list_PKEY_alg) {
			list_alg[i] = a;
			i++;
		}
		for (Algoritmo a : list_alg) {
			alg_map.put(a.algorithm, a);
		}
	}

	private final String algorithm;
	private final String common_name;
	private final int type;

	Algoritmo(String algorithm, String name, int type) {
		this.algorithm = algorithm;
		this.common_name = name;
		this.type = type;
	}

	/**
	 * Devuelve la lista con todos los algoritmos PBE usables
	 * por la JVM.
	 *
	 * @return Array con los algoritmos usables.
	 */
	public static Algoritmo[] getListOfAlgorithms() {
		return list_alg;
	}

	/**
	 * Obtiene un algoritmo de la lista de usables con base en su nombre como cadena
	 * o arroja una excepción si el algoritmo proporcionado no se encuentra en la lista
	 * de algoritmos usables para desencriptar.
	 *
	 * @param algorithm String con el valor del algoritmo buscado
	 * @return Objeto algoritmo
	 * @throws NoSuchAlgorithmException Si el algoritmo no está disponible para la desencripción
	 *                                  o no existe.
	 */
	public static Algoritmo get(String algorithm) throws NoSuchAlgorithmException {
		return Optional.ofNullable(alg_map.get(algorithm)).orElseThrow(() -> new NoSuchAlgorithmException("Algoritmo no disponible en el sistema"));
	}

	/**
	 * Devuelve el nombre del algoritmo para ser usado en los métodos de la librería de criptografía.
	 *
	 * @return String con el nombre del algoritmo
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Devuelve el nombre común del algoritmo para ser mostrado al usuario.
	 *
	 * @return String con el nombre común.
	 */
	@Override
	public String toString() {
		return common_name;
	}


}
