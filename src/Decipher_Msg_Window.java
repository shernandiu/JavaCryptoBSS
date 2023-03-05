import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;

public class Decipher_Msg_Window extends JDialog {
	private JPanel mainPanel;
	private JTextField textField1;
	private JButton desencriptarButton;
	private JPasswordField passwordField1;
	private JTextField textField2;


	public Decipher_Msg_Window() {
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();

		desencriptarButton.addActionListener(e -> {
			try {
				System.out.println("presionado");
				byte[] base64_text = Base64.getDecoder().decode(textField1.getText().getBytes());

				System.out.println(new String(base64_text));
				Cipher_msg cm = new Cipher_msg(Algoritmo.PBEDM53DES, passwordField1.getPassword(), new ByteArrayInputStream(base64_text));
				cm.decipher();
				System.out.println(cm.getText());
				textField2.setText(cm.getText());
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} catch (InvalidAlgorithmParameterException ex) {
				throw new RuntimeException(ex);
			} catch (InvalidKeyException ex) {
				throw new RuntimeException(ex);
			}
		});
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Decipher_Msg_Window();
	}
}
