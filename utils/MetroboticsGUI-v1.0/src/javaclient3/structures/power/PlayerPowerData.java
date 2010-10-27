/*
 *  Player Java Client 3 - PlayerPowerData.java
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
 * Data: voltage (PLAYER_POWER_DATA_VOLTAGE)
 * The power interface returns data in this format. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPowerData implements PlayerConstants {

    // Status bits. The driver will set the bits to indicate which fields
    // it is using. Bitwise-and with PLAYER_POWER_MASK_X values to see
    // which fields are being set.
    private int valid;
    // Battery voltage [V] 
    private float volts;
    // Percent of full charge [%] 
    private float percent;
    // energy stored [J]. 
    private float joules;
    // estimated current energy consumption (negative values) or
    // aquisition (positive values) [W]. 
    private float watts;
    // charge exchange status: if 1, the device is currently receiving
    // charge from another energy device. If -1 the device is currently
    // providing charge to another energy device. If 0, the device is
    // not exchanging charge with an another device. 
    private int charging;


    /**
     * @return  Status bits. The driver will set the bits to indicate which 
     * fields it is using. Bitwise-and with PLAYER_POWER_MASK_X values to see
     * which fields are being set.
     **/
    public synchronized int getValid () {
        return this.valid;
    }

    /**
     * @param newValid  Status bits. The driver will set the bits to indicate 
     * which fields it is using. Bitwise-and with PLAYER_POWER_MASK_X values to 
     * see which fields are being set.
     */
    public synchronized void setValid (int newValid) {
        this.valid = newValid;
    }

    /**
     * @return  Battery voltage [V] 
     **/
    public synchronized float getVolts () {
        return this.volts;
    }

    /**
     * @param newVolts  Battery voltage [V] 
     */
    public synchronized void setVolts (float newVolts) {
        this.volts = newVolts;
    }

    /**
     * @return  Percent of full charge [%] 
     **/
    public synchronized float getPercent () {
        return this.percent;
    }

    /**
     * @param newPercent  Percent of full charge [%] 
     */
    public synchronized void setPercent (float newPercent) {
        this.percent = newPercent;
    }
    
    /**
     * @return  energy stored [J]. 
     **/
    public synchronized float getJoules () {
        return this.joules;
    }

    /**
     * @param newJoules  energy stored [J]. 
     */
    public synchronized void setJoules (float newJoules) {
        this.joules = newJoules;
    }
    
    /**
     * @return  estimated current energy consumption (negative values) or
     *       aquisition (positive values) [W]. 
     **/
    public synchronized float getWatts () {
        return this.watts;
    }

    /**
     * @param newWatts  estimated current energy consumption (negative values) 
     * or aquisition (positive values) [W]. 
     */
    public synchronized void setWatts (float newWatts) {
        this.watts = newWatts;
    }
    
    /**
     * @return charge exchange status: if 1, the device is currently receiving
     * charge from another energy device. If -1 the device is currently
     * providing charge to another energy device. If 0, the device is
     * not exchanging charge with an another device. 
     **/
    public synchronized int getCharging () {
        return this.charging;
    }

    /**
     * @param newCharging  charge exchange status: if 1, the device is 
     * currently receiving charge from another energy device. If -1 the device 
     * is currently providing charge to another energy device. If 0, the device 
     * is not exchanging charge with an another device. 
     */
    public synchronized void setCharging (int newCharging) {
        this.charging = newCharging;
    }

}