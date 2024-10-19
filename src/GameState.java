import java.awt.Graphics2D;
import java.util.List;

public interface GameState {
    // Handle a user click and return the resulting new GameState. May return
    // "this", if the move represents an invalid or partial move.
    public GameState click(int id);

    // Draw a graphic representation of the state. The GameImage class provides
    // several useful methods for this.
    public GameImage draw();

    // Return the winner of a finished game, +1.0 for white, -1.0 for black,
    // 0.0 for a draw. For a game not yet finished, return either always 0.0 or
    // a value between +1.0 and -1.0, indicating an advantage for a player.
    public double evaluate();

    // Generate a list of all possible moves. The moves are themselves
    // GameState objects. Most of the rules are implemented here.
    public List<GameState> generateMoves();

    // Return the notation of the move that created this state. One may devise
    // one's own system of notation here.
    public String getNotation();

    // Return the player to make the next move, +1 for white, -1 for black.
    public int getPlayer();
}