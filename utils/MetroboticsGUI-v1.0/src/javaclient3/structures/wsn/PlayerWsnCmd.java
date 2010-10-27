/*
 *  Player Java Client 3 - PlayerWsnCmd.java
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
 * $Id: PlayerWsnCmd.java,v 1.0 2006/03/26 22:15:06 veedee Exp $
 *
 */

package javaclient3.structures.wsn;

import javaclient3.structures.*;

/**
 * Command: set LEDs state (PLAYER_WSN_CMD_LEDSTATE).
 * <br><br>
 * This WSN command sets the state of the node's indicator lights.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWsnCmd implements PlayerConstants {

    // The ID of the WSN node. -1 for all.
    private int  node_id;
    // The Group ID of the WSN node. -1 for all.
    private int group_id;
    // The device number.
    private int  device;
    // The state: 0=disabled, 1=enabled.
    private byte enable;
    
    /**
     * @return The ID of the WSN node. -1 for all.
     **/
    public synchronized int getNode_id () {
        return this.node_id;
    }

    /**
     * @param newNode_id The ID of the WSN node. -1 for all.
     *
     */
    public synchronized void setNode_id (int newNode_id) {
        this.node_id = newNode_id;
    }

    /**
     * @return The Group ID of the WSN node. -1 for all.
     **/
    public synchronized int getGroup_id () {
        return this.group_id;
    }

    /**
     * @param newGroup_id The Group ID of the WSN node. -1 for all.
     *
     */
    public synchronized void setGroup_id (int newGroup_id) {
        this.group_id = newGroup_id;
    }
    
    /**
     * @return The device number.
     **/
    public synchronized int getDevice () {
        return this.device;
    }

    /**
     * @param newDevice The device number.
     *
     */
    public synchronized void setDevice (int newDevice) {
        this.device = newDevice;
    }

    /**
     * @return The state: 0=disabled, 1=enabled.
     **/
    public synchronized byte getState () {
        return this.enable;
    }

    /**
     * @param newState The state: 0=disabled, 1=enabled.
     *
     */
    public synchronized void setState (int newState) {
        this.enable = (byte)newState;
    }
}