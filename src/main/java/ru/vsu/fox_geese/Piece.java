package ru.vsu.fox_geese;

import ru.vsu.fox_geese.Board;
import ru.vsu.fox_geese.Position;

public abstract class Piece {
    protected Position position;
    protected boolean isAlive;
    protected char symbol;

    public Piece(Position position, char symbol) {
        this.position = position;
        this.isAlive = true;
        this.symbol = symbol;
    }

    public abstract boolean canMove(Board board, Position to);

    // Общие методы для всех фигур
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void capture() {
        this.isAlive = false;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol + " at " + position;
    }
}
