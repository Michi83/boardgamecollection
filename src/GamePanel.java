import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private BufferedImage image;

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int height = getHeight();
        int width = getWidth();
        int size;
        int marginTop;
        int marginLeft;
        if (height > width) {
            size = width;
            marginTop = (height - size) / 2;
            marginLeft = 0;
        } else {
            size = height;
            marginTop = 0;
            marginLeft = (width - size) / 2;
        }
        graphics.setColor(new Color(0, 128, 0));
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(image, marginLeft, marginTop, size, size, null);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}