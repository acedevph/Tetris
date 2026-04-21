//Tetris Game in Java using my py code
// @JellyAce-69 | acedevph

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Tetris extends JPanel implements ActionListener, KeyListener {

    // Board settings
    static final int COLS = 10, ROWS = 20, CELL = 30;
    static final int WIDTH = COLS * CELL + 150, HEIGHT = ROWS * CELL;

    // Shapes (same 7 tetrominoes as Python version)
    static final int[][][] SHAPES = {
        {{1,1,1,1}},                        // I
        {{1,1},{1,1}},                      // O
        {{1,1,1},{0,1,0}},                  // T
        {{1,1,1},{1,0,0}},                  // J
        {{1,1,1},{0,0,1}},                  // L
        {{1,1,0},{0,1,1}},                  // S
        {{0,1,1},{1,1,0}}                   // Z
    };

    static final Color[] COLORS = {
        new Color(0,   255, 255),  // I — cyan
        new Color(255, 255,   0),  // O — yellow
        new Color(160,   0, 255),  // T — purple
        new Color(0,    0,  255),  // J — blue
        new Color(255, 165,   0),  // L — orange
        new Color(0,   255,   0),  // S — green
        new Color(255,   0,   0)   // Z — red
    };

    // Grid — 0 means empty, stores color index+1
    int[][] grid = new int[ROWS][COLS];
    Color[][] gridColor = new Color[ROWS][COLS];

    // Current piece
    int[][] currentShape;
    Color currentColor;
    int shapeX, shapeY;

    // Next piece
    int[][] nextShape;
    Color nextColor;

    // Game state
    int score = 0, level = 1, lines = 0;
    boolean gameOver = false, paused = false;

    Random rand = new Random();
    Timer timer;

    // ─── Constructor ────────────────────────────────────────────
    public Tetris() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        spawnNext();
        spawnPiece();
        timer = new Timer(getSpeed(), this);
        timer.start();
    }

    // ─── Spawn next piece into the preview ──────────────────────
    void spawnNext() {
        int idx = rand.nextInt(SHAPES.length);
        nextShape = SHAPES[idx];
        nextColor = COLORS[idx];
    }

    // ─── Bring next piece onto the board ────────────────────────
    void spawnPiece() {
        currentShape = nextShape;
        currentColor = nextColor;
        shapeX = COLS / 2 - currentShape[0].length / 2;
        shapeY = 0;
        spawnNext();
        if (!canMove(currentShape, shapeX, shapeY)) {
            gameOver = true;
            timer.stop();
        }
    }

    // ─── Collision check (same logic as Python can_move) ────────
    boolean canMove(int[][] shape, int x, int y) {
        for (int i = 0; i < shape.length; i++)
            for (int j = 0; j < shape[i].length; j++)
                if (shape[i][j] != 0) {
                    int nx = x + j, ny = y + i;
                    if (nx < 0 || nx >= COLS || ny >= ROWS) return false;
                    if (ny >= 0 && grid[ny][nx] != 0)      return false;
                }
        return true;
    }

    // ─── Lock piece and clear lines (same logic as place_shape) ─
    void placePiece() {
        for (int i = 0; i < currentShape.length; i++)
            for (int j = 0; j < currentShape[i].length; j++)
                if (currentShape[i][j] != 0) {
                    gridColor[shapeY + i][shapeX + j] = currentColor;
                    grid[shapeY + i][shapeX + j] = 1;
                }
        clearLines();
        spawnPiece();
    }

    // ─── Clear completed lines, update score ────────────────────
    void clearLines() {
        int cleared = 0;
        for (int i = ROWS - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < COLS; j++)
                if (grid[i][j] == 0) { full = false; break; }
            if (full) {
                // Shift everything down
                for (int r = i; r > 0; r--) {
                    grid[r]      = grid[r - 1].clone();
                    gridColor[r] = gridColor[r - 1].clone();
                }
                grid[0]      = new int[COLS];
                gridColor[0] = new Color[COLS];
                cleared++;
                i++; // re-check this row
            }
        }
        if (cleared > 0) {
            // Scoring: 1=100, 2=300, 3=500, 4=800 (classic Tetris)
            int[] pts = {0, 100, 300, 500, 800};
            score += pts[Math.min(cleared, 4)] * level;
            lines += cleared;
            level = lines / 10 + 1;
            timer.setDelay(getSpeed());
        }
    }

    // ─── Rotate (same logic as Python rotate) ───────────────────
    int[][] rotate(int[][] shape) {
        int rows = shape.length, cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rotated[j][rows - 1 - i] = shape[i][j];
        return rotated;
    }

    // ─── Hard drop ──────────────────────────────────────────────
    void hardDrop() {
        while (canMove(currentShape, shapeX, shapeY + 1)) shapeY++;
        placePiece();
    }

    // ─── Ghost piece Y position ──────────────────────────────────
    int ghostY() {
        int gy = shapeY;
        while (canMove(currentShape, shapeX, gy + 1)) gy++;
        return gy;
    }

    // ─── Fall speed based on level ───────────────────────────────
    int getSpeed() {
        return Math.max(100, 500 - (level - 1) * 40);
    }

    // ─── Timer tick — auto fall ──────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver) {
            if (canMove(currentShape, shapeX, shapeY + 1)) shapeY++;
            else placePiece();
            repaint();
        }
    }

    // ─── Drawing ─────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid cells
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                if (gridColor[i][j] != null)
                    drawCell(g2, j, i, gridColor[i][j]);

        // Draw ghost piece
        int gy = ghostY();
        for (int i = 0; i < currentShape.length; i++)
            for (int j = 0; j < currentShape[i].length; j++)
                if (currentShape[i][j] != 0) {
                    g2.setColor(new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 60));
                    g2.fillRect((shapeX + j) * CELL, (gy + i) * CELL, CELL, CELL);
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.drawRect((shapeX + j) * CELL, (gy + i) * CELL, CELL - 1, CELL - 1);
                }

        // Draw current piece
        for (int i = 0; i < currentShape.length; i++)
            for (int j = 0; j < currentShape[i].length; j++)
                if (currentShape[i][j] != 0)
                    drawCell(g2, shapeX + j, shapeY + i, currentColor);

        // Draw grid lines
        g2.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i <= ROWS; i++) g2.drawLine(0, i * CELL, COLS * CELL, i * CELL);
        for (int j = 0; j <= COLS; j++) g2.drawLine(j * CELL, 0, j * CELL, ROWS * CELL);

        // Side panel
        int px = COLS * CELL + 10;
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(COLS * CELL, 0, 150, HEIGHT);
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawLine(COLS * CELL, 0, COLS * CELL, HEIGHT);

        // Score / Level / Lines
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.setColor(new Color(180, 180, 180));
        drawLabel(g2, px, 20,  "SCORE",  String.valueOf(score));
        drawLabel(g2, px, 70,  "LEVEL",  String.valueOf(level));
        drawLabel(g2, px, 120, "LINES",  String.valueOf(lines));

        // Next piece preview
        g2.setColor(new Color(180, 180, 180));
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString("NEXT", px + 30, 185);
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(px + 5, 195, 130, 80);
        int offX = px + 5 + (130 - nextShape[0].length * CELL) / 2;
        int offY = 195 + (80 - nextShape.length * CELL) / 2;
        for (int i = 0; i < nextShape.length; i++)
            for (int j = 0; j < nextShape[i].length; j++)
                if (nextShape[i][j] != 0) {
                    g2.setColor(nextColor);
                    g2.fillRect(offX + j * CELL, offY + i * CELL, CELL - 2, CELL - 2);
                    g2.setColor(nextColor.brighter());
                    g2.drawRect(offX + j * CELL, offY + i * CELL, CELL - 2, CELL - 2);
                }

        // Controls hint
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(100, 100, 100));
        String[] hints = {"← → Move", "↑ Rotate", "↓ Soft drop", "SPACE Hard drop", "P Pause"};
        for (int i = 0; i < hints.length; i++)
            g2.drawString(hints[i], px + 5, 300 + i * 16);

        // Pause overlay
        if (paused) drawOverlay(g2, "PAUSED", "Press P to resume");

        // Game over overlay
        if (gameOver) drawOverlay(g2, "GAME OVER", "Press R to restart");
    }

    void drawCell(Graphics2D g, int col, int row, Color color) {
        g.setColor(color);
        g.fillRect(col * CELL + 1, row * CELL + 1, CELL - 2, CELL - 2);
        g.setColor(color.brighter());
        g.drawLine(col * CELL + 1, row * CELL + 1, col * CELL + CELL - 2, row * CELL + 1);
        g.drawLine(col * CELL + 1, row * CELL + 1, col * CELL + 1, row * CELL + CELL - 2);
        g.setColor(color.darker());
        g.drawLine(col * CELL + CELL - 2, row * CELL + 1, col * CELL + CELL - 2, row * CELL + CELL - 2);
        g.drawLine(col * CELL + 1, row * CELL + CELL - 2, col * CELL + CELL - 2, row * CELL + CELL - 2);
    }

    void drawLabel(Graphics2D g, int x, int y, String label, String value) {
        g.setColor(new Color(120, 120, 120));
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString(label, x + 5, y);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString(value, x + 5, y + 18);
    }

    void drawOverlay(Graphics2D g, String title, String sub) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, HEIGHT / 2 - 50, COLS * CELL, 100);
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (COLS * CELL - fm.stringWidth(title)) / 2, HEIGHT / 2 - 10);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        g.setColor(new Color(180, 180, 180));
        g.drawString(sub, (COLS * CELL - fm.stringWidth(sub)) / 2, HEIGHT / 2 + 15);
    }

    // ─── Restart ─────────────────────────────────────────────────
    void restart() {
        grid      = new int[ROWS][COLS];
        gridColor = new Color[ROWS][COLS];
        score = 0; level = 1; lines = 0;
        gameOver = false; paused = false;
        spawnNext();
        spawnPiece();
        timer.setDelay(getSpeed());
        timer.start();
        repaint();
    }

    // ─── Key input (same controls as Python version) ─────────────
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) { if (e.getKeyCode() == KeyEvent.VK_R) restart(); return; }
        if (e.getKeyCode() == KeyEvent.VK_P) { paused = !paused; repaint(); return; }
        if (paused) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMove(currentShape, shapeX - 1, shapeY)) shapeX--;
                break;
            case KeyEvent.VK_RIGHT:
                if (canMove(currentShape, shapeX + 1, shapeY)) shapeX++;
                break;
            case KeyEvent.VK_DOWN:
                if (canMove(currentShape, shapeX, shapeY + 1)) shapeY++;
                break;
            case KeyEvent.VK_UP:
                int[][] rotated = rotate(currentShape);
                if (canMove(rotated, shapeX, shapeY)) currentShape = rotated;
                break;
            case KeyEvent.VK_SPACE:
                hardDrop();
                break;
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // ─── Main ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris — @JellyAce-69");
        Tetris game = new Tetris();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}