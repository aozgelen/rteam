/*
 *  Player Java Client 3 - PlayerGripperData.java
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

package javaclient3.structures.gripper;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_GRIPPER_DATA_STATE)
 * The gripper interface returns 3 values that represent the current
 * state of the gripper; the format is given below.  Note that the exact
 * interpretation of this data may vary depending on the details of your
 * gripper and how it is connected to your robot (e.g., General I/O vs. User
 * I/O for the Pioneer gripper).
 * The following list defines how the data can be interpreted for some
 * Pioneer robots and Stage:
 * - state (unsigned byte)
 *   - bit 0: Paddles open
 *   - bit 1: Paddles closed
 *   - bit 2: Paddles moving
 *   - bit 3: Paddles error
 *   - bit 4: Lift is up
 *   - bit 5: Lift is down
 *   - bit 6: Lift is moving
 *   - bit 7: Lift error
 * - beams (unsigned byte)
 *   - bit 0: Gripper limit reached
 *   - bit 1: Lift limit reached
 *   - bit 2: Outer beam obstructed
 *   - bit 3: Inner beam obstructed
 *   - bit 4: Left paddle open
 *   - bit 5: Right paddle open
 *
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 * TODO: rewrite comments
 */
public class PlayerGripperData implements PlayerConstants {

    // The current gripper state
    private int state;
    // The current gripper break-beam state
    private int beams;
    // The number of currently-stored objects
    private int stored;


    /**
     * @return  The current gripper state
     **/
    public synchronized int getState () {
        return this.state;
    }

    /**
     * @param newState  The current gripper state
     *
     */
    public synchronized void setState (int newState) {
        this.state = newState;
    }

    /**
     * @return  The current gripper breakbeam state
     **/
    public synchronized int getBeams () {
        return this.beams;
    }

    /**
     * @param newBeams  The current gripper breakbeam state
     *
     */
    public synchronized void setBeams (int newBeams) {
        this.beams = newBeams;
    }

    /**
     * @return  The number of currently-stored objects
     **/
    public synchronized int getStored () {
        return this.stored;
    }

    /**
     * @param newStored  The number of currently-stored objects
     *
     */
    public synchronized void setStored (int newStored) {
        this.stored = newStored;
    }
}