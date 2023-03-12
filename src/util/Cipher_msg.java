package util;

import byss.Header;
import byss.Options;
import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Cipher_msg {
	protected static final int ITERATIONCOUNT = 5;
	protected static final int SALT_DEFAULT_SIZE = 8;
	protected static final int BUFFER_SIZE = 1024;


	protected final InputStream is;
	protected final char[] password;
	protected OutputStream os;
	protected Algoritmo cypher_type;
	protected byte[] salt = null;
	protected Cipher c;
	protected SecretKey sKey;
	protected PBEParameterSpec pPS;


	public Cipher_msg(Algoritmo cypher_type, char[] password, InputStream is) {
		this.is = is;
		this.cypher_type = cypher_type;
		this.password = password;
		for (int i = 0; i < this.password.length; i++) {
			this.password[i] &= 0x7F;
		}
		os = new ByteArrayOutputStream();
	}

	public Cipher_msg(char[] password, InputStream is) {
		this.is = is;
		this.password = password;
		for (int i = 0; i < this.password.length; i++) {
			this.password[i] &= 0x7F;
		}
		os = new ByteArrayOutputStream();
	}

	protected void generate_cypher() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		pPS = new PBEParameterSpec(salt, ITERATIONCOUNT);
		SecretKeyFactory kf = SecretKeyFactory.getInstance(cypher_type.getAlgorithm());
		sKey = kf.generateSecret(pbeKeySpec);


		c = Cipher.getInstance(cypher_type.getAlgorithm());
	}

	public void cipher() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
		salt = generate_salt();

		generate_cypher();

		c.init(Cipher.ENCRYPT_MODE, sKey, pPS);

		CipherOutputStream cos = new CipherOutputStream(os, c);

		generate_header(os);

		byte[] buffer = new byte[BUFFER_SIZE];

		int read;
		while (is.available() > 0) {
			read = is.read(buffer);
			cos.write(buffer, 0, read);
		}

		cos.close();
		is.close();
	}

	public void decipher() throws IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, PasswError {
		read_header();

		generate_cypher();

		c.init(Cipher.DECRYPT_MODE, sKey, pPS);

		CipherInputStream cis = new CipherInputStream(is, c);

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		try {
			while ((read = cis.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}
		} catch (IOException ex) {
			throw new PasswError(ex.getMessage());
		} finally {
			cis.close();
			os.close();
		}
	}

	private byte[] generate_salt() {
		byte[] salt = new byte[SALT_DEFAULT_SIZE];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		return salt;
	}


	void generate_header(OutputStream os) {
		try {
			Header header = new Header(Options.OP_NONE, cypher_type.getAlgorithm(), null, salt);
			header.save(os);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void read_header() throws HeaderError, NoSuchAlgorithmException {
		try {
			Header header = new Header();
			if (!header.load(is))
				throw new HeaderError("Cant read header");
			salt = header.getData();
			cypher_type = Algoritmo.get(header.getAlgorithm1());
		} catch (NoSuchAlgorithmException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new HeaderError(ex.getMessage());
		}
	}

	public byte[] getText() throws IOException {
		os.flush();
		ByteArrayOutputStream os1 = (ByteArrayOutputStream) os;
		return os1.toByteArray();
	}

	public Algoritmo getCypher_type() {
		return cypher_type;
	}
}
