import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameWindow extends JFrame implements MouseListener, Runnable {
    private static final int WHITE = 1;
    private static final int BLACK = -1;

    private Player black;
    private BufferedImage image;
    private GamePanel gamePanel;
    private Player player;
    private GameState state;
    private Player white;

    public GameWindow(GameState state, Player white, Player black) {
        super();

        this.state = state;
        this.white = white;
        this.black = black;

        gamePanel = new GamePanel();
        gamePanel.addMouseListener(this);
        gamePanel.setPreferredSize(new Dimension(512, 512));
        add(gamePanel);

        pack();
        setVisible(true);
        new Thread(this).start();
    }

    private void draw() {
        try {
            BufferedImage whitesmallking = ImageIO.read(new File(
                "img/png/whitesmallking.png"));
            BufferedImage blacksmallking = ImageIO.read(new File(
                "img/png/blacksmallking.png"));
            BufferedImage whitesmallpiece = ImageIO.read(new File(
                "img/png/whitesmallpiece.png"));
            BufferedImage blacksmallpiece = ImageIO.read(new File(
                "img/png/blacksmallpiece.png"));
            BufferedImage image = new BufferedImage(1024, 1024,
                BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            state.draw(graphics);
            // indicator symbols in the lower right corner
            if (state.generateMoves().size() == 0) {
                double score = state.evaluate();
                if (score > 0) {
                    graphics.drawImage(whitesmallking, 976, 976, null);
                } else if (score < 0) {
                    graphics.drawImage(blacksmallking, 976, 976, null);
                }
            } else {
                switch (state.getPlayer()) {
                    case WHITE:
                        graphics.drawImage(whitesmallpiece, 976, 976, null);
                        break;
                    case BLACK:
                        graphics.drawImage(blacksmallpiece, 976, 976, null);
                }
            }
            gamePanel.setImage(image);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void mouseClicked(MouseEvent event) {
        int height = gamePanel.getHeight();
        int width = gamePanel.getWidth();
        double size;
        double marginTop = 0;
        double marginLeft = 0;
        if (height > width) {
            size = width;
            marginTop = (height - width) / 2.0;
        } else {
            size = height;
            marginLeft = (width - height) / 2.0;
        }
        int x = (int)(1024 * (event.getX() - marginLeft) / size);
        int y = (int)(1024 * (event.getY() - marginTop) / size);
        player.click(x, y);
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void run() {
        try {
            draw();
            startPlayer();
            while (true) {
                GameState move = player.getMove();
                if (move != null) {
                    state = move;
                    draw();
                    startPlayer();
                }
                Thread.sleep(100);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void startPlayer() {
        if (state.generateMoves().size() == 0) {
            player = new IdlePlayer();
        } else if (state.getPlayer() == WHITE) {
            player = white;
        } else if (state.getPlayer() == BLACK) {
            player = black;
        }
        player.setState(state);
        new Thread(player).start();
    }
}