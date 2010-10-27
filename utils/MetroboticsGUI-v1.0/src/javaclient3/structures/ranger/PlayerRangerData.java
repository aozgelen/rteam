/*
 *  Player Java Client 3 - PlayerRangerData.java
 *  Copyright (C) 2010 Jorge Santos Simon
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
 * $Id: PlayerRangerData.java 34 2006-02-15 17:51:14Z veedee $
 *
 */

package javaclient3.structures.ranger;

import javaclient3.structures.*;

/**
 * Data: ranges (PLAYER_RANGER_DATA_RANGES)
 * The ranger interface returns an array of distance readings
 * from a robot's ranger device such as a laser scanner, sonar
 * array or IR array.
 * @author Jorge Santos Simon
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerRangerData implements PlayerConstants {

    // The range readings [m]
    private double[] ranges;


    /**
     * @return  The number of valid range readings.
     **/
    public synchronized int getRanges_count () {
        return (this.ranges == null)?0:this.ranges.length;
    }

    /**
     * @return  The range readings [m]
     **/
    public synchronized double[] getRanges () {
        return this.ranges;
    }

    /**
     * @param newRanges  The range readings [m]
     */
    public synchronized void setRanges (double[] newRanges) {
        this.ranges = newRanges;
    }

}