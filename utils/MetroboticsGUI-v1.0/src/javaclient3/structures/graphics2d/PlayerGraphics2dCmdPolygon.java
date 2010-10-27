/*
 *  Player Java Client 3 - PlayerGraphics2dCmdPolygon.java
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
 * Draw polygon (PLAYER_GRAPHICS2D_CMD_POLYGON).
 * <br><br>
 * Draw a polygon.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerGraphics2dCmdPolygon implements PlayerConstants {

    // Number of points in this packet
    private int             count;
    // Array of points defining the polygon
    private PlayerPoint2d[] points = new PlayerPoint2d[PLAYER_GRAPHICS2D_MAX_POINTS];
    // Color in which the outline should be drawn
    private PlayerColor     color;
    // Color in which the polygon should be filled
    private PlayerColor     fill_color;
    // If non-zero, the polygon should be drawn filled, else empty
    private byte            filled;


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
     * @return Array of points defining the polygon
     **/
    public synchronized PlayerPoint2d[] getPoints () {
        return this.points;
    }

    /**
     * @param newCount Array of points defining the polygon
     *
     */
    public synchronized void setPoints (PlayerPoint2d[] newPoints) {
        this.points = newPoints;
    }
    
    /**
     * @return Color in which the outline should be drawn
     **/
    public synchronized PlayerColor getColor () {
        return this.color;
    }

    /**
     * @param newColor Color in which the outline should be drawn
     *
     */
    public synchronized void setColor (PlayerColor newColor) {
        this.color = newColor;
    }

    /**
     * @return Color in which the polygon should be filled
     **/
    public synchronized PlayerColor getFill_color () {
        return this.fill_color;
    }

    /**
     * @param newFill_color Color in which the polygon should be filled
     *
     */
    public synchronized void setFill_color (PlayerColor newFill_color) {
        this.fill_color = newFill_color;
    }

    /**
     * @return If non-zero, the polygon should be drawn filled, else empty.
     **/
    public synchronized byte getFilled () {
        return this.filled;
    }

    /**
     * @param newFilled If non-zero, the polygon should be drawn filled, else 
     * empty.
     */
    public synchronized void setFilled (int newFilled) {
        this.filled = (byte)newFilled;
    }
}