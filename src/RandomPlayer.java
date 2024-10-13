import java.util.List;

public class RandomPlayer implements Player {
    private GameState state;
    private GameState move;

    public void click(int x, int y) {
    }

    public GameState getMove() {
        return move;
    }

    public void run() {
        List<GameState> moves = state.generateMoves();
        int index = (int)(Math.random() * moves.size());
        move = moves.get(index);
    }

    public void setState(GameState state) {
        move = null;
        this.state = state;
    }
}