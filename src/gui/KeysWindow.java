package gui;

import util.Keys;
import util.KeysStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Vector;

public class KeysWindow extends JDialog {
	private JButton crearButton;
	private JPanel mainPanel;
	private JButton OKButton;
	private JTable table1;
	private JTextField selectedk;
	DefaultTableModel dtm;
	private Keys selectedKey;

	public KeysWindow(Frame owner) {
		super(owner);
		setLocationRelativeTo(owner);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		crearButton.addActionListener(e -> {
			KeyCreationWindow kcw = new KeyCreationWindow(KeysWindow.this);
			if (kcw.getGeneratedKeys() != null) {
				updateTable();
			}
		});

		table1.getSelectionModel().addListSelectionListener(e -> {
			int selectedRow = table1.getSelectedRow();
			if (selectedRow < 0 || selectedRow > table1.getRowCount())
				return;
			selectedKey = KeysStore.get((String) dtm.getDataVector().get(selectedRow).get(0));
			if (selectedKey.privateAvailable() == Keys.NOT_AVAILABLE) {
				new KeyDecipWindow(KeysWindow.this, selectedKey);
				if (selectedKey.privateAvailable() == Keys.NOT_AVAILABLE)
					selectedKey = null;   // delete if not available
				else
//					updateTable();
				{
					dtm.getDataVector().get(selectedRow).set(1, switch (selectedKey.privateAvailable()) {
						case Keys.ON_CACHE -> "En caché";
						case Keys.NOT_ENCRYPTED -> "Sin encriptar";
						case Keys.NOT_AVAILABLE -> "Encriptada";
						default ->
								throw new IllegalStateException("Unexpected value: " + selectedKey.privateAvailable());
					});
				}
			}
			selectedk.setText(selectedKey == null ? "" : selectedKey.toString());
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
			keys.get(i).add(switch (k.privateAvailable()) {
				case Keys.ON_CACHE -> "En caché";
				case Keys.NOT_ENCRYPTED -> "Sin encriptar";
				case Keys.NOT_AVAILABLE -> "Encriptada";
				default -> throw new IllegalStateException("Unexpected value: " + k.privateAvailable());
			});
		}
		dtm = new DefaultTableModel(keys, names);
		table1 = new JTable(dtm);
		table1.setDefaultEditor(Object.class, null); // non editable
		dtm.fireTableDataChanged();

	}

	private void updateTable() {
		Vector<Vector<String>> keys = new Vector<>();
		Vector<String> names = new Vector<>(List.of(new String[]{"Nombre de la clave", "Disponible"}));
		try {
			List<Keys> key_list = KeysStore.getListOfKeys();
			for (int i = 0; i < key_list.size(); i++) {
				Keys k = key_list.get(i);
				keys.add(new Vector<>());
				keys.get(i).add(k.toString());
				keys.get(i).add(switch (k.privateAvailable()) {
					case Keys.ON_CACHE -> "En caché";
					case Keys.NOT_ENCRYPTED -> "Sin encriptar";
					case Keys.NOT_AVAILABLE -> "Encriptada";
					default -> throw new IllegalStateException("Unexpected value: " + k.privateAvailable());
				});
			}
			dtm.setDataVector(keys, names);
		} catch (FileAlreadyExistsException e) {
			Logger.add_error("Existe un fichero con el nombre de: " + Keys.PATH);
		}
	}
}
