/*
 *  Player Java Client 3 - PlayerBlobfinderColorConfig.java
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

package javaclient3.structures.blobfinder;

import javaclient3.structures.*;

/**
 * Request/reply: Set tracking color.
 * For some sensors (ie CMUcam), simple blob tracking tracks only one color.
 * To set the tracking color, send a PLAYER_BLOBFINDER_REQ_SET_COLOR request
 * with the format below, including the RGB color ranges (max and min).
 * Values of -1 will cause the track color to be automatically set to the
 * current window color.  This is useful for setting the track color by
 * holding the tracking object in front of the lens.  Null response.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBlobfinderColorConfig implements PlayerConstants {

    // RGB minimum and max values (0-255) *
    private int rmin;
    // RGB minimum and max values (0-255) *
    private int rmax;
    // RGB minimum and max values (0-255) *
    private int gmin;
    // RGB minimum and max values (0-255) *
    private int gmax;
    // RGB minimum and max values (0-255) *
    private int bmin;
    // RGB minimum and max values (0-255) *
    private int bmax;


    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getRmin () {
        return this.rmin;
    }

    /**
     * @param newRmin  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setRmin (int newRmin) {
        this.rmin = newRmin;
    }
    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getRmax () {
        return this.rmax;
    }

    /**
     * @param newRmax  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setRmax (int newRmax) {
        this.rmax = newRmax;
    }
    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getGmin () {
        return this.gmin;
    }

    /**
     * @param newGmin  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setGmin (int newGmin) {
        this.gmin = newGmin;
    }
    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getGmax () {
        return this.gmax;
    }

    /**
     * @param newGmax  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setGmax (int newGmax) {
        this.gmax = newGmax;
    }
    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getBmin () {
        return this.bmin;
    }

    /**
     * @param newBmin  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setBmin (int newBmin) {
        this.bmin = newBmin;
    }
    /**
     * @return  RGB minimum and max values (0-255) *
     **/
    public synchronized int getBmax () {
        return this.bmax;
    }

    /**
     * @param newBmax  RGB minimum and max values (0-255) *
     *
     */
    public synchronized void setBmax (int newBmax) {
        this.bmax = newBmax;
    }

}