/*
 *  Player Java Client 3 - PlayerLocalizeGetParticles.java
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
 * Request/reply: Get particles.
 * To get (usually a subset of) the current particle set (assuming
 * the underlying driver uses a particle filter), send a null
 * PLAYER_LOCALIZE_REQ_GET_PARTICLES request. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLocalizeGetParticles implements PlayerConstants {

    // The best (?) pose (mm, mm, arc-seconds). 
    private PlayerPose mean;
    // The variance of the best (?) pose (mm^2) 
    private double variance;
    // The number of particles included 
    private int particles_count;
    // The particles 
    private PlayerLocalizeParticle[] particles = new PlayerLocalizeParticle[PLAYER_LOCALIZE_PARTICLES_MAX];


    /**
     * @return  The best (?) pose (mm, mm, arc-seconds). 
     **/
    public synchronized PlayerPose getMean () {
        return this.mean;
    }

    /**
     * @param newMean  The best (?) pose (mm, mm, arc-seconds). 
     *
     */
    public synchronized void setMean (PlayerPose newMean) {
        this.mean = newMean;
    }
    /**
     * @return  The variance of the best (?) pose (mm^2) 
     **/
    public synchronized double getVariance () {
        return this.variance;
    }

    /**
     * @param newVariance  The variance of the best (?) pose (mm^2) 
     *
     */
    public synchronized void setVariance (double newVariance) {
        this.variance = newVariance;
    }
    /**
     * @return  The number of particles included 
     **/
    public synchronized int getParticles_count () {
        return this.particles_count;
    }

    /**
     * @param newParticles_count  The number of particles included 
     *
     */
    public synchronized void setParticles_count (int newParticles_count) {
        this.particles_count = newParticles_count;
    }
    /**
     * @return  The particles 
     **/
    public synchronized PlayerLocalizeParticle[] getParticles () {
        return this.particles;
    }

    /**
     * @param newParticles  The particles 
     *
     */
    public synchronized void setParticles (PlayerLocalizeParticle[] newParticles) {
        this.particles = newParticles;
    }

}