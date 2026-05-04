import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

public class Game2048 extends JFrame implements KeyListener {

    int[][] board = new int[4][4];
    JLabel[][] cells = new JLabel[4][4];
    Random rand = new Random();

    int score = 0;
    int highScore = 0;

    public Game2048() {
        setTitle("2048 Game");
        setSize(420, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 🔥 MAIN PANEL (THIS FIXES GRID VISIBILITY)
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 10, 10)); // spacing
        panel.setBackground(new Color(187, 173, 160)); // board color
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font("Arial", Font.BOLD, 28);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cells[i][j] = new JLabel("", SwingConstants.CENTER);
                cells[i][j].setFont(font);
                cells[i][j].setOpaque(true);

                // empty tile background
                cells[i][j].setBackground(new Color(205, 193, 180));

                panel.add(cells[i][j]);
            }
        }

        add(panel);

        loadHighScore();

        addKeyListener(this);
        setFocusable(true);

        spawn();
        spawn();
        updateBoard();

        setVisible(true);
    }

    void spawn() {
        int r, c;
        do {
            r = rand.nextInt(4);
            c = rand.nextInt(4);
        } while (board[r][c] != 0);

        board[r][c] = rand.nextInt(10) < 9 ? 2 : 4;
    }

    void updateBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = board[i][j];
                cells[i][j].setText(val == 0 ? "" : String.valueOf(val));
                cells[i][j].setBackground(getColor(val));
            }
        }

        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }

        setTitle("Score: " + score + " | High Score: " + highScore);
    }

    Color getColor(int value) {
        switch (value) {
            case 2: return new Color(238, 228, 218);
            case 4: return new Color(237, 224, 200);
            case 8: return new Color(242, 177, 121);
            case 16: return new Color(245, 149, 99);
            case 32: return new Color(246, 124, 95);
            case 64: return new Color(246, 94, 59);
            case 128: return new Color(237, 207, 114);
            case 256: return new Color(237, 204, 97);
            case 512: return new Color(237, 200, 80);
            case 1024: return new Color(237, 197, 63);
            case 2048: return new Color(237, 194, 46);
            default: return new Color(205, 193, 180);
        }
    }

    void moveLeft() {
        for (int i = 0; i < 4; i++) {
            int[] newRow = new int[4];
            int idx = 0;

            for (int j = 0; j < 4; j++) {
                if (board[i][j] != 0) {
                    if (idx > 0 && newRow[idx - 1] == board[i][j]) {
                        newRow[idx - 1] *= 2;
                        score += newRow[idx - 1];
                    } else {
                        newRow[idx++] = board[i][j];
                    }
                }
            }
            board[i] = newRow;
        }
    }

    void rotate() {
        int[][] temp = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                temp[j][3 - i] = board[i][j];
        board = temp;
    }

    void move(int times) {
        for (int i = 0; i < times; i++) rotate();

        moveLeft();

        for (int i = 0; i < (4 - times) % 4; i++) rotate();

        spawn();
        updateBoard();

        if (hasWon()) {
            JOptionPane.showMessageDialog(this, "You reached 2048! 🎉");
            restartGame();
            return;
        }

        if (isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score);
            restartGame();
        }
    }

    boolean hasWon() {
        for (int[] row : board)
            for (int val : row)
                if (val == 2048)
                    return true;
        return false;
    }

    boolean isGameOver() {
        for (int[] row : board)
            for (int val : row)
                if (val == 0) return false;
        return true;
    }

    void restartGame() {
        board = new int[4][4];
        score = 0;
        spawn();
        spawn();
        updateBoard();
    }

    void saveHighScore() {
        try (PrintWriter out = new PrintWriter("highscore.txt")) {
            out.println(highScore);
        } catch (Exception e) {}
    }

    void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (Exception e) {
            highScore = 0;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) move(0);
        if (e.getKeyCode() == KeyEvent.VK_UP) move(3);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) move(2);
        if (e.getKeyCode() == KeyEvent.VK_DOWN) move(1);
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Game2048();
    }
}