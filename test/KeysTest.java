import exceptions.PasswError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import util.Keys;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.InvalidParameterException;
import java.util.concurrent.atomic.AtomicReference;

class KeysTest {
	Keys test = null;
	Keys test2 = null;
	char[] password = "P4ssw0rd".toCharArray();

	@AfterEach
	void tearDown() throws IOException {
		if (test != null && !test.getFile().getPath().replace('\\', '/').matches(".*/test/resources/.*"))
			test.getFile().delete();
		if (test2 != null)
			test2.getFile().delete();
	}

	@Test
	void saveKeys() {
		File f = new File(Keys.PATH + "prueba" + Keys.EXTENSION);

		assertDoesNotThrow(() -> {
			test = new Keys("prueba", null);
		});
		assertTrue(f.exists());
		assertEquals(f, test.getFile());
		assertEquals(test.privateAvailable(), Keys.NOT_ENCRYPTED);
	}


	@Test
	void load() throws Exception {
		test = new Keys("k1", null);
		test2 = new Keys(test.getFile());
		assertEquals(test.getPuk(), test2.getPuk());
		assertEquals(test.getPrk(), test2.getPrk());
	}

	@Test
	void checkDupKey() throws Exception {
		test = new Keys("testK", null);
		assertThrows(FileAlreadyExistsException.class, () -> new Keys("testK", null));
	}

	@Test
	void encryptedKey() throws Exception {
		test = new Keys("testK", password);
		assertNotNull(test.getPrk());
		assertEquals(test.privateAvailable(), Keys.ON_CACHE);
	}

	@Test
	void decryptKey() throws Exception {
		test = new Keys(new File("./test/resources/key2.key"));
		assertNull(test.getPrk());
		assertNotNull(test.getPuk());
		assertEquals(test.privateAvailable(), Keys.NOT_AVAILABLE);
		assertThrows(PasswError.class, () -> test.decipherKey("badpass".toCharArray()));
		assertDoesNotThrow(() -> test.decipherKey(password));
		assertNotNull(test.getPrk());
		assertThrows(InvalidParameterException.class, () -> test.decipherKey("badpass".toCharArray()));
	}
}
