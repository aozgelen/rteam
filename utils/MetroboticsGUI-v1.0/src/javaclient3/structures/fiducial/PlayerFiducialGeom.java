/*
 *  Player Java Client 3 - PlayerFiducialGeom.java
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
 * Request/reply: Get geometry.
 * The geometry (pose and size) of the fiducial device can be queried by
 * sending a null PLAYER_FIDUCIAL_REQ_GET_GEOM request.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerFiducialGeom implements PlayerConstants {

    // Pose of the detector in the robot cs 
    private PlayerPose pose;
    // Size of the detector 
    private PlayerBbox size;
    // Dimensions of the fiducials in units of (m, m). 
    private PlayerBbox fiducial_size;


    /**
     * @return  Pose of the detector in the robot cs 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  Pose of the detector in the robot cs 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }
    /**
     * @return  Size of the detector 
     **/
    public synchronized PlayerBbox getSize () {
        return this.size;
    }

    /**
     * @param newSize  Size of the detector 
     *
     */
    public synchronized void setSize (PlayerBbox newSize) {
        this.size = newSize;
    }
    /**
     * @return  Dimensions of the fiducials in units of (m, m). 
     **/
    public synchronized PlayerBbox getFiducial_size () {
        return this.fiducial_size;
    }

    /**
     * @param newFiducial_size  Dimensions of the fiducials in units of (m, m). 
     *
     */
    public synchronized void setFiducial_size (PlayerBbox newFiducial_size) {
        this.fiducial_size = newFiducial_size;
    }

}