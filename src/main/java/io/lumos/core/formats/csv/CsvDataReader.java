package io.lumos.core.formats.csv;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import io.lumos.core.formats.DataReader;
import io.lumos.core.types.Row;
import io.lumos.core.types.Schema;
import io.lumos.core.types.Datum;
import io.lumos.core.types.NullDatum;

import javax.annotation.concurrent.NotThreadSafe;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

@NotThreadSafe
public final class CsvDataReader implements DataReader {
    private final Path path;
    private final Schema schema;
    private final CsvParser parser;
    private final CsvOptions options;
    private final AtomicLong recordNumber = new AtomicLong();

    CsvDataReader(Path path, Schema schema, CsvOptions options) {
        this.path = checkNotNull(path);
        this.schema = checkNotNull(schema);
        this.options = checkNotNull(options);
        this.parser = new CsvParser(options.asParserSettings());
    }

    @Override
    public Row readNext() throws CsvParseException {
        long no = recordNumber.incrementAndGet();
        if (no == 1) {
            // If parsing the first record, indicate to the parser to start a new iterator-style
            // parsing. We do it here and not in the constructor because this method may throw
            // exceptions.
            try {
                parser.beginParsing(path.toFile());
            } catch (Throwable e) {
                throw new CsvParseException(0, e);
            }
        }
        Record record;
        try {
            record = parser.parseNextRecord();
        } catch (IllegalArgumentException e) {
            throw new CsvParseException(no, e);
        }
        if (record == null) {
            return null;
        }
        return toRow(no, record);
    }

    @Override
    public void close() {
        parser.stopParsing();
    }

    private Row toRow(long no, Record record) throws CsvParseException {
        Datum[] values = new Datum[schema.size()];
        for (int i = 0; i < schema.size(); ++i) {
            Schema.Column column = schema.get(i);
            String str = record.getString(column.getName());
            Datum value = column.getType().parser().parse(str);
            if (value == null) {
                if (options.isStrict()) {
                    throw new CsvParseException(no, String.format("Invalid %s: %s", column.getType().name(), str));
                }
                value = NullDatum.INSTANCE;
            }
            values[i] = value;
        }
        return new Row(values);
    }
}
