import java.awt.Graphics2D;
import java.util.List;

public interface GameState {
    // Handle a user click and return the resulting new GameState. May return
    // "this", if the move represents an invalid or partial move. Coordinates
    // are in range 0 to 1023.
    public GameState click(int x, int y);

    // Draw a graphic representation of the state. The Graphics2D object
    // provided is 1024x1024 pixels.
    public void draw(Graphics2D graphics);

    // Return the winner of a finished game, +1.0 for white, -1.0 for black,
    // 0.0 for a draw. For a game not yet finished, return either always 0.0 or
    // a value between +1.0 and -1.0, indicating an advantage for a player.
    public double evaluate();

    // Generate a list of all possible moves. The moves are themselves
    // GameState objects. Most of the rules are implented here.
    public List<GameState> generateMoves();

    // Return the player to make the next move, +1 for white, -1 for black.
    public int getPlayer();
}