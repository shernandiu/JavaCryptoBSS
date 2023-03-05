//import javax.crypto.spec.*;
//import javax.crypto.*;
//
//
//public class Main {
//	public static void main(String[] args) throws Exception {
//
//		char[] passwd = "Hola".toCharArray();
//		PBEKeySpec pbeKeySpec = new PBEKeySpec(passwd);
//
//		byte[] salt={0,1,2,3};
//		int iterationCount=0;
//		PBEParameterSpec pPS = new PBEParameterSpec(salt,iterationCount);
//
//		String algorithm="AES";
//		SecretKeyFactory kf = SecretKeyFactory.getInstance(algorithm);
//
//		SecretKey sKey = kf.generateSecret(pbeKeySpec);
//
//		Cipher c = Cipher.getInstance(algorithm);
//		c.init(Cipher.ENCRYPT_MODE,sKey,pPS);
//
//		CipherOutputStream cos = new CipherOutputStream(OutputStream,c);
//
//	}
//}
