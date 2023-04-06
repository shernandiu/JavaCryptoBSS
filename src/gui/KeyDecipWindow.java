package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import util.Keys;
import util.KeysStore;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class KeyDecipWindow extends JDialog {
	private JPasswordField passwordField1;
	private JButton cancelarButton;
	private JButton OKButton;
	private JPanel mainPanel;

	private char[] password;
	private Keys keys;

	public KeyDecipWindow(Dialog owner, Keys k) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		// Añadir listeners

		// Cerrar al pulsar cancelar
		cancelarButton.addActionListener(e -> dispose());
		// Comprobar que se ha introducido contraseña.
		// Mostrar ventana de diálogo.
		OKButton.addActionListener(e -> {
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(KeyDecipWindow.this, "No se ha introducido ninguna contraseña");
				return;
			}
			try {
				if (k.decipherKey(passwordField1.getPassword())) {
					JOptionPane.showMessageDialog(KeyDecipWindow.this, "Clave desencriptada correctamente");
					dispose();
				}
			} catch (IllegalArgumentException ex) {
				Logger.add_error("Error: el fichero con la clave es incorrecto.");
			} catch (IOException ex) {
				Logger.add_error("Error con la entrada y salida.");
			} catch (PasswError ex) {
				Logger.add_error("Error: Contraseña incorrecta, no puede descifrarse el mensaje.");
				JOptionPane.showMessageDialog(KeyDecipWindow.this, "Contraseña incorrecta,");
			} catch (InvalidKeyException ex) {
				Logger.add_error("Error: La contraseña contiene caracteres extendidos.");
			} catch (HeaderError ex) {
				Logger.add_error("Error: La cabecera de la clave no es correcta.");
			} catch (NoSuchAlgorithmException ex) {
				Logger.add_error("Error: No se reconoce el algoritmo de encriptado.");
			} catch (GeneralSecurityException ex) {
				Logger.add_error("Error: En el desencriptado.");
			}
		});

		// Asignar modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	// Método main pruebas
	public static void main(String[] args) throws FileAlreadyExistsException {
		new KeyDecipWindow(null, KeysStore.getListOfKeys().get(0));
	}
}
