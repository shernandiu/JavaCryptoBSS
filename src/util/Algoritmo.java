package util;

public enum Algoritmo {
	PBEMD5DES("PBEWithMD5AndDES", "PBE con MD5 y DES"),
	PBEDM53DES("PBEWithMD5AndTripleDES", "PBE con MD5 y 3DES"),
	PBESHA1DESEDE("PBEWithSHA1AndDESede", "PBE con SHA1 y DESede"),
	PBESHA1RC240("PBEWithSHA1andRC2_40", "PBE con SHA1 y RC2"),

	;

	private final String algorithm;
	private final String name;

	Algoritmo(String algorithm, String name) {
		this.algorithm = algorithm;
		this.name = name;

	}

	public static Algoritmo[] getListOfAlgorithms() {
		return Algoritmo.values();
	}

	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		return name;
	}

}
