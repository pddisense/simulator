package io.lumos.core.formats.csv;

import io.lumos.core.formats.DataFormat;
import io.lumos.core.formats.DataReader;
import io.lumos.core.formats.DataWriter;
import io.lumos.core.types.Schema;

import java.nio.file.Path;
import java.util.Map;

public final class CsvDataFormat implements DataFormat {
    @Override
    public String name() {
        return "csv";
    }

    @Override
    public DataWriter newWriter(Path path, Schema schema, Map<String, String> options) {
        return new CsvDataWriter(path, schema, new CsvOptions(options));
    }

    @Override
    public DataReader newReader(Path path, Schema schema, Map<String, String> options) {
        return new CsvDataReader(path, schema, new CsvOptions(options));
    }
}
