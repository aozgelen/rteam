/*
 *  Player Java Client 3 - PlayerAudiodspData.java
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
 * Data: detected tones (PLAYER_AUDIODSP_DATA_TONES)
 * The audiodsp interface reads the audio stream from /dev/dsp (which
 * is assumed to be associated with a sound card connected to a microphone)
 * and performs some analysis on it.  PLAYER_AUDIODSP_MAX_FREQS number of
 * frequency/amplitude pairs are then returned as data. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAudiodspData implements PlayerConstants {

    // Number of frequencies 
    private int frequency_count;
    // [Hz] 
    private float[] frequency = new float[PLAYER_AUDIODSP_MAX_FREQS];
    // Number of amplitudes 
    private int amplitude_count;
    // [Db] 
    private float[] amplitude = new float[PLAYER_AUDIODSP_MAX_FREQS];


    /**
     * @return  Number of frequencies 
     **/
    public synchronized int getFrequency_count () {
        return this.frequency_count;
    }

    /**
     * @param newFrequency_count  Number of frequencies 
     *
     */
    public synchronized void setFrequency_count (int newFrequency_count) {
        this.frequency_count = newFrequency_count;
    }
    /**
     * @return  [Hz] 
     **/
    public synchronized float[] getFrequency () {
        return this.frequency;
    }

    /**
     * @param newFrequency  [Hz] 
     *
     */
    public synchronized void setFrequency (float[] newFrequency) {
        this.frequency = newFrequency;
    }
    /**
     * @return  Number of amplitudes 
     **/
    public synchronized int getAmplitude_count () {
        return this.amplitude_count;
    }

    /**
     * @param newAmplitude_count  Number of amplitudes 
     *
     */
    public synchronized void setAmplitude_count (int newAmplitude_count) {
        this.amplitude_count = newAmplitude_count;
    }
    /**
     * @return  [Db] 
     **/
    public synchronized float[] getAmplitude () {
        return this.amplitude;
    }

    /**
     * @param newAmplitude  [Db] 
     *
     */
    public synchronized void setAmplitude (float[] newAmplitude) {
        this.amplitude = newAmplitude;
    }

}