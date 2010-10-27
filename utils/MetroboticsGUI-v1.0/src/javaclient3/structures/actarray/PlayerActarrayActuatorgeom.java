/*
 *  Player Java Client 3 - PlayerActarrayActuatorgeom.java
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

package javaclient3.structures.actarray;

import javaclient3.structures.*;

/**
 * Actuator geometry 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerActarrayActuatorgeom implements PlayerConstants {

    // The type of the actuator - linear or rotary. 
    private byte type;
    // The range of motion of the actuator, in m or rad depending on the type. 
    private float min;
    // The range of motion of the actuator, in m or rad depending on the type. 
    private float centre;
    // The range of motion of the actuator, in m or rad depending on the type. 
    private float max;
    // The range of motion of the actuator, in m or rad depending on the type. 
    private float home;
    // The configured speed setting of the actuator - different from current speed. 
    private float config_speed;
    // If the actuator has brakes or not. 
    private byte hasbrakes;


    /**
     * @return  The type of the actuator - linear or rotary. 
     **/
    public synchronized byte getType () {
        return this.type;
    }

    /**
     * @param newType  The type of the actuator - linear or rotary. 
     *
     */
    public synchronized void setType (byte newType) {
        this.type = newType;
    }
    /**
     * @return  The range of motion of the actuator, in m or rad depending on the type. 
     **/
    public synchronized float getMin () {
        return this.min;
    }

    /**
     * @param newMin  The range of motion of the actuator, in m or rad depending on the type. 
     *
     */
    public synchronized void setMin (float newMin) {
        this.min = newMin;
    }
    /**
     * @return  The range of motion of the actuator, in m or rad depending on the type. 
     **/
    public synchronized float getCentre () {
        return this.centre;
    }

    /**
     * @param newCentre  The range of motion of the actuator, in m or rad depending on the type. 
     *
     */
    public synchronized void setCentre (float newCentre) {
        this.centre = newCentre;
    }
    /**
     * @return  The range of motion of the actuator, in m or rad depending on the type. 
     **/
    public synchronized float getMax () {
        return this.max;
    }

    /**
     * @param newMax  The range of motion of the actuator, in m or rad depending on the type. 
     *
     */
    public synchronized void setMax (float newMax) {
        this.max = newMax;
    }
    /**
     * @return  The range of motion of the actuator, in m or rad depending on the type. 
     **/
    public synchronized float getHome () {
        return this.home;
    }

    /**
     * @param newHome  The range of motion of the actuator, in m or rad depending on the type. 
     *
     */
    public synchronized void setHome (float newHome) {
        this.home = newHome;
    }
    /**
     * @return  The configured speed setting of the actuator - different from current speed. 
     **/
    public synchronized float getConfig_speed () {
        return this.config_speed;
    }

    /**
     * @param newConfig_speed  The configured speed setting of the actuator - different from current speed. 
     *
     */
    public synchronized void setConfig_speed (float newConfig_speed) {
        this.config_speed = newConfig_speed;
    }
    /**
     * @return  If the actuator has brakes or not. 
     **/
    public synchronized byte getHasbrakes () {
        return this.hasbrakes;
    }

    /**
     * @param newHasbrakes  If the actuator has brakes or not. 
     *
     */
    public synchronized void setHasbrakes (byte newHasbrakes) {
        this.hasbrakes = newHasbrakes;
    }

}