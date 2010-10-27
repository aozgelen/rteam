/*
 *  Player Java Client 3 - PlayerPowerChargepolicyConfig.java
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

package javaclient3.structures.power;

import javaclient3.structures.*;

/**
 * Request/reply: set charging policy
 *  
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerPowerChargepolicyConfig implements PlayerConstants {

    // If FALSE, recharging is disabled. Defaults to TRUE 
    private byte enable_input;
    // Controlling whether others can recharge from this device. If FALSE, 
    // charging others is disabled. Defaults to TRUE.
    private byte enable_output;


    /**
     * @return Controls recharging. If FALSE, recharging is disabled. Defaults 
     * to TRUE. 
     **/
    public synchronized byte getEnable_input () {
        return this.enable_input;
    }

    /**
     * @param newEnable_input Controls recharging.
     *
     */
    public synchronized void setEnable_input (int newEnable_input) {
        this.enable_input = (byte)newEnable_input;
    }
    /**
     * @return Controlling whether others can recharge from this device. If 
     * FALSE, charging others is disabled. Defaults to TRUE.
     **/
    public synchronized byte getEnable_output () {
        return this.enable_output;
    }

    /**
     * @param newEnable_output Controlling whether others can recharge from 
     * this device.
     */
    public synchronized void setEnable_output (int newEnable_output) {
        this.enable_output = (byte)newEnable_output;
    }

}