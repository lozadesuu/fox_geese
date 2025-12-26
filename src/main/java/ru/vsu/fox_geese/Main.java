package ru.vsu.fox_geese;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();

        int geeseCount = ui.selectGeeseCount();
        int foxCount = ui.selectFoxCount();
        Game.GameMode gameMode = ui.selectGameMode();

        Game game = new Game(foxCount, geeseCount, gameMode);
        game.start();
    }
}
