/*
 *  Player Java Client 3 - PlayerRangerGeom.java
 *  Copyright (C) 2010 Jorge Santos Simon
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
 * $Id: PlayerRangerGeom.java 34 2006-02-15 17:51:14Z veedee $
 *
 */

package javaclient3.structures.ranger;

import javaclient3.structures.*;

/**
 * Data AND Request/reply: geometry.
 * To query the geometry of the entire ranger device and the geometry
 * of each individual element, send a null PLAYER_RANGER_REQ_GET_GEOM
 * request. Depending on the underlying driver, this message can also
 * be sent as data with the subtype PLAYER_RANGER_DATA_GEOM.
 * @author Jorge Santos Simon
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerRangerGeom implements PlayerConstants {

    // Ranger device centre pose, in robot cs [m, m, m, rad, rad, rad]
    private PlayerPose3d pose;
    
    // Ranger device size [m, m, m]
    private PlayerBbox3d size;

    // Pose of each individual sensor that makes up the device, in device cs
    private PlayerPose3d[] poses;

    // Size of each individual sensor that makes up the device
    private PlayerBbox3d[] sizes;

    /**
     * @return  Pose of the entire ranger device, in robot cs
     **/
    public synchronized PlayerPose3d getPose () {
        return this.pose;
    }

    /**
     * @param newPose  Pose of the entire ranger device, in robot cs
     */
    public synchronized void setPose (PlayerPose3d newPose) {
        this.pose = newPose;
    }

    /**
     * @return  Size of the entire ranger device, in robot cs
     **/
    public synchronized PlayerBbox3d getSize () {
        return this.size;
    }

    /**
     * @param newSize  Size of the entire ranger device, in robot cs
     */
    public synchronized void setSize (PlayerBbox3d newSize) {
        this.size = newSize;
    }

    /**
     * @return  The number of valid poses.
     **/
    public synchronized int getPoses_count () {
        return (this.poses == null)?0:this.poses.length;
    }

    /**
     * @return  The number of valid sizes.
     **/
    public synchronized int getSizes_count () {
        return (this.sizes == null)?0:this.sizes.length;
    }

    /**
     * @return  Pose of each ranger, in robot cs
     **/
    public synchronized PlayerPose3d[] getPoses () {
        return this.poses;
    }

    /**
     * @param newPoses  Pose of each ranger, in robot cs
     */
    public synchronized void setPoses (PlayerPose3d[] newPoses) {
        this.poses = newPoses;
    }

    /**
     * @return  Size of each ranger, in robot cs
     **/
    public synchronized PlayerBbox3d[] getSizes () {
        return this.sizes;
    }

    /**
     * @param newSizes  Size of each ranger, in robot cs
     */
    public synchronized void setSizes (PlayerBbox3d[] newSizes) {
        this.sizes = newSizes;
    }

}