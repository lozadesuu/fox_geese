package ru.vsu.fox_geese;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class GamePanel extends JPanel {
    private Game game;
    private Position selectedPos = null;

    public GamePanel() {
        setBackground(Color.LIGHT_GRAY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    public void setGame(Game game) {
        this.game = game;
        selectedPos = null;
        repaint();
    }

    private int getCellSize() {
        int width = getWidth();
        int height = getHeight();
        return Math.min(width, height) / 7;
    }

    private int getOffsetX() {
        int cellSize = getCellSize();
        return (getWidth() - cellSize * 7) / 2;
    }

    private int getOffsetY() {
        int cellSize = getCellSize();
        return (getHeight() - cellSize * 7) / 2;
    }

    private void handleMouseClick(int x, int y) {
        if (game == null) {
            return;
        }

        int cellSize = getCellSize();
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();

        int col = (x - offsetX) / cellSize;
        int row = (y - offsetY) / cellSize;

        if (row < 0 || row >= 7 || col < 0 || col >= 7) {
            return;
        }

        Board board = game.getBoard();

        if (selectedPos != null) {
            Position to = new Position(row, col);

            boolean moveMade = game.makeMove(selectedPos, to);

            if (moveMade) {
                selectedPos = null;
                Main.updateStatus();
                repaint();

                if (game.getCurrentState() != Game.GameState.GEESE_WIN &&
                        game.getCurrentState() != Game.GameState.FOX_WIN) {

                    Game.GameMode mode = game.getGameMode();

                    if (mode == Game.GameMode.PLAYER_VS_BOT_GEESE) {
                        if (game.getCurrentState() == Game.GameState.GEESE_TURN) {
                            SwingUtilities.invokeLater(() -> {
                                game.makeBotMoveForGeese();
                                Main.updateStatus();
                                repaint();
                            });
                        }
                    }
                    else if (mode == Game.GameMode.PLAYER_VS_BOT_FOX) {
                        if (game.getCurrentState() == Game.GameState.FOX_TURN) {
                            SwingUtilities.invokeLater(() -> {
                                game.makeBotMoveForFoxes();
                                Main.updateStatus();
                                repaint();
                            });
                        }
                    }
                }
            } else {
                selectedPos = null;
                repaint();
            }
            return;
        }

        char cell = board.getCell(row, col);

        if (cell == 'G') {
            if (game.getCurrentState() == Game.GameState.GEESE_TURN) {
                selectedPos = new Position(row, col);
                System.out.println("✅ Выбран гусь на " + selectedPos);
                repaint();
            } else {
                System.out.println("❌ Сейчас не ход гусей! Текущий ход: " + game.getCurrentState());
            }
        } else if (cell == 'F') {
            if (game.getCurrentState() == Game.GameState.FOX_TURN) {
                selectedPos = new Position(row, col);
                System.out.println("✅ Выбрана лиса на " + selectedPos);
                repaint();
            } else {
                System.out.println("❌ Сейчас не ход лис! Текущий ход: " + game.getCurrentState());
            }
        } else if (cell == '.') {
            System.out.println("Пустая клетка. Выберите фигуру для хода.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game == null) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "Нажмите 'Новая игра'";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g.drawString(msg, x, y);
            return;
        }

        Board board = game.getBoard();
        int cellSize = getCellSize();
        int offsetX = getOffsetX();
        int offsetY = getOffsetY();

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;

                if (board.isValidCell(row, col)) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(x, y, cellSize, cellSize);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);

                char cell = board.getCell(row, col);
                if (cell == 'G') {
                    drawGoose(g, x, y, cellSize);
                } else if (cell == 'F') {
                    drawFox(g, x, y, cellSize);
                }

                if (selectedPos != null &&
                        selectedPos.getRow() == row &&
                        selectedPos.getCol() == col) {
                    g.setColor(new Color(255, 255, 0, 100));
                    g.fillRect(x, y, cellSize, cellSize);
                }
            }
        }
    }

    private void drawGoose(Graphics g, int x, int y, int cellSize) {
        int padding = cellSize / 8;
        g.setColor(new Color(184, 184, 184, 100));
        g.fillOval(x + padding, y + padding, cellSize - padding * 2, cellSize - padding * 2);
        g.setColor(Color.BLACK);
        g.drawOval(x + padding, y + padding, cellSize - padding * 2, cellSize - padding * 2);
    }

    private void drawFox(Graphics g, int x, int y, int cellSize) {
        int padding = cellSize / 8;
        g.setColor(new Color(255, 165, 0));
        g.fillOval(x + padding, y + padding, cellSize - padding * 2, cellSize - padding * 2);
        g.setColor(Color.BLACK);
        g.drawOval(x + padding, y + padding, cellSize - padding * 2, cellSize - padding * 2);
    }
}
