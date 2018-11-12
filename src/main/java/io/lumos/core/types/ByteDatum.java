package io.lumos.core.types;

import java.util.Objects;

public final class ByteDatum implements NumericDatum {
    private final byte value;

    public static ByteDatum ZERO = new ByteDatum((byte) 0);

    ByteDatum(byte value) {
        this.value = value;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public byte asByte() {
        return value;
    }

    @Override
    public short asShort() {
        return value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String asString() {
        return Byte.toString(value);
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{value};
    }

    @Override
    public Byte get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.BYTE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteDatum)) {
            return false;
        }
        return value == ((ByteDatum) obj).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return asString();
    }

    static final class Parser implements DatumType.Parser {
        @Override
        public Datum parse(String str) {
            if (str.isEmpty()) {
                return ZERO;
            }
            try {
                return new ByteDatum(Byte.parseByte(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
