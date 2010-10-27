/*
 *  Player Java Client 3 - PlayerPtzReqGeneric.java
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

package javaclient3.structures.ptz;

import javaclient3.structures.*;

/**
 * Request/reply: Generic request
 * To send a unit-specific command to the unit, use the
 * PLAYER_PTZ_REQ_GENERIC request.  Whether data is returned depends on the
 * command that was sent.  The device may fill in "config" with a reply if
 * applicable. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPtzReqGeneric implements PlayerConstants {

    // Length of data in config buffer 
    private int config_count;
    // Buffer for command/reply 
    private int[] config = new int[PLAYER_PTZ_MAX_CONFIG_LEN];


    /**
     * @return  Length of data in config buffer 
     **/
    public synchronized int getConfig_count () {
        return this.config_count;
    }

    /**
     * @param newConfig_count  Length of data in config buffer 
     *
     */
    public synchronized void setConfig_count (int newConfig_count) {
        this.config_count = newConfig_count;
    }
    /**
     * @return  Buffer for command/reply 
     **/
    public synchronized int[] getConfig () {
        return this.config;
    }

    /**
     * @param newConfig  Buffer for command/reply 
     *
     */
    public synchronized void setConfig (int[] newConfig) {
        this.config = newConfig;
    }

}