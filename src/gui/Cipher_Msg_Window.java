package gui;

import util.Algoritmo;
import util.Cipher_msg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Cipher_Msg_Window extends JDialog {
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

	public Cipher_Msg_Window(Frame f, Algoritmo algoritmo) {
		super(f);
		setLocationRelativeTo(f);

		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();


		ButtonGroup bg = new ButtonGroup();
		bg.add(ASCIIRadioButton);
		bg.add(base64RadioButton);

		encriptarButton.addActionListener(e -> {
			if (input_text.getText().length() == 0) {
				JOptionPane.showMessageDialog(Cipher_Msg_Window.this, "No se ha introducido texto a cifrar");
				return;
			}
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(Cipher_Msg_Window.this, "No se ha introducido ninguna contraseña");
				return;
			}
			if (!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
				JOptionPane.showMessageDialog(Cipher_Msg_Window.this, "Las contraseñas no coinciden");
				return;
			}

			try {
				Cipher_msg cm = new Cipher_msg(algoritmo, passwordField1.getPassword(), new ByteArrayInputStream(input_text.getText().getBytes()));
				cm.cipher();
				ciphed_text = cm.getText();
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
		});

		base64RadioButton.addActionListener(e -> {
			if (ciphed_text != null)
				output_text.setText(Base64.getEncoder().encodeToString(ciphed_text));
		});
		ASCIIRadioButton.addActionListener(e -> {
			if (ciphed_text != null) {
				output_text.setText(new String(ciphed_text, StandardCharsets.ISO_8859_1));
			}
		});
		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				progressBar1.setValue(util.PasswordStrength.check_strength(passwordField1.getPassword()));
			}
		});


		input_text.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				resize_window();
			}
		});
		output_text.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				super.componentResized(e);
				resize_window();
			}
		});

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

//	public static void main(String[] args) {
//		new Cipher_Msg_Window(null, Algoritmo.PBEDM53DES);
//	}

	private void resize_window() {
		revalidate();
		pack();
		repaint();
	}
}
