/*
 *  Player Java Client 3 - PlayerBumperDefine.java
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

package javaclient3.structures.bumper;

import javaclient3.structures.*;

/**
 * The geometry of a single bumper 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBumperDefine implements PlayerConstants {

    // the local pose of a single bumper 
    private PlayerPose pose;
    // length of the sensor [m] 
    private float length;
    // radius of curvature [m] - zero for straight lines 
    private float radius;


    /**
     * @return  the local pose of a single bumper 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  the local pose of a single bumper 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }
    /**
     * @return  length of the sensor [m] 
     **/
    public synchronized float getLength () {
        return this.length;
    }

    /**
     * @param newLength  length of the sensor [m] 
     *
     */
    public synchronized void setLength (float newLength) {
        this.length = newLength;
    }
    /**
     * @return  radius of curvature [m] - zero for straight lines 
     **/
    public synchronized float getRadius () {
        return this.radius;
    }

    /**
     * @param newRadius  radius of curvature [m] - zero for straight lines 
     *
     */
    public synchronized void setRadius (float newRadius) {
        this.radius = newRadius;
    }

}