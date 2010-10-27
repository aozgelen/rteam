/*
 *  Player Java Client 3 - PlayerBlobfinderData.java
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
 * Data: detected blobs (PLAYER_BLOBFINDER_DATA_BLOBS)
 * The list of detected blobs, returned as data by blobfinder devices. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBlobfinderData implements PlayerConstants {

    // The image dimensions. [pixels] 
    private int width;
    // The image dimensions. [pixels] 
    private int height;
    // The number of blobs 
    private int blobs_count;
    // The list of blobs 
    private PlayerBlobfinderBlob[] blobs = new PlayerBlobfinderBlob[PLAYER_BLOBFINDER_MAX_BLOBS];


    /**
     * @return  The image dimensions. [pixels] 
     **/
    public synchronized int getWidth () {
        return this.width;
    }

    /**
     * @param newWidth  The image dimensions. [pixels] 
     *
     */
    public synchronized void setWidth (int newWidth) {
        this.width = newWidth;
    }
    /**
     * @return  The image dimensions. [pixels] 
     **/
    public synchronized int getHeight () {
        return this.height;
    }

    /**
     * @param newHeight  The image dimensions. [pixels] 
     *
     */
    public synchronized void setHeight (int newHeight) {
        this.height = newHeight;
    }
    /**
     * @return  The number of blobs 
     **/
    public synchronized int getBlobs_count () {
        return this.blobs_count;
    }

    /**
     * @param newBlobs_count  The number of blobs 
     *
     */
    public synchronized void setBlobs_count (int newBlobs_count) {
        this.blobs_count = newBlobs_count;
    }
    /**
     * @return  The list of blobs 
     **/
    public synchronized PlayerBlobfinderBlob[] getBlobs () {
        return this.blobs;
    }

    /**
     * @param newBlobs  The list of blobs 
     *
     */
    public synchronized void setBlobs (PlayerBlobfinderBlob[] newBlobs) {
        this.blobs = newBlobs;
    }

}