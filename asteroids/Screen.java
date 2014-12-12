package asteroids;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import static asteroids.Constants.*;

/**
 * The area in which the game takes place.
 * 
 * @author Joe Zachary, Jackson Murphy
 */
public class Screen extends JPanel
{
    // The participants (asteroids, bullets, ships, etc.) that are
    // involved in the game.
    private LinkedList<Participant> participants;

    // Objects interested in learning about collisions between
    // pairs of participants
    private Set<CollisionListener> listeners;

    // Participants that will be added to/removed from the game at the next
    // refresh
    private Set<Participant> pendingAdds;
    private Set<Participant> pendingRemoves;

    // Legend that is displayed across the screen
    private String legend;

    /**
     * Creates an empty screen
     */
    public Screen ()
    {
        participants = new LinkedList<Participant>();
        listeners = new HashSet<CollisionListener>();
        pendingAdds = new HashSet<Participant>();
        pendingRemoves = new HashSet<Participant>();
        legend = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Add a participant to the game
     */
    public void addParticipant (Participant p)
    {
        pendingAdds.add(p);
    }

    /**
     * Remove a participant from the game
     */
    public void removeParticipant (Participant p)
    {
        pendingRemoves.add(p);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics g)
    {
        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        for (Participant e : participants)
        {
            e.draw((Graphics2D) g);
        }

        // Draws the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
    }

    /**
     * Clear the screen so that nothing is displayed
     */
    public void clear ()
    {
        pendingRemoves.clear();
        pendingAdds.clear();
        participants.clear();
        legend = "";
    }

    /**
     * Records a new listener
     */
    public void addCollisionListener (CollisionListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Removes an existing listener.
     */
    public void removeCollisionListener (CollisionListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Compares each pair of elements to detect collisions, then notifies all
     * listeners of any found.
     */
    private void checkForCollisions ()
    {
        for (Participant p1 : participants)
        {
            Iterator<Participant> iter = participants.descendingIterator();
            while (iter.hasNext())
            {
                Participant p2 = iter.next();
                if (p2 == p1)
                    break;
                if (pendingRemoves.contains(p1))
                    break;
                if (pendingRemoves.contains(p2))
                    break;
                if (p1.overlaps(p2))
                {
                    for (CollisionListener listener : listeners)
                    {
                        listener.collidedWith(p1, p2);
                    }
                }
            }
        }
    }

    /**
     * Completes any adds and removes that have been requested.
     */
    private void completeAddsAndRemoves ()
    {
        // Note: These updates are saved up done later to avoid modifying
        // the participants list while it is being iterated over
        for (Participant p : pendingAdds)
        {
            participants.add(p);
        }
        pendingAdds.clear();
        for (Participant p : pendingRemoves)
        {
            participants.remove(p);
        }
        pendingRemoves.clear();
    }

    /**
     * Pauses all participants on the screen. Returns an ArrayList of the ship's
     * and asteroids' x- and y-speeds. For example,
     * [shipX,shipY,ast1X,ast1Y,ast2X,ast2Y,...]. Note: The order of the return
     * array depends on the order of the "participants" array.
     */
    public ArrayList<Double> pause ()
    {
        ArrayList<Double> speedsArray = new ArrayList<Double>();
        for (Participant p : participants)
        {
            // Save the speeds of the ship and asteroids
            if (p instanceof Ship || p instanceof Asteroid)
            {
                speedsArray.add(p.getSpeedX());
                speedsArray.add(p.getSpeedY());
            }
            // Set speeds of all participants to zero
            p.setSpeedX(0);
            p.setSpeedY(0);
        }
        return speedsArray;

    }

    /**
     * Unpauses the ship and asteroids by assigning each of them x- and y-speeds
     * that are contained in an ArrayList<Double> that is passed as the
     * parameter.
     */
    public void unpause (ArrayList<Double> speedsArray)
    {
        int speedIndex = 0;
        for (Participant p : participants)
        {
            if (p instanceof Ship || p instanceof Asteroid)
            {
                try
                {
                    p.setSpeedX(speedsArray.get(speedIndex));
                    p.setSpeedY(speedsArray.get(speedIndex + 1));
                    speedIndex += 2;
                }
                catch (IndexOutOfBoundsException e)
                {
                }
            }
        }
    }

    /**
     * Called when it is time to update the screen display. This is what drives
     * the animation.
     */
    public void refresh ()
    {
        completeAddsAndRemoves();
        for (Participant p : participants)
        {
            p.move();
        }
        checkForCollisions();
        repaint();
    }

}
