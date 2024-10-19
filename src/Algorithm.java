public interface Algorithm extends Runnable {
    public void click(int x, int y);
    public GameState getMove();
    public void setState(GameState state);
}