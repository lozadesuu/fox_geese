package ru.vsu.fox_geese;

public class Goose {
    private Position position;
    private boolean isAlive;

    public Goose(Position position) {
        this.position = position;
        this.isAlive = true;
    }

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

    public boolean canMove(Board board, Position to) {
        if (!isAlive) return false;

        int rowDiff = to.getRow() - position.getRow();
        int colDiff = to.getCol() - position.getCol();

        if (Math.abs(rowDiff) + Math.abs(colDiff) != 1) {
            return false;
        }

        if (rowDiff > 0) {
            return false;
        }

        if (!board.isValidCell(to.getRow(), to.getCol())) {
            return false;
        }

        return board.isEmpty(to.getRow(), to.getCol());
    }
}
