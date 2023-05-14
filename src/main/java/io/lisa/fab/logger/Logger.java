package io.lisa.fab.logger;

import io.lisa.fab.logger.errors.LanguageException;

public interface Logger {

    void out(Object text);

    void warn(Object text);

    void fail(final String file, final String source, LanguageException languageException);

    void printObject(Object text);

    void debug(String format);

    boolean isDebugging();
}
