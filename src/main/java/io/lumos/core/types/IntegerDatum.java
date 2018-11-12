package io.lumos.core.types;

import com.google.common.primitives.Ints;

import java.util.Objects;

public final class IntegerDatum implements NumericDatum {
    private final int value;

    public static IntegerDatum ZERO = new IntegerDatum(0);

    IntegerDatum(int value) {
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
        return Integer.toString(value);
    }

    @Override
    public byte[] asBytes() {
        return Ints.toByteArray(value);
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.INTEGER;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntegerDatum)) {
            return false;
        }
        return value == ((IntegerDatum) obj).value;
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
                return new IntegerDatum(Integer.parseInt(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
