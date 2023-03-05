import java.io.*;
import java.util.Arrays;

public class Header {
	private static final int SIZE = 64;

	private final byte salt_length;
	private final byte[] salt;
	private final Algoritmo algor;

	public Header(byte[] salt, Algoritmo algor) {
		this.salt = salt;
		this.salt_length = (byte) salt.length;
		this.algor = algor;
	}

	public Header(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		this.salt_length = dis.readByte();
		this.salt = dis.readNBytes(this.salt_length);
		algor = Algoritmo.getListOfAlgorithms()[dis.readByte()];
	}

	public void write(OutputStream os) throws IOException {
		DataOutputStream dot = new DataOutputStream(os);
		dot.writeByte(salt_length);
		dot.write(salt);
		dot.writeByte(Arrays.stream(Algoritmo.getListOfAlgorithms()).toList().indexOf(algor));
	}

	public byte[] getSalt() {
		return salt;
	}

	public Algoritmo getAlgor() {
		return algor;
	}
}
