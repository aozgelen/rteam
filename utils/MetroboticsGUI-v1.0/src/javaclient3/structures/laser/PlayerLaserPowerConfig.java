/*
 *  Player Java Client 3 - PlayerLaserPowerConfig.java
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

package javaclient3.structures.laser;

import javaclient3.structures.*;

/**
 * Request/reply: Turn power on/off.
 * Send a PLAYER_LASER_REQ_POWER request to turn laser power on or off 
 * (assuming your hardware supports it). 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLaserPowerConfig implements PlayerConstants {

    // FALSE to turn laser off, TRUE to turn laser on 
    private byte state;


    /**
     * @return  FALSE to turn laser off, TRUE to turn laser on 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  FALSE to turn laser off, TRUE to turn laser on 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}