package util;

import exceptions.HeaderError;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderTest {

	@Test
	void write() throws IOException {
		Header h = new Header(new byte[]{10, 11, 12, 13, 14, 15, 16, 17}, Algoritmo.getListOfAlgorithms()[0]);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		h.write(baos);

		byte[] output = baos.toByteArray();

		assertEquals(output[0], 8);
		for (int i = 0; i < 8; i++) {
			assertEquals(output[i + 1], 10 + i);
		}

		assertEquals(output[9], 0);
	}

	@Test
	void read() throws HeaderError {
		byte[] array = new byte[]{10, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

		Header h = new Header(new ByteArrayInputStream(array));

		byte[] salt = h.getSalt();
		assertEquals(salt.length, 10);
		for (int i = 0; i < 10; i++) {
			assertEquals(salt[i], i);
		}

		assertEquals(h.getAlgor(), Algoritmo.getListOfAlgorithms()[0]);
	}
}
