package asteroids.participants;

import java.awt.Shape;

import asteroids.Participant;
import asteroids.destroyers.*;
import java.awt.geom.*;

import asteroids.ParticipantCountdownTimer;

import static asteroids.Constants.*;

/**
 * Represents bullet
 */
public class AlienBullet extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    // The outline of the bullet
    private Shape outline;
        
    // Constructs a bullet at the center of the ship
    // that is pointed in the given direction.
    public AlienBullet (AlienShip ship, Double direction) //, double direction, int speed, Controller controller)
    {   
        
        setPosition(ship.getX() , ship.getY());
        setVelocity(BULLET_SPEED, direction);
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(1, 1);
        poly.lineTo(-1, 1);
        poly.lineTo(-1, -1);
        poly.lineTo(1, -1);
        poly.closePath();
        outline = poly;
        
        // Schedule the removal of the bullet
        new ParticipantCountdownTimer(this, "remove", BULLET_DURATION);
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
     * When a bullet collides with a BulletDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienBulletDestroyer)
        {
            // Expire the bullet from the game
            Participant.expire(this);
        }
    }
    
    /**
     * This method is invoked when a ParticipantCountdownTimer completes
     * its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Remove the bullet
        if (payload.equals("remove"))
        {
        	// Expire the bullet from the game
            Participant.expire(this); 
        }
    }
}



