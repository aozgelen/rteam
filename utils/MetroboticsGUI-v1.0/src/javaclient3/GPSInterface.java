/*
 *  Player Java Client 3 - GPSInterface.java
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
import javaclient3.structures.gps.PlayerGpsData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The gps interface provides access to an absolute position system, such as GPS.
 * This interface accepts no commands. 
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class GPSInterface extends PlayerDevice {

    private PlayerGpsData pgdata;
    private boolean       readyPgdata     = false;

    /**
     * Constructor for GPSInterface.
     * @param pc a reference to the PlayerClient object
     */
    public GPSInterface (PlayerClient pc) { super (pc); }
    
    /**
     * Read the current global position and heading information.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_GPS_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    pgdata = new PlayerGpsData ();
                    
                    // Buffer for player_gps_data
                    byte[] buffer = new byte[68];
                    // Read player_gps_data
                    is.readFully (buffer, 0, 68);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pgdata.setTime_sec  (xdr.xdrDecodeInt ());
                    pgdata.setTime_usec (xdr.xdrDecodeInt ());
                    pgdata.setLatitude  (xdr.xdrDecodeInt ());
                    pgdata.setLongitude (xdr.xdrDecodeInt ());
                    pgdata.setAltitude  (xdr.xdrDecodeInt ());
                    pgdata.setUtm_e     (xdr.xdrDecodeDouble ());
                    pgdata.setUtm_n     (xdr.xdrDecodeDouble ());
                    pgdata.setQuality   (xdr.xdrDecodeInt ());
                    pgdata.setNum_sats  (xdr.xdrDecodeInt ());
                    pgdata.setHdop      (xdr.xdrDecodeInt ());
                    pgdata.setVdop      (xdr.xdrDecodeInt ());
                    pgdata.setErr_horz  (xdr.xdrDecodeDouble ());
                    pgdata.setErr_vert  (xdr.xdrDecodeDouble ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPgdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[GPS] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[GPS] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the GPS data.
     * @return an object of type PlayerGpsData containing the requested GPS data 
     */
    public PlayerGpsData getData () { return this.pgdata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPgdata) {
            readyPgdata = false;
            return true;
        }
        return false;
    }
}
