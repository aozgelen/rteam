/*
 *  Player Java Client 3 - PlayerLimbSetposeCmd.java
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

package javaclient3.structures.limb;

import javaclient3.structures.*;

/**
 * Command: Set end effector pose (PLAYER_LIMB_SETPOSE_CMD)
 * Provides a fully-described pose (position, normal vector and
 * orientation vector) for the end effector to move to. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLimbSetposeCmd implements PlayerConstants {

    // Position of the end effector. 
    private float pX;
    // Position of the end effector. 
    private float pY;
    // Position of the end effector. 
    private float pZ;
    // Approach vector. 
    private float aX;
    // Approach vector. 
    private float aY;
    // Approach vector. 
    private float aZ;
    // Orientation vector. 
    private float oX;
    // Orientation vector. 
    private float oY;
    // Orientation vector. 
    private float oZ;


    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPx () {
        return this.pX;
    }

    /**
     * @param newPx  Position of the end effector. 
     *
     */
    public synchronized void setPx (float newPx) {
        this.pX = newPx;
    }
    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPy () {
        return this.pY;
    }

    /**
     * @param newPy  Position of the end effector. 
     *
     */
    public synchronized void setPy (float newPy) {
        this.pY = newPy;
    }
    /**
     * @return  Position of the end effector. 
     **/
    public synchronized float getPz () {
        return this.pZ;
    }

    /**
     * @param newPz  Position of the end effector. 
     *
     */
    public synchronized void setPz (float newPz) {
        this.pZ = newPz;
    }
    /**
     * @return  Approach vector. 
     **/
    public synchronized float getAx () {
        return this.aX;
    }

    /**
     * @param newAx  Approach vector. 
     *
     */
    public synchronized void setAx (float newAx) {
        this.aX = newAx;
    }
    /**
     * @return  Approach vector. 
     **/
    public synchronized float getAy () {
        return this.aY;
    }

    /**
     * @param newAy  Approach vector. 
     *
     */
    public synchronized void setAy (float newAy) {
        this.aY = newAy;
    }
    /**
     * @return  Approach vector. 
     **/
    public synchronized float getAz () {
        return this.aZ;
    }

    /**
     * @param newAz  Approach vector. 
     *
     */
    public synchronized void setAz (float newAz) {
        this.aZ = newAz;
    }
    /**
     * @return  Orientation vector. 
     **/
    public synchronized float getOx () {
        return this.oX;
    }

    /**
     * @param newOx  Orientation vector. 
     *
     */
    public synchronized void setOx (float newOx) {
        this.oX = newOx;
    }
    /**
     * @return  Orientation vector. 
     **/
    public synchronized float getOy () {
        return this.oY;
    }

    /**
     * @param newOy  Orientation vector. 
     *
     */
    public synchronized void setOy (float newOy) {
        this.oY = newOy;
    }
    /**
     * @return  Orientation vector. 
     **/
    public synchronized float getOz () {
        return this.oZ;
    }

    /**
     * @param newOz  Orientation vector. 
     *
     */
    public synchronized void setOz (float newOz) {
        this.oZ = newOz;
    }

}