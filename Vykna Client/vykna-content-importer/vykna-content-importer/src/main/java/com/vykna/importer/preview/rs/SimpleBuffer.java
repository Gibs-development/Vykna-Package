package com.vykna.importer.preview.rs;

public final class SimpleBuffer {
    private final byte[] data;
    public int pos;

    public SimpleBuffer(byte[] data) {
        this.data = data;
        this.pos = 0;
    }

    public void setOffset(int offset) {
        this.pos = offset;
    }

    public int readUnsignedByte() {
        return data[pos++] & 0xFF;
    }

    public byte readByte() {
        return data[pos++];
    }

    public int readUShort() {
        return (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public int readInt() {
        return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | (readUnsignedByte() << 8) | readUnsignedByte();
    }

    /**
     * Signed short-smart (this is your client's method421()).
     *
     *  - if next byte < 128:  value = u1 - 64
     *  - else:                value = u2 - 49152
     */
    public int readSmart() {
        int peek = data[pos] & 0xFF;
        if (peek < 128) {
            return readUnsignedByte() - 64;
        }
        return readUShort() - 49152;
    }

    /** Unsigned smart (0..32767-ish). Useful for some config formats. */
    public int readUSmart() {
        int peek = data[pos] & 0xFF;
        if (peek < 128) {
            return readUnsignedByte();
        }
        return readUShort() - 32768;
    }
}
