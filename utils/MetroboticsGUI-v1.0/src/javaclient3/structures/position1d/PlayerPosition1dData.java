/*
 *  Player Java Client 3 - PlayerPosition1dData.java
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

package javaclient3.structures.position1d;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_POSITION1D_DATA_STATE)
 * The position interface returns data regarding the odometric pose and
 * velocity of the robot, as well as motor stall information. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition1dData implements PlayerConstants {

    // position [m] or [rad] depending on actuator type
    private float pos;
    // translational velocities [m/s] or [rad/s] depending on actuator type
    private float vel;
    // Is the motor stalled? 
    private byte stall;
    // bitfield of extra data in the following order:
    // - status (unsigned byte)
    // - bit 0: limit min
    // - bit 1: limit center
    // - bit 2: limit max
    // - bit 3: over current
    // - bit 4: trajectory complete
    // - bit 5: is enabled
    // - bit 6:
    // - bit 7:
    private byte status;

    /**
     * @return position [m] or [rad] depending on actuator type
     **/
    public synchronized float getPos () {
        return this.pos;
    }

    /**
     * @param newPos position [m] or [rad] depending on actuator type
     *
     */
    public synchronized void setPos (float newPos) {
        this.pos = newPos;
    }
    
    /**
     * @return translational velocities [m/s] or [rad/s] depending on actuator 
     * type
     **/
    public synchronized float getVel () {
        return this.vel;
    }

    /**
     * @param newVel translational velocities [m/s] or [rad/s] depending on 
     * actuator type
     *
     */
    public synchronized void setVel (float newVel) {
        this.vel = newVel;
    }
    
    /**
     * @return  Is the motor stalled? 
     **/
    public synchronized byte getStall () {
        return this.stall;
    }

    /**
     * @param newStall  Is the motor stalled? 
     *
     */
    public synchronized void setStall (byte newStall) {
        this.stall = newStall;
    }
    
    /**
     * @return bitfield of extra data
     **/
    public synchronized byte getStatus () {
        return this.status;
    }

    /**
     * @param newStatus bitfield of extra data 
     *
     */
    public synchronized void setStatus (byte newStatus) {
        this.status = newStatus;
    }
}