/*
 *  Player Java Client 3 - PlayerMcomData.java
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

package javaclient3.structures.mcom;

import javaclient3.structures.*;

/**
 * A piece of data. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerMcomData implements PlayerConstants {

    // a flag 
    private char full;
    // length of data 
    private int data_count;
    // the data 
    private char[] data = new char[MCOM_DATA_LEN];


    /**
     * @return  a flag 
     **/
    public synchronized char getFull () {
        return this.full;
    }

    /**
     * @param newFull  a flag 
     *
     */
    public synchronized void setFull (char newFull) {
        this.full = newFull;
    }
    /**
     * @return  length of data 
     **/
    public synchronized int getData_count () {
        return this.data_count;
    }

    /**
     * @param newData_count  length of data 
     *
     */
    public synchronized void setData_count (int newData_count) {
        this.data_count = newData_count;
    }
    /**
     * @return  the data 
     **/
    public synchronized char[] getData () {
        return this.data;
    }

    /**
     * @param newData  the data 
     *
     */
    public synchronized void setData (char[] newData) {
        this.data = newData;
    }

}