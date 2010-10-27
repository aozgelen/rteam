/*
 *  Player Java Client 3 - PlayerGripperGeom.java
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
 * Request/reply: get geometry
 * The geometry (pose and size) of the gripper device can be queried
 * by sending a null PLAYER_GRIPPER_REQ_GET_GEOM request.
 *
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerGripperGeom implements PlayerConstants {

    // Gripper pose, in robot cs (m, m, m, rad, rad, rad).
    private PlayerPose3d pose;

    // Inside dimensions of gripper, i.e. the size of the space
    // between the fingers when they are fully open (m, m, m).
    private PlayerBbox3d inner_size;

    // Outside dimensions of gripper (m, m, m).
    private PlayerBbox3d outer_size;

    // The number of break-beams the gripper has
    private byte num_beams;

    // The capacity of the gripper's store - if 0, the gripper cannot store
    private byte capacity;

    /**
     * @return  Gripper pose, in robot cs (m, m, m, rad, rad, rad).
     **/
    public synchronized PlayerPose3d getPose () {
        return this.pose;
    }

    /**
     * @param newPose  Gripper pose, in robot cs (m, m, m, rad, rad, rad).
     *
     */
    public synchronized void setPose (PlayerPose3d newPose) {
        this.pose = newPose;
    }

    /**
     * @return  Gripper inside dimensions (m, m, m).
     **/
    public synchronized PlayerBbox3d getInnerSize () {
        return this.inner_size;
    }

    /**
     * @param newSize  Gripper inside dimensions (m, m, m).
     *
     */
    public synchronized void setInnerSize (PlayerBbox3d newSize) {
        this.inner_size = newSize;
    }

    /**
     * @return  Gripper outside dimensions (m, m, m).
     **/
    public synchronized PlayerBbox3d getOuterSize () {
        return this.outer_size;
    }

    /**
     * @param newSize  Gripper outside dimensions (m, m, m).
     *
     */
    public synchronized void setOuterSize (PlayerBbox3d newSize) {
        this.outer_size = newSize;
    }

    /**
     * @param newNumBeams  The number of break-beams the gripper has.
     *
     */
    public synchronized void setNumBeams (byte newNumBeams) {
        this.num_beams = newNumBeams;
    }

    /**
     * @return  The number of break-beams the gripper has.
     **/
    public synchronized byte getNumBeams () {
        return this.num_beams;
    }

    /**
     * @param newCapacity  Capacity of the gripper's store.
     *
     */
    public synchronized void setCapacity (byte newCapacity) {
        this.capacity = newCapacity;
    }

    /**
     * @return  Capacity of the gripper's store.
     **/
    public synchronized byte getCapacity () {
        return this.capacity;
    }
}