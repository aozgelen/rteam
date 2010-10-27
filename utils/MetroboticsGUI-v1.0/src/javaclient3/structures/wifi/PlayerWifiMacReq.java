/*
 *  Player Java Client 3 - PlayerWifiMacReq.java
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

package javaclient3.structures.wifi;

import javaclient3.structures.*;

/**
 * Request/reply: 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWifiMacReq implements PlayerConstants {

    // MAC address. 
    private int mac_count;


    /**
     * @return  MAC address. 
     **/
    public synchronized int getMac_count () {
        return this.mac_count;
    }

    /**
     * @param newMac_count  MAC address. 
     *
     */
    public synchronized void setMac_count (int newMac_count) {
        this.mac_count = newMac_count;
    }

}