/*
 *  Player Java Client 3 - PlayerActarrayPositionCmd.java
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
 * Command: Joint position control (PLAYER_ACTARRAY_POS_CMD)
 * Tells a joint to attempt to move to a requested position. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerActarrayPositionCmd implements PlayerConstants {

    // The joint to command. 
    private byte joint;
    // The position to move to. 
    private float position;


    /**
     * @return  The joint to command. 
     **/
    public synchronized byte getJoint () {
        return this.joint;
    }

    /**
     * @param newJoint  The joint to command. 
     *
     */
    public synchronized void setJoint (byte newJoint) {
        this.joint = newJoint;
    }
    /**
     * @return  The position to move to. 
     **/
    public synchronized float getPosition () {
        return this.position;
    }

    /**
     * @param newPosition  The position to move to. 
     *
     */
    public synchronized void setPosition (float newPosition) {
        this.position = newPosition;
    }

}