package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.regex.Pattern;

public class password_screen extends JDialog {
	private static final double LOWER_POINTS = 10;
	private static final double UPPER_POINTS = 15;
	private static final double NUMBER_POINTS = 15;
	private static final double PUNCT_POINTS = 20;

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

//		passwordField1.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//				super.keyTyped(e);
//				check_strength();
//			}
//		});
		passwordField1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				check_strength();
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

	private void check_strength() {
		String password = new String(passwordField1.getPassword());
		int size = passwordField1.getPassword().length;


		Pattern upper = Pattern.compile("\\p{Upper}");
		Pattern lower = Pattern.compile("\\p{Lower}");
		Pattern number = Pattern.compile("\\p{Digit}");
		Pattern punct = Pattern.compile("\\p{Punct}");

		int number_upper = (int) upper.matcher(password).results().count();
		int number_lower = (int) lower.matcher(password).results().count();
		int number_number = (int) number.matcher(password).results().count();
		int number_punct = (int) punct.matcher(password).results().count();

		int strength = (int) (number_lower * LOWER_POINTS / 10 + (number_lower > 0 ? LOWER_POINTS : 0) +
				number_upper * UPPER_POINTS / 10 + (number_upper > 0 ? UPPER_POINTS : 0) +
				number_number * NUMBER_POINTS / 10 + (number_number > 0 ? NUMBER_POINTS : 0) +
				number_punct * PUNCT_POINTS / 10 + (number_punct > 0 ? PUNCT_POINTS : 0));

		progressBar1.setValue(strength);

		System.out.println();
		System.out.println("Password: " + password);
		System.out.println("Lower: " + number_lower);
		System.out.println("Upper: " + number_upper);
		System.out.println("Numbers: " + number_number);
		System.out.println("Punct: " + number_punct);
	}

	public char[] getPassword() {
		return password;
	}

}
