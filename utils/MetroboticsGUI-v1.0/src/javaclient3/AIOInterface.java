/*
 *  Player Java Client 3 - AIOInterface.java
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
import javaclient3.structures.aio.PlayerAioData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;


/**
 * The aio interface provides access to an analog I/O device.
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class AIOInterface extends PlayerDevice {
    
    private PlayerAioData padata;
    private boolean       readyPadata = false;
    
    /**
     * Constructor for AIOInterface.
     * @param pc a reference to the PlayerClient object
     */
    public AIOInterface (PlayerClient pc) { super (pc); }
    
    /**
     * Read the samples values.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_AIO_DATA_STATE: {
                    this.timestamp = header.getTimestamp();

                    // Buffer for reading voltages_count
                    byte[] buffer = new byte[4];
                    // Read voltages_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    // voltages_count
                    int voltagesCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding ();
                    xdr.close ();

                    // Buffer for reading voltages
                    buffer = new byte[PLAYER_AIO_MAX_INPUTS * 4];
                    // Read voltages add one for the voltage count
                    is.readFully (buffer, 0, (voltagesCount + 1) * 4);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    // voltages_count
                    float[] voltages = xdr.xdrDecodeFloatVector ();
                    xdr.endDecoding ();
                    xdr.close ();

                    padata = new PlayerAioData ();
                    padata.setVoltages_count (voltagesCount);    // number of valid samples
                    padata.setVoltages (voltages);                 // the samples

                    readyPadata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[AIO] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[AIO] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Returns the AIO data (number of valid samples, samples)
     * @return the AIO data
     */
    public synchronized PlayerAioData getData () { return padata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPadata) {
            readyPadata = false;
            return true;
        }
        return false;
    }
    
    /**
     * The AIO interface allows for the voltage level on one output to be set
     * <br><br>
     * See the player_aio_cmd structure from player.h
     * @param id which I/O output to command
     * @param voltage voltage level to set
     */
    public void setState (int id, float voltage) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_AIO_CMD_STATE, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt   (id);
            xdr.xdrEncodeFloat (voltage);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[AIO] : Couldn't send state command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[AIO] : Error while XDR-encoding state command: " + 
                        e.toString(), e);
        }
    }

}
