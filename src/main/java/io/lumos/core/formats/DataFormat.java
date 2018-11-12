package io.lumos.core.formats;

import io.lumos.core.types.Schema;

import java.nio.file.Path;
import java.util.Map;

public interface DataFormat {
    /**
     * Return the name of this data format, which should be unique across all data formats.
     */
    String name();

    DataWriter newWriter(Path path, Schema schema, Map<String, String> options);

    DataReader newReader(Path path, Schema schema, Map<String, String> options);
}
