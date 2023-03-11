package util;

import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Cipher_File extends Cipher_msg {

	public static final String ENCRYPTED_EXTENSION = "cif";
	public static final String DECRYPTED_EXTENSION = "uncif";

	private final File file;
	private File output_file;

	public Cipher_File(File file, Algoritmo cypher_type, char[] password) throws FileNotFoundException {
		super(cypher_type, password, new FileInputStream(file));
		this.file = file;
	}

	public Cipher_File(File file, char[] password) throws FileNotFoundException {
		super(password, new FileInputStream(file));
		this.file = file;
	}

	@Override
	public void cipher() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + ENCRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);

		super.cipher();
	}

	@Override
	public void decipher() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, PasswError {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + DECRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);

		super.decipher();
	}

	public File getOutput_file() {
		return output_file;
	}

}
