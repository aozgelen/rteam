/*
 *  Player Java Client 3 - PlayerLocalizeParticle.java
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

package javaclient3.structures.localize;

import javaclient3.structures.*;

/**
 * A particle on amcl algorithm
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerLocalizeParticle implements PlayerConstants {

    // The particle's pose (m,m,rad) 
    private PlayerPose pose;
    // The weight coefficient for linear combination (alpha) 
    private double alpha;


    /**
     * @return  The particle's pose (m,m,rad) 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  The particle's pose (m,m,rad) 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }
    
    /**
     * @return  The weight coefficient for linear combination (alpha) 
     **/
    public synchronized double getAlpha () {
        return this.alpha;
    }

    /**
     * @param newAlpha  The weight coefficient for linear combination (alpha) 
     *
     */
    public synchronized void setAlpha (double newAlpha) {
        this.alpha = newAlpha;
    }

}