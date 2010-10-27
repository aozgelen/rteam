/*
 *  Player Java Client 3 - PlayerBlobfinderBlob.java
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
 * Structure describing a single blob. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerBlobfinderBlob implements PlayerConstants {

    // Blob id. 
    private int id;
    // A descriptive color for the blob (useful for gui's).  The color
    // is stored as packed 32-bit RGB, i.e., 0x00RRGGBB. 
    private int color;
    // The blob area [pixels]. 
    private int area;
    // The blob centroid [pixels]. 
    private int x;
    // The blob centroid [pixels]. 
    private int y;
    // Bounding box for the blob [pixels]. 
    private int left;
    // Bounding box for the blob [pixels]. 
    private int right;
    // Bounding box for the blob [pixels]. 
    private int top;
    // Bounding box for the blob [pixels]. 
    private int bottom;
    // Range to the blob center [meters] 
    private float range;


    /**
     * @return  Blob id. 
     **/
    public synchronized int getId () {
        return this.id;
    }

    /**
     * @param newId  Blob id. 
     *
     */
    public synchronized void setId (int newId) {
        this.id = newId;
    }
    /**
     * @return  A descriptive color for the blob (useful for gui's).  The color
     *       is stored as packed 32-bit RGB, i.e., 0x00RRGGBB. 
     **/
    public synchronized int getColor () {
        return this.color;
    }

    /**
     * @param newColor  A descriptive color for the blob (useful for gui's).  The color
     *       is stored as packed 32-bit RGB, i.e., 0x00RRGGBB. 
     *
     */
    public synchronized void setColor (int newColor) {
        this.color = newColor;
    }
    /**
     * @return  The blob area [pixels]. 
     **/
    public synchronized int getArea () {
        return this.area;
    }

    /**
     * @param newArea  The blob area [pixels]. 
     *
     */
    public synchronized void setArea (int newArea) {
        this.area = newArea;
    }
    /**
     * @return  The blob centroid [pixels]. 
     **/
    public synchronized int getX () {
        return this.x;
    }

    /**
     * @param newX  The blob centroid [pixels]. 
     *
     */
    public synchronized void setX (int newX) {
        this.x = newX;
    }
    /**
     * @return  The blob centroid [pixels]. 
     **/
    public synchronized int getY () {
        return this.y;
    }

    /**
     * @param newY  The blob centroid [pixels]. 
     *
     */
    public synchronized void setY (int newY) {
        this.y = newY;
    }
    /**
     * @return  Bounding box for the blob [pixels]. 
     **/
    public synchronized int getLeft () {
        return this.left;
    }

    /**
     * @param newLeft  Bounding box for the blob [pixels]. 
     *
     */
    public synchronized void setLeft (int newLeft) {
        this.left = newLeft;
    }
    /**
     * @return  Bounding box for the blob [pixels]. 
     **/
    public synchronized int getRight () {
        return this.right;
    }

    /**
     * @param newRight  Bounding box for the blob [pixels]. 
     *
     */
    public synchronized void setRight (int newRight) {
        this.right = newRight;
    }
    /**
     * @return  Bounding box for the blob [pixels]. 
     **/
    public synchronized int getTop () {
        return this.top;
    }

    /**
     * @param newTop  Bounding box for the blob [pixels]. 
     *
     */
    public synchronized void setTop (int newTop) {
        this.top = newTop;
    }
    /**
     * @return  Bounding box for the blob [pixels]. 
     **/
    public synchronized int getBottom () {
        return this.bottom;
    }

    /**
     * @param newBottom  Bounding box for the blob [pixels]. 
     *
     */
    public synchronized void setBottom (int newBottom) {
        this.bottom = newBottom;
    }
    /**
     * @return  Range to the blob center [meters] 
     **/
    public synchronized float getRange () {
        return this.range;
    }

    /**
     * @param newRange  Range to the blob center [meters] 
     *
     */
    public synchronized void setRange (float newRange) {
        this.range = newRange;
    }

}