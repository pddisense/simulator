package io.lumos.core.types;

import com.google.common.primitives.Ints;

import java.util.Objects;

public final class ShortDatum implements NumericDatum {
    private final short value;

    public static ShortDatum ZERO = new ShortDatum((short) 0);

    ShortDatum(short value) {
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
        return Short.toString(value);
    }

    @Override
    public byte[] asBytes() {
        return Ints.toByteArray(value);
    }

    @Override
    public Short get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.SHORT;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShortDatum)) {
            return false;
        }
        return value == ((ShortDatum) obj).value;
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
                return new ShortDatum(Short.parseShort(str));
            } catch (NumberFormatException e) {
                return NullDatum.INSTANCE;
            }
        }
    }
}
