package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Ventana para encriptar ficheros
 *
 * @author Santiago Hernández
 */
public class FileEncWindow extends JDialog {
	private JPasswordField passwordField1;
	private JButton okButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private JPasswordField passwordField2;
	private JProgressBar progressBar1;
	private char[] password = null;

	/**
	 * Constructor de la ventana
	 *
	 * @param owner Ventana que la crea.
	 */
	public FileEncWindow(Frame owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();


		// Añadir listeners

		//Cerrar con el botón de cancelar
		cancelarButton.addActionListener(e -> dispose());
		// Asignar la fortaleza de la contraseña a la barra cada vez que se teclea
		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				progressBar1.setValue(util.PasswordStrength.check_strength(passwordField1.getPassword()));
			}
		});
		// Comprobar que se ha introducido una contraseña.
		// Comprobar que las contraseñas coinciden.
		// Mostrar ventana de diálogo.
		okButton.addActionListener(e -> {
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(FileEncWindow.this, "No se ha introducido ninguna contraseña");
				return;
			}
			if (!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
				JOptionPane.showMessageDialog(FileEncWindow.this, "Las contraseñas no coinciden");
				return;
			}
			password = passwordField1.getPassword();
			dispose();
		});

		// Asignar modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new FileEncWindow(null);
	}


	public char[] getPassword() {
		return password;
	}

}
