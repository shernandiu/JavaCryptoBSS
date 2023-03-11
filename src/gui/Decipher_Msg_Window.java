package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import util.Cipher_msg;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import util.Algoritmo;

public class Decipher_Msg_Window extends JDialog {
	private JPanel mainPanel;
	private JTextArea input_text;
	private JButton desencriptarButton;
	private JPasswordField passwordField1;
	private JTextArea output_text;
	private JRadioButton base64RadioButton;
	private JRadioButton ASCIIRadioButton;


	ComponentListener cl = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			resize_window();
		}
	};

	public Decipher_Msg_Window(Frame f) {
		super(f);
		setLocationRelativeTo(f);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		ButtonGroup bg = new ButtonGroup();
		bg.add(base64RadioButton);
		bg.add(ASCIIRadioButton);

		desencriptarButton.addActionListener(e -> {
			try {
				if (passwordField1.getPassword().length == 0) {
					JOptionPane.showMessageDialog(Decipher_Msg_Window.this, "No se ha introducido ninguna contraseña.");
					return;
				}

				byte[] input_msg;
				if (base64RadioButton.isSelected()) {
					input_msg = Base64.getDecoder().decode(input_text.getText().getBytes());
				} else
					input_msg = input_text.getText().getBytes(StandardCharsets.ISO_8859_1);

				Cipher_msg cm = new Cipher_msg(passwordField1.getPassword(), new ByteArrayInputStream(input_msg));
				cm.decipher();
				Logger.add_text("Mensaje desencriptado con " + cm.getCypher_type());
				output_text.setText(new String(cm.getText()));

				/*
				Exception handling
				*/
			} catch (IllegalArgumentException ex) {
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
		});
		input_text.addComponentListener(cl);
		output_text.addComponentListener(cl);


		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	private void resize_window() {
		revalidate();
		pack();
		repaint();
	}


	public static void main(String[] args) {
		new Decipher_Msg_Window(null);
	}
}
