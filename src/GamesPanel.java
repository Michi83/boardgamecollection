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
        "Chess",
        "Go 9x9",
        "Gomoku",
        "Nine Men's Morris"
    };

    private static final String[] RULES = new String[] {
        "https://wcdf.net/rules.htm",
        "https://handbook.fide.com/chapter/E012023",
        "https://www.cs.cmu.edu/~wjh/go/rules/Chinese.html",
        "https://en.wikipedia.org/wiki/Gomoku",
        "https://library.slmath.org/books/Book29/files/gasser.pdf"
    };

    private static final String[] CLASSES = new String[] {
        "CheckersState",
        "ChessState",
        "Go9State",
        "GomokuState",
        "MorrisState"
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
        GameState state = null;
        try {
            int index = gamesList.getSelectedIndex();
            Class<?> cls = Class.forName(CLASSES[index]);
            state = (GameState)cls.getConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return state;
    }
}