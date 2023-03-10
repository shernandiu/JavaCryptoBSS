package util;

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
	private static final Algoritmo[] list_alg;                              //lista con todos los algoritmos disponibles
	private static final Map<String, Algoritmo> alg_map = new HashMap<>();  // diccionario con el par nombre->algoritmo

	static {
		/* Obtención de los algoritmos disponibles
		 * Filtrando los algoritmos de tipo SecretKey,
		 *  PBE y sin Hmac (al requerir un IV al desencriptar) */
		String[] alg_str_list = Arrays.stream(Security.getProviders())
				.flatMap(provider -> provider.getServices().stream())
				.filter(service -> "SecretKeyFactory".equals(service.getType()))
				.map(Provider.Service::getAlgorithm).filter(e -> e.startsWith("PBE")).filter(e -> !e.contains("Hmac")).toArray(String[]::new);

		list_alg = new Algoritmo[alg_str_list.length];

		for (int i = 0; i < alg_str_list.length; i++) {
			String str = alg_str_list[i];
			list_alg[i] = new Algoritmo(str, str.replace("With", " con ").replace("And", " & ").replaceFirst("_(.*)", " $1 bits"));
		}

		for (Algoritmo a : list_alg) {
			alg_map.put(a.algorithm, a);
		}
	}

	private final String algorithm;
	private final String common_name;

	Algoritmo(String algorithm, String name) {
		this.algorithm = algorithm;
		this.common_name = name;
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
