package asteroids;

import java.awt.event.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

import static asteroids.Constants.*;

/**
 * Controls a game of asteroids
 * 
 * @author Joe Zachary
 */
public class Controller implements CollisionListener, ActionListener,
        KeyListener, CountdownTimerListener
{
    // Shared random number generator
    private Random random;

    // The ship (if one is active) or null (otherwise)
    private Ship ship;

    // The bullets
    private Bullet bullet; // TODO

    // The active bullets
    private LinkedList<Bullet> bullets;

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

    // List of timers for removing bullets from play
    private LinkedList<Timer> bulletTimers;

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

        // Set up the ship's acceleration timer
        shipAccelTimer = new Timer(SHIP_ACCEL_INTERVAL, this);

        // Set up the ship's right-rotation timer
        shipRotateRTimer = new Timer(SHIP_ROTATION_INTERVAL, this);

        // Set up the ship's left-rotation timer
        shipRotateLTimer = new Timer(SHIP_ROTATION_INTERVAL, this);

        // Initialize the bullet list
        bullets = new LinkedList<Bullet>();

        // Set up the bullet timer list
        bulletTimers = new LinkedList<Timer>();

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
     * Places four large asteroids near the corners of the screen. Give them
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

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Reset statistics
        lives = 3;
        score = 0;
        level = 1;
        game.setLives("Lives: " + lives);
        game.setScore("Score: " + score);
        game.setLevel("Level: " + level);
        asteroidsHit = 0;

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
        asteroidsHit = 0;
        ship = null;

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
        Timer bulletTimer = new Timer(BULLET_DURATION, this);
        bulletTimer.setActionCommand("bullet");
        bulletTimer.start();
        bulletTimers.add(bulletTimer);
    }

    /**
     * Deal with collisions between participants.
     */
    @Override
    public void collidedWith (Participant p1, Participant p2)
    {
        if (p1 instanceof Asteroid && p2 instanceof Ship)
        {
            shipCollision((Ship) p2);
            asteroidCollision((Asteroid) p1);
        }
        else if (p1 instanceof Ship && p2 instanceof Asteroid)
        {
            shipCollision((Ship) p1);
            asteroidCollision((Asteroid) p2);

        }
        else if (p1 instanceof Asteroid && p2 instanceof Bullet)
        {
            bulletCollision((Bullet) p2);
            asteroidCollision((Asteroid) p1);
        }
        else if (p1 instanceof Bullet && p2 instanceof Asteroid)
        {
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

        // Two smaller asteroids are created if the destroyed asteroid wasn't
        // the smallest. Put them at the same position as the one that was just
        // destroyed, increase their speed, and give them a random direction.
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
        if (e.getSource() instanceof JButton)
        {
            transitionCount++;
            initialScreen();
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
