/*
 *  Player Java Client 3 - PlayerRfidTag.java
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

package javaclient3.structures.rfid;

import javaclient3.structures.*;

/**
 * Structure describing a single RFID tag. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerRfidTag implements PlayerConstants {

    // Tag type. 
    private int type;
    // GUID count.
    private int guid_count;
    // The Globally Unique IDentifier (GUID) of the tag.
    private byte[] guid = new byte[PLAYER_RFID_MAX_GUID];

    /**
     * @return Tag type.
     **/
    public synchronized int getType () {
        return this.type;
    }

    /**
     * @param newType Tag type.
     *
     */
    public synchronized void setType (int newType) {
        this.type = newType;
    }
    
    /**
     * @return GUID count.
     **/
    public synchronized int getGuid_count () {
        return this.guid_count;
    }

    /**
     * @param newGuid_count GUID count.
     *
     */
    public synchronized void setGuid_count (int newGuid_count) {
        this.guid_count = newGuid_count;
    }
    
    /**
     * @return The Globally Unique IDentifier (GUID) of the tag. 
     **/
    public synchronized byte[] getGuid () {
        return this.guid;
    }

    /**
     * @param newGuid The Globally Unique IDentifier (GUID) of the tag.
     *
     */
    public synchronized void setGuid (byte[] newGuid) {
        this.guid = newGuid;
    }

}