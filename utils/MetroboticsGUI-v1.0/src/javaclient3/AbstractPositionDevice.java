/*
 *  Player Java Client 3 - AbstractPositionDevice.java
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
package javaclient3;

/**
 * Abstract class for all Player Position* interfaces. Used for
 * PositionControl and HeadingControl.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public abstract class AbstractPositionDevice extends PlayerDevice {

    /**
     * Abstract constructor for each AbstractPositionDevice.
     * @param plc a reference to the PlayerClient object
     */
    public AbstractPositionDevice (PlayerClient plc) { super (plc); }

    public abstract double getX     ();
    public abstract double getY     ();
    public abstract double getYaw   ();
    public abstract void  setSpeed (double speed, double turnrate);
}
