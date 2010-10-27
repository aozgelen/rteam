/*
 *  Player Java Client - PlayerDeviceDevlist.java
 *  Copyright (C) 2002-2005 Maxim A. Batalin, Esben H. Ostergaard & Radu Bogdan Rusu
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
 * It's useful for applications such as viewer programs and test suites that 
 * tailor behave differently depending on which devices are available.  
 * To request the list, send a null PLAYER_PLAYER_REQ_DEVLIST.
 * (see the player_device_devlist structure from player.h)
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 - initial support
 * </ul>
 */
public class PlayerDeviceDevlist {
    private int             devicesCount;     /* the number of devices */
    private PlayerDevAddr[] devList;          /* the list of available devices */
    
    /**
     * 
     * @return the number of devices 
     */
    public synchronized int getDeviceCount () {
    	return this.devicesCount;
    }
    
    /**
     * 
     * @param newdevicescount number of devices 
     */
    public synchronized void setDeviceCount (int newdevicescount) {
    	this.devicesCount = newdevicescount;
    }
    
    /**
     * 
     * @return the list of available devices
     */
    public synchronized PlayerDevAddr[] getDevList () {
    	return this.devList;
    }
    
    /**
     * 
     * @param newdevlist list of available devices
     */
    public synchronized void setDevList (PlayerDevAddr[] newdevlist) {
    	this.devList = newdevlist;
    }
}