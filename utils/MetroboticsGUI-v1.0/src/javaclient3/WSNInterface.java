/*
 *  Player Java Client 3 - WSNInterface.java
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
 * $Id$
 *
 */
package javaclient3;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.wsn.PlayerWsnCmd;
import javaclient3.structures.wsn.PlayerWsnData;
import javaclient3.structures.wsn.PlayerWsnNodeData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The WSN interface provides access to a Wireless Sensor Network (driver
 * implementations include WSN's such as Crossbow's MICA2 motes and TeCo's 
 * RCore particles).
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class WSNInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (WSNInterface.class.getName ());

    private PlayerWsnData pwdata;
    private boolean       readyPwdata = false;

    /**
     * Constructor for WSNInterface.
     * @param pc a reference to the PlayerClient object
     */
    public WSNInterface (PlayerClient pc) { super(pc); }
    
    /**
     * Read the WSN data packet.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_WSN_DATA: {
                    this.timestamp = header.getTimestamp();

                    pwdata = new PlayerWsnData ();
                    
                    // Buffer for reading node_{type, id, parent_id}, 
                    // data_packet
                    byte[] buffer = new byte[52];
                    // Read node_{type, id, parent_id}, data_packet
                    is.readFully (buffer, 0, 52);
                    
                    XdrBufferDecodingStream xdr = 
                        new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pwdata.setNode_type      (xdr.xdrDecodeInt ());
                    pwdata.setNode_id        (xdr.xdrDecodeInt ());
                    pwdata.setNode_parent_id (xdr.xdrDecodeInt ());
                    PlayerWsnNodeData pwnd = new PlayerWsnNodeData ();
                    pwnd.setLight       (xdr.xdrDecodeFloat ());
                    pwnd.setMic         (xdr.xdrDecodeFloat ());
                    pwnd.setAccel_x     (xdr.xdrDecodeFloat ());
                    pwnd.setAccel_y     (xdr.xdrDecodeFloat ());
                    pwnd.setAccel_z     (xdr.xdrDecodeFloat ());
                    pwnd.setMagn_x      (xdr.xdrDecodeFloat ());
                    pwnd.setMagn_y      (xdr.xdrDecodeFloat ());
                    pwnd.setMagn_z      (xdr.xdrDecodeFloat ());
                    pwnd.setTemperature (xdr.xdrDecodeFloat ());
                    pwnd.setBattery     (xdr.xdrDecodeFloat ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    pwdata.setData_packet (pwnd);
                    
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPwdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[WSN] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WSN] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    
    /**
     * Get the WSN data.
     * @return an object of type PlayerWsnData containing the requested data 
     */
    public PlayerWsnData getData () { return this.pwdata; }
    
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

    /**
     * Command: set device state (PLAYER_WSN_CMD_DEVSTATE).
     * <br><br>
     * This WSN command sets the state of the node's actuators (such as 
     * indicator lights or buzzer).
     * @param pwc a PlayerWsnCmd structure holding the required data
     */
    public void setDeviceState (PlayerWsnCmd pwc) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_WSN_CMD_DEVSTATE, 16);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (16);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt  (pwc.getNode_id  ());
            xdr.xdrEncodeInt  (pwc.getGroup_id ());
            xdr.xdrEncodeInt  (pwc.getDevice   ());
            xdr.xdrEncodeByte (pwc.getState    ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[WSN] : Couldn't send set LED command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WSN] : Error while XDR-encoding set LED command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Put the node in sleep mode (0) or wake it up (1).
     * <br><br>
     * Send a PLAYER_WSN_POWER_REQ request to power or wake up a node in the 
     * WSN. Null response.
     * @param nodeID the ID of the WSN node (set to -1 for all)
     * @param groupID the group ID of the WSN node (set to -1 for all)
     * @param value 0 for off, 1 for on 
     */
    public void setPower (int nodeID, int groupID, int value) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_WSN_REQ_POWER, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt  (nodeID);
            xdr.xdrEncodeInt  (groupID);
            xdr.xdrEncodeByte ((byte)value);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[WSN] : Couldn't request PLAYER_WSN_POWER_REQ" +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WSN] : Error while XDR-encoding POWER request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: change the data type to RAW or converted engineering
     * units.
     * <br><br>
     * Send a PLAYER_WSN_REQ_DATATYPE request to switch between RAW or converted
     * engineering units values in the data packet. Null response. 
     * @param value data type setting: 0 for RAW values, 1 for converted units
     */
    public void setDataType (int value) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_WSN_REQ_DATATYPE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)value);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[WSN] : Couldn't request PLAYER_WSN_REQ_DATATYPE" +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WSN] : Error while XDR-encoding DATATYPE " + 
                        "request: " + e.toString(), e);
        }
    }

    /**
     * Request/reply: Change data delivery frequency.
     * <br><br>
     * By default, the frequency set for receiving data is set on the wireless 
     * node. Send a PLAYER_WSN_REQ_DATAFREQ request to change the frequency. 
     * Fill in the node ID or set -1 for all nodes. Null response.
     * @param nodeID the ID of the WSN node (set to -1 for all)
     * @param groupID the group ID of the WSN node (set to -1 for all)
     * @param frequency the desired frequency in Hz
     */
    public void setDataFreq (int nodeID, int groupID, double frequency) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_WSN_REQ_DATAFREQ, 16);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (16);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt  (nodeID);
            xdr.xdrEncodeInt  (groupID);
            xdr.xdrEncodeDouble (frequency);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[WSN] : Couldn't request PLAYER_WSN_REQ_DATAFREQ" +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[WSN] : Error while XDR-encoding DATAFREQ " + 
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Handle acknowledgement response messages
     * @param header Player header
     */
    public void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_WSN_REQ_POWER: {
                // null response
                break;
            }
            case PLAYER_WSN_REQ_DATATYPE: {
                // null response
                break;
            }
            case PLAYER_WSN_REQ_DATAFREQ: {
                // null response
                break;
            }
            default: {
                if (isDebugging)
                    logger.log (Level.FINEST, "[WSN][Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }

}
