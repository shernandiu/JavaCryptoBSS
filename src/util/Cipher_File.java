package util;

import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Subclase de {@link Cipher_msg} especializada en cifrar y descifrar ficheros en lugar de texto.
 * Guarda los ficheros resultantes del encriptado junto al original con la extensión <i>".cif"</i>
 * Guarda los ficheros resultantes del desencriptado junto al cifrado con la extensión <i>".uncif"</i>
 *
 * @author Santiago Hernández
 */
public class Cipher_File extends Cipher_msg {

	public static final String ENCRYPTED_EXTENSION = "cif";     // Extensión de los ficheros encriptados
	public static final String DECRYPTED_EXTENSION = "uncif";   // Extensión de los ficheros desencriptados

	protected final File file;        // Fichero de entrada
	protected File output_file;       // Fichero de salida

	/**
	 * Constructor de la clase para encriptar ficheros indicando el algoritmo a usar y la contraseña.
	 * Llama al constructor de la clase {@link Cipher_msg#Cipher_msg(Algoritmo, char[], InputStream)}
	 *
	 * @param file        Fichero a encriptar con el algoritmo indicado.
	 *                    El fichero debe existir.
	 * @param cypher_type Algoritmo a usar para encriptar
	 * @param password    Contraseña para encriptar el fichero que posteriormente será necesaria para desencriptar
	 *                    el fichero.
	 * @throws FileNotFoundException Si el fichero especificado no existe.
	 */
	public Cipher_File(File file, Algoritmo cypher_type, char[] password) throws FileNotFoundException {
		super(cypher_type, password, new FileInputStream(file));
		this.file = file;
	}

	/**
	 * Constructor de la clase para desencriptar ficheros indicando el fichero encriptado y la contraseña.
	 * La clase se encargará de leer la información necesaria para desencriptar en la cabecera del fichero.
	 * Llama al constructor de la clase {@link Cipher_msg#Cipher_msg(char[], InputStream)}
	 *
	 * @param file     Fichero encriptado a pasar a texto plano.
	 * @param password Contraseña para desencriptar el fichero. Debe ser igual a la usada para encriptarlo.
	 * @throws FileNotFoundException Si el fichero especificado no existe.
	 */
	public Cipher_File(File file, char[] password) throws FileNotFoundException {
		super(password, new FileInputStream(file));
		this.file = file;
	}

	/**
	 * Realiza el proceso de cifrado del fichero generando una cabecera para este y guardándolo tras aplicar el algoritmo
	 * indicado en el constructor con la contraseña correspondiente en un fichero de extensión <i>".cif"</i>.
	 * En el caso de que cifrar el fichero produzca una excepción, se borra el fichero resultante.
	 *
	 * @throws IOException                        Error en la entrada/salida
	 * @throws NoSuchPaddingException             El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException           El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException            La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws InvalidAlgorithmParameterException Faltan parámetros en el algoritmo o son erróneos.
	 * @throws InvalidKeyException                La contraseña no posee un formato adecuado
	 */
	@Override
	public void cipher() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + ENCRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);

		try {
			super.cipher();
		} catch (Exception ex) {
			os.close();
			output_file.delete();
			throw ex;
		}
	}

	/**
	 * Realiza el proceso de descifrado del fichero indicado en el constructor leyendo la cabecera del fichero y lo
	 * descifra con los parámetros de la cabecera y la contraseña indicada, guardándolo en un fichero de extensión
	 * <i>".uncif"</i> si tod es correcto, en caso contrario borra el fichero.
	 *
	 * @throws PasswError                         La contraseña es incorrecta y no se puede descifrar el fichero
	 * @throws HeaderError                        No se puede leer la cabecera o la información es incorrecta.
	 * @throws IOException                        Error en la entrada/salida
	 * @throws NoSuchPaddingException             El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException           El algoritmo de cifrado no está disponible este cifrado en cuestión.
	 * @throws InvalidKeySpecException            La contraseña no se acepta para el algoritmo en cuestión.
	 * @throws InvalidAlgorithmParameterException Faltan parámetros en el algoritmo o son erróneos.
	 * @throws InvalidKeyException                La contraseña no posee un formato adecuado
	 */
	@Override
	public void decipher() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, PasswError, IllegalBlockSizeException, BadPaddingException {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + DECRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);
		try {
			super.decipher();
		} catch (Exception ex) {
			os.close();
			output_file.delete();
			throw ex;
		}
	}

	/**
	 * Devuelve el fichero donde se ha volcado el texto cifrado o en claro, dependiendo de la operación realizada.
	 * Devuelve {@code null} si no se ha realizado ningún proceso de cifrado o descifrado.
	 *
	 * @return Fichero de salida del proceso realizado.
	 */
	public File getOutput_file() {
		return output_file;
	}

}
