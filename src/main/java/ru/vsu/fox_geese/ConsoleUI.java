package ru.vsu.fox_geese;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    public void displayBoard(Board board, Game.GameState state) {
        char[][] grid = board.getGrid();
        int size = board.getSize();

        boolean flipDisplay;
        if (state == Game.GameState.FOX_TURN || state == Game.GameState.FOX_WIN) {
            flipDisplay = true;
        } else {
            flipDisplay = false;
        }

        if (flipDisplay) {
            System.out.println("\n  6  5  4  3  2  1  0   (вид со стороны лисы)");
        } else {
            System.out.println("\n  0  1  2  3  4  5  6   (вид со стороны гусей)");
        }
        System.out.println();

        for (int i = 0; i < size; i++) {
            int displayRow;
            if (flipDisplay) {
                displayRow = size - 1 - i;
            } else {
                displayRow = i;
            }

            System.out.print(displayRow + " ");

            for (int j = 0; j < size; j++) {
                int displayCol;
                if (flipDisplay) {
                    displayCol = size - 1 - j;
                } else {
                    displayCol = j;
                }

                char cell = grid[displayRow][displayCol];

                switch (cell) {
                    case 'F':
                        System.out.print("\uD83E\uDD8A ");
                        break;
                    case 'G':
                        System.out.print("\uD83E\uDEBF ");
                        break;
                    case '.':
                        System.out.print("·  ");
                        break;
                    case 'X':
                        System.out.print("   ");
                        break;
                }
            }
            System.out.println();
        }

        System.out.println();

        if (flipDisplay) {
            System.out.println("Доска перевёрнута! Координаты вводите как обычно");
        }
    }


    public Position readPosition() {
        try {
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            return new Position(row, col);
        } catch (Exception e) {
            scanner.nextLine();
            return new Position(-1, -1);
        }
    }

    public int selectGeeseCount() {
        System.out.println("=== ИГРА 'ЛИСА И ГУСИ' ===\n");
        System.out.println("Выберите количество гусей:");
        System.out.println("1) 13 гусей (легче для лисы)");
        System.out.println("2) 17 гусей (сбалансированная игра)");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            return 13;
        } else {
            return 17;
        }
    }

    public int selectFoxCount() {
        System.out.println("\nВыберите количество лис:");
        System.out.println("1) Одна лиса (классический вариант)");
        System.out.println("2) Две лисы (сложнее для гусей)");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();

        if (choice == 2) {
            return 2;
        } else {
            return 1;
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
