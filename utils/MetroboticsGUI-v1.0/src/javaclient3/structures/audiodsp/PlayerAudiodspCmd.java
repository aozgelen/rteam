/*
 *  Player Java Client 3 - PlayerAudiodspCmd.java
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
 * Command: tone / chirp to play
 * The audiodsp interface accepts commands to produce fixed-frequency
 * tones or binary phase shift keyed(BPSK) chirps through /dev/dsp
 * (which is assumed to be associated with a sound card to which a speaker is
 * attached). The command subtype, which should be PLAYER_AUDIODSP_PLAY_TONE,
 * PLAYER_AUDIODSP_PLAY_CHIRP, or PLAYER_AUDIODSP_REPLAY, determines what
 * to do.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAudiodspCmd implements PlayerConstants {

    // Frequency to play [Hz] 
    private float frequency;
    // Amplitude to play [dB] 
    private float amplitude;
    // Duration to play [s] 
    private float duration;
    // Length of bit string 
    private int bit_string_count;
    // BitString to encode in sine wave 
    private byte[] bit_string = new byte[PLAYER_AUDIODSP_MAX_BITSTRING_LEN];
    // Length of the bit string 
    private int bit_string_len;


    /**
     * @return  Frequency to play [Hz] 
     **/
    public synchronized float getFrequency () {
        return this.frequency;
    }

    /**
     * @param newFrequency  Frequency to play [Hz] 
     *
     */
    public synchronized void setFrequency (float newFrequency) {
        this.frequency = newFrequency;
    }
    /**
     * @return  Amplitude to play [dB] 
     **/
    public synchronized float getAmplitude () {
        return this.amplitude;
    }

    /**
     * @param newAmplitude  Amplitude to play [dB] 
     *
     */
    public synchronized void setAmplitude (float newAmplitude) {
        this.amplitude = newAmplitude;
    }
    /**
     * @return  Duration to play [s] 
     **/
    public synchronized float getDuration () {
        return this.duration;
    }

    /**
     * @param newDuration  Duration to play [s] 
     *
     */
    public synchronized void setDuration (float newDuration) {
        this.duration = newDuration;
    }
    /**
     * @return  Length of bit string 
     **/
    public synchronized int getBit_string_count () {
        return this.bit_string_count;
    }

    /**
     * @param newBit_string_count  Length of bit string 
     *
     */
    public synchronized void setBit_string_count (int newBit_string_count) {
        this.bit_string_count = newBit_string_count;
    }
    /**
     * @return  BitString to encode in sine wave 
     **/
    public synchronized byte[] getBit_string () {
        return this.bit_string;
    }

    /**
     * @param newBit_string  BitString to encode in sine wave 
     *
     */
    public synchronized void setBit_string (byte[] newBit_string) {
        this.bit_string = newBit_string;
    }
    /**
     * @return  Length of the bit string 
     **/
    public synchronized int getBit_string_len () {
        return this.bit_string_len;
    }

    /**
     * @param newBit_string_len  Length of the bit string 
     *
     */
    public synchronized void setBit_string_len (int newBit_string_len) {
        this.bit_string_len = newBit_string_len;
    }

}