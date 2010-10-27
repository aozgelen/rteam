/*
 *  Player Java Client 3 - PlayerPosition2dCmdVel.java
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
 * Command: velocity (PLAYER_POSITION2D_CMD_VEL)
 * The position interface accepts new velocities
 * for the robot's motors (drivers may support position control, speed control,
 * or both). 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition2dCmdVel implements PlayerConstants {

    // translational velocities [m/s,m/s,rad/s] (x, y, yaw)
    private PlayerPose vel;
    // Motor state (FALSE is either off or locked, depending on the driver). 
    private byte state;


    /**
     * @return  translational velocities [m/s,m/s,rad/s] (x, y, yaw)
     **/
    public synchronized PlayerPose getVel () {
        return this.vel;
    }

    /**
     * @param newVel  translational velocities [m/s,m/s,rad/s] (x, y, yaw)
     *
     */
    public synchronized void setVel (PlayerPose newVel) {
        this.vel = newVel;
    }
    /**
     * @return  Motor state (FALSE is either off or locked, depending on the driver). 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  Motor state (FALSE is either off or locked, depending on the driver). 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}