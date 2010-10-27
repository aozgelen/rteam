/*
 *  Player Java Client 3 - PlayerFiducialData.java
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

package javaclient3.structures.fiducial;

import javaclient3.structures.*;

/**
 * Data: detected fiducials (PLAYER_FIDUCIAL_DATA_SCAN)
 * The fiducial data packet (all fiducials). 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerFiducialData implements PlayerConstants {

    // The number of detected fiducials 
    private int fiducials_count;
    // List of detected fiducials 
    private PlayerFiducialItem[] fiducials = new PlayerFiducialItem[PLAYER_FIDUCIAL_MAX_SAMPLES];


    /**
     * @return  The number of detected fiducials 
     **/
    public synchronized int getFiducials_count () {
        return this.fiducials_count;
    }

    /**
     * @param newFiducials_count  The number of detected fiducials 
     *
     */
    public synchronized void setFiducials_count (int newFiducials_count) {
        this.fiducials_count = newFiducials_count;
    }
    /**
     * @return  List of detected fiducials 
     **/
    public synchronized PlayerFiducialItem[] getFiducials () {
        return this.fiducials;
    }

    /**
     * @param newFiducials  List of detected fiducials 
     *
     */
    public synchronized void setFiducials (PlayerFiducialItem[] newFiducials) {
        this.fiducials = newFiducials;
    }

}