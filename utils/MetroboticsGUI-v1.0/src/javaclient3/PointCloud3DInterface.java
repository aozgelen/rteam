/*
 *  Player Java Client 3 - PointCloud3DInterface.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
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
 * $Id: PointCloud3DInterface.java 69 2006-06-30 09:10:43Z veedee $
 *
 */
package javaclient3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javaclient3.structures.PlayerColor;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.PlayerPoint3d;
import javaclient3.structures.pointcloud3d.PlayerPointCloud3DData;
import javaclient3.structures.pointcloud3d.PlayerPointCloud3DElement;
import javaclient3.structures.rfid.PlayerRfidTag;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;

/**
 * The pointcloud3d interface is used to transmit 3-D point cloud data (e.g.,
 * from a 3-D range sensor).
 * 
 * @author Radu Bogdan Rusu
 * @version
 *         <ul>
 *         <li>v2.0 - Player 2.0 supported
 *         </ul>
 */
public class PointCloud3DInterface extends PlayerDevice {

    private static final boolean    isDebugging        = PlayerClient.isDebugging;

    // Logging support
    private Logger                    logger            = Logger
                                                            .getLogger (PointCloud3DInterface.class
                                                                    .getName ());

    private PlayerPointCloud3DData    ppc3data;

    private boolean                    readyPpc3data    = false;

    /**
     * Constructor for PointCloud3DInterface.
     * 
     * @param pc
     *            a reference to the PlayerClient object
     */
    public PointCloud3DInterface (PlayerClient pc) {
        super (pc);
    }

    /**
     * Read the bumper values.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_POINTCLOUD3D_DATA_STATE: {
                    this.timestamp = header.getTimestamp();

                    ppc3data = new PlayerPointCloud3DData ();

                    // Buffer for points_count, array_count
                    byte[] buffer = new byte[8];
                    // Read points_count, array_count
                    is.readFully (buffer, 0, 8);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (
                            buffer);
                    xdr.beginDecoding ();
                    int pointsCount = xdr.xdrDecodeInt (); // points_count
                    xdr.endDecoding ();
                    xdr.close ();

                    PlayerPointCloud3DElement[] points = new PlayerPointCloud3DElement[pointsCount];
                    for (int i = 0; i < pointsCount; i++) {
                        PlayerPointCloud3DElement pt = new PlayerPointCloud3DElement ();

                        PlayerPoint3d point = new PlayerPoint3d ();
                        PlayerColor color = new PlayerColor ();

                        // Buffer for reading point, color
                        buffer = new byte[12 + 16];
                        // Read point, color
                        is.readFully (buffer, 0, 12 + 16);

                        xdr = new XdrBufferDecodingStream (buffer);
                        xdr.beginDecoding ();
                        point.setPx (xdr.xdrDecodeFloat ());
                        point.setPy (xdr.xdrDecodeFloat ());
                        point.setPz (xdr.xdrDecodeFloat ());
                        color.setAlpha (xdr.xdrDecodeByte ());
                        color.setRed (xdr.xdrDecodeByte ());
                        color.setGreen (xdr.xdrDecodeByte ());
                        color.setBlue (xdr.xdrDecodeByte ());
                        xdr.endDecoding ();
                        xdr.close ();

                        pt.setPoint (point);
                        pt.setColor (color);

                        points[i] = pt;

                    }

                    ppc3data.setPoints_count (pointsCount);
                    ppc3data.setPoints (points);

                    readyPpc3data = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException (
                    "[PointCloud3D] : Error reading payload: " + e.toString (),
                    e);
        } catch (OncRpcException e) {
            throw new PlayerException (
                    "[PointCloud3D] : Error while XDR-decoding payload: "
                            + e.toString (), e);
        }
    }

    /**
     * Get the state data.
     * 
     * @return an object of type PlayerPointCloud3DData containing the required
     *         state data
     */
    public PlayerPointCloud3DData getData () {
        return this.ppc3data;
    }

    /**
     * Check if data is available.
     * 
     * @return true if ready, false if not ready
     */
    public boolean isDataReady () {
        if (readyPpc3data) {
            readyPpc3data = false;
            return true;
        }
        return false;
    }
}
