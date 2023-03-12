package util;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Algoritmo {
	private static final Algoritmo[] list_alg;
	private static final Map<String, Algoritmo> alg_map = new HashMap<>();

	static {
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


	public static Algoritmo[] getListOfAlgorithms() {
		return list_alg;
	}

	public static Algoritmo get(String algorithm) throws NoSuchAlgorithmException {
		return Optional.ofNullable(alg_map.get(algorithm)).orElseThrow(() -> new NoSuchAlgorithmException("Algoritmo no disponible en el sistema"));
	}

	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		return common_name;
	}


}
