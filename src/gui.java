import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class gui {
	private JPanel panel1;
	private JComboBox<Algoritmo> comboBox1;
	private JButton cifrarButton;
	private JButton cifrarMensajeButton;
	private JButton descifrarButton;
	private JButton descifrarMensajeButton;
	private JTextField file_route;
	private JTextArea logTextArea;
	private JButton abrirButton;

	private File file = null;
	private Algoritmo algoritmo = Algoritmo.getListOfAlgorithms()[0];
	private char[] password = null;

	public gui() {
		for (Algoritmo a : Algoritmo.getListOfAlgorithms())
			comboBox1.addItem(a);


		file_route.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);


			}
		});
		abrirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.addChoosableFileFilter(new FileNameExtensionFilter(String.format("Ficheros encriptados (.%s)", Cipher_File.ENCRYPTED_EXTENSION), Cipher_File.ENCRYPTED_EXTENSION));
				int returnVal = jfc.showOpenDialog(jfc);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file_route.setText(jfc.getSelectedFile().getPath());
					System.out.println(jfc.getSelectedFile());
					file = jfc.getSelectedFile();
				} else
					logTextArea.setText("No se ha seleccionado archivo");
			}
		});
		cifrarButton.addActionListener(e -> {
			if (file == null) {
				logTextArea.setText("Error, no se ha seleccionado archivo");
				return;
			}
			logTextArea.setText("Seleccionado: " + comboBox1.getSelectedItem() + "\n Fichero: " + file_route.getText());

			password_screen ps = new password_screen();

			if (ps.getPassword() != null) {
				password = ps.getPassword();
				Cipher_File c = null;
				try {
					c = new Cipher_File(file, algoritmo, password);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					logTextArea.setText("Error, fichero eliminado");
				}
				try {
					c.cipher();
				} catch (IOException ex) {
					ex.printStackTrace();
					logTextArea.setText("Error al cifrar archivo");
				}

				file_route.setText("Fichero: ");
				file = null;
			}

		});
		file_route.addActionListener(e -> {
			file = new File(file_route.getText());
			if (!file.isFile()) {
				logTextArea.setText("Error: " + file_route.getText() + " no es un fichero.");
				file = null;
			} else {
				logTextArea.setText("Seleccionado: " + file_route.getText());
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
			algoritmo = (Algoritmo) comboBox1.getSelectedItem();
			logTextArea.setText("Algoritmo seleccionado: " + algoritmo);
		});
		descifrarButton.addActionListener(e -> {
			if (file == null) {
				logTextArea.setText("Error, no se ha seleccionado archivo");
				return;
			}
			logTextArea.setText("Seleccionado: " + comboBox1.getSelectedItem() + "\n Fichero: " + file_route.getText());

			Write_Password ps = new Write_Password();
			System.out.println(ps.getPassword());


			if (ps.getPassword() != null) {
				password = ps.getPassword();
				Cipher_File c;
				try {
					c = new Cipher_File(file, algoritmo, password);
					c.decipher();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					logTextArea.setText("Error fichero no encontrado");
				} catch (Exception ex) {
					ex.printStackTrace();
					logTextArea.setText("Error al descifrar el fichero");
				}

				file_route.setText("Fichero: ");
				file = null;
			}


		});
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame("APP");
		jf.setContentPane(new gui().panel1);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.pack();

		jf.setVisible(true);
	}


}
