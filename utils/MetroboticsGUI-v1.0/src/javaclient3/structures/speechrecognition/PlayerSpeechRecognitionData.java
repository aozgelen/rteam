/*
 *  Player Java Client 3 - PlayerSpeechRecognitionData.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
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

package javaclient3.structures.speechrecognition;

import javaclient3.structures.*;

/**
 * Data: recognized string (PLAYER_SPEECH_MAX_STRING_LEN)
 * The speech recognition data packet.  
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerSpeechRecognitionData implements PlayerConstants {

    // Length of text 
    private int text_count;
    // Recognized text 
    private char[] text = new char[SPEECH_RECOGNITION_TEXT_LEN];


    /**
     * @return  Length of text 
     **/
    public synchronized int getText_count () {
        return this.text_count;
    }

    /**
     * @param newText_count  Length of text 
     *
     */
    public synchronized void setText_count (int newText_count) {
        this.text_count = newText_count;
    }
    /**
     * @return  Recognized text 
     **/
    public synchronized char[] getText () {
        return this.text;
    }

    /**
     * @param newText  Recognized text 
     *
     */
    public synchronized void setText (char[] newText) {
        this.text = newText;
    }

}