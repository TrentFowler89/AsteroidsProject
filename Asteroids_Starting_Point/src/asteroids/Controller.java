package asteroids;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Debris;
import asteroids.participants.Ship;
import static asteroids.Constants.*;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    // The state of all the Participants
    private ParticipantState pstate;
    
    // The ship (if one is active) or null (otherwise)
    private Ship ship;
    
    // When this timer goes off, it is time to refresh the animation
    private Timer refreshTimer;

    // The time at which a transition to a new stage of the game should be made.
    // A transition is scheduled a few seconds in the future to give the user
    // time to see what has happened before doing something like going to a new
    // level or resetting the current level.
    private long transitionTime;
    
    // Number of lives left
    private int lives;
    
    //The current level
    private int level;
    
    //The current score
    private int score;

    // The game display
    private Display display;
    
    // The state of the Left Turn keys
	private boolean turnLeft = false;
	
	// The state of the Right Turn keys
	private boolean turnRight = false;
	
	// The state of the accelerate keys
	private boolean accelerate = false;

	// The state of the Alien Ship
	private AlienShip alienShip;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Record the game and screen objects
        display = new Display(this);
        display.setVisible(true);
        
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        refreshTimer.start();
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }
    
    /**
     * Returns the number of lives the player has
     */
    public int getLives()
    {
    	return this.lives;
    }
    
    /**
     * Returns the number of lives the player has
     */
    public int getLevel()
    {
    	return this.level;
    }
    
    /**
     * Returns the number of lives the player has
     */
    public int getScore()
    {
    	return this.score;
    }
    
    /**
     * Returns the value of the accelerate variable
     */
    public boolean getAccelerate() 
    {
		return accelerate;
	}

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship
     * first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }
    
    /**
     * Place a new alien ship in the center of the screen. Remove any existing ship
     * first.
     */
    private void placeAlienShip ()
    {
        // Expire previous Alien Ship
        Participant.expire(alienShip);
        
        if (level == 2)
        {
        alienShip = new AlienShip(SIZE, SIZE, 1, -Math.PI / 2, this);
        addParticipant(alienShip);
        }
        else if (level > 2)
        {
        	alienShip = new AlienShip(SIZE, SIZE, 0, -Math.PI / 2, this);
        	addParticipant(alienShip);
        }
        display.setLegend("");
    }

    /**
     * Places four asteroids near the corners of the screen. Gives them random
     * velocities and rotations.
     */
    private void placeAsteroids ()
    {
        addParticipant(new Asteroid(0, 0, EDGE_OFFSET, EDGE_OFFSET, 3.0, this));
        addParticipant(new Asteroid(1, 0, SIZE - EDGE_OFFSET, EDGE_OFFSET, 3.0, this));
        addParticipant(new Asteroid(2, 0, EDGE_OFFSET, SIZE - EDGE_OFFSET, 3.0, this));
        addParticipant(new Asteroid(3, 0, SIZE - EDGE_OFFSET, SIZE - EDGE_OFFSET, 3.0, this));
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
        alienShip = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Place four asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Reset statistics
        lives = 100;
        level = 1;
        score = 0;

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
        
        //refresh the display labels
        display.refreshLabels(this);
    }
    /**
     * Clears the screen and sets up the next level.
     */
    private void nextLevelScreen ()
    {
        //Update Level
    	this.level++;
    	
    	// Clear the screen
        clear();
        
        //Place a ship
        placeShip();

        // Place four asteroids
        placeAsteroids();
        
        //Update the Asteroid Speed
        pstate.updateAstroidSpeed(this.level);
        
        //Set a timer to place the alien ship 
        scheduleTransition(ALIEN_DELAY);

        // Give focus to the game screen
        display.requestFocusInWindow();
        
        //refresh the display labels
        display.refreshLabels(this);
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
    	//Create the floating Debris
        this.addParticipant(new Debris(ship));
        this.addParticipant(new Debris(ship));
        this.addParticipant(new Debris(ship));
        
    	// Null out the ship
        ship = null;
        
        // Display a legend
        display.setLegend("");
        
        //Reset Controls
        turnLeft = false;
        turnRight = false;
        accelerate = false;

        // Decrement lives
        lives--;
        
        //Update Labels
        display.refreshLabels(this);

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }
    
    public void alienShipDestroyed(int size) {
       	//Create the floating Debris
        this.addParticipant(new Debris(alienShip));
        this.addParticipant(new Debris(alienShip));
        this.addParticipant(new Debris(alienShip));
        this.addParticipant(new Debris(alienShip));
        this.addParticipant(new Debris(alienShip));
        this.addParticipant(new Debris(alienShip));
        
    	// Null out the alien ship
        alienShip = null;

        //Update the Score
    	score+=ALIENSHIP_SCORE[size];
        
        //Update Labels
        display.refreshLabels(this);
        
        // Since the ship was destroyed, schedule a transition
        scheduleTransition(ALIEN_DELAY);
	}

    /**
     * An asteroid of the given size has been destroyed
     */
    public void asteroidDestroyed (int size)
    {
    	//Update the Score
    	score+=ASTEROID_SCORE[size];
    	display.refreshLabels(this);
    	
    	// If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            scheduleTransition(END_DELAY);
        }
    }
    
 

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();
            
            if(ship != null)
            {
            	if(turnLeft == true)
            	{
            		ship.turnLeft();
            	}
            	if(turnRight == true)
        		{
            		ship.turnRight();
        		}
            	
            	if(accelerate == true)
            	{
            		ship.accelerate();
            	}
            }

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                finalScreen();
            }
            
            // If all the asteroids have been destroyed, go to the next level
            else if(pstate.countAsteroids() == 0)
            {
            	nextLevelScreen();
            }

            // If the ship was destroyed, place a new one and continue
            else if (ship == null)
            {
                placeShip();
                
                //if the ship was destroyed by and alien ship, both are created
                if (alienShip == null && level > 1) {
    				placeAlienShip();
    			}
            }
            
			// If there is no alien ship, place a new one
			else if (alienShip == null && level > 1) {
				placeAlienShip();
			}
            
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            this.turnLeft = true;
        }
        else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
        	this.turnRight = true;
        }
        else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
        	this.accelerate = true;
        }
        else if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && ship != null)
        {
        	if(pstate.countBullets() < BULLET_LIMIT)
        	{
        		addParticipant(new Bullet(ship));
        	}	
        }
    }

    /**
     * Ignore these events.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * Ignore these events.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {     
    	if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
         {
             this.turnLeft = false;
         }
    	else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
         {
         	this.turnRight = false;
         }
    	else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
         {
         	this.accelerate = false;
         }
    }
}
