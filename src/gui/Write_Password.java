package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Write_Password extends JDialog {
	private JPasswordField passwordField1;
	private JButton OKButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private char[] password;

	public Write_Password(Frame owner) {
		super(owner);

		setLocationRelativeTo(owner);

		System.out.println(getOwner());
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		cancelarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		OKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (passwordField1.getPassword().length == 0) {
					JOptionPane.showMessageDialog(Write_Password.this, "No se ha introducido ninguna contraseña");
					return;
				}
				password = passwordField1.getPassword();
				System.out.println("WP: " + password);
				dispose();
			}
		});

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public char[] getPassword() {
		return password;
	}

	public static void main(String[] args) {
		new Write_Password(null);
	}
}
