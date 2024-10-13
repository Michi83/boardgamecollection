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

public class MCTSPlayer implements Player {
    private int maxTime; // in seconds
    private GameState move;
    private GameState state;

    public MCTSPlayer(int maxTime) {
        this.maxTime = maxTime;
    }

    public void click(int x, int y) {
    }

    public GameState getMove() {
        return move;
    }

    public void run() {
        MCTSNode root = new MCTSNode(state);
        int count = 0;
        long time1 = System.currentTimeMillis();
        while (true) {
            MCTSNode node = root.select();
            node = node.expand();
            double result = node.playout();
            node.backpropagate(result);
            count++;
            long time2 = System.currentTimeMillis();
            if (time2 - time1 > 1000L * maxTime) {
                break;
            }
        }
        System.out.println(count + " MCTS iterations");
        double topScore = Double.NEGATIVE_INFINITY;
        GameState topMove = null;
        for (MCTSNode child : root.children) {
            double score = child.w / child.n;
            if (score > topScore) {
                topScore = score;
                topMove = child.state;
            }
        }
        move = topMove;
    }

    public void setState(GameState state) {
        move = null;
        this.state = state;
    }
}