/*
 *  Player Java Client 3 - PlayerDevice.java
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerDevAddr;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * Abstract class for all Player interfaces.
 * @author Radu Bogdan Rusu, Maxim Batalin, Esben Ostergaard
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public abstract class PlayerDevice implements PlayerConstants {

    private PlayerDevAddr deviceAddress;
    private byte          deviceAccess;
    private String        deviceDriverName;

    protected double        timestamp;
    
    // Logging support
    private Logger logger = Logger.getLogger (PlayerDevice.class.getName ());

    protected PlayerClient     pc;
    protected DataInputStream  is;
    protected DataOutputStream os;
    
    /**
     * Abstract constructor for each PlayerDevice.
     * @param plc a reference to the PlayerClient object
     */
    public PlayerDevice (PlayerClient plc) {
        pc = plc;
        is = pc.is;
        os = pc.os;
    }
    
    /**
     * Sends a Player message header filled with the given values.
     * @param type type of message (DATA, CMD, REQ, RESP_ACK, SYNCH, RESP_NACK)
     * @param subtype subtype of message
     * @param size size of the payload to follow
     */
    protected void sendHeader (int type, int subtype, int size) {
        try {
            Date d = new Date ();
            double timestamp = d.getTime () / 1000;
            
            XdrBufferEncodingStream xdr = 
                new XdrBufferEncodingStream (PlayerMsgHdr.PLAYERXDR_MSGHDR_SIZE);
            xdr.beginEncoding (null, 0);
            // see player.h / player_msghdr for additional explanations
            // The "host" on which the device resides
            xdr.xdrEncodeInt    (deviceAddress.getHost   ());
            // The "robot" or device collection in which the device resides
            xdr.xdrEncodeInt    (deviceAddress.getRobot  ());
            // The interface provided by the device; must be one of PLAYER_*_CODE
            xdr.xdrEncodeShort  (deviceAddress.getInterf ());
            // Which device of that interface
            xdr.xdrEncodeShort  (deviceAddress.getIndex  ());
            // Message type; must be one of PLAYER_MSGTYPE_*
            xdr.xdrEncodeByte   ((byte)type);
            // Message subtype; interface specific
            xdr.xdrEncodeByte   ((byte)subtype);
            // Time associated with message contents (seconds since epoch)
            xdr.xdrEncodeDouble (timestamp);
            // For keeping track of associated messages.
            xdr.xdrEncodeInt    (0);
            // Size in bytes of the payload to follow
            xdr.xdrEncodeInt    (size);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, PlayerMsgHdr.PLAYERXDR_MSGHDR_SIZE);
            xdr.close ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[PlayerDevice] : Error sending header: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[PlayerDevice] : Error while XDR-encoding header: " + 
                        e.toString(), e);
        }
    }

    /**
     * Read the data.
     */
    public synchronized void readData (PlayerMsgHdr header) { }
    
    /**
     * Abstract handleNARMessage method.
     */
    public void handleNARMessage () {
        logger.log (Level.FINEST, "[PlayerDevice] : Need to handle a NAR message.");
    }
        
    /**
     * Abstract handleResponse method.
     * @param header a PlayerMsgHdr structure containing the Player header
     */
    protected void handleResponse (PlayerMsgHdr header) {
        logger.log (Level.FINEST, "[PlayerDevice] : General handle response was triggered.");
    }

    /**
     * Set a new device address
     * @param newDevAddr new device address (player_devaddr)
     */
    public void setDeviceAddress (PlayerDevAddr newDevAddr) {
        this.deviceAddress = newDevAddr;
    }
  
    /**
     * Return the current device address
     * @return the current device address (player_devaddr)
     */
    public PlayerDevAddr getDeviceAddress () {
        return this.deviceAddress;
    }
    
    /**
     * Set a new device access
     * @param newDeviceAccess new device access
     */
    public void setDeviceAccess (byte newDeviceAccess) {
        this.deviceAccess = newDeviceAccess;
    }
    
    /**
     * Return the current device access code
     * @return the current device access code
     */
    public byte getDeviceAccess () {
        return this.deviceAccess;
    }
    
    /**
     * Set a new driver name for the device
     * @param newDeviceDriverName new driver name
     */
    public void setDeviceDriverName (String newDeviceDriverName) {
        this.deviceDriverName = newDeviceDriverName;
    }
    
    /**
     * Return the current driver name for the device
     * @return the current driver name for the device
     */
    public String getDeviceDriverName () {
        return this.deviceDriverName;
    }

    /** 
     * Return the current data timestamp
     * @return the current data timestamp
     */
    public double getTimestamp() {
      return timestamp;
    }
}
