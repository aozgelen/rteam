/*
 *  Player Java Client 3 - PlayerGraphics3dCmdDraw.java
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
 * $Id: PlayerGraphics3dCmdDraw.java 54 2006-03-26 22:14:01Z veedee $
 *
 */

package javaclient3.structures.graphics3d;

import javaclient3.structures.*;

/**
 * Command: Draw. (PLAYER_GRAPHICS3D_CMD_DRAW).
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerGraphics3dCmdDraw implements PlayerConstants {

    // The drawing mode defining how the verticies should be interpreted
    private int             draw_mode;
    // Number of points in this packet
    private int             points_count;
    // Array of points
    private PlayerPoint3d[] points = new PlayerPoint3d[PLAYER_GRAPHICS3D_MAX_POINTS];
    // Color in which the points should be drawn
    private PlayerColor     color;

    /**
     * @return The drawing mode defining how the verticies should be interpreted
     **/
    public synchronized int getDraw_mode () {
        return this.draw_mode;
    }

    /**
     * @param newDraw_mode The drawing mode defining how the verticies should 
     * be interpreted
     */
    public synchronized void setDraw_mode (int newDraw_mode) {
        this.draw_mode = newDraw_mode;
    }

    /**
     * @return Number of points in this packet
     **/
    public synchronized int getCount () {
        return this.points_count;
    }

    /**
     * @param newCount Number of points in this packet
     *
     */
    public synchronized void setCount (int newCount) {
        this.points_count = newCount;
    }

    /**
     * @return Array of points
     **/
    public synchronized PlayerPoint3d[] getPoints () {
        return this.points;
    }

    /**
     * @param newCount Array of points
     *
     */
    public synchronized void setPoints (PlayerPoint3d[] newPoints) {
        this.points = newPoints;
    }
    
    /**
     * @return Color in which the points should be drawn
     **/
    public synchronized PlayerColor getColor () {
        return this.color;
    }

    /**
     * @param newColor Color in which the points should be drawn
     *
     */
    public synchronized void setColor (PlayerColor newColor) {
        this.color = newColor;
    }
}