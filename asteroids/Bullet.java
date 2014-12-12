package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

import static asteroids.Constants.*;

/**
 * Represents bullet objects.
 * 
 * @author Jackson Murphy
 *
 */
public class Bullet extends Participant
{
    // The outline shape of a Bullet
    private Shape outline;

    /**
     * Creates a bullet.
     */
    public Bullet ()
    {
        outline = createBullet();

    }

    /**
     * Creates the shape of a Bullet object, which is represented by a 2x2 pixel
     * square centered at (0,0).
     */
    private Shape createBullet ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Create a square centered at (0,0)
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
