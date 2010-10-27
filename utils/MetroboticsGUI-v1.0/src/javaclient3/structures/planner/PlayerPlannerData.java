/*
 *  Player Java Client 3 - PlayerPlannerData.java
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
 * Data: state (PLAYER_PLANNER_DATA_STATE)
 * The planner interface reports the current execution state of the
 * planner. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPlannerData implements PlayerConstants {

    // Did the planner find a valid path? 
    private byte valid;
    // Have we arrived at the goal? 
    private byte done;
    // Current location (m,m,rad) 
    private PlayerPose pos;
    // Goal location (m,m,rad) 
    private PlayerPose goal;
    // Current waypoint location (m,m,rad) 
    private PlayerPose waypoint;
    // Current waypoint index (handy if you already have the list
    // of waypoints). May be negative if there's no plan, or if
    // the plan is done 
    private int waypoint_idx;
    // Number of waypoints in the plan 
    private int waypoints_count;


    /**
     * @return  Did the planner find a valid path? 
     **/
    public synchronized byte getValid () {
        return this.valid;
    }

    /**
     * @param newValid  Did the planner find a valid path? 
     */
    public synchronized void setValid (byte newValid) {
        this.valid = newValid;
    }
    
    /**
     * @return  Have we arrived at the goal? 
     **/
    public synchronized byte getDone () {
        return this.done;
    }

    /**
     * @param newDone  Have we arrived at the goal? 
     */
    public synchronized void setDone (byte newDone) {
        this.done = newDone;
    }
    
    /**
     * @return  Current location (m,m,rad) 
     **/
    public synchronized PlayerPose getPos () {
        return this.pos;
    }

    /**
     * @param newPos  Current location (m,m,rad) 
     */
    public synchronized void setPos (PlayerPose newPos) {
        this.pos = newPos;
    }

    /**
     * @return  Goal location (m,m,rad) 
     **/
    public synchronized PlayerPose getGoal () {
        return this.goal;
    }

    /**
     * @param newGoal  Goal location (m,m,rad) 
     */
    public synchronized void setGoal (PlayerPose newGoal) {
        this.goal = newGoal;
    }

    /**
     * @return  Current waypoint location (m,m,rad) 
     **/
    public synchronized PlayerPose getWaypoint () {
        return this.waypoint;
    }

    /**
     * @param newWaypoint  Current waypoint location (m,m,rad) 
     *
     */
    public synchronized void setWaypoint (PlayerPose newWaypoint) {
        this.waypoint = newWaypoint;
    }

    /**
     * @return  Current waypoint index (handy if you already have the list
     *       of waypoints). May be negative if there's no plan, or if
     *       the plan is done 
     **/
    public synchronized int getWaypoint_idx () {
        return this.waypoint_idx;
    }

    /**
     * @param newWaypoint_idx  Current waypoint index (handy if you already have the list
     *       of waypoints). May be negative if there's no plan, or if
     *       the plan is done 
     */
    public synchronized void setWaypoint_idx (int newWaypoint_idx) {
        this.waypoint_idx = newWaypoint_idx;
    }
    
    /**
     * @return  Number of waypoints in the plan 
     **/
    public synchronized int getWaypoints_count () {
        return this.waypoints_count;
    }

    /**
     * @param newWaypoints_count  Number of waypoints in the plan 
     */
    public synchronized void setWaypoints_count (int newWaypoints_count) {
        this.waypoints_count = newWaypoints_count;
    }

}