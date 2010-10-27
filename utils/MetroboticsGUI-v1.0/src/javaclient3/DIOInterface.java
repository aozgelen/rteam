/*
 *  Player Java Client 3 - DIOInterface.java
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


import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.dio.PlayerDioData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The dio interface provides access to a digital I/O device.
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class DIOInterface extends PlayerDevice {
    
    private PlayerDioData pddata;
    private boolean       readyPddata = false;

    /**
     * Constructor for DIOInterface.
     * @param pc a reference to the PlayerClient object
    */
    public DIOInterface (PlayerClient pc) { super(pc); }
    
    /**
     * Read the current state of the digital inputs.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_DIO_DATA_VALUES: {
                    this.timestamp = header.getTimestamp();
               
                    pddata = new PlayerDioData ();
                    
                    // Buffer for reading count and digin
                    byte[] buffer = new byte[8];
                    // Read count and digin
                    is.readFully (buffer, 0, 8);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pddata.setCount (xdr.xdrDecodeInt ());        // number of samples
                    pddata.setDigin (xdr.xdrDecodeInt ());        // bitfield of samples
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPddata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[DIO] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[DIO] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Returns the DIO data (number of samples, bitfield of samples)
     * @return the DIO data
     */
    public synchronized PlayerDioData getData () { return pddata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPddata) {
            readyPddata = false;
            return true;
        }
        return false;
    }
    
    /**
     * The dio interface accepts 4-byte commands which consist of the ouput bitfield.
     * @param count the command
     * @param digout the output bitfield
     */
    public void setOutputBitfield (int count, int digout) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_DIO_CMD_VALUES, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt   (count);
            xdr.xdrEncodeFloat (digout);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[DIO] : Couldn't send output bitfield command request: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[DIO] : Error while XDR-encoding bitfield command request: "
                        + e.toString(), e);
        }
    }
    
}
