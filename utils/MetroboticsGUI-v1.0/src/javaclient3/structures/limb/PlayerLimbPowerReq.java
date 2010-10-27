/*
 *  Player Java Client 3 - PlayerLimbPowerReq.java
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
 * Request/reply: Power.
 * Turn the power to the limb by sending a PLAYER_LIMB_POWER_REQ request. Be
 * careful when turning power on that the limb is not obstructed from its
 * home position in case it moves to it (common behaviour). Null reponse
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLimbPowerReq implements PlayerConstants {

    // Power setting; 0 for off, 1 for on. 
    private byte value;


    /**
     * @return  Power setting; 0 for off, 1 for on. 
     **/
    public synchronized byte getValue () {
        return this.value;
    }

    /**
     * @param newValue  Power setting; 0 for off, 1 for on. 
     *
     */
    public synchronized void setValue (byte newValue) {
        this.value = newValue;
    }

}