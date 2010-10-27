/*
 *  Player Java Client 3 - LaserInterface.java
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
import javaclient3.structures.PlayerPoint2d;
import javaclient3.structures.PlayerPose;
import javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import javaclient3.structures.laser.PlayerLaserConfig;
import javaclient3.structures.laser.PlayerLaserData;
import javaclient3.structures.laser.PlayerLaserDataScanpose;
import javaclient3.structures.laser.PlayerLaserGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The laser interface provides access to a single-origin scanning range 
 * sensor, such as a SICK laser range-finder (e.g., sicklms200).
 * <br><br>
 * Devices supporting the laser interface can be configured to scan at 
 * different angles and resolutions. As such, the data returned by the 
 * laser interface can take different forms. To make interpretation of 
 * the data simple, the laser data packet contains some extra fields 
 * before the actual range data. These fields tell the client the starting 
 * and ending angles of the scan, the angular resolution of the scan, and 
 * the number of range readings included. Scans proceed counterclockwise 
 * about the laser (0 degrees is forward). The laser can return a maximum of 
 * PLAYER_LASER_MAX_SAMPLES readings; this limits the valid combinations of 
 * scan width and angular resolution.
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class LaserInterface extends PlayerDevice {
    
    private static final boolean isDebugging = PlayerClient.isDebugging;
    
    // Logging support
    private Logger logger = Logger.getLogger (LaserInterface.class.getName ());

    private PlayerLaserData         pldata;
    private boolean                 readyPldata     = false;
    private PlayerLaserDataScanpose pldatascan;
    private boolean                 readyPldatascan = false;
    private PlayerLaserConfig         plconfig;
    private boolean                 readyPlconfig   = false;
    private PlayerLaserGeom         plgeom;
    private boolean                 readyPlgeom     = false;
    
    /**
     * Constructor for LaserInterface.
     * @param pc a reference to the PlayerClient object
     */
    public LaserInterface (PlayerClient pc) { super (pc); }

    private synchronized PlayerLaserData readLaserData () {
        PlayerLaserData pld = new PlayerLaserData ();
        try {
            // Buffer for reading min/max_angle, resolution, max_range, ranges_count
            byte[] buffer = new byte[20];
            // Read min/max_angle, resolution, max_range, ranges_count
            is.readFully (buffer, 0, 20);
            
            // Begin decoding the XDR buffer
            XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            pld.setMin_angle    (xdr.xdrDecodeFloat ());
            pld.setMax_angle    (xdr.xdrDecodeFloat ());
            pld.setResolution   (xdr.xdrDecodeFloat ());
            pld.setMax_range    (xdr.xdrDecodeFloat ());
            int rangesCount = xdr.xdrDecodeInt ();
            xdr.endDecoding   ();
            xdr.close ();
            
            // Buffer for reading range values
            buffer = new byte[PLAYER_LASER_MAX_SAMPLES * 4 + 4];
            // Read range values
            is.readFully (buffer, 0, rangesCount * 4 + 4);
            xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            pld.setRanges (xdr.xdrDecodeFloatVector ());
            xdr.endDecoding   ();
            xdr.close ();
            
            pld.setRanges_count (rangesCount);
            
            // Buffer for intensity_count
            buffer = new byte[8];
            // Read intensity_count
            is.readFully (buffer, 0, 8);
            
            // Begin decoding the XDR buffer
            xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            int intensityCount = xdr.xdrDecodeInt ();
            xdr.endDecoding   ();
            xdr.close ();
            
            // Buffer for reading intensity values (non XDR)
            buffer = new byte[PLAYER_LASER_MAX_SAMPLES];
            // Read intensity values
            is.readFully (buffer, 0, intensityCount);
            pld.setIntensity_count (intensityCount);
            pld.setIntensity       (buffer);
            
            // Take care of the residual zero bytes
            if ((intensityCount % 4) != 0)
                is.readFully (buffer, 0, 4 - (intensityCount % 4));
            
            
            // Buffer for ID
            buffer = new byte[4];
            // Read ID
            is.readFully (buffer, 0, 4);
            xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            pld.setId        (xdr.xdrDecodeInt ());
            xdr.endDecoding   ();
            xdr.close ();
            
            // Compute the cartesian coordinates X and Y
            double currentAngle = pld.getMin_angle  ();
            double resolution   = pld.getResolution ();
            float  ranges[]     = pld.getRanges     ();
            PlayerPoint2d[] points = new PlayerPoint2d[pld.getRanges_count ()];
            // Iterate through the ranges array
            for (int i = 0; i < pld.getRanges_count (); i++)
            {
                PlayerPoint2d pp2d = new PlayerPoint2d ();
                pp2d.setPx ((float)(ranges[i] * Math.cos (currentAngle)));
                pp2d.setPy ((float)(ranges[i] * Math.sin (currentAngle)));
                currentAngle += resolution;
                
                points[i] = pp2d;
            }
            
            pld.setPoints (points);
            
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Error reading laser data: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Laser] : Error while XDR-decoding laser data: " + 
                        e.toString(), e);
        }
        return pld;
    }
    
    /**
     * Read the laser data packet.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_LASER_DATA_SCAN: {
               this.timestamp = header.getTimestamp();
               
               pldata = readLaserData ();
                    readyPldata = true;
                    break;
                }
                case PLAYER_LASER_DATA_SCANPOSE: {
               this.timestamp = header.getTimestamp();
               
               PlayerLaserData pld = readLaserData ();
                    PlayerPose pp = new PlayerPose ();
                    
                    // Buffer for reading pose
                    byte[] buffer = new byte[12];
                    // Read pose
                    is.readFully (buffer, 0, 12);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pp.setPx (xdr.xdrDecodeFloat ());
                    pp.setPy (xdr.xdrDecodeFloat ());
                    pp.setPa (xdr.xdrDecodeFloat ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    pldatascan = new PlayerLaserDataScanpose ();
                    
                    pldatascan.setScan (pld);
                    pldatascan.setPose (pp);
                    
                    readyPldatascan = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Laser] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the laser data.
     * @return an object of type PlayerLaserData containing the laser data 
     */
    public PlayerLaserData getData () { return this.pldata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPldata) {
            readyPldata = false;
            return true;
        }
        return false;
    }

   /** 
    * Get a laser scan with a pose that indicates the (possibly estimated) 
    * pose of the laser when the scan was taken.
    * @return an object of type PlayerLaserDataScanpose containing the laser data 
    */
   public PlayerLaserDataScanpose getDataScanPose () { return this.pldatascan; }
   
   /**
    * Check if Scanpose data is available.
    * @return true if ready, false if not ready 
    */
   public boolean isDataScanposeReady () {
       if (readyPldatascan) {
           readyPldatascan = false;
           return true;
       }
       return false;
   }
    
    /**
     * Configuration request: Get geometry.
     * <br><br>
     * The laser geometry (position and size) can be queried.
     * <br><br>
     * See the player_laser_geom structure from player.h
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LASER_REQ_GET_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Couldn't request PLAYER_LASER_REQ_GET_GEOM: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Configuration request: Set scan properties.
     * <br><br>
     * The scan configuration (resolution, aperture, etc) can be queried 
     * by sending a null PLAYER_LASER_REQ_GET_CONFIG request and modified 
     * by sending a PLAYER_LASER_REQ_SET_CONFIG request. In either case, 
     * the current configuration (after attempting any requested modification) 
     * will be returned in the response.
     * @param plc a PlayerLaserConfig structure holding the laser configuration data
     */
    public void setScanProperties (PlayerLaserConfig plc) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LASER_REQ_SET_CONFIG, 24);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (24);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (plc.getMin_angle  ());
            xdr.xdrEncodeFloat (plc.getMax_angle  ());
            xdr.xdrEncodeFloat (plc.getResolution ());
            xdr.xdrEncodeFloat (plc.getMax_range  ());
            xdr.xdrEncodeFloat (plc.getRange_res  ());
            xdr.xdrEncodeByte  (plc.getIntensity  ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Couldn't request PLAYER_LASER_REQ_SET_CONFIG: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Laser] : Error while XDR-encoding SET_CONFIG request: " + 
                        e.toString(), e);
        }
    }

    /**
     * Configuration request: Get scan properties.
     * <br><br>
     * The scan configuration (resolution, aperture, etc) can be queried 
     * by sending a null PLAYER_LASER_REQ_GET_CONFIG request and modified 
     * by sending a PLAYER_LASER_REQ_SET_CONFIG request. In either case, 
     * the current configuration (after attempting any requested modification) 
     * will be returned in the response.
     */
    public void getScanProperties () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LASER_REQ_GET_CONFIG, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Couldn't request PLAYER_LASER_REQ_GET_CONFIG: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Configuration request: Turn power on/off.
     * @param value 0 to turn laser off, 1 to turn laser on 
     */
    public void setPower (int value) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LASER_REQ_POWER, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)value);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Couldn't request PLAYER_LASER_REQ_POWER: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Laser] : Error while XDR-encoding POWER request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Handle acknowledgement response messages (threaded mode).
     * @param header Player headerd
     */
    public void handleResponse (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_LASER_REQ_GET_GEOM: {
                    // Buffer for reading pose and size
                    byte[] buffer = new byte[12+8];
                    // Read pose and size
                    is.readFully (buffer, 0, 12+8);
                    
                    PlayerPose pp = new PlayerPose ();
                    PlayerBbox pb = new PlayerBbox ();
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pp.setPx (xdr.xdrDecodeFloat ());
                    pp.setPy (xdr.xdrDecodeFloat ());
                    pp.setPa (xdr.xdrDecodeFloat ());
                    pb.setSw (xdr.xdrDecodeFloat ());
                    pb.setSl (xdr.xdrDecodeFloat ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    plgeom = new PlayerLaserGeom ();
                    plgeom.setPose (pp);
                    plgeom.setSize (pb);
                    
                    readyPlgeom = true;
                    break;
                }
                case PLAYER_LASER_REQ_SET_CONFIG: {
                    break;
                }
                case PLAYER_LASER_REQ_GET_CONFIG: {
                    // Buffer for reading laser configuration data
                    byte[] buffer = new byte[24];
                    // Read laser configuration data
                    is.readFully (buffer, 0, 24);
                    
                    plconfig = new PlayerLaserConfig ();
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    plconfig.setMin_angle  (xdr.xdrDecodeFloat ());
                    plconfig.setMax_angle  (xdr.xdrDecodeFloat ());
                    plconfig.setResolution (xdr.xdrDecodeFloat ());
                    plconfig.setMax_range  (xdr.xdrDecodeFloat ());
                    plconfig.setRange_res  (xdr.xdrDecodeFloat ());
                    plconfig.setIntensity  (xdr.xdrDecodeByte  ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPlconfig = true;
                    break;
                }
                case PLAYER_LASER_REQ_POWER: {
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Laser]Debug] : " +
                                "Unexpected response " + header.getSubtype () + 
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Laser] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Laser] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the laser geometry after a PLAYER_LASER_REQ_GET_GEOM request.
     * @return an object of PlayerLaserGeom type containing the laser geometry
     * @see #isReadyPlgeom()
     */
    public PlayerLaserGeom   getPlayerLaserGeom   () { return plgeom; }
    
    /**
     * Get the laser configuration after a PLAYER_LASER_REQ_GET_CONFIG request.
     * @return an object of PlayerLaserConfig type containing the laser configuration
     * @see #isReadyPlconfig()
     */
    public PlayerLaserConfig getPlayerLaserConfig () { return plconfig; }
    
    /**
     * Check if the geometry data is available.
     * @return true if ready, false if not ready
     * @see #getPlayerLaserGeom()
     */
    public boolean isReadyPlgeom () {
        if (readyPlgeom) {
            readyPlgeom = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if the configuration data is available.
     * @return true if ready, false if not ready
     * @see #getPlayerLaserConfig()
     */
    public boolean isReadyPlconfig () {
        if (readyPlconfig) {
            readyPlconfig = false;
            return true;
        }
        return false;
    }
}
