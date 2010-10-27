/*
 *  Player Java Client 3 - PlayerWaveformData.java
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

package javaclient3.structures.waveform;

import javaclient3.structures.*;

/**
 * Data: sample (PLAYER_WAVEFORM_DATA_SAMPLE)
 * The waveform interface reads a digitized waveform from the target
 * device.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWaveformData implements PlayerConstants {

    // Bit rate - bits per second 
    private int rate;
    // Depth - bits per sample 
    private int depth;
    // Samples - the number of bytes of raw data 
    private int data_count;
    // data - an array of raw data 
    private byte[] data = new byte[PLAYER_WAVEFORM_DATA_MAX];


    /**
     * @return  Bit rate - bits per second 
     **/
    public synchronized int getRate () {
        return this.rate;
    }

    /**
     * @param newRate  Bit rate - bits per second 
     *
     */
    public synchronized void setRate (int newRate) {
        this.rate = newRate;
    }
    /**
     * @return  Depth - bits per sample 
     **/
    public synchronized int getDepth () {
        return this.depth;
    }

    /**
     * @param newDepth  Depth - bits per sample 
     *
     */
    public synchronized void setDepth (int newDepth) {
        this.depth = newDepth;
    }
    /**
     * @return  Samples - the number of bytes of raw data 
     **/
    public synchronized int getData_count () {
        return this.data_count;
    }

    /**
     * @param newData_count  Samples - the number of bytes of raw data 
     *
     */
    public synchronized void setData_count (int newData_count) {
        this.data_count = newData_count;
    }
    /**
     * @return  data - an array of raw data 
     **/
    public synchronized byte[] getData () {
        return this.data;
    }

    /**
     * @param newData  data - an array of raw data 
     *
     */
    public synchronized void setData (byte[] newData) {
        this.data = newData;
    }

}