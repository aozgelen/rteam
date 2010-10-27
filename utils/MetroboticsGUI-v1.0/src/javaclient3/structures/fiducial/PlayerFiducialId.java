/*
 *  Player Java Client 3 - PlayerFiducialId.java
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

package javaclient3.structures.fiducial;

import javaclient3.structures.*;

/**
 * Request/reply: Get/set fiducial ID.
 * Some fiducial finder devices display their own fiducial. Send a null
 * PLAYER_FIDUCIAL_REQ_GET_ID request to get the identifier displayed by the
 * fiducial.
 * Some devices can dynamically change the identifier they display. Send
 * a PLAYER_FIDUCIAL_REQ_SET_ID request to set the currently displayed
 * value. Make the request with the player_fiducial_id_t structure. The
 * device replies with the same structure with the id field set to the value
 * it actually used. You should check this value, as the device may not be
 * able to display the value you requested.
 * Currently supported by the stg_fiducial driver.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerFiducialId implements PlayerConstants {

    // The value displayed 
    private int id;


    /**
     * @return  The value displayed 
     **/
    public synchronized int getId () {
        return this.id;
    }

    /**
     * @param newId  The value displayed 
     *
     */
    public synchronized void setId (int newId) {
        this.id = newId;
    }

}