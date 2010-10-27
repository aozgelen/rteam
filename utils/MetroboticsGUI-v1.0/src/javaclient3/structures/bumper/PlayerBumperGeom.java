/*
 *  Player Java Client 3 - PlayerBumperGeom.java
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
 * Data AND Request/reply: bumper geometry
 * To query the geometry of a bumper array, send a null
 * PLAYER_BUMPER_GET_GEOM request.  The response will be in this form.  This
 * message may also be sent as data with the subtype PLAYER_BUMPER_DATA_GEOM
 * (e.g., from a robot whose bumper can move with respect to its body)
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBumperGeom implements PlayerConstants {

    // The number of valid bumper definitions. 
    private int bumper_def_count;
    // geometry of each bumper 
    private PlayerBumperDefine[] bumper_def = new PlayerBumperDefine[PLAYER_BUMPER_MAX_SAMPLES];


    /**
     * @return  The number of valid bumper definitions. 
     **/
    public synchronized int getBumper_def_count () {
        return this.bumper_def_count;
    }

    /**
     * @param newBumper_def_count  The number of valid bumper definitions. 
     *
     */
    public synchronized void setBumper_def_count (int newBumper_def_count) {
        this.bumper_def_count = newBumper_def_count;
    }
    /**
     * @return  geometry of each bumper 
     **/
    public synchronized PlayerBumperDefine[] getBumper_def () {
        return this.bumper_def;
    }

    /**
     * @param newBumper_def  geometry of each bumper 
     *
     */
    public synchronized void setBumper_def (PlayerBumperDefine[] newBumper_def) {
        this.bumper_def = newBumper_def;
    }

}