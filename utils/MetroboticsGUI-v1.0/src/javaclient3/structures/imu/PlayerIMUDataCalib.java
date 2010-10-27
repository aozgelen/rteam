/*
 *  Player Java Client 3 - PlayerIMUDataCalib.java
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
 * $Id: PlayerIMUDataCalib.java 55 2006-03-26 22:15:06Z veedee $
 *
 */

package javaclient3.structures.imu;

import javaclient3.structures.*;

/**
 * Data: calibrated IMU data (PLAYER_IMU_DATA_CALIB)
 * The imu interface returns calibrated acceleration, gyro and magnetic values
 * from the IMU sensor.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerIMUDataCalib implements PlayerConstants {

    // The IMU's calibrated acceleration value on X-axis.
    private float accel_x;
    // The IMU's calibrated acceleration value on Y-axis.
    private float accel_y;
    // The IMU's calibrated acceleration value on Z-axis.
    private float accel_z;
    // The IMU's calibrated gyro value on X-axis.
    private float gyro_x;
    // The IMU's calibrated gyro value on Y-axis.
    private float gyro_y;
    // The IMU's calibrated gyro value on Z-axis.
    private float gyro_z;
    // The IMU's calibrated magnetic value on X-axis.
    private float magn_x;
    // The IMU's calibrated magnetic value on Y-axis.
    private float magn_y;
    // The IMU's calibrated magnetic value on Z-axis.
    private float magn_z;

    /**
     * @return The IMU's calibrated acceleration value on X-axis.
     **/
    public synchronized float getAccel_x () {
        return this.accel_x;
    }

    /**
     * @param newAccel_x The IMU's calibrated acceleration value on X-axis. 
     *
     */
    public synchronized void setAccel_x (float newAccel_x) {
        this.accel_x = newAccel_x;
    }

    /**
     * @return The IMU's calibrated acceleration value on Y-axis.
     **/
    public synchronized float getAccel_y () {
        return this.accel_y;
    }

    /**
     * @param newAccel_y The IMU's calibrated acceleration value on Y-axis.
     *
     */
    public synchronized void setAccel_y (float newAccel_y) {
        this.accel_y = newAccel_y;
    }

    /**
     * @return The IMU's calibrated acceleration value on Z-axis.
     **/
    public synchronized float getAccel_z () {
        return this.accel_z;
    }

    /**
     * @param newAccel_z The IMU's calibrated acceleration value on Z-axis.
     *
     */
    public synchronized void setAccel_z (float newAccel_z) {
        this.accel_z = newAccel_z;
    }

    /**
     * @return The IMU's calibrated gyro value on X-axis.
     **/
    public synchronized float getGyro_x () {
        return this.gyro_x;
    }

    /**
     * @param newGyro_x The IMU's calibrated gyro value on X-axis. 
     *
     */
    public synchronized void setGyro_x (float newGyro_x) {
        this.gyro_x = newGyro_x;
    }

    /**
     * @return The IMU's calibrated gyro value on Y-axis.
     **/
    public synchronized float getGyro_y () {
        return this.gyro_y;
    }

    /**
     * @param newGyro_y The IMU's calibrated gyro value on Y-axis.
     *
     */
    public synchronized void setGyro_y (float newGyro_y) {
        this.gyro_y = newGyro_y;
    }

    /**
     * @return The IMU's calibrated gyro value on Z-axis.
     **/
    public synchronized float getGyro_z () {
        return this.gyro_z;
    }

    /**
     * @param newGyro_z The IMU's calibrated gyro value on Z-axis.
     *
     */
    public synchronized void setGyro_z (float newGyro_z) {
        this.gyro_z = newGyro_z;
    }
    
    /**
     * @return The IMU's calibrated magnetic value on X-axis.
     **/
    public synchronized float getMagn_x () {
        return this.magn_x;
    }

    /**
     * @param newMagn_x The IMU's calibrated magnetic value on X-axis.
     *
     */
    public synchronized void setMagn_x (float newMagn_x) {
        this.magn_x = newMagn_x;
    }

    /**
     * @return The IMU's calibrated magnetic value on Y-axis.
     **/
    public synchronized float getMagn_y () {
        return this.magn_y;
    }

    /**
     * @param newMagn_y The IMU's calibrated magnetic value on Y-axis.
     *
     */
    public synchronized void setMagn_y (float newMagn_y) {
        this.magn_y = newMagn_y;
    }

    /**
     * @return The IMU's calibrated magnetic value on Z-axis.
     **/
    public synchronized float getMagn_z () {
        return this.magn_z;
    }

    /**
     * @param newMagn_z The IMU's calibrated magnetic value on Z-axis.
     *
     */
    public synchronized void setMagn_z (float newMagn_z) {
        this.magn_z = newMagn_z;
    }
    
}