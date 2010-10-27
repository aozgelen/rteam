/*
 *  Player Java Client 3 - HeadingControl.java
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

/**
 * Heading control interface for Position, Position2D and Position3D Player
 * interfaces. Uses methods from both player interfaces and PIDController.
 * @author Radu Bogdan Rusu & Marius Borodi
 */
public class HeadingControl extends PIDController {

	private static final boolean isDebugging = PlayerClient.isDebugging;

	private AbstractPositionDevice device = null;

	/* PID coefficients */
	private int Kp = 1;
	private int Ki = 0;
	private int Kd = 0;

	private boolean stop = false;

	/* minimum and maximum admissible commands */
	private double minCommand = 1;
	private double maxCommand = 180;
	/* maximum allowed error */
	private double maxError   = 0;

	/**
	 * Constructor for HeadingControl.
	 * @param pd a reference to a PlayerDevice interface (Position, Position2D
	 * or Position3D).
	 */
	public HeadingControl (AbstractPositionDevice pd) {
		super (1, 0, 0);
		this.device = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * @param pd a reference to a PlayerDevice interface (Position, Position2D
	 * or Position3D).
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the derivative constant
	 */
	public HeadingControl (AbstractPositionDevice pd,
			int kp, int ki, int kd) {
		super (kp, ki, kd);
		this.Kp     = kp;
		this.Ki     = ki;
		this.Kd     = kd;
		this.device = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * @param pd a reference to a PlayerDevice interface (Position, Position2D
	 * or Position3D).
	 * @param minC minimum admissible command for the robot's motors
	 * @param maxC maximum admissible command for the robot's motors
	 */
	public HeadingControl (AbstractPositionDevice pd, int minC, int maxC) {
		super (1, 0, 0);
		this.minCommand = minC;
		this.maxCommand = maxC;
		this.device     = pd;
	}

	/**
	 * Constructor for HeadingControl.
	 * @param pd a reference to a PlayerDevice interface (Position, Position2D
	 * or Position3D).
	 * @param minC minimum admissible command for the robot's motors
	 * @param maxC maximum admissible command for the robot's motors
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the derivative constant
	 */
	public HeadingControl (AbstractPositionDevice pd, int minC, int maxC,
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
	public void setMinimumCommand (double minC) {
		this.minCommand = minC;
	}

	/**
	 * Set the maximum admissible command for the robot's motors.
	 * @param maxC maximum admissible command as an integer
	 */
	public void setMaximumCommand (double maxC) {
		this.maxCommand = maxC;
	}

	/**
	 * Stop the robot from moving.
	 */
	public void stopRobot () {
		this.stop = true;
	}

	/**
	 * Set the maximum allowed error between the final goal and
	 * the current position. (default error is 0)
	 * @param err maximum allowed error as an integer
	 */
	public void setAllowedError (double err) {
		this.maxError = err;
	}

	/**
	 * Calculate and return the controller's command for the controlled system.
	 * @param currentOutput the current output of the system
	 * @return the new calculated command for the system
	 */
	public double getCommand (double currentOutput) {
		this.currE = this.goal - currentOutput;

		/* Angle adjustments */
		if (currE  <= -180 )
			currE =  360 + currE;
		else
			if(currE >= 180  && currE <= 360)
				currE = currE - 360;
			else
				if(currE > 360)
					currE = currE - 360;

		eSum += currE;

		lastE = currE;

		double Pgain = this.Kp * currE;
		double Igain = this.Ki * eSum;
		double Dgain = this.Kd * deltaE ();

		return Pgain + Igain + Dgain;
	}

	/**
	 * Bound the output command to the minimum and maximum admissible commands.
	 * @param command command to bound
	 * @return new bounded command
	 */
	private double boundCommand (double command) {
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
	 * Angle transformations, used internally.
	 * @param angle angle to transform
	 * @return new transformed angle
	 */
	private double transformAngle (double angle) {
		angle = angle % 360;
		if (angle < 0)
			angle = 360 + angle;
		return angle;
	}

	/**
	 * Rotate the robot on spot (differential heading) with a desired heading.
	 * @param angle angle for rotation
	 * @return false in case the rotation was interrupted, true otherwise
	 */
	public boolean setDiffHeading (double angle) {
		if (angle == 0)
			return true;

		stop        = false;
		boolean ret = true;
		/* get the current heading */
		double currentHead = transformAngle (device.getYaw ());
		/* calculate the goal heading */
		double newGoal     = transformAngle (currentHead + angle);

		setGoal (newGoal);

		double now = transformAngle (device.getYaw ());

		/* keep rotating while the goal was not reached */
		while (now != newGoal) {
			if (stop == true) {
				ret = false;
				break;
			}

			/* no point in rotating at all if we're at +/-180 */
			if (Math.abs (now - newGoal) <= 1 && newGoal == 180)
				break;                  /* exit if we reached our destination */

			/* in case a diff. of maxError (default 0) between angles is acceptable */
			if (Math.abs (now - newGoal) <= maxError)
				break;                  /* exit if we reached our destination */

			/* get the current heading */
			now = transformAngle (device.getYaw ());

			/* get the motor command and check if within the desired limits */
			double command = getCommand (now);
			command = boundCommand (command);
			device.setSpeed (0, command);

			try { Thread.sleep (100); } catch (Exception e) {}
			if (isDebugging)
				System.err.println ("[HeadingControl][Debug] Angle left : " +
						Math.abs (now - newGoal));
		}
		device.setSpeed (0, 0);         /* stop the robot from rotating */

		return ret;
	}

	/**
	 * Rotate the robot on spot (absolute heading) to the desired heading.
	 * @param angle goal angle
	 * @return false in case the rotation was interrupted, true otherwise
	 */
	public boolean setHeading (double angle) {
		/* get the current heading */
	    double currentAngle =  transformAngle (device.getYaw ());

		/* difference between the current heading and the goal heading */
	    double deltaAngle   = (angle - currentAngle);

		if (deltaAngle != 0) {
			if (deltaAngle <= 180 && deltaAngle > 0)
				return setDiffHeading (deltaAngle);
			else
				if (deltaAngle > -180)
					return setDiffHeading (-360 + deltaAngle);
				else
					return setDiffHeading (360 + deltaAngle);
		}
		return true;
	}
}
