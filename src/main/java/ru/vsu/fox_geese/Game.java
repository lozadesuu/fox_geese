package ru.vsu.fox_geese;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public enum GameState {
        GEESE_TURN,
        FOX_TURN,
        GEESE_WIN,
        FOX_WIN
    }

    private Board board;
    private List<Fox> foxes;
    private List<Goose> geese;
    private GameState currentState;
    private int geeseEaten;
    private int totalGeese;
    private int foxCount;

    public Game(int foxCount, int geeseCount) {
        this.foxCount = foxCount;
        this.totalGeese = geeseCount;
        this.board = new Board();
        this.foxes = new ArrayList<>();
        this.geese = new ArrayList<>();
        this.geeseEaten = 0;
        this.currentState = GameState.GEESE_TURN;
        initializeGame();
    }

    private void initializeGame() {
        board.placeInitialPieces(foxCount, totalGeese);
        if (foxCount == 1) {
            foxes.add(new Fox(new Position(3, 3)));
        } else {
            foxes.add(new Fox(new Position(3, 2)));
            foxes.add(new Fox(new Position(3, 4)));
        }
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getCell(i, j) == 'G') {
                    geese.add(new Goose(new Position(i, j)));
                }
            }
        }
    }


    public void start() {
        ConsoleUI ui = new ConsoleUI();

        while (!isGameOver()) {
            ui.displayBoard(board, currentState);

            if (currentState == GameState.GEESE_TURN) {
                ui.displayMessage("\n=== ХОД ГУСЕЙ ===");
                makeGooseMove(ui);
                currentState = GameState.FOX_TURN;
            } else if (currentState == GameState.FOX_TURN) {
                ui.displayMessage("\n=== ХОД ЛИСЫ ===");
                makeFoxMove(ui);
                currentState = GameState.GEESE_TURN;
            }
        }

        ui.displayBoard(board, currentState);
        ui.displayMessage("\n" + getWinnerMessage());
    }

    private void makeGooseMove(ConsoleUI ui) {
        while (true) {
            ui.displayMessage("Введите координаты гуся (строка столбец): ");
            Position from = ui.readPosition();

            if (board.getCell(from.getRow(), from.getCol()) != 'G') {
                ui.displayMessage("На этой клетке нет гуся!");
                continue;
            }

            Goose goose = findGooseAt(from);
            if (goose == null || !goose.isAlive()) {
                ui.displayMessage("Гусь не найден!");
                continue;
            }

            ui.displayMessage("Введите куда пойти (строка столбец): ");
            Position to = ui.readPosition();

            if (goose.canMove(board, to)) {
                board.setCell(from.getRow(), from.getCol(), '.');
                board.setCell(to.getRow(), to.getCol(), 'G');
                goose.setPosition(to);
                return;
            } else {
                ui.displayMessage("Недопустимый ход!");
            }
        }
    }

    private void makeFoxMove(ConsoleUI ui) {
        while (true) {
            ui.displayMessage("Введите координаты лисы (строка столбец): ");
            Position from = ui.readPosition();

            if (board.getCell(from.getRow(), from.getCol()) != 'F') {
                ui.displayMessage("На этой клетке нет лисы!");
                continue;
            }

            Fox fox = findFoxAt(from);
            if (fox == null) {
                ui.displayMessage("Лиса не найдена!");
                continue;
            }

            ui.displayMessage("Введите куда пойти (строка столбец): ");
            Position to = ui.readPosition();

            if (fox.canMove(board, to)) {
                board.setCell(from.getRow(), from.getCol(), '.');
                board.setCell(to.getRow(), to.getCol(), 'F');
                if (fox.isCapture(to)) {
                    Position capturedPos = fox.getCapturedGoosePosition(to);
                    board.setCell(capturedPos.getRow(), capturedPos.getCol(), '.');
                    Goose capturedGoose = findGooseAt(capturedPos);
                    if (capturedGoose != null) {
                        capturedGoose.capture();
                        geeseEaten++;
                        ui.displayMessage("Гусь съеден! Всего съедено: " + geeseEaten);
                    }
                }

                fox.setPosition(to);
                return;
            } else {
                ui.displayMessage("Недопустимый ход!");
            }
        }
    }

    public boolean isGameOver() {
        int winCount;
        if (totalGeese == 13) {
            winCount = 8;
        } else {
            winCount = 12;
        }

        if (geeseEaten >= winCount) {
            currentState = GameState.FOX_WIN;
            return true;
        }

        boolean allFoxesBlocked = true;
        for (Fox fox : foxes) {
            if (!isFoxBlocked(fox)) {
                allFoxesBlocked = false;
                break;
            }
        }

        if (allFoxesBlocked) {
            currentState = GameState.GEESE_WIN;
            return true;
        }

        return false;
    }

    private boolean isFoxBlocked(Fox fox) {
        Position pos = fox.getPosition();
        int row = pos.getRow();
        int col = pos.getCol();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Position adjacent = new Position(row + dir[0], col + dir[1]);
            if (fox.canMove(board, adjacent)) {
                return false;
            }

            Position jump = new Position(row + dir[0] * 2, col + dir[1] * 2);
            if (fox.canMove(board, jump)) {
                return false;
            }
        }

        return true;
    }

    private Goose findGooseAt(Position pos) {
        for (Goose goose : geese) {
            if (goose.isAlive() && goose.getPosition().equals(pos)) {
                return goose;
            }
        }
        return null;
    }

    private Fox findFoxAt(Position pos) {
        for (Fox fox : foxes) {
            if (fox.getPosition().equals(pos)) {
                return fox;
            }
        }
        return null;
    }

    private String getWinnerMessage() {
        if (currentState == GameState.FOX_WIN) {
            return "\uD83E\uDD8A ЛИСА ПОБЕДИЛА! Съедено гусей: " + geeseEaten;
        } else {
            return "\uD83E\uDEBF ГУСИ ПОБЕДИЛИ! Лиса заблокирована.";
        }
    }
}
