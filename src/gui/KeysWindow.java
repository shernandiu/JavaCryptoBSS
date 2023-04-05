package gui;

import util.Keys;
import util.KeysStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Vector;

public class KeysWindow extends JDialog {
	private JButton crearButton;
	private JPanel mainPanel;
	private JButton OKButton;
	private JTable table1;
	DefaultTableModel dtm;

	public KeysWindow(Frame owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		crearButton.addActionListener(e -> {
			KeyCreationWindow kcw = new KeyCreationWindow(KeysWindow.this);
			if (kcw.getGeneratedKeys() != null) {
				updateTable(kcw.getGeneratedKeys());
			}
		});

		// Seleccionar la modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new KeysWindow(null);
	}

	private void createUIComponents() throws FileAlreadyExistsException {
		Vector<Vector<String>> keys = new Vector<>();
		Vector<String> names = new Vector<>(List.of(new String[]{"Nombre de la clave", "Disponible"}));

		List<Keys> key_list = KeysStore.getListOfKeys();
		for (int i = 0; i < key_list.size(); i++) {
			Keys k = key_list.get(i);
			keys.add(new Vector<>());
			keys.get(i).add(k.toString());
			keys.get(i).add(switch (k.privateAvaliable()) {
				case Keys.ON_CACHE -> "En caché";
				case Keys.NOT_ENCRYPTED -> "Sin encriptar";
				case Keys.NOT_AVALIABLE -> "Encriptada";
				default -> throw new IllegalStateException("Unexpected value: " + k.privateAvaliable());
			});
		}
		dtm = new DefaultTableModel(keys, names);
		table1 = new JTable(dtm);
		dtm.fireTableDataChanged();
	}

	private void updateTable(Keys k) {
		dtm.addRow(new String[]{k.toString(), switch (k.privateAvaliable()) {
			case Keys.ON_CACHE -> "En caché";
			case Keys.NOT_ENCRYPTED -> "Sin encriptar";
			case Keys.NOT_AVALIABLE -> "Encriptada";
			default -> throw new IllegalStateException("Unexpected value: " + k.privateAvaliable());
		}});
	}
}
