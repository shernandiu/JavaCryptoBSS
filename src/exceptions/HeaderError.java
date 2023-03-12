package exceptions;

/**
 * Excepción para controlar errores en la lectura del header del cifrado.
 *
 * @author Santiago Hernández
 */
public class HeaderError extends Exception {
	/**
	 * Construye un HeaderError sin mensaje de error.
	 */
	public HeaderError() {
		super();
	}

	/**
	 * Construye un HeaderError con un mensaje describiendo los detalles de la excepción.
	 *
	 * @param msg mensaje con los detalles
	 */
	public HeaderError(String msg) {
		super(msg);
	}
}
