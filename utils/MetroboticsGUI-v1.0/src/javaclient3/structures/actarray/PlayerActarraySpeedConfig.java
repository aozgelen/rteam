/*
 *  Player Java Client 3 - PlayerActarraySpeedConfig.java
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

package javaclient3.structures.actarray;

import javaclient3.structures.*;

/**
 * Request/reply: Speed.
 * Send a PLAYER_ACTARRAY_SPEED_REQ request to set the speed of a joint for
 * all subsequent movements. Null response. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerActarraySpeedConfig implements PlayerConstants {

    // Joint to set speed for. 
    private char joint;
    // Speed setting in mrad/s. 
    private float speed;


    /**
     * @return  Joint to set speed for. 
     **/
    public synchronized char getJoint () {
        return this.joint;
    }

    /**
     * @param newJoint  Joint to set speed for. 
     *
     */
    public synchronized void setJoint (char newJoint) {
        this.joint = newJoint;
    }
    /**
     * @return  Speed setting in mrad/s. 
     **/
    public synchronized float getSpeed () {
        return this.speed;
    }

    /**
     * @param newSpeed  Speed setting in mrad/s. 
     *
     */
    public synchronized void setSpeed (float newSpeed) {
        this.speed = newSpeed;
    }

}