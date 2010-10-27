/*
 *  Player Java Client 3 - PlayerLimbData.java
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
 * Data: state (PLAYER_LIMB_DATA)
 *   The limb data packet. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLimbData implements PlayerConstants {

	// The position of the end effector.
	private PlayerPoint3d position;
	// The approach vector of the end effector.
	private PlayerPoint3d approach;
	// The orientation vector of the end effector (a vector in a predefined 
	// direction on the end effector, generally from fingertip to fingertip).
	private PlayerPoint3d orientation;
	// The state of the limb.
	private byte state;

    /**
     * @return The position of the end effector. 
     **/
    public synchronized PlayerPoint3d getPosition () {
        return this.position;
    }

    /**
     * @param newPosition The position of the end effector. 
     *
     */
    public synchronized void setPosition (PlayerPoint3d newPosition) {
        this.position = newPosition;
    }
    
    /**
     * @return The approach vector of the end effector.
     **/
    public synchronized PlayerPoint3d getApproach () {
        return this.approach;
    }

    /**
     * @param newApproach The approach vector of the end effector.
     *
     */
    public synchronized void setApproach (PlayerPoint3d newApproach) {
        this.approach = newApproach;
    }
    
    /**
     * @return The orientation vector of the end effector.
     **/
    public synchronized PlayerPoint3d getOrientation () {
        return this.orientation;
    }

    /**
     * @param newOrientation The orientation vector of the end effector.
     *
     */
    public synchronized void setOrientation (PlayerPoint3d newOrientation) {
        this.orientation = newOrientation;
    }
    
    /**
     * @return  The state of the limb. 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  The state of the limb. 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}