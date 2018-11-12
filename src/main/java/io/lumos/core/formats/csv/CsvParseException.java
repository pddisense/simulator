package io.lumos.core.formats.csv;

import java.io.IOException;

public final class CsvParseException extends IOException {
    private final long recordNumber;

    CsvParseException(long recordNumber, String message) {
        super(message);
        this.recordNumber = recordNumber;
    }

    CsvParseException(long recordNumber, Throwable e) {
        super(e);
        this.recordNumber = recordNumber;
    }

    public long getRecordNumber() {
        return recordNumber;
    }
}
