package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
import static asteroids.Constants.*;

public class Flame extends Participant {

	// The outline of the Debris
    private Shape outline;
        
    // Constructs a bullet at the nose of the ship
    // that is pointed in the given direction.
    public Flame (Ship ship) 
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
       
        //Ellipse2D.Double circle = new Ellipse2D.Double(ship.getXNose(), ship.getYNose(), 1.0, 1.0);
        //outline = circle;
        
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
