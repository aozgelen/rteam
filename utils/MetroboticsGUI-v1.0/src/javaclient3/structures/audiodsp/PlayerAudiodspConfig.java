/*
 *  Player Java Client 3 - PlayerAudiodspConfig.java
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

package javaclient3.structures.audiodsp;

import javaclient3.structures.*;

/**
 * Request/reply : Get/set audio properties.
 * Send a null PLAYER_AUDIODSP_GET_CONFIG request to receive the audiodsp
 * configuration.  Send a full PLAYER_AUDIODSP_SET_CONFIG request to modify
 * the configuration (and receive a null response).
 * The sample format is defined in sys/soundcard.h, and defines the byte
 * size and endian format for each sample.
 * The sample rate defines the Hertz at which to sample.
 * Mono or stereo sampling is defined in the channels parameter where
 * 1==mono and 2==stereo. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAudiodspConfig implements PlayerConstants {

    // Format with which to sample 
    private int format;
    // Sample rate [Hz] 
    private float frequency;
    // Number of channels to use. 1=mono, 2=stereo 
    private int channels;


    /**
     * @return  Format with which to sample 
     **/
    public synchronized int getFormat () {
        return this.format;
    }

    /**
     * @param newFormat  Format with which to sample 
     *
     */
    public synchronized void setFormat (int newFormat) {
        this.format = newFormat;
    }
    /**
     * @return  Sample rate [Hz] 
     **/
    public synchronized float getFrequency () {
        return this.frequency;
    }

    /**
     * @param newFrequency  Sample rate [Hz] 
     *
     */
    public synchronized void setFrequency (float newFrequency) {
        this.frequency = newFrequency;
    }
    /**
     * @return  Number of channels to use. 1=mono, 2=stereo 
     **/
    public synchronized int getChannels () {
        return this.channels;
    }

    /**
     * @param newChannels  Number of channels to use. 1=mono, 2=stereo 
     *
     */
    public synchronized void setChannels (int newChannels) {
        this.channels = newChannels;
    }

}