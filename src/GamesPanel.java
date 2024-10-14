import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class GamesPanel extends JPanel implements ActionListener {
    private static final String[] GAMES = new String[] {
        "Checkers/Draughts",
        "Go 9x9",
    };

    private static final String[] RULES = new String[] {
        "https://wcdf.net/rules.htm",
        "https://www.cs.cmu.edu/~wjh/go/rules/Chinese.html",
    };

    private static final GameState[] STATES = new GameState[] {
        new CheckersState(),
        new Go9State(),
    };

    private JList<String> gamesList;

    public GamesPanel() {
        super();
        setBorder(new TitledBorder("Games"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        gamesList = new JList<String>(GAMES);
        gamesList.setSelectedIndex(0);
        JScrollPane scrollPane = new JScrollPane(gamesList);
        add(scrollPane);

        JButton rulesButton = new JButton("Rules");
        rulesButton.addActionListener(this);
        add(rulesButton);
    }

    public void actionPerformed(ActionEvent event) {
        try {
            int index = gamesList.getSelectedIndex();
            Desktop.getDesktop().browse(new URI(RULES[index]));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public GameState getState() {
        int index = gamesList.getSelectedIndex();
        return STATES[index];
    }
}