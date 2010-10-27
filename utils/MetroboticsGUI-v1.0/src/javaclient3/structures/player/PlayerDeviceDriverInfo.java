/*
 *  Player Java Client 3 - PlayerDeviceDriverInfo.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu, Maxim Batalin, Esben Ostergaard
 *
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
package javaclient3.structures.player;

import javaclient3.structures.PlayerDevAddr;

/**
 * To get a name, send a PLAYER_PLAYER_REQ_DRIVERINFO request that specifies
 * the addresses of the desired device in the addr field. Set driver_name_count
 * to 0 and leave driver_name empty. The response will contain the driver name.
 * (see the player_device_driverinfo structure from player.h)
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 - initial support
 * </ul>
 */
public class PlayerDeviceDriverInfo {

    private PlayerDevAddr addr;              /* The device identifier */
    private int           driverNameCount;   /* Length of the driver name */
    private String        driverName;		 /* The driver name (returned) */

    /**
     *
     * @return the device identifier
     */
    public synchronized PlayerDevAddr getAddr () {
    	return this.addr;
    }

    /**
     *
     * @param newAddr device identifier
     */
    public synchronized void setAddr (PlayerDevAddr newAddr) {
    	this.addr = newAddr;
    }

    /**
     *
     * @return length of the driver name
     */
    public synchronized int getDriverNameCount () {
    	return this.driverNameCount;
    }

    /**
     *
     * @param newDriverNameCount length of the driver name
     */
    public synchronized void setDriverNameCount (int newDriverNameCount) {
    	this.driverNameCount = newDriverNameCount;
    }

    /**
     *
     * @return the driver name
     */
    public synchronized String getDriverName () {
    	return this.driverName;
    }

    /**
     *
     * @param newdrivername driver name
     */
    public synchronized void setDriverName (String newdrivername) {
    	this.driverName = newdrivername.replace('\0', ' ').trim();
    }
}
