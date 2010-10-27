/*
 *  Player Java Client 3 - PlannerInterface.java
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
import javaclient3.structures.planner.PlayerPlannerData;
import javaclient3.structures.planner.PlayerPlannerWaypointsReq;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The planner interface provides control of a 2-D motion planner.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 * TODO: Test the player 3.0 update
 */
public class PlannerInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (PlannerInterface.class.getName ());

    private PlayerPlannerData         ppdata;
    private boolean                   readyPpdata      = false;
    private PlayerPlannerWaypointsReq ppwaypoints;
    private boolean                   readyPpWaypoints = false;

    /**
     * Constructor for PlannerInterface.
     * @param pc a reference to the PlayerClient object
     */
    public PlannerInterface (PlayerClient pc) { super(pc); }

    /**
     * Read the planner data.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_PLANNER_DATA_STATE: {
                    this.timestamp = header.getTimestamp();

                    ppdata = new PlayerPlannerData ();

                    // Buffer for reading planner data
                    byte[] buffer = new byte[16 + 72];

                    // Read planner data
                    is.readFully (buffer, 0, 16 + 72);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    ppdata.setValid (xdr.xdrDecodeByte ());
                    ppdata.setDone  (xdr.xdrDecodeByte ());

                    PlayerPose pose     = new PlayerPose ();
                    PlayerPose goal     = new PlayerPose ();
                    PlayerPose waypoint = new PlayerPose ();

                    pose.setPx     (xdr.xdrDecodeDouble ());
                    pose.setPy     (xdr.xdrDecodeDouble ());
                    pose.setPa     (xdr.xdrDecodeDouble ());
                    goal.setPx     (xdr.xdrDecodeDouble ());
                    goal.setPy     (xdr.xdrDecodeDouble ());
                    goal.setPa     (xdr.xdrDecodeDouble ());
                    waypoint.setPx (xdr.xdrDecodeDouble ());
                    waypoint.setPy (xdr.xdrDecodeDouble ());
                    waypoint.setPa (xdr.xdrDecodeDouble ());

                    ppdata.setPos      (pose);
                    ppdata.setGoal     (goal);
                    ppdata.setWaypoint (waypoint);

                    ppdata.setWaypoint_idx    (xdr.xdrDecodeInt ());
                    ppdata.setWaypoints_count (xdr.xdrDecodeInt ());

                    xdr.endDecoding   ();
                    xdr.close ();

                    readyPpdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Planner] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Planner] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Sends a new goal to the planner interface.
     * @param goal a PlayerPose structure containing the goal location (X, Y, A)
     */
    public void setGoal (PlayerPose goal) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_PLANNER_CMD_GOAL, 24);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (24);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeDouble (goal.getPx ());
            xdr.xdrEncodeDouble (goal.getPy ());
            xdr.xdrEncodeDouble (goal.getPa ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Planner] : Couldn't send new goals command: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Planner] : Error while XDR-encoding goals command: " +
                        e.toString(), e);
        }
    }

    /**
     * Configuration request: Get waypoints.
     * <br><br>
     * See the player_planner_waypoints_req structure from player.h
     */
    public void getWaypoints () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PLANNER_REQ_GET_WAYPOINTS, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Planner] : Couldn't request " + "" +
                        "PLAYER_PLANNER_REQ_GET_WAYPOINTS: " +
                        e.toString(), e);
        }
    }

    /**
     * Configuration request: Enable/disable robot motion.
     * <br><br>
     * To enable or disable the planner, send a PLAYER_PLANNER_REQ_ENABLE
     * request. When disabled, the planner will stop the robot. When
     * enabled, the planner should resume plan execution. Null response.
     * <br><br>
     * See the player_planner_enable_req structure from player.h
     * @param state 1 to enable, 0 to disable
     */
    public void setRobotMotion (int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_PLANNER_REQ_ENABLE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Planner] : Couldn't request PLAYER_PLANNER_REQ_ENABLE: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Planner] : Error while XDR-encoding ENABLE request: " +
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
                case PLAYER_PLANNER_REQ_GET_WAYPOINTS: {
                    // Buffer for reading waypoints_count
                    byte[] buffer = new byte[4];

                    // Read waypoints_count
                    is.readFully (buffer, 0, 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();

                    // Number of valid bumper definitions
                    int waypointsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading the waypoints
                    buffer = new byte[waypointsCount * 24];

                    // Read the waypoints
                    is.readFully (buffer, 0, waypointsCount * 24);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    PlayerPose[] waypoints = new PlayerPose[waypointsCount];
                    for (int i = 0; i < waypointsCount; i++) {
                        PlayerPose pp = new PlayerPose ();
                        pp.setPx (xdr.xdrDecodeDouble ());
                        pp.setPy (xdr.xdrDecodeDouble ());
                        pp.setPa (xdr.xdrDecodeDouble ());
                        waypoints[i] = pp;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();

                    ppwaypoints = new PlayerPlannerWaypointsReq ();
                    ppwaypoints.setWaypoints (waypoints);

                    readyPpWaypoints = true;
                    break;
                }
                case PLAYER_PLANNER_REQ_ENABLE: {
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Planner][Debug] : " +
                                "Unexpected response " + header.getSubtype () +
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Planner] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Planner] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Get the planner data.
     * @return an object of type PlayerPlannerData containing the requested data
     */
    public PlayerPlannerData getData () { return this.ppdata; }

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
     * Get the number of waypoints to follow
     * @return number of waypoints to follow as an int
     */
    public synchronized PlayerPlannerWaypointsReq getWaypointData () { return this.ppwaypoints; }

    /**
     * Check if waypoint data is available.
     * @return true if ready, false if not ready
     */
    public synchronized boolean isReadyWaypointData () {
        if (readyPpWaypoints) {
            readyPpWaypoints = false;
            return true;
        }
        return false;
    }

}
