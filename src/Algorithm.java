public interface Algorithm extends Runnable {
    public void click(int id);
    public GameState getMove();
    public void setState(GameState state);
}