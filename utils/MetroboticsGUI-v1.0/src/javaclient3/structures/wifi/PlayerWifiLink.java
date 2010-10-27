/*
 *  Player Java Client 3 - PlayerWifiLink.java
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
 * Link information for one host.
 * The wifi interface returns data regarding the signal characteristics
 * of remote hosts as perceived through a wireless network interface; this
 * is the format of the data for each host. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerWifiLink implements PlayerConstants {

    // MAC address. 
    private int mac_count;
    private byte[] mac = new byte[32];
    // IP address. 
    private int ip_count;
    private byte[] ip = new byte[32];
    // ESSID. 
    private int essid_count;
    private byte[] essid = new byte[32];
    // Mode (master, adhoc, etc). 
    private int mode;
    // Frequency [MHz]. 
    private int freq;
    // Encryted?. 
    private int encrypt;

    // Link quality 
    private int qual;
    // Link level 
    private int level;
    // Link noise 
    private int noise;

    /**
     * @return Mac_count
     **/
    public synchronized int getMac_count () {
        return this.mac_count;
    }

    /**
     * @param newMac_count 
     *
     */
    public synchronized void setMac_count (int newMac_count) {
        this.mac_count = newMac_count;
    }
    /**
     * @return MAC address.
     **/
    public synchronized byte[] getMac () {
        return this.mac;
    }

    /**
     * @param newMac MAC address.
     *
     */
    public synchronized void setMac (byte[] newMac) {
        this.mac = newMac;
    }
    /**
     * @return Ip_count
     **/
    public synchronized int getIp_count () {
        return this.ip_count;
    }

    /**
     * @param newIp_count
     *
     */
    public synchronized void setIp_count (int newIp_count) {
        this.ip_count = newIp_count;
    }
    /**
     * @return IP address
     **/
    public synchronized byte[] getIp () {
        return this.ip;
    }

    /**
     * @param newIp IP address
     *
     */
    public synchronized void setIp (byte[] newIp) {
        this.ip = newIp;
    }
    
    /**
     * @return Essid_coun
     **/
    public synchronized int getEssid_count () {
        return this.essid_count;
    }

    /**
     * @param newEssid_count
     *
     */
    public synchronized void setEssid_count (int newEssid_count) {
        this.essid_count = newEssid_count;
    }
    
    /**
     * @return ESSID
     **/
    public synchronized byte[] getEssid () {
        return this.essid;
    }

    /**
     * @param newEssid ESSID
     *
     */
    public synchronized void setEssid (byte[] newEssid) {
        this.essid = newEssid;
    }
    
    /**
     * @return Mode (master, adhoc, etc)
     **/
    public synchronized int getMode () {
        return this.mode;
    }

    /**
     * @param newMode Mode (master, adhoc, etc)
     *
     */
    public synchronized void setMode (int newMode) {
        this.mode = newMode;
    }
    
    /**
     * @return Frequency [MHz]
     **/
    public synchronized int getFreq () {
        return this.freq;
    }

    /**
     * @param newFreq Frequency [MHz]
     *
     */
    public synchronized void setFreq (int newFreq) {
        this.freq = newFreq;
    }
    
    /**
     * @return Encryted?
     **/
    public synchronized int getEncrypt () {
        return this.encrypt;
    }

    /**
     * @param newEncrypt Encryted?
     *
     */
    public synchronized void setEncrypt (int newEncrypt) {
        this.encrypt = newEncrypt;
    }

    /**
     * @return  Link quality 
     **/
    public synchronized int getQual () {
        return this.qual;
    }

    /**
     * @param newQual Link quality 
     *
     */
    public synchronized void setQual (int newQual) {
        this.qual = newQual;
    }
    
    /**
     * @return Link level
     **/
    public synchronized int getLevel () {
        return this.level;
    }

    /**
     * @param newLevel Link level
     *
     */
    public synchronized void setLevel (int newLevel) {
        this.level = newLevel;
    }

    /**
     * @return  Link noise 
     **/
    public synchronized int getNoise () {
        return this.noise;
    }

    /**
     * @param newNoise Link noise 
     *
     */
    public synchronized void setNoise (int newNoise) {
        this.noise = newNoise;
    }
}