package ru.vsu.fox_geese;

public class Goose extends Piece {

    public Goose(Position position) {
        super(position, 'G');
    }

    @Override
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
