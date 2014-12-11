package asteroids;

import javax.swing.*;
import java.awt.*;
import static asteroids.Constants.*;

/**
 * Implements an asteroid game.
 * 
 * @author Joe Zachary
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

        // The button that starts the game
        JButton startGame = new JButton(START_LABEL);
        controls.add(startGame);

        // The score label
        scoreLabel = new JLabel("Score: 0");
        controls.add(scoreLabel);

        // The lives-count label
        livesLabel = new JLabel("Lives: 3");
        controls.add(livesLabel);

        // The game-level label
        levelLabel = new JLabel("Level: 1");
        controls.add(levelLabel);

        // The button that pauses the game
        JButton pauseGame = new JButton(PAUSE_LABEL);
        controls.add(pauseGame);
        pauseGame.addActionListener(controller);
        pauseGame.setActionCommand("Pause");

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
        startGame.setActionCommand("Start");
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
     * Refreshes the window to update the "score" and "lives" labels
     */
    public void refresh ()
    {
        repaint();
    }

}
