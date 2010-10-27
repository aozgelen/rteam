/*
 *  Player Java Client 3 - PlayerPointCloud3DData.java
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
 * $Id: PlayerPointCloud3DData.java 74 2006-08-04 08:25:04Z veedee $
 *
 */

package javaclient3.structures.pointcloud3d;

import javaclient3.structures.*;

/**
 * Data: Get cloud (PLAYER_POINTCLOUD3D_DATA_STATE).
 * <br>
 * The basic 3dcloudpoint data packet.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPointCloud3DData implements PlayerConstants {

	int points_count;
	PlayerPointCloud3DElement[] points = new PlayerPointCloud3DElement[PLAYER_POINTCLOUD3D_MAX_POINTS];

    /**
     * @return 
     **/
    public synchronized int getPoints_count () {
        return this.points_count;
    }

    /**
     * @param newPoints_count
     *
     */
    public synchronized void setPoints_count (int newPoints_count) {
        this.points_count = newPoints_count;
    }
    
    /**
     * @return 
     **/
    public synchronized PlayerPointCloud3DElement[] getPoints () {
        return this.points;
    }

    /**
     * @param newPoints
     *
     */
    public synchronized void setPoints (PlayerPointCloud3DElement[] newPoints) {
        this.points = newPoints;
    }
}