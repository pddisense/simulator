package io.lumos.core.types;

import com.google.common.primitives.Longs;

import java.util.Objects;

public final class LongDatum implements NumericDatum {
    private final long value;

    public static LongDatum ZERO = new LongDatum(0);

    LongDatum(long value) {
        this.value = value;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public byte asByte() {
        return (byte) value;
    }

    @Override
    public short asShort() {
        return (short) value;
    }

    @Override
    public int asInt() {
        return (int) value;
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
        return Long.toString(value);
    }

    @Override
    public byte[] asBytes() {
        return Longs.toByteArray(value);
    }

    @Override
    public Long get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.LONG;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LongDatum)) {
            return false;
        }
        return value == ((LongDatum) obj).value;
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
                return new LongDatum(Long.parseLong(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
