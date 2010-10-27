/*
 *  Player Java Client 3 - WaveformInterface.java
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
import javaclient3.structures.waveform.PlayerWaveformData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The waveform interface is used to receive arbitrary digital samples, say from 
 * a digital audio device. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class WaveformInterface extends PlayerDevice {
    
    private PlayerWaveformData pwdata;
    private boolean            readyPwdata     = false;

    /**
     * Constructor for WaveformInterface.
     * @param pc a reference to the PlayerClient object
     */
    public WaveformInterface (PlayerClient pc) { super(pc); }

    /**
     * The waveform interface reads a digitized waveform from the target device.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_POSITION2D_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
                
                    pwdata = new PlayerWaveformData ();
                    
                    // Buffer for reading rate, depth, data_count
                    byte[] buffer = new byte[12];
                    // Read rate, depth, data_count
                    is.readFully (buffer, 0, 12);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    
                    pwdata.setRate       (xdr.xdrDecodeInt ());    // bits per second
                    pwdata.setDepth      (xdr.xdrDecodeInt ());    // bits per sample
                    pwdata.setData_count (xdr.xdrDecodeInt ());    // number of bytes of raw data
                    
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading raw data (non XDR)
                    buffer = new byte[PLAYER_WAVEFORM_DATA_MAX];
                    // Read raw data
                    is.readFully (buffer, 0, pwdata.getData_count ());
                    pwdata.setData (buffer);        // raw data
                    
                    // Take care of the residual zero bytes
                    if ((pwdata.getData_count () % 4) != 0)
                        is.readFully (buffer, 0, 4 - (pwdata.getData_count () % 4));
                    
                    readyPwdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Waveform] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Waveform] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the data.
     * @return an object of type PlayerWaveformData containing the requested data 
     */
    public PlayerWaveformData getData () { return this.pwdata; }
    
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
}
