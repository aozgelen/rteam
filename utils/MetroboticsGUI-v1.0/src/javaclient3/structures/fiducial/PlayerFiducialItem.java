/*
 *  Player Java Client 3 - PlayerFiducialItem.java
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
 * Info on a single detected fiducial
 * The fiducial data packet contains a list of these.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerFiducialItem implements PlayerConstants {

    // The fiducial id.  Fiducials that cannot be identified get id -1. 
    private int id;
    // Fiducial pose relative to the detector. 
    private PlayerPose3d pose;
    // Uncertainty in the measured pose . 
    private PlayerPose3d upose;


    /**
     * @return  The fiducial id.  Fiducials that cannot be identified get id
     *       -1. 
     **/
    public synchronized int getId () {
        return this.id;
    }

    /**
     * @param newId  The fiducial id.  Fiducials that cannot be identified get id
     *       -1. 
     *
     */
    public synchronized void setId (int newId) {
        this.id = newId;
    }
    /**
     * @return  Fiducial pose relative to the detector. 
     **/
    public synchronized PlayerPose3d getPose () {
        return this.pose;
    }

    /**
     * @param newPose  Fiducial pose relative to the detector. 
     *
     */
    public synchronized void setPose (PlayerPose3d newPose) {
        this.pose = newPose;
    }
    /**
     * @return  Uncertainty in the measured pose . 
     **/
    public synchronized PlayerPose3d getUpose () {
        return this.upose;
    }

    /**
     * @param newUpose  Uncertainty in the measured pose . 
     *
     */
    public synchronized void setUpose (PlayerPose3d newUpose) {
        this.upose = newUpose;
    }

}