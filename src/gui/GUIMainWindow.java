package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import gui.util.SeparatorComboBox;
import util.Algoritmo;
import util.Cipher_File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * Ventana principal del programa.
 *
 * @author Santiago Hernández
 */
public class GUIMainWindow extends JFrame {
	private final ActionListener decipher_msg_button = e -> new MsgDecipherWindow(GUIMainWindow.this);
	private JPanel panel1;
	private JComboBox<Algoritmo> comboBox1;
	private JButton cifrarButton;
	private JButton cifrarMensajeButton;
	private JButton descifrarButton;
	private JButton descifrarMensajeButton;
	private JTextField file_route;
	private JButton abrirButton;
	private JTextPane Log;

	// Fichero seleccionado
	private File file = null;

	// Algoritmo seleccionado
	// Por defecto el primero de la lista
	private Algoritmo algoritmo = Algoritmo.getListOfAlgorithms()[0];

	// Contraseña especificada para los cifrados de fichero
	private char[] password = null;

	/**
	 * Acción al pulsar el botón de abrir fichero.
	 * <p>
	 * Muestra una ventana de selección de fichero que permite filtrar por todos los ficheros o <i>.cif</i>.
	 * <p>
	 * Informa al usuario del fichero seleccionado o error en el caso de no seleccionar ninguno.
	 */
	private final ActionListener open_file = e -> {
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new FileNameExtensionFilter(String.format("Ficheros encriptados (.%s)", Cipher_File.ENCRYPTED_EXTENSION), Cipher_File.ENCRYPTED_EXTENSION));
		int returnVal = jfc.showOpenDialog(jfc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file_route.setText(jfc.getSelectedFile().getPath());
			file = jfc.getSelectedFile();
			Logger.add_text("Fichero seleccionado: " + jfc.getSelectedFile().getAbsolutePath());
		} else if (file == null)
			Logger.add_error("No se ha seleccionado archivo");
	};
	/**
	 * Acción al escribir sobre el cuadro con la ruta del fichero.
	 * <p>
	 * Informa al usuario si existe un fichero en la ruta especificada.
	 */
	private final ActionListener file_route_area = e -> {
		file = new File(file_route.getText());
		if (!file.isFile()) {
			Logger.add_error("Error: " + file_route.getText() + " no es un fichero.");
			file = null;
		} else {
			Logger.add_text("Fichero seleccionado: " + file.getAbsolutePath());
		}
	};
	/**
	 * Acción al seleccionar un algoritmo de la lista.
	 * <p>
	 * Informa del algoritmo escogido.
	 */
	private final ActionListener cipher_selection = e -> {
		algoritmo = (util.Algoritmo) comboBox1.getSelectedItem();
		Logger.add_text("Algoritmo seleccionado: " + algoritmo);
	};
	/**
	 * Acción al pulsar el botón de encriptar mensaje.
	 * <p>
	 * Mostrar una {@link MsgCipherWindow}
	 */
	private final ActionListener cipher_msg_button = e -> new MsgCipherWindow(GUIMainWindow.this, algoritmo);
	/**
	 * Acción al pulsar el botón de encriptar fichero
	 * <p>
	 * Comprueba si se ha escogido un fichero y lanza una {@link FileEncWindow}
	 */
	private final ActionListener cipher_button = e -> {
		if (file == null) {
			Logger.add_error("Error, no se ha seleccionado archivo.");
			return;
		}

		FileEncWindow ps = new FileEncWindow(this);

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
	/**
	 * Acción al pulsar el botón de encriptar fichero
	 * <p>
	 * Comprueba si se ha escogido un fichero y lanza una {@link FileDecripWindow}
	 */
	private final ActionListener decipher_button = e -> {
		if (file == null) {
			Logger.add_error("Error, no se ha seleccionado archivo.");
			return;
		}
		FileDecripWindow ps = new FileDecripWindow(GUIMainWindow.this);

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
	/**
	 * Acción al seleccionar y deseleccionar el cuadro con la ruta del fichero.
	 * <p>
	 * Insertar <i>Fichero:</i> si no se inserta nada o borrarlo al perder el focus.
	 * <p>
	 * Mostrar error si al escribir no se corresponde con la ruta de un fichero.
	 */
	private final FocusListener focus_path = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			super.focusGained(e);
			if (file_route.getText().equals("Fichero:")) file_route.setText("");
		}

		@Override
		public void focusLost(FocusEvent e) {
			super.focusLost(e);
			if (file_route.getText().equals("")) {
				file_route.setText("Fichero:");
				return;
			}
			file = new File(file_route.getText());
			if (!file.isFile()) {
				Logger.add_error("Error: " + file_route.getText() + " no es un fichero.");
				file = null;
			} else {
				Logger.add_text("Fichero seleccionado: " + file.getAbsolutePath());
			}
		}
	};

	/**
	 * Constructor de la ventana
	 */
	public GUIMainWindow() {
		ListCellRenderer<Algoritmo> lcr = new SeparatorComboBox<Algoritmo>();
		setMinimumSize(new Dimension(600, -1));
		setTitle("Cifrador");
		setContentPane(panel1);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();

		setVisible(true);

		// Inicializar el log
		Logger.setLog(Log);

		// Insertar los algoritmos disponibles en el desplegable.
		comboBox1.setRenderer(lcr);
		for (Algoritmo a : Algoritmo.list_PBE_alg)
			comboBox1.addItem(a);
		comboBox1.addItem(null);
		for (Algoritmo a : Algoritmo.list_PKEY_alg)
			comboBox1.addItem(a);

		// Añadir los listeners a los botones
		abrirButton.addActionListener(open_file);
		cifrarButton.addActionListener(cipher_button);
		file_route.addActionListener(file_route_area);
		comboBox1.addActionListener(cipher_selection);
		descifrarButton.addActionListener(decipher_button);
		cifrarMensajeButton.addActionListener(cipher_msg_button);
		descifrarMensajeButton.addActionListener(decipher_msg_button);
		file_route.addFocusListener(focus_path);
	}

	// Método main para pruebas.
	public static void main(String[] args) {
		new GUIMainWindow();
	}
}
