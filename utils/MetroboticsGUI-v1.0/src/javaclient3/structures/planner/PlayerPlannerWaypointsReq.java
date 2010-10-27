/*
 *  Player Java Client 3 - PlayerPlannerWaypointsReq.java
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

package javaclient3.structures.planner;

import javaclient3.structures.*;

/**
 * Request/reply: Get waypoints 
 * To retrieve the list of waypoints, send a null
 * PLAYER_PLANNER_REQ_GET_WAYPOINTS request.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPlannerWaypointsReq implements PlayerConstants {

    // List of waypoints to follow
    private PlayerPose[] waypoints;


    /**
     * @return  Number of waypoints to follow 
     **/
    public synchronized int getWaypoints_count () {
        return (waypoints == null)?0:this.waypoints.length;
    }

    /**
     * @return  The waypoints 
     **/
    public synchronized PlayerPose[] getWaypoints () {
        return this.waypoints;
    }

    /**
     * @param newWaypoints  The waypoints 
     */
    public synchronized void setWaypoints (PlayerPose[] newWaypoints) {
        this.waypoints = newWaypoints;
    }

}