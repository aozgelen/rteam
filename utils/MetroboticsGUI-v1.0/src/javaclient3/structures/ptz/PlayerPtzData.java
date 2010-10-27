/*
 *  Player Java Client 3 - PlayerPtzData.java
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

package javaclient3.structures.ptz;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_PTZ_DATA_STATE)
 * The ptz interface returns data reflecting the current state of the
 * Pan-Tilt-Zoom unit. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPtzData implements PlayerConstants {

    // Pan [rad] 
    private float pan;
    // Tilt [rad] 
    private float tilt;
    // Field of view [rad] 
    private float zoom;
    // Current pan velocity [rad/s] 
    private float panspeed;
    // Current tilt velocity [rad/s] 
    private float tiltspeed;

    /**
     * @return  Pan [rad] 
     **/
    public synchronized float getPan () {
        return this.pan;
    }

    /**
     * @param newPan  Pan [rad] 
     */
    public synchronized void setPan (float newPan) {
        this.pan = newPan;
    }
    
    /**
     * @return  Tilt [rad] 
     **/
    public synchronized float getTilt () {
        return this.tilt;
    }

    /**
     * @param newTilt  Tilt [rad] 
     */
    public synchronized void setTilt (float newTilt) {
        this.tilt = newTilt;
    }
    
    /**
     * @return  Field of view [rad] 
     **/
    public synchronized float getZoom () {
        return this.zoom;
    }

    /**
     * @param newZoom  Field of view [rad] 
     */
    public synchronized void setZoom (float newZoom) {
        this.zoom = newZoom;
    }
    
    /**
     * @return  Current pan velocity [rad/s] 
     **/
    public synchronized float getPanspeed () {
        return this.panspeed;
    }

    /**
     * @param newPanspeed  Current pan velocity [rad/s] 
     */
    public synchronized void setPanspeed (float newPanspeed) {
        this.panspeed = newPanspeed;
    }
    
    /**
     * @return  Current tilt velocity [rad/s] 
     **/
    public synchronized float getTiltspeed () {
        return this.tiltspeed;
    }

    /**
     * @param newTiltspeed  Current tilt velocity [rad/s] 
     *
     */
    public synchronized void setTiltspeed (float newTiltspeed) {
        this.tiltspeed = newTiltspeed;
    }

}