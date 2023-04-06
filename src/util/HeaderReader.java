package util;

import byss.Header;
import exceptions.HeaderError;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public abstract class HeaderReader {
	protected byte option;
	protected Algoritmo cypher_type;
	protected byte[] data;
	protected OutputStream os;
	protected InputStream is;


	/**
	 * Genera la cabecera del mensaje con la información necesaria para poder obtener posteriormente los parámetros
	 * necesarios para desencriptar.
	 */
	void generate_header() {
		try {
			Header header = new Header(option, cypher_type.getAlgorithm(), null, data);
			header.save(os);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Lee los parámetros de la cabecera del mensaje necesarios para configurar los métodos para desencriptar y los guarda
	 * como atributos del objeto {@link Cipher_msg}.
	 *
	 * @throws HeaderError              No se puede leer la cabecera o esta no es correcta.
	 * @throws NoSuchAlgorithmException El algoritmo especificado en la cabecera no se puede usar para desencriptar.
	 */
	protected void read_header() throws HeaderError, NoSuchAlgorithmException {
		try {
			Header header = new Header();
			if (!header.load(is))
				throw new HeaderError("Cant read header");
			data = header.getData();
			cypher_type = Algoritmo.get(header.getAlgorithm1());
		} catch (NoSuchAlgorithmException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new HeaderError(ex.getMessage());
		}
	}
}
