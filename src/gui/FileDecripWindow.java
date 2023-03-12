package gui;

import javax.swing.*;
import java.awt.*;

public class FileDecripWindow extends JDialog {
	private JPasswordField passwordField1;
	private JButton OKButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private char[] password;

	public FileDecripWindow(Frame owner) {
		super(owner);

		setLocationRelativeTo(owner);

		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		cancelarButton.addActionListener(e -> dispose());
		OKButton.addActionListener(e -> {
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(FileDecripWindow.this, "No se ha introducido ninguna contraseña");
				return;
			}
			password = passwordField1.getPassword();
			dispose();
		});

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new FileDecripWindow(null);
	}

	public char[] getPassword() {
		return password;
	}
}
