/*
 *  Player Java Client 3 - PlayerPosition2dCmdCar.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
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

package javaclient3.structures.position2d;

import javaclient3.structures.*;

/**
 * Command: carlike (PLAYER_POSITION2D_CMD_CAR)
 * The position interface accepts new carlike velocity commands (speed and turning angle)
 * for the robot's motors (only supported by some drivers). 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition2dCmdCar implements PlayerConstants {

    // forward velocity (m/s) 
    private double velocity;
    // turning angle (rad) 
    private double angle;


    /**
     * @return  forward velocity (m/s) 
     **/
    public synchronized double getVelocity () {
        return this.velocity;
    }

    /**
     * @param newVelocity  forward velocity (m/s) 
     *
     */
    public synchronized void setVelocity (double newVelocity) {
        this.velocity = newVelocity;
    }
    /**
     * @return  turning angle (rad) 
     **/
    public synchronized double getAngle () {
        return this.angle;
    }

    /**
     * @param newAngle  turning angle (rad) 
     *
     */
    public synchronized void setAngle (double newAngle) {
        this.angle = newAngle;
    }

}