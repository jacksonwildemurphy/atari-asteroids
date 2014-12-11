package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Represents Debris objects. This is a subclass of asteroids.Participants
 * 
 * @author Jackson Murphy. 12/11/14
 */
public class Debris extends Participant
{

    // The outline of a debris object
    private Shape outline;

    /**
     * Constructs a debris object
     */
    public Debris ()
    {
        outline = createDebris();
    }

    /**
     * Creates the shape of a debris object, which is represented by a rectangle
     * centered at (0,0) with a height of 14 and a width of 2 pixels.
     */
    private Shape createDebris ()
    {

        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Create a rectangle centered at (0,0)
        poly.moveTo(-7, 0);
        poly.lineTo(7, -1);
        poly.lineTo(7, 1);
        poly.closePath();

        return poly;
    }

    public Shape getOutline ()
    {

        return outline;
    }

}
