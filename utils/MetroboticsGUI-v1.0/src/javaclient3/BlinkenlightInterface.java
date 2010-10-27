/*
 *  Player Java Client 3 - BlinkenlightInterface.java
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


import javaclient3.structures.PlayerColor;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.blinkenlight.PlayerBlinkenlightData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The blinkenlight interface is used to switch on and off a flashing indicator 
 * light, and to set it's flash period.
 * <br><br>
 * This interface accepts no configuration requests.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class BlinkenlightInterface extends PlayerDevice {

    private PlayerBlinkenlightData pbdata;
    private boolean                  readyPbdata = false;
    
    /**
     * Constructor for BlinkenlightInterface.
      * @param pc a reference to the PlayerClient object
    */
    public BlinkenlightInterface (PlayerClient pc) { super(pc); }
    
    /**
     * The blinkenlight data provides the current state of the indicator light.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_BLINKENLIGHT_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    pbdata = new PlayerBlinkenlightData ();
                    
                    // Buffer for reading status, period, dutycycle, color
                    byte[] buffer = new byte[12+16];
                    // Read status, period, dutycycle, color
                    is.readFully (buffer, 0, 12+16);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    
                    pbdata.setEnable    (xdr.xdrDecodeByte  ());    // status
                    pbdata.setPeriod    (xdr.xdrDecodeFloat ());    // flash period
                    pbdata.setDutycycle (xdr.xdrDecodeFloat ());    // flash dutycycle
                    PlayerColor pcolor = new PlayerColor ();
                    pcolor.setAlpha (xdr.xdrDecodeByte ());
                    pcolor.setRed   (xdr.xdrDecodeByte ());
                    pcolor.setGreen (xdr.xdrDecodeByte ());
                    pcolor.setBlue  (xdr.xdrDecodeByte ());
                    pbdata.setColor (pcolor);
                    
                    xdr.endDecoding   ();
                    xdr.close ();
                    readyPbdata = true;
                    
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Returns the Blinkenlight data (status, flash period)
     * @return the Blinkenlight data
     */
    public synchronized PlayerBlinkenlightData getData () { return pbdata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPbdata) {
            readyPbdata = false;
            return true;
        }
        return false;
    }

    
    /**
     * Set the blinkenlight state.
     * @param pbdata a PlayerBlinkenlightData structure filled with the 
     *        required data
     */
    public void setState (PlayerBlinkenlightData pbdata) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_BLINKENLIGHT_CMD_STATE, 12+16);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (12+16);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)pbdata.getEnable ());
            xdr.xdrEncodeFloat (pbdata.getPeriod    ());
            xdr.xdrEncodeFloat (pbdata.getDutycycle ());
            xdr.xdrEncodeByte  ((byte)pbdata.getColor ().getAlpha ());
            xdr.xdrEncodeByte  ((byte)pbdata.getColor ().getRed   ());
            xdr.xdrEncodeByte  ((byte)pbdata.getColor ().getGreen ());
            xdr.xdrEncodeByte  ((byte)pbdata.getColor ().getBlue  ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Couldn't send state command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-encoding state command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Command: power.
     * This command turns the light on or off.
     * @param state FALSE: off, TRUE: on
     */
    public void setPower (int state) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_BLINKENLIGHT_CMD_POWER, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)state);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Couldn't send power command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-encoding power command: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Command: color.
     * This command ets the color of the light.
     * @param color a PlayerColor structure filled with the required data
     */
    public void setColor (PlayerColor color) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_BLINKENLIGHT_CMD_COLOR, 16);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (16);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)color.getAlpha ());
            xdr.xdrEncodeByte  ((byte)color.getRed   ());
            xdr.xdrEncodeByte  ((byte)color.getGreen ());
            xdr.xdrEncodeByte  ((byte)color.getBlue  ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Couldn't send color command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-encoding color command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Command: period.
     * This command sets the duration of one on/off blink cycle in seconds.
     * @param period flash period (duration of one whole on-off cycle) [s].
     */
    public void setPeriod (int period) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_BLINKENLIGHT_CMD_PERIOD, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)period);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Couldn't send period command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-encoding period command: " + 
                        e.toString(), e);
        }
    }

    /**
     * Command: dutycycle.
     * This command sets the ratio of light-on to light-off time in one on/off 
     * blink cycle.
     * @param dutycycle flash duty cycle (ratio of time-on to time-off in one 
     *           cycle).
     */
    public void setDutycycle (int dutycycle) {
        try {
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_BLINKENLIGHT_CMD_DUTYCYCLE, 4);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte  ((byte)dutycycle);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Couldn't send dutycycle command: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Blinkenlight] : Error while XDR-encoding dutycycle command: " + 
                        e.toString(), e);
        }
    }
}
