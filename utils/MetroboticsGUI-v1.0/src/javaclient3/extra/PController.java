/*
 *  Player Java Client 3 - PController.java
 *  Copyright (C) 2002-2006 Radu Bogdan Rusu, Maxim Batalin
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
package javaclient3.extra;

/**
 * Proportional controller implementation.
 * @author Radu Bogdan Rusu
 */
public class PController extends Controller {

    /** Proportional constant */
    protected double kp;

    /**
     * Constructor for PController.
     * @param Kp the proportional constant
     */
    public PController (double Kp) {
    	this.kp = Kp;
    }

    /**
     * Calculate and return the controller's command for the controlled system.
     * @param currentOutput the current output of the system
     * @return the new calculated command for the system
     */
    public double getCommand (double currentOutput) {
    	this.currE = this.goal - currentOutput;
        eSum += currE;

        lastE = currE;
        double Pgain = this.kp * currE;

        return Pgain;
    }

    /**
     * Get the current value of the proportional constant.
     * @return Kp as a double
     */
    public double getKp () {
    	return this.kp;
    }

    /**
     * Set a new value for the proportional constant.
     * @param newKp the new value for Kp
     */
    public void setKp (double newKp) {
    	this.kp = newKp;
    }
}
