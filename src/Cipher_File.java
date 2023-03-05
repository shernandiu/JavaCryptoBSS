import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Cipher_File extends Cipher_msg {

	private final File file;

	public Cipher_File(File file, Algoritmo cypher_type, char[] password) throws FileNotFoundException {
		super(cypher_type, password, new FileInputStream(file));
		this.file = file;
	}

	@Override
	void cipher() throws IOException {
		File output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat(".cif"));
		os = new FileOutputStream(output_file);

		super.cipher();
	}

	@Override
	void decipher() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException {
		File output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat(".uncif"));
		os = new FileOutputStream(output_file);

		super.decipher();
	}

	public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException {
//		Cypher c = new Cypher(new File("test"), Algoritmo.PBEDM53DES.getAlgorithm(), "aaaa");
//		c.generate_cypher();
//		System.out.println(c.salt);
//		c.read();
	}

}
