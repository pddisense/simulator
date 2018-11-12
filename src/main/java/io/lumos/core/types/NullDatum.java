package io.lumos.core.types;

public final class NullDatum implements Datum {
    public static NullDatum INSTANCE = new NullDatum();

    private NullDatum() {
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public byte[] asBytes() {
        return new byte[0];
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public DatumType getType() {
        return DatumType.NULL;
    }

    @Override
    public String toString() {
        return "<null>";
    }

    static final class Parser implements DatumType.Parser {
        @Override
        public Datum parse(String str) {
            return INSTANCE;
        }
    }
}
