import javax.swing.*;
import javax.xml.transform.Source;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

public class Cipher_Msg_Window extends JDialog {
	private JTextField textField1;
	private JTextField textField2;
	private JButton encriptarButton;
	private JPanel mainPanel;
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;

	public Cipher_Msg_Window() {
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		encriptarButton.addActionListener(e -> {
			try {
				System.out.println("presionado");
				Cipher_msg cm = new Cipher_msg(Algoritmo.PBEDM53DES, passwordField1.getPassword(), new ByteArrayInputStream(textField1.getText().getBytes()));
				cm.cipher();
				System.out.println(cm.getText());
				textField2.setText(cm.getTextBase64());
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Cipher_Msg_Window();
	}
}
