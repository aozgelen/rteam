/*
 *  Player Java Client 3 - PlayerIMUDataQuat.java
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
 * $Id: PlayerIMUDataQuat.java 55 2006-03-26 22:15:06Z veedee $
 *
 */

package javaclient3.structures.imu;

import javaclient3.structures.*;

/**
 * Data: Quaternions orientation data (PLAYER_IMU_DATA_QUAT)
 * The imu interface returns calibrated IMU values as well as orientation data
 * as quaternions.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerIMUDataQuat implements PlayerConstants {

    // Calibrated IMU data (accel, gyro, magnetometer)
    PlayerIMUDataCalib calib_data;
    
    // Orientation data as quaternions
    float quaternions[] = new float[4];
//    float q0, q1, q2, q3;
    
    /**
     * @return Calibrated IMU data (accel, gyro, magnetometer)
     **/
    public synchronized PlayerIMUDataCalib getCalib_data () {
        return this.calib_data;
    }

    /**
     * @param newCalib_data Calibrated IMU data (accel, gyro, magnetometer)
     *
     */
    public synchronized void setCalib_data (PlayerIMUDataCalib newCalib_data) {
        this.calib_data = newCalib_data;
    }

    /**
     * @return Orientation data as quaternions
     **/
    public synchronized float[] getQuaternions () {
        return this.quaternions;
    }

    /**
     * @param newQuaternions Orientation data as quaternions
     *
     */
    public synchronized void setQuaternions (float[] newQuaternions) {
        this.quaternions = newQuaternions;
    }
}