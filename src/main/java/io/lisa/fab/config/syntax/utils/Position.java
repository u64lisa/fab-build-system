package io.lisa.fab.config.syntax.utils;

public record Position(int column, int line) {

    public static final Position EMPTY = new Position(0, 0);

    public Position copy() {
        return new Position(column, line);
    }
}
