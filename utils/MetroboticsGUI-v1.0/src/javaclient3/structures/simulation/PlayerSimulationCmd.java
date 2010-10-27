/*
 *  Player Java Client 3 - PlayerSimulationCmd.java
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

package javaclient3.structures.simulation;

import javaclient3.structures.*;

/**
 * Command
 * Just a placeholder for now; data will be added in future.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerSimulationCmd implements PlayerConstants {

    // A single byte of as-yet-unspecified command. Useful for experiments. 
    private byte cmd;


    /**
     * @return  A single byte of as-yet-unspecified command. Useful for experiments. 
     **/
    public synchronized byte getCmd () {
        return this.cmd;
    }

    /**
     * @param newCmd  A single byte of as-yet-unspecified command. Useful for experiments. 
     *
     */
    public synchronized void setCmd (byte newCmd) {
        this.cmd = newCmd;
    }

}