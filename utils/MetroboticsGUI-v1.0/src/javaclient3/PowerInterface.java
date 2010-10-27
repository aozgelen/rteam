/*
 *  Player Java Client 3 - PowerInterface.java
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
import javaclient3.structures.power.PlayerPowerData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The power interface provides access to a robot's power subsystem. 
 * Includes the functionality of the old Player energy device, which
 * is now deprecated.
 * @author Radu Bogdan Rusu, Maxim Batalin 
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PowerInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;

    // Logging support
    private Logger logger = Logger.getLogger (PowerInterface.class.getName ());

    private PlayerPowerData pwdata;
    private boolean         readyPwdata = false;
    
    /**
     * Constructor for PowerInterface.
     * @param pc a reference to the PlayerClient object
     */
    public PowerInterface (PlayerClient pc) { super (pc); }

    /**
     * Read the battery voltage value.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            switch (header.getSubtype ()) {
                case PLAYER_POWER_DATA_STATE: {
                    this.timestamp = header.getTimestamp();
               
                    pwdata = new PlayerPowerData ();
                    
                    // Buffer for reading status, voltage, charge, energy, joules, watts, charging
                    byte[] buffer = new byte[24];
                    // Read status, voltage, charge, energy, joules, watts, charging
                    is.readFully (buffer, 0, 24);
                    
                    // Begin decoding the XDR buffer
                    XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
                    xdr.beginDecoding ();
                    pwdata.setValid   (xdr.xdrDecodeInt   ());    // status bits
                    pwdata.setVolts   (xdr.xdrDecodeFloat ());    // battery voltage [V]
                    pwdata.setPercent (xdr.xdrDecodeFloat ());    // percent of full charge [%]
                    pwdata.setJoules  (xdr.xdrDecodeFloat ());    // energy stored [J]
                    pwdata.setWatts   (xdr.xdrDecodeFloat ());    // current energy consumption [W]
                    pwdata.setCharging(xdr.xdrDecodeInt   ());    // charge exchange status
                    xdr.endDecoding   ();
                    xdr.close ();
                    
                    readyPwdata = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new PlayerException 
                ("[Power] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Power] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Returns the power data (status, voltage, charge, energy, joules, watts, charging)
     * @return the power data
     */
    public synchronized PlayerPowerData getData () { return pwdata; }
    
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
     * Request a change of charging policy.
     * @param enable_input if zero, recharging is disabled
     * @param enable_output if zero, charging others is disabled
     */
    public void requestCharge (int enable_input, int enable_output) {
        try {
            sendHeader (PLAYER_MSGTYPE_REQ, PLAYER_POWER_SET_CHARGING_POLICY_REQ, 8);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (8);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeByte ((byte)enable_input);
            xdr.xdrEncodeByte ((byte)enable_output);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Power] : Couldn't request PLAYER_MAIN_POWER_REQ: "
                        + e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Power] : Error while XDR-encoding POWER request: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Handle acknowledgement response messages.
     * @param header Player header
     */
    protected void handleResponse (PlayerMsgHdr header) {
        switch (header.getSubtype ()) {
            case PLAYER_POWER_SET_CHARGING_POLICY_REQ: {
                break;
            }
            default:{
                if (isDebugging)
                    logger.log (Level.FINEST, "[Power][Debug] : " +
                            "Unexpected response " + header.getSubtype () + 
                            " of size = " + header.getSize ());
                break;
            }
        }
    }

}
