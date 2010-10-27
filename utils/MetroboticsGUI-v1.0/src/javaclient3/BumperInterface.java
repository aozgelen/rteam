/*
 *  Player Java Client 3 - BumperInterface.java
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
import javaclient3.structures.PlayerPose;
import javaclient3.structures.bumper.PlayerBumperData;
import javaclient3.structures.bumper.PlayerBumperDefine;
import javaclient3.structures.bumper.PlayerBumperGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The bumper interface returns data from a bumper array. 
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class BumperInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;
    
    // Logging support
    private Logger logger = Logger.getLogger (BumperInterface.class.getName ());

    // object containing player_bumper_geom
    private PlayerBumperData  pbdata;
    private boolean           readyPbdata = false;
    private PlayerBumperGeom  pbgeom;
    private boolean           readyPbgeom = false;
    
    /**
     * Constructor for BumperInterface.
     * @param pc a reference to the PlayerClient object
     */
    public BumperInterface (PlayerClient pc) { super (pc); }
    
    /**
     * Read the bumper values.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_BUMPER_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    pbdata = new PlayerBumperData ();
                    
                    // Buffer for bumpers_count, array_count
                    byte[] buffer = new byte[8];
                    // Read bumpers_count, array_count
                    is.readFully (buffer, 0, 8);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int bumpersCount = xdr.xdrDecodeInt ();        // bumpers_count
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading bumper values (non XDR)
                    buffer = new byte[PLAYER_BUMPER_MAX_SAMPLES];
                    // Read bumper values
                    is.readFully (buffer, 0, bumpersCount);
                    pbdata.setBumpers_count (bumpersCount);        // the number of valid bumper readings
                    pbdata.setBumpers       (buffer);            // array of bumper values     
                    
                    // Take care of the residual zero bytes
                    if ((bumpersCount % 4) != 0)
                        is.readFully (buffer, 0, 4 - (bumpersCount % 4));
                    
                    readyPbdata = true;
                    break;
                }
                case PLAYER_BUMPER_DATA_GEOM: {
                    readGeom ();
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Bumper] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Bumper] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the state data.
     * @return an object of type PlayerBumperData containing the required state data 
     */
    public PlayerBumperData getData () { return this.pbdata; }
    
    /**
     * Get the geometry data.
     * @return an object of type PlayerBumperGeom containing the required geometry data 
     */
    public PlayerBumperGeom getGeom () { return this.pbgeom; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPbdata) {
            readyPbdata = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if geometry data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isGeomReady () {
        if (readyPbgeom) {
            readyPbgeom = false;
            return true;
        }
        return false;
    }

    private void readGeom () {
        try {
            // Buffer for reading bumper_def_count
            byte[] buffer = new byte[4];
            // Read bumper_def_count
            is.readFully (buffer, 0, 4);
            
            // Begin decoding the XDR buffer
            XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            // number of valid bumper definitions
            int bumpersDefCount = xdr.xdrDecodeInt ();
            xdr.endDecoding   ();
            xdr.close ();
            
            // Buffer for reading bumper geometry values
            buffer = new byte[PLAYER_BUMPER_MAX_SAMPLES * 20];
            // Read bumper geometry values
            is.readFully (buffer, 0, bumpersDefCount * 20);
            xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            // bumper geometry values
            PlayerBumperDefine[] pbds = new PlayerBumperDefine[bumpersDefCount];
            for (int i = 0; i < bumpersDefCount; i++) {
                PlayerPose pp = new PlayerPose ();
                pp.setPx (xdr.xdrDecodeFloat ());
                pp.setPy (xdr.xdrDecodeFloat ());
                pp.setPa (xdr.xdrDecodeFloat ());
                
                PlayerBumperDefine pbd = new PlayerBumperDefine ();
                pbd.setPose (pp);
                pbd.setLength (xdr.xdrDecodeFloat ());
                pbd.setRadius (xdr.xdrDecodeFloat ());
                
                pbds[i] = pbd;
            }
            xdr.endDecoding   ();
            xdr.close ();
            
            pbgeom = new PlayerBumperGeom (); 
            pbgeom.setBumper_def_count (bumpersDefCount);
            pbgeom.setBumper_def       (pbds);
            
            readyPbgeom = true;
        } catch (IOException e) {
            throw new PlayerException 
                ("[Bumper] : Error reading geometry data: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Bumper] : Error while XDR-decoding geometry data: " + 
                        e.toString(), e);
        }
    }

    /**
     * Configuration request: Query geometry.
     *<br><br>
     * See the player_bumper_geom structure from player.h
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_BUMPER_GET_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Bumper] : Couldn't request PLAYER_BUMPER_GET_GEOM: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Handle acknowledgement response messages
     * @param header Player header
     */
    public void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_BUMPER_GET_GEOM: {
                readGeom ();
                break;
            }
            default:{
                if (isDebugging)
                    logger.log (Level.FINEST, "[Bumper]Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }
}

