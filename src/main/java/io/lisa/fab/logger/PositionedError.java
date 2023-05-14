package io.lisa.fab.logger;

import io.lisa.fab.logger.errors.LanguageException;

public record PositionedError(LanguageException languageException, String file, String source) {

}
