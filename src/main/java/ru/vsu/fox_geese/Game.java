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

    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_BOT_GEESE,
        PLAYER_VS_BOT_FOX,
        BOT_VS_BOT
    }

    private Board board;
    private List<Piece> pieces;
    private GameState currentState;
    private GameMode gameMode;
    private PlayerBot bot;
    private int geeseEaten;
    private int totalGeese;
    private int foxCount;

    public Game(int foxCount, int geeseCount, GameMode gameMode) {
        this.foxCount = foxCount;
        this.totalGeese = geeseCount;
        this.gameMode = gameMode;
        this.board = new Board();
        this.pieces = new ArrayList<>();
        this.geeseEaten = 0;

        if (gameMode == GameMode.PLAYER_VS_BOT_FOX) {
            this.currentState = GameState.GEESE_TURN;
        } else if (gameMode == GameMode.PLAYER_VS_BOT_GEESE) {
            this.currentState = GameState.GEESE_TURN;
        } else {
            this.currentState = GameState.GEESE_TURN;
        }

        if (gameMode != GameMode.PLAYER_VS_PLAYER) {
            this.bot = new PlayerBot();
        }

        initializeGame();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Board getBoard() {
        return board;
    }

    public int getGeeseEaten() {
        return geeseEaten;
    }

    private List<Fox> getFoxes() {
        List<Fox> foxes = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece instanceof Fox && piece.isAlive()) {
                foxes.add((Fox) piece);
            }
        }
        return foxes;
    }

    private List<Goose> getGeese() {
        List<Goose> geese = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece instanceof Goose && piece.isAlive()) {
                geese.add((Goose) piece);
            }
        }
        return geese;
    }

    private void initializeGame() {
        board.placeInitialPieces(foxCount, totalGeese);

        if (foxCount == 1) {
            pieces.add(new Fox(new Position(3, 3)));
        } else {
            pieces.add(new Fox(new Position(3, 2)));
            pieces.add(new Fox(new Position(3, 4)));
        }

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getCell(i, j) == 'G') {
                    pieces.add(new Goose(new Position(i, j)));
                }
            }
        }
    }

    public boolean makeGooseMove(Position from, Position to) {
        if (currentState != GameState.GEESE_TURN) {
            return false;
        }

        for (Piece piece : pieces) {
            if (piece instanceof Goose && piece.isAlive() && piece.getPosition().equals(from)) {
                if (piece.canMove(board, to)) {
                    board.setCell(from.getRow(), from.getCol(), '.');
                    board.setCell(to.getRow(), to.getCol(), 'G');
                    piece.setPosition(to);
                    currentState = GameState.FOX_TURN;

                    if (checkGeeseWin()) {
                        currentState = GameState.GEESE_WIN;
                        System.out.println("ГУСИ ПОБЕДИЛИ после своего хода!");
                    } else if (checkFoxWin()) {
                        currentState = GameState.FOX_WIN;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean makeFoxMove(Position from, Position to) {
        if (currentState != GameState.FOX_TURN) {
            return false;
        }

        for (Piece piece : pieces) {
            if (piece instanceof Fox && piece.getPosition().equals(from)) {
                Fox fox = (Fox) piece;
                if (fox.canMove(board, to)) {
                    board.setCell(from.getRow(), from.getCol(), '.');
                    board.setCell(to.getRow(), to.getCol(), 'F');

                    if (fox.isCapture(to)) {
                        Position capturedPos = fox.getCapturedGoosePosition(to);
                        board.setCell(capturedPos.getRow(), capturedPos.getCol(), '.');

                        for (Piece p : pieces) {
                            if (p instanceof Goose && p.isAlive() && p.getPosition().equals(capturedPos)) {
                                p.capture();
                                geeseEaten++;
                                System.out.println("Гусь съеден! Всего съедено: " + geeseEaten);
                                break;
                            }
                        }
                    }

                    fox.setPosition(to);
                    currentState = GameState.GEESE_TURN;

                    if (checkFoxWin()) {
                        currentState = GameState.FOX_WIN;
                        System.out.println("Лисы победили! Съедено гусей: " + geeseEaten);
                    } else if (checkGeeseWin()) {
                        currentState = GameState.GEESE_WIN;
                        System.out.println("Гуси победили! Лисы заблокированы.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void makeBotMoveForGeese() {
        if (bot != null && currentState == GameState.GEESE_TURN) {
            bot.makeGooseMove(board, getGeese());
            currentState = GameState.FOX_TURN;

            if (checkFoxWin()) {
                currentState = GameState.FOX_WIN;
                System.out.println("Лисы победили! Съедено гусей: " + geeseEaten);
            } else if (checkGeeseWin()) {
                currentState = GameState.GEESE_WIN;
                System.out.println("Гуси победили!");
            }
        }
    }

    public void makeBotMoveForFoxes() {
        if (bot != null && currentState == GameState.FOX_TURN) {
            bot.makeFoxMove(board, getFoxes(), getGeese());

            int alive = 0;
            for (Piece piece : pieces) {
                if (piece instanceof Goose && piece.isAlive()) {
                    alive++;
                }
            }
            geeseEaten = totalGeese - alive;

            currentState = GameState.GEESE_TURN;

            if (checkFoxWin()) {
                currentState = GameState.FOX_WIN;
                System.out.println("Лисы победили! Съедено гусей: " + geeseEaten);
            } else if (checkGeeseWin()) {
                currentState = GameState.GEESE_WIN;
                System.out.println("Гуси победили!");
            }
        }
    }

    private boolean checkFoxWin() {
        int geeseAlive = 0;
        for (Piece piece : pieces) {
            if (piece instanceof Goose && piece.isAlive()) {
                geeseAlive++;
            }
        }

        if (totalGeese == 13) {
            return (geeseEaten >= 8) || (geeseAlive < 8);
        } else {
            return (geeseEaten >= 12) || (geeseAlive < 12);
        }
    }

    private boolean checkGeeseWin() {
        List<Fox> foxes = getFoxes();

        for (Fox fox : foxes) {
            if (!fox.isSurrounded(board)) {
                boolean canMove = false;
                Position pos = fox.getPosition();
                int row = pos.getRow();
                int col = pos.getCol();

                int[][] moves = {
                        {row-1, col},
                        {row+1, col},
                        {row, col-1},
                        {row, col+1},
                        {row-2, col},
                        {row+2, col},
                        {row, col-2},
                        {row, col+2}
                };

                for (int[] move : moves) {
                    Position to = new Position(move[0], move[1]);
                    if (fox.canMove(board, to)) {
                        canMove = true;
                        break;
                    }
                }
                if (canMove) {
                    return false;
                }
            }
        }

        System.out.println("=== ВСЕ лисы заблокированы! ГУСИ ПОБЕДИЛИ! ===");
        return true;
    }

    public boolean makeMove(Position from, Position to) {
        Piece selectedPiece = null;
        for (Piece piece : pieces) {
            if (piece.isAlive() && piece.getPosition().equals(from)) {
                selectedPiece = piece;
                break;
            }
        }

        if (selectedPiece == null) {
            System.out.println("На клетке " + from + " нет фигуры");
            return false;
        }

        if (selectedPiece instanceof Fox && currentState != GameState.FOX_TURN) {
            System.out.println("Сейчас не ход лис!");
            return false;
        }
        if (selectedPiece instanceof Goose && currentState != GameState.GEESE_TURN) {
            System.out.println("Сейчас не ход гусей!");
            return false;
        }

        if (!selectedPiece.canMove(board, to)) {
            System.out.println(selectedPiece.getClass().getSimpleName() +
                    " не может сходить с " + from + " на " + to);
            return false;
        }

        board.setCell(from.getRow(), from.getCol(), '.');
        board.setCell(to.getRow(), to.getCol(), selectedPiece.getSymbol());

        if (selectedPiece instanceof Fox) {
            Fox fox = (Fox) selectedPiece;
            if (fox.isCapture(to)) {
                Position capturedPos = fox.getCapturedGoosePosition(to);
                board.setCell(capturedPos.getRow(), capturedPos.getCol(), '.');

                for (Piece piece : pieces) {
                    if (piece.isAlive() && piece.getPosition().equals(capturedPos)) {
                        piece.capture();
                        geeseEaten++;
                        System.out.println("Fox съела гуся! Всего: " + geeseEaten);
                        break;
                    }
                }
            }
        }

        selectedPiece.setPosition(to);

        currentState = (currentState == GameState.GEESE_TURN)
                ? GameState.FOX_TURN
                : GameState.GEESE_TURN;

        if (checkFoxWin()) {
            currentState = GameState.FOX_WIN;
            System.out.println("Лисы победили!");
        } else if (checkGeeseWin()) {
            currentState = GameState.GEESE_WIN;
            System.out.println("Гуси победили!");
        }

        return true;
    }

}
