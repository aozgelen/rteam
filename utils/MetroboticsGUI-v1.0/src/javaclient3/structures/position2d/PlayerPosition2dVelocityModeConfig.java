/*
 *  Player Java Client 3 - PlayerPosition2dVelocityModeConfig.java
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
 * Request/reply: Change velocity control.
 * Some robots offer different velocity control modes.  It can be changed by
 * sending a PLAYER_POSITION2D_REQ_VELOCITY_MODE request with the format
 * given below, including the appropriate mode.  No matter which mode is
 * used, the external client interface to the position device remains
 * the same.  Null response.
 * The driver_p2os driver offers two modes of velocity control:
 * separate translational and rotational control and direct wheel control.
 * When in the separate mode, the robot's microcontroller internally
 * computes left and right wheel velocities based on the currently commanded
 * translational and rotational velocities and then attenuates these values
 * to match a nice predefined acceleration profile.  When in the direct
 * mode, the microcontroller simply passes on the current left and right
 * wheel velocities.  Essentially, the separate mode offers smoother but
 * slower (lower acceleration) control, and the direct mode offers faster
 * but jerkier (higher acceleration) control.  Player's default is to use
 * the direct mode.  Set @a mode to zero for direct control and non-zero
 * for separate control.
 * For the driver_reb driver, 0 is direct velocity control,
 * 1 is for velocity-based heading PD controller.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPosition2dVelocityModeConfig implements PlayerConstants {

    // driver-specific 
    private int value;


    /**
     * @return  driver-specific 
     **/
    public synchronized int getValue () {
        return this.value;
    }

    /**
     * @param newValue  driver-specific 
     *
     */
    public synchronized void setValue (int newValue) {
        this.value = newValue;
    }

}