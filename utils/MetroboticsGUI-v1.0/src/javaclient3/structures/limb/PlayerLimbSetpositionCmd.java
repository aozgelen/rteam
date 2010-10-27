/*
 *  Player Java Client 3 - PlayerLimbSetpositionCmd.java
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
 * Command: Set end effector position (PLAYER_LIMB_SETPOSITION_CMD)
 * Set the position of the end effector without worrying about a
 * specific orientation. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLimbSetpositionCmd implements PlayerConstants {

    // Position of the end effector. 
    private float pX;
    // Position of the end effector. 
    private float pY;
    // Position of the end effector. 
    private float pZ;


    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPx () {
        return this.pX;
    }

    /**
     * @param newPx  Position of the end effector. 
     *
     */
    public synchronized void setPx (float newPx) {
        this.pX = newPx;
    }
    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPy () {
        return this.pY;
    }

    /**
     * @param newPy  Position of the end effector. 
     *
     */
    public synchronized void setPy (float newPy) {
        this.pY = newPy;
    }
    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPz () {
        return this.pZ;
    }

    /**
     * @param newPz  Position of the end effector. 
     *
     */
    public synchronized void setPz (float newPz) {
        this.pZ = newPz;
    }

}