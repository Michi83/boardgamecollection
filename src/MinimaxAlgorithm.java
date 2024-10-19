import java.util.Collections;
import java.util.List;

public class MinimaxAlgorithm implements Algorithm {
    private int maxTime;
    private GameState move;
    private int nodes;
    private GameState state;
    private long timeStarted;

    public MinimaxAlgorithm(int maxTime) {
        this.maxTime = maxTime;
    }

    public void click(int id) {
    }

    public GameState getMove() {
        return move;
    }

    private MinimaxResult minimax(GameState state, int depth) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        return minimax(state, depth, alpha, beta);
    }

    private MinimaxResult minimax(GameState state, int depth, double alpha,
            double beta) {
        nodes++;
        if (System.currentTimeMillis() - timeStarted > 1000L * maxTime) {
            throw new MinimaxTimeoutException();
        }
        if (depth == 0) {
            double score = state.getPlayer() * state.evaluate();
            return new MinimaxResult(score, null);
        }
        List<GameState> moves = state.generateMoves();
        if (moves.size() == 0) {
            double score = state.getPlayer() * state.evaluate();
            return new MinimaxResult(score, null);
        }
        Collections.shuffle(moves);
        double topScore = Double.NEGATIVE_INFINITY;
        GameState topMove = null;
        for (GameState move : moves) {
            double score = -minimax(move, depth - 1, -beta, -alpha).score;
            if (score > topScore) {
                topScore = score;
                topMove = move;
                if (score > alpha) {
                    alpha = score;
                    if (score >= beta) {
                        break;
                    }
                }
            }
        }
        return new MinimaxResult(topScore, topMove);
    }

    public void run() {
        timeStarted = System.currentTimeMillis();
        GameState move = null;
        System.out.println("ply  score  time   nodes");
        for (int depth = 1; true; depth++) {
            try {
                nodes = 0;
                long time1 = System.currentTimeMillis();
                MinimaxResult result = minimax(state, depth);
                long time2 = System.currentTimeMillis();
                double score = result.score;
                move = result.move;
                long dtime = time2 - time1;
                System.out.printf("%3d %6.3f %5d %7d\n", depth, score, dtime,
                    nodes);
            } catch (MinimaxTimeoutException exception) {
                System.out.println();
                this.move = move;
                return;
            }
        }
    }

    public void setState(GameState state) {
        move = null;
        this.state = state;
    }
}