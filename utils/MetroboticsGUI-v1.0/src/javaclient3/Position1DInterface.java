/*
 *  Player Java Client 3 - Position1DInterface.java
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
import javaclient3.structures.position1d.PlayerPosition1dCmdPos;
import javaclient3.structures.position1d.PlayerPosition1dCmdVel;
import javaclient3.structures.position1d.PlayerPosition1dData;
import javaclient3.structures.position1d.PlayerPosition1dGeom;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The position1D interface is used to control linear actuators.
 * @author Radu Bogdan Rusu, Maxim A. Batalin
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class Position1DInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (Position1DInterface.class.getName ());

    private PlayerPosition1dData pp1ddata;
    private boolean              readyPp1ddata = false;
    private PlayerPosition1dGeom pp1dgeom;
    private boolean              readyPp1dgeom = false;

    /**
     * Constructor for Position1DInterface.
     * @param pc a reference to the PlayerClient object
     */
    public Position1DInterface (PlayerClient pc) { super (pc); }
    
    /**
     * Read the Position1D data.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_POSITION1D_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    // Buffer for reading pos, vel, stall and status
                    byte[] buffer = new byte[16];
                    // Read pos, vel, stall and status
                    is.readFully (buffer, 0, 16);
                    
                    pp1ddata = new PlayerPosition1dData (); 
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pp1ddata.setPos    (xdr.xdrDecodeFloat ());    // position [m]
                    pp1ddata.setVel    (xdr.xdrDecodeFloat ());    // tr.vel.[m/s]
                    pp1ddata.setStall  (xdr.xdrDecodeByte  ());    // stalled?
                    pp1ddata.setStatus (xdr.xdrDecodeByte  ());    // status
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPp1ddata = true;
                    break;
                }
                case PLAYER_POSITION1D_DATA_GEOM: {
               this.timestamp = header.getTimestamp();
               
               readGeom ();
                    readyPp1dgeom = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * The position1D interface accepts new positions and/or velocities for 
     * the robot's motors (drivers may support position control, speed control 
     * or both).
     * <br><br>
     * See the player_position1d_cmd_pos structure from player.h
     * @param pos position data [m] or [rad]
     * @param vel velocity at which to move to the position [m/s] or [rad/s] 
     * @param state motor state (zero is either off or locked, depending on the driver)
     */
    public void setPosition (float pos, float vel, int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_POSITION1D_CMD_POS, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (pos);
            xdr.xdrEncodeFloat (vel);
            xdr.xdrEncodeByte  ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't send position command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding position command: " + 
                        e.toString(), e);
        }
    }

    /**
     * The position1D interface accepts new positions and/or velocities for 
     * the robot's motors (drivers may support position control, speed control 
     * or both).
     * <br><br>
     * See the player_position1d_cmd_pos structure from player.h
     * @param pp1dcp a PlayerPosition1dCmdPos structure holding the required data
     */
    public void setPosition (PlayerPosition1dCmdPos pp1dcp) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_POSITION1D_CMD_POS, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (pp1dcp.getPos   ());
            xdr.xdrEncodeFloat (pp1dcp.getVel   ());
            xdr.xdrEncodeByte  (pp1dcp.getState ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't send position command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding position command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * The position1D interface accepts new positions and/or velocities for 
     * the robot's motors (drivers may support position control, speed control 
     * or both).
     * <br><br>
     * See the player_position1d_cmd_vel structure from player.h
     * @param vel velocity [m/s] or [rad/s] 
     * @param state motor state (zero is either off or locked, depending on the driver)
     */
    public void setVelocity (float vel, int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_POSITION1D_CMD_VEL, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (vel);
            xdr.xdrEncodeByte  ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't send velocity command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding position command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * The position1D interface accepts new positions and/or velocities for 
     * the robot's motors (drivers may support position control, speed control 
     * or both).
     * <br><br>
     * See the player_position1d_cmd_vel structure from player.h
     * @param pp1dcv a PlayerPosition1dCmdVel structure holding the required data
     */
    public void setVelocity (PlayerPosition1dCmdVel pp1dcv) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_POSITION1D_CMD_VEL, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (pp1dcv.getVel   ());
            xdr.xdrEncodeByte  (pp1dcv.getState ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't send velocity command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding position command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Query geometry.
     * <br><br>
     * To request robot geometry, send a null PLAYER_POSITION1D_GET_GEOM.
     */
    public void queryGeometry () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_GET_GEOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request GET_GEOM: "
                        + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Motor power.
     * <br><br>
     * On some robots, the motor power can be turned on and off from software.
     * To do so, send a PLAYER_POSITION1D_MOTOR_POWER request with the format 
     * given below, and with the appropriate state (zero for motors off and 
     * non-zero for motors on). Null response.
     * <br><br>
     * Be VERY careful with this command! You are very likely to start the 
     * robot running across the room at high speed with the battery charger 
     * still attached.
     * @param state 0 for off, 1 for on 
     */
    public void setMotorPower (int state) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_MOTOR_POWER, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request MOTOR_POWER: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding POWER request: " + 
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Change velocity control.
     * <br><br>
     * Some robots offer different velocity control modes. It can be changed 
     * by sending a PLAYER_POSITION1D_VELOCITY_MODE request with the format 
     * given below, including the appropriate mode. No matter which mode is 
     * used, the external client interface to the position1d device remains
     * the same. Null response. 
     * @param mode driver-specific mode
     */
    public void setVelocityControl (int mode) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_VELOCITY_MODE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (mode);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request VELOCITY_MODE: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding VELOCITY_MODE " +
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Reset odometry.
     * <br><br>
     * To reset the robot's odometry to x = 0, send a 
     * PLAYER_POSITION1D_RESET_ODOM request. Null response.
     */
    public void resetOdometry () {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_RESET_ODOM, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request RESET_ODOM: " + 
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Change control mode.
     * <br><br>
     * To change the control mode, send a PLAYER_POSITION1D_POSITION_MODE 
     * request. Null response.
     * @param state 0 for velocity mode, 1 for position mode
     */
    public void setControlMode (int state) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_POSITION_MODE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request POSITION_MODE: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding POSITION_MODE " +
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Set odometry.
     * <br><br>
     * To set a robot's odometry to a particular state, send a 
     * PLAYER_POSITION1D_SET_ODOM request. Null response.
     * @param pos position (X) in [m] 
     */
    public void setOdometry (float pos) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_SET_ODOM, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (pos);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request SET_ODOM: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding SET_ODOM " +
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Reset odometry.
     * <br><br>
     * To set a robot's odometry to x = 0, send a 
     * PLAYER_POSITION1D_REQ_RESET_ODOM request. Null response.
     * @param value driver-specific
     */
    public void resetOdometry (int value) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_SET_ODOM, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (value);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request RESET_ODOM: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding RESET_ODOM " +
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Set velocity PID parameters.
     * <br><br>
     * To set velocity PID parameters, send a PLAYER_POSITION1D_SPEED_PID 
     * request. Null response.
     * @param kp P parameter
     * @param ki I parameter
     * @param kd D parameter
     */
    public void setVelocityPIDParams (float kp, float ki, float kd) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_SPEED_PID, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (kp);
            xdr.xdrEncodeFloat (ki);
            xdr.xdrEncodeFloat (kd);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request SPEED_PID: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding SPEED_PID " +
                        "request: " + e.toString(), e);
        }
    }

    /**
     * Request/reply: Set position PID parameters.
     * <br><br>
     * To set position PID parameters, send a PLAYER_POSITION1D_POSITION_PID 
     * request. Null response.
     * @param kp P parameter
     * @param ki I parameter
     * @param kd D parameter
     */
    public void setPositionPIDParams (float kp, float ki, float kd) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_POSITION_PID, 12);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (kp);
            xdr.xdrEncodeFloat (ki);
            xdr.xdrEncodeFloat (kd);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request POSITION_PID: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding POSITION_PID " +
                        "request: " + e.toString(), e);
        }
    }
    
    /**
     * Request/reply: Set linear speed profile parameters.
     * <br><br>
     * To set linear speed profile parameters, send a 
     * PLAYER_POSITION1D_SPEED_PROF request. Null response.
     * @param speed max speed [m/s]
     * @param acc max acceleration [m/s^2]
     */
    public void setSpeedProfileParams (float speed, float acc) {
        try {
            sendHeader 
                (PLAYER_MSGTYPE_REQ, PLAYER_POSITION1D_REQ_SPEED_PROF, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeFloat (speed);
            xdr.xdrEncodeFloat (acc);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Couldn't request SPEED_PROF: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-encoding SPEED_PROF " +
                        "request: " + e.toString(), e);
        }
    }
    
    
    private void readGeom () {
        try {
            // Buffer for reading pose and size
            byte[] buffer = new byte[12+8];
            // Read pose and size
            is.readFully (buffer, 0, 12+8);
            
            pp1dgeom = new PlayerPosition1dGeom (); 
            PlayerPose pose = new PlayerPose ();
            PlayerBbox size = new PlayerBbox ();
            
            // Begin decoding the XDR buffer
            XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            
            // pose of the robot base [m, m, rad]
            pose.setPx (xdr.xdrDecodeFloat ());
            pose.setPy (xdr.xdrDecodeFloat ());
            pose.setPa (xdr.xdrDecodeFloat ());
            pp1dgeom.setPose (pose);
            // dimensions of the base [m, m]
            size.setSw (xdr.xdrDecodeFloat ());
            size.setSl (xdr.xdrDecodeFloat ());
            pp1dgeom.setSize (size);
            xdr.endDecoding   ();
            xdr.close ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Position1D] : Error reading geometry data: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Position1D] : Error while XDR-decoding geometry data: " + 
                        e.toString(), e);
        }
    }

    /**
     * Handle acknowledgement response messages.
     * @param header Player header
     */
    protected void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_POSITION1D_REQ_GET_GEOM: {
                readGeom ();
                readyPp1dgeom = true;
                break;
            }
            case PLAYER_POSITION1D_REQ_MOTOR_POWER: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_VELOCITY_MODE: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_POSITION_MODE: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_SET_ODOM: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_RESET_ODOM: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_SPEED_PID: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_POSITION_PID: {
                // null response
                break;
            }
            case PLAYER_POSITION1D_REQ_SPEED_PROF: {
                // null response
                break;
            }
            default:{
                if (isDebugging)
                    logger.log (Level.FINEST, "[Position1D][Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }
    
    /**
     * Get the Position1D data.
     * @return an object of type PlayerPosition1DData containing the requested 
     * data 
     */
    public PlayerPosition1dData getData () { return this.pp1ddata; }
    
    /**
     * Get the geometry data.
     * @return an object of type PlayerPosition1DGeom containing the requested 
     * data 
     */
    public PlayerPosition1dGeom getGeom () { return this.pp1dgeom; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPp1ddata) {
            readyPp1ddata = false;
            return true;
        }
        return false;
    }
    
    /**
     * Check if geometry data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isGeomReady () {
        if (readyPp1dgeom) {
            readyPp1dgeom = false;
            return true;
        }
        return false;
    }

}
