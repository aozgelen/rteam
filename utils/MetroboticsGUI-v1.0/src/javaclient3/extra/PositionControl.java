/*
 *  Player Java Client 3 - PositionControl.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu, Maxim Batalin
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id$
 *
 */
package javaclient3.extra;

import javaclient3.AbstractPositionDevice;
import javaclient3.PlayerClient;

import java.awt.Point;

/**
 * Position control interface for Position, Position2D and Position3D Player
 * interfaces. Uses methods from both player interfaces and PIDController.
 * @author Marius Borodi & Radu Bogdan Rusu
 */
public class PositionControl extends PIDController {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    private AbstractPositionDevice device = null;

    /* PID coefficients */
    private int Kp = 1;
    private int Ki = 0;
    private int Kd = 0;

    private boolean stop = false;

    /* minimum and maximum admissible commands */
    private int minCommand = 1;
    private int maxCommand = 100;
    /* maximum allowed error */
    private int maxError   = 10;

    /**
      * Constructor for PositionControl.
      * @param pd a reference to a PlayerDevice interface (Position, Position2D
      * or Position3D).
      */
    public PositionControl (AbstractPositionDevice pd) {
    	super (1, 0, 0);
    	this.device = pd;
    }

    /**
     * Constructor for PositionControl.
     * @param pd a reference to a PlayerDevice interface (Position, Position2D
     * or Position3D).
     * @param kp the proportional constant
     * @param ki the integral constant
     * @param kd the derivative constant
     */
    public PositionControl (AbstractPositionDevice pd,
                            int kp, int ki, int kd) {
        super (kp, ki, kd);
        this.Kp     = kp;
        this.Ki     = ki;
        this.Kd     = kd;
    	this.device = pd;
    }

    /**
     * Constructor for PositionControl.
     * @param pd a reference to a PlayerDevice interface (Position, Position2D
     * or Position3D).
     * @param minC minimum admissible command for the robot's motors
     * @param maxC maximum admissible command for the robot's motors
     */
    public PositionControl (AbstractPositionDevice pd, int minC, int maxC) {
    	super (1, 0, 0);
        this.minCommand = minC;
        this.maxCommand = maxC;
    	this.device     = pd;
    }

    /**
     * Constructor for PositionControl.
     * @param pd a reference to a PlayerDevice interface (Position, Position2D
     * or Position3D).
     * @param minC minimum admissible command for the robot's motors
     * @param maxC maximum admissible command for the robot's motors
     * @param kp the proportional constant
     * @param ki the integral constant
     * @param kd the derivative constant
     */
    public PositionControl (AbstractPositionDevice pd, int minC, int maxC,
                            int kp, int ki, int kd) {
        super (kp, ki, kd);
        this.minCommand = minC;
        this.maxCommand = maxC;
        this.Kp         = kp;
        this.Ki         = ki;
        this.Kd         = kd;
        this.device     = pd;
    }

    /**
     * Set the minimum admissible command for the robot's motors.
     * @param minC minimum admissible command as an integer
     */
    public void setMinimumCommand (int minC) {
       this.minCommand = minC;
    }

    /**
     * Set the maximum admissible command for the robot's motors.
     * @param maxC maximum admissible command as an integer
     */
    public void setMaximumCommand (int maxC) {
       this.maxCommand = maxC;
    }

    /**
     * Set the maximum allowed error between the final goal and
     * the current position. (default error is 10)
     * @param err maximum allowed error as an integer
     */
    public void setAllowedError (int err) {
    	this.maxError = err;
    }

    /**
     * Stop the robot from moving.
     */
    public void stopRobot () {
        this.stop = true;
    }

    /**
     * Get the current robot position as a Point (AWT).
     * @return the current robot position
     */
    public Point getRobotPosition () {
    	Point p = new Point ();
    	p.setLocation (device.getX (), device.getY ());
    	return p;
    }

    /**
     * Bound the output command to the minimum and maximum admissible commands.
     * @param command command to bound
     * @return new bounded command
     */
    private int boundCommand (int command) {
        if (command == 0)
            return 0;
        if (command < 0) {
            if (command > -minCommand)
                command = -minCommand;
            if (command < -maxCommand)
                command = -maxCommand;
        }
        else {
            if (command < minCommand)
                command = minCommand;
            if (command > maxCommand)
                command = maxCommand;
        }
        return command;
    }

    /**
      * Move the robot for a given distance to a new destination.
      * @param distance the desired distance
      * @return false in case the movement was interrupted, true otherwise
      */
    public boolean moveRobot (int distance) {
        stop          = false;
        boolean ret   = true;
        double dist   = distance;
    	double angle  = device.getYaw    ();      // get the current heading
    	Point roboPos = getRobotPosition ();      // get the current position

        // find out the destination point using distance and current angle
    	Point dest = PositionGeometryTools.calcDistPoint (roboPos, distance, angle);
        // get the distance from Dest to roboPos
    	dist       = PositionGeometryTools.calcDist (dest, roboPos);

    	setGoal (0);
    	int sgn = -1;
    	if (distance < 0 )
    		sgn = 1;

    	/* move to the goal, minimize distance */
    	while (dist > 0) {
            if (stop == true) {
            	ret = false;
                break;
            }
    		roboPos = getRobotPosition ();        /* get current position */
            /* get the distance from Dest to roboPos */
    		dist    = PositionGeometryTools.calcDist (dest, roboPos);
    		int erX = roboPos.x - dest.x;
    		int erY = roboPos.y - dest.y;

            /* in case a diff. of maxError (default 10) to the goal is acceptable */
    		if (dist <= maxError)
    			break;                           /* exit if we reached our destination */

    		if ((erX == 0 && Math.abs (erY) <= 10) || (erY == 0 && Math.abs (erX) <= 10))
    			break;                           /* exit if we reached our destination */

            /* get the motor command and check if within the desired limits */
    		int command = (int)getCommand (dist);
            command = boundCommand (command);
    		device.setSpeed ((sgn) * command, 0);

    		try { Thread.sleep(100); } catch (Exception e) { }
            if (isDebugging)
            	System.err.println ("[PositionControl][Debug] Distance left : " + dist);
    	}
    	device.setSpeed (0, 0);                 /* stop the robot from moving */
    	roboPos = getRobotPosition ();          /* get current robot position */
    	dist = PositionGeometryTools.calcDist (dest, roboPos);
        if (dist != 0)                          /* send a warning in case of errors */
            if (isDebugging)
            	System.err.println ("[PositionControl][Debug] Distance error : " + dist);

        return ret;
    }
    }
