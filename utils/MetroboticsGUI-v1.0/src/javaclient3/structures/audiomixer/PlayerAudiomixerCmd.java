/*
 *  Player Java Client 3 - PlayerAudiomixerCmd.java
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

package javaclient3.structures.audiomixer;

import javaclient3.structures.*;

/**
 * Command: set level
 * The audiomixer interface accepts commands to set the left and right
 * volume levels of various channels. The channel is determined by the
 * subtype of the command: PLAYER_AUDIOMIXER_SET_MASTER for the master volume,
 * PLAYER_AUDIOMIXER_SET_PCM for the PCM volume, PLAYER_AUDIOMIXER_SET_LINE for
 * the line in volume, PLAYER_AUDIOMIXER_SET_MIC for the microphone volume,
 * PLAYER_AUDIOMIXER_SET_IGAIN for the input gain, and PLAYER_AUDIOMIXER_SET_OGAIN
 * for the output gain.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAudiomixerCmd implements PlayerConstants {

    // Left level 
    private int left;
    // Right level 
    private int right;


    /**
     * @return  Left level 
     **/
    public synchronized int getLeft () {
        return this.left;
    }

    /**
     * @param newLeft  Left level 
     *
     */
    public synchronized void setLeft (int newLeft) {
        this.left = newLeft;
    }
    /**
     * @return  Right level 
     **/
    public synchronized int getRight () {
        return this.right;
    }

    /**
     * @param newRight  Right level 
     *
     */
    public synchronized void setRight (int newRight) {
        this.right = newRight;
    }

}