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

    private Algorithm black;
    private GameImage image;
    private GamePanel gamePanel;
    private Algorithm algorithm;
    private GameState state;
    private Algorithm white;

    public GameWindow(String name, GameState state, Algorithm white,
            Algorithm black) {
        super(name);

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
        image = state.draw();
        // indicator symbols in the lower right corner
        if (state.generateMoves().size() == 0) {
            double score = state.evaluate();
            if (score > 0) {
                image.fillTile(61, 61, "whitesmallking.png");
            } else if (score < 0) {
                image.fillTile(61, 61, "blacksmallking.png");
            } else {
                image.fillTile(61, 61, "neutralsmallpiece.png");
            }
        } else {
            switch (state.getPlayer()) {
            case WHITE:
                image.fillTile(61, 61, "whitesmallpiece.png");
                break;
            case BLACK:
                image.fillTile(61, 61, "blacksmallpiece.png");
                break;
            }
        }
        gamePanel.setImage(image);
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
        int x = (int)(64 * (event.getX() - marginLeft) / size);
        int y = (int)(64 * (event.getY() - marginTop) / size);
        int id = image.getClickedRegion(x, y);
        algorithm.click(id);
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
            startAlgorithm();
            while (true) {
                GameState move = algorithm.getMove();
                if (move != null) {
                    state = move;
                    draw();
                    startAlgorithm();
                }
                Thread.sleep(100);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void startAlgorithm() {
        if (state.generateMoves().size() == 0) {
            algorithm = new IdleAlgorithm();
        } else if (state.getPlayer() == WHITE) {
            algorithm = white;
        } else if (state.getPlayer() == BLACK) {
            algorithm = black;
        }
        algorithm.setState(state);
        new Thread(algorithm).start();
    }
}