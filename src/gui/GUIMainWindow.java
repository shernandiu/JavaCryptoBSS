package gui;

import exceptions.HeaderError;
import exceptions.PasswError;
import gui.util.SeparatorComboBox;
import org.junit.platform.commons.function.Try;
import util.*;

import javax.crypto.BadPaddingException;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

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
	private Algoritmo algoritmo;

	// Contraseña especificada para los cifrados de fichero
	private char[] password = null;

	// Forma de descifrar el fichero (PBE, C publica, Firma)
	private int decipType;

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
		jfc.addChoosableFileFilter(new FileNameExtensionFilter(String.format("Ficheros firmados (.%s)", Signer.EXTENSION_SIGN), Signer.EXTENSION_SIGN));
		int returnVal = jfc.showOpenDialog(jfc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file_route.setText(jfc.getSelectedFile().getPath());
			file = jfc.getSelectedFile();
			Logger.add_text("Fichero seleccionado: " + jfc.getSelectedFile().getAbsolutePath());
			checkFile();
		} else if (file == null) {
			descifrarButton.setEnabled(false);
			Logger.add_error("No se ha seleccionado archivo");
		}
	};

	void checkFile() {
		try {
			switch (decipType = AlgType.getType(file)) {
				case Algoritmo.PBE, Algoritmo.PKEY -> descifrarButton.setText("Descifrar");
				case Algoritmo.SIGN -> descifrarButton.setText("Comprobar Firma");
				case -1 -> descifrarButton.setText("Descifrar/Cmp Firma");
			}
		} catch (Exception ex) {
			Logger.add_error("Error con el fichero seleccionado");
		}
		descifrarButton.setEnabled(true);
	}

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
			descifrarButton.setEnabled(false);
		} else {
			Logger.add_text("Fichero seleccionado: " + file.getAbsolutePath());
			checkFile();
		}
	};
	/**
	 * Acción al seleccionar un algoritmo de la lista.
	 * <p>
	 * Informa del algoritmo escogido.
	 */
	private final ActionListener cipher_selection = e -> {
		algoritmo = (util.Algoritmo) comboBox1.getSelectedItem();
		assert algoritmo != null;
		cifrarMensajeButton.setEnabled(algoritmo.getType() == Algoritmo.PBE);
		if (algoritmo.getType() == Algoritmo.SIGN)
			cifrarButton.setText("Firmar");
		else
			cifrarButton.setText("Cifrar");
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
		switch (algoritmo.getType()) {
			case Algoritmo.PBE -> cipherPBE();
			case Algoritmo.PKEY -> cipherPKEY();
			case Algoritmo.SIGN -> cipherSIGN();
		}
		file_route.setText("Fichero: ");
		file = null;
		descifrarButton.setEnabled(false);
	};

	private void cipherPBE() {
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
		}
	}

	private void cipherPKEY() {
		KeysWindow ps = new KeysWindow(this, false);

		Keys key = ps.getSelectedKey();

		if (key == null) return;

		ASym_Cipher_File c;
		try {
			c = new ASym_Cipher_File(file, algoritmo, key.getPuk());
			c.cipher();
			Logger.add_text("Fichero encriptado con la clave publica: " + key);
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
	}

	private void cipherSIGN() {
		KeysWindow ps = new KeysWindow(this, true);

		Keys key = ps.getSelectedKey();

		if (key == null) return;

		Signer s;

		try {
			s = new Signer(file, algoritmo);
			s.sign(key.getPrk());
			Logger.add_text("Fichero firmado con la clave: " + key);
			Logger.add_text("Fichero firmado en: " + s.getOutputFile());
		} catch (NoSuchAlgorithmException ex) {
			Logger.add_error("No se puede firmar con ese algoritmo.", ex);
		} catch (SignatureException ex) {
			Logger.add_error("Error en la firma.", ex);
		} catch (InvalidKeyException ex) {
			Logger.add_error("La clave privada no es válida.", ex);
		} catch (IOException ex) {
			Logger.add_error("Error al leer o escribir el fichero.", ex);
		}
	}

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
		switch (decipType) {
			case Algoritmo.PBE -> decipPBE();
			case Algoritmo.PKEY -> decipPKEY();
			case Algoritmo.SIGN -> decipSIGN();
			default -> Logger.add_error("No se reconoce ningún tipo de cifrado o firma para el fichero seleccionado.");
		}
		file_route.setText("Fichero: ");
		file = null;
		descifrarButton.setEnabled(false);
	};

	private void decipPBE() {
		FileDecripWindow ps = new FileDecripWindow(GUIMainWindow.this);

		if (ps.getPassword() != null) {
			try {
				password = ps.getPassword();
				Cipher_File c = new Cipher_File(file, password);
				c.decipher();
				Logger.add_text("Desencriptado con " + c.getCypher_type());
				Logger.add_text("Fichero descifrado en: " + c.getOutput_file());
			} catch (FileNotFoundException ex) {
				Logger.add_error("Error: Fichero no encontrado.", ex);
			} catch (HeaderError ex) {
				Logger.add_error("Error: No se puede leer la cabecera.", ex);
			} catch (PasswError ex) {
				Logger.add_error("Error: Contraseña incorrecta, no puede descifrarse el fichero.", ex);
			} catch (IOException ex) {
				Logger.add_error("Error con la entrada/salida.", ex);
			} catch (NoSuchAlgorithmException ex) {
				Logger.add_error("Error: No se reconoce el algoritmo de cifrado.", ex);
			} catch (GeneralSecurityException ex) {
				Logger.add_error("Error al descifrar el fichero.", ex);
			}
		}
	}

	private void decipPKEY() {
		KeysWindow kw = new KeysWindow(GUIMainWindow.this, true);
		Keys key;
		if ((key = kw.getSelectedKey()) != null) {
			try {
				ASym_Cipher_File acf = new ASym_Cipher_File(file, key.getPrk());
				acf.decipher();
				Logger.add_text("Desencriptado con " + acf.getCypher_type());
				Logger.add_text("Fichero descifrado en: " + acf.getOutput_file());
			} catch (FileNotFoundException ex) {
				Logger.add_error("Error: Fichero no encontrado.", ex);
			} catch (HeaderError ex) {
				Logger.add_error("Error: No se puede leer la cabecera.", ex);
			} catch (IOException ex) {
				Logger.add_error("Error con la entrada/salida.", ex);
			} catch (BadPaddingException ex) {
				Logger.add_error("La clave usada no se corresponde con la clave de cifrado.");
			} catch (NoSuchAlgorithmException ex) {
				Logger.add_error("Error: No se reconoce el algoritmo de cifrado.", ex);
			} catch (GeneralSecurityException ex) {
				Logger.add_error("Error al descifrar el fichero.", ex);
			}
		}
	}

	private void decipSIGN() {
		KeysWindow kw = new KeysWindow(GUIMainWindow.this, false);
		Keys key;
		if ((key = kw.getSelectedKey()) == null)
			return;

		try {
			Signer s = new Signer(file);
			boolean signV = s.verify(key.getPuk());
			Logger.add_text("Desencriptado con " + s.getSignAlg());
			if (signV) {
				Logger.add_good("Fima correcta.");
				s.getOriginalFile();
				Logger.add_text("Guardado fichero original en: " + s.getOutputFile());
			} else {
				Logger.add_error("Fima incorrecta.");
				int option = JOptionPane.showOptionDialog(this, "Firma incorrecta\n¿Obtener el fichero original de todas formas?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (option == JOptionPane.YES_OPTION) {
					s.getOriginalFile();
					Logger.add_text("Guardado fichero original en: " + s.getOutputFile());
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.add_error("Error: Fichero no encontrado.", ex);
		} catch (HeaderError ex) {
			Logger.add_error("Error: No se puede leer la cabecera.", ex);
		} catch (IOException ex) {
			Logger.add_error("Error con la entrada/salida.", ex);
		} catch (NoSuchAlgorithmException ex) {
			Logger.add_error("Error: No se reconoce el algoritmo de cifrado.", ex);
		} catch (GeneralSecurityException ex) {
			Logger.add_error("Error al descifrar el fichero.", ex);
		}

	}

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

		comboBox1.setRenderer(lcr);
		// Insertar los algoritmos disponibles en el desplegable.
		for (Algoritmo a : Algoritmo.list_PKEY_alg)
			comboBox1.addItem(a);
		comboBox1.addItem(null);
		for (Algoritmo a : Algoritmo.list_SIGN_alg)
			comboBox1.addItem(a);
		comboBox1.addItem(null);
		for (Algoritmo a : Algoritmo.list_PBE_alg)
			comboBox1.addItem(a);

		algoritmo = (Algoritmo) comboBox1.getSelectedItem();
		// bloquear el boton de cifrar mensaje si el algoritmo no es un PBE
		cifrarMensajeButton.setEnabled(algoritmo.getType() == Algoritmo.PBE);

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
