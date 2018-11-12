package io.lumos.core.formats.csv;

import com.univocity.parsers.csv.CsvWriter;
import io.lumos.core.formats.DataWriter;
import io.lumos.core.types.Row;
import io.lumos.core.types.Schema;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

@NotThreadSafe
public final class CsvDataWriter implements DataWriter {
    private final Schema schema;
    private final CsvWriter writer;
    private final String[] buffer;
    private boolean started = false;

    CsvDataWriter(Path path, Schema schema, CsvOptions options) {
        this.schema = checkNotNull(schema);
        this.buffer = new String[schema.size()];
        this.writer = new CsvWriter(path.toFile(), options.asWriterSettings());
    }

    @Override
    public void write(Row row) {
        if (!started) {
            writer.writeHeaders(schema.getHeaders());
            started = true;
        }
        for (int i = 0; i < schema.size(); ++i) {
            buffer[i] = row.get(i).asString();
        }
        writer.writeRow(buffer);
    }

    @Override
    public void close() throws IOException {
        try {
            writer.close();
        } catch (IllegalStateException e) {
            throw new IOException(e);
        }
    }
}
