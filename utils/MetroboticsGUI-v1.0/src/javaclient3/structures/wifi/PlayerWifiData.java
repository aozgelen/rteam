/*
 *  Player Java Client 3 - PlayerWifiData.java
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

package javaclient3.structures.wifi;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_WIFI_DATA_STATE)
 * The complete data packet format. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWifiData implements PlayerConstants {

    // length of said list 
    private int links_count;
    // A list of links 
    private PlayerWifiLink[] links = new PlayerWifiLink[PLAYER_WIFI_MAX_LINKS];
    // mysterious throughput calculated by driver 
    private int throughput;
    // current bitrate of device 
    private int bitrate;
    // operating mode of device 
    private int mode;
    // Indicates type of link quality info we have 
    private int qual_type;
    // Maximum value for quality 
    private int maxqual;
    // Maximum value for level 
    private int maxlevel;
    // Maximum value for noise. 
    private int maxnoise;
    // MAC address of current access point/cell 
    private char[] ap = new char[32];


    /**
     * @return  length of said list 
     **/
    public synchronized int getLinks_count () {
        return this.links_count;
    }

    /**
     * @param newLinks_count  length of said list 
     *
     */
    public synchronized void setLinks_count (int newLinks_count) {
        this.links_count = newLinks_count;
    }
    /**
     * @return  A list of links 
     **/
    public synchronized PlayerWifiLink[] getLinks () {
        return this.links;
    }

    /**
     * @param newLinks  A list of links 
     *
     */
    public synchronized void setLinks (PlayerWifiLink[] newLinks) {
        this.links = newLinks;
    }
    /**
     * @return  mysterious throughput calculated by driver 
     **/
    public synchronized int getThroughput () {
        return this.throughput;
    }

    /**
     * @param newThroughput  mysterious throughput calculated by driver 
     *
     */
    public synchronized void setThroughput (int newThroughput) {
        this.throughput = newThroughput;
    }
    /**
     * @return  current bitrate of device 
     **/
    public synchronized int getBitrate () {
        return this.bitrate;
    }

    /**
     * @param newBitrate  current bitrate of device 
     *
     */
    public synchronized void setBitrate (int newBitrate) {
        this.bitrate = newBitrate;
    }
    /**
     * @return  operating mode of device 
     **/
    public synchronized int getMode () {
        return this.mode;
    }

    /**
     * @param newMode  operating mode of device 
     *
     */
    public synchronized void setMode (int newMode) {
        this.mode = newMode;
    }
    /**
     * @return  Indicates type of link quality info we have 
     **/
    public synchronized int getQual_type () {
        return this.qual_type;
    }

    /**
     * @param newQual_type  Indicates type of link quality info we have 
     *
     */
    public synchronized void setQual_type (int newQual_type) {
        this.qual_type = newQual_type;
    }
    /**
     * @return  Maximum value for quality 
     **/
    public synchronized int getMaxqual () {
        return this.maxqual;
    }

    /**
     * @param newMaxqual  Maximum value for quality 
     *
     */
    public synchronized void setMaxqual (int newMaxqual) {
        this.maxqual = newMaxqual;
    }
    /**
     * @return  Maximum value for level 
     **/
    public synchronized int getMaxlevel () {
        return this.maxlevel;
    }

    /**
     * @param newMaxlevel  Maximum value for level 
     *
     */
    public synchronized void setMaxlevel (int newMaxlevel) {
        this.maxlevel = newMaxlevel;
    }
    /**
     * @return  Maximum value for noise. 
     **/
    public synchronized int getMaxnoise () {
        return this.maxnoise;
    }

    /**
     * @param newMaxnoise  Maximum value for noise. 
     *
     */
    public synchronized void setMaxnoise (int newMaxnoise) {
        this.maxnoise = newMaxnoise;
    }
    /**
     * @return  MAC address of current access point/cell 
     **/
    public synchronized char[] getAp () {
        return this.ap;
    }

    /**
     * @param newAp  MAC address of current access point/cell 
     *
     */
    public synchronized void setAp (char[] newAp) {
        this.ap = newAp;
    }

}