package io.lumos.core.formats.csv;

import com.google.common.collect.ImmutableMap;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriterSettings;
import com.univocity.parsers.csv.UnescapedQuoteHandling;

import java.util.Map;

final class CsvOptions {
    private final Map<String, String> options;
    private final char delimiter;
    private final char quote;
    private final char escape;
    private final char comment;
    private final boolean ignoreLeadingWhiteSpace;
    private final boolean ignoreTrailingWhiteSpace;
    private final String nullValue;
    private final boolean header;
    private final boolean strict;
    private final int maxColumns;
    private final boolean quoteAll;
    private final boolean escapeQuotes;
    private final int maxCharsPerColumn;

    CsvOptions(Map<String, String> options) {
        this.options = ImmutableMap.copyOf(options);
        this.delimiter = getChar("delimiter", ',');
        this.quote = getChar("quote", '\"');
        this.escape = getChar("escape", '\\');
        this.comment = getChar("comment", '\u0000');
        this.ignoreLeadingWhiteSpace = getBool("ignoreLeadingWhiteSpace", false);
        this.ignoreTrailingWhiteSpace = getBool("ignoreTrailingWhiteSpace", false);
        this.nullValue = options.getOrDefault("nullValue", "");
        this.header = getBool("header", true);
        this.strict = getBool("strict", true);
        this.maxColumns = getInt("maxColumns", 20480);
        this.quoteAll = getBool("quoteAll", false);
        this.escapeQuotes = getBool("escapeQuotes", true);
        this.maxCharsPerColumn = getInt("maxCharsPerColumn", -1);
    }

    public boolean isStrict() {
        return strict;
    }

    public CsvWriterSettings asWriterSettings() {
        CsvWriterSettings settings = new CsvWriterSettings();
        CsvFormat format = settings.getFormat();
        format.setDelimiter(delimiter);
        format.setQuote(quote);
        format.setQuoteEscape(escape);
        format.setComment(comment);
        settings.setIgnoreLeadingWhitespaces(ignoreLeadingWhiteSpace);
        settings.setIgnoreTrailingWhitespaces(ignoreTrailingWhiteSpace);
        settings.setNullValue(nullValue);
        settings.setEmptyValue("\"\"");
        settings.setSkipEmptyLines(true);
        settings.setHeaderWritingEnabled(header);
        settings.setQuoteAllFields(quoteAll);
        settings.setQuoteEscapingEnabled(escapeQuotes);
        return settings;
    }

    public CsvParserSettings asParserSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        CsvFormat format = settings.getFormat();
        format.setDelimiter(delimiter);
        format.setQuote(quote);
        format.setQuoteEscape(escape);
        format.setComment(comment);
        settings.setIgnoreLeadingWhitespaces(ignoreLeadingWhiteSpace);
        settings.setIgnoreTrailingWhitespaces(ignoreTrailingWhiteSpace);
        settings.setReadInputOnSeparateThread(false);
        settings.setInputBufferSize(128);
        settings.setMaxColumns(maxColumns);
        settings.setNullValue(nullValue);
        settings.setEmptyValue("");
        settings.setHeaderExtractionEnabled(header);
        settings.setMaxCharsPerColumn(maxCharsPerColumn);
        settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.STOP_AT_DELIMITER);
        return settings;
    }

    private char getChar(String name, char defaultValue) {
        String value = options.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value.length() == 0) {
            return '\u0000';
        }
        if (value.length() == 1) {
            return value.charAt(0);
        }
        throw new RuntimeException(String.format("%s cannot be more than one character", name));
    }

    private int getInt(String name, int defaultValue) {
        String value = options.get(name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("$%s should be an integer. Found '%s'.", name, value));
        }
    }

    private boolean getBool(String name, boolean defaultValue) {
        String value = options.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        throw new RuntimeException(String.format("$%s should be a boolean. Found '%s'.", name, value));
    }
}
