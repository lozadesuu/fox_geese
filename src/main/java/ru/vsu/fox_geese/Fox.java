package ru.vsu.fox_geese;

public class Fox {
    private Position position;

    public Fox(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public boolean canMove(Board board, Position to) {
        int rowDiff = to.getRow() - position.getRow();
        int colDiff = to.getCol() - position.getCol();

        if (Math.abs(rowDiff) + Math.abs(colDiff) == 1) {
            return board.isEmpty(to.getRow(), to.getCol());
        }

        if (Math.abs(rowDiff) + Math.abs(colDiff) == 2) {
            if (rowDiff == 0 || colDiff == 0) {
                int middleRow = (position.getRow() + to.getRow()) / 2;
                int middleCol = (position.getCol() + to.getCol()) / 2;

                return board.getCell(middleRow, middleCol) == 'G' &&
                        board.isEmpty(to.getRow(), to.getCol());
            }
        }

        return false;
    }

    public boolean isCapture(Position to) {
        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        return rowDiff + colDiff == 2;
    }

    public Position getCapturedGoosePosition(Position to) {
        int middleRow = (position.getRow() + to.getRow()) / 2;
        int middleCol = (position.getCol() + to.getCol()) / 2;
        return new Position(middleRow, middleCol);
    }
}
