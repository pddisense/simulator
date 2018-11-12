package io.lumos.core.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.lumos.core.types.DatumType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A schema defining the structure of the {@link Row}s inside a dataset. A schema is formed of
 * one or several columns, each column having essentially a name and a data type. The order of the
 * columns is generally important, e.g., the rows will follow the same order.
 *
 * <p>We only supported flat schemas, i.e., nested structures, arrays and maps are not supported.
 */
public final class Schema {
    private final List<Column> columns;

    /**
     * Constructor.
     *
     * @param columns Columns forming this schema.
     */
    public Schema(List<Column> columns) {
        this.columns = ImmutableList.copyOf(columns);
    }

    /**
     * Constructor.
     *
     * @param columns Columns forming this schema.
     */
    public Schema(Column... columns) {
        this(ImmutableList.copyOf(columns));
    }

    /**
     * Create a schema for a given class. This uses the constructor with the highest number of
     * arguments to derive the "schema" of the class. Using the constructor arguments is by no
     * means perfect, as for example the naming may differ between the parameter names and the
     * getter method names, but it notably allows to use the same code for both Java POJOs and
     * Scala case classes.
     * <p>
     * Calling this method is an extensive operation, as it uses the reflection API, so you should
     * avoid calling this method multiple times for the same class.
     *
     * @param clazz JVM class.
     */
    public static Schema from(Class<?> clazz) {
        Constructor<?>[] ctors = clazz.getConstructors();
        if (ctors.length == 0) {
            throw new IllegalArgumentException(String.format("Unsupported class %s", clazz.getName()));
        }
        Constructor<?> ctor = ctors[0];
        for (Constructor<?> candidate : ctors) {
            if (candidate.getParameterCount() > ctor.getParameterCount()) {
                ctor = candidate;
            }
        }
        ImmutableList.Builder<Column> columns = ImmutableList.builderWithExpectedSize(ctor.getParameterCount());
        for (Parameter param : ctor.getParameters()) {
            DatumType type;
            if (param.getType() == Byte.class || param.getType() == byte.class) {
                type = DatumType.BYTE;
            } else if (param.getType() == Short.class || param.getType() == short.class) {
                type = DatumType.SHORT;
            } else if (param.getType() == Integer.class || param.getType() == int.class) {
                type = DatumType.INTEGER;
            } else if (param.getType() == Long.class || param.getType() == long.class) {
                type = DatumType.LONG;
            } else if (param.getType() == Float.class || param.getType() == float.class) {
                type = DatumType.FLOAT;
            } else if (param.getType() == Double.class || param.getType() == double.class) {
                type = DatumType.DOUBLE;
            } else if (param.getType() == String.class) {
                type = DatumType.STRING;
            } else if (param.getType() == Boolean.class || param.getType() == boolean.class) {
                type = DatumType.BOOLEAN;
            } else {
                throw new IllegalArgumentException(String.format(
                        "Unsupported parameter %s::%s of type %s",
                        clazz.getName(),
                        param.getName(),
                        param.getType().getName()));
            }
            columns.add(new Schema.Column(param.getName(), type));
        }
        return new Schema(columns.build());
    }

    /**
     * Return the column at a given position.
     *
     * @param i Position.
     */
    public Column get(int i) {
        return columns.get(i);
    }

    /**
     * Return the number of columns forming this schema.
     */
    public int size() {
        return columns.size();
    }

    /**
     * Return the names of the columns.
     */
    public List<String> getHeaders() {
        List<String> headers = Lists.newArrayListWithCapacity(columns.size());
        for (Column column : columns) {
            headers.add(column.getName());
        }
        return headers;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Schema)) {
            return false;
        }
        Schema that = (Schema) obj;
        return columns.equals(that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns);
    }

    /**
     * A column, associating name with a data type.
     */
    public static final class Column {
        private final String name;
        private final DatumType type;

        /**
         * Constructor.
         *
         * @param name Column name.
         * @param type Data type.
         */
        public Column(String name, DatumType type) {
            this.name = checkNotNull(name);
            this.type = checkNotNull(type);
        }

        /**
         * Return this column's name.
         */
        public String getName() {
            return name;
        }

        /**
         * Return this column's data type.
         */
        public DatumType getType() {
            return type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Column)) {
                return false;
            }
            Column that = (Column) obj;
            return Objects.equals(name, that.name)
                    && Objects.equals(type, that.type);
        }
    }
}