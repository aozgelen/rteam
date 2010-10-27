/*
 *  Player Java Client 3 - PlayerBlobfinderImagerConfig.java
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

package javaclient3.structures.blobfinder;

import javaclient3.structures.*;

/**
 * Configuration request: Set imager params.
 * Imaging sensors that do blob tracking generally have some sorts of
 * image quality parameters that you can tweak.  The following ones
 * are implemented here:
 *    - brightness  (0-255)
 *    - contrast    (0-255)
 *    - auto gain   (0=off, 1=on)
 *    - color mode  (0=RGB/AutoWhiteBalance Off,  1=RGB/AutoWhiteBalance On,
 *                 2=YCrCB/AWB Off, 3=YCrCb/AWB On)
 * To set the params, send a PLAYER_BLOBFINDER_REQ_SET_IMAGER_PARAMS request
 * with the format below.  Any values set to -1 will be left unchanged.
 * Null response.
 * 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBlobfinderImagerConfig implements PlayerConstants {

    // Brightness: (0-255)  -1=no change. 
    private int brightness;
    // Contrast: (0-255)  -1=no change. 
    private int contrast;
    // Color Mode
    //       ( 0=RGB/AutoWhiteBalance Off,  1=RGB/AutoWhiteBalance On,
    //       2=YCrCB/AWB Off, 3=YCrCb/AWB On)  -1=no change.
    private int colormode;
    // AutoGain:   0=off, 1=on.  -1=no change. 
    private int autogain;


    /**
     * @return  Brightness: (0-255)  -1=no change. 
     **/
    public synchronized int getBrightness () {
        return this.brightness;
    }

    /**
     * @param newBrightness  Brightness: (0-255)  -1=no change. 
     *
     */
    public synchronized void setBrightness (int newBrightness) {
        this.brightness = newBrightness;
    }
    /**
     * @return  Contrast: (0-255)  -1=no change. 
     **/
    public synchronized int getContrast () {
        return this.contrast;
    }

    /**
     * @param newContrast  Contrast: (0-255)  -1=no change. 
     *
     */
    public synchronized void setContrast (int newContrast) {
        this.contrast = newContrast;
    }
    /**
     * @return  Color Mode
     *       ( 0=RGB/AutoWhiteBalance Off,  1=RGB/AutoWhiteBalance On,
     *       2=YCrCB/AWB Off, 3=YCrCb/AWB On)  -1=no change.
     *   
     **/
    public synchronized int getColormode () {
        return this.colormode;
    }

    /**
     * @param newColormode  Color Mode
     *       ( 0=RGB/AutoWhiteBalance Off,  1=RGB/AutoWhiteBalance On,
     *       2=YCrCB/AWB Off, 3=YCrCb/AWB On)  -1=no change.
     *   
     *
     */
    public synchronized void setColormode (int newColormode) {
        this.colormode = newColormode;
    }
    /**
     * @return  AutoGain:   0=off, 1=on.  -1=no change. 
     **/
    public synchronized int getAutogain () {
        return this.autogain;
    }

    /**
     * @param newAutogain  AutoGain:   0=off, 1=on.  -1=no change. 
     *
     */
    public synchronized void setAutogain (int newAutogain) {
        this.autogain = newAutogain;
    }

}