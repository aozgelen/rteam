/*
 *  Player Java Client 3 - PtzInterface.java
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
import java.util.logging.Logger;
import java.util.logging.Level;


import javaclient3.structures.PlayerBbox3d;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.PlayerPose3d;
import javaclient3.structures.ptz.PlayerPtzCmd;
import javaclient3.structures.ptz.PlayerPtzData;
import javaclient3.structures.ptz.PlayerPtzGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The PTZ interface is used to control a pan-tilt-zoom unit.
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 * TODO: Test the player 3.0 update
 */
public class PtzInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (PtzInterface.class.getName ());

    private PlayerPtzData ppdata      = null;
    private boolean       readyPpdata = false;
    private PlayerPtzGeom ppgeom      = null;
    private boolean       readyPpgeom = false;
    private int           status      = -1;  // Current pan / tilt status

    /**
     * Constructor for PtzInterface.
     * @param pc a reference to the PlayerClient object
     */
    public PtzInterface (PlayerClient pc) { super(pc); }

    /**
     * Read the data reflecting the current state of the Pan-Tilt-Zoom unit.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_POSITION2D_DATA_STATE: {
                    this.timestamp = header.getTimestamp();

                    ppdata = new PlayerPtzData ();

                    // Buffer for reading PTZ data
                    byte[] buffer = new byte[24];

                    // Read PTZ camera data
                    is.readFully (buffer, 0, 24);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    ppdata.setPan       (xdr.xdrDecodeFloat ());    // pan [rad]
                    ppdata.setTilt      (xdr.xdrDecodeFloat ());    // tilt [rad]
                    ppdata.setZoom      (xdr.xdrDecodeFloat ());    // field of view [rad]
                    ppdata.setPanspeed  (xdr.xdrDecodeFloat ());    // pan velocity [rad/s]
                    ppdata.setTiltspeed (xdr.xdrDecodeFloat ());    // tilt velocity [rad/s]
                    status =             xdr.xdrDecodeInt   ();     // pan / tilt status
                    xdr.endDecoding   ();
                    xdr.close ();

                    readyPpdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ptz] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Get the PTZ data.
     * @return an object of type PlayerPtzData containing the requested data
     */
    public PlayerPtzData getData () { return this.ppdata; }

    /**
     * Get the geometry data.
     * @return an object of type PlayerPtzGeom containing the requested geometry data
     */
    public PlayerPtzGeom getGeom () { return this.ppgeom; }

    /**
     * @return Current pan / tilt status
     */
    public int getStatus() { return this.status; }

    /**
     * Check if data is available.
     * @return true if ready, false if not ready
     */
    public boolean isDataReady () {
        if (readyPpdata) {
            readyPpdata = false;
            return true;
        }
        return false;
    }

    /**
     * Check if geometry data is available.
     * @return true if ready, false if not ready
     */
    public boolean isGeomReady () {
        if (readyPpgeom) {
            readyPpgeom = false;
            return true;
        }
        return false;
    }

    /**
     * The PTZ interface accepts commands that set change the state of the unit.
     * Note that the commands are absolute, not relative.
     * @param ptc a PlayerPtzCmd structure holding the command data
     */
    public void setPTZ (PlayerPtzCmd ptc) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_PTZ_CMD_STATE, 20);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (20);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (ptc.getPan       ());
            xdr.xdrEncodeFloat (ptc.getTilt      ());
            xdr.xdrEncodeFloat (ptc.getZoom      ());
            xdr.xdrEncodeFloat (ptc.getPanspeed  ());
            xdr.xdrEncodeFloat (ptc.getTiltspeed ());
            xdr.xdrEncodeFloat (ptc.getTiltspeed ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Couldn't send PTZ parameters command: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ptz] : Error while XDR-encoding parameters command: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Query geometry.
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PTZ_REQ_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Couldn't request PLAYER_PTZ_REQ_GEOM: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Generic request.
     * <br><br>
     * To send a uni-t specific command to the unit, use the
     * PLAYER_PTZ_REQ_GENERIC request. Whether data is returned depends on the
     * command that was sent. The device may fill in "config" with a reply if
     * applicable.
     * @param configCount length of data in config buffer
     * @param config buffer for command/reply
     */
    public void genericRequest (int configCount, int[] config) {
        if (configCount > PLAYER_PTZ_MAX_CONFIG_LEN)
            configCount = PLAYER_PTZ_MAX_CONFIG_LEN;
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PTZ_REQ_GENERIC, configCount * 4 + 4);

            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (configCount * 4 + 4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (configCount);
            xdr.xdrEncodeIntVector (config);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Couldn't request PLAYER_PTZ_GENERIC_CONFIG_REQ: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ptz] : Error while XDR-encoding GENERIC_CONFIG request: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Control mode.
     * To switch between position position and velocity control (for those
     * drivers that support it), send a PLAYER_PTZ_REQ_CONTROL_MODE request.
     * Note that this request changes how the driver interprets forthcoming
     * commands from all clients.
     * @param mode mode to use: must be either PLAYER_PTZ_VELOCITY_CONTROL (0) or
     * PLAYER_PTZ_POSITION_CONTROL (1)
     */
    public void controlRequest (int mode) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PTZ_REQ_CONTROL_MODE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (mode);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Couldn't request PLAYER_PTZ_REQ_CONTROL_MODE: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ptz] : Error while XDR-encoding CONTROL_MODE request: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Request PTZ camera status.
     */
    public void statusRequest () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PTZ_REQ_STATUS, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Couldn't request PLAYER_PTZ_REQ_STATUS: " +
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
                case PLAYER_PTZ_REQ_GEOM: {
                    // Buffer for reading pose and size
                    byte[] buffer = new byte[48 + 24];

                    // Read pose and size
                    is.readFully (buffer, 0, 48 + 24);

                    ppgeom = new PlayerPtzGeom ();
                    PlayerPose3d pose = new PlayerPose3d ();
                    PlayerBbox3d size = new PlayerBbox3d ();

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();

                    // Pose of the PTZ base [m, m, m]/[rad, rad, rad]
                    pose.setPx     (xdr.xdrDecodeDouble ());
                    pose.setPy     (xdr.xdrDecodeDouble ());
                    pose.setPz     (xdr.xdrDecodeDouble ());
                    pose.setProll  (xdr.xdrDecodeDouble ());
                    pose.setPpitch (xdr.xdrDecodeDouble ());
                    pose.setPyaw   (xdr.xdrDecodeDouble ());
                    ppgeom.setPose (pose);

                    // Dimensions of the PTZ base [m, m, m]
                    size.setSw     (xdr.xdrDecodeDouble ());
                    size.setSl     (xdr.xdrDecodeDouble ());
                    size.setSh     (xdr.xdrDecodeDouble ());
                    ppgeom.setSize (size);

                    xdr.endDecoding   ();
                    xdr.close ();

                    readyPpgeom = true;
                    break;
                }
                case PLAYER_PTZ_REQ_GENERIC: {
                    break;
                }
                case PLAYER_PTZ_REQ_CONTROL_MODE: {
                    break;
                }
                case PLAYER_PTZ_REQ_STATUS: {
                    // Read PTZ camera status
                    byte[] buffer = new byte[4];
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    status = xdr.xdrDecodeInt ();     // pan / tilt status
                    xdr.endDecoding   ();
                    xdr.close ();
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Ptz][Debug] : " +
                                "Unexpected response " + header.getSubtype () +
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Ptz] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ptz] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

}
