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
    public static final int HUMAN = 1;
    public static final int COMPUTER = 2;
    public static final int RANDOM = 3;

    private JRadioButton computerButton;
    private JRadioButton humanButton;
    private JRadioButton randomButton;
    private JLabel timeLabel;
    private JSlider timeSlider;

    public PlayerPanel(String title) {
        super();
        setBorder(new TitledBorder(title));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        humanButton = new JRadioButton("Human");
        add(humanButton);

        computerButton = new JRadioButton("Computer");
        add(computerButton);

        timeSlider = new JSlider(1, 60, 15);
        timeSlider.addChangeListener(this);
        add(timeSlider);

        timeLabel = new JLabel();
        stateChanged(null);
        add(timeLabel);

        randomButton = new JRadioButton("Random moves");
        add(randomButton);

        ButtonGroup playerGroup = new ButtonGroup();
        playerGroup.add(humanButton);
        playerGroup.add(computerButton);
        playerGroup.add(randomButton);
    }

    public Player getPlayer() {
        if (humanButton.isSelected()) {
            return new HumanPlayer();
        } else if (computerButton.isSelected()) {
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
            case HUMAN:
                humanButton.setSelected(true);
                break;
            case COMPUTER:
                computerButton.setSelected(true);
                break;
            case RANDOM:
                randomButton.setSelected(true);
                break;
        }
    }
}