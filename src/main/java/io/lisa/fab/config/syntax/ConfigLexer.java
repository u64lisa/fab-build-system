package io.lisa.fab.config.syntax;

import io.lisa.fab.config.syntax.utils.ErrorUtil;
import io.lisa.fab.config.syntax.utils.ISyntaxPos;
import io.lisa.fab.config.syntax.utils.Position;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConfigLexer {
    public static final GenericLexerContext<TokenType> LEXER;

    static {
        LEXER = new GenericLexerContext<TokenType>()
                // Whitespaces
                .addRule(TokenType.WHITESPACE, i -> i
                        .addMultiline("/*", "*/")
                        .addRegex("//[^\\r\\n]*")
                        .addRegex("[ \t\r\n]+")
                )

                .addRule(TokenType.L_CURLY, i -> i.addString("{"))
                .addRule(TokenType.R_CURLY, i -> i.addString("}"))
                .addRule(TokenType.DOT, i -> i.addString("."))
                .addRule(TokenType.EQUALS, i -> i.addString("="))

                // Atoms
                .addRule(TokenType.IDENTIFIER, i -> i.addRegex("[a-zA-Z_][a-zA-Z0-9_]*"))
                .addRule(TokenType.NUMBER, i-> i.addRegex("[0-9]*"))
                .addRule(TokenType.STRING, i -> i
                        .addMultiline("'", "\\", "'")
                        .addMultiline("\"", "\\", "\""))

                .addRule(TokenType.PROJECT_PROPERTY_KEY, i -> i.addString("project"))
                .addRule(TokenType.DEPEND_TAG, i -> i.addString("depend"))
                .addRule(TokenType.DEVELOPMENT_TAG, i -> i.addString("development"))
                .addRule(TokenType.PLUGIN_TAG, i -> i.addString("plugins"))
                .addRule(TokenType.MODULE, i -> i.addString("module"))

                .addRule(TokenType.RESOLVE, i -> i.addString("resolve"))
                .addRule(TokenType.FROM, i -> i.addString("from"))

                .toImmutable();
    }

    public List<Token> parse(String path, byte[] bytes) {
        List<Token> tokenList = parseKeepWhitespace(path, bytes);
        tokenList.removeIf(token -> token.tokenType == TokenType.WHITESPACE);
        return tokenList;
    }

    public List<Token> parseKeepWhitespace(String path, byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        List<Token> tokenList = new ArrayList<>();
        int offset = 0;
        int line = 0;
        int column = 0;
        int length = text.length();
        String input = text;

        while (offset < length) {
            Position startPos = new Position(column, line); // offset

            LexerToken<TokenType> lexerToken = LEXER.nextToken(input);
            if (lexerToken == null) {
                throw new RuntimeException(ErrorUtil.createFullError(
                        ISyntaxPos.of(path, startPos, startPos),
                        text,
                        "Could not parse token"
                ));
            }

            if (lexerToken.length + offset > length) {
                break;
            }

            for (int i = offset; i < offset + lexerToken.length; i++) {
                char c = text.charAt(i);

                if (c == '\n') {
                    line++;
                    column = 0;
                } else {
                    column += (c == '\t') ? 4 : 1;
                }
            }

            Position endPos = new Position(column, line); // offset + lexerToken.length
            tokenList.add(new Token(
                    lexerToken.type,
                    lexerToken.content,
                    ISyntaxPos.of(path, startPos, endPos)
            ));

            input = input.substring(lexerToken.length);
            offset += lexerToken.length;
        }

        return tokenList;
    }
}

