package io.lumos.core.types;

import java.util.Locale;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StringDatum implements Datum {
    private final String value;

    public static StringDatum EMPTY = new StringDatum("");

    StringDatum(String value) {
        this.value = checkNotNull(value);
    }

    @Override
    public boolean asBoolean() {
        String lower = value.toLowerCase(Locale.ROOT);
        return "true".equals(lower) || "yes".equals(lower) || "1".equals(lower);
    }

    @Override
    public byte asByte() {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public short asShort() {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int asInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public float asFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public byte[] asBytes() {
        return value.getBytes();
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DatumType getType() {
        return DatumType.STRING;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringDatum)) {
            return false;
        }
        return value.equals(((StringDatum) obj).value);
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
                return EMPTY;
            }
            return new StringDatum(str);
        }
    }
}
