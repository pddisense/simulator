package io.lumos.core.formats;

import io.lumos.core.types.Row;

import java.io.Closeable;

public interface DataWriter extends Closeable {
    void write(Row row);
}
