/*
 *  Player Java Client 3 - WiFiInterface.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu, Maxim Batalin
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
package javaclient3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.wifi.PlayerWifiData;
import javaclient3.structures.wifi.PlayerWifiLink;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The wifi interface provides access to the state of a wireless network interface.
 * @author Radu Bogdan Rusu, Maxim Batalin 
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class WiFiInterface extends PlayerDevice {
    
    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (WiFiInterface.class.getName ());

    private PlayerWifiData pwdata;
    private boolean        readyPwdata = false;

    /**
     * Constructor for WiFiInterface.
     * @param pc a reference to the PlayerClient object
     */
    public WiFiInterface (PlayerClient pc) { super(pc); }
    
    /**
     * Read the WiFi link information.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_WIFI_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    pwdata = new PlayerWifiData ();
                    
                    // Buffer for links_count
                    byte[] buffer = new byte[4];
                    // Read links_count
                    is.readFully (buffer, 0, 4);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int linksCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    int size = (4 + 32*4)*3 + 4*6;
                    // Buffer for reading links
                    buffer = new byte[PLAYER_WIFI_MAX_LINKS * size];
                    // Read bumper values
                    is.readFully (buffer, 0, linksCount * size);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    PlayerWifiLink[] pwls = new PlayerWifiLink[linksCount];
                    for (int i = 0; i < linksCount; i++) {
                        PlayerWifiLink pwl = new PlayerWifiLink ();
                        pwl.setMac_count  (xdr.xdrDecodeInt ());
                        pwl.setMac        (xdr.xdrDecodeByteVector ());
                        pwl.setIp_count   (xdr.xdrDecodeInt ());
                        pwl.setIp         (xdr.xdrDecodeByteVector ());
                        pwl.setEssid_count(xdr.xdrDecodeInt ());
                        pwl.setEssid      (xdr.xdrDecodeByteVector ());
                        pwl.setMode       (xdr.xdrDecodeInt ());
                        pwl.setFreq       (xdr.xdrDecodeInt ());
                        pwl.setEncrypt      (xdr.xdrDecodeInt ());
                        pwl.setQual          (xdr.xdrDecodeInt ());
                        pwl.setLevel      (xdr.xdrDecodeInt ());
                        pwl.setNoise      (xdr.xdrDecodeInt ());
                        
                        pwls[i] = pwl;
                    }
                    
                    pwdata.setLinks_count (linksCount);
                    pwdata.setLinks       (pwls);
                    pwdata.setThroughput (xdr.xdrDecodeInt ());
                    pwdata.setBitrate    (xdr.xdrDecodeInt ());
                    pwdata.setMode       (xdr.xdrDecodeInt ());
                    pwdata.setQual_type  (xdr.xdrDecodeInt ());
                    pwdata.setMaxqual    (xdr.xdrDecodeInt ());
                    pwdata.setMaxlevel   (xdr.xdrDecodeInt ());
                    pwdata.setMaxnoise   (xdr.xdrDecodeInt ());
                    pwdata.setAp (new String (xdr.xdrDecodeByteVector ()).toCharArray ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPwdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[WiFi] : Error reading payload: " + e.toString (), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WiFi] : Error while XDR-decoding payload: " + 
                        e.toString (), e);
        }
    }
    
    /**
     * Get the requested data.
     * @return an object of type PlayerWifiData containing the requested state data 
     */
    public PlayerWifiData getData () { return this.pwdata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPwdata) {
            readyPwdata = false;
            return true;
        }
        return false;
    }

    /**
     * Configuration request: PLAYER_WIFI_MAC
     *<br><br>
     * See the player_wifi_mac_req structure from player.h
     */
    public void queryWifiMac () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_WIFI_MAC, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Wifi] : Couldn't request PLAYER_WIFI_MAC: " + 
                        e.toString (), e);
        }
    }
    
    /**
     * Configuration request: PLAYER_WIFI_IWSPY_ADD
     *<br><br>
     * See the player_wifi_iwspy_addr_req structure from player.h
     */
    public void queryWifiIwspyAddrAdd () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_WIFI_IWSPY_ADD, 32*4);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Wifi] : Couldn't request PLAYER_WIFI_IWSPY_ADD: " + 
                        e.toString (), e);
        }
    }
    
    /**
     * Handle acknowledgement response messages.
     * @param header Player header
     */
    public void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_WIFI_MAC: {
                break;
            }
            default:{
                if (isDebugging)
                    logger.log (Level.FINEST, "[Wifi][Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }

}
