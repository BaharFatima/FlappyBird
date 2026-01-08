import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Screen
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    // Bird (slower physics)
    private int birdX = 80;
    private int birdY = HEIGHT / 2;
    private int birdSize = 45;
    private int velocity = 0;
    private static final int GRAVITY = 1;
    private static final int JUMP = -9;

    // Pipes (slower)
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 190;
    private static final int PIPE_SPEED = 1;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Random random = new Random();

    // Background
    private int cloudX1 = 60, cloudX2 = 220, cloudX3 = 360;
    private int groundOffset = 0;
    private static final int GROUND_Y = 500;

    // Game state
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;

    // Assets
    private Image birdImage;
    private FancyButton restartButton;

    // Pipe wrapper (for scoring)
    private static class Pipe {
        Rectangle rect;
        boolean scored = false;
        Pipe(Rectangle r) { rect = r; }
    }

    public FlappyBird() {
        JFrame frame = new JFrame("Flappy Bird – Calm Aesthetic");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        birdImage = new ImageIcon("player.png").getImage();

        restartButton = new FancyButton("Restart");
        restartButton.setBounds(WIDTH / 2 - 60, HEIGHT / 2, 120, 42);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);

        frame.add(this);
        frame.setVisible(true);

        addPipePair();
        timer = new Timer(25, this); // ⏱ slower update loop
        timer.start();
    }

    private void addPipePair() {
        int h = 140 + random.nextInt(180);
        pipes.add(new Pipe(new Rectangle(WIDTH, 0, PIPE_WIDTH, h)));
        pipes.add(new Pipe(new Rectangle(WIDTH, h + PIPE_GAP, PIPE_WIDTH, HEIGHT)));
    }

    private void restartGame() {
        pipes.clear();
        birdY = HEIGHT / 2;
        velocity = 0;
        score = 0;
        gameOver = false;
        restartButton.setVisible(false);
        addPipePair();
        timer.start();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sky
        g2.setPaint(new GradientPaint(
                0, 0, new Color(135, 205, 255),
                0, HEIGHT, new Color(240, 250, 255)
        ));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Clouds
        drawCloud(g2, cloudX1, 90);
        drawCloud(g2, cloudX2, 150);
        drawCloud(g2, cloudX3, 60);

        // Buildings
        for (int i = 0; i < WIDTH; i += 60) {
            int h = 90 + (i % 3) * 35;
            drawBuilding(g2, i, GROUND_Y - h, 55, h);
        }

        // Ground
        g2.setColor(new Color(95, 175, 95));
        g2.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        drawGrass(g2);
        drawTreesAndFlowers(g2);

        // Pipes
        g2.setColor(new Color(70, 170, 100));
        for (Pipe p : pipes) {
            g2.fillRoundRect(p.rect.x, p.rect.y, p.rect.width, p.rect.height, 22, 22);
        }

        // Bird (no rotation)
        g2.drawImage(birdImage, birdX, birdY, birdSize, birdSize, this);

        // Score
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 35);

        if (gameOver) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
            g2.setColor(Color.RED);
            g2.drawString("GAME OVER", 85, HEIGHT / 2 - 60);
        }
    }

    /* ---------- Background helpers ---------- */

    private void drawCloud(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillOval(x, y, 70, 40);
        g2.fillOval(x + 25, y - 18, 70, 50);
        g2.fillOval(x + 50, y, 70, 40);
    }

    private void drawBuilding(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(75, 85, 105));
        g2.fillRect(x, y, w, h);

        // Windows
        g2.setColor(new Color(255, 220, 160, 180));
        for (int i = y + 12; i < y + h - 20; i += 22) {
            g2.fillRect(x + 10, i, 10, 10);
            g2.fillRect(x + 30, i, 10, 10);
        }

        // Balcony
        g2.setColor(new Color(120, 120, 135));
        g2.fillRect(x + 5, y + h / 2, w - 10, 6);
    }

    private void drawGrass(Graphics2D g2) {
        g2.setColor(new Color(70, 145, 70));
        for (int i = -groundOffset; i < WIDTH; i += 10) {
            g2.drawLine(i, GROUND_Y, i + 2, GROUND_Y - 8);
        }
    }

    private void drawTreesAndFlowers(Graphics2D g2) {
        for (int i = 0; i < WIDTH; i += 90) {
            g2.setColor(new Color(120, 75, 45));
            g2.fillRect(i + 22, GROUND_Y - 28, 8, 28);

            g2.setColor(new Color(65, 150, 80));
            g2.fillOval(i + 5, GROUND_Y - 55, 40, 30);

            g2.setColor(Color.PINK);
            g2.fillOval(i + 55, GROUND_Y - 6, 6, 6);
            g2.setColor(Color.YELLOW);
            g2.fillOval(i + 65, GROUND_Y - 6, 6, 6);
        }
    }

    /* ---------- Game loop ---------- */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            velocity += GRAVITY;
            birdY += velocity;

            cloudX1--; cloudX2--; cloudX3--;
            if (cloudX1 < -150) cloudX1 = WIDTH;
            if (cloudX2 < -150) cloudX2 = WIDTH + 120;
            if (cloudX3 < -150) cloudX3 = WIDTH + 240;

            groundOffset = (groundOffset + 1) % 10;

            Rectangle birdRect = new Rectangle(birdX, birdY, birdSize, birdSize);

            for (Pipe p : pipes) {
                p.rect.x -= PIPE_SPEED;

                if (!p.scored && p.rect.y > 0 && p.rect.x + PIPE_WIDTH < birdX) {
                    p.scored = true;
                    score++;
                }

                if (p.rect.intersects(birdRect)) {
                    endGame();
                }
            }

            // ONLY screen bounds cause loss
            if (birdY < 0 || birdY + birdSize > HEIGHT) {
                endGame();
            }

            if (pipes.get(0).rect.x + PIPE_WIDTH < 0) {
                pipes.remove(0);
                pipes.remove(0);
                addPipePair();
            }
        }
        repaint();
    }

    private void endGame() {
        gameOver = true;
        timer.stop();
        restartButton.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocity = JUMP;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FlappyBird();
    }
}

/* -------- Fancy Glass Button -------- */
class FancyButton extends JButton {
    FancyButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(255, 255, 255, 90));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

        g2.setColor(new Color(255, 255, 255, 160));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);

        super.paintComponent(g);
    }
}
