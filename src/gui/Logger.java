package gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

class Logger {
	private static final AttributeSet error_AS = new SimpleAttributeSet();
	private static final AttributeSet text_AS = new SimpleAttributeSet();
	private static JTextPane log = null;
	private static Document doc = null;

	static {
		StyleConstants.setForeground((MutableAttributeSet) text_AS, Color.BLACK);
		StyleConstants.setForeground((MutableAttributeSet) error_AS, Color.RED);
	}

	public static void setLog(JTextPane log) {
		Logger.log = log;
		Logger.doc = log.getStyledDocument();
	}

	public static void add_text(String str) {
		try {
			doc.insertString(doc.getLength(), str + "\n", text_AS);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void add_error(String str) {
		try {
			doc.insertString(doc.getLength(), str + "\n", error_AS);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void clear() {
		log.setText("");
	}

}
