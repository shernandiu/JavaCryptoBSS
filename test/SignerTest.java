import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Algoritmo;
import util.Cipher_File;
import util.Keys;
import util.Signer;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SignerTest {
	File f = new File("./test/resources/test.txt");
	Keys k = new Keys(new File("./test/resources/testsKey.key"));
	Signer s;
	Algoritmo alg = Algoritmo.list_SIGN_alg[0];

	@AfterEach
	void tearDown() {
		if (s.getOutputFile() != null) {
			s.getOutputFile().delete();
		}
	}

	SignerTest() throws Exception {
	}

	@Test
	void sign() throws Exception {
		s = new Signer(f, alg);
		assertDoesNotThrow(() -> s.sign(k.getPrk()));
		assertTrue(s.getOutputFile().exists());
		assertEquals(new File(f.getAbsolutePath() + "." + Signer.EXTENSION_SIGN), s.getOutputFile());
	}

	@Test
	void verify() throws Exception {
		s = new Signer(f, alg);
		s.sign(k.getPrk());

		Signer t = new Signer(s.getOutputFile());
		assertTrue(t.verify(k.getPuk()));
		RandomAccessFile raf = new RandomAccessFile(s.getOutputFile(), "rw");
		raf.seek(100);
		raf.writeByte(-1);
		assertFalse(t.verify(k.getPuk()));
		raf.close();
	}

	@Test
	void writeFile() throws Exception {
		s = new Signer(f, alg);
		s.sign(k.getPrk());
		Signer t = new Signer(s.getOutputFile());
		assertDoesNotThrow(t::getOriginalFile);
		assertTrue(t.getOutputFile().exists());
		assertArrayEquals(Cipher_FileTest.checksum(f), Cipher_FileTest.checksum(t.getOutputFile()));
	}


	private static Stream<Algoritmo> getAlg() {
		return Arrays.stream(Algoritmo.list_SIGN_alg);
	}

	/**
	 * Realiza la prueba {@link SignerTest#decipher()} mediante
	 * todos los algoritmos de firma disponibles.
	 *
	 * @param a Algoritmo Firma.
	 * @throws Exception Si ocurre un error durante el test.
	 */
	@ParameterizedTest
	@MethodSource("getAlg")
	void signAll(Algoritmo a) throws Exception {
		alg = a;
		sign();
	}

}
