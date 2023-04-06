package util;

import byss.Options;
import exceptions.HeaderError;

import java.io.*;
import java.security.*;

public class Signer extends HeaderReader {
	public static final String EXTENSION_SIGN = "sign";
	public static final String EXTENSION_RECO = "orig";

	private static final int BUFFER_SIZE = 2048;

	private final File file;
	private File outFile;


	public Signer(File f, Algoritmo alg) throws IOException {
		outFile = new File(f.getAbsolutePath().concat("." + EXTENSION_SIGN));
		cypher_type = alg;
		this.file = f;
		option = Options.OP_SIGNED;
	}

	public Signer(File f) throws IOException {
		this.file = f;
		option = Options.OP_SIGNED;
	}

	public void sign(PrivateKey prk) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		is = new FileInputStream(file);

		os = new FileOutputStream(outFile);

		Signature dsa = Signature.getInstance(cypher_type.getAlgorithm());
		dsa.initSign(prk);


		int read;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((read = is.read(buffer)) >= 0) {
			dsa.update(buffer, 0, read);
		}

		data = dsa.sign();  // set sign as data in the header;

		generate_header();

		is = new FileInputStream(file);
		while ((read = is.read(buffer)) >= 0) {
			os.write(buffer, 0, read);
		}

		is.close();
		os.close();
	}

	public boolean verify(PublicKey puk) throws IOException, HeaderError, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		is = new FileInputStream(file);

		read_header();

		Signature dsa = Signature.getInstance(cypher_type.getAlgorithm());
		dsa.initVerify(puk);

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while ((read = is.read(buffer)) >= 0) {
			dsa.update(buffer, 0, read);
		}

		is.close();

		return dsa.verify(data);
	}

	public File getOutputFile() {
		return outFile;
	}

	public File getOriginalFile() throws IOException, HeaderError, NoSuchAlgorithmException {
		String url = file.getAbsolutePath().replaceFirst(String.format("\\.%s$", EXTENSION_SIGN), "") + "." + EXTENSION_RECO;
		outFile = new File(url);
		is = new FileInputStream(file);
		os = new FileOutputStream(outFile);
		read_header();

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while ((read = is.read(buffer)) >= 0) {
			os.write(buffer, 0, read);
		}
		is.close();
		os.close();
		return outFile;
	}
}
