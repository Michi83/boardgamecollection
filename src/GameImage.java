// A GameImage is always 1024x1024 pixels even if the actual window size is
// different. It is subdivided into 64x64 tiles, each 16x16 pixels. GameState
// objects may use the following helpful methods:
// fillTile: Fills a tile with an image file. The image may extend into
// neighboring tiles to the right and to the bottom.
// addRegion: Defines a clickable region with an id.
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class GameImage extends BufferedImage {
    private List<int[]> regions;

    public GameImage() {
        super(1024, 1024, BufferedImage.TYPE_INT_RGB);
        regions = new ArrayList<int[]>();
    }

    public void addRegion(int id, int x, int y, int width, int height) {
        int[] region = new int[] { id, x, y, width, height };
        regions.add(region);
    }

    public void fillTile(int x, int y, String filename) {
        try {
            Image tile = ImageIO.read(new File("img/png/" + filename));
            Graphics2D graphics = createGraphics();
            graphics.drawImage(tile, 16 * x, 16 * y, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public int getClickedRegion(int x, int y) {
        for (int[] region : regions) {
            int id = region[0];
            int x0 = region[1];
            int y0 = region[2];
            int width = region[3];
            int height = region[4];
            if (x0 <= x && x < x0 + width && y0 <= y && y < y0 + height) {
                return id;
            }
        }
        return Integer.MIN_VALUE;
    }
}