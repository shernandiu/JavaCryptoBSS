package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class password_screen extends JDialog {
	private JPasswordField passwordField1;
	private JButton okButton;
	private JButton cancelarButton;
	private JPanel mainPanel;
	private JPasswordField passwordField2;
	private JProgressBar progressBar1;
	private char[] password = null;

	public password_screen(Frame owner) {
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		cancelarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});


		pack();

		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				progressBar1.setValue(util.PasswordStrength.check_strength(passwordField1.getPassword()));
			}
		});
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (passwordField1.getPassword().length == 0) {
					JOptionPane.showMessageDialog(password_screen.this, "No se ha introducido ninguna contraseña");
					return;
				}
				if (!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
					JOptionPane.showMessageDialog(password_screen.this, "Las contraseñas no coinciden");
					return;
				}
				password = passwordField1.getPassword();
				dispose();
			}
		});
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new password_screen(null);
	}


	public char[] getPassword() {
		return password;
	}

}
