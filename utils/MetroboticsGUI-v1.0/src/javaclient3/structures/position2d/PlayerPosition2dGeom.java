/*
 *  Player Java Client 3 - PlayerPosition2dGeom.java
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

package javaclient3.structures.position2d;

import javaclient3.structures.*;

/**
 * Data AND Request/reply: geometry.
 * To request robot geometry, send a null PLAYER_POSITION2D_REQ_GET_GEOM
 * request.  Depending on the underlying driver, this message may also be
 * sent as data, with subtype PLAYER_POSITION2D_DATA_GEOM. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition2dGeom implements PlayerConstants {

    // Pose of the robot base, in the robot cs (m, m, rad). 
    private PlayerPose pose;
    // Dimensions of the base (m, m). 
    private PlayerBbox size;


    /**
     * @return  Pose of the robot base, in the robot cs (m, m, rad). 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  Pose of the robot base, in the robot cs (m, m, rad). 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }
    /**
     * @return  Dimensions of the base (m, m). 
     **/
    public synchronized PlayerBbox getSize () {
        return this.size;
    }

    /**
     * @param newSize  Dimensions of the base (m, m). 
     *
     */
    public synchronized void setSize (PlayerBbox newSize) {
        this.size = newSize;
    }

}