/*
 *  Player Java Client 3 - PlayerLaserData.java
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
 * Data: scan (PLAYER_LASER_DATA_SCAN)
 * The basic laser data packet.  
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLaserData implements PlayerConstants {

    // Start and end angles for the laser scan [rad].  
    private float min_angle;
    // Start and end angles for the laser scan [rad].  
    private float max_angle;
    // Angular resolution [rad].  
    private float resolution;
    // Maximum range [m]. 
    private float max_range;
    // Number of range readings.  
    private int ranges_count;
    // Range readings [m]. 
    private float[] ranges = new float[PLAYER_LASER_MAX_SAMPLES];
    // Number of intensity readings 
    private int intensity_count;
    // Intensity readings. 
    private byte[] intensity = new byte[PLAYER_LASER_MAX_SAMPLES];
    // A unique, increasing, ID for the scan 
    private int id;

    // X,Y cartesian position of scanned data [m] (not in player_laser_data)
    private PlayerPoint2d points[] = new PlayerPoint2d[PLAYER_LASER_MAX_SAMPLES];

    /**
     * @return  Start and end angles for the laser scan [rad].  
     **/
    public synchronized float getMin_angle () {
        return this.min_angle;
    }

    /**
     * @param newMin_angle  Start and end angles for the laser scan [rad].  
     *
     */
    public synchronized void setMin_angle (float newMin_angle) {
        this.min_angle = newMin_angle;
    }
    
    /**
     * @return  Start and end angles for the laser scan [rad].  
     **/
    public synchronized float getMax_angle () {
        return this.max_angle;
    }

    /**
     * @param newMax_angle  Start and end angles for the laser scan [rad].  
     *
     */
    public synchronized void setMax_angle (float newMax_angle) {
        this.max_angle = newMax_angle;
    }
    
    /**
     * @return  Angular resolution [rad].  
     **/
    public synchronized float getResolution () {
        return this.resolution;
    }

    /**
     * @param newResolution  Angular resolution [rad].  
     *
     */
    public synchronized void setResolution (float newResolution) {
        this.resolution = newResolution;
    }
    
    /**
     * @return  Maximum range [m]. 
     **/
    public synchronized float getMax_range () {
        return this.max_range;
    }

    /**
     * @param newMax_range  Maximum range [m]. 
     *
     */
    public synchronized void setMax_range (float newMax_range) {
        this.max_range = newMax_range;
    }
    
    /**
     * @return  Number of range readings.  
     **/
    public synchronized int getRanges_count () {
        return this.ranges_count;
    }

    /**
     * @param newRanges_count  Number of range readings.  
     *
     */
    public synchronized void setRanges_count (int newRanges_count) {
        this.ranges_count = newRanges_count;
    }
    
    /**
     * @return  Range readings [m]. 
     **/
    public synchronized float[] getRanges () {
        return this.ranges;
    }

    /**
     * @param newRanges  Range readings [m]. 
     *
     */
    public synchronized void setRanges (float[] newRanges) {
        this.ranges = newRanges;
    }
    
    /**
     * @return  Number of intensity readings 
     **/
    public synchronized int getIntensity_count () {
        return this.intensity_count;
    }

    /**
     * @param newIntensity_count  Number of intensity readings 
     *
     */
    public synchronized void setIntensity_count (int newIntensity_count) {
        this.intensity_count = newIntensity_count;
    }
    
    /**
     * @return  Intensity readings. 
     **/
    public synchronized byte[] getIntensity () {
        return this.intensity;
    }

    /**
     * @param newIntensity  Intensity readings. 
     *
     */
    public synchronized void setIntensity (byte[] newIntensity) {
        this.intensity = newIntensity;
    }
    
    /**
     * @return  A unique, increasing, ID for the scan 
     **/
    public synchronized int getId () {
        return this.id;
    }

    /**
     * @param newId  A unique, increasing, ID for the scan 
     *
     */
    public synchronized void setId (int newId) {
        this.id = newId;
    }

    /**
     * @return X,Y cartesian position of scanned data [m]
     **/
    public synchronized PlayerPoint2d[] getPoints () {
        return this.points;
    }

    /**
     * @param newPoints X,Y cartesian position of scanned data [m]
     *
     */
    public synchronized void setPoints (PlayerPoint2d[] newPoints) {
        this.points = newPoints;
    }
}