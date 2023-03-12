import org.junit.jupiter.api.Test;
import util.Algoritmo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
		for (Algoritmo a : Algoritmo.getListOfAlgorithms()) {
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
}
