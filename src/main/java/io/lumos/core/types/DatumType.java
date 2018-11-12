package io.lumos.core.types;

/**
 * Describe a datum type.
 */
public enum DatumType {
    NULL(new NullDatum.Parser()),
    BOOLEAN(new BooleanDatum.Parser()),
    BYTE(new ByteDatum.Parser()),
    SHORT(new ShortDatum.Parser()),
    INTEGER(new IntegerDatum.Parser()),
    LONG(new LongDatum.Parser()),
    FLOAT(new FloatDatum.Parser()),
    DOUBLE(new DoubleDatum.Parser()),
    STRING(new StringDatum.Parser());

    private final Parser parser;

    DatumType(Parser parser) {
        this.parser = parser;
    }

    public Parser parser() {
        return parser;
    }

    public interface Parser {
        /**
         * Parse a string into a datum of some target type. It may return a null datum if the
         * string does not represent a valid value for the target type, but it should not throw
         * any exception.
         *
         * @param str String to parse.
         */
        Datum parse(String str);
    }
}
