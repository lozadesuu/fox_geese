package ru.vsu.fox_geese;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();

        int geeseCount = ui.selectGeeseCount();
        int foxCount = ui.selectFoxCount();

        Game game = new Game(foxCount, geeseCount);
        game.start();
    }
}
