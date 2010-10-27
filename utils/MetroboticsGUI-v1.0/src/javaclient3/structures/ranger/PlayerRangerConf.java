/*
 *  Player Java Client 3 - PlayerRangerPowerConfig.java
 *  Copyright (C) 2010 Jorge Santos Simon
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
 * $Id: PlayerRangerPowerConfig.java 34 2006-02-15 17:51:14Z veedee $
 *
 */

package javaclient3.structures.ranger;

import javaclient3.structures.*;

/**
 * Request/reply: Ranger configuration.
 * Ranger device configuration parameters.
 * PLAYER_RANGER_REQ_GET_CONFIG request obtains the current configuration,
 * while PLAYER_RANGER_REQ_SET_CONFIG (not yet implemented) overwrites it.
 * @author Jorge Santos Simon
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerRangerConf implements PlayerConstants {

    private double min_angle  = Double.NaN;  // Start angle of scans [rad]; may be unfilled
    private double max_angle  = Double.NaN;  // End angle of scans [rad]; may be unfilled
    private double resolution = Double.NaN;  // Scan resolution [rad]; may be unfilled
    private double min_range  = Double.NaN;  // Minimum range [m]; may be unfilled
    private double max_range  = Double.NaN;  // Maximum range [m]; may be unfilled
    private double range_res  = Double.NaN;  // Range resolution [m]; may be unfilled
    private double frequency  = Double.NaN;  // Scanning frequency [Hz]; may be unfilled


    /**
     * @return Start angle of scans [rad].
     */
    public double getMin_angle() {
        return this.min_angle;
    }

    /**
     * @param minAngle Start angle of scans [rad].
     */
    public void setMin_angle(double minAngle) {
        this.min_angle = minAngle;
    }

    /**
     * @return End angle of scans [rad].
     */
    public double getMax_angle() {
        return this.max_angle;
    }

    /**
     * @param maxAngle End angle of scans [rad].
     */
    public void setMax_angle(double maxAngle) {
        this.max_angle = maxAngle;
    }

    /**
     * @return Minimum range [m]
     */
    public double getMin_range() {
        return min_range;
    }

    /**
     * @param minRange Minimum range [m]
     */
    public void setMin_range(double minRange) {
        min_range = minRange;
    }

    /**
     * @return Maximum range [m].
     */
    public double getMax_range() {
        return this.max_range;
    }

    /**
     * @param maxRange Maximum range [m].
     */
    public void setMax_range(double maxRange) {
        this.max_range = maxRange;
    }

    /**
     * @return Scanning frequency [Hz].
     */
    public double getFrequency() {
        return this.frequency;
    }

    /**
     * @param frequency Scanning frequency [Hz].
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /**
     * @return Range resolution [m].
     */
    public double getRange_res() {
        return this.range_res;
    }

    /**
     * @param rangeRes Range resolution [m].
     */
    public void setRange_res(double rangeRes) {
        this.range_res = rangeRes;
    }

    /**
     * @return Scan resolution [rad].
     */
    public double getResolution() {
        return this.resolution;
    }

    /**
     * @param resolution Scan resolution [rad].
     */
    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

}