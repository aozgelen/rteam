/*
 *  Player Java Client 3 - PlayerLogGetState.java
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

package javaclient3.structures.log;

import javaclient3.structures.*;

/**
 * Request/reply: Get state.
 * To find out whether logging/playback is enabled or disabled, send a null
 * PLAYER_LOG_REQ_GET_STATE request. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLogGetState implements PlayerConstants {

    // The type of log device, either @ref PLAYER_LOG_TYPE_READ or
//       @ref PLAYER_LOG_TYPE_WRITE 
    private byte type;
    // Logging/playback state: FALSE=disabled, TRUE=enabled 
    private byte state;


    /**
     * @return The type of log device, either PLAYER_LOG_TYPE_READ or
     * PLAYER_LOG_TYPE_WRITE 
     **/
    public synchronized byte getType () {
        return this.type;
    }

    /**
     * @param newType The type of log device, either PLAYER_LOG_TYPE_READ or
     * PLAYER_LOG_TYPE_WRITE 
     *
     */
    public synchronized void setType (byte newType) {
        this.type = newType;
    }
    /**
     * @return  Logging/playback state: FALSE=disabled, TRUE=enabled 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  Logging/playback state: FALSE=disabled, TRUE=enabled 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}