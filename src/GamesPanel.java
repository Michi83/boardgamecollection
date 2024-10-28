import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;

public class GamesPanel extends JPanel implements ActionListener {
    private List<String> games;
    private List<String> classes;
    private List<String> rules;
    private JList<String> gamesList;

    public GamesPanel() {
        super();

        // load config file
        games = new ArrayList<String>();
        classes = new ArrayList<String>();
        rules = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(
                new FileReader("config.csv"));
            String line = reader.readLine();
            while (line != null) {
                String[] cells = line.split(",");
                games.add(cells[0]);
                classes.add(cells[1]);
                rules.add(cells[2]);
                line = reader.readLine();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // set up contents
        setBorder(new TitledBorder("Games"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        gamesList = new JList<String>(games.toArray(new String[0]));
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
            Desktop.getDesktop().browse(new URI(rules.get(index)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getGameName() {
        int index = gamesList.getSelectedIndex();
        return games.get(index);
    }

    public GameState getState() {
        GameState state = null;
        try {
            int index = gamesList.getSelectedIndex();
            Class<?> cls = Class.forName(classes.get(index));
            state = (GameState)cls.getConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return state;
    }
}