package asteroids;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

import static asteroids.Constants.*;

/**
 * Controls a game of asteroids
 * 
 * @author Jackson Murphy and Joe Zachary
 */
public class Controller implements CollisionListener, ActionListener,
        KeyListener, CountdownTimerListener
{
    // Shared random number generator
    private Random random;

    // The ship (if one is active) or null (otherwise)
    private Ship ship;

    // All the bullets on the screen
    private LinkedList<Bullet> bullets;

    // All the dust particles on the screen
    private LinkedList<Dust> dust;

    // All the debris particles on the screen
    private LinkedList<Debris> debris;

    // When this timer goes off, it is time to refresh the animation
    private Timer refreshTimer;

    // When this timer goes off, it is time to start a new level
    private Timer nextLevelTimer;

    // When this timer goes off, it is time to accelerate the ship
    private Timer shipAccelTimer;

    // When this timer goes off, it is time to right-rotate the ship
    private Timer shipRotateRTimer;

    // When this timer goes off, it is time to left-rotate the ship
    private Timer shipRotateLTimer;

    // List of timers for removing bullets from the screen
    private LinkedList<Timer> bulletTimers;

    // List of timers for removing dust from the screen
    private LinkedList<Timer> dustTimers;

    // List of timers for removing debris from the screen
    private LinkedList<Timer> debrisTimers;

    // Count of how many transitions have been made. This is used to keep two
    // conflicting transitions from being made at almost the same time.
    private int transitionCount;

    // Number of lives left
    private int lives;

    // The user's score
    private int score;

    // The game level
    private int level;

    // The number of asteroids destroyed on a given level
    private int asteroidsHit;

    // Whether or not the game is paused
    private boolean isPaused;

    // Holds the speeds of the paused ship and asteroids
    private ArrayList<Double> pausedSpeeds;

    // The Game and Screen objects being controlled
    private Game game;
    private Screen screen;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller (Game game, Screen screen)
    {
        // Record the game and screen objects
        this.game = game;
        this.screen = screen;

        // Initialize the random number generator
        random = new Random();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);
        transitionCount = 0;

        // Set up the next-level timer
        nextLevelTimer = new Timer(END_DELAY, this);

        // Set up the ship's acceleration, right-, and left-rotation timers
        shipAccelTimer = new Timer(SHIP_ACCEL_INTERVAL, this);
        shipRotateRTimer = new Timer(SHIP_ROTATION_INTERVAL, this);
        shipRotateLTimer = new Timer(SHIP_ROTATION_INTERVAL, this);

        // Initialize the bullet list and associated timers
        bullets = new LinkedList<Bullet>();
        bulletTimers = new LinkedList<Timer>();

        // Initialize the dust list and associated timers
        dust = new LinkedList<Dust>();
        dustTimers = new LinkedList<Timer>();

        // Initialize the debris list and associated timers
        debris = new LinkedList<Debris>();
        debrisTimers = new LinkedList<Timer>();

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        refreshTimer.start();
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen and display the legend
        screen.clear();
        screen.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();

        // Make sure there's no ship
        ship = null;
    }

    /**
     * Get the number of transitions that have occurred.
     */
    public int getTransitionCount ()
    {
        return transitionCount;
    }

    /**
     * The game is over. Displays a message to that effect and enables the start
     * button to permit playing another game.
     */
    private void finalScreen ()
    {
        screen.setLegend(GAME_OVER);
        screen.removeCollisionListener(this);
        screen.removeKeyListener(this);
    }

    /**
     * Places four large asteroids near the corners of the screen. Gives them
     * random directions and rotations, and a level-dependent speed.
     */
    private void placeAsteroids ()
    {
        Participant a = new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET);
        a.setVelocity(level + 2, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(1, 2, SIZE - EDGE_OFFSET, EDGE_OFFSET);
        a.setVelocity(level + 2, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(2, 2, EDGE_OFFSET, SIZE - EDGE_OFFSET);
        a.setVelocity(level + 2, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);

        a = new Asteroid(3, 2, SIZE - EDGE_OFFSET, SIZE - EDGE_OFFSET);
        a.setVelocity(level + 2, random.nextDouble() * 2 * Math.PI);
        a.setRotation(2 * Math.PI * random.nextDouble());
        screen.addParticipant(a);
    }

    /**
     * Set things up and begin a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        screen.clear();

        // Reset the statistics
        lives = 3;
        score = 0;
        level = 1;
        asteroidsHit = 0;
        isPaused = false;
        pausedSpeeds = new ArrayList<Double>();

        // Reset the GUI labels
        game.setLives("Lives: " + lives);
        game.setScore("Score: " + score);
        game.setLevel("Level: " + level);

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // In case a new game was started while the game was paused, correct the
        // label on the pause button
        game.setPauseLabel("Pause");

        // Start listening to events. In case we're already listening, take
        // care to avoid listening twice.
        screen.removeCollisionListener(this);
        screen.removeKeyListener(this);
        screen.addCollisionListener(this);
        screen.addKeyListener(this);

        // Give focus to the game screen
        screen.requestFocusInWindow();
    }

    /**
     * Starts a new level.
     */
    private void nextLevelScreen ()
    {
        // Restart ship
        ship = null;

        // Reset asteroid counter
        asteroidsHit = 0;

        // Clear the screen
        screen.clear();

        // Display the level number and make it disappear in one second
        screen.setLegend("Level " + level);
        new CountdownTimer(this, null, 1000);

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Start listening to events. In case we're already listening, take
        // care to avoid listening twice.
        screen.removeCollisionListener(this);
        screen.removeKeyListener(this);
        screen.addCollisionListener(this);
        screen.addKeyListener(this);

        // Give the user an extra life if they've reached level 5 or 7
        if (level == 5 || level == 7)
        {
            lives++;
            game.setLives("Lives: " + lives);
        }

        // Give focus to the game screen
        screen.requestFocusInWindow();
    }

    /**
     * Place a ship in the center of the screen.
     */
    private void placeShip ()
    {
        if (ship == null)
        {
            ship = new Ship();
        }
        ship.setPosition(SIZE / 2, SIZE / 2);
        ship.setRotation(-Math.PI / 2);
        screen.addParticipant(ship);
    }

    /**
     * Shoot a bullet with constant speed from the nose of the ship, in the
     * direction of the ship's orientation.
     */
    private void shootBullet ()
    {
        Bullet bullet = new Bullet();
        bullet.setPosition(ship.getXNose(), ship.getYNose());
        bullet.setVelocity(BULLET_SPEED, ship.getRotation());
        screen.addParticipant(bullet);
        bullets.add(bullet);

        // An associated timer removes the bullet from play after a period of
        // time
        Timer t = new Timer(BULLET_DURATION, this);
        t.setActionCommand("bullet");
        t.start();
        bulletTimers.add(t);
    }

    /**
     * Create dust at the spot where an asteroid is hit
     */
    private void createDust (Asteroid a)
    {
        // Create six dust particles and give them random directions
        for (int i = 0; i < 6; i++)
        {
            Dust d = new Dust();
            d.setPosition(a.getX(), a.getY());
            d.setVelocity(DUST_SPEED, random.nextDouble() * 2 * Math.PI);
            dust.add(d); // add the dust particle to the list of active dust
            screen.addParticipant(d);
        }
        // Add a timer for later removing the six dust particles
        Timer t = new Timer(DUST_DURATION, this);
        t.setActionCommand("dust");
        t.start();
        dustTimers.add(t);
    }

    /**
     * Create debris at the spot where the ship is hit
     */
    public void createDebris (Ship s)
    {
        // Create three debris particles, give them random directions, and add
        // them to the list of active debris
        for (int i = 0; i < 3; i++)
        {
            Debris d = new Debris();
            d.setPosition(s.getX(), s.getY());
            d.setVelocity(DEBRIS_SPEED, random.nextDouble() * 2 * Math.PI);
            d.setRotation(2 * Math.PI * random.nextDouble());
            debris.add(d);
            screen.addParticipant(d);
        }
        // Add a timer for later removing the three debris particles
        Timer t = new Timer(DEBRIS_DURATION, this);
        t.setActionCommand("debris");
        t.start();
        debrisTimers.add(t);
    }

    /**
     * Deal with collisions between participants.
     */
    @Override
    public void collidedWith (Participant p1, Participant p2)
    {
        if (p1 instanceof Asteroid && p2 instanceof Ship)
        {
            createDust((Asteroid) p1);
            createDebris((Ship) p2);
            shipCollision((Ship) p2);
            asteroidCollision((Asteroid) p1);
        }
        else if (p1 instanceof Ship && p2 instanceof Asteroid)
        {
            createDust((Asteroid) p2);
            createDebris((Ship) p1);
            shipCollision((Ship) p1);
            asteroidCollision((Asteroid) p2);

        }
        else if (p1 instanceof Asteroid && p2 instanceof Bullet)
        {
            createDust((Asteroid) p1);
            bulletCollision((Bullet) p2);
            asteroidCollision((Asteroid) p1);
        }
        else if (p1 instanceof Bullet && p2 instanceof Asteroid)
        {
            createDust((Asteroid) p2);
            bulletCollision((Bullet) p1);
            asteroidCollision((Asteroid) p2);
        }
    }

    /**
     * The ship has collided with something
     */
    private void shipCollision (Ship s)
    {
        // Remove the ship from the screen and null it out
        screen.removeParticipant(s);
        ship = null;

        // Display a legend and make it disappear in one second
        screen.setLegend("Ouch!");
        new CountdownTimer(this, null, 1000);

        // Decrement lives and update the lives label
        lives--;
        game.setLives("Lives: " + lives);

        // Start the timer that will cause the next round to begin.
        new TransitionTimer(END_DELAY, transitionCount, this);
    }

    /**
     * Something has hit an asteroid
     */
    private void asteroidCollision (Asteroid a)
    {
        // The asteroid disappears
        screen.removeParticipant(a);

        // The asteroidsHit counter is incremented
        asteroidsHit++;

        // Points are added to the user's score
        int size = a.getSize();
        switch (size)
        {
        case 0:
            score += 100;
            break;
        case 1:
            score += 50;
            break;
        case 2:
            score += 20;
            break;
        }
        // The score label is updated
        game.setScore("Score: " + score);

        // Move on to the next level if all 28 asteroids have been destroyed
        if (asteroidsHit == 28)
        {
            level++;
            game.setLevel("Level: " + level);

            // Start the timer that will cause the next level to begin.
            new TransitionTimer(END_DELAY, transitionCount, this);

        }

        // If there are still asteroids remaining:
        // Two smaller asteroids replace the one just destroyed (unless the
        // destroyed asteroid was of size small. Puts them at the same position
        // as the one that was just destroyed, increases their speed, and gives
        // them a random direction.
        size--;
        if (size >= 0)
        {
            int speed = 4 - size + level;
            Asteroid a1 = new Asteroid(random.nextInt(4), size, a.getX(),
                    a.getY());
            Asteroid a2 = new Asteroid(random.nextInt(4), size, a.getX(),
                    a.getY());
            a1.setVelocity(speed, random.nextDouble() * 2 * Math.PI);
            a2.setVelocity(speed, random.nextDouble() * 2 * Math.PI);
            a1.setRotation(2 * Math.PI * random.nextDouble());
            a2.setRotation(2 * Math.PI * random.nextDouble());
            screen.addParticipant(a1);
            screen.addParticipant(a2);
        }
    }

    /**
     * A bullet has hit an asteroid.
     */
    private void bulletCollision (Bullet b)
    {
        screen.removeParticipant(b);
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getActionCommand() == "Start")
        {
            transitionCount++;
            game.setStartButtonLabel("New Game");
            initialScreen();
        }

        // The pause button has been pressed. Pause or resume the game depending
        // on the game's current state.
        else if (e.getActionCommand() == "Pause")
        {
            // Pause the game
            if (!isPaused)
            {
                isPaused = true;

                // // Remove all dust and debris from the screen because
                // // their timers interfere with the pause/resume cycle.
                // for (Dust d : dust)
                // {
                // screen.removeParticipant(d);
                // }
                // for (Debris b : debris)
                // {
                // screen.removeParticipant(b);
                // }

                // Store the speed of each participant so that we can restore
                // their speeds upon resuming the game
                pausedSpeeds = screen.pause();

                // Update the button label
                game.setPauseLabel("Resume");
            }
            // If already paused, resume the game
            else
            {
                screen.unpause(pausedSpeeds);
                isPaused = false;
                game.setPauseLabel("Pause");
                // Return focus to the game screen
                screen.requestFocusInWindow();
            }
        }

        // Time to refresh the screen
        else if (e.getSource() == refreshTimer)
        {

            // Refresh screen
            screen.refresh();
        }

        // Time to go to the next level
        // Note: Why doesn't Command().equals() work here?
        else if (e.getActionCommand() == "level")
        {
            nextLevelTimer.stop();
            nextLevelScreen();
        }

        // Time to accelerate the ship
        else if (e.getSource() == shipAccelTimer)
        {
            if (ship != null)
                ship.accelerate(ACCELERATION_VALUE);
        }

        // Time to rotate the ship to the right
        else if (e.getSource() == shipRotateRTimer)
        {
            if (ship != null)
                ship.rotate(Math.PI / 16);
        }

        // Time to rotate the ship to the left
        else if (e.getSource() == shipRotateLTimer)
        {
            if (ship != null)
                ship.rotate(-Math.PI / 16);
        }

        // Time to remove a bullet and delete its associated timer
        else if (e.getActionCommand().equals("bullet"))
        {
            bulletTimers.removeFirst().stop();
            screen.removeParticipant(bullets.removeFirst());
        }

        // Time to remove a dust group from the screen and delete its timer
        else if (e.getActionCommand().equals("dust"))
        {
            for (int i = 0; i < 6; i++)
            {
                screen.removeParticipant(dust.removeFirst());
            }
            dustTimers.removeFirst().stop();
        }

        // Time to remove a debris group from the screen and delete its timer
        else if (e.getActionCommand().equals("debris"))
        {
            for (int i = 0; i < 3; i++)
            {
                screen.removeParticipant(debris.removeFirst());
            }
            debrisTimers.removeFirst().stop();
        }

    }

    /**
     * Based on the state of the controller, transition to the next state.
     */
    public void performTransition ()
    {
        // Record that a transition was made. That way, any other pending
        // transitions will be ignored.
        transitionCount++;

        // If there are no lives left, the game is over. Show
        // the final screen.
        if (lives == 0)
        {
            finalScreen();
        }

        // If all the asteroids have been destroyed, advance to the next level
        else if (asteroidsHit == 28)
        {
            // Show a message on the screen
            screen.setLegend("Level " + level);

            // Go to the next level
            nextLevelTimer.start();
            nextLevelTimer.setActionCommand("level");

        }

        // The ship must have been destroyed. Place a new one and
        // continue on the current level
        else
        {
            placeShip();
        }
    }

    /**
     * Deals with certain key presses
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        // Left arrow key rotates ship to the left
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            if (ship != null && !shipRotateLTimer.isRunning())
                shipRotateLTimer.start();

        }

        // Right arrow key starts a timer that smoothly rotates the ship
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            if (ship != null && !shipRotateRTimer.isRunning())
                shipRotateRTimer.start();
        }

        // Up arrow key starts a timer that smoothly accelerates the ship
        else if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            if (ship != null && !shipAccelTimer.isRunning())
                shipAccelTimer.start();
        }

        // Space bar shoots a bullet if there are fewer than 8 bullets already
        // on the screen.
        else if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if (ship != null && bullets.size() < 8)
                shootBullet();

        }
    }

    @Override
    public void keyReleased (KeyEvent e)
    {
        // Releasing the up arrow stops the ship's acceleration
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            shipAccelTimer.stop();
        }

        // Releasing the right arrow stops the ship's right-rotation
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            shipRotateRTimer.stop();
        }

        // Releasing the left arrow stops the ship's left-rotation
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            shipRotateLTimer.stop();
        }
    }

    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * Callback for countdown timer. Used to create transient effects.
     */
    @Override
    public void timeExpired (Participant p)
    {
        screen.setLegend("");
    }

}
