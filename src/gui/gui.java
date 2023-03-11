package gui;

import util.Algoritmo;
import util.Cipher_File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class gui extends JFrame {
	private JPanel panel1;
	private JComboBox<Algoritmo> comboBox1;
	private JButton cifrarButton;
	private JButton cifrarMensajeButton;
	private JButton descifrarButton;
	private JButton descifrarMensajeButton;
	private JTextField file_route;
	private JButton abrirButton;
	private JTextPane Log;

	private File file = null;
	private Algoritmo algoritmo = Algoritmo.getListOfAlgorithms()[0];
	private char[] password = null;

	public gui() {
		new Logger(Log);

		setMinimumSize(new Dimension(600, -1));
		setTitle("Cifrador");
		setContentPane(panel1);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();

		setVisible(true);

		for (Algoritmo a : Algoritmo.getListOfAlgorithms())
			comboBox1.addItem(a);


		file_route.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);


			}
		});
		abrirButton.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			jfc.addChoosableFileFilter(new FileNameExtensionFilter(String.format("Ficheros encriptados (.%s)", Cipher_File.ENCRYPTED_EXTENSION), Cipher_File.ENCRYPTED_EXTENSION));
			int returnVal = jfc.showOpenDialog(jfc);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file_route.setText(jfc.getSelectedFile().getPath());
				System.out.println(jfc.getSelectedFile());
				file = jfc.getSelectedFile();
			} else
				Logger.add_text("No se ha seleccionado archivo");
		});
		cifrarButton.addActionListener(e -> {
			if (file == null) {
				Logger.add_error("Error, no se ha seleccionado archivo");
				return;
			}
			Logger.add_text("Seleccionado: " + comboBox1.getSelectedItem() + "\n Fichero: " + file_route.getText());

			password_screen ps = new password_screen(this);

			if (ps.getPassword() != null) {
				password = ps.getPassword();
				Cipher_File c = null;
				try {
					c = new Cipher_File(file, algoritmo, password);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					Logger.add_error("Error, fichero eliminado");
				}
				try {
					c.cipher();
				} catch (IOException ex) {
					ex.printStackTrace();
					Logger.add_error("Error al cifrar archivo");
				}

				file_route.setText("Fichero: ");
				file = null;
			}

		});
		file_route.addActionListener(e -> {
			file = new File(file_route.getText());
			if (!file.isFile()) {
				Logger.add_error("Error: " + file_route.getText() + " no es un fichero.");
				file = null;
			} else {
				Logger.add_error("Seleccionado: " + file_route.getText());
			}
		});
		file_route.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (file_route.getText().equals("Fichero:")) file_route.setText("");
			}
		});
		comboBox1.addActionListener(e -> {
			algoritmo = (util.Algoritmo) comboBox1.getSelectedItem();
			Logger.add_error("util.Algoritmo seleccionado: " + algoritmo);
		});
		descifrarButton.addActionListener(e -> {
			if (file == null) {
				Logger.add_error("Error, no se ha seleccionado archivo");
				return;
			}
			Logger.add_error("Seleccionado: " + comboBox1.getSelectedItem() + "\n Fichero: " + file_route.getText());

			Write_Password ps = new Write_Password(gui.this);
			System.out.println(ps.getPassword());


			if (ps.getPassword() != null) {
				password = ps.getPassword();
				Cipher_File c;
				try {
					c = new Cipher_File(file, algoritmo, password);
					c.decipher();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					Logger.add_error("Error fichero no encontrado");
				} catch (Exception ex) {
					ex.printStackTrace();
					Logger.add_error("Error al descifrar el fichero");
				}

				file_route.setText("Fichero: ");
				file = null;
			}


		});
		cifrarMensajeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Cipher_Msg_Window(gui.this, algoritmo);
			}
		});

		descifrarMensajeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Decipher_Msg_Window(gui.this);
			}
		});
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new gui();

	}


}
