/*
 *  Player Java Client 3 - OpaqueInterface.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu
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
 * $Id: OpaqueInterface.java 50 2006-03-10 19:05:00Z veedee $
 *
 */
package javaclient3;

import javaclient3.structures.PlayerMsgHdr;

/**
 * OpaqueInterface skeleton! Extend to provide the appropriate functionality for
 * your device.
 * @author Radu Bogdan Rusu 
 */
public abstract class OpaqueInterface extends PlayerDevice {

    /**
     * Constructor for OpaqueInterface.
     * @param pc a reference to the PlayerClient object
     */
    public OpaqueInterface (PlayerClient pc) { super (pc); }

    /**
     * Read the data packet.
     */
    public synchronized void readData (PlayerMsgHdr header) {}
}
