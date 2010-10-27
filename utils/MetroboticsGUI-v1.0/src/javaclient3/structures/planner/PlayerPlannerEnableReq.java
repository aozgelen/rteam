/*
 *  Player Java Client 3 - PlayerPlannerEnableReq.java
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
 * Request/reply: Enable/disable robot motion 
 * To enable or disable the planner, send a PLAYER_PLANNER_REQ_ENABLE
 * request.  When disabled, the planner will stop the robot.  When enabled, the planner should resume plan execution.  Null response.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPlannerEnableReq implements PlayerConstants {

    // state: TRUE to enable, FALSE to disable 
    private byte state;


    /**
     * @return  state: TRUE to enable, FALSE to disable 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  state: TRUE to enable, FALSE to disable 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}