package io.lisa.fab.logger;

import io.lisa.fab.logger.errors.ExceptionHighlighter;
import io.lisa.fab.logger.errors.LanguageException;

public class ImplLogger implements Logger {

    private static final Logger instance = new ImplLogger();

    private final boolean debug;
    private boolean failed = false;

    private final ExceptionHighlighter highlighter = new ExceptionHighlighter();

    ImplLogger() {
        debug = System.getProperty("lang.debug", "false").equals("true");
    }


    @Override
    public void out(Object text) {
        System.out.print(text);
        System.out.flush();
    }

    @Override
    public void warn(Object text) {
        System.out.println(text);
    }

    @Override
    public void fail(final String file, final String source, LanguageException languageException) {
        if (failed)
            return;

        failed = true;

        if (languageException.type().equals(LanguageException.Type.COMPILER)) {
            final StringBuilder errorMessage = appendFormattedDetails(new StringBuilder("\n")
                    .append("=".repeat(70))
                    .append("\n")
                    .append("Error while compiling file!")
                    .append("\n")
                    .append("\n")
                    .append("cause:  ")
                    .append(languageException.errorName())
                    .append("\n"), languageException);

            System.err.println(errorMessage);

            System.exit(0);
            return;
        }


        System.err.println(highlighter.createFullError(
                new PositionedError(languageException, file, source)));

        System.exit(0);
    }

    private StringBuilder appendFormattedDetails(final StringBuilder stringBuilder, final LanguageException languageException) {
        stringBuilder.append("details:  ");
        for (int i = 0; i < languageException.details().length; i++) {
            if (i == 0) {
                stringBuilder.append(languageException.details()[i]).append("\n");
                continue;
            }
            stringBuilder.append(" ".repeat(10)).append(languageException.details()[i]).append("\n");
        }
        return stringBuilder;
    }

    @Override
    public void printObject(Object text) {
        System.out.println(text);
    }

    @Override
    public void debug(String format) {
        if (debug) {
            System.out.println(format);
        }
    }

    @Override
    public boolean isDebugging() {
        return this.debug;
    }

    public static Logger getInstance() {
        return instance;
    }
}
