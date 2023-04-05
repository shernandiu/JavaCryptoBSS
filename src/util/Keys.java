package util;

import netscape.javascript.JSObject;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioFormat;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Keys {
	public static final int NOT_ENCRYPTED = 0;
	public static final int ON_CACHE = 1;
	public static final int NOT_AVALIABLE = 2;

	private static final int DEFAULT_KEY_SIZE = 512;
	public static final String PATH = "./keypairs/";
	public static final String EXTENSION = ".key";


	private PublicKey puk;
	private PrivateKey prk;
	private String name;
	private String type;
	private int size;
	private byte[] prk_encrypted;
	private Algoritmo encryp_type;
	private boolean privateAvaliable;

	public Keys(String name, char[] password) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(DEFAULT_KEY_SIZE); // 512 bits
		KeyPair kp = kpg.generateKeyPair();
		puk = kp.getPublic();
		prk = kp.getPrivate();
		this.name = name;
		this.size = DEFAULT_KEY_SIZE;
		this.type = "RSA";
		if (password != null) {
			saveKeys(password);
		}
		privateAvaliable = password == null;
	}

	public Keys(File f) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		loadKeys(f);
	}

	public void saveKeys(char[] password) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		File f = new File(PATH + name + EXTENSION);
		if (f.exists())
			throw new FileAlreadyExistsException(name + " exists");
		// do not overwrite keys
		StringBuilder output = new StringBuilder();

		// header
		output.append(name).append("\n");
		output.append(String.format("%s # %d bits\n", type, size));
		output.append(String.format("private_ciphered=%s%s\n", password != null, password != null ? " # " + Algoritmo.list_PBE_alg[0].getAlgorithm() : ""));
		output.append("\n");

		// build public key
		output.append("Public key:\n");
		String strpuk = Base64.getEncoder().encodeToString(puk.getEncoded());

		for (int i = 0; i < strpuk.length(); i += 40)
			output.append(strpuk, i, Math.min(i + 40, strpuk.length())).append("\n");
		output.append("\n");

		// build private key
		output.append("Private key:\n");
		String strprk = Base64.getEncoder().encodeToString(password == null ? prk.getEncoded() : cipherPrivateKey(password));

		for (int i = 0; i < strprk.length(); i += 40)
			output.append(strprk, i, Math.min(i + 40, strprk.length())).append("\n");
		output.append("\n");

		if (!f.getParentFile().exists())
			f.getParentFile().mkdir();
		f.createNewFile();

		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
		osw.write(String.format(output.toString()));
		osw.close();
	}

	private byte[] cipherPrivateKey(char[] password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		Cipher_msg cm = new Cipher_msg(Algoritmo.list_PBE_alg[0], password, new ByteArrayInputStream(prk.getEncoded()));
		cm.cipher();
		return cm.getText();
	}

	private void loadKeys(File f) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String tmp;
		String[] splt;
		StringBuilder puk_sb = new StringBuilder();
		StringBuilder prk_sb = new StringBuilder();
		Matcher m;

		BufferedReader br = new BufferedReader(new FileReader(f));
		name = br.readLine();

		// TYPE AND SIZE
		tmp = br.readLine();
		splt = tmp.split(" *# *");
		type = splt[0].trim();
		m = Pattern.compile("(\\d+)").matcher(splt[1]);
		System.out.println(m.find());
		size = Integer.parseInt(m.group(1));

		// Encryption / type
		tmp = br.readLine();
		splt = tmp.split(" *# *");
		m = Pattern.compile("(?:private_ciphered *= *)(true|false)").matcher(splt[0]);
		m.find();
		boolean encryp = Boolean.parseBoolean(m.group(1));
		privateAvaliable = !encryp;
		if (encryp)
			encryp_type = Algoritmo.get(splt[1].trim());

		// Find public key
		do {
			tmp = br.readLine().trim();
		} while (!tmp.equals("Public key:"));

		// Get public key
		tmp = br.readLine().trim();
		while (!(tmp.equals("") | tmp.equals("Private key:"))) {
			puk_sb.append(tmp);
			tmp = br.readLine().trim();
		}

		// Find private key
		do {
			tmp = br.readLine().trim();
		} while (!tmp.equals("Private key:"));

		// Get private key
		tmp = br.readLine().trim();
		while (!(tmp.equals(""))) {
			prk_sb.append(tmp);
			tmp = br.readLine().trim();
		}

		KeyFactory kf = KeyFactory.getInstance(type);
		KeySpec kp = new X509EncodedKeySpec(Base64.getDecoder().decode(puk_sb.toString()));
		puk = kf.generatePublic(kp);

		if (!encryp) {
//			kf = KeyFactory.getInstance(type);
			kp = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(prk_sb.toString()));
			prk = kf.generatePrivate(kp);
		} else {
			prk_encrypted = Base64.getDecoder().decode(prk_sb.toString());
		}

		br.close();
	}

	public PublicKey getPuk() {
		return puk;
	}

	public PrivateKey getPrk() {
		return prk;
	}

	public int privateAvaliable() {
		if (prk == null)
			return NOT_AVALIABLE;
		if (!privateAvaliable)
			return ON_CACHE;
		else
			return NOT_ENCRYPTED;
	}

	public String toString() {
		return name;
	}

	public static void main(String[] args) throws Exception {

//		new File(PATH + "example.key").delete();
		Keys k = new Keys("key2", "password".toCharArray());
//		k.saveKeys("passwordpassword".toCharArray());
//		k.saveKeys(null);

//		Keys l = new Keys(new File(PATH + "example.key"));

//		System.out.println(k.puk);
//		System.out.println(k.prk);
//		System.out.println(l.puk);
//		System.out.println(l.prk);
	}
}
