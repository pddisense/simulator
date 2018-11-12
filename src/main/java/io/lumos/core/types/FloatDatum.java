package io.lumos.core.types;

import java.util.Objects;

public final class FloatDatum implements NumericDatum {
    private final float value;

    public static FloatDatum ZERO = new FloatDatum(0);

    FloatDatum(float value) {
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
        return (long) value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        // It sure doesn't look very nice, but it seems to be the only way to preserve the exact
        // same number when converting a float into a double.
        // For example, new Float(3.14).doubleValue() == 3.140000104904175.
        // https://stackoverflow.com/questions/916081/convert-float-to-double-without-losing-precision
        return Double.parseDouble(Float.toString(value));
    }

    @Override
    public String asString() {
        return Float.toString(value);
    }

    @Override
    public byte[] asBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.FLOAT;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatDatum)) {
            return false;
        }
        return value == ((FloatDatum) obj).value;
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
                return new FloatDatum(Float.parseFloat(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
