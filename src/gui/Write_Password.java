package gui;

import javax.swing.*;
import java.awt.*;

public class Write_Password extends JDialog {
	private JPasswordField passwordField1;
	private JButton OKButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private char[] password;

	public Write_Password(Frame owner) {
		super(owner);

		setLocationRelativeTo(owner);

		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		cancelarButton.addActionListener(e -> dispose());
		OKButton.addActionListener(e -> {
			if (passwordField1.getPassword().length == 0) {
				JOptionPane.showMessageDialog(Write_Password.this, "No se ha introducido ninguna contraseña");
				return;
			}
			password = passwordField1.getPassword();
			dispose();
		});

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Write_Password(null);
	}

	public char[] getPassword() {
		return password;
	}
}
