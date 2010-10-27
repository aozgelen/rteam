/*
 *  Player Java Client 3 - PlayerPtzCmd.java
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
 * Command: state (PLAYER_PTZ_CMD_STATE)
 * The ptz interface accepts commands that set change the state of the unit.
 * Note that the commands are absolute, not relative. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPtzCmd implements PlayerConstants {

    // Desired pan angle [rad] 
    private float pan;
    // Desired tilt angle [rad] 
    private float tilt;
    // Desired field of view [rad]. 
    private float zoom;
    // Desired pan velocity [rad/s] 
    private float panspeed;
    // Desired tilt velocity [rad/s] 
    private float tiltspeed;


    /**
     * @return  Desired pan angle [rad] 
     **/
    public synchronized float getPan () {
        return this.pan;
    }

    /**
     * @param newPan  Desired pan angle [rad] 
     */
    public synchronized void setPan (float newPan) {
        this.pan = newPan;
    }
    
    /**
     * @return  Desired tilt angle [rad] 
     **/
    public synchronized float getTilt () {
        return this.tilt;
    }

    /**
     * @param newTilt  Desired tilt angle [rad] 
     */
    public synchronized void setTilt (float newTilt) {
        this.tilt = newTilt;
    }
    
    /**
     * @return  Desired field of view [rad]. 
     **/
    public synchronized float getZoom () {
        return this.zoom;
    }

    /**
     * @param newZoom  Desired field of view [rad]. 
     */
    public synchronized void setZoom (float newZoom) {
        this.zoom = newZoom;
    }
    
    /**
     * @return  Desired pan velocity [rad/s] 
     **/
    public synchronized float getPanspeed () {
        return this.panspeed;
    }

    /**
     * @param newPanspeed  Desired pan velocity [rad/s] 
     */
    public synchronized void setPanspeed (float newPanspeed) {
        this.panspeed = newPanspeed;
    }
    
    /**
     * @return  Desired tilt velocity [rad/s] 
     **/
    public synchronized float getTiltspeed () {
        return this.tiltspeed;
    }

    /**
     * @param newTiltspeed  Desired tilt velocity [rad/s] 
     */
    public synchronized void setTiltspeed (float newTiltspeed) {
        this.tiltspeed = newTiltspeed;
    }

}