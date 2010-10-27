/*
 *  Player Java Client 3 - PlayerFiducialFov.java
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
 * Request/reply: Get/set sensor field of view.
 * The field of view of the fiducial device can be set using the
 * PLAYER_FIDUCIAL_REQ_SET_FOV request (response will be null), and queried
 * using a null PLAYER_FIDUCIAL_REQ_GET_FOV request.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerFiducialFov implements PlayerConstants {

    // The minimum range of the sensor [m] 
    private float min_range;
    // The maximum range of the sensor [m] 
    private float max_range;
    // The receptive angle of the sensor [rad]. 
    private float view_angle;


    /**
     * @return  The minimum range of the sensor [m] 
     **/
    public synchronized float getMin_range () {
        return this.min_range;
    }

    /**
     * @param newMin_range  The minimum range of the sensor [m] 
     *
     */
    public synchronized void setMin_range (float newMin_range) {
        this.min_range = newMin_range;
    }
    /**
     * @return  The maximum range of the sensor [m] 
     **/
    public synchronized float getMax_range () {
        return this.max_range;
    }

    /**
     * @param newMax_range  The maximum range of the sensor [m] 
     *
     */
    public synchronized void setMax_range (float newMax_range) {
        this.max_range = newMax_range;
    }
    /**
     * @return  The receptive angle of the sensor [rad]. 
     **/
    public synchronized float getView_angle () {
        return this.view_angle;
    }

    /**
     * @param newView_angle  The receptive angle of the sensor [rad]. 
     *
     */
    public synchronized void setView_angle (float newView_angle) {
        this.view_angle = newView_angle;
    }

}