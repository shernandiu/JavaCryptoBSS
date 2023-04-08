package gui;

import util.Keys;
import util.KeysStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Vector;

/**
 * Ventana para seleccionar el par de claves a usar para cifrar/descifrar,
 * firmar/verificar o crear un nuevo par de claves.
 *
 * @author Santigo Hernández
 */
public class KeysWindow extends JDialog {
	private JButton crearButton;
	private JPanel mainPanel;
	private JButton OKButton;
	private JTable table1;
	private JTextField selectedk;
	private DefaultTableModel dtm;        // contenido de la tabla.
	private Keys selectedKey;
	private Keys finalSelectedKey = null; // clave seleccionada si se presiona aceptar.

	private void accept() {
		if (selectedKey == null) {
			JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna clave.", null, JOptionPane.WARNING_MESSAGE);
			return;
		}
		finalSelectedKey = selectedKey;
		dispose();
	}

	/**
	 * Constructor de la ventana
	 *
	 * @param owner         Ventana padre
	 * @param privateNeeded {@code true} si la clave privada es necesaria y por tanto se necesita descifrarla
	 *                      en caso de que se encuentre cifrada.
	 *                      <p>{@code false} si solo se requiere la clave pública y no es necesario descifrar las privadas.
	 */
	public KeysWindow(Frame owner, boolean privateNeeded) {
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

		table1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				System.out.println(e.getClickCount());
				int selectedRow = table1.getSelectedRow();
				if (selectedRow < 0 || selectedRow > table1.getRowCount())
					return;
				selectedKey = KeysStore.get((String) dtm.getDataVector().get(selectedRow).get(0));
				if (selectedKey.privateAvailable() == Keys.NOT_AVAILABLE && privateNeeded) {
					new KeyDecipWindow(KeysWindow.this, selectedKey);
					if (selectedKey.privateAvailable() == Keys.NOT_AVAILABLE)
						selectedKey = null;   // delete if not available
					else
//					updateTable();
						updateRow(selectedRow);
				}
				selectedk.setText(selectedKey == null ? "" : selectedKey.toString());
				if (e.getClickCount() >= 2 && selectedKey != null)
					accept();
			}
		});

		OKButton.addActionListener(e -> accept());

		// Seleccionar la modalidad
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new KeysWindow(null, true);
	}

	/**
	 * Inicializa la tabla de claves con los valores de {@link KeysStore}
	 *
	 * @throws FileAlreadyExistsException Existe un fichero con el mismo nombre que {@link Keys#PATH}
	 */
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

	/**
	 * Actualiza la tabla con los nuevos valores de {@link KeysStore}
	 */
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

	/**
	 * Actualiza el valor de una fila concreta.
	 *
	 * @param row Índice de la fila a modificar.
	 */
	private void updateRow(int row) {
		Vector<String> vrow = dtm.getDataVector().get(row);
		vrow.set(1, switch (selectedKey.privateAvailable()) {
			case Keys.ON_CACHE -> "En caché";
			case Keys.NOT_ENCRYPTED -> "Sin encriptar";
			case Keys.NOT_AVAILABLE -> "Encriptada";
			default -> throw new IllegalStateException("Unexpected value: " + selectedKey.privateAvailable());
		});
	}

	/**
	 * Devuelve la clave seleccionada
	 *
	 * @return {@link Keys} seleccionadas o {@code null} si se ha cerrado la ventana.
	 */
	public Keys getSelectedKey() {
		return finalSelectedKey;
	}
}
