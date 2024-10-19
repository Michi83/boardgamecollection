public class HumanAlgorithm implements Algorithm {
    private GameState move;
    private GameState state;

    public void click(int id) {
        move = state.click(id);
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