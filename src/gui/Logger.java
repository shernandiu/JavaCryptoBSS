package gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Clase para volcar los logs de salida al panel de log de la ventana principal
 * para mostrar información al usuario.
 * Puede mostrar mensajes en negro y mensajes de error en rojo.
 *
 * @author Santiago Hernández
 */
class Logger {
	private static final AttributeSet error_AS = new SimpleAttributeSet();  // Atributos de los errores
	private static final AttributeSet text_AS = new SimpleAttributeSet();   // Atributos del texto normal
	private static JTextPane log = null;
	private static Document doc = null;

	static {
		StyleConstants.setForeground((MutableAttributeSet) text_AS, Color.BLACK);
		StyleConstants.setForeground((MutableAttributeSet) error_AS, Color.RED);
	}

	/**
	 * Inicia el log indicando el panel donde se va a volcar la información de salida.
	 *
	 * @param log JTextPane donde volcar la información
	 */
	public static void setLog(JTextPane log) {
		Logger.log = log;
		Logger.doc = log.getStyledDocument();
	}

	/**
	 * Añade un mensaje de información al log.
	 *
	 * @param str String con el mensaje
	 */
	public static void add_text(String str) {
		try {
			doc.insertString(doc.getLength(), str + "\n", text_AS);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Añade un mensaje de error al log.
	 *
	 * @param str String con el error
	 */
	public static void add_error(String str) {
		try {
			doc.insertString(doc.getLength(), str + "\n", error_AS);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Limpia el contenido del log.
	 */
	public static void clear() {
		log.setText("");
	}

}
