import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.ASym_Cipher_File;
import util.Algoritmo;
import util.Cipher_File;

import java.io.*;
import java.security.DigestInputStream;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para probar las funcionalidades del cifrado de ficheros.
 *
 * @author Santiago Hernández
 */
class ASym_Cipher_FileTest {
	private Cipher_File cipher_C;
	private File f;
	private static KeyPair kp;

	@BeforeAll
	static void init() throws IOException, ClassNotFoundException {
		kp = (KeyPair) (new ObjectInputStream(new FileInputStream("./test/resources/keys"))).readObject();
	}

	@BeforeEach
	void setUp() throws FileNotFoundException {
		f = new File("./test/resources/test.txt");
		cipher_C = new ASym_Cipher_File(f, Algoritmo.list_PKEY_alg[0], kp.getPublic());
	}

	/**
	 * Prueba que cifrar el fichero de prueba no arroja excepciones
	 * y genera un fichero de salida.
	 */
	@Test
	void cipher() {
		assertDoesNotThrow(() -> cipher_C.cipher());
		File outputfile = new File("./test/resources/test." + Cipher_File.ENCRYPTED_EXTENSION);
		assertTrue(outputfile.exists());
		assertEquals(outputfile.getAbsoluteFile(), cipher_C.getOutput_file().getAbsoluteFile());

		outputfile.delete();
	}

	/**
	 * Prueba que al descifrar un fichero cifrado genera un fichero exactamente igual al original.
	 *
	 * @throws Exception Si algo sale mal durante el test.
	 */
	@Test
	void decipher() throws Exception {
		cipher_C.cipher();
		File outputfile = new File("./test/resources/test." + Cipher_File.ENCRYPTED_EXTENSION);

		Cipher_File decip = new ASym_Cipher_File(outputfile, kp.getPrivate());
		decip.decipher();
		File decipfile = new File("./test/resources/test." + Cipher_File.DECRYPTED_EXTENSION);

		assertTrue(decipfile.exists());

		assertArrayEquals(checksum(f), checksum(decipfile));

		decipfile.delete();
		outputfile.delete();
	}

	/**
	 * Genera el resumen de un fichero mediante SHA-256 o MD4 si no está disponible
	 * en la máquina virtual
	 *
	 * @param f Fichero a resumir
	 * @return Resumen hash del fichero como array de byres
	 * @throws IOException              Error al leer el fichero
	 * @throws NoSuchAlgorithmException La máquina virtual no dispone del algoritmo SHA-256 ni MD4
	 */
	byte[] checksum(File f) throws IOException, NoSuchAlgorithmException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			md = MessageDigest.getInstance("MD4");
		}
		DigestInputStream dis = new DigestInputStream(new FileInputStream(f), md);
		byte[] buffer = new byte[1024 * 8];
		while (dis.read(buffer) != -1) {
		}
		dis.close();
		return md.digest();
	}


	/**
	 * Prueba si al desencriptar un fichero con error se borra el fichero
	 */
	@Test
	void badDecription() {
		assertThrows(Exception.class, () -> cipher_C.decipher());
		assertFalse(new File("./test/resources/test." + Cipher_File.DECRYPTED_EXTENSION).exists());
	}
}
