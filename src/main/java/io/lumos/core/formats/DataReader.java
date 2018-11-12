package io.lumos.core.formats;

import io.lumos.core.types.Row;

import java.io.Closeable;
import java.io.IOException;

public interface DataReader extends Closeable {
    Row readNext() throws IOException;
}