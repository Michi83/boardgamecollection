public class HumanAlgorithm implements Algorithm {
    private GameState move;
    private GameState state;

    public void click(int x, int y) {
        move = state.click(x, y);
    }

    public GameState getMove() {
        return move;
    }

    public void run() {
    }

    public void setState(GameState state) {
        move = null;
        this.state = state;
    }
}