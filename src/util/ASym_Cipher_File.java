package util;

import byss.Options;
import exceptions.HeaderError;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

/**
 * Subclase de {@link Cipher_File} especializada en cifrar y descifrar ficheros mediante cifrado de clave pública.
 * Guarda los ficheros resultantes del encriptado junto al original con la extensión <i>".cif"</i>
 * Guarda los ficheros resultantes del desencriptado junto al cifrado con la extensión <i>".uncif"</i>
 *
 * @author Santiago Hernández
 */
public class ASym_Cipher_File extends Cipher_File {

	private PublicKey puk;
	private PrivateKey prk;

	/**
	 * Constructor de la clase indicado para inicializar el cifrado de un fichero.
	 *
	 * @param file        Fichero a encriptar.
	 * @param cypher_type Tipo de algoritmo de cifrado asimétrico a usar.
	 * @param puk         Clave pública con la que encriptar el fichero
	 * @throws FileNotFoundException Si el fichero no existe
	 */
	public ASym_Cipher_File(File file, Algoritmo cypher_type, PublicKey puk) throws FileNotFoundException {
		super(file, cypher_type, null);
		this.puk = puk;
		data = new byte[0];
		option = Options.OP_PUBLIC_CIPHER;
	}

	/**
	 * Constructor de la clase indicado para inicializar el descifrado de un fichero.
	 * Lee la cabecera para conocer el algoritmo a usar.
	 *
	 * @param file Fichero a descifrar.
	 * @param prk  Clave privada con la qu desencriptar el fichero
	 * @throws FileNotFoundException Si el fichero no existe
	 */
	public ASym_Cipher_File(File file, PrivateKey prk) throws FileNotFoundException {
		super(file, null);
		this.prk = prk;
		option = Options.OP_PUBLIC_CIPHER;
	}

	// Usado para pruebas
	public static void main(String[] args) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512); // 512 bits
		KeyPair kp = kpg.generateKeyPair();
		System.out.println("Encriptando");
		ASym_Cipher_File acf = new ASym_Cipher_File(new File("./test/resources/test.txt"), Algoritmo.get("RSA/ECB/PKCS1Padding"), kp.getPublic());
		acf.cipher();
		System.out.println("Desencriptando");
		acf = new ASym_Cipher_File(new File("./test/resources/test.cif"), kp.getPrivate());
		acf.decipher();

		ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream("./test/resources/keys"));
		fos.writeObject(kp);
	}

	/**
	 * Realiza el proceso de cifrado del fichero generando una cabecera para este y guardándolo tras aplicar el algoritmo
	 * indicado en el constructor con la contraseña correspondiente en un fichero de extensión <i>".cif"</i>.
	 * En el caso de que cifrar el fichero produzca una excepción, se borra el fichero resultante.
	 *
	 * @throws IOException               Error en la entrada/salida
	 * @throws NoSuchPaddingException    El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException  El algoritmo de cifrado está disponible o
	 *                                   no se puede usar para este cifrado
	 * @throws InvalidKeyException       La clave pública usada no cumple con los requisitos para el tipo de cifrado
	 * @throws IllegalBlockSizeException La forma de operar en bloques no es correcta
	 */
	@Override
	public void cipher() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		generateOutputFile(true);
		os = new FileOutputStream(output_file);

		try {
			generate_cypher();
			c.init(Cipher.ENCRYPT_MODE, puk);

			generate_header();

			byte[] buffer = new byte[53];
			byte[] output_buffer;

			int read;
			while ((read = is.read(buffer)) >= 0) {

				output_buffer = c.doFinal(buffer, 0, read);
				os.write(output_buffer);

				System.out.print(read + ", ");
			}
			System.out.println("\b\b.");
//			cos.close();
			is.close();
			os.close();
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
	 * @throws HeaderError               Error al leer la cabecera del fichero
	 * @throws IOException               Error en la entrada/salida
	 * @throws NoSuchPaddingException    El algoritmo de relleno no está disponible
	 * @throws NoSuchAlgorithmException  El algoritmo de cifrado está disponible o
	 *                                   no se puede usar para este cifrado
	 * @throws InvalidKeyException       La clave pública usada no cumple con los requisitos para el tipo de cifrado
	 * @throws IllegalBlockSizeException La forma de operar en bloques no es correcta
	 * @throws BadPaddingException       La clave de descifrado no se corresponde con la clave de cifrado
	 */
	@Override
	public void decipher() throws InvalidKeyException, IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
		generateOutputFile(false);
		os = new FileOutputStream(output_file);
		try {
			read_header();

			generate_cypher();

			c.init(Cipher.DECRYPT_MODE, prk);


			byte[] buffer = new byte[64];
			byte[] output_buffer;
			int read;
			while ((read = is.read(buffer)) >= 0) {
				output_buffer = c.doFinal(buffer, 0, read);
				os.write(output_buffer);
				System.out.print(read + ":" + output_buffer.length + ", ");
			}
			System.out.println("\b\b.");
			os.close();
			is.close();

		} catch (Exception ex) {
			os.close();
			output_file.delete();
			throw ex;
		}
	}

	@Override
	protected void generate_cypher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		c = Cipher.getInstance(cypher_type.getAlgorithm());
	}
}
