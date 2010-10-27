/*
 *  Player Java Client 3 - PlayerDioCmd.java
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

package javaclient3.structures.dio;

import javaclient3.structures.*;

/**
 * Command: output values (PLAYER_DIO_CMD_VALUES)
 * The dio interface accepts 4-byte commands which consist of the ouput
 * bitfield 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerDioCmd implements PlayerConstants {

    // the command 
    private int count;
    // output bitfield 
    private int digout;


    /**
     * @return  the command 
     **/
    public synchronized int getCount () {
        return this.count;
    }

    /**
     * @param newCount  the command 
     *
     */
    public synchronized void setCount (int newCount) {
        this.count = newCount;
    }
    /**
     * @return  output bitfield 
     **/
    public synchronized int getDigout () {
        return this.digout;
    }

    /**
     * @param newDigout  output bitfield 
     *
     */
    public synchronized void setDigout (int newDigout) {
        this.digout = newDigout;
    }

}