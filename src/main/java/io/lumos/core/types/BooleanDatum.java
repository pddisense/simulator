package io.lumos.core.types;

import java.util.Locale;
import java.util.Objects;

/**
 *
 * Note: {@link Object#equals} is not implemented as the default implementation using the identity
 * comparison (==) is enough because there are only two possible instances of BooleanDatum
 * (TRUE and FALSE).
 */
public final class BooleanDatum implements Datum {
    private final boolean value;

    public static final BooleanDatum TRUE = new BooleanDatum(true);
    public static final BooleanDatum FALSE = new BooleanDatum(false);

    private BooleanDatum(boolean value) {
        this.value = value;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public byte asByte() {
        return (byte) (value ? 1 : 0);
    }

    @Override
    public short asShort() {
        return (short) (value ? 1 : 0);
    }

    @Override
    public int asInt() {
        return value ? 1 : 0;
    }

    @Override
    public long asLong() {
        return value ? 1 : 0;
    }

    @Override
    public float asFloat() {
        return value ? 1 : 0;
    }

    @Override
    public double asDouble() {
        return value ? 1 : 0;
    }

    @Override
    public String asString() {
        return value ? "true" : "false";
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{asByte()};
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.BOOLEAN;
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
            switch (str.toLowerCase(Locale.ROOT)) {
                case "true":
                case "yes":
                case "1":
                    return TRUE;
                case "false":
                case "no":
                case "0":
                case "":
                    return FALSE;
                default:
                    return NullDatum.INSTANCE;
            }
        }
    }
}
