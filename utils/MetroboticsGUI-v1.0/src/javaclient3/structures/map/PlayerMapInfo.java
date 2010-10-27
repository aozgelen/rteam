/*
 *  Player Java Client 3 - PlayerMapInfo.java
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

package javaclient3.structures.map;

import javaclient3.structures.*;

/**
 * Data AND Request/reply: Map information.
 * To retrieve the size and scale information of a map, send a null
 * PLAYER_MAP_REQ_GET_INFO request. This message can also be sent as data,
 * with the subtype PLAYER_MAP_DATA_INFO, depending on the underlying
 * driver. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerMapInfo implements PlayerConstants {

    // The scale of the map [m/pixel]. 
    private float scale;
    // The size of the map [pixels]. 
    private int width;
    // The size of the map [pixels]. 
    private int height;
    // The origin of the map [m, m, rad]. That is, the real-world pose of
//    * cell (0,0) in the map 
    private PlayerPose origin;


    /**
     * @return  The scale of the map [m/pixel]. 
     **/
    public synchronized float getScale () {
        return this.scale;
    }

    /**
     * @param newScale  The scale of the map [m/pixel]. 
     *
     */
    public synchronized void setScale (float newScale) {
        this.scale = newScale;
    }
    /**
     * @return  The size of the map [pixels]. 
     **/
    public synchronized int getWidth () {
        return this.width;
    }

    /**
     * @param newWidth  The size of the map [pixels]. 
     *
     */
    public synchronized void setWidth (int newWidth) {
        this.width = newWidth;
    }
    /**
     * @return  The size of the map [pixels]. 
     **/
    public synchronized int getHeight () {
        return this.height;
    }

    /**
     * @param newHeight  The size of the map [pixels]. 
     *
     */
    public synchronized void setHeight (int newHeight) {
        this.height = newHeight;
    }
    /**
     * @return  The origin of the map [m, m, rad]. That is, the real-world pose of
     *    * cell (0,0) in the map 
     **/
    public synchronized PlayerPose getOrigin () {
        return this.origin;
    }

    /**
     * @param newOrigin  The origin of the map [m, m, rad]. That is, the real-world pose of
     *    * cell (0,0) in the map 
     *
     */
    public synchronized void setOrigin (PlayerPose newOrigin) {
        this.origin = newOrigin;
    }

}