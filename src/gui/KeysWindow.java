package gui;

import util.Keys;
import util.KeysStore;

import javax.swing.*;
import java.awt.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public class KeysWindow extends JDialog {
	private JButton crearButton;
	private JPanel mainPanel;
	private JButton OKButton;
	private JTable table1;
	String[][] keys;
	String[] names = {"Nombre de la clave", "Disponible"};

	public KeysWindow(Dialog owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		crearButton.addActionListener(e -> {
			new KeysWindow(KeysWindow.this);

		});

		// Seleccionar la modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new KeysWindow(null);
	}

	private void createUIComponents() throws FileAlreadyExistsException {
		List<Keys> key_list = KeysStore.getListOfKeys();
		keys = new String[key_list.size()][2];
		for (int i = 0; i < keys.length; i++) {
			Keys k = key_list.get(i);
			keys[i][0] = k.toString();
			keys[i][1] = switch (k.privateAvaliable()) {
				case Keys.ON_CACHE -> "En caché";
				case Keys.NOT_ENCRYPTED -> "Sin encriptar";
				case Keys.NOT_AVALIABLE -> "Encriptada";
				default -> throw new IllegalStateException("Unexpected value: " + k.privateAvaliable());
			};

		}
		table1 = new JTable(keys, names);
	}
}
