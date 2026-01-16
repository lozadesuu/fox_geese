package ru.vsu.fox_geese;

public class Fox extends Piece {

    public Fox(Position position) {
        super(position, 'F');
    }

    @Override
    public boolean canMove(Board board, Position to) {
        int fromRow = position.getRow();
        int fromCol = position.getCol();
        int toRow = to.getRow();
        int toCol = to.getCol();

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (toRow < 0 || toRow >= board.getSize() ||
                toCol < 0 || toCol >= board.getSize()) {
            return false;
        }

        if (!board.isValidCell(toRow, toCol)) {
            return false;
        }

        if (!board.isEmpty(toRow, toCol)) {
            return false;
        }

        if (Math.abs(rowDiff) + Math.abs(colDiff) == 1) {
            return isPathClear(board, fromRow, fromCol, toRow, toCol);
        }

        if ((Math.abs(rowDiff) == 2 && colDiff == 0) ||
                (Math.abs(colDiff) == 2 && rowDiff == 0)) {

            int middleRow = fromRow + rowDiff / 2;
            int middleCol = fromCol + colDiff / 2;

            if (middleRow < 0 || middleRow >= board.getSize() ||
                    middleCol < 0 || middleCol >= board.getSize()) {
                return false;
            }

            if (board.getCell(middleRow, middleCol) != 'G') {
                return false;
            }

            return isPathClear(board, fromRow, fromCol, toRow, toCol);
        }

        return false;
    }

    private boolean isPathClear(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) == 2 || Math.abs(colDiff) == 2) {
            int middleRow = fromRow + rowDiff / 2;
            int middleCol = fromCol + colDiff / 2;

            if (board.getCell(middleRow, middleCol) != 'G') {
                return false;
            }

            if (!board.isValidCell(middleRow, middleCol)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSurrounded(Board board) {
        int row = position.getRow();
        int col = position.getCol();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int blockedDirections = 0;

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (!board.isValidCell(newRow, newCol)) {
                blockedDirections++;
                continue;
            }

            char cell = board.getCell(newRow, newCol);
            if (cell == 'G') {
                blockedDirections++;
            }
        }

        return blockedDirections == 4;
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
