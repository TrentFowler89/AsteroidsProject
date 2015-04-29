package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
import static asteroids.Constants.*;

public class Debris extends Participant {

	// The outline of the Debris
    private Shape outline;
        
    // Constructs debris in the center of the ship.
    public Debris (Ship ship) 
    {   
        
        setPosition(ship.getX() , ship.getY());
        
        
        setVelocity(0.5, 2 * Math.PI * RANDOM.nextDouble());
        setRotation(RANDOM.nextDouble());
        setDirection(2 * Math.PI * RANDOM.nextDouble());
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(RANDOM.nextInt(25), RANDOM.nextInt(25));
        poly.lineTo(RANDOM.nextInt(25), RANDOM.nextInt(25));
        poly.closePath();
        outline = poly;
        
        // Schedule an acceleration in two seconds
        new ParticipantCountdownTimer(this, "remove", 2000);
    }
    
 // Constructs debris in the center of the alien ship.
    public Debris (AlienShip ship) 
    {   
        
        setPosition(ship.getX() , ship.getY());
        
        
        setVelocity(0.5, 2 * Math.PI * RANDOM.nextDouble());
        setRotation(RANDOM.nextDouble());
        setDirection(2 * Math.PI * RANDOM.nextDouble());
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(RANDOM.nextInt(25), RANDOM.nextInt(25));
        poly.lineTo(RANDOM.nextInt(25), RANDOM.nextInt(25));
        poly.closePath();
        outline = poly;
        
        // Schedule an acceleration in two seconds
        new ParticipantCountdownTimer(this, "remove", 2000);
    }


    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Calls the base move method
     */
    public void move ()
    {
    	super.move();
    }


    /**
     * Necessary Implementation of an unnecessary method
     */
    @Override
    public void collidedWith (Participant p)
    {
    	
    }
    
    /**
     * This method is invoked when a ParticipantCountdownTimer completes
     * its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Remove a dust particle after a given amount of time
        if (payload.equals("remove"))
        {
        	// Expire the bullet from the game
            Participant.expire(this);
           
        }
    }
}
