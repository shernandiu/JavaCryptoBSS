package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import util.Algoritmo;
import util.Cipher_File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class gui extends JFrame {
	private final ActionListener decipher_msg_button = e -> new Decipher_Msg_Window(gui.this);
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
	private final ActionListener open_file = e -> {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new FileNameExtensionFilter(String.format("Ficheros encriptados (.%s)", Cipher_File.ENCRYPTED_EXTENSION), Cipher_File.ENCRYPTED_EXTENSION));
		int returnVal = jfc.showOpenDialog(jfc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file_route.setText(jfc.getSelectedFile().getPath());
			file = jfc.getSelectedFile();
			Logger.add_text("Fichero seleccionado: " + jfc.getSelectedFile());
		} else if (file == null)
			Logger.add_error("No se ha seleccionado archivo");
	};
	private final ActionListener file_route_area = e -> {
		file = new File(file_route.getText());
		if (!file.isFile()) {
			Logger.add_error("Error: " + file_route.getText() + " no es un fichero.");
			file = null;
		} else {
			Logger.add_text("Fichero seleccionado: " + file_route.getText());
		}
	};
	private Algoritmo algoritmo = Algoritmo.getListOfAlgorithms()[0];
	private final ActionListener cipher_selection = e -> {
		algoritmo = (util.Algoritmo) comboBox1.getSelectedItem();
		Logger.add_text("Algoritmo seleccionado: " + algoritmo);
	};
	private final ActionListener cipher_msg_button = e -> new Cipher_Msg_Window(gui.this, algoritmo);
	private char[] password = null;
	private final ActionListener cipher_button = e -> {
		if (file == null) {
			Logger.add_error("Error, no se ha seleccionado archivo.");
			return;
		}

		password_screen ps = new password_screen(this);

		if (ps.getPassword() != null) {
			password = ps.getPassword();
			Cipher_File c;
			try {
				c = new Cipher_File(file, algoritmo, password);
				c.cipher();
				Logger.add_text("Fichero encriptado en: " + c.getOutput_file());
			} catch (FileNotFoundException ex) {
				Logger.add_error("Error: no se puede acceder al fichero o ha sido eliminado.");
			} catch (IOException ex) {
				Logger.add_error("Error al cifrar archivo.");
			} catch (NoSuchAlgorithmException ex) {
				Logger.add_error("Error: El algoritmo no se reconoce.");
			} catch (GeneralSecurityException ex) {
				Logger.add_error("Error con el cifrado.");
			}

			file_route.setText("Fichero: ");
			file = null;
		}
	};
	private final ActionListener decipher_button = e -> {
		if (file == null) {
			Logger.add_error("Error, no se ha seleccionado archivo.");
			return;
		}
		Write_Password ps = new Write_Password(gui.this);

		if (ps.getPassword() != null) {
			try {
				password = ps.getPassword();
				Cipher_File c = new Cipher_File(file, password);
				c.decipher();
				Logger.add_text("Desencriptado con " + c.getCypher_type());
				Logger.add_text("Fichero descifrado en: " + c.getOutput_file());
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				Logger.add_error("Error: Fichero no encontrado.");
			} catch (HeaderError ex) {
				ex.printStackTrace();
				Logger.add_error("Error: No se puede leer la cabecera.");
			} catch (PasswError ex) {
				ex.printStackTrace();
				Logger.add_error("Error: Contraseña incorrecta, no puede descifrarse el fichero.");
			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.add_error("Error con la entrada/salida.");
			} catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();
				Logger.add_error("Error: No se reconoce el algoritmo de cifrado.");
			} catch (GeneralSecurityException ex) {
				ex.printStackTrace();
				Logger.add_error("Error al descifrar el fichero.");
			}


			file_route.setText("Fichero: ");
			file = null;
		}
	};

	public gui() {
		Logger.setLog(Log);

		setMinimumSize(new Dimension(600, -1));
		setTitle("Cifrador");
		setContentPane(panel1);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();

		setVisible(true);

		for (Algoritmo a : Algoritmo.getListOfAlgorithms())
			comboBox1.addItem(a);


		abrirButton.addActionListener(open_file);
		cifrarButton.addActionListener(cipher_button);
		file_route.addActionListener(file_route_area);
		file_route.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (file_route.getText().equals("Fichero:")) file_route.setText("");
			}
		});
		comboBox1.addActionListener(cipher_selection);
		descifrarButton.addActionListener(decipher_button);
		cifrarMensajeButton.addActionListener(cipher_msg_button);
		descifrarMensajeButton.addActionListener(decipher_msg_button);
	}

	public static void main(String[] args) {
		new gui();
	}
}
