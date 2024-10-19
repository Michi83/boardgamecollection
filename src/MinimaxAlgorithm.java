import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinimaxAlgorithm implements Algorithm {
    private Map<String, Integer> killerMoves;
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
        Collections.sort(moves, new KillerMovesComparator(killerMoves));
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
                        String key = move.getNotation();
                        int value = killerMoves.getOrDefault(key, 0);
                        value += depth * depth;
                        killerMoves.put(key, value);
                        break;
                    }
                }
            }
        }
        return new MinimaxResult(topScore, topMove);
    }

    public void run() {
        timeStarted = System.currentTimeMillis();
        killerMoves = new HashMap<String, Integer>();
        GameState move = null;
        System.out.println("ply  score  time   nodes pv");
        for (int depth = 1; true; depth++) {
            try {
                nodes = 0;
                long time1 = System.currentTimeMillis();
                MinimaxResult result = minimax(state, depth);
                long time2 = System.currentTimeMillis();
                double score = result.score;
                move = result.move;
                System.out.printf("%3d %6.3f %5d %7d %s\n", depth, score,
                    time2 - time1, nodes, move.getNotation());
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