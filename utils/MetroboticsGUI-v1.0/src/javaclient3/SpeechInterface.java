/*
 *  Player Java Client 3 - SpeechInterface.java
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

import javaclient3.xdr.OncRpcException;
import javaclient3.xdr.XdrBufferEncodingStream;

/**
 * The speech interface provides access to a speech synthesis system.
 * @author Radu Bogdan Rusu, Maxim Batalin 
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class SpeechInterface extends PlayerDevice {

    /**
     * Constructor for SpeechInterface.
     * @param pc a reference to the PlayerClient object
     */
    public SpeechInterface (PlayerClient pc) { super (pc); }
 
    /**
     * Command: say a string (PLAYER_SPEECH_CMD_SAY)
     * <br><br>
     * The speech interface accepts a command that is a string to be given to 
     * the speech synthesizer. 
     * @param text the string to say 
     */
    public void speech (String text) {
        String temp = text;
        if (text.length () > PLAYER_SPEECH_MAX_STRING_LEN)
            temp = text.substring (0, PLAYER_SPEECH_MAX_STRING_LEN);
        try {
            int leftOvers = 0;
            // Take care of the residual zero bytes
            if ((temp.length () % 4) != 0)
                leftOvers = 4 - (temp.length () % 4);
            int size = 4 + 4 + temp.length () + leftOvers;
            
            sendHeader (PLAYER_MSGTYPE_CMD, PLAYER_SPEECH_CMD_SAY, size);
            XdrBufferEncodingStream xdr = new XdrBufferEncodingStream (size+4);
            xdr.beginEncoding (null, 0);
            xdr.xdrEncodeInt  (size);
            xdr.xdrEncodeString(text);
            xdr.endEncoding ();
            os.write (xdr.getXdrData (), 0, xdr.getXdrLength ());
            xdr.close ();
            os.flush ();
        } catch (IOException e) {
            throw new PlayerException 
                ("[Speech] : Couldn't send speech command request: " + 
                    e.toString (), e);
        } catch (OncRpcException e) {
            throw new PlayerException 
                ("[Speech] : Couldn't XDR-encode speech command request: " + 
                    e.toString (), e);
        }
    }
}
