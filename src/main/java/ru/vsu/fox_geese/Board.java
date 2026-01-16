package ru.vsu.fox_geese;

public class Board {
    private final char[][] grid;
    private static final int SIZE = 7;

    public Board() {
        grid = new char[SIZE][SIZE];
        initializeCrossBoard();
    }

    private void initializeCrossBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = 'X';
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 2; j <= 4; j++) {
                grid[i][j] = '.';
            }
        }

        for (int i = 2; i <= 4; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public void placeInitialPieces(int foxCount, int geese) {
        if (foxCount == 1) {
            grid[3][3] = 'F';
        } else {
            grid[3][2] = 'F';
            grid[3][4] = 'F';
        }

        if (geese == 13) {
            for (int j = 2; j <= 4; j++) {
                grid[5][j] = 'G';
                grid[6][j] = 'G';
            }

            for (int j = 0; j <= 6; j++) {
                if (grid[4][j] == '.') {
                    grid[4][j] = 'G';
                }
            }
        } else {
            for (int j = 2; j <= 4; j++) {
                grid[5][j] = 'G';
                grid[6][j] = 'G';
            }

            for (int j = 0; j <= 6; j++) {
                if (grid[4][j] == '.') {
                    grid[4][j] = 'G';
                }
            }

            for (int j = 0; j <= 6; j++) {
                if (grid[3][j] == '.') {
                    grid[3][j] = 'G';
                }
            }
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public char getCell(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return 'X';
        }
        return grid[row][col];
    }

    public void setCell(int row, int col, char value) {
        if (isValidCell(row, col)) {
            grid[row][col] = value;
        }
    }

    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }
        return grid[row][col] != 'X';
    }

    public boolean isEmpty(int row, int col) {
        if (!isValidCell(row, col)) {
            return false;
        }
        return grid[row][col] == '.';
    }

    public int getSize() {
        return SIZE;
    }
}
