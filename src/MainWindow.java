import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame implements ActionListener {
    private GamesPanel gamesPanel;
    private AlgorithmPanel whitePanel;
    private AlgorithmPanel blackPanel;

    public MainWindow() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(768, 384);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 3));
        add(mainPanel);

        gamesPanel = new GamesPanel();
        mainPanel.add(gamesPanel);

        whitePanel = new AlgorithmPanel("White");
        whitePanel.setAlgorithm(0);
        mainPanel.add(whitePanel);

        blackPanel = new AlgorithmPanel("Black");
        blackPanel.setAlgorithm(1);
        mainPanel.add(blackPanel);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(this);
        add(playButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        GameState state = gamesPanel.getState();
        Algorithm white = whitePanel.getAlgorithm();
        Algorithm black = blackPanel.getAlgorithm();
        GameWindow gameWindow = new GameWindow(state, white, black);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}