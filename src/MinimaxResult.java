public class MinimaxResult {
    public double score;
    public GameState move;

    public MinimaxResult(double score, GameState move) {
        this.score = score;
        this.move = move;
    }
}