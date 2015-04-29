package asteroids.participants;

import java.awt.Shape;

import asteroids.Participant;
import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.BulletDestroyer;

import java.awt.geom.*;

import asteroids.ParticipantCountdownTimer;
import static asteroids.Constants.*;

/**
 * Represents bullet
 */
public class Bullet extends Participant implements AsteroidDestroyer, AlienShipDestroyer
{
    // The outline of the bullet
    private Shape outline;
        
    // Constructs a bullet at the nose of the ship
    // that is pointed in the given direction.
    public Bullet (Ship ship) //, double direction, int speed, Controller controller)
    {   
        //double shipNoseX = ship.getXNose();
        //double shipNoseY = ship.getYNose();
        
        setPosition(ship.getXNose() , ship.getYNose());
        
        //setRotation(direction);
        setVelocity(BULLET_SPEED, ship.getRotation());
        //setRotation(2 * Math.PI * RANDOM.nextDouble());
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(1, 1);
        poly.lineTo(0, 1);
        poly.lineTo(0, 0);
        poly.lineTo(1, 0);
        poly.closePath();
        outline = poly;
       
        //Ellipse2D.Double circle = new Ellipse2D.Double(ship.getXNose(), ship.getYNose(), 1.0, 1.0);
        //outline = circle;
        
        // Schedule an acceleration in two seconds
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
        if (p instanceof BulletDestroyer)
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
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("remove"))
        {
        	// Expire the bullet from the game
            Participant.expire(this);
           
        }
    }
}



