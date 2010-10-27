/*
 *  Player Java Client 3 - PlayerAioCmd.java
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

package javaclient3.structures.aio;

import javaclient3.structures.*;

/**
 * Command: state (PLAYER_AIO_CMD_STATE)
 * The aio interface allows for the voltage level on one output to be set 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAioCmd implements PlayerConstants {

    // Which I/O output to command 
    private int id;
    // Voltage level to set 
    private float voltage;


    /**
     * @return  Which I/O output to command 
     **/
    public synchronized int getId () {
        return this.id;
    }

    /**
     * @param newId  Which I/O output to command 
     *
     */
    public synchronized void setId (int newId) {
        this.id = newId;
    }
    /**
     * @return  Voltage level to set 
     **/
    public synchronized float getVoltage () {
        return this.voltage;
    }

    /**
     * @param newVoltage  Voltage level to set 
     *
     */
    public synchronized void setVoltage (float newVoltage) {
        this.voltage = newVoltage;
    }

}