/*
 *  Player Java Client 3 - PlayerGpsData.java
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

package javaclient3.structures.gps;

import javaclient3.structures.*;

/**
 * Data: state (PLAYER_GPS_DATA_STATE)
 * The gps interface gives current global position and heading information.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerGpsData implements PlayerConstants {

    // GPS (UTC) time, in seconds and microseconds since the epoch. 
    private int time_sec;
    // GPS (UTC) time, in seconds and microseconds since the epoch. 
    private int time_usec;
    // Latitude in degrees / 1e7 (units are scaled such that the
//       effective resolution is roughly 1cm).  Positive is north of
//       equator, negative is south of equator. 
    private int latitude;
    // Longitude in degrees / 1e7 (units are scaled such that the
//       effective resolution is roughly 1cm).  Positive is east of prime
//       meridian, negative is west of prime meridian. 
    private int longitude;
    // Altitude, in millimeters.  Positive is above reference (e.g.,
//       sea-level), and negative is below. 
    private int altitude;
    // UTM WGS84 coordinates, easting [m] 
    private double utm_e;
    // UTM WGS84 coordinates, northing [m] 
    private double utm_n;
    // Quality of fix 0 = invalid, 1 = GPS fix, 2 = DGPS fix 
    private int quality;
    // Number of satellites in view. 
    private int num_sats;
    // Horizontal dilution of position (HDOP), times 10 
    private int hdop;
    // Vertical dilution of position (VDOP), times 10 
    private int vdop;
    // Horizonal error [m] 
    private double err_horz;
    // Vertical error [m] 
    private double err_vert;


    /**
     * @return  GPS (UTC) time, in seconds and microseconds since the epoch. 
     **/
    public synchronized int getTime_sec () {
        return this.time_sec;
    }

    /**
     * @param newTime_sec  GPS (UTC) time, in seconds and microseconds since the epoch. 
     *
     */
    public synchronized void setTime_sec (int newTime_sec) {
        this.time_sec = newTime_sec;
    }
    /**
     * @return  GPS (UTC) time, in seconds and microseconds since the epoch. 
     **/
    public synchronized int getTime_usec () {
        return this.time_usec;
    }

    /**
     * @param newTime_usec  GPS (UTC) time, in seconds and microseconds since the epoch. 
     *
     */
    public synchronized void setTime_usec (int newTime_usec) {
        this.time_usec = newTime_usec;
    }
    /**
     * @return  Latitude in degrees / 1e7 (units are scaled such that the
     *       effective resolution is roughly 1cm).  Positive is north of
     *       equator, negative is south of equator. 
     **/
    public synchronized int getLatitude () {
        return this.latitude;
    }

    /**
     * @param newLatitude  Latitude in degrees / 1e7 (units are scaled such that the
     *       effective resolution is roughly 1cm).  Positive is north of
     *       equator, negative is south of equator. 
     *
     */
    public synchronized void setLatitude (int newLatitude) {
        this.latitude = newLatitude;
    }
    /**
     * @return  Longitude in degrees / 1e7 (units are scaled such that the
     *       effective resolution is roughly 1cm).  Positive is east of prime
     *       meridian, negative is west of prime meridian. 
     **/
    public synchronized int getLongitude () {
        return this.longitude;
    }

    /**
     * @param newLongitude  Longitude in degrees / 1e7 (units are scaled such that the
     *       effective resolution is roughly 1cm).  Positive is east of prime
     *       meridian, negative is west of prime meridian. 
     *
     */
    public synchronized void setLongitude (int newLongitude) {
        this.longitude = newLongitude;
    }
    /**
     * @return  Altitude, in millimeters.  Positive is above reference (e.g.,
     *       sea-level), and negative is below. 
     **/
    public synchronized int getAltitude () {
        return this.altitude;
    }

    /**
     * @param newAltitude  Altitude, in millimeters.  Positive is above reference (e.g.,
     *       sea-level), and negative is below. 
     *
     */
    public synchronized void setAltitude (int newAltitude) {
        this.altitude = newAltitude;
    }
    /**
     * @return  UTM WGS84 coordinates, easting [m] 
     **/
    public synchronized double getUtm_e () {
        return this.utm_e;
    }

    /**
     * @param newUtm_e  UTM WGS84 coordinates, easting [m] 
     *
     */
    public synchronized void setUtm_e (double newUtm_e) {
        this.utm_e = newUtm_e;
    }
    /**
     * @return  UTM WGS84 coordinates, northing [m] 
     **/
    public synchronized double getUtm_n () {
        return this.utm_n;
    }

    /**
     * @param newUtm_n  UTM WGS84 coordinates, northing [m] 
     *
     */
    public synchronized void setUtm_n (double newUtm_n) {
        this.utm_n = newUtm_n;
    }
    /**
     * @return  Quality of fix 0 = invalid, 1 = GPS fix, 2 = DGPS fix 
     **/
    public synchronized int getQuality () {
        return this.quality;
    }

    /**
     * @param newQuality  Quality of fix 0 = invalid, 1 = GPS fix, 2 = DGPS fix 
     *
     */
    public synchronized void setQuality (int newQuality) {
        this.quality = newQuality;
    }
    /**
     * @return  Number of satellites in view. 
     **/
    public synchronized int getNum_sats () {
        return this.num_sats;
    }

    /**
     * @param newNum_sats  Number of satellites in view. 
     *
     */
    public synchronized void setNum_sats (int newNum_sats) {
        this.num_sats = newNum_sats;
    }
    /**
     * @return  Horizontal dilution of position (HDOP), times 10 
     **/
    public synchronized int getHdop () {
        return this.hdop;
    }

    /**
     * @param newHdop  Horizontal dilution of position (HDOP), times 10 
     *
     */
    public synchronized void setHdop (int newHdop) {
        this.hdop = newHdop;
    }
    /**
     * @return  Vertical dilution of position (VDOP), times 10 
     **/
    public synchronized int getVdop () {
        return this.vdop;
    }

    /**
     * @param newVdop  Vertical dilution of position (VDOP), times 10 
     *
     */
    public synchronized void setVdop (int newVdop) {
        this.vdop = newVdop;
    }
    /**
     * @return  Horizonal error [m] 
     **/
    public synchronized double getErr_horz () {
        return this.err_horz;
    }

    /**
     * @param newErr_horz  Horizonal error [m] 
     *
     */
    public synchronized void setErr_horz (double newErr_horz) {
        this.err_horz = newErr_horz;
    }
    /**
     * @return  Vertical error [m] 
     **/
    public synchronized double getErr_vert () {
        return this.err_vert;
    }

    /**
     * @param newErr_vert  Vertical error [m] 
     *
     */
    public synchronized void setErr_vert (double newErr_vert) {
        this.err_vert = newErr_vert;
    }

}