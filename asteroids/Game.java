package asteroids;

import javax.swing.*;

import java.awt.*;

import static asteroids.Constants.*;

/**
 * Implements an asteroid game.
 * 
 * @author Jackson Murphy, Joe Zachary
 *
 */
public class Game extends JFrame
{
    /**
     * Launches the game
     */
    public static void main (String[] args)
    {
        Game a = new Game();
        a.setVisible(true);
    }

    // Label showing the user's score
    private JLabel scoreLabel;

    // Label showing the number of lives remaining
    private JLabel livesLabel;

    // Label showing the game level
    private JLabel levelLabel;

    // Button for pausing/resuming the game
    private JButton pauseButton;

    // Button for starting a new game
    private JButton newGameButton;

    /**
     * Lays out the game and creates the controller
     */
    public Game ()
    {
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        Screen screen = new Screen();
        Controller controller = new Controller(this, screen);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 1));

        newGameButton = new JButton(START_LABEL);
        controls.add(newGameButton);

        // The score label. The initial score is 0
        scoreLabel = new JLabel("Score: 0");
        controls.add(scoreLabel);

        // The lives-count label. Initially there are three lives
        livesLabel = new JLabel("Lives: 3");
        controls.add(livesLabel);

        // The game-level label. The game beings on level 1
        levelLabel = new JLabel("Level: 1");
        controls.add(levelLabel);

        // The button that pauses the game
        pauseButton = new JButton(PAUSE_LABEL);
        controls.add(pauseButton);

        // Connect the controller to the pause button
        pauseButton.addActionListener(controller);
        pauseButton.setActionCommand("Pause");

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        newGameButton.addActionListener(controller);
        newGameButton.setActionCommand("Start");
    }

    /**
     * Setter for updating the score label
     */
    public void setScore (String s)
    {
        scoreLabel.setText(s);
    }

    /**
     * Setter for updating the lives label
     */
    public void setLives (String s)
    {
        livesLabel.setText(s);
    }

    /**
     * Setter for updating the game-level label
     */
    public void setLevel (String s)
    {
        levelLabel.setText(s);
    }

    /**
     * Setter for changing the pause button label
     */
    public void setPauseLabel (String s)
    {
        pauseButton.setText(s);
    }

    /**
     * Setter for changing the label of the new game button
     */
    public void setStartButtonLabel (String s)
    {
        newGameButton.setText(s);
    }
}
