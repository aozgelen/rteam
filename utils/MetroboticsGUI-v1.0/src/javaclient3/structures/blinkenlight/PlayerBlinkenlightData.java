/*
 *  Player Java Client 3 - PlayerBlinkenlightData.java
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

package javaclient3.structures.blinkenlight;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_BLINKENLIGHT_DATA_STATE)
 * The blinkenlight data provides the current state of the indicator
 * light.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBlinkenlightData implements PlayerConstants {

    // FALSE: disabled, TRUE: enabled 
    private byte enable;
    // flash period (one whole on-off cycle) [s]. 
    private float period;
    // flash duty cycle (ratio of time-on to time-off in one cycle)
    private float dutycycle;
    // the color of the light
    private PlayerColor color;


    /**
     * @return  FALSE: disabled, TRUE: enabled 
     **/
    public synchronized byte getEnable () {
        return this.enable;
    }

    /**
     * @param newEnable  FALSE: disabled, TRUE: enabled 
     *
     */
    public synchronized void setEnable (byte newEnable) {
        this.enable = newEnable;
    }
    
    /**
     * @return  flash period (one whole on-off cycle) [s]. 
     **/
    public synchronized float getPeriod () {
        return this.period;
    }

    /**
     * @param newPeriod  flash period (one whole on-off cycle) [s]. 
     *
     */
    public synchronized void setPeriod (float newPeriod) {
        this.period = newPeriod;
    }

    /**
     * @return flash duty cycle (ratio of time-on to time-off in one cycle). 
     **/
    public synchronized float getDutycycle () {
        return this.dutycycle;
    }

    /**
     * @param newDutycycle flash duty cycle (ratio of time-on to time-off in 
     * one cycle)
     *
     */
    public synchronized void setDutycycle (float newDutycycle) {
        this.dutycycle = newDutycycle;
    }

    /**
     * @return the color of the light 
     **/
    public synchronized PlayerColor getColor () {
        return this.color;
    }

    /**
     * @param newColor the color of the light
     *
     */
    public synchronized void setColor (PlayerColor newColor) {
        this.color = newColor;
    }
}