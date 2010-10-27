/*
 *  Player Java Client 3 - PlayerBumperData.java
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

package javaclient3.structures.bumper;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_BUMPER_DATA_GEOM)
 * The bumper interface gives current bumper state
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBumperData implements PlayerConstants {

    // the number of valid bumper readings 
    private int bumpers_count;
    // array of bumper values 
    private byte[] bumpers = new byte[PLAYER_BUMPER_MAX_SAMPLES];


    /**
     * @return  the number of valid bumper readings 
     **/
    public synchronized int getBumpers_count () {
        return this.bumpers_count;
    }

    /**
     * @param newBumpers_count  the number of valid bumper readings 
     *
     */
    public synchronized void setBumpers_count (int newBumpers_count) {
        this.bumpers_count = newBumpers_count;
    }
    /**
     * @return  array of bumper values 
     **/
    public synchronized byte[] getBumpers () {
        return this.bumpers;
    }

    /**
     * @param newBumpers  array of bumper values 
     *
     */
    public synchronized void setBumpers (byte[] newBumpers) {
        this.bumpers = newBumpers;
    }

}