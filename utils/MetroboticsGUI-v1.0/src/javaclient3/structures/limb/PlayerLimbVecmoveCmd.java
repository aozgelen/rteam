/*
 *  Player Java Client 3 - PlayerLimbVecmoveCmd.java
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

package javaclient3.structures.limb;

import javaclient3.structures.*;

/**
 * Command: Vector move the end effector (PLAYER_LIMB_VECMOVE_CMD)
 * Move the end effector along the provided vector from its current
 * position for the provided distance. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLimbVecmoveCmd implements PlayerConstants {

    // Direction vector to move in. 
    private float x;
    // Direction vector to move in. 
    private float y;
    // Direction vector to move in. 
    private float z;
    // Distance to move. 
    private float length;


    /**
     * @return  Direction vector to move in. 
     **/
    public synchronized float getX () {
        return this.x;
    }

    /**
     * @param newX  Direction vector to move in. 
     *
     */
    public synchronized void setX (float newX) {
        this.x = newX;
    }
    /**
     * @return  Direction vector to move in. 
     **/
    public synchronized float getY () {
        return this.y;
    }

    /**
     * @param newY  Direction vector to move in. 
     *
     */
    public synchronized void setY (float newY) {
        this.y = newY;
    }
    /**
     * @return  Direction vector to move in. 
     **/
    public synchronized float getZ () {
        return this.z;
    }

    /**
     * @param newZ  Direction vector to move in. 
     *
     */
    public synchronized void setZ (float newZ) {
        this.z = newZ;
    }
    /**
     * @return  Distance to move. 
     **/
    public synchronized float getLength () {
        return this.length;
    }

    /**
     * @param newLength  Distance to move. 
     *
     */
    public synchronized void setLength (float newLength) {
        this.length = newLength;
    }

}