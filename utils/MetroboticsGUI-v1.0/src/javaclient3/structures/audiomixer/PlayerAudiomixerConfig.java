/*
 *  Player Java Client 3 - PlayerAudiomixerConfig.java
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
 * Request/reply: Get levels
 * Send a null PLAYER_AUDIOMIXER_GET_LEVELS request to receive the
 * current state of the mixer levels.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerAudiomixerConfig implements PlayerConstants {

    // Levels 
    private int master_left;
    // Levels 
    private int master_right;
    // Levels 
    private int pcm_left;
    // Levels 
    private int pcm_right;
    // Levels 
    private int line_left;
    // Levels 
    private int line_right;
    // Levels 
    private int mic_left;
    // Levels 
    private int mic_right;
    // Levels 
    private int i_gain;
    // Levels 
    private int o_gain;


    /**
     * @return  Levels 
     **/
    public synchronized int getMaster_left () {
        return this.master_left;
    }

    /**
     * @param newMaster_left  Levels 
     *
     */
    public synchronized void setMaster_left (int newMaster_left) {
        this.master_left = newMaster_left;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getMaster_right () {
        return this.master_right;
    }

    /**
     * @param newMaster_right  Levels 
     *
     */
    public synchronized void setMaster_right (int newMaster_right) {
        this.master_right = newMaster_right;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getPcm_left () {
        return this.pcm_left;
    }

    /**
     * @param newPcm_left  Levels 
     *
     */
    public synchronized void setPcm_left (int newPcm_left) {
        this.pcm_left = newPcm_left;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getPcm_right () {
        return this.pcm_right;
    }

    /**
     * @param newPcm_right  Levels 
     *
     */
    public synchronized void setPcm_right (int newPcm_right) {
        this.pcm_right = newPcm_right;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getLine_left () {
        return this.line_left;
    }

    /**
     * @param newLine_left  Levels 
     *
     */
    public synchronized void setLine_left (int newLine_left) {
        this.line_left = newLine_left;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getLine_right () {
        return this.line_right;
    }

    /**
     * @param newLine_right  Levels 
     *
     */
    public synchronized void setLine_right (int newLine_right) {
        this.line_right = newLine_right;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getMic_left () {
        return this.mic_left;
    }

    /**
     * @param newMic_left  Levels 
     *
     */
    public synchronized void setMic_left (int newMic_left) {
        this.mic_left = newMic_left;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getMic_right () {
        return this.mic_right;
    }

    /**
     * @param newMic_right  Levels 
     *
     */
    public synchronized void setMic_right (int newMic_right) {
        this.mic_right = newMic_right;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getI_gain () {
        return this.i_gain;
    }

    /**
     * @param newI_gain  Levels 
     *
     */
    public synchronized void setI_gain (int newI_gain) {
        this.i_gain = newI_gain;
    }
    /**
     * @return  Levels 
     **/
    public synchronized int getO_gain () {
        return this.o_gain;
    }

    /**
     * @param newO_gain  Levels 
     *
     */
    public synchronized void setO_gain (int newO_gain) {
        this.o_gain = newO_gain;
    }

}