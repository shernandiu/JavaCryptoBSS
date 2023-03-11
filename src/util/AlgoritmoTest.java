package util;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AlgoritmoTest {


	@Test
	void getListOfAlgorithms() {
		for (Algoritmo a : Algoritmo.getListOfAlgorithms()) {
			try {
				assertDoesNotThrow(() -> Cipher.getInstance(a.getAlgorithm()));
				System.out.println(a + ": OK");
			} catch (Exception e) {
				System.out.println(a + ": not");
			}

		}
	}
}
