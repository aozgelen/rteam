	/*
 *  Player Java Client 3 - RFIDInterface.java
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
import javaclient3.structures.rfid.PlayerRfidData;
import javaclient3.structures.rfid.PlayerRfidTag;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The RFID interface provides access to a RFID reader (driver implementations
 * include RFID readers such as Skyetek M1 and Inside M300).
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class RFIDInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (RFIDInterface.class.getName ());

    private PlayerRfidData prdata;
    private boolean        readyPrdata = false;

    /**
     * Constructor for RFIDInterface.
     * @param pc a reference to the PlayerClient object
     */
    public RFIDInterface (PlayerClient pc) { super(pc); }
    
    /**
     * Read the RFID data packet.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_RFID_DATA: {
                    this.timestamp = header.getTimestamp();

                    // Buffer for reading tags_count
                    byte[] buffer = new byte[8];
                    // Read tags_count, array_count
                    is.readFully (buffer, 0, 8);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int tagsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    PlayerRfidTag[] prts = new PlayerRfidTag[tagsCount];
                    for (int i = 0; i < tagsCount; i++) {
                        PlayerRfidTag prt = new PlayerRfidTag ();
                        
                        // Buffer for reading type, guid_count
                        buffer = new byte[12];
                        // Read type, guid_count, array_count
                        is.readFully (buffer, 0, 12);
                        
                        xdr = new XdrBufferDecodingStream (buffer);
                        xdr.beginDecoding ();
                        prt.setType (xdr.xdrDecodeInt ());
                        int guidCount = xdr.xdrDecodeInt ();
                        xdr.endDecoding   ();
                        xdr.close ();
                        
                        buffer = new byte[PLAYER_RFID_MAX_GUID];
                        is.readFully (buffer, 0, guidCount);
                        
                        // Take care of the residual zero bytes
                        if ((guidCount % 4) != 0)
                            is.readFully (buffer, 0, 4 - (guidCount % 4));
                        
                        prt.setGuid_count (guidCount);
                        prt.setGuid       (buffer);
                        
                        prts[i] = prt;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    prdata = new PlayerRfidData ();
                    
                    prdata.setTags_count (tagsCount);
                    prdata.setTags       (prts);
                    
                    readyPrdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[RFID] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[RFID] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    
    /**
     * Get the RFID data.
     * @return an object of type PlayerRfidData containing the requested data 
     */
    public PlayerRfidData getData () { return this.prdata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPrdata) {
            readyPrdata = false;
            return true;
        }
        return false;
    }
        
    /**
     * Handle acknowledgement response messages
     * @param header Player header
     */
    public void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_RFID_REQ_POWER: {
                // null response
                break;
            }
            case PLAYER_RFID_REQ_READTAG: {
                // null response
                break;
            }
            case PLAYER_RFID_REQ_WRITETAG: {
                // null response
                break;
            }
            case PLAYER_RFID_REQ_LOCKTAG: {
                // null response
                break;
            }
            default:{
                if (isDebugging)
                    logger.log (Level.FINEST, "[RFID][Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }

}
