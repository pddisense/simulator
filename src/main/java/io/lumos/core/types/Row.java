package io.lumos.core.types;

import com.google.common.base.MoreObjects;
import io.lumos.core.types.Datum;

import java.util.Arrays;

/**
 * A row of data, formed of one or several values represented as {@link Datum}'s.
 */
public final class Row {
    private final Datum[] values;

    /**
     * Constructor.
     *
     * @param values Values, one per column in the schema and in the same order.
     */
    public Row(Datum... values) {
        this.values = values;
    }

    /**
     * Return the value at a given position.
     *
     * @param i Position.
     */
    public Datum get(int i) {
        return values[i];
    }

    /**
     * Return the number of values forming this row.
     */
    public int length() {
        return values.length;
    }

    /**
     * Return this row as an array of raw values.
     */
    public Object[] toArray() {
        Object[] rawValues = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            rawValues[i] = values[i].get();
        }
        return rawValues;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Row)) {
            return false;
        }
        Row that = (Row) obj;
        return Arrays.equals(values, that.values);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper builder = MoreObjects.toStringHelper(this);
        for (Datum value : values) {
            builder.addValue(value);
        }
        return builder.toString();
    }
}
