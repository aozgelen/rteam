/*
 *  Player Java Client 3 - PlayerWsnNodeData.java
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

package javaclient3.structures.wsn;

import javaclient3.structures.*;

/**
 * Structure describing the WSN node's data packet.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWsnNodeData implements PlayerConstants {

    // The node's light measurement from a light sensor.
    private float light;
    // The node's accoustic measurement from a microphone.
    private float mic;
    // The node's acceleration on X-axis from an acceleration sensor.
    private float accel_x;
    // The node's acceleration on y-axis from an acceleration sensor.
    private float accel_y;
    // The node's acceleration on Z-axis from an acceleration sensor.
    private float accel_z;
    // The node's magnetic measurement on X-axis from a magnetometer.
    private float magn_x;
    // The node's magnetic measurement on Y-axis from a magnetometer.
    private float magn_y;
    // The node's magnetic measurement on Z-axis from a magnetometer.
    private float magn_z;
    // The node's temperature measurement from a temperature sensor.
    private float temperature;
    // The node's remaining battery voltage.
    private float battery;

    /**
     * @return The node's light measurement from a light sensor.
     **/
    public synchronized float getLight () {
        return this.light;
    }

    /**
     * @param newLight The node's light measurement from a light sensor.
     *
     */
    public synchronized void setLight (float newLight) {
        this.light = newLight;
    }

    /**
     * @return The node's accoustic measurement from a microphone.
     **/
    public synchronized float getMic () {
        return this.mic;
    }

    /**
     * @param newMic The node's accoustic measurement from a microphone.
     *
     */
    public synchronized void setMic (float newMic) {
        this.mic = newMic;
    }

    /**
     * @return The node's acceleration on X-axis from an acceleration sensor.
     **/
    public synchronized float getAccel_x () {
        return this.accel_x;
    }

    /**
     * @param newAccel_x The node's acceleration on X-axis from an acceleration 
     * sensor.
     *
     */
    public synchronized void setAccel_x (float newAccel_x) {
        this.accel_x = newAccel_x;
    }

    /**
     * @return The node's acceleration on Y-axis from an acceleration sensor.
     **/
    public synchronized float getAccel_y () {
        return this.accel_y;
    }

    /**
     * @param newAccel_y The node's acceleration on Y-axis from an acceleration 
     * sensor.
     *
     */
    public synchronized void setAccel_y (float newAccel_y) {
        this.accel_y = newAccel_y;
    }

    /**
     * @return The node's acceleration on Z-axis from an acceleration sensor.
     **/
    public synchronized float getAccel_z () {
        return this.accel_z;
    }

    /**
     * @param newAccel_z The node's acceleration on Z-axis from an acceleration 
     * sensor.
     *
     */
    public synchronized void setAccel_z (float newAccel_z) {
        this.accel_z = newAccel_z;
    }

    /**
     * @return The node's magnetic measurement on X-axis from a magnetometer.
     **/
    public synchronized float getMagn_x () {
        return this.magn_x;
    }

    /**
     * @param newMagn_x The node's magnetic measurement on X-axis from a 
     * magnetometer. 
     *
     */
    public synchronized void setMagn_x (float newMagn_x) {
        this.magn_x = newMagn_x;
    }

    /**
     * @return The node's magnetic measurement on Y-axis from a magnetometer.
     **/
    public synchronized float getMagn_y () {
        return this.magn_y;
    }

    /**
     * @param newMagn_y The node's magnetic measurement on Y-axis from a 
     * magnetometer. 
     *
     */
    public synchronized void setMagn_y (float newMagn_y) {
        this.magn_y = newMagn_y;
    }

    /**
     * @return The node's magnetic measurement on Z-axis from a magnetometer.
     **/
    public synchronized float getMagn_z () {
        return this.magn_z;
    }

    /**
     * @param newMagn_z The node's magnetic measurement on X-axis from a 
     * magnetometer. 
     *
     */
    public synchronized void setMagn_z (float newMagn_z) {
        this.magn_z = newMagn_z;
    }
    
    /**
     * @return The node's temperature measurement from a temperature sensor.
     **/
    public synchronized float getTemperature () {
        return this.temperature;
    }

    /**
     * @param newTemperature The node's temperature measurement from a 
     * temperature sensor.
     *
     */
    public synchronized void setTemperature (float newTemperature) {
        this.temperature = newTemperature;
    }

    /**
     * @return The node's remaining battery voltage.
     **/
    public synchronized float getBattery () {
        return this.battery;
    }

    /**
     * @param newBattery The node's remaining battery voltage.
     *
     */
    public synchronized void setBattery (float newBattery) {
        this.battery = newBattery;
    }

}