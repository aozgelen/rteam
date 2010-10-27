/*
 *  Player Java Client 3 - MapInterface.java
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
import javaclient3.structures.PlayerPose;
import javaclient3.structures.PlayerSegment;
import javaclient3.structures.map.PlayerMapData;
import javaclient3.structures.map.PlayerMapDataVector;
import javaclient3.structures.map.PlayerMapInfo;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

/**
 * The map interface provides acces to an occupancy grid map. This interface returns no data 
 * and accepts no commands. The map is delivered in tiles, via a sequence of configuration 
 * requests.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class MapInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (MapInterface.class.getName ());
    
    private PlayerMapInfo       pminfo;
    private boolean             readyPminfo       = false;
    private PlayerMapData       pmdata;
    private boolean             readyPmdata       = false;
    private PlayerMapDataVector pmdatavector;
    private boolean             readyPmdatavector = false;
    
    /**
     * Constructor for MapInterface.
     * @param pc a reference to the PlayerClient object
     */
    public MapInterface (PlayerClient pc) { super(pc); }

    /**
     * Read map information data. 
     */
    public synchronized void readMapInfo () {
        try {
            pminfo = new PlayerMapInfo ();
            // Buffer for reading map information
            byte[] buffer = new byte[12+12];
            // Read map information
            is.readFully (buffer, 0, 12+12);
            
            // Begin decoding the XDR buffer
            XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            pminfo.setScale  (xdr.xdrDecodeFloat ());
            pminfo.setWidth  (xdr.xdrDecodeInt   ());
            pminfo.setHeight (xdr.xdrDecodeInt   ());
            
            PlayerPose pp = new PlayerPose ();
            pp.setPx (xdr.xdrDecodeFloat ());
            pp.setPy (xdr.xdrDecodeFloat ());
            pp.setPa (xdr.xdrDecodeFloat ());
            pminfo.setOrigin (pp);
            
            xdr.endDecoding   ();
            xdr.close ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Map] : Error reading map information: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Map] : Error while XDR-decoding map information: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Read the map information.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_MAP_DATA_INFO: {
            this.timestamp = header.getTimestamp();

                readMapInfo ();
                readyPminfo = true;
                break;
            }
        }
    }

    /**
     * Data and Request/reply: Get map information.
     * <br><br>
     * To retrieve the size and scale information of a map, send a null 
     * PLAYER_MAP_REQ_GET_INFO request. This message can also be sent as data, 
     * with the subtype PLAYER_MAP_DATA_INFO, depending on the underlying 
     * driver.
     * <br><br>
     * See the player_map_info structure from player.h
     */
    public void requestMapInformation () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_MAP_REQ_GET_INFO, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Map] : ECouldn't request PLAYER_MAP_GET_INFO_REQ: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Get grid map tile.
     * <br><br>
     * To request a grid map tile, send a PLAYER_MAP_REQ_GET_DATA request with 
     * the tile origin and size you want. Set data_count to 0, and leave the 
     * data field empty. The response will contain origin, size and occupancy 
     * data for a tile. Note that the response tile may not be exactly the same 
     * as the tile you requested (e.g., your requested tile is too large or 
     * runs off the map).
     * <br><br>
     * See the player_map_data structure from player.h
     * @param pmd a PlayerMapData structure filled with the required data
     */
    public void requestMapData (PlayerMapData pmd) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_MAP_REQ_GET_DATA, 24);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (24);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (pmd.getCol    ());
            xdr.xdrEncodeInt (pmd.getRow    ());
            xdr.xdrEncodeInt (pmd.getWidth  ());
            xdr.xdrEncodeInt (pmd.getHeight ());
            xdr.xdrEncodeInt (0);
            xdr.xdrEncodeInt (0);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Map] : Couldn't request PLAYER_MAP_REQ_GET_DATA: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Map] : Error while XDR-encoding GET_DATA request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Get vector map.
     * <br><br>
     * A vector map is represented as line segments. To retreive the vector 
     * map, send a null PLAYER_MAP_REQ_GET_VECTOR request.
     * <br><br>
     * See the player_map_data_vector structure from player.h
     */
    public void requestMapDataVector () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_MAP_REQ_GET_VECTOR, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Map] : Couldn't request PLAYER_MAP_REQ_GET_VECTOR: " + 
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
                case PLAYER_MAP_REQ_GET_INFO: {
                    readMapInfo ();
                    readyPminfo = true;
                    break;
                }
                case PLAYER_MAP_REQ_GET_DATA: {
                    // Buffer for reading col, row, width, height, data_count
                    // NOTE: extra 4 bytes are also returned
                    byte[] buffer = new byte[24];
                    // Read col, row, width, height, data_count
                    is.readFully (buffer, 0, 24);

                    pmdata = new PlayerMapData ();

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pmdata.setCol        (xdr.xdrDecodeInt ());
                    pmdata.setRow        (xdr.xdrDecodeInt ());
                    pmdata.setWidth      (xdr.xdrDecodeInt ());
                    pmdata.setHeight     (xdr.xdrDecodeInt ());
                    pmdata.setData_count (xdr.xdrDecodeInt ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading data (non XDR)
                    buffer = new byte[PLAYER_MAP_MAX_TILE_SIZE];
                    // Read data
                    is.readFully (buffer, 0, pmdata.getData_count ());

                    try {
                        byte[] outbuffer = new byte[PLAYER_MAP_MAX_TILE_SIZE];
                        Inflater decomp = new Inflater ();
                        decomp.reset ();
                        // NOTE: need to offset input buffer by 4 bytes
                        decomp.setInput (buffer, 0, pmdata.getData_count ());
                        int len = decomp.inflate (outbuffer);
                        pmdata.setData (new String (outbuffer).toCharArray ());

                        // Take care of the residual zero bytes
                        if ((pmdata.getData_count () % 4) != 0)
                            is.readFully (buffer, 0, 4 - 
                                    (pmdata.getData_count () % 4));

                        // Reset data count
                        pmdata.setData_count (len);
                        logger.log (Level.INFO, "Map decompress: "
                                + pmdata.getData_count () + " bytes");
                        
                        readyPmdata = true;
                    } catch (Exception ex) {
                        ex.printStackTrace ();
                    }

                    break;
                }
                case PLAYER_MAP_REQ_GET_VECTOR: {
                    // Buffer for reading minx, maxx, miny, maxy, segments_count
                    byte[] buffer = new byte[20];
                    // Read minx, maxx, miny, maxy, segments_count
                    is.readFully (buffer, 0, 20);
                    
                    pmdatavector = new PlayerMapDataVector (); 
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pmdatavector.setMinx           (xdr.xdrDecodeFloat ());
                    pmdatavector.setMaxx           (xdr.xdrDecodeFloat ());
                    pmdatavector.setMiny           (xdr.xdrDecodeFloat ());
                    pmdatavector.setMaxy           (xdr.xdrDecodeFloat ());
                    int segmentsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading data
                    buffer = new byte[PLAYER_MAP_MAX_SEGMENTS * 16];
                    // Read data
                    is.readFully (buffer, 0, segmentsCount * 16);
                    
                    // Begin decoding the XDR buffer
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    PlayerSegment[] segments = new PlayerSegment[segmentsCount];
                    for (int i = 0; i < segmentsCount; i++) {
                        PlayerSegment seg = new PlayerSegment ();
                        seg.setX0 (xdr.xdrDecodeFloat ());
                        seg.setY0 (xdr.xdrDecodeFloat ());
                        seg.setX1 (xdr.xdrDecodeFloat ());
                        seg.setY1 (xdr.xdrDecodeFloat ());
                        
                        segments[i] = seg;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    pmdatavector.setSegments_count (segmentsCount);
                    pmdatavector.setSegments       (segments);
                    
                    readyPmdatavector = true;
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Map][Debug] : " +
                                "Unexpected response " + header.getSubtype () + 
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Map] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Map] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }

    /**
     * Get the map info data.
     * @return an object of type PlayerMapInfo containing the map info data 
     */
    public PlayerMapInfo getData () { return this.pminfo; }
    
    /**
     * Check if data is available. (map information)
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPminfo) {
            readyPminfo = false;
            return true;
        }
        return false;
    }

    /**
     * Get the grid map tile data.
     * @return an object of type PlayerMapData containing the grid map tile data 
     */
    public PlayerMapData getGridData () { return this.pmdata; }
    
    /**
     * Check if grid map tile data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isGridDataReady () {
        if (readyPmdata) {
            readyPmdata = false;
            return true;
        }
        return false;
    }
    
    /**
     * Get the map vector data.
     * @return an object of type PlayerMapDatVector containing the map vector data 
     */
    public PlayerMapDataVector getMapDataVector () { return this.pmdatavector; }
    
    /**
     * Check if map data vector is available.
     * @return true if ready, false if not ready 
     */
    public boolean isMapDataVectorReady () {
        if (readyPmdatavector) {
            readyPmdatavector = false;
            return true;
        }
        return false;
    }
}
