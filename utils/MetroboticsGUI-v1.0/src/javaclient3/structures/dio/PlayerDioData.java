/*
 *  Player Java Client 3 - PlayerDioData.java
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
 * Data: input values (PLAYER_DIO_DATA_VALUES)
 * The dio interface returns data regarding the current state of the
 * digital inputs. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerDioData implements PlayerConstants {

    // number of samples 
    private int count;
    // bitfield of samples 
    private int digin;


    /**
     * @return  number of samples 
     **/
    public synchronized int getCount () {
        return this.count;
    }

    /**
     * @param newCount  number of samples 
     *
     */
    public synchronized void setCount (int newCount) {
        this.count = newCount;
    }
    /**
     * @return  bitfield of samples 
     **/
    public synchronized int getDigin () {
        return this.digin;
    }

    /**
     * @param newDigin  bitfield of samples 
     *
     */
    public synchronized void setDigin (int newDigin) {
        this.digin = newDigin;
    }

}