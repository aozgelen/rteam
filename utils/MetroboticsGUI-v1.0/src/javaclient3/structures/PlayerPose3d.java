/*
 *  Player Java Client 3 - PlayerPose3d.java
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
 * A pose in 3D space.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerPose3d {
    private double px;		// X [m]
    private double py;		// Y [m]
    private double pz;		// Z [m]
    private double proll;	// roll  [rad]
    private double ppitch;	// pitch [rad]
    private double pyaw;	// yaw   [rad]

    /**
     *
     * @return X [m]
     */
    public synchronized double getPx () {
    	return this.px;
    }

    /**
     *
     * @param newPx X [m]
     */
    public synchronized void setPx (double newPx) {
    	this.px = newPx;
    }

    /**
     *
     * @return Y [m]
     */
    public synchronized double getPy () {
        return this.py;
    }

    /**
     *
     * @param newPy Y [m]
     */
    public synchronized void setPy (double newPy) {
        this.py = newPy;
    }

    /**
     *
     * @return Z [m]
     */
    public synchronized double getPz () {
        return this.pz;
    }

    /**
     *
     * @param newPz Z [m]
     */
    public synchronized void setPz (double newPz) {
        this.pz = newPz;
    }

    /**
     *
     * @return roll [rad]
     */
    public synchronized double getProll () {
        return this.proll;
    }

    /**
     *
     * @param newProll roll [rad]
     */
    public synchronized void setProll (double newProll) {
        this.proll = newProll;
    }

    /**
     *
     * @return pitch [rad]
     */
    public synchronized double getPpitch () {
        return this.ppitch;
    }

    /**
     *
     * @param newPpitch pitch [rad]
     */
    public synchronized void setPpitch (double newPpitch) {
        this.ppitch = newPpitch;
    }

    /**
     *
     * @return yaw [rad]
     */
    public synchronized double getPyaw () {
        return this.pyaw;
    }

    /**
     *
     * @param newPyaw yaw [rad]
     */
    public synchronized void setPyaw (double newPyaw) {
        this.pyaw = newPyaw;
    }
}