/*
 *  Player Java Client 3 - PlayerSegment.java
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
package javaclient3.structures;

/**
 * A line segment, used to construct vector-based maps.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerSegment {
    private float x0;		// Endpoints [m]
    private float y0;		// Endpoints [m]
    private float x1;		// Endpoints [m]
    private float y1;		// Endpoints [m]
    
    /**
     * 
     * @return Endpoints [m]
     */
    public synchronized float getX0 () {
    	return this.x0;
    }
    
    /**
     * 
     * @param newX0 Endpoints [m] 
     */
    public synchronized void setX0 (float newX0) {
    	this.x0 = newX0;
    }
    
    /**
     * 
     * @return Endpoints [m]
     */
    public synchronized float getY0 () {
    	return this.y0;
    }
    
    /**
     * 
     * @param newY0 Endpoints [m] 
     */
    public synchronized void setY0 (float newY0) {
    	this.y0 = newY0;
    }

    /**
     * 
     * @return Endpoints [m]
     */
    public synchronized float getX1 () {
    	return this.x1;
    }
    
    /**
     * 
     * @param newX1 Endpoints [m] 
     */
    public synchronized void setX1 (float newX1) {
    	this.x1 = newX1;
    }
    
    /**
     * 
     * @return Endpoints [m]
     */
    public synchronized float getY1 () {
    	return this.y1;
    }
    
    /**
     * 
     * @param newY1 Endpoints [m] 
     */
    public synchronized void setY1 (float newY1) {
    	this.y1 = newY1;
    }
}