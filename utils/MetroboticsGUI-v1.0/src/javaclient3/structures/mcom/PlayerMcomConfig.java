/*
 *  Player Java Client 3 - PlayerMcomConfig.java
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
 * Configuration request to the device. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerMcomConfig implements PlayerConstants {

    // Which request.  Should be one of the defined request ids. 
    private int command;
    // The "type" of the data. 
    private int type;
    // length of channel name 
    private int channel_count;
    // The name of the channel. 
    private int[] channel = new int[MCOM_CHANNEL_LEN];
    // The data. 
    private PlayerMcomData data;


    /**
     * @return  Which request.  Should be one of the defined request ids. 
     **/
    public synchronized int getCommand () {
        return this.command;
    }

    /**
     * @param newCommand  Which request.  Should be one of the defined request ids. 
     *
     */
    public synchronized void setCommand (int newCommand) {
        this.command = newCommand;
    }
    /**
     * @return  The "type" of the data. 
     **/
    public synchronized int getType () {
        return this.type;
    }

    /**
     * @param newType  The "type" of the data. 
     *
     */
    public synchronized void setType (int newType) {
        this.type = newType;
    }
    /**
     * @return  length of channel name 
     **/
    public synchronized int getChannel_count () {
        return this.channel_count;
    }

    /**
     * @param newChannel_count  length of channel name 
     *
     */
    public synchronized void setChannel_count (int newChannel_count) {
        this.channel_count = newChannel_count;
    }
    /**
     * @return  The name of the channel. 
     **/
    public synchronized int[] getChannel () {
        return this.channel;
    }

    /**
     * @param newChannel  The name of the channel. 
     *
     */
    public synchronized void setChannel (int[] newChannel) {
        this.channel = newChannel;
    }
    /**
     * @return  The data. 
     **/
    public synchronized PlayerMcomData getData () {
        return this.data;
    }

    /**
     * @param newData  The data. 
     *
     */
    public synchronized void setData (PlayerMcomData newData) {
        this.data = newData;
    }

}