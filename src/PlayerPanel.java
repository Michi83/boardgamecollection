import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PlayerPanel extends JPanel implements ChangeListener {
    private JRadioButton humanButton;
    private JRadioButton mctsButton;
    private JRadioButton minimaxButton;
    private ButtonGroup playerGroup;
    private JRadioButton randomButton;
    private JLabel timeLabel;
    private JSlider timeSlider;

    public PlayerPanel(String title) {
        super();
        setBorder(new TitledBorder(title));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        humanButton = new JRadioButton("Human");
        add(humanButton);

        minimaxButton = new JRadioButton("Computer (Minimax)");
        add(minimaxButton);

        mctsButton = new JRadioButton("Computer (MCTS)");
        add(mctsButton);

        randomButton = new JRadioButton("Random moves");
        add(randomButton);

        JLabel label = new JLabel("Computer time");
        add(label);

        timeSlider = new JSlider(1, 60, 5);
        timeSlider.addChangeListener(this);
        add(timeSlider);

        timeLabel = new JLabel();
        stateChanged(null);
        add(timeLabel);

        playerGroup = new ButtonGroup();
        playerGroup.add(humanButton);
        playerGroup.add(minimaxButton);
        playerGroup.add(mctsButton);
        playerGroup.add(randomButton);
    }

    public Player getPlayer() {
        if (humanButton.isSelected()) {
            return new HumanPlayer();
        } else if (minimaxButton.isSelected()) {
            int maxTime = timeSlider.getValue();
            return new MinimaxPlayer(maxTime);
        } else if (mctsButton.isSelected()) {
            int maxTime = timeSlider.getValue();
            return new MCTSPlayer(maxTime);
        } else if (randomButton.isSelected()) {
            return new RandomPlayer();
        }
        return null;
    }

    public void stateChanged(ChangeEvent event) {
        int maxTime = timeSlider.getValue();
        timeLabel.setText(maxTime + " seconds");
    }

    public void setPlayer(int player) {
        switch (player) {
            case 0:
                humanButton.setSelected(true);
                break;
            case 1:
                minimaxButton.setSelected(true);
                break;
            case 2:
                mctsButton.setSelected(true);
                break;
            case 3:
                randomButton.setSelected(true);
        }
    }
}