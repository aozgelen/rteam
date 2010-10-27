/*
 *  Player Java Client 3 - ActarrayInterface.java
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
import javaclient3.structures.actarray.PlayerActarrayActuator;
import javaclient3.structures.actarray.PlayerActarrayActuatorgeom;
import javaclient3.structures.actarray.PlayerActarrayData;
import javaclient3.structures.actarray.PlayerActarrayGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The actuator array provides access to an array of actuators.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class ActarrayInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (ActarrayInterface.class.getName ());

    private PlayerActarrayData padata;
    private boolean            readyPadata = false;
    private PlayerActarrayGeom pageom;
    private boolean            readyPageom = false;

    /**
     * Constructor for ActarrayInterface.
     * @param pc a reference to the PlayerClient object
     */
    public ActarrayInterface (PlayerClient pc) { super (pc); }
    
    /**
     * Read the Actarray data.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_ACTARRAY_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    // Buffer for reading actarray_count
                    byte[] buffer = new byte[4];
                    // Read actarray_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int actuatorsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading actuators data
                    buffer = new byte[PLAYER_ACTARRAY_NUM_ACTUATORS * 12];
                    // Read actuators data
                    is.readFully (buffer, 0, actuatorsCount * 12);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    
                    PlayerActarrayActuator[] paas = new PlayerActarrayActuator[actuatorsCount];
                    for (int i = 0; i < actuatorsCount; i++) {
                        PlayerActarrayActuator paa = new PlayerActarrayActuator ();
                        paa.setPosition (xdr.xdrDecodeFloat ());
                        paa.setSpeed    (xdr.xdrDecodeFloat ());
                        paa.setState    (xdr.xdrDecodeByte  ());
                        
                        paas[i] = paa;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();

                    padata = new PlayerActarrayData ();
                    padata.setActuators_count (actuatorsCount);
                    padata.setActuators       (paas);
                    
                    readyPadata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Command: Joint position control.
     * <br><br>
     * Tells a join to attempt to move to a requested position. 
     * @param joint the joint to command
     * @param position the position to move to
     */
    public void setPosition (int joint, float position) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_ACTARRAY_POS_CMD, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)joint);
            xdr.xdrEncodeFloat (position);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't send position command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding position command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Command: Joint speed control.
     * <br><br>
     * Tells a join to attempt to move at a requested speed. 
     * @param joint the joint to command
     * @param speed the speed to move at
     */
    public void setSpeed (int joint, float speed) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_ACTARRAY_POS_CMD, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)joint);
            xdr.xdrEncodeFloat (speed);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't send speed command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding speed command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Command: Joint home (PLAYER_ACTARRAY_HOME_CMD)
     * <br><br>
     * Tells a join (or the whole array) to go to home position. 
     * @param joint the joint to command (set -1 to comand all)
     */
    public void homeCmd (int joint) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_ACTARRAY_HOME_CMD, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)joint);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't send homing command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding homing command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Query geometry.
     * <br><br>
     * Send a null PLAYER_ACTARRAY_GET_GEOM_REQ request to receive the 
     * geometry in this form.
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_ACTARRAY_GET_GEOM_REQ, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't request PLAYER_ACTARRAY_GET_GEOM_REQ: "
                        + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Power.
     * <br><br>
     * Send a PLAYER_ACTARRAY_POWER_REQ request to turn the power to all 
     * actuators in the array on or off. Be careful when turning power on 
     * that the array is not obstructed from its home position in case it 
     * moves to it (common behaviour). Null response. 
     * @param state 0 for off, 1 for on 
     */
    public void setPower (int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_ACTARRAY_POWER_REQ, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't request PLAYER_ACTARRAY_POWER_REQ: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding POWER request: " + 
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Brakes.
     * <br><br>
     * Send a PLAYER_ACTARRAY_BRAKES_REQ request to turn the brakes of all 
     * actuators in the array that have them on or off. Null response. 
     * @param state 0 for off, 1 for on 
     */
    public void setBrakes (int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_ACTARRAY_BRAKES_REQ, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't request PLAYER_ACTARRAY_BRAKES_REQ: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding BRAKES request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Speed.
     * <br><br>
     * Send a PLAYER_ACTARRAY_SPEED_REQ request to set the speed of a joint 
     * for all subsequent movements. Null response.
     * @param joint joint to set speed for
     * @param speed speed setting in rad/s 
     */
    public void setSpeedConfig (int joint, float speed) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_ACTARRAY_SPEED_REQ, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)joint);
            xdr.xdrEncodeFloat (speed);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Couldn't request PLAYER_ACTARRAY_SPEED_REQ: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-encoding SPEED request: " + 
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
                case PLAYER_ACTARRAY_POWER_REQ: {
                    // null response
                    break;
                }
                case PLAYER_ACTARRAY_BRAKES_REQ: {
                    // null response
                    break;
                }
                case PLAYER_ACTARRAY_GET_GEOM_REQ: {
                    // Buffer for reading actuators_count
                    byte[] buffer = new byte[4];
                    // Read actuators_count
                    is.readFully (buffer, 0, 4);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    int actuatorsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    // Buffer for reading actuators geometry data
                    buffer = new byte[PLAYER_ACTARRAY_NUM_ACTUATORS * 28];
                    // Read actuators geometry data
                    is.readFully (buffer, 0, actuatorsCount * 28);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    
                    PlayerActarrayActuatorgeom[] paags = new PlayerActarrayActuatorgeom[actuatorsCount];
                    for (int i = 0; i < actuatorsCount; i++) {
                        PlayerActarrayActuatorgeom paa = new PlayerActarrayActuatorgeom ();
                        paa.setType         (xdr.xdrDecodeByte  ());
                        paa.setMin          (xdr.xdrDecodeFloat ());
                        paa.setCentre       (xdr.xdrDecodeFloat ());
                        paa.setMax          (xdr.xdrDecodeFloat ());
                        paa.setHome         (xdr.xdrDecodeFloat ());
                        paa.setConfig_speed (xdr.xdrDecodeFloat ());
                        paa.setHasbrakes    (xdr.xdrDecodeByte  ());
                        
                        paags[i] = paa;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    pageom = new PlayerActarrayGeom ();
                    pageom.setActuators_count (actuatorsCount);
                    pageom.setActuators       (paags);
                    
                    readyPageom = true;
                    
                    break;
                }
                case PLAYER_ACTARRAY_SPEED_REQ: {
                    // null response
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Actarray][Debug] : " +
                                "Unexpected response " + header.getSubtype () + 
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Actarray] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Actarray] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Get the Actarray data.
     * @return an object of type PlayerActarrayData containing the requested data 
     */
    public PlayerActarrayData getData () { return this.padata; }
    
    /**
     * Get the geometry data.
     * @return an object of type PlayerActarrayGeom containing the requested data 
     */
    public PlayerActarrayGeom getGeom () { return this.pageom; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPadata) {
            readyPadata = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if geometry data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isGeomReady () {
        if (readyPageom) {
            readyPageom = false;
            return true;
        }
        return false;
    }

}
