/*
 *  Player Java Client 3 - PlayerActarrayData.java
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

package javaclient3.structures.actarray;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_ACTARRAY_DATA_STATE)
 * The actuator array data packet. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerActarrayData implements PlayerConstants {

    // The number of actuators in the array. 
    private int actuators_count;
    // The actuator data. 
    private PlayerActarrayActuator[] actuators = new PlayerActarrayActuator[PLAYER_ACTARRAY_NUM_ACTUATORS];


    /**
     * @return  The number of actuators in the array. 
     **/
    public synchronized int getActuators_count () {
        return this.actuators_count;
    }

    /**
     * @param newActuators_count  The number of actuators in the array. 
     *
     */
    public synchronized void setActuators_count (int newActuators_count) {
        this.actuators_count = newActuators_count;
    }
    /**
     * @return  The actuator data. 
     **/
    public synchronized PlayerActarrayActuator[] getActuators () {
        return this.actuators;
    }

    /**
     * @param newActuators  The actuator data. 
     *
     */
    public synchronized void setActuators (PlayerActarrayActuator[] newActuators) {
        this.actuators = newActuators;
    }

}