package exceptions;

/**
 * Excepción para controlar errores en el proceso de descifrado por contraseña errónea.
 *
 * @author Santiago Hernández
 */
public class PasswError extends Exception {
	/**
	 * Construye un PasswError sin mensaje de error.
	 */
	public PasswError() {
		super();
	}

	/**
	 * Construye un PasswError con un mensaje describiendo los detalles de la excepción.
	 *
	 * @param msg mensaje con los detalles
	 */
	public PasswError(String msg) {
		super(msg);
	}
}
