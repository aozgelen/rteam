/*
 *  Player Java Client 3 - FiducialInterface.java
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


import javaclient3.structures.PlayerBbox;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.PlayerPose3d;
import javaclient3.structures.fiducial.PlayerFiducialData;
import javaclient3.structures.fiducial.PlayerFiducialFov;
import javaclient3.structures.fiducial.PlayerFiducialGeom;
import javaclient3.structures.fiducial.PlayerFiducialItem;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The fiducial interface provides access to devices that detect coded fiducials 
 * (markers) placed in the environment. It can also be used for devices the 
 * detect natural landmarks.
 * @author Radu Bogdan Rusu, Maxim Batalin, Moshe Sayag
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class FiducialInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;
    
    // Logging support
    private Logger logger = Logger.getLogger (FiducialInterface.class.getName ());

    private PlayerFiducialData pfdata;
    private boolean            readyPfdata = false;
    private PlayerFiducialGeom pfgeom;
    private boolean            readyPfgeom = false;
    private PlayerFiducialFov  pffov;
    private boolean            readyPffov  = false;
    private int                pfid;
    private boolean            readyPfid   = false;

    /**
     * Constructor for FiducialInterface.
     * @param pc a reference to the PlayerClient object
     */
    public FiducialInterface (PlayerClient pc) { super(pc); }

    /**
     * Read the fiducial data packet (all fiducials).
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_FIDUCIAL_DATA_SCAN: {
                    this.timestamp = header.getTimestamp();
               
                    // Buffer for reading fiducials_count
                    byte[] buffer = new byte[8];
                    // Read fiducials_count
                    is.readFully (buffer, 0, 8);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int fiducialsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading fiducials
                    buffer = new byte[PLAYER_FIDUCIAL_MAX_SAMPLES * (4+24*2)];
                    // Read fiducials
                    is.readFully (buffer, 0, fiducialsCount * (4+24*2));
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    PlayerFiducialItem[] pfis = new PlayerFiducialItem[fiducialsCount];
                    for (int i = 0; i < fiducialsCount; i++ ) {
                        PlayerFiducialItem pfi = new PlayerFiducialItem ();
                        pfi.setId (xdr.xdrDecodeInt ());
                        
                        PlayerPose3d pose  = new PlayerPose3d ();
                        PlayerPose3d upose = new PlayerPose3d ();
                        
                        pose.setPx (xdr.xdrDecodeFloat ());
                        pose.setPy (xdr.xdrDecodeFloat ());
                        pose.setPz (xdr.xdrDecodeFloat ());
                        pose.setProll  (xdr.xdrDecodeFloat ());
                        pose.setPpitch (xdr.xdrDecodeFloat ());
                        pose.setPyaw   (xdr.xdrDecodeFloat ());
                        
                        upose.setPx (xdr.xdrDecodeFloat ());
                        upose.setPy (xdr.xdrDecodeFloat ());
                        upose.setPz (xdr.xdrDecodeFloat ());
                        upose.setProll  (xdr.xdrDecodeFloat ());
                        upose.setPpitch (xdr.xdrDecodeFloat ());
                        upose.setPyaw   (xdr.xdrDecodeFloat ());
                        
                        pfi.setPose  (pose);
                        pfi.setUpose (upose);
                        
                        pfis[i] = pfi;
                        
                    }
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    pfdata = new PlayerFiducialData ();
                    
                    pfdata.setFiducials_count (fiducialsCount);
                    pfdata.setFiducials       (pfis);
                    
                    readyPfdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Fiducial] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
   
    /**
     * Request/reply: Get geometry.
     * <br><br>
     * The geometry (pose and size) of the fiducial device can be queried 
     * by sending a null PLAYER_FIDUCIAL_REQ_GET_GEOM request.
     * <br><br>
     * See the player_fiducial_geom structure from player.h
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_FIDUCIAL_REQ_GET_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Couldn't request PLAYER_FIDUCIAL_REQ_GET_GEOM: "
                        + e.toString(), e);
        }
    }

    /**
     * Request/reply: Get/set sensor field of view.
     * <br><br>
     * The field of view of the fiducial device can be set using the 
     * PLAYER_FIDUCIAL_REQ_GET_FOV request (response will be null), and 
     * queried using a null PLAYER_FIDUCIAL_REQ_GET_FOV request.
     * <br><br>
     * See the player_fiducial_fov structure from player.h
     */
    public void queryFOV () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_FIDUCIAL_REQ_GET_FOV, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Couldn't request PLAYER_FIDUCIAL_REQ_GET_FOV: "
                        + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Get/set sensor field of view.
     * <br><br>
     * The field of view of the fiducial device can be set using the 
     * PLAYER_FIDUCIAL_REQ_GET_FOV request (response will be null), and 
     * queried using a null PLAYER_FIDUCIAL_REQ_GET_FOV request.
     * <br><br>
     * See the player_fiducial_fov structure from player.h
     * @param pff a PlayerFiducialFov structure holding the required data
     */
    public void setFov (PlayerFiducialFov pff) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_FIDUCIAL_REQ_SET_FOV, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (pff.getMin_range  ());
            xdr.xdrEncodeFloat (pff.getMax_range  ());
            xdr.xdrEncodeFloat (pff.getView_angle ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Couldn't request PLAYER_FIDUCIAL_REQ_SET_FOV: "
                        + e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Fiducial] : Error while XDR-encoding SET_FOV request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Get/set fiducial ID.
     * <br><br>
     * Some fiducial finder devices display their own fiducial. Send a null 
     * PLAYER_FIDUCIAL_REQ_GET_ID request to get the identifier displayed by 
     * the fiducial.
     * <br><br> 
     * Some devices can dynamically change the identifier they display. They 
     * can use the PLAYER_FIDUCIAL_REQ_SET_ID request to allow a client to set 
     * the  currently displayed value. Make the request with the 
     * player_fiducial_id_t structure. The device replies with the same 
     * structure with the id field set to the value it actually used. You 
     * should check this value, as the device may not be able to display the 
     * value you requested.
     * <br><br>
     * Currently supported by the stg_fiducial driver.
     * <br><br>
     * See the player_fiducial_id structure from player.h
     */
    public void queryFiducialVal () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_FIDUCIAL_REQ_GET_ID, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Couldn't request PLAYER_FIDUCIAL_REQ_GET_ID: "
                        + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Get/set fiducial ID.
     * <br><br>
     * Some fiducial finder devices display their own fiducial. Send a null 
     * PLAYER_FIDUCIAL_REQ_GET_ID request to get the identifier displayed by 
     * the fiducial.
     * <br><br> 
     * Some devices can dynamically change the identifier they display. They 
     * can use the PLAYER_FIDUCIAL_REQ_SET_ID request to allow a client to set 
     * the  currently displayed value. Make the request with the 
     * player_fiducial_id_t structure. The device replies with the same 
     * structure with the id field set to the value it actually used. You 
     * should check this value, as the device may not be able to display the 
     * value you requested.
     * <br><br>
     * Currently supported by the stg_fiducial driver.
     * <br><br>
     * See the player_fiducial_id structure from player.h
     * @param id the fiducial ID to be displayed
     */
    public void setFiducialVal (int id) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_FIDUCIAL_REQ_SET_ID, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (id);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Couldn't request PLAYER_FIDUCIAL_REQ_SET_ID: "
                        + e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Fiducial] : Error while XDR-encoding SET_ID request: " + 
                        e.toString(), e);
        }
    }

    /**
     * Handle acknowledgement response messages.
     * @param header Player header
     */
    protected void handleResponse (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_FIDUCIAL_REQ_GET_GEOM: {
                    // Buffer for reading the geometry data
                    byte[] buffer = new byte[12+8+8];
                    // Read the geometry data
                    is.readFully (buffer, 0, 12+8+8);
                    
                    pfgeom = new PlayerFiducialGeom ();
                    
                    PlayerPose pose          = new PlayerPose ();
                    PlayerBbox size          = new PlayerBbox ();
                    PlayerBbox fiducial_size = new PlayerBbox ();
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    
                    // pose of the detector in the robot cs [m, m, rad]
                    pose.setPx (xdr.xdrDecodeFloat ());
                    pose.setPy (xdr.xdrDecodeFloat ());
                    pose.setPa (xdr.xdrDecodeFloat ());
                    pfgeom.setPose (pose);
                    // size of the detector [m, m]
                    size.setSw (xdr.xdrDecodeFloat ());
                    size.setSl (xdr.xdrDecodeFloat ());
                    pfgeom.setSize (size);
                    // dimensions of the fiducial in units of [m, m]
                    fiducial_size.setSw (xdr.xdrDecodeFloat ());
                    fiducial_size.setSl (xdr.xdrDecodeFloat ());
                    pfgeom.setFiducial_size (size);
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPfgeom = true;
                    break;
                }
                case PLAYER_FIDUCIAL_REQ_GET_FOV: {
                    // Buffer for reading the fov data
                    byte[] buffer = new byte[12];
                    // Read the fov data
                    is.readFully (buffer, 0, 12);
                    
                    pffov = new PlayerFiducialFov ();
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pffov.setMin_range  (xdr.xdrDecodeFloat ());
                    pffov.setMax_range  (xdr.xdrDecodeFloat ());
                    pffov.setView_angle (xdr.xdrDecodeFloat ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPffov = true;
                    break;
                }
                case PLAYER_FIDUCIAL_REQ_SET_FOV: {
                    break;
                }
                case PLAYER_FIDUCIAL_REQ_GET_ID: {
                    // Buffer for reading the ID data
                    byte[] buffer = new byte[4];
                    // Read the ID data
                    is.readFully (buffer, 0, 4);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pfid = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPfid = true;
                    break;
                }
                case PLAYER_FIDUCIAL_REQ_SET_ID: {
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Fiducial][Debug] : " +
                                "Unexpected response " + header.getSubtype () + 
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Fiducial] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Fiducial] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }

    /**
     * Get the fiducial data.
     * @return an object of type PlayerFiducialData containing the requested data 
     */
    public PlayerFiducialData getData () { return this.pfdata; }
    
    /**
     * Get the geometry data.
     * @return an object of type PlayerFiducialGeom containing the requested data 
     */
    public PlayerFiducialGeom getGeom () { return this.pfgeom; }
    
    /**
     * Get the FOV data.
     * @return an object of type PlayerFiducialFov containing the requested data 
     */
    public PlayerFiducialFov getFOV () { return this.pffov; }
    
    /**
     * Get the ID data.
     * @return an integer (player_fiducial_id) containing the requested data 
     */
    public int getID () { return this.pfid; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPfdata) {
            readyPfdata = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if geometry data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isGeomReady () {
        if (readyPfgeom) {
            readyPfgeom = false;
            return true;
        }
        return false;
    }

    /**
     * Check if FOV data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isFOVReady () {
        if (readyPffov) {
            readyPffov = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if ID data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isIDReady () {
        if (readyPfid) {
            readyPfid = false;
            return true;
        }
        return false;
    }
}
