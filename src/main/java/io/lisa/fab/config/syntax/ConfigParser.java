package io.lisa.fab.config.syntax;


import io.lisa.fab.config.syntax.tree.ConfigTree;
import io.lisa.fab.config.syntax.utils.ErrorUtil;
import io.lisa.fab.config.syntax.utils.ISyntaxPos;
import io.lisa.fab.config.syntax.utils.Position;

import java.util.List;
import java.util.function.Supplier;

public class ConfigParser {

    private final ConfigTree configTree = new ConfigTree();

    private final String fileContent;

    private final Token[] tokens;
    private int index = 0;

    public ConfigParser(final String fileContent, List<Token> tokenList) {
        this.fileContent = fileContent;
        this.tokens = tokenList.toArray(new Token[0]);
    }

    public ConfigTree parse() {
        while (hasMore()) {

            switch (current().tokenType) {
                case PROJECT_PROPERTY_KEY -> {
                    advance();

                    tryMatchOrError(TokenType.DOT);
                    advance();

                    tryMatchOrError(TokenType.IDENTIFIER);

                    String key = current().value;
                    advance();

                    tryMatchOrError(TokenType.EQUALS);
                    advance();

                    tryMatchOrError(TokenType.STRING);
                    String value = current().value
                            .substring(1, current().value.length() - 1);

                    this.configTree.getProjectProperties().put(key, value);
                }
                case DEVELOPMENT_TAG -> {
                    advance();

                    tryMatchOrError(TokenType.L_CURLY);
                    advance();

                    while (current().tokenType != TokenType.R_CURLY) {
                        tryMatchOrError(TokenType.IDENTIFIER);

                        String owner = current().value
                                .substring(1, current().value.length() - 1);;
                        advance();

                        if (current().tokenType == TokenType.DOT) {
                            tryMatchOrError(TokenType.DOT);
                            advance();

                            tryMatchOrError(TokenType.IDENTIFIER);
                            String key = current().value
                                    .substring(1, current().value.length() - 1);;
                            advance();

                            tryMatchOrError(TokenType.EQUALS);
                            advance();

                            tryMatchOrError(TokenType.STRING);
                            String value = current().value
                                    .substring(1, current().value.length() - 1);;
                            advance();

                            this.configTree.getDevelopmentTag().put(owner, key, value);
                            continue;
                        }

                        tryMatchOrError(TokenType.EQUALS);
                        advance();

                        tryMatchOrError(TokenType.STRING);
                        String value = current().value;
                        advance();

                        this.configTree.getDevelopmentTag().put(owner, value);
                    }
                }
                case MODULE -> {
                    this.advance();

                    tryMatchOrError(TokenType.STRING);
                    String path = current().value
                            .substring(1, current().value.length() - 1);
                    advance();

                    tryMatchOrError(TokenType.NUMBER);
                    String priority = current().value;

                    // todo
                }
                case DEPEND_TAG -> {
                    advance();

                    tryMatchOrError(TokenType.L_CURLY);
                    advance();

                    while (current().tokenType != TokenType.R_CURLY) {

                        tryMatchOrError(TokenType.RESOLVE);
                        advance();

                        tryMatchOrError(TokenType.STRING);
                        String libraryDetails = current().value
                                .substring(1, current().value.length() - 1);;
                        advance();

                        tryMatchOrError(TokenType.FROM);
                        advance();

                        tryMatchOrError(TokenType.STRING);
                        String source = current().value
                                .substring(1, current().value.length() - 1);;

                        advance();

                        this.configTree.getDependTag().put(libraryDetails, source);
                    }
                }
                case PLUGIN_TAG -> {
                    advance();

                    tryMatchOrError(TokenType.L_CURLY);
                    advance();

                    while (current().tokenType != TokenType.R_CURLY) {

                        tryMatchOrError(TokenType.RESOLVE);
                        advance();

                        tryMatchOrError(TokenType.STRING);
                        String pluginDetails = current().value
                                .substring(1, current().value.length() - 1);;
                        advance();

                        tryMatchOrError(TokenType.FROM);
                        advance();

                        tryMatchOrError(TokenType.STRING);
                        String source = current().value
                                .substring(1, current().value.length() - 1);;

                        advance();

                        this.configTree.getPluginsTag().put(pluginDetails, source);
                    }
                }

                default -> throw createParseException(current().syntaxPosition, "Unexpected TokenType %s".formatted(current().tokenType));
            }

            advance();
        }

        return configTree;
    }

    Token current() {
        return tokens[index];
    }

    void advance() {
        this.index++;
    }

    boolean hasMore() {
        return index < tokens.length;
    }

    ParseException createParseException(String format, Object... args) {
        return createParseException(tokens == null ? null : current().syntaxPosition, format, args);
    }

    ParseException createParseException(ISyntaxPos syntaxPosition, String format, Object... args) {
        return createParseException(syntaxPosition.getPath(), syntaxPosition, format, args);
    }

    ParseException createParseException(String path, ISyntaxPos syntaxPosition, String format, Object... args) {
        String msg = String.format(format, args);

        StringBuilder sb = new StringBuilder();
        if (path == null) {
            sb.append("(?) ");
        } else {
            sb.append("(").append(path).append(") ");
        }

        if (syntaxPosition == null) {
            sb.append("(line: ?, column: ?): ").append(msg);
        } else {
            Position position = syntaxPosition.getStartPosition();
            sb.append("(line: ").append(position.line() + 1).append(", column: ").append(position.column() + 1).append("): ")
                    .append(ErrorUtil.createError(syntaxPosition, fileContent, msg));
        }

        return new ParseException(sb.toString());
    }

    void tryMatchOrError(TokenType tokenType) throws ParseException {
        tryMatchOrError(tokenType, () -> "Expected %s but got %s".formatted(tokenType, current().tokenType));
    }

    void tryMatchOrError(TokenType tokenType, Supplier<String> message) throws ParseException {
        if (current().tokenType != tokenType) {
            throw createParseException(current().syntaxPosition, message.get());
        }

    }


}
