package gui.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SeparatorComboBox<T> extends JLabel implements ListCellRenderer<T> {
	JSeparator separator;

	public SeparatorComboBox() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		separator = new JSeparator(JSeparator.HORIZONTAL);
	}

	public Component getListCellRendererComponent(JList list, Object value,
	                                              int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			return separator;
		}
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setText(value.toString());
		return this;
	}
}
