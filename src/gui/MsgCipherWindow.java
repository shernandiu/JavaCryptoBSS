package gui;

import util.Algoritmo;
import util.Cipher_msg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Ventana de encriptación de texto.
 *
 * @author Santiogo Hernández
 */
public class MsgCipherWindow extends JDialog {
	private JTextArea input_text;
	private JTextArea output_text;
	private JButton encriptarButton;
	private JPanel mainPanel;
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;
	private JRadioButton ASCIIRadioButton;
	private JRadioButton base64RadioButton;
	private JProgressBar progressBar1;

	private byte[] ciphed_text;
	private final Algoritmo alg;

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
	 * Acción del botón de encriptar
	 * Comprueba que existe contraseña y son iguales.
	 * De lo contrario muestra una ventana de alerta.
	 * Cifra el mensaje con {@link Cipher_msg#cipher()}
	 * y gestiona las excepciones mostrando por el log.
	 */
	private final ActionListener cifrar = e -> {
		if (input_text.getText().length() == 0) {
			JOptionPane.showMessageDialog(MsgCipherWindow.this, "No se ha introducido texto a cifrar");
			return;
		}
		if (passwordField1.getPassword().length == 0) {
			JOptionPane.showMessageDialog(MsgCipherWindow.this, "No se ha introducido ninguna contraseña");
			return;
		}
		if (!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
			JOptionPane.showMessageDialog(MsgCipherWindow.this, "Las contraseñas no coinciden");
			return;
		}

		try {
			Cipher_msg cm = new Cipher_msg(alg, passwordField1.getPassword(), new ByteArrayInputStream(input_text.getText().getBytes()));
			cm.cipher();
			ciphed_text = cm.getText();
			// cambiar el texto de salida según base64 o ascii
			output_text.setText(base64RadioButton.isSelected() ? Base64.getEncoder().encodeToString(ciphed_text) : new String(ciphed_text, StandardCharsets.ISO_8859_1));
		} catch (IOException ex) {
			Logger.add_error("Error con la entrada y salida.");
		} catch (NoSuchAlgorithmException ex) {
			Logger.add_error("Error: No se reconoce el algoritmo de cifrado.");
		} catch (InvalidKeyException ex) {
			Logger.add_error("Error: La contraseña contiene caracteres extendidos.");
		} catch (GeneralSecurityException ex) {
			Logger.add_error("Error con el cifrado.");
		}
	};

	/**
	 * Constructor de la ventana
	 *
	 * @param owner Ventana que la crea.
	 * @param alg   Algoritmo a usar para cifrar
	 */
	public MsgCipherWindow(Frame owner, Algoritmo alg) {
		super(owner);
		this.alg = alg;
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		// Agrupar botones radiales
		ButtonGroup bg = new ButtonGroup();
		bg.add(ASCIIRadioButton);
		bg.add(base64RadioButton);

		// Añadir listeners
		encriptarButton.addActionListener(cifrar);

		base64RadioButton.addActionListener(e -> {
			if (ciphed_text != null)
				output_text.setText(Base64.getEncoder().encodeToString(ciphed_text));
		});
		ASCIIRadioButton.addActionListener(e -> {
			if (ciphed_text != null) {
				output_text.setText(new String(ciphed_text, StandardCharsets.ISO_8859_1));
			}
		});
		// Asignar la barra con la fortaleza de la contraseña.
		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				progressBar1.setValue(util.PasswordStrength.check_strength(passwordField1.getPassword()));
			}
		});
		input_text.addComponentListener(cl);
		output_text.addComponentListener(cl);

		// Seleccionar la modalidad
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
}
