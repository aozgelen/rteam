/*
 *  Player Java Client 3 - AudioMixerInterface.java
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
import javaclient3.structures.audiomixer.PlayerAudiomixerCmd;
import javaclient3.structures.audiomixer.PlayerAudiomixerConfig;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The audiomixer interface is used to control sound levels.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class AudioMixerInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;
    
    // Logging support
    private Logger logger = Logger.getLogger (AudioMixerInterface.class.getName ());

    private PlayerAudiomixerConfig paconfig;
    private boolean                readyPaconfig = false;

    /**
     * Constructor for AudioMixerInterface.
     * @param pc a reference to the PlayerClient object
     */
    public AudioMixerInterface (PlayerClient pc) { super(pc); }

    /**
     * The audiomixer interface accepts commands to set the left and right 
     * volume levels of various channels. The channel may be 
     * PLAYER_AUDIOMIXER_MASTER for the master volume, PLAYER_AUDIOMIXER_PCM 
     * for the PCM volume, PLAYER_AUDIOMIXER_LINE for the line in volume, 
     * PLAYER_AUDIOMIXER_MIC for the microphone volume, PLAYER_AUDIOMIXER_IGAIN 
     * for the input gain, and PLAYER_AUDIOMIXER_OGAIN for the output gain.
     * <br><br>
     * See the player_audiomixer_cmd structure from player.h
     * @param subtype one of the types above
     * @param pacmd a PlayerAudiomixerCmd structure containing data
     */
    public void setVolume (int subtype, PlayerAudiomixerCmd pacmd) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, subtype, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (pacmd.getLeft  ());     // left level
            xdr.xdrEncodeInt (pacmd.getRight ());     // right level
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[AudioMixer] : Couldn't send command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[AudioMixer] : Error while XDR-encoding command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Configuration request: Get levels.
     * <br><br>
     * Send a null PLAYER_AUDIOMIXER_GET_LEVELS request to receive the current 
     * state of the mixer levels.
     * <br><br>
     * See the player_audiomixer_config structure from player.h
     */
    public void getLevels (byte subtype) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_AUDIOMIXER_GET_LEVELS, 0);
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[AudioMixer] : Couldn't request PLAYER_AUDIOMIXER_GET_LEVELS: " + 
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
                case PLAYER_AUDIOMIXER_GET_LEVELS: {
                    paconfig = new PlayerAudiomixerConfig ();
                    
                    // Buffer for reading configuration data
                    byte[] buffer = new byte[40];
                    // Read configuration data
                    is.readFully (buffer, 0, 40);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    paconfig.setMaster_left  (xdr.xdrDecodeInt ());
                    paconfig.setMaster_right (xdr.xdrDecodeInt ());
                    paconfig.setPcm_left     (xdr.xdrDecodeInt ());
                    paconfig.setPcm_right    (xdr.xdrDecodeInt ());
                    paconfig.setLine_left    (xdr.xdrDecodeInt ());
                    paconfig.setLine_right   (xdr.xdrDecodeInt ());
                    paconfig.setMic_left     (xdr.xdrDecodeInt ());
                    paconfig.setMic_right    (xdr.xdrDecodeInt ());
                    paconfig.setI_gain       (xdr.xdrDecodeInt ());
                    paconfig.setO_gain       (xdr.xdrDecodeInt ());
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPaconfig = true;
                    break;
                }
                default:{
                    if (isDebugging)
                        logger.log (Level.FINEST, "[AudioMixer][Debug] : " +
                                "Unexpected response " + header.getSubtype () + 
                                " of size = " + header.getSize ());
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[AudioMixer] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[AudioMixer] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }

    /**
     * Get the configuration data.
     * @return an object of type PlayerAudiomixerConfig containing the requested data 
     */
    public PlayerAudiomixerConfig getConfig () { return this.paconfig; }
    
    /**
     * Check if configuration data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isConfigReady () {
        if (readyPaconfig) {
            readyPaconfig = false;
            return true;
        }
        return false;
    }
    
}
