/*
 *  Player Java Client 3 - PlayerIMUDataState.java
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
 * $Id: PlayerIMUDataState.java 74 2006-08-04 08:25:04Z veedee $
 *
 */

package javaclient3.structures.imu;

import javaclient3.structures.*;

/**
 * Data: calibrated IMU data (PLAYER_IMU_DATA_STATE)
 * The imu interface returns the complete 3D coordinates + angles position in
 * space, of the IMU sensor.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerIMUDataState implements PlayerConstants {

	// The complete pose of the IMU in 3D coordinates + angles
	PlayerPose3d pose;

    /**
     * @return the complete pose of the IMU in 3D coordinates + angles
     **/
    public synchronized PlayerPose3d getPose () {
        return this.pose;
    }

    /**
     * @param newPose the complete pose of the IMU in 3D coordinates + angles
     *
     */
    public synchronized void setPose (PlayerPose3d newPose) {
        this.pose = newPose;
    }
}