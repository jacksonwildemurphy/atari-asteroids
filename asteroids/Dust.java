package asteroids;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Represents Dust objects.
 * 
 * @author Jackson Murphy
 */
public class Dust extends Participant
{
    
    // The outline of a dust object
    private Shape outline;
    
    /**
     * Constructs a dust object
     */
    public Dust () {
        outline = createDust();
    }
  
    /**
     * Creates the shape of a dust object, which is represented by a 2x2 pixel
     * square centered at (0,0).
     */
    private Shape createDust () {
        
     // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Create a square whose center coords. are (0,0)
        poly.moveTo(1, -1);
        poly.lineTo(1, -1);
        poly.lineTo(1, 1);
        poly.lineTo(-1, 1);
        poly.closePath();

        return poly;
    }
    
    public Shape getOutline() {
        
        return outline;
    }
  
    

}
