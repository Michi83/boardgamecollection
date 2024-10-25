import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MCTSNode {
    public List<MCTSNode> children;
    public int n; // number of visits
    private MCTSNode parent;
    public GameState state;
    public double w; // number of wins for previous(!) player

    public MCTSNode(GameState state) {
        this(state, null);
    }

    private MCTSNode(GameState state, MCTSNode parent) {
        children = new ArrayList<MCTSNode>();
        n = 0;
        this.parent = parent;
        this.state = state;
        w = 0;
    }

    public void backpropagate(double result) {
        MCTSNode node = this;
        while (node != null) {
            node.n++;
            node.w += (-node.state.getPlayer() * result + 1) / 2;
            node = node.parent;
        }
    }

    public MCTSNode expand() {
        // If we visited the node once already, we attempt to generate its
        // children.
        if (n == 1) {
            List<GameState> moves = state.generateMoves();
            Collections.shuffle(moves);
            for (GameState move : moves) {
                MCTSNode child = new MCTSNode(move, this);
                children.add(child);
            }
            if (children.size() > 0) {
                return children.get(0);
            }
        }
        // If there are no children, return the node itself.
        return this;
    }

    public String getPrincipalVariation() {
        String pv = "";
        MCTSNode node = getTopChild();
        while (node != null) {
            if (!pv.equals("")) {
                pv += " ";
            }
            pv += node.state.getNotation();
            node = node.getTopChild();
        }
        return pv;
    }

    public MCTSNode getTopChild() {
        int topScore = Integer.MIN_VALUE;
        MCTSNode topChild = null;
        for (MCTSNode child : children) {
            if (child.n > topScore) {
                topScore = child.n;
                topChild = child;
            }
        }
        return topChild;
    }

    public double playout() {
        GameState state = this.state;
        for (int i = 0; i < 100; i++) {
            List<GameState> moves = state.generateMoves();
            if (moves.size() == 0) {
                return state.evaluate();
            }
            int index = (int)(Math.random() * moves.size());
            state = moves.get(index);
        }
        return this.state.evaluate();
    }

    public MCTSNode select() {
        MCTSNode node = this;
        while (node.children.size() > 0) {
            double topScore = Double.NEGATIVE_INFINITY;
            MCTSNode topChild = null;
            for (MCTSNode child : node.children) {
                double score = child.uct();
                if (score > topScore) {
                    topScore = score;
                    topChild = child;
                }
            }
            node = topChild;
        }
        return node;
    }

    private double uct() {
        if (n == 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            double h = -state.getPlayer() * state.evaluate();
            return w / n + Math.sqrt(2 * Math.log(parent.n) / n) + h / n;
        }
    }
}