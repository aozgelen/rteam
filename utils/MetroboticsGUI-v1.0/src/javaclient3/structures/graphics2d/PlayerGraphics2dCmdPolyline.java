/*
 *  Player Java Client 3 - PlayerGraphics2dCmdPolyline.java
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

package javaclient3.structures.graphics2d;

import javaclient3.structures.*;

/**
 * Command: Draw polyline (PLAYER_GRAPHICS2D_CMD_POLYLINE).
 * <br><br>
 * Draw a series of straight line segments between a set of points.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerGraphics2dCmdPolyline implements PlayerConstants {

    // Number of points in this packet
    private int             count;
    // Array of points to be joined by lines
    private PlayerPoint2d[] points = new PlayerPoint2d[PLAYER_GRAPHICS2D_MAX_POINTS];
    // Color in which the line should be drawn
    private PlayerColor     color;


    /**
     * @return Number of points in this packet
     **/
    public synchronized int getCount () {
        return this.count;
    }

    /**
     * @param newCount Number of points in this packet
     *
     */
    public synchronized void setCount (int newCount) {
        this.count = newCount;
    }

    /**
     * @return Array of points to be joined by lines
     **/
    public synchronized PlayerPoint2d[] getPoints () {
        return this.points;
    }

    /**
     * @param newCount Array of points to be joined by lines
     *
     */
    public synchronized void setPoints (PlayerPoint2d[] newPoints) {
        this.points = newPoints;
    }
    
    /**
     * @return Color in which the line should be drawn
     **/
    public synchronized PlayerColor getColor () {
        return this.color;
    }

    /**
     * @param newColor Color in which the line should be drawn
     *
     */
    public synchronized void setColor (PlayerColor newColor) {
        this.color = newColor;
    }
}