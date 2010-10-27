/*
 *  Player Java Client 3 - PlayerPosition2dSetOdomReq.java
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
 * Request/reply: Set odometry.
 * To set the robot's odometry to a particular state, send a
 * PLAYER_POSITION2D_REQ_SET_ODOM request.  Null response. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition2dSetOdomReq implements PlayerConstants {

    // (x, y, yaw) [m, m, rad] 
    private PlayerPose pose;


    /**
     * @return  (x, y, yaw) [m, m, rad] 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  (x, y, yaw) [m, m, rad] 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }

}