package io.lumos.core.types;

import java.util.Objects;

public final class DoubleDatum implements NumericDatum {
    private final double value;

    public static DoubleDatum ZERO = new DoubleDatum(0);

    DoubleDatum(double value) {
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
        return (float) value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public String asString() {
        return Double.toString(value);
    }

    @Override
    public byte[] asBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.DOUBLE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DoubleDatum)) {
            return false;
        }
        return value == ((DoubleDatum) obj).value;
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
                return new DoubleDatum(Double.parseDouble(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
