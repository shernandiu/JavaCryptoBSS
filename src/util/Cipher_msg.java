package util;

import byss.Options;
import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Clase para manejar el encriptado y desencriptado de un mensaje de texto en formato de stream
 * mediante un algoritmo de encriptado PBE.
 * Guarda los mensajes indicando en una cabecera en el mensaje el algoritmo de cifrado usado así como otra
 * información parar poder desencriptar indicando solo el mensaje y la contraseña.
 *
 * @author Santiago Hernández
 */
public class Cipher_msg extends HeaderReader {
	protected static final int ITERATIONCOUNT = 1_000;  // número de veces a iterar para obtener la clave
	protected static final int SALT_DEFAULT_SIZE = 8;   // tamaño del salt a generar en bytes
	protected static final int BUFFER_SIZE = 1024;      // tamaño del buffer para leer y escribir en los flujos de E/S


	protected final char[] password;
	protected Cipher c;
	protected SecretKey sKey;
	protected PBEParameterSpec pPS;

	/**
	 * Constructor de la clase indicado para realizar la encriptación de un mensaje.
	 *
	 * @param cypher_type Algoritmo a usar para encriptar
	 * @param password    Contraseña a usar para encriptar y posteriormente se usará para desencriptar el mensaje cifrado
	 * @param is          Stream con el texto de entrada. Generable desde un {@link java.io.ByteArrayInputStream}
	 */
	public Cipher_msg(Algoritmo cypher_type, char[] password, InputStream is) {
		this.is = is;
		this.cypher_type = cypher_type;
		this.password = password;
		if (password != null) {
			for (int i = 0; i < this.password.length; i++) {
				this.password[i] &= 0x7F;
			}
		}
		os = new ByteArrayOutputStream();
		option = Options.OP_SYMMETRIC_CIPHER;
	}

	/**
	 * Constructor de la clase indicado para realizar el desencriptado de un mensaje. No es necesario indicar el tipo de
	 * algoritmo, ya que se encuentra indicado en la cabecera.
	 *
	 * @param password Contraseña para desencriptar el mensaje. En caso de que no sea correcta, es posible que no pueda
	 *                 ni producir texto ilegible.
	 * @param is       Stream con el texto cifrado de entrada obtenido previamente cifrando un mensaje. Generable desde
	 *                 un {@link java.io.ByteArrayInputStream}
	 */
	public Cipher_msg(char[] password, InputStream is) {
		this.is = is;
		this.password = password;
		if (password != null) {
			for (int i = 0; i < this.password.length; i++) {
				this.password[i] &= 0x7F;
			}
		}
		os = new ByteArrayOutputStream();
		option = Options.OP_SYMMETRIC_CIPHER;
	}

	/**
	 * Inicializa los parámetros necesarios para hacer las funciones de encriptación y desencriptación según
	 * los métodos proporcionados por {@link javax.crypto} e inicializa la clase {@link Cipher} necesaria para
	 * leer o escribir el texto cifrado.
	 *
	 * @throws NoSuchAlgorithmException El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException  La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws NoSuchPaddingException   El algoritmo de relleno no está disponible
	 */
	protected void generate_cypher() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		pPS = new PBEParameterSpec(data, ITERATIONCOUNT);
		SecretKeyFactory kf = SecretKeyFactory.getInstance(cypher_type.getAlgorithm());
		sKey = kf.generateSecret(pbeKeySpec);


		c = Cipher.getInstance(cypher_type.getAlgorithm());
	}

	/**
	 * Cifra el mensaje indicado en el constructor y guarda el texto resultante en su buffer de salida, obtenible mediante
	 * el método {@link Cipher_msg#getText()}.
	 *
	 * @throws IOException                        Problema con la entrada y salida del mensaje
	 * @throws NoSuchPaddingException             El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException           El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException            La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws InvalidAlgorithmParameterException Faltan parámetros en el algoritmo o son erróneos.
	 * @throws InvalidKeyException                Especificaciones de la contraseña incorrecta.
	 */
	public void cipher() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		data = generate_salt();

		generate_cypher();

		c.init(Cipher.ENCRYPT_MODE, sKey, pPS);

		CipherOutputStream cos = new CipherOutputStream(os, c);

		generate_header();

		byte[] buffer = new byte[BUFFER_SIZE];

		int read;
		while (is.available() > 0) {
			read = is.read(buffer);
			cos.write(buffer, 0, read);
		}

		cos.close();
		is.close();
	}

	/**
	 * Descifra el mensaje cifrado indicado en el constructor leyendo los parámetros indicados en la cabecera del mensaje
	 * guardando el texto en claro resultante en el buffer de salida accesible mediante {@link Cipher_msg#getText()} en
	 * el caso de que no se produzcan errores.
	 * <p>Al finalizar sin errores cierra los flujos de entrada y salida.
	 *
	 * @throws IOException                        Error con la entrada y salida de los mensajes
	 * @throws PasswError                         La contraseña no es correcta y no se puede descifrar el mensaje.
	 * @throws HeaderError                        No se puede leer la cabecera del mensaje o esta no es correcta.
	 * @throws NoSuchPaddingException             El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException           El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException            La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws InvalidAlgorithmParameterException Faltan parámetros en el algoritmo o son erróneos.
	 */
	public void decipher() throws IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, PasswError, IllegalBlockSizeException, BadPaddingException {
		read_header();

		generate_cypher();

		c.init(Cipher.DECRYPT_MODE, sKey, pPS);

		try (CipherInputStream cis = new CipherInputStream(is, c)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read;
			while ((read = cis.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}
		} catch (IOException ex) {
			throw new PasswError(ex.getMessage());
		} finally {
			os.close();
		}
	}

	private byte[] generate_salt() {
		byte[] salt = new byte[SALT_DEFAULT_SIZE];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		return salt;
	}


	/**
	 * Lee el buffer de salida con el mensaje cifrado resultante de haber cifrado o el texto en claro tras haber
	 * descifrado.
	 *
	 * @return Array de bytes con el resultado del proceso
	 * @throws IOException Si ocurre un error al limpiar el flujo.
	 */
	public byte[] getText() throws IOException {
		os.flush();
		ByteArrayOutputStream os1 = (ByteArrayOutputStream) os;
		return os1.toByteArray();
	}

	/**
	 * Devuelve el algoritmo que se ha usado en el proceso de encriptar o el que ha sido leído de la cabecera y se ha
	 * usado para descifrar el texto en claro.
	 *
	 * @return Algoritmo usado para encriptar o desencriptar.
	 */
	public Algoritmo getCypher_type() {
		return cypher_type;
	}
}
