package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.*;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
import asteroids.destroyers.*;
import static asteroids.Constants.*;

/**
 * Represents ships
 */
public class AlienShip extends Participant implements AsteroidDestroyer, ShipDestroyer, BulletDestroyer
{
    // The outline of the ship
    private Shape shipOutline;

    // Game controller
    private Controller controller;
    
    //Alien ship size
    private int size;

	

    // Constructs a ship at the specified coordinates
    // that is pointed in the given direction.
    public AlienShip (int x, int y, int size, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setVelocity(4.0, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(Math.PI);

       //Creates an alien ship
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(7, -12);
        poly.lineTo(-7, -12);
        poly.lineTo(-20 , 0);
        poly.lineTo(20, 0);
        poly.lineTo(7, 12);
        poly.lineTo(-7, 12);
        poly.lineTo(-5, 20);
        poly.lineTo(5, 20);
        poly.lineTo(7, 12);
        poly.lineTo(-7, 12);
        poly.lineTo(-20, 0);
        poly.closePath();
        

     // Scale to the desired size
        double scale = ALIENSHIP_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));
        
        shipOutline = poly;
        
        new ParticipantCountdownTimer(this, "fire", 200);
        new ParticipantCountdownTimer(this, "moveUp", 400);
        new ParticipantCountdownTimer(this, "moveDown", 600);
        new ParticipantCountdownTimer(this, "moveLevel", 800);
    }

    @Override
    protected Shape getOutline ()
    {
        return shipOutline;
    }
    
    /**
     * Returns the size of the Alien Ship
     */
    public int getSize()
    {
    	return size;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        super.move();
    }

 

    /**
     * When a Ship collides with a ShipKiller, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.alienShipDestroyed(getSize());
        }
    }
    
    /**
     * This method is invoked when a ParticipantCountdownTimer completes
     * its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Fires an Alien bullet
        if (payload.equals("fire"))
        {
        	if(controller.getLevel() == 2)
        	{
        		controller.addParticipant(new AlienBullet(this, Math.PI*RANDOM.nextDouble()));
        	}
        	else
        	{
        		try
        		{
        			Ship ship = controller.getShip();
        			double direction = Math.atan((this.getY() - ship.getY())/(this.getX() - ship.getX()));
        			System.out.println("Alien Ship: " + this.getX() + ", " + this.getY());
        			System.out.println("Ship: " + ship.getX() + ", " + ship.getY());
        			System.out.println(Math.toDegrees(direction) + " Degrees");
        			System.out.println("");
        			controller.addParticipant(new AlienBullet(this, direction));
        		}
        		catch (NullPointerException e)
        		{
        			
        		}
        	}
           
            new ParticipantCountdownTimer(this, "fire", 2000);
        }
        else if (payload.equals("moveUp"))
        {
        	setDirection(Math.PI/8);
        	new ParticipantCountdownTimer(this, "moveUp", 200);
        }
        else if (payload.equals("moveDown"))
        {
        	setDirection(Math.PI/8);
        	new ParticipantCountdownTimer(this, "moveDown", 200);
        }
        else if (payload.equals("moveLevel"))
        {
        	setDirection(Math.PI/8);
        	new ParticipantCountdownTimer(this, "moveLevel", 200);
        }
    }
}
