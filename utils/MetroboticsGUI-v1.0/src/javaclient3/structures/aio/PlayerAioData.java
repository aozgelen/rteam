/*
 *  Player Java Client 3 - PlayerAioData.java
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

package javaclient3.structures.aio;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_AIO_DATA_STATE)
 * The aio interface returns data regarding the current state of the
 * analog inputs. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAioData implements PlayerConstants {

    // number of valid samples 
    private int voltages_count;
    // the samples [V] 
    private float[] voltages = new float[PLAYER_AIO_MAX_INPUTS];


    /**
     * @return  number of valid samples 
     **/
    public synchronized int getVoltages_count () {
        return this.voltages_count;
    }

    /**
     * @param newVoltages_count  number of valid samples 
     *
     */
    public synchronized void setVoltages_count (int newVoltages_count) {
        this.voltages_count = newVoltages_count;
    }
    /**
     * @return  the samples [V] 
     **/
    public synchronized float[] getVoltages () {
        return this.voltages;
    }

    /**
     * @param newVoltages  the samples [V] 
     *
     */
    public synchronized void setVoltages (float[] newVoltages) {
        this.voltages = newVoltages;
    }

}