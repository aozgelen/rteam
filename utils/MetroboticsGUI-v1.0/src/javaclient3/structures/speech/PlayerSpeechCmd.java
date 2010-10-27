/*
 *  Player Java Client 3 - PlayerSpeechCmd.java
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

package javaclient3.structures.speech;

import javaclient3.structures.*;

/**
 * Command: say a string (PLAYER_SPEECH_CMD_SAY)
 * The speech interface accepts a command that is a string to
 * be given to the speech synthesizer.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerSpeechCmd implements PlayerConstants {

    // Length of string 
    private int string_count;
    // The string to say 
    private char[] string = new char[PLAYER_SPEECH_MAX_STRING_LEN];


    /**
     * @return  Length of string 
     **/
    public synchronized int getString_count () {
        return this.string_count;
    }

    /**
     * @param newString_count  Length of string 
     *
     */
    public synchronized void setString_count (int newString_count) {
        this.string_count = newString_count;
    }
    /**
     * @return  The string to say 
     **/
    public synchronized char[] getString () {
        return this.string;
    }

    /**
     * @param newString  The string to say 
     *
     */
    public synchronized void setString (char[] newString) {
        this.string = newString;
    }

}