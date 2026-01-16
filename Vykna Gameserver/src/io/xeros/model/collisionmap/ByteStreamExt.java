package io.xeros.model.collisionmap;

public final class ByteStreamExt {

	private static final char[] OSRS_CHARACTERS = new char[]
			{
					'\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026',
					'\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039',
					'\u0152', '\u0000', '\u017d', '\u0000', '\u0000', '\u2018',
					'\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014',
					'\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u0000',
					'\u017e', '\u0178'
			};

	public void skip(int length) {
		currentOffset += length;
	}

	public ByteStreamExt(byte[] abyte0) {
		buffer = abyte0;
		currentOffset = 0;
	}

	public int readUnsignedByte() {
		return buffer[currentOffset++] & 0xff;
	}

	public byte readSignedByte() {
		return buffer[currentOffset++];
	}

	public int readUnsignedWord() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readSignedWord() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int read3Bytes() {
		currentOffset += 3;
		return ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readR3Bytes() {
		currentOffset += 3;
		return ((buffer[currentOffset - 1] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 3] & 0xff);
	}

	public int readDWord() {
		currentOffset += 4;
		return ((buffer[currentOffset - 4] & 0xff) << 24) + ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8)
				+ (buffer[currentOffset - 1] & 0xff);
	}

	public long readQWord() {
		long l = readDWord() & 0xffffffffL;
		long l1 = readDWord() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String readString() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 10)
			;
		return new String(buffer, i, currentOffset - i - 1);
	}

	public String readNewString() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 0)
			;
		return new String(buffer, i, currentOffset - i - 1);
	}

	
	
	public byte[] readBytes() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 10)
			;
		byte[] abyte0 = new byte[currentOffset - i - 1];
		System.arraycopy(buffer, i, abyte0, i - i, currentOffset - 1 - i);
		return abyte0;
	}

	public void readBytes(int i, int j, byte[] abyte0) {
		for (int l = j; l < j + i; l++)
			abyte0[l] = buffer[currentOffset++];
	}

	public byte[] buffer;
	public int currentOffset;

	public int read24BitInt()
	{
		return (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 8) + this.readUnsignedByte();
	}

	public String readOSRSString()
	{
		StringBuilder sb = new StringBuilder();

		for (; ; )
		{
			int ch = this.readUnsignedByte();

			if (ch == 0)
			{
				break;
			}

			if (ch >= 128 && ch < 160)
			{
				char var7 = OSRS_CHARACTERS[ch - 128];
				if (0 == var7)
				{
					var7 = '?';
				}

				ch = var7;
			}

			sb.append((char) ch);
		}
		return sb.toString();
	}

	// removed useless static initializer
}
