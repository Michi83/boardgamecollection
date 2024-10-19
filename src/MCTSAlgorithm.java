// Monte Carlo Tree Search (MCTS) is an alternative to the classic alpha-beta
// algorithm. It is well explained on Wikipedia, so here is the short version:
// Construct a partial game tree, initially only containing the root node. Then
// repeat the following steps a couple thousand times:
// 1) SELECT: Select a leaf node, such that both promising and underexplored
//    branches of the tree are preferred.
// 2) EXPAND: If the leaf node has been visited already, generate its children
//    and select any one of them.
// 3) PLAYOUT: Beginning with the selected node, play a test game of random
//    moves.
// 4) BACKPROPAGATE: Update the statistics of the selected node and its parent
//    chain all the way up to the root.

public class MCTSAlgorithm implements Algorithm {
    private int maxTime; // in seconds
    private GameState move;
    private GameState state;

    public MCTSAlgorithm(int maxTime) {
        this.maxTime = maxTime;
    }

    public void click(int id) {
    }

    public GameState getMove() {
        return move;
    }

    public void run() {
        MCTSNode root = new MCTSNode(state);
        System.out.println(" time     wins/visits pv");
        long time1 = System.currentTimeMillis();
        while (true) {
            MCTSNode node = root.select();
            node = node.expand();
            double result = node.playout();
            node.backpropagate(result);
            long time2 = System.currentTimeMillis();
            if (root.n % 1000 == 0) {
                System.out.printf("%5d %8.1f/%-6d %s\n", time2 - time1, root.w,
                    root.n, root.getPrincipalVariation());
            }
            if (time2 - time1 > 1000L * maxTime) {
                break;
            }
        }
        System.out.println();
        move = root.getTopChild().state;
    }

    public void setState(GameState state) {
        move = null;
        this.state = state;
    }
}