/*
 *  Player Java Client 3 - RangerInterface.java
 *  Copyright (C) 2010 Jorge Santos Simon
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
 * $Id: RangerInterface.java 84 2007-11-25 23:25:15Z veedee $
 *
 */
package javaclient3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javaclient3.structures.*;
import javaclient3.structures.ranger.PlayerRangerConf;
import javaclient3.structures.ranger.PlayerRangerData;
import javaclient3.structures.ranger.PlayerRangerGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The ranger interface provides access to a collection of range sensors,
 * such as a laser scanner, sonar array or IR array.
 * @author Jorge Santos Simon
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 * TODO Implement PLAYER_RANGER_REQ_INTNS, PLAYER_RANGER_REQ_SET_CONFIG,
 * PLAYER_RANGER_DATA_RANGEPOSE, PLAYER_RANGER_DATA_INTNS and
 * PLAYER_RANGER_DATA_INTNSPOSE.
 */
public class RangerInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (RangerInterface.class.getName ());

    private PlayerRangerData   prdata;
    private boolean            readyPrdata = false;
    private PlayerRangerGeom   prgeom;
    private boolean            readyPrgeom = false;
    private PlayerRangerConf prconf;
    private boolean            readyPrconf = false;

    /**
     * Constructor for RangerInterface.
     * @param pc a reference to the PlayerClient object
     */
    public RangerInterface (PlayerClient pc) { super(pc); }

    /**
     * Read the ranger values.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_RANGER_DATA_RANGE: {
                    this.timestamp = header.getTimestamp();

                    // Buffer for reading ranges_count
                    byte[] buffer = new byte[4];

                    // Read ranges_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int rangesCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading range values
                    buffer = new byte[rangesCount * 8 + 4];

                    // Read range values
                    is.readFully (buffer, 0, rangesCount * 8 + 4);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    double[] ranges = xdr.xdrDecodeDoubleVector ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    prdata = new PlayerRangerData ();
                    prdata.setRanges (ranges);

                    readyPrdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Ranger] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ranger] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Get the state data.
     * @return an object of type PlayerRangerData containing the requested data
     */
    public PlayerRangerData getData () { return this.prdata; }

    /**
     * Get the geometry data.
     * @return an object of type PlayerRangerGeom containing the requested geometry data
     */
    public PlayerRangerGeom getGeom () { return this.prgeom; }

    /**
     * Get the geometry data.
     * @return an object of type PlayerRangerGeom containing the requested geometry data
     */
    public PlayerRangerConf getConf () { return this.prconf; }

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
     * Check if geometry data is available.
     * @return true if ready, false if not ready
     */
    public boolean isGeomReady () {
        if (readyPrgeom) {
            readyPrgeom = false;
            return true;
        }
        return false;
    }

    /**
     * Check if configuration data is available.
     * @return true if ready, false if not ready
     */
    public boolean isConfReady () {
        if (readyPrconf) {
            readyPrconf = false;
            return true;
        }
        return false;
    }

    /**
     * Request/reply: Query geometry.
     * <br><br>
     * See the player_ranger_geom structure from player.h
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_RANGER_REQ_GET_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ranger] : Couldn't request PLAYER_RANGER_REQ_GET_GEOM: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Query configuration.
     * <br><br>
     * See the player_ranger_config structure from player.h
     */
    public void queryConfiguration () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_RANGER_REQ_GET_CONFIG, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ranger] : Couldn't request PLAYER_RANGER_REQ_GET_CONFIG: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Ranger power.
     * <br><br>
     * On some robots, the rangers can be turned on and off from software.
     * <br>
     * To do so, send a PLAYER_RANGER_REQ_POWER request.
     * <br><br>
     * See the player_ranger_power_config structure from player.h
     * @param state turn power off (0) or on (>0)
     */
    public void setRangerPower (int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_RANGER_REQ_POWER, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Ranger] : Couldn't request PLAYER_RANGER_REQ_POWER: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ranger] : Error while XDR-encoding POWER request: " +
                        e.toString(), e);
        }
    }

    /**
     * Handle acknowledgement response messages
     * @param header Player header
     */
    public void handleResponse (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_RANGER_REQ_GET_GEOM: {

                    // Buffer for reading entire ranger device size and size
                    byte[] buffer = new byte[48 + 24];

                    prgeom = new PlayerRangerGeom ();
                    PlayerPose3d pose = new PlayerPose3d ();
                    PlayerBbox3d size = new PlayerBbox3d ();

                    // Read entire ranger device pose and size
                    is.readFully (buffer, 0, 48 + 24);
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pose.setPx     (xdr.xdrDecodeDouble ());
                    pose.setPy     (xdr.xdrDecodeDouble ());
                    pose.setPz     (xdr.xdrDecodeDouble ());
                    pose.setProll  (xdr.xdrDecodeDouble ());
                    pose.setPpitch (xdr.xdrDecodeDouble ());
                    pose.setPyaw   (xdr.xdrDecodeDouble ());
                    size.setSw     (xdr.xdrDecodeDouble ());
                    size.setSl     (xdr.xdrDecodeDouble ());
                    size.setSh     (xdr.xdrDecodeDouble ());
                    xdr.endDecoding   ();
                    xdr.close ();

                    prgeom.setPose (pose);
                    prgeom.setSize (size);

                    // Buffer for reading poses_count
          //          buffer = new byte[4];

                    // Read poses_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int posesCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading ranger poses
                    buffer = new byte[posesCount * 48 + 4];

                    // Read ranger poses
                    is.readFully (buffer, 0, posesCount * 48 + 4);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    xdr.xdrDecodeInt (); // skip poses count
                    PlayerPose3d[] ppr = new PlayerPose3d[posesCount];
                    for (int i = 0; i < posesCount; i++) {
                        ppr[i] = new PlayerPose3d ();
                        ppr[i].setPx     (xdr.xdrDecodeDouble ());
                        ppr[i].setPy     (xdr.xdrDecodeDouble ());
                        ppr[i].setPz     (xdr.xdrDecodeDouble ());
                        ppr[i].setProll  (xdr.xdrDecodeDouble ());
                        ppr[i].setPpitch (xdr.xdrDecodeDouble ());
                        ppr[i].setPyaw   (xdr.xdrDecodeDouble ());
                    }

                    xdr.endDecoding   ();
                    xdr.close ();

                    prgeom.setPoses (ppr);

                    // Read sizes_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int sizesCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading ranger sizes
        //            buffer = new byte[sizesCount * 24 + 4];

                    // Read ranger sizes
                    is.readFully (buffer, 0, sizesCount * 24 + 4);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    xdr.xdrDecodeInt (); // skip sizes count
                    PlayerBbox3d[] pbr = new PlayerBbox3d[sizesCount];
                    for (int i = 0; i < sizesCount; i++) {
                        pbr[i] = new PlayerBbox3d ();
                        pbr[i].setSw (xdr.xdrDecodeDouble ());
                        pbr[i].setSl (xdr.xdrDecodeDouble ());
                        pbr[i].setSh (xdr.xdrDecodeDouble ());
                    }

                    xdr.endDecoding   ();
                    xdr.close ();

                    prgeom.setSizes (pbr);

                    readyPrgeom = true;
                    break;
                }
                case PLAYER_RANGER_REQ_GET_CONFIG: {

                    // Buffer for reading ranger configuration
                    byte[] buffer = new byte[56];

                    prconf = new PlayerRangerConf ();

                    // Read ranger device configuration
                    is.readFully (buffer, 0, 56);
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    prconf.setMin_angle  (xdr.xdrDecodeDouble ());
                    prconf.setMax_angle  (xdr.xdrDecodeDouble ());
                    prconf.setResolution (xdr.xdrDecodeDouble ());
                    prconf.setMin_range  (xdr.xdrDecodeDouble ());
                    prconf.setMax_range  (xdr.xdrDecodeDouble ());
                    prconf.setRange_res  (xdr.xdrDecodeDouble ());
                    prconf.setFrequency  (xdr.xdrDecodeDouble ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    break;
                }
                case PLAYER_RANGER_REQ_POWER: {
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Ranger][Debug] : " +
                                "Unexpected response " + header.getSubtype () +
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Ranger] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Ranger] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }
}
