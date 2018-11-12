package io.lumos.core.types;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Datum {
    boolean asBoolean();

    byte asByte();

    short asShort();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    String asString();

    byte[] asBytes();

    Object get();

    boolean isNull();

    DatumType getType();

    static NullDatum ofNull() {
        return NullDatum.INSTANCE;
    }

    static ByteDatum of(byte value) {
        return new ByteDatum(value);
    }

    static ShortDatum of(short value) {
        return new ShortDatum(value);
    }

    static IntegerDatum of(int value) {
        return new IntegerDatum(value);
    }

    static LongDatum of(long value) {
        return new LongDatum(value);
    }

    static FloatDatum of(float value) {
        return new FloatDatum(value);
    }

    static DoubleDatum of(double value) {
        return new DoubleDatum(value);
    }

    static StringDatum of(String value) {
        checkNotNull(value);
        if (value.isEmpty()) {
            return StringDatum.EMPTY;
        }
        return new StringDatum(value);
    }

    static BooleanDatum of(boolean value) {
        return value ? BooleanDatum.TRUE : BooleanDatum.FALSE;
    }

    static Datum of(Object value) {
        if (null == value) {
            return NullDatum.INSTANCE;
        } else if (value instanceof Byte) {
            return of((byte) value);
        } else if (value instanceof Short) {
            return of((short) value);
        } else if (value instanceof Integer) {
            return of((int) value);
        } else if (value instanceof Long) {
            return of((long) value);
        } else if (value instanceof Float) {
            return of((float) value);
        } else if (value instanceof Double) {
            return of((double) value);
        } else if (value instanceof Boolean) {
            return of((boolean) value);
        } else if (value instanceof String) {
            return of((String) value);
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
