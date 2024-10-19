import java.util.Comparator;
import java.util.Map;

public class KillerMovesComparator implements Comparator<GameState> {
    private Map<String, Integer> killerMoves;

    public KillerMovesComparator(Map<String, Integer> killerMoves) {
        this.killerMoves = killerMoves;
    }

    public int compare(GameState state1, GameState state2) {
        String key1 = state1.getNotation();
        String key2 = state2.getNotation();
        int value1 = killerMoves.getOrDefault(key1, 0);
        int value2 = killerMoves.getOrDefault(key2, 0);
        return value2 - value1;
    }
}