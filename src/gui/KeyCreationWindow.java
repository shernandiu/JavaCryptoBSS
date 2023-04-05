package gui;

import util.Cipher_msg;
import util.Keys;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class KeyCreationWindow extends JDialog {
	private JTextField nombreTextField;
	private JPanel mainPanel;
	private JRadioButton sinEncriptarRadioButton;
	private JRadioButton encriptadaRadioButton;
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;
	private JButton aceptarButton;
	private JButton cancelarButton;
	private JProgressBar progressBar1;
	private JLabel passw;
	private JLabel reppas;
	private JLabel seg;

	private boolean encrypt;
	private Keys generatedKeys = null;

	private final ActionListener aceptar = e -> {
		if (nombreTextField.getText().length() == 0) {
			JOptionPane.showMessageDialog(KeyCreationWindow.this, "No se ha introducido un nombre");
			return;
		}
		if (encrypt) {
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(KeyCreationWindow.this, "No se ha introducido ninguna contraseña");
				return;
			}
			if (!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
				JOptionPane.showMessageDialog(KeyCreationWindow.this, "Las contraseñas no coinciden");
				return;
			}
		}
		try {
			generatedKeys = new Keys(nombreTextField.getText(), encrypt ? passwordField1.getPassword() : null);
			JOptionPane.showMessageDialog(KeyCreationWindow.this, "Clave creada correctamente");
			dispose();
		} catch (FileAlreadyExistsException ex) {
			Logger.add_error("Error: Ya existe una clave con el mismo nombre.");
		} catch (GeneralSecurityException ex) {
			Logger.add_error("Error al cifrar la clave.");
		} catch (IOException ex) {
			Logger.add_error("Error en el fichero de salida.");
		}

	};

	public KeyCreationWindow(Dialog owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		// Agrupar botones radiales
		ButtonGroup bg = new ButtonGroup();
		bg.add(sinEncriptarRadioButton);
		bg.add(encriptadaRadioButton);

		//add here
		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				progressBar1.setValue(util.PasswordStrength.check_strength(passwordField1.getPassword()));
			}
		});

		sinEncriptarRadioButton.addActionListener(e -> setEnabledEncript(false));
		encriptadaRadioButton.addActionListener(e -> setEnabledEncript(true));

		cancelarButton.addActionListener(e -> dispose());
		aceptarButton.addActionListener(aceptar);

		// Seleccionar la modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	private void setEnabledEncript(boolean enabled) {
		passwordField1.setEnabled(enabled);
		passwordField2.setEnabled(enabled);
		progressBar1.setEnabled(enabled);
		passw.setEnabled(enabled);
		reppas.setEnabled(enabled);
		seg.setEnabled(enabled);
		encrypt = enabled;
	}

	public static void main(String[] args) {
		new KeyCreationWindow(null);
	}

	public Keys getGeneratedKeys() {
		return generatedKeys;
	}
}
