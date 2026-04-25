import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SuperMarioGame extends JFrame {
    private GamePanel gamePanel;
    private boolean isRunning = true;

    public SuperMarioGame() {
        setTitle("Super Mario - Java Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        gamePanel = new GamePanel();
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        startGameLoop();
    }

    private void startGameLoop() {
        new Thread(() -> {
            while (isRunning) {
                gamePanel.update();
                gamePanel.repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperMarioGame());
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int GRAVITY = 1;
    private static final int TILE_SIZE = 40;

    private Player player;
    private ArrayList<Block> blocks;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int cameraX = 0;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);
        
        initGame();
    }

    private void initGame() {
        player = new Player(100, 400);
        blocks = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        score = 0;
        lives = 3;
        gameOver = false;
        gameWon = false;
        cameraX = 0;
        
        createLevel();
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(100, this);
        timer.start();
    }

    private void createLevel() {
        for (int i = 0; i < 100; i++) {
            blocks.add(new Block(i * TILE_SIZE, 520, Block.BlockType.GROUND));
        }
        
        for (int i = 3; i < 7; i++) {
            blocks.add(new Block(i * TILE_SIZE, 440, Block.BlockType.GROUND));
        }
        
        for (int i = 8; i < 12; i++) {
            blocks.add(new Block(i * TILE_SIZE, 360, Block.BlockType.GROUND));
        }
        
        blocks.add(new Block(15 * TILE_SIZE, 440, Block.BlockType.BRICK));
        blocks.add(new Block(16 * TILE_SIZE, 440, Block.BlockType.QUESTION));
        blocks.add(new Block(17 * TILE_SIZE, 440, Block.BlockType.BRICK));
        blocks.add(new Block(18 * TILE_SIZE, 440, Block.BlockType.QUESTION));
        
        for (int i = 22; i < 30; i++) {
            blocks.add(new Block(i * TILE_SIZE, 440, Block.BlockType.GROUND));
        }
        for (int i = 24; i < 28; i++) {
            blocks.add(new Block(i * TILE_SIZE, 360, Block.BlockType.GROUND));
        }
        for (int i = 25; i < 27; i++) {
            blocks.add(new Block(i * TILE_SIZE, 280, Block.BlockType.GROUND));
        }
        
        blocks.add(new Block(35 * TILE_SIZE, 440, Block.BlockType.PIPE));
        blocks.add(new Block(35 * TILE_SIZE, 480, Block.BlockType.PIPE));
        blocks.add(new Block(36 * TILE_SIZE, 440, Block.BlockType.PIPE));
        blocks.add(new Block(36 * TILE_SIZE, 480, Block.BlockType.PIPE));
        
        blocks.add(new Block(95 * TILE_SIZE, 480, Block.BlockType.FLAG));
        
        enemies.add(new Enemy(500, 480));
        enemies.add(new Enemy(800, 480));
        enemies.add(new Enemy(1200, 480));
        enemies.add(new Enemy(1600, 280));
        enemies.add(new Enemy(2000, 480));
        
        coins.add(new Coin(400, 400));
        coins.add(new Coin(450, 400));
        coins.add(new Coin(500, 400));
        coins.add(new Coin(640, 280));
        coins.add(new Coin(680, 280));
        coins.add(new Coin(720, 280));
        coins.add(new Coin(960, 320));
        coins.add(new Coin(1000, 320));
        coins.add(new Coin(1040, 320));
    }

    public void update() {
        if (gameOver || gameWon) {
            return;
        }

        player.velocityY += GRAVITY;
        
        player.update();
        
        if (player.y > PANEL_HEIGHT) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                player = new Player(100, 400);
                cameraX = 0;
            }
        }
        
        checkCollisions();
        
        for (Enemy enemy : enemies) {
            enemy.update(blocks, GRAVITY);
        }
        
        int targetCameraX = player.x - PANEL_WIDTH / 3;
        if (targetCameraX > 0) {
            cameraX = targetCameraX;
        }
    }

    private void checkCollisions() {
        player.onGround = false;
        
        for (Block block : blocks) {
            if (block.isFlag()) {
                if (player.intersects(block)) {
                    gameWon = true;
                    return;
                }
                continue;
            }
            
            if (player.intersects(block)) {
                int playerBottom = player.y + player.height;
                int playerTop = player.y;
                int playerRight = player.x + player.width;
                int playerLeft = player.x;
                
                int blockTop = block.y;
                int blockBottom = block.y + block.height;
                int blockLeft = block.x;
                int blockRight = block.x + block.width;
                
                int overlapTop = playerBottom - blockTop;
                int overlapBottom = blockBottom - playerTop;
                int overlapLeft = playerRight - blockLeft;
                int overlapRight = blockRight - playerLeft;
                
                int minOverlap = Math.min(Math.min(overlapTop, overlapBottom), 
                                          Math.min(overlapLeft, overlapRight));
                
                if (minOverlap == overlapTop && player.velocityY >= 0) {
                    player.y = blockTop - player.height;
                    player.velocityY = 0;
                    player.onGround = true;
                } else if (minOverlap == overlapBottom && player.velocityY < 0) {
                    player.y = blockBottom;
                    player.velocityY = 0;
                    if (block.isQuestion()) {
                        block.hit();
                        coins.add(new Coin(block.x, block.y - 40));
                    }
                } else if (minOverlap == overlapLeft && player.velocityX > 0) {
                    player.x = blockLeft - player.width;
                    player.velocityX = 0;
                } else if (minOverlap == overlapRight && player.velocityX < 0) {
                    player.x = blockRight;
                    player.velocityX = 0;
                }
            }
        }
        
        ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (player.intersects(enemy)) {
                if (player.velocityY > 0 && player.y + player.height - 10 < enemy.y + enemy.height / 2) {
                    enemiesToRemove.add(enemy);
                    player.velocityY = -12;
                    score += 100;
                } else {
                    lives--;
                    if (lives <= 0) {
                        gameOver = true;
                    } else {
                        player = new Player(100, 400);
                        cameraX = 0;
                    }
                }
            }
        }
        enemies.removeAll(enemiesToRemove);
        
        ArrayList<Coin> coinsToRemove = new ArrayList<>();
        for (Coin coin : coins) {
            if (player.intersects(coin)) {
                coinsToRemove.add(coin);
                score += 50;
            }
        }
        coins.removeAll(coinsToRemove);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        drawBackground(g2d);
        
        g2d.translate(-cameraX, 0);
        
        for (Block block : blocks) {
            block.draw(g2d);
        }
        
        for (Coin coin : coins) {
            coin.draw(g2d);
        }
        
        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }
        
        player.draw(g2d);
        
        g2d.translate(cameraX, 0);
        
        drawHUD(g2d);
        
        if (gameOver) {
            drawGameOver(g2d);
        }
        
        if (gameWon) {
            drawGameWon(g2d);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(new Color(135, 206, 235));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            int cloudX = (i * 200 - cameraX / 2) % (PANEL_WIDTH + 200);
            drawCloud(g2d, cloudX, 50 + i * 30);
        }
        
        g2d.setColor(new Color(34, 139, 34));
        for (int i = 0; i < 4; i++) {
            int hillX = (i * 300 - cameraX / 3) % (PANEL_WIDTH + 300);
            drawHill(g2d, hillX, 450, 150, 70);
        }
    }

    private void drawCloud(Graphics2D g2d, int x, int y) {
        g2d.fillOval(x, y, 50, 30);
        g2d.fillOval(x + 25, y - 10, 60, 40);
        g2d.fillOval(x + 60, y, 50, 30);
    }

    private void drawHill(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.fillArc(x, y - height, width, height * 2, 0, 180);
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Lives: " + lives, 150, 30);
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = g2d.getFontMetrics();
        String text = "GAME OVER!";
        int x = (PANEL_WIDTH - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, x, 250);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 25));
        String scoreText = "Final Score: " + score;
        metrics = g2d.getFontMetrics();
        x = (PANEL_WIDTH - metrics.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, 320);
        
        String restartText = "Press R to Restart";
        metrics = g2d.getFontMetrics();
        x = (PANEL_WIDTH - metrics.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, 380);
    }

    private void drawGameWon(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = g2d.getFontMetrics();
        String text = "YOU WIN!";
        int x = (PANEL_WIDTH - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, x, 250);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 25));
        String scoreText = "Final Score: " + score;
        metrics = g2d.getFontMetrics();
        x = (PANEL_WIDTH - metrics.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, 320);
        
        String restartText = "Press R to Restart";
        metrics = g2d.getFontMetrics();
        x = (PANEL_WIDTH - metrics.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, 380);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && !gameOver && !gameWon) {
            player.left = true;
        }
        if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && !gameOver && !gameWon) {
            player.right = true;
        }
        if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && !gameOver && !gameWon) {
            if (player.onGround) {
                player.velocityY = -15;
                player.onGround = false;
            }
        }
        if (key == KeyEvent.VK_R) {
            initGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            player.left = false;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            player.right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

class Player extends Rectangle {
    public int velocityX = 0;
    public int velocityY = 0;
    public boolean left = false;
    public boolean right = false;
    public boolean onGround = false;
    private static final int SPEED = 5;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 40;
    
    private int animFrame = 0;
    private int animCounter = 0;

    public Player(int x, int y) {
        super(x, y, WIDTH, HEIGHT);
    }

    public void update() {
        if (left) {
            velocityX = -SPEED;
        } else if (right) {
            velocityX = SPEED;
        } else {
            velocityX = 0;
        }
        
        x += velocityX;
        y += velocityY;
        
        if (velocityX != 0) {
            animCounter++;
            if (animCounter >= 8) {
                animCounter = 0;
                animFrame = (animFrame + 1) % 4;
            }
        } else {
            animFrame = 0;
        }
        
        if (x < 0) x = 0;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0));
        g2d.fillRect(x + 4, y, 24, 12);
        g2d.fillRect(x, y + 8, 32, 4);
        
        g2d.setColor(new Color(255, 223, 196));
        g2d.fillRect(x + 6, y + 12, 20, 12);
        
        g2d.setColor(Color.BLACK);
        if (velocityX >= 0) {
            g2d.fillRect(x + 18, y + 15, 4, 4);
        } else {
            g2d.fillRect(x + 10, y + 15, 4, 4);
        }
        
        g2d.setColor(new Color(178, 34, 34));
        g2d.fillRect(x + 4, y + 24, 24, 8);
        
        g2d.setColor(Color.BLUE);
        g2d.fillRect(x + 2, y + 32, 12, 8);
        g2d.fillRect(x + 18, y + 32, 12, 8);
        
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x, y + 36, 14, 4);
        g2d.fillRect(x + 18, y + 36, 14, 4);
    }
}

class Block extends Rectangle {
    public enum BlockType {
        GROUND, BRICK, QUESTION, PIPE, FLAG
    }
    
    private BlockType type;
    private boolean hit = false;
    private static final int SIZE = 40;

    public Block(int x, int y, BlockType type) {
        super(x, y, SIZE, SIZE);
        this.type = type;
    }

    public boolean isQuestion() {
        return type == BlockType.QUESTION;
    }

    public boolean isFlag() {
        return type == BlockType.FLAG;
    }

    public void hit() {
        hit = true;
        type = BlockType.BRICK;
    }

    public void draw(Graphics2D g2d) {
        switch (type) {
            case GROUND:
                g2d.setColor(new Color(139, 69, 19));
                g2d.fillRect(x, y, width, height);
                g2d.setColor(new Color(34, 139, 34));
                g2d.fillRect(x, y, width, 8);
                g2d.setColor(new Color(160, 82, 45));
                g2d.drawRect(x, y, width, height);
                break;
                
            case BRICK:
                g2d.setColor(new Color(205, 127, 50));
                g2d.fillRect(x, y, width, height);
                g2d.setColor(new Color(139, 69, 19));
                g2d.drawRect(x, y, width, height);
                g2d.drawLine(x, y + height / 2, x + width, y + height / 2);
                g2d.drawLine(x + width / 2, y, x + width / 2, y + height / 2);
                g2d.drawLine(x + width / 4, y + height / 2, x + width / 4, y + height);
                g2d.drawLine(x + width * 3 / 4, y + height / 2, x + width * 3 / 4, y + height);
                break;
                
            case QUESTION:
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillRect(x, y, width, height);
                g2d.setColor(new Color(255, 165, 0));
                g2d.drawRect(x, y, width, height);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("?", x + 14, y + 28);
                break;
                
            case PIPE:
                g2d.setColor(new Color(34, 139, 34));
                g2d.fillRect(x, y, width, height);
                g2d.setColor(new Color(0, 100, 0));
                g2d.fillRect(x + 5, y, 8, height);
                g2d.setColor(new Color(50, 205, 50));
                g2d.fillRect(x + width - 13, y, 8, height);
                g2d.setColor(new Color(0, 100, 0));
                g2d.drawRect(x, y, width, height);
                break;
                
            case FLAG:
                g2d.setColor(new Color(139, 69, 19));
                g2d.fillRect(x + width / 2 - 3, y, 6, height);
                
                g2d.setColor(Color.RED);
                int[] px = {x + width / 2 + 3, x + width / 2 + 30, x + width / 2 + 3};
                int[] py = {y, y + 15, y + 30};
                g2d.fillPolygon(px, py, 3);
                break;
        }
    }
}

class Enemy extends Rectangle {
    private int velocityX = -2;
    private int velocityY = 0;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private int animFrame = 0;
    private int animCounter = 0;
    private boolean onGround = false;

    public Enemy(int x, int y) {
        super(x, y, WIDTH, HEIGHT);
    }

    public void update(ArrayList<Block> blocks, int gravity) {
        velocityY += gravity;
        
        x += velocityX;
        y += velocityY;
        
        onGround = false;
        
        for (Block block : blocks) {
            if (block.isFlag()) continue;
            
            if (this.intersects(block)) {
                int enemyBottom = y + height;
                int enemyTop = y;
                int enemyRight = x + width;
                int enemyLeft = x;
                
                int blockTop = block.y;
                int blockBottom = block.y + block.height;
                int blockLeft = block.x;
                int blockRight = block.x + block.width;
                
                int overlapTop = enemyBottom - blockTop;
                int overlapBottom = blockBottom - enemyTop;
                int overlapLeft = enemyRight - blockLeft;
                int overlapRight = blockRight - enemyLeft;
                
                int minOverlap = Math.min(Math.min(overlapTop, overlapBottom), 
                                          Math.min(overlapLeft, overlapRight));
                
                if (minOverlap == overlapTop && velocityY >= 0) {
                    y = blockTop - height;
                    velocityY = 0;
                    onGround = true;
                } else if (minOverlap == overlapLeft && velocityX > 0) {
                    x = blockLeft - width;
                    velocityX = -velocityX;
                } else if (minOverlap == overlapRight && velocityX < 0) {
                    x = blockRight;
                    velocityX = -velocityX;
                }
            }
        }
        
        boolean hasGroundAhead = false;
        int checkX = velocityX > 0 ? x + width + 2 : x - 2;
        
        for (Block block : blocks) {
            if (block.isFlag()) continue;
            if (checkX >= block.x && checkX < block.x + block.width) {
                if (y + height + 5 >= block.y && y + height < block.y + 10) {
                    hasGroundAhead = true;
                    break;
                }
            }
        }
        
        if (onGround && !hasGroundAhead) {
            velocityX = -velocityX;
        }
        
        animCounter++;
        if (animCounter >= 10) {
            animCounter = 0;
            animFrame = (animFrame + 1) % 2;
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillOval(x, y, width, height - 8);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + 6, y + 6, 8, 8);
        g2d.fillOval(x + 18, y + 6, 8, 8);
        
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + 10, y + 10, 4, 4);
        g2d.fillOval(x + 18, y + 10, 4, 4);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + 8, y + 18, 16, 4);
        
        g2d.setColor(Color.BLACK);
        if (animFrame == 0) {
            g2d.fillOval(x + 2, y + height - 10, 10, 10);
            g2d.fillOval(x + 20, y + height - 10, 10, 10);
        } else {
            g2d.fillOval(x + 6, y + height - 10, 10, 10);
            g2d.fillOval(x + 16, y + height - 10, 10, 10);
        }
    }
}

class Coin extends Rectangle {
    private static final int SIZE = 24;
    private int animFrame = 0;
    private int animCounter = 0;
    private float scale = 1.0f;

    public Coin(int x, int y) {
        super(x, y, SIZE, SIZE);
    }

    public void draw(Graphics2D g2d) {
        animCounter++;
        if (animCounter >= 5) {
            animCounter = 0;
            animFrame = (animFrame + 1) % 4;
            scale = 1.0f - Math.abs(animFrame - 2) * 0.25f;
        }
        
        int drawWidth = (int) (width * scale);
        int drawX = x + (width - drawWidth) / 2;
        
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(drawX, y, drawWidth, height);
        
        g2d.setColor(new Color(255, 165, 0));
        g2d.drawOval(drawX, y, drawWidth, height);
        
        if (scale > 0.5f) {
            g2d.setColor(new Color(255, 165, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            int textX = drawX + drawWidth / 2 - 3;
            g2d.drawString("$", textX, y + 17);
        }
    }
}
