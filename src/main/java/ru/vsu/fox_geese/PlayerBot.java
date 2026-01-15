package ru.vsu.fox_geese;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerBot {
    private Random random;

    public PlayerBot() {
        this.random = new Random();
    }

    public void makeGooseMove(Board board, List<Goose> geese) {
        List<MoveOption> allMoves = new ArrayList<>();

        for (Goose goose : geese) {
            if (!goose.isAlive()) {
                continue;
            }

            Position from = goose.getPosition();
            int[][] directions = {{-1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int newRow = from.getRow() + dir[0];
                int newCol = from.getCol() + dir[1];
                Position to = new Position(newRow, newCol);

                if (goose.canMove(board, to)) {
                    allMoves.add(new MoveOption(goose, null, from, to));
                }
            }
        }

        if (!allMoves.isEmpty()) {
            MoveOption chosenMove = allMoves.get(random.nextInt(allMoves.size()));
            board.setCell(chosenMove.from.getRow(), chosenMove.from.getCol(), '.');
            board.setCell(chosenMove.to.getRow(), chosenMove.to.getCol(), 'G');
            chosenMove.goose.setPosition(chosenMove.to);
        }
    }

    public void makeFoxMove(Board board, List<Fox> foxes, List<Goose> geese) {
        List<MoveOption> allMoves = new ArrayList<>();
        List<MoveOption> captureMoves = new ArrayList<>();

        for (Fox fox : foxes) {
            Position from = fox.getPosition();

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int newRow = from.getRow() + dir[0];
                int newCol = from.getCol() + dir[1];
                Position to = new Position(newRow, newCol);

                if (fox.canMove(board, to)) {
                    allMoves.add(new MoveOption(null, fox, from, to));
                }

                newRow = from.getRow() + dir[0] * 2;
                newCol = from.getCol() + dir[1] * 2;
                Position jumpTo = new Position(newRow, newCol);

                if (fox.canMove(board, jumpTo)) {
                    MoveOption move = new MoveOption(null, fox, from, jumpTo);
                    allMoves.add(move);
                    captureMoves.add(move);
                }
            }
        }

        List<MoveOption> movesToChooseFrom = captureMoves.isEmpty() ? allMoves : captureMoves;

        if (!movesToChooseFrom.isEmpty()) {
            MoveOption chosenMove = movesToChooseFrom.get(random.nextInt(movesToChooseFrom.size()));
            board.setCell(chosenMove.from.getRow(), chosenMove.from.getCol(), '.');
            board.setCell(chosenMove.to.getRow(), chosenMove.to.getCol(), 'F');
            Fox fox = chosenMove.fox;

            if (fox.isCapture(chosenMove.to)) {
                Position capturedPos = fox.getCapturedGoosePosition(chosenMove.to);
                board.setCell(capturedPos.getRow(), capturedPos.getCol(), '.');

                for (Goose goose : geese) {
                    if (goose.isAlive() && goose.getPosition().equals(capturedPos)) {
                        goose.capture();
                        break;
                    }
                }
            }

            fox.setPosition(chosenMove.to);
        }
    }

    private static class MoveOption {
        Goose goose;
        Fox fox;
        Position from;
        Position to;

        MoveOption(Goose goose, Fox fox, Position from, Position to) {
            this.goose = goose;
            this.fox = fox;
            this.from = from;
            this.to = to;
        }
    }
}
