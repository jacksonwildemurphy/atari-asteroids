package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

import static asteroids.Constants.*;

/**
 * Represents bullet objects.
 * 
 * @author Jackson Murphy. Created 12/2/14.
 *
 */
public class Bullet extends Participant
{
    // The outline shape of a Bullet
    Shape outline;

    /**
     * Creates a bullet.
     */
    public Bullet ()
    {
        outline = createBullet();

    }

    /**
     * Creates the shape of a Bullet object, which is represented by a 2x2 px
     * square centered at (0,0).
     */
    private Shape createBullet ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Create a square whose center coords. are (x,y)
        poly.moveTo(1, -1);
        poly.lineTo(1, -1);
        poly.lineTo(1, 1);
        poly.lineTo(-1, 1);
        poly.closePath();

        return poly;
    }

    /**
     * Returns the outline of a Bullet object.
     */
    @Override
    Shape getOutline ()
    {
        return outline;
    }

}
