/*
 *  Player Java Client 3 - PlayerWsnData.java
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

package javaclient3.structures.wsn;

import javaclient3.structures.*;

/**
 * The WSN data packet describes a wireless sensor network node.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWsnData implements PlayerConstants {

    // The type of WSN node.
    private int node_type;
    // The ID of the WSN node.
    private int node_id;
    // The ID of the WSN node's parent (if existing).
    private int node_parent_id;
    // The WSN node's data packet.
    PlayerWsnNodeData data_packet;
    
    /**
     * @return The type of WSN node.
     **/
    public synchronized int getNode_type () {
        return this.node_type;
    }

    /**
     * @param newNode_type The type of WSN node.
     *
     */
    public synchronized void setNode_type (int newNode_type) {
        this.node_type = newNode_type;
    }

    /**
     * @return The ID of the WSN node.
     **/
    public synchronized int getNode_id () {
        return this.node_id;
    }

    /**
     * @param newNode_id The ID of the WSN node.
     *
     */
    public synchronized void setNode_id (int newNode_id) {
        this.node_id = newNode_id;
    }

    /**
     * @return The ID of the WSN node's parent (if existing).
     **/
    public synchronized int getNode_parent_id () {
        return this.node_parent_id;
    }

    /**
     * @param newNode_parent_id The ID of the WSN node's parent (if existing).
     *
     */
    public synchronized void setNode_parent_id (int newNode_parent_id) {
        this.node_parent_id = newNode_parent_id;
    }

    /**
     * @return The WSN node's data packet
     **/
    public synchronized PlayerWsnNodeData getData_packet () {
        return this.data_packet;
    }

    /**
     * @param newNode_parent_id The WSN node's data packet
     *
     */
    public synchronized void setData_packet (PlayerWsnNodeData newData_packet) {
        this.data_packet = newData_packet;
    }
}