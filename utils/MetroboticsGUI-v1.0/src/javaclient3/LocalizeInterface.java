/*
 *  Player Java Client 3 - LocalizeInterface.java
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
import javaclient3.structures.localize.PlayerLocalizeData;
import javaclient3.structures.localize.PlayerLocalizeGetParticles;
import javaclient3.structures.localize.PlayerLocalizeHypoth;
import javaclient3.structures.localize.PlayerLocalizeParticle;
import javaclient3.structures.localize.PlayerLocalizeSetPose;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The localize interface provides pose information for the robot. Generally
 * speaking, localization drivers will estimate the pose of the robot by
 * comparing observed sensor readings against a pre-defined map of the
 * environment. See, for the example, the amcl driver, which implements a
 * probabilistic Monte-Carlo localization algorithm.
 * <br><br>
 * This interface accepts no commands.
 * @author Radu Bogdan Rusu, Maxim Batalin
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 * TODO: Test the player 3.0 update
 */
public class LocalizeInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (LocalizeInterface.class.getName ());

    private PlayerLocalizeData         pldata;
    private boolean                    readyPldata = false;
    private PlayerLocalizeGetParticles plgp;
    private boolean                    readyPlgp = false;

    /**
     * Constructor for LocalizeInterface.
     * @param pc a reference to the PlayerClient object
     */
    public LocalizeInterface (PlayerClient pc) { super(pc); }

    /**
     * Read an array of hypotheses.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_LOCALIZE_DATA_HYPOTHS: {
                    this.timestamp = header.getTimestamp();

                    pldata = new PlayerLocalizeData ();

                    // Buffer for reading pending_count, pending_time, hypoths_count
                    byte[] buffer = new byte[4 + 8 + 4];

                    // Read pending_count, pending_time, hypoths_count
                    is.readFully (buffer, 0, 4 + 8 + 4);

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pldata.setPending_count (xdr.xdrDecodeInt    ());
                    pldata.setPending_time  (xdr.xdrDecodeDouble ());
                    int hypothsCount = xdr.xdrDecodeInt ();
                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading the hypotheses
                    buffer = new byte[hypothsCount * (24 + 3*8 + 8)];

                    // Read the hypotheses
                    is.readFully (buffer, 0, hypothsCount * (24 + 3*8 + 8));
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();

                    // Hypotheses values
                    PlayerLocalizeHypoth[] plhs = new PlayerLocalizeHypoth[hypothsCount];
                    for (int i = 0; i < hypothsCount; i++) {
                        PlayerLocalizeHypoth plh = new PlayerLocalizeHypoth ();

                        PlayerPose pp = new PlayerPose ();
                        pp.setPx (xdr.xdrDecodeDouble ());
                        pp.setPy (xdr.xdrDecodeDouble ());
                        pp.setPa (xdr.xdrDecodeDouble ());

                        plh.setMean (pp);
                        double[] cov = new double[3];
                        cov[0] = xdr.xdrDecodeDouble ();
                        cov[1] = xdr.xdrDecodeDouble ();
                        cov[2] = xdr.xdrDecodeDouble ();

                        plh.setCov (cov);
                        plh.setAlpha (xdr.xdrDecodeDouble ());

                        plhs[i] = plh;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();

                    pldata.setHypoths_count (hypothsCount);
                    pldata.setHypoths       (plhs);

                    readyPldata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Localize] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Localize] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Set the robot pose estimate.
     * <br><br>
     * Set the current robot pose hypothesis by sending a
     * PLAYER_LOCALIZE_REQ_SET_POSE request. Null response.
     * @param plsp a PlayerLocalizeSetPose structure holding the required data
     */
    public void setPose (PlayerLocalizeSetPose plsp) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LOCALIZE_REQ_SET_POSE, 24 + 24);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (24 + 24);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeDouble (plsp.getMean ().getPx ());
            xdr.xdrEncodeDouble (plsp.getMean ().getPy ());
            xdr.xdrEncodeDouble (plsp.getMean ().getPa ());
            xdr.xdrEncodeDouble (plsp.getCov ()[0]);
            xdr.xdrEncodeDouble (plsp.getCov ()[1]);
            xdr.xdrEncodeDouble (plsp.getCov ()[2]);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Localize] : Couldn't request PLAYER_LOCALIZE_SET_POSE_REQ: "
                        + e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Localize] : Error while XDR-encoding SET_POSE request: " +
                        e.toString(), e);
        }
    }

    /**
     * Request/reply: Get particles.
     * <br><br>
     * To get (usually a subset of) the current particle set (assuming
     * that the underlying driver uses a particle filter), send a null
     * PLAYER_LOCALIZE_REQ_GET_PARTICLES request.
     */
    public void queryParticles () {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_LOCALIZE_REQ_GET_PARTICLES, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException
                ("[Localize] : Couldn't request " +
                        "PLAYER_LOCALIZE_REQ_GET_PARTICLES: "
                        + e.toString(), e);
        }
    }

    /**
     * Handle acknowledgement response messages.
     * @param header Player header
     */
    protected void handleResponse (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_LOCALIZE_REQ_SET_POSE:{
                    break;
                }
                case PLAYER_LOCALIZE_REQ_GET_PARTICLES:{
                    // Buffer for reading mean, variance, particles_count
                    byte[] buffer = new byte[24 + 8 + 4];

                    // Read mean, variance, particles_count
                    is.readFully (buffer, 0, 24 + 8 + 4);

                    plgp = new PlayerLocalizeGetParticles ();
                    PlayerPose mean = new PlayerPose ();

                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();

                    mean.setPx (xdr.xdrDecodeDouble ());
                    mean.setPy (xdr.xdrDecodeDouble ());
                    mean.setPa (xdr.xdrDecodeDouble ());

                    plgp.setMean (mean);
                    plgp.setVariance (xdr.xdrDecodeDouble ());
                    int particlesCount = xdr.xdrDecodeInt ();

                    xdr.endDecoding   ();
                    xdr.close ();

                    // Buffer for reading particle values
                    buffer = new byte[particlesCount * 32];

                    // Read particle values
                    is.readFully (buffer, 0, particlesCount * 32);
                    xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    PlayerLocalizeParticle[] plps = new PlayerLocalizeParticle[particlesCount];
                    for (int i = 0; i < particlesCount; i++) {
                        PlayerLocalizeParticle plp = new PlayerLocalizeParticle ();
                        PlayerPose pp = new PlayerPose ();
                        pp.setPx (xdr.xdrDecodeDouble ());
                        pp.setPy (xdr.xdrDecodeDouble ());
                        pp.setPa (xdr.xdrDecodeDouble ());

                        plp.setPose  (pp);
                        plp.setAlpha (xdr.xdrDecodeDouble ());

                        plps[i] = plp;
                    }
                    xdr.endDecoding   ();
                    xdr.close ();

                    plgp.setParticles_count (particlesCount);
                    plgp.setParticles       (plps);

                    readyPlgp = true;
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[Localize][Debug] : " +
                                "Unexpected response " + header.getSubtype () +
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException
                ("[Localize] : Error reading payload: " +
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException
                ("[Localize] : Error while XDR-decoding payload: " +
                        e.toString(), e);
        }
    }

    /**
     * Get the Localize data.
     * @return an object of type PlayerLocalizeData containing the requested data
     */
    public PlayerLocalizeData getData () { return this.pldata; }

    /**
     * Get the particle data.
     * @return an object of type PlayerLocalizeGetParticles containing the requested data
     */
    public PlayerLocalizeGetParticles getParticleData () { return this.plgp; }

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
     * Check if particle data is available.
     * @return true if ready, false if not ready
     */
    public boolean isParticleDataReady () {
        if (readyPlgp) {
            readyPlgp = false;
            return true;
        }
        return false;
    }
}
