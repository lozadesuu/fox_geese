package ru.vsu.fox_geese;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame mainFrame;
    private static Game game;
    private static GamePanel gamePanel;
    private static JLabel statusLabel;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            runConsoleMode();
        } else {
            int choice = showModeSelection();
            if (choice == 0) {
                runConsoleMode();
            } else if (choice == 1) {
                runGUIMode();
            } else {
                System.out.println("Выход из программы.");
            }
        }
    }

    private static int showModeSelection() {
        try {
            String[] options = {"Консольный режим", "Графический режим", "Выход"};
            return JOptionPane.showOptionDialog(
                    null,
                    "Выберите режим запуска игры:",
                    "Режим игры",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]
            );
        } catch (HeadlessException e) {
            System.out.println("GUI недоступен. Запуск консольного режима...");
            return 0;
        }
    }

    private static void runConsoleMode() {
        ConsoleUI ui = new ConsoleUI();

        int geeseCount = ui.selectGeeseCount();
        int foxCount = ui.selectFoxCount();
        Game.GameMode gameMode = ui.selectGameMode();

        ConsoleGame consoleGame = new ConsoleGame(foxCount, geeseCount, gameMode);
        consoleGame.start();
    }

    private static void runGUIMode() {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Гуси и Лисы");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(600, 670);

        statusLabel = new JLabel("Начните новую игру", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainFrame.add(statusLabel, BorderLayout.NORTH);

        gamePanel = new GamePanel();
        mainFrame.add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton newGameButton = new JButton("Новая игра");
        JButton rulesButton = new JButton("Правила");

        newGameButton.addActionListener(e -> showNewGameDialog());
        rulesButton.addActionListener(e -> showRules());

        controlPanel.add(newGameButton);
        controlPanel.add(rulesButton);
        mainFrame.add(controlPanel, BorderLayout.SOUTH);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void showNewGameDialog() {
        JDialog dialog = new JDialog(mainFrame, "Новая игра", true);
        dialog.setLayout(new GridLayout(4, 1));
        dialog.setSize(400, 450);

        JPanel foxPanel = new JPanel();
        foxPanel.setBorder(BorderFactory.createTitledBorder("Количество лис"));
        ButtonGroup foxGroup = new ButtonGroup();
        JRadioButton oneFox = new JRadioButton("1 лиса", true);
        foxGroup.add(oneFox);
        foxPanel.add(oneFox);

        JPanel goosePanel = new JPanel();
        goosePanel.setBorder(BorderFactory.createTitledBorder("Количество гусей"));
        ButtonGroup gooseGroup = new ButtonGroup();
        JRadioButton thirteenGeese = new JRadioButton("13 гусей", true);
        JRadioButton seventeenGeese = new JRadioButton("17 гусей");
        gooseGroup.add(thirteenGeese);
        gooseGroup.add(seventeenGeese);
        goosePanel.add(thirteenGeese);
        goosePanel.add(seventeenGeese);

        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Режим игры"));
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButton pvp = new JRadioButton("Два игрока", true);
        JRadioButton playerFox = new JRadioButton("Я - лиса vs Бот - гуси");
        JRadioButton playerGoose = new JRadioButton("Я - гуси vs Бот - лиса");
        JRadioButton botVsBot = new JRadioButton("Бот vs Бот");
        modeGroup.add(pvp);
        modeGroup.add(playerFox);
        modeGroup.add(playerGoose);
        modeGroup.add(botVsBot);
        modePanel.add(pvp);
        modePanel.add(playerFox);
        modePanel.add(playerGoose);
        modePanel.add(botVsBot);

        JPanel buttonPanel = new JPanel();
        JButton start = new JButton("Начать");
        JButton cancel = new JButton("Отмена");

        start.addActionListener(e -> {
            int foxes = oneFox.isSelected() ? 1 : 2;
            int geese = thirteenGeese.isSelected() ? 13 : 17;
            Game.GameMode mode = pvp.isSelected() ? Game.GameMode.PLAYER_VS_PLAYER :
                    playerFox.isSelected() ? Game.GameMode.PLAYER_VS_BOT_GEESE :
                            playerGoose.isSelected() ? Game.GameMode.PLAYER_VS_BOT_FOX :
                                    Game.GameMode.BOT_VS_BOT;

            startNewGame(foxes, geese, mode);
            dialog.dispose();
        });

        cancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(start);
        buttonPanel.add(cancel);

        dialog.add(foxPanel);
        dialog.add(goosePanel);
        dialog.add(modePanel);
        dialog.add(buttonPanel);

        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private static void startNewGame(int foxCount, int gooseCount, Game.GameMode mode) {
        game = new Game(foxCount, gooseCount, mode);
        gamePanel.setGame(game);
        updateStatus();
        gamePanel.repaint();

        if (mode == Game.GameMode.PLAYER_VS_BOT_GEESE || mode == Game.GameMode.BOT_VS_BOT) {
            SwingUtilities.invokeLater(() -> {
                game.makeBotMoveForGeese();
                updateStatus();
                gamePanel.repaint();

                if (mode == Game.GameMode.BOT_VS_BOT) {
                    startBotVsBotGame();
                }
            });
        }
    }

    private static void startBotVsBotGame() {
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            if (game.getCurrentState() == Game.GameState.GEESE_WIN ||
                    game.getCurrentState() == Game.GameState.FOX_WIN) {
                timer.stop();
                return;
            }

            if (game.getCurrentState() == Game.GameState.FOX_TURN) {
                game.makeBotMoveForFoxes();
            } else if (game.getCurrentState() == Game.GameState.GEESE_TURN) {
                game.makeBotMoveForGeese();
            }

            updateStatus();
            gamePanel.repaint();
        });
        timer.start();
    }

    public static void updateStatus() {
        if (game == null) {
            statusLabel.setText("Начните новую игру");
            return;
        }

        String status = "";
        switch (game.getCurrentState()) {
            case GEESE_TURN:
                status = "Ход гусей";
                break;
            case FOX_TURN:
                status = "Ход лис";
                break;
            case GEESE_WIN:
                status = "Гуси победили!";
                break;
            case FOX_WIN:
                status = "Лисы победили!";
                break;
        }
        statusLabel.setText(status);
    }

    private static void showRules() {
        String rules = "ПРАВИЛА ИГРЫ:\n\n" +
                "1. Гуси ходят на 1 клетку вперед, влево или вправо\n" +
                "2. Лисы ходят на 1 клетку в любом направлении\n" +
                "3. Лисы могут прыгать через гусей, съедая их\n" +
                "4. Цель гусей - окружить лис\n" +
                "5. Цель лис - съесть достаточно гусей\n\n" +
                "УПРАВЛЕНИЕ:\n" +
                "- Кликните на фигуру, чтобы выбрать\n" +
                "- Кликните на пустую клетку, чтобы переместить";

        JOptionPane.showMessageDialog(mainFrame, rules, "Правила", JOptionPane.INFORMATION_MESSAGE);
    }
}
