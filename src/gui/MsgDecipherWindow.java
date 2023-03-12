package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import util.Cipher_msg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Ventana para cifrar mensajes
 *
 * @author Santiago Hernández
 */
public class MsgDecipherWindow extends JDialog {
	private JPanel mainPanel;
	private JTextArea input_text;
	private JButton desencriptarButton;
	private JPasswordField passwordField1;
	private JTextArea output_text;
	private JRadioButton base64RadioButton;
	private JRadioButton ASCIIRadioButton;

	/**
	 * Acción para redimensionar la ventana cada vez que las entradas de texto varían de tamaño.
	 */
	private final ComponentListener cl = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			resize_window();
		}
	};

	/**
	 * Acción para descifrar el mensaje.
	 * <p>Lee el mensaje y la contraseña en sus campos.
	 * <p>Retorna una alerta si no existe contraseña o mensaje.
	 * <p>Crea la clase {@link Cipher_msg} y descifra el mensaje.
	 * <p>Muestra pon el log la gestión de excepciones.
	 */
	private final ActionListener descifrar = e -> {
		// Comprobar si existe mensaje y contraseña
		if (passwordField1.getPassword().length == 0) {
			JOptionPane.showMessageDialog(MsgDecipherWindow.this, "No se ha introducido ninguna contraseña.");
			return;
		}
		if (input_text.getText().length() == 0) {
			JOptionPane.showMessageDialog(MsgDecipherWindow.this, "No se ha introducido ningún mensaje.");
			return;
		}

		// Convierte la entrada en un array de bytes según sea base64 o ASCII
		byte[] input_msg;
		if (base64RadioButton.isSelected()) {
			input_msg = Base64.getDecoder().decode(input_text.getText().getBytes());    // Base64
		} else
			input_msg = input_text.getText().getBytes(StandardCharsets.ISO_8859_1);     // ASCII -> ISO 8859

		try {
			Cipher_msg cm = new Cipher_msg(passwordField1.getPassword(), new ByteArrayInputStream(input_msg));
			cm.decipher();

			// Descifrado correcto
			Logger.add_text("Mensaje desencriptado con " + cm.getCypher_type());
			output_text.setText(new String(cm.getText()));
		}
		// Manejo de excepciones
		catch (IllegalArgumentException ex) {
			Logger.add_error("Error: el texto no se corresponde con base64.");
		} catch (IOException ex) {
			Logger.add_error("Error con la entrada y salida.");
		} catch (PasswError ex) {
			Logger.add_error("Error: Contraseña incorrecta, no puede descifrarse el mensaje.");
		} catch (InvalidKeyException ex) {
			Logger.add_error("Error: La contraseña contiene caracteres extendidos.");
		} catch (HeaderError ex) {
			Logger.add_error("Error: La cabecera del mensaje no es correcta.");
		} catch (NoSuchAlgorithmException ex) {
			Logger.add_error("Error: No se reconoce el algoritmo de encriptado.");
		} catch (GeneralSecurityException ex) {
			Logger.add_error("Error: En el desencriptado.");
		}
	};

	/**
	 * Constructor de la ventana
	 *
	 * @param owner Ventana que la crea.
	 */
	public MsgDecipherWindow(Frame owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		// Agrupar los botones radiales
		ButtonGroup bg = new ButtonGroup();
		bg.add(base64RadioButton);
		bg.add(ASCIIRadioButton);

		// Agregar los action listeners
		desencriptarButton.addActionListener(descifrar);
		input_text.addComponentListener(cl);
		output_text.addComponentListener(cl);

		// Seleccionar la modalidad y mostrar
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	/**
	 * Método para agrandar la ventana automáticamente.
	 */
	private void resize_window() {
		revalidate();
		pack();
		repaint();
	}

	// Pruebas. No usar
	public static void main(String[] args) {
		new MsgDecipherWindow(null);
	}
}
