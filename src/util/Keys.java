package util;

import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para encapsular los conjuntos de claves públicas/privadas
 * y guardarlas y leerlas de ficheros.
 * Los ficheros de claves tendrán la extensión <i>.key</i>
 * y se guardarán en la carpeta <i>keypairs/</i>
 *
 * @author Santiago Hernández
 */
public class Keys {
	public static final int NOT_ENCRYPTED = 0;
	public static final int ON_CACHE = 1;
	public static final int NOT_AVAILABLE = 2;
	public static final String PATH = "./keypairs/";
	public static final String EXTENSION = ".key";
	private static final int DEFAULT_KEY_SIZE = 512;
	private PublicKey puk;
	private PrivateKey prk;
	private String name;
	private String type;
	private int size;
	private byte[] prk_encrypted;
	private boolean privateAvaliable;
	private File file;

	/**
	 * Genera un nuevo par de claves pública y privada de tipo <i>RSA</i>
	 * y las guarda en el fichero correspondiente.
	 * Si se especifica {@code password} se encriptará la clave privada con dicha contraseña
	 * con el primer algoritmo de <i>PBE</i> disponible.
	 * Si {@code password} es {@code null} no se encripta la clave privada.
	 *
	 * @param name     Nombre del par de  claves a crear
	 * @param password Contraseña para encriptar la clave privada
	 *                 o {@code null} para no hacerlo
	 * @throws GeneralSecurityException   Error al crear la clave
	 * @throws FileAlreadyExistsException Ya existe un par de claves con el mismo nombre en el directorio de claves.
	 * @throws IOException                Error en la entrada/salida
	 */
	public Keys(String name, char[] password) throws GeneralSecurityException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(DEFAULT_KEY_SIZE); // 512 bits
		KeyPair kp = kpg.generateKeyPair();
		puk = kp.getPublic();
		prk = kp.getPrivate();
		this.name = name;
		this.size = DEFAULT_KEY_SIZE;
		this.type = "RSA";
		saveKeys(password);
		privateAvaliable = password == null;
		KeysStore.addKey(this);
	}

	/**
	 * Carga un par de claves del fichero especificado.
	 * Si la clave privada se encuentra cifrada, no podrá usarse
	 * hasta descifrar la clave mediante @{@link Keys#decipherKey(char[])}.
	 *
	 * @param f Fichero con el par de claves
	 * @throws IOException              Error en la entrada/salida
	 * @throws NoSuchAlgorithmException El tipo de claves no está disponible.
	 * @throws InvalidKeySpecException  Los parámetros de la clave en el fichero no son correctos.
	 */
	public Keys(File f) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		loadKeys(f);
		file = f;
	}

	/**
	 * Guarda la clave en su fichero correspondiente con su nombre,
	 * encriptando la clave privada si se indica una contraseña.
	 *
	 * @param password Contraseña para cifrar la clave privada.
	 * @throws GeneralSecurityException   Error al crear la clave
	 * @throws FileAlreadyExistsException Ya existe un par de claves con el mismo nombre en el directorio de claves.
	 * @throws IOException                Error en la entrada/salida
	 */
	private void saveKeys(char[] password) throws IOException, GeneralSecurityException {
		file = new File(PATH + name + EXTENSION);
		if (file.exists())
			throw new FileAlreadyExistsException(name + " exists");
		// do not overwrite keys
		StringBuilder output = new StringBuilder();

		// header
		output.append(name).append("\n");
		output.append(String.format("%s # %d bits\n", type, size));
		output.append(String.format("private_ciphered=%s%s\n", password != null, password != null ? " # " + Algoritmo.list_PBE_alg[0].getAlgorithm() : ""));
		output.append("\n");

		// build public key
		output.append("Public key:\n");
		String strpuk = Base64.getEncoder().encodeToString(puk.getEncoded());

		for (int i = 0; i < strpuk.length(); i += 40)
			output.append(strpuk, i, Math.min(i + 40, strpuk.length())).append("\n");
		output.append("\n");

		// build private key
		output.append("Private key:\n");
		String strprk = Base64.getEncoder().encodeToString(password == null ? prk.getEncoded() : cipherPrivateKey(password));

		for (int i = 0; i < strprk.length(); i += 40)
			output.append(strprk, i, Math.min(i + 40, strprk.length())).append("\n");
		output.append("\n");

		if (!file.getParentFile().exists())
			file.getParentFile().mkdir();
		file.createNewFile();

		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
		osw.write(String.format(output.toString()));
		osw.close();
	}

	/**
	 * Cifra la clave privada con la contraseña indicada mediante
	 * el primer algoritmo de PBE disponible usando uso de @{@link Cipher_msg}.
	 *
	 * @param password Contraseña con la que cifrar la clave.
	 * @return Clave privada cifrada como mensaje con cabecera.
	 * @throws IOException                        Problema con la entrada y salida del mensaje
	 * @throws NoSuchPaddingException             El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException           El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException            La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws InvalidAlgorithmParameterException Faltan parámetros en el algoritmo o son erróneos.
	 * @throws InvalidKeyException                Especificaciones de la contraseña incorrecta.
	 */
	private byte[] cipherPrivateKey(char[] password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		Cipher_msg cm = new Cipher_msg(Algoritmo.list_PBE_alg[0], password, new ByteArrayInputStream(prk.getEncoded()));
		cm.cipher();
		return cm.getText();
	}

	/**
	 * Carga un par de claves del fichero especificado.
	 * Si la clave privada se encuentra cifrada, no podrá usarse
	 * hasta descifrar la clave mediante @{@link Keys#decipherKey(char[])}.
	 *
	 * @param f Fichero con el par de claves
	 * @throws IOException              Error en la entrada/salida
	 * @throws NoSuchAlgorithmException El tipo de claves no está disponible.
	 * @throws InvalidKeySpecException  Los parámetros de la clave en el fichero no son correctos.
	 */
	private void loadKeys(File f) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String tmp;
		String[] splt;
		StringBuilder puk_sb = new StringBuilder();
		StringBuilder prk_sb = new StringBuilder();
		Matcher m;

		BufferedReader br = new BufferedReader(new FileReader(f));
		name = br.readLine();

		// TYPE AND SIZE
		tmp = br.readLine();
		splt = tmp.split(" *# *");
		type = splt[0].trim();
		m = Pattern.compile("(\\d+)").matcher(splt[1]);
		m.find();
		size = Integer.parseInt(m.group(1));

		// Encryption / type
		tmp = br.readLine();
		splt = tmp.split(" *# *");
		m = Pattern.compile("(?:private_ciphered *= *)(true|false)").matcher(splt[0]);
		m.find();
		boolean encryp = Boolean.parseBoolean(m.group(1));
		privateAvaliable = !encryp;
		Algoritmo encryp_type;
		if (encryp)
			encryp_type = Algoritmo.get(splt[1].trim());

		// Find public key
		do {
			tmp = br.readLine().trim();
		} while (!tmp.equals("Public key:"));

		// Get public key
		tmp = br.readLine().trim();
		while (!(tmp.equals("") | tmp.equals("Private key:"))) {
			puk_sb.append(tmp);
			tmp = br.readLine().trim();
		}

		// Find private key
		do {
			tmp = br.readLine().trim();
		} while (!tmp.equals("Private key:"));

		// Get private key
		tmp = br.readLine().trim();
		while (!(tmp.equals(""))) {
			prk_sb.append(tmp);
			tmp = br.readLine().trim();
		}

		KeyFactory kf = KeyFactory.getInstance(type);
		KeySpec kp = new X509EncodedKeySpec(Base64.getDecoder().decode(puk_sb.toString()));
		puk = kf.generatePublic(kp);

		if (!encryp) {
			setPRK(Base64.getDecoder().decode(prk_sb.toString().getBytes()));
		} else {
			prk_encrypted = Base64.getDecoder().decode(prk_sb.toString());
		}

		br.close();
	}

	/**
	 * Devuelve la clave pública del par de claves.
	 *
	 * @return Clave pública.
	 */
	public PublicKey getPuk() {
		return puk;
	}

	/**
	 * Devuelve la clave privada del par de claves.
	 *
	 * @return Clave privada o null si no se ha descifrado.
	 */
	public PrivateKey getPrk() {
		return prk;
	}

	/**
	 * Devuelve el estado en el que se encuentra la clave privada del par de claves.
	 *
	 * @return {@link Keys#NOT_AVAILABLE} Si la clave privada no se encuentra disponible al estar cifrada.
	 * <p>{@link Keys#ON_CACHE} Si la clave privada se encuentra cifrada pero ha sido descifrada durante la ejecución.
	 * <p>{@link Keys#NOT_ENCRYPTED} Si la clave privada no se encuentra cifrada y por tanto está disponible.
	 */
	public int privateAvailable() {
		if (prk == null)
			return NOT_AVAILABLE;
		if (!privateAvaliable)
			return ON_CACHE;
		else
			return NOT_ENCRYPTED;
	}

	public String toString() {
		return name;
	}

	/**
	 * Descifra la clave privada con la contraseña indicada
	 * haciendo uso de @{@link Cipher_msg#decipher()}
	 * La clave privada será guardada en caché durante toda la ejecución del programa
	 * o se elimine el objeto.
	 *
	 * @param password Contraseña para descifrar la clave
	 * @return {@code true} si se ha logrado descifrar o {@code false} si no
	 * @throws HeaderError                        Error al leer la cabecera de la clave encriptada
	 * @throws PasswError                         La contraseña de la clave no se corresponde
	 * @throws InvalidAlgorithmParameterException La clave no se encuentra encriptada o ya ha sido desencriptada
	 * @throws IOException                        Error con la entrada/salida
	 * @throws GeneralSecurityException           Error con el desencriptado.
	 */
	public boolean decipherKey(char[] password) throws GeneralSecurityException, HeaderError, PasswError, IOException {
		if (prk != null || prk_encrypted == null)
			throw new InvalidParameterException("Key not encrypted");

		Cipher_msg c = new Cipher_msg(password, new ByteArrayInputStream(prk_encrypted));
		c.decipher();
		setPRK(c.getText());
		return prk != null;
	}

	/**
	 * Establece la clave privada según un byte array con el contenido de la clave.
	 *
	 * @param prk_sb Array de bytes con el contenido de la clave.
	 * @throws NoSuchAlgorithmException Algoritmos de la clave no disponible.
	 * @throws InvalidKeySpecException  Especificaciones de la clave no disponibles.
	 */
	private void setPRK(byte[] prk_sb) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory kf = KeyFactory.getInstance(type);
		KeySpec kp = new PKCS8EncodedKeySpec(prk_sb);
		prk = kf.generatePrivate(kp);
	}

	/**
	 * Devuelve el fichero donde se encuentra guardado el par de claves.
	 *
	 * @return Fichero con el par de claves.
	 */
	public File getFile() {
		return file;
	}
}
