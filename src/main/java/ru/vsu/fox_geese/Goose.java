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

        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
            if (!board.isValidCell(to.getRow(), to.getCol())) {
                return false;
            }
            return board.isEmpty(to.getRow(), to.getCol());
        }

        return false;
    }
}
