import exceptions.HeaderError;
import exceptions.PasswError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Algoritmo;
import util.Cipher_msg;

import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para probar las capacidades del cifrado de mensajes
 * de la clase {@link Cipher_msg}.
 *
 * @author Santiago Hernández
 */
class Cipher_msgTest {
	private static final String original_msg = "This is a test, this should be ciphered with no problems";
	private static final String password = "Sup3rS3cur3P455w0rd";
	private Cipher_msg cipher_C;
	private Cipher_msg decipher_C;


	@BeforeEach
	void setUp() {
		cipher_C = new Cipher_msg(Algoritmo.getListOfAlgorithms()[0], password.toCharArray(), new ByteArrayInputStream(original_msg.getBytes()));
	}


	/**
	 * Prueba que el cifrado no arroja una excepción.
	 */
	@Test
	void cipher() throws Exception {
		assertDoesNotThrow(() -> cipher_C.cipher());
		assertTrue(cipher_C.getText().length > 0);
	}

	/**
	 * Prueba que el descifrado produce un mensaje igual al original.
	 *
	 * @throws Exception Si ocurre un fallo durante el test.
	 */
	@Test
	void decipher() throws Exception {
		cipher_C.cipher();
		decipher_C = new Cipher_msg(password.toCharArray(), new ByteArrayInputStream(cipher_C.getText()));
		decipher_C.decipher();

		assertEquals(new String(decipher_C.getText()), original_msg);

		decipher_C = new Cipher_msg(Algoritmo.getListOfAlgorithms()[0], password.toCharArray(), new ByteArrayInputStream(cipher_C.getText()));
		decipher_C.decipher();

		assertEquals(new String(decipher_C.getText()), original_msg);
	}

	/**
	 * Prueba que la clase {@link Cipher_msg} almacena el cifrado correctamente.
	 *
	 * @throws Exception Si ocurre un error durante el test.
	 */
	@Test
	void getCypher_type() throws Exception {
		assertEquals(cipher_C.getCypher_type().getAlgorithm(), Algoritmo.getListOfAlgorithms()[0].getAlgorithm());
		cipher_C.cipher();
		decipher_C = new Cipher_msg(password.toCharArray(), new ByteArrayInputStream(cipher_C.getText()));
		decipher_C.decipher();
		assertEquals(decipher_C.getCypher_type().getAlgorithm(), Algoritmo.getListOfAlgorithms()[0].getAlgorithm());
	}

	/**
	 * Prueba que un mensaje lanza error al descifrar con contraseñas erróneas.
	 */
	@Test
	void testBadPassword() throws Exception {
		cipher_C.cipher();

		decipher_C = new Cipher_msg("ThisIsNotThePassword".toCharArray(), new ByteArrayInputStream(cipher_C.getText()));

		assertThrowsExactly(PasswError.class, () -> decipher_C.decipher(), "No badpassword error");
	}

	/**
	 * Prueba que un mensaje lanza error al descifrar un mensaje no cifrado
	 * al intentar leer la cabecera.
	 */
	@Test
	void testBadMsg() throws Exception {
		decipher_C = new Cipher_msg(password.toCharArray(), new ByteArrayInputStream("0000This is not an encrypted message".getBytes()));

		assertThrowsExactly(HeaderError.class, () -> decipher_C.decipher(), "Decipher non encrypted message");
	}
}
