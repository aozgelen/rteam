/*
 *  Player Java Client 3 - PlayerLaserDataScanpose.java
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

package javaclient3.structures.laser;

import javaclient3.structures.*;

/**
 * Data: pose-stamped scan (PLAYER_LASER_DATA_SCANPOSE)
 * A laser scan with a pose that indicates the (possibly esimated) pose of the 
 * laser when the scan was taken. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLaserDataScanpose implements PlayerConstants {

    // The scan data 
    private PlayerLaserData scan;
    // The global pose of the laser at the time the scan was acquired 
    private PlayerPose pose;


    /**
     * @return  The scan data 
     **/
    public synchronized PlayerLaserData getScan () {
        return this.scan;
    }

    /**
     * @param newScan  The scan data 
     *
     */
    public synchronized void setScan (PlayerLaserData newScan) {
        this.scan = newScan;
    }
    /**
     * @return  The global pose of the laser at the time the scan was acquired 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  The global pose of the laser at the time the scan was acquired 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }

}