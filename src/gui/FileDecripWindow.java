package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana para escribir la contraseña para encriptar un fichero.
 *
 * @author Santiago Hernández
 */
public class FileDecripWindow extends JDialog {
	private JPasswordField passwordField1;
	private JButton OKButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private char[] password;

	/**
	 * Constructor de la ventana
	 *
	 * @param owner Ventana que la crea.
	 */
	public FileDecripWindow(Frame owner) {
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
				JOptionPane.showMessageDialog(FileDecripWindow.this, "No se ha introducido ninguna contraseña");
				return;
			}
			password = passwordField1.getPassword();
			dispose();
		});

		// Asignar modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	/**
	 * Devuelve la contraseña escrita o null si se ha cerrado la ventana.
	 *
	 * @return Contraseña escrita.
	 */
	public char[] getPassword() {
		return password;
	}

	// Método main pruebas
	public static void main(String[] args) {
		new FileDecripWindow(null);
	}
}
