package asteroids;

/**
 * Provides constants governing the game
 * 
 * @author Jackson Murphy, Joe Zachary
 */

public class Constants
{
    /**
     * The height and width of the game area.
     */
    public final static int SIZE = 750;

    /**
     * Game title
     */
    public final static String TITLE = "CS 1410 Asteroids";

    /**
     * Label on start game button
     */
    public final static String START_LABEL = "Start Game";
    
    /**
     * Label on the pause game button
     */
    public final static String PAUSE_LABEL = "Pause";
    
    /**
     * Speed beyond which participants may not accelerate
     */
    public final static double SPEED_LIMIT = 15;

    /**
     * Amount of "friction" that can be applied to ships so that they eventually
     * stop. Should be negative.
     */
    public final static double FRICTION = -0.05;

    /**
     * The number of milliseconds between the beginnings of frame refreshes
     */
    public final static int FRAME_INTERVAL = 33;

    /**
     * The number of milliseconds between the end of a life and the display of
     * the next screen.
     */
    public final static int END_DELAY = 2500;

    /**
     * The offset in pixels from the edges of the screen of newly-placed
     * asteroids.
     */
    public final static int EDGE_OFFSET = 100;

    /**
     * The game over message
     */
    public final static String GAME_OVER = "Game Over";

    /**
     * Number of asteroids that must be destroyed to complete a level.
     */
    public final static int ASTEROID_COUNT = 28;

    /**
     * Duration in milliseconds of a bullet before it disappears.
     */
    public final static int BULLET_DURATION = 1000;

    /**
     * Speed, in pixels per frame, of a bullet.
     */
    public final static double BULLET_SPEED = 15;
    
    /**
     * Duration in milliseconds of dust before it disappears.
     */
    public final static int DUST_DURATION = 1500;
    
    /**
     * Speed, in pixels per frame, of a dust particle
     */
    public final static double DUST_SPEED = 1;
    
    /**
     * Duration in milliseconds of debris before it disappears.
     */
    public final static int DEBRIS_DURATION = 2000;
    
    /**
     * Speed, in pixels per frame, of a debris particle
     */
    public final static double DEBRIS_SPEED = 1;
    
    /**
     * Scaling factors used for asteroids of size 0, 1, and 2.
     */
    public final static double[] ASTEROID_SCALE = { 0.5, 1.0, 2.0 };

    /**
     * The ship's acceleration value.
     */
    public final static double ACCELERATION_VALUE = 1.0;

    /**
     * The number of milliseconds between firings of the ship's acceleration
     * timer.
     */
    public final static int SHIP_ACCEL_INTERVAL = 100;

    /**
     * The number of milliseconds between firings of the ship's left- and
     * right-rotation timers.
     */
    public final static int SHIP_ROTATION_INTERVAL = 40;
}
