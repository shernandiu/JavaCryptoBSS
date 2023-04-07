package util;

import byss.Options;
import exceptions.HeaderError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

/**
 * Clase para realizar las tareas de firma de un fichero y verificación de firma.
 * <p>
 * Los ficheros firmados se guardan con extensión <i>.sing</i>
 * y los ficheros tras verificar una firma <i>.orig</i>.
 *
 * @author Santiago Hernández
 */
public class Signer extends HeaderReader {
	public static final String EXTENSION_SIGN = "sign";
	public static final String EXTENSION_RECO = "orig";

	private static final int BUFFER_SIZE = 2048;

	private final File file;
	private File outFile;


	/**
	 * Crea la instancia para firmar un fichero con el algoritmo especificado.
	 *
	 * @param f   Fichero a firmar.
	 * @param alg Algoritmo de firma a usar.
	 */
	public Signer(File f, Algoritmo alg) {
		outFile = new File(f.getAbsolutePath().concat("." + EXTENSION_SIGN));
		cypher_type = alg;
		this.file = f;
		option = Options.OP_SIGNED;
	}

	/**
	 * Crea la instancia para verificar la firma de un fichero.
	 * El algoritmo se determinará automáticamente en base a la cabecera de la firma.
	 *
	 * @param f Fichero a verificar.
	 */
	public Signer(File f) {
		this.file = f;
		option = Options.OP_SIGNED;
	}

	/**
	 * Firma el fichero especificado con la clave privada indicada.
	 * <p>
	 * El fichero resultante se guardará con extensión <i>.sing</i> junto al original
	 * Incluyendo una cabecera con los datos del algoritmo usado y la firma seguido del fichero original.
	 * <p>
	 * Si ocurre un error durante la firma se borra el fichero resultante.
	 *
	 * @param prk Clave privada para firmar el fichero
	 * @throws IOException              Error en la entrada/salida
	 * @throws InvalidKeyException      La clave especificada no puede usarse para el algoritmo
	 * @throws NoSuchAlgorithmException El algoritmo especificado no está disponible
	 * @throws SignatureException       Error al generar la firma.
	 */
	public void sign(PrivateKey prk) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		try {

			is = new FileInputStream(file);

			os = new FileOutputStream(outFile);

			Signature dsa = Signature.getInstance(cypher_type.getAlgorithm());
			dsa.initSign(prk);


			int read;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((read = is.read(buffer)) >= 0) {
				dsa.update(buffer, 0, read);
			}

			data = dsa.sign();  // set sign as data in the header;

			generate_header();

			is = new FileInputStream(file);
			while ((read = is.read(buffer)) >= 0) {
				os.write(buffer, 0, read);
			}

			is.close();
			os.close();
		} catch (Exception e) {
			outFile.delete();
			throw e;
		}

	}

	/**
	 * Verifica si la firma de un fichero es correcta.
	 *
	 * @param puk Clave pública para comprobar la firma.
	 * @return {@code true} si la firma se corresponde con el contenido del fichero.
	 * <p>{@code false} si la firma no se corresponde con el contenido del fichero o la clave
	 * de verificación no se corresponde con la clave de firma.
	 * @throws IOException              Error en la entrada/salida.
	 * @throws HeaderError              Error al leer la cabecera.
	 * @throws NoSuchAlgorithmException No se puede usar el algoritmo que se ha usado para la firma
	 * @throws SignatureException       Error al generar la firma de comprobación.
	 * @throws InvalidKeyException      La clave es inválida.
	 */
	public boolean verify(PublicKey puk) throws IOException, HeaderError, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		is = new FileInputStream(file);

		read_header();

		Signature dsa = Signature.getInstance(cypher_type.getAlgorithm());
		dsa.initVerify(puk);

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while ((read = is.read(buffer)) >= 0) {
			dsa.update(buffer, 0, read);
		}

		is.close();

		return dsa.verify(data);
	}

	/**
	 * Devuelve el fichero donde se ha guardado la firma o el fichero tras verificar.
	 *
	 * @return Fichero resultante de las operaciones.
	 */
	public File getOutputFile() {
		return outFile;
	}

	/**
	 * Genera el fichero con el contenido original de un fichero firmado, independientemente
	 * de que la firma sea correcta o no.
	 * <p>
	 * El fichero se guardará junto al fichero de firma con extensión <i>.orig</i>
	 *
	 * @return Fichero con el contenido original
	 * @throws IOException Error en la entrada/salida
	 * @throws HeaderError Error al leer la cabecera
	 */
	public File getOriginalFile() throws IOException, HeaderError {
		String url = file.getAbsolutePath().replaceFirst(String.format("\\.%s$", EXTENSION_SIGN), "") + "." + EXTENSION_RECO;
		outFile = new File(url);
		is = new FileInputStream(file);
		os = new FileOutputStream(outFile);
		try {
			read_header();
		} catch (NoSuchAlgorithmException ignored) {
		}

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while ((read = is.read(buffer)) >= 0) {
			os.write(buffer, 0, read);
		}
		is.close();
		os.close();
		return outFile;
	}

	/**
	 * Devuelve el tipo de firma del fichero.
	 *
	 * @return Algoritmo usado para firmar.
	 */
	public Algoritmo getSignAlg() {
		return cypher_type;
	}
}
