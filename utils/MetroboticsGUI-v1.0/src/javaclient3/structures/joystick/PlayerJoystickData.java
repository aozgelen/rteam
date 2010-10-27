/*
 *  Player Java Client 3 - PlayerJoystickData.java
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

package javaclient3.structures.joystick;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_JOYSTICK_DATA_STATE)
 * The joystick data packet, which contains the current state of the
 * joystick 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerJoystickData implements PlayerConstants {

    // Current joystick position (unscaled) 
    private int xpos;
    // Current joystick position (unscaled) 
    private int ypos;
    // Scaling factors 
    private int xscale;
    // Scaling factors 
    private int yscale;
    // Button states (bitmask) 
    private int buttons;


    /**
     * @return  Current joystick position (unscaled) 
     **/
    public synchronized int getXpos () {
        return this.xpos;
    }

    /**
     * @param newXpos  Current joystick position (unscaled) 
     *
     */
    public synchronized void setXpos (int newXpos) {
        this.xpos = newXpos;
    }
    /**
     * @return  Current joystick position (unscaled) 
     **/
    public synchronized int getYpos () {
        return this.ypos;
    }

    /**
     * @param newYpos  Current joystick position (unscaled) 
     *
     */
    public synchronized void setYpos (int newYpos) {
        this.ypos = newYpos;
    }
    /**
     * @return  Scaling factors 
     **/
    public synchronized int getXscale () {
        return this.xscale;
    }

    /**
     * @param newXscale  Scaling factors 
     *
     */
    public synchronized void setXscale (int newXscale) {
        this.xscale = newXscale;
    }
    /**
     * @return  Scaling factors 
     **/
    public synchronized int getYscale () {
        return this.yscale;
    }

    /**
     * @param newYscale  Scaling factors 
     *
     */
    public synchronized void setYscale (int newYscale) {
        this.yscale = newYscale;
    }
    /**
     * @return  Button states (bitmask) 
     **/
    public synchronized int getButtons () {
        return this.buttons;
    }

    /**
     * @param newButtons  Button states (bitmask) 
     *
     */
    public synchronized void setButtons (int newButtons) {
        this.buttons = newButtons;
    }

}