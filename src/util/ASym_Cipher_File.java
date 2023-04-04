package util;

import byss.Options;
import exceptions.HeaderError;
import exceptions.PasswError;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class ASym_Cipher_File extends Cipher_File {

	protected byte operation = Options.OP_PUBLIC_CIPHER;
	private PublicKey puk;
	private PrivateKey prk;

	public ASym_Cipher_File(File file, Algoritmo cypher_type, PublicKey puk) throws FileNotFoundException {
		super(file, cypher_type, null);
		this.puk = puk;
		salt = new byte[0];
	}

	public ASym_Cipher_File(File file, PrivateKey prk) throws FileNotFoundException {
		super(file, null);
		this.prk = prk;
	}

	@Override
	public void cipher() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + ENCRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);

		try {
			generate_cypher();
			c.init(Cipher.ENCRYPT_MODE, puk);

//			CipherOutputStream cos = new CipherOutputStream(os, c); // elimina esto

			generate_header();

			byte[] buffer = new byte[53];
			byte[] output_buffer;

			int read;
			while ((read = is.read(buffer)) >= 0) {

				output_buffer = c.doFinal(buffer, 0, read);
				os.write(output_buffer);

				System.out.print(read + ", ");
			}
			System.out.println("\b\b.");
//			cos.close();
			is.close();
			os.close();
		} catch (Exception ex) {
			os.close();
			output_file.delete();
			throw ex;
		}
	}

	@Override
	public void decipher() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, HeaderError, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, PasswError, IllegalBlockSizeException, BadPaddingException {
		output_file = new File(file.getAbsolutePath().replaceFirst("\\.\\w+$", "").concat("." + DECRYPTED_EXTENSION));
		os = new FileOutputStream(output_file);
		try {
			read_header();

			generate_cypher();

			c.init(Cipher.DECRYPT_MODE, prk);

			try {
				byte[] buffer = new byte[64];
				byte[] output_buffer;
				int read;
				while ((read = is.read(buffer)) >= 0) {
					output_buffer = c.doFinal(buffer, 0, read);
					os.write(output_buffer);
					System.out.print(read + ":" + output_buffer.length + ", ");
				}
				System.out.println("\b\b.");
			} catch (IOException ex) {
				throw new PasswError(ex.getMessage());
			} finally {
				os.close();
				is.close();
			}
		} catch (Exception ex) {
			os.close();
			output_file.delete();
			throw ex;
		}
	}

	@Override
	protected void generate_cypher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		c = Cipher.getInstance(cypher_type.getAlgorithm());
	}

	public static void main(String[] args) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512); // 512 bits
		KeyPair kp = kpg.generateKeyPair();
		System.out.println("Encriptando");
		ASym_Cipher_File acf = new ASym_Cipher_File(new File("./test/resources/test.txt"), Algoritmo.get("RSA/ECB/PKCS1Padding"), kp.getPublic());
		acf.cipher();
		System.out.println("Desencriptando");
		acf = new ASym_Cipher_File(new File("./test/resources/test.cif"), kp.getPrivate());
		acf.decipher();

		ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream("./test/resources/keys"));
		fos.writeObject(kp);
	}
}
