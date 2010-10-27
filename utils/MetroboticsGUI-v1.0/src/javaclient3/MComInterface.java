/*
 *  Player Java Client 3 - MCommInterface.java
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
import javaclient3.structures.mcom.PlayerMcomConfig;
import javaclient3.structures.mcom.PlayerMcomData;
import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferDecodingStream;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The mcom interface is designed for exchanging information between clients. 
 * A client sends a message of a given "type" and "channel". This device 
 * stores adds the message to that channel's stack. A second client can then 
 * request data of a given "type" and "channel". Push, Pop, Read, and Clear 
 * operations are defined, but their semantics can vary, based on the stack 
 * discipline of the underlying driver. For example, the lifomcom driver 
 * enforces a last-in-first-out stack.  
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class MComInterface extends PlayerDevice {

    private static final boolean isDebugging = PlayerClient.isDebugging;
    
    // Logging support
    private Logger logger = Logger.getLogger (MComInterface.class.getName ());

    private PlayerMcomData pmdata;
    private boolean        readyPmdata = false;

    /**
     * Constructor for MComInterface.
     * @param pc a reference to the PlayerClient object
     */
    public MComInterface (PlayerClient pc) { super(pc); }
    
    /**
     * Read the MCOM data.
     */
    public synchronized void readData (PlayerMsgHdr header) {
        try {
            this.timestamp = header.getTimestamp();
               
            // Buffer for reading full, data_count, array_count
            byte[] buffer = new byte[12];
            // Read full, data_count, array_count
            is.readFully (buffer, 0, 12);
            
            pmdata = new PlayerMcomData (); 
            
            // Begin decoding the XDR buffer
            XdrBufferDecodingStream xdr = new XdrBufferDecodingStream (buffer);
            xdr.beginDecoding ();
            pmdata.setFull ((char)xdr.xdrDecodeByte ());
            int dataCount = xdr.xdrDecodeInt ();
            xdr.endDecoding   ();
            xdr.close ();
            
            // Buffer for reading the MCom data
            buffer = new byte[MCOM_DATA_LEN];
            // Read the MCom data
            is.readFully (buffer, 0, dataCount);
            pmdata.setData_count (dataCount);
            pmdata.setData       (new String (buffer).toCharArray ());
            
            // Take care of the residual zero bytes
            if ((dataCount % 4) != 0)
                is.readFully (buffer, 0, 4 - (dataCount % 4));
            
            readyPmdata = true;
        } catch (IOException e) {
            throw new PlayerException 
                ("[MCom] : Error reading payload: " + 
                        e.toString(), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[MCOM] : Error while XDR-decoding payload: " + 
                        e.toString(), e);
        }
    }
    
    /**
     * Configuration request to the device.
     * <br><br>
     * @param pmconfig a PlayerMcomConfig structure filled with the 
     *           required data
     * @param whichReq the appropriate request (PUSH, POP, READ, CLEAR, 
     *        SET_CAPACITY)
     */
    public void sendConfigReq (PlayerMcomConfig pmconfig, int whichReq) {
        try {
            int total = 12 + 4 + pmconfig.getChannel_count () + 8 + 4 + 
                pmconfig.getData ().getData_count ();
            sendHeader (PLAYER_MSGTYPE_REQ, whichReq, total);     /* payload */
            
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (24);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt (pmconfig.getCommand       ());
            xdr.xdrEncodeInt (pmconfig.getType          ());
            xdr.xdrEncodeInt (pmconfig.getChannel_count ());
            xdr.xdrEncodeByte((byte)pmconfig.getChannel_count ());
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            
            // Need to finish this after Player 2 gets updated in CVS!!!
            os.flush ();
        } catch (Exception e) {
            String subtype = "";
            switch (whichReq) {
                case PLAYER_MCOM_PUSH: {
                    subtype = "PLAYER_MCOM_PUSH";
                    break;
                }
                case PLAYER_MCOM_POP: {
                    subtype = "PLAYER_MCOM_POP";
                    break;
                }
                case PLAYER_MCOM_READ: {
                    subtype = "PLAYER_MCOM_READ";
                    break;
                }
                case PLAYER_MCOM_CLEAR: {
                    subtype = "PLAYER_MCOM_CLEAR";
                    break;
                }
                case PLAYER_MCOM_SET_CAPACITY: {
                    subtype = "PLAYER_MCOM_SET_CAPACITY";
                    break;
                }
                default: {
                    logger.log (Level.FINEST, "[MCom] : Couldn't send " + subtype +
                            " command: " + e.toString ());
                }
            }
        }
    }

    /**
     * Configuration request: Push (PLAYER_MCOM_PUSH_REQ)
     */
    public void Push (int type, String channel, char[] dataT) {
//        sendConfigReq (PLAYER_MCOM_PUSH, type, channel, true, dataT);
    }

    /**
     * Configuration request: Pop (PLAYER_MCOM_POP_REQ)
     */
    public void Pop (int type, String channel) {
//        char[] dataT = new char[MCOM_DATA_LEN];
//        sendConfigReq (PLAYER_MCOM_POP, type, channel, false, dataT);
    }
    
    /**
     * Configuration request: Read (PLAYER_MCOM_READ_REQ)
     */
    public void Read (int type, String channel) {
//        char[] dataT = new char[MCOM_DATA_LEN];
//        sendConfigReq (PLAYER_MCOM_READ, type, channel, false, dataT);
    }

    /**
     * Configuration request: Clear (PLAYER_MCOM_CLEAR_REQ)
     */
    public void Clear (int type, String channel) {
//        char[] dataT = new char[MCOM_DATA_LEN];
//        sendConfigReq (PLAYER_MCOM_CLEAR, type, channel, false, dataT);
    }

    /**
     * Configuration request: Set capacity (PLAYER_MCOM_SET_CAPACITY_REQ)
     */
    public void setCapacity (int type, String channel, char capacity) {
        char[] dataT = new char[MCOM_DATA_LEN];
        dataT[0] = capacity;
//        sendConfigReq (PLAYER_MCOM_SET_CAPACITY, type, channel, false, dataT);
    }

    /**
     * Handle acknowledgement response messages (threaded mode).
     * @param size size of the payload
     */
    public void handleResponse (int size) {
        if (size == 0) {
            if (isDebugging)
                System.err.println ("[MCom][Debug] : Unexpected response of size 0!");
            return;
        }
        try {
            /*byte subtype = is.readByte ();
            switch (subtype) {
                case PLAYER_MCOM_PUSH: {
                    break;
                }
                case PLAYER_MCOM_POP: {
                    //dataType = (short)is.readUnsignedShort ();
                    
                    //for (int i = 0; i < MCOM_CHANNEL_LEN; i++)
                    //    channelName[i] = is.readChar ();    // the name of the channel
                    
                    //fullF = is.readChar ();                 // a flag (boolean 0/1)
                    //for (int i = 0; i < MCOM_DATA_LEN; i++)
                    //    dataF[i] = is.readChar ();          // the data
                    
                    //readyData = true;
                    break;
                }
                case PLAYER_MCOM_READ: {
                    //dataType = (short)is.readUnsignedShort ();
                    
                    //for (int i = 0; i < MCOM_CHANNEL_LEN; i++)
                    //    channelName[i] = is.readChar ();    // the name of the channel
                    
                    //fullF = is.readChar ();                 // a flag (boolean 0/1)
                    //for (int i = 0; i < MCOM_DATA_LEN; i++)
                    //    dataF[i] = is.readChar ();          // the data
                    
                    //readyData = true;
                    break;
                }
                case PLAYER_MCOM_CLEAR: {
                    break;
                }
                case PLAYER_MCOM_SET_CAPACITY: {
                    break;
                }
                default: {
                    System.err.println ("[MCom] : Unexpected response " + subtype + 
                            " of size = " + size);
                    break;
                }
            }*/
        } catch (Exception e) {
            logger.log (Level.FINEST, "[MCom] : Error when reading payload " + e.toString ());
        }
    }
   
    /**
     * Get the MCom data.
     * @return an object of type PlayerMcomData containing the requested data 
     */
    public PlayerMcomData getData () { return this.pmdata; }
    
    /**
     * Check if data is available.
     * @return true if ready, false if not ready 
     */
    public boolean isDataReady () {
        if (readyPmdata) {
            readyPmdata = false;
            return true;
        }
        return false;
    }
}
