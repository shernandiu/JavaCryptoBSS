import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

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


	public Cipher_msg(Algoritmo cypher_type, char[] password, InputStream is) throws FileNotFoundException {
		this.is = is;
		this.cypher_type = cypher_type;
		this.password = password;
		for (int i = 0; i < this.password.length; i++) {
			this.password[i] &= 0x7F;
		}
		os = new ByteArrayOutputStream();
	}

	protected void generate_cypher(int ciphing) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {
		if (ciphing == Cipher.ENCRYPT_MODE)
			salt = generate_salt();
		else
			read_header();

		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		pPS = new PBEParameterSpec(salt, ITERATIONCOUNT);
		SecretKeyFactory kf = SecretKeyFactory.getInstance(cypher_type.getAlgorithm());
		sKey = kf.generateSecret(pbeKeySpec);


		c = Cipher.getInstance(cypher_type.getAlgorithm());
	}

	void cipher() throws IOException {
		try {
			generate_cypher(Cipher.ENCRYPT_MODE);

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
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	//TODO: ARREGLAR CUANDO NO DETECTA EL ENCRIPTADO
	void decipher() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException {
		try {
			generate_cypher(Cipher.DECRYPT_MODE);

			c.init(Cipher.DECRYPT_MODE, sKey, pPS);

			CipherInputStream cis = new CipherInputStream(is, c);

			byte[] buffer = new byte[BUFFER_SIZE];

			int read;
			while ((read = cis.read(buffer)) > 0) {
				os.write(buffer, 0, read);
			}

			cis.close();
			os.close();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}


	}

	byte[] generate_salt() {
		byte[] salt = new byte[SALT_DEFAULT_SIZE];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		return salt;
	}


	void generate_header(OutputStream os) throws IOException {
		Header header = new Header(salt, cypher_type);
		header.write(os);
	}

	void read_header() throws IOException {
		Header header = new Header(is);
		salt = header.getSalt();
		cypher_type = header.getAlgor();
	}

	String getText() {
		ByteArrayOutputStream os1 = (ByteArrayOutputStream) os;
		return os1.toString();
	}

	String getTextBase64() {
		return Base64.getEncoder().encodeToString(((ByteArrayOutputStream) os).toByteArray());
	}
}
