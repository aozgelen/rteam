/*
 *  Player Java Client 3 - PlayerLaserConfig.java
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
 * Request/reply: Get/set scan properties.
 * The scan configuration (resolution, aperture, etc) can be queried by
 * sending a null PLAYER_LASER_REQ_GET_CONFIG request and modified by
 * sending a PLAYER_LASER_REQ_SET_CONFIG request.  In either case, the
 * current configuration (after attempting any requested modification) will
 * be returned in the response.  Read the documentation for your driver to
 * determine what configuration values are permissible. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLaserConfig implements PlayerConstants {

    // Start and end angles for the laser scan [rad].
    private float min_angle;
    // Start and end angles for the laser scan [rad].
    private float max_angle;
    // Scan resolution [rad].  
    private float resolution;
    // Maximum range [m] 
    private float max_range;
    // Range Resolution [m] 
    private float range_res;
    // Enable reflection intensity data. 
    private byte intensity;


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
     * @return  Scan resolution [rad].  
     **/
    public synchronized float getResolution () {
        return this.resolution;
    }

    /**
     * @param newResolution  Scan resolution [rad].  
     *
     */
    public synchronized void setResolution (float newResolution) {
        this.resolution = newResolution;
    }
    /**
     * @return  Maximum range [m] 
     **/
    public synchronized float getMax_range () {
        return this.max_range;
    }

    /**
     * @param newMax_range  Maximum range [m] 
     *
     */
    public synchronized void setMax_range (float newMax_range) {
        this.max_range = newMax_range;
    }
    /**
     * @return  Range Resolution [m] 
     **/
    public synchronized float getRange_res () {
        return this.range_res;
    }

    /**
     * @param newRange_res  Range Resolution [m] 
     *
     */
    public synchronized void setRange_res (float newRange_res) {
        this.range_res = newRange_res;
    }
    /**
     * @return  Enable reflection intensity data. 
     **/
    public synchronized byte getIntensity () {
        return this.intensity;
    }

    /**
     * @param newIntensity  Enable reflection intensity data. 
     *
     */
    public synchronized void setIntensity (byte newIntensity) {
        this.intensity = newIntensity;
    }

}