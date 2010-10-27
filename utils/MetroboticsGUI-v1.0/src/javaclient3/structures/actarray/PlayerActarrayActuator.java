/*
 *  Player Java Client 3 - PlayerActarrayActuator.java
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
 * Structure containing a single actuator's information 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerActarrayActuator implements PlayerConstants {

    // The position of the actuator in m or rad depending on the type. 
    private float position;
    // The speed of the actuator in m/s or rad/s depending on the type. 
    private float speed;
    // The current state of the actuator. 
    private byte state;


    /**
     * @return  The position of the actuator in m or rad depending on the type. 
     **/
    public synchronized float getPosition () {
        return this.position;
    }

    /**
     * @param newPosition  The position of the actuator in m or rad depending on the type. 
     *
     */
    public synchronized void setPosition (float newPosition) {
        this.position = newPosition;
    }
    /**
     * @return  The speed of the actuator in m/s or rad/s depending on the type. 
     **/
    public synchronized float getSpeed () {
        return this.speed;
    }

    /**
     * @param newSpeed  The speed of the actuator in m/s or rad/s depending on the type. 
     *
     */
    public synchronized void setSpeed (float newSpeed) {
        this.speed = newSpeed;
    }
    /**
     * @return  The current state of the actuator. 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  The current state of the actuator. 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}