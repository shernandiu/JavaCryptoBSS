import org.junit.jupiter.api.*;
import util.Algoritmo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para probar que todos los algoritmos de la máquina son usables para crear secret keys
 *
 * @author Santiago Hernández
 */
class AlgoritmoTest {

	/**
	 * Prueba a crear varios {@link Cipher} e inializarlos con todos los algoritmos de PBE.
	 */
	@Test
	void getListOfAlgorithms() {
		for (Algoritmo a : Algoritmo.list_PBE_alg) {
			assertDoesNotThrow(() -> {

				PBEKeySpec pbeKeySpec = new PBEKeySpec("password".toCharArray());
				PBEParameterSpec pPS = new PBEParameterSpec(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}, 1000);
				SecretKeyFactory kf = SecretKeyFactory.getInstance(a.getAlgorithm());
				SecretKey sKey = kf.generateSecret(pbeKeySpec);


				Cipher c = Cipher.getInstance(a.getAlgorithm());

				c.init(Cipher.DECRYPT_MODE, sKey, pPS);

			}, a.toString());
		}
	}

	@Test
	void get() throws NoSuchAlgorithmException {
		assertEquals(Algoritmo.get("PBEWithMD5AndDES").toString(), "PBE con MD5 & DES", "Cant get PBE/MD5/DES");
		assertThrows(NoSuchAlgorithmException.class, () -> Algoritmo.get("ThisShouldCauseException"), "Does not throws with invented");
	}
}
