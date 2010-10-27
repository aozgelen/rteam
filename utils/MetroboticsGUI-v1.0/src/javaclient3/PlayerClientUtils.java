/*
 *  Player Java Client 3 - PlayerClientUtils.java
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

import javaclient3.structures.PlayerConstants;

/**
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerClientUtils implements PlayerConstants {

    /**
     *
     * @param code subtype code
     * @return the subtype name as a String
     */
    public String lookupNameSubtype (int code) {
        switch (code) {
            case PLAYER_PLAYER_REQ_DEVLIST:
                return new String ("PLAYER_PLAYER_REQ_DEVLIST");
            case PLAYER_PLAYER_REQ_DRIVERINFO:
                return new String ("PLAYER_PLAYER_REQ_DRIVERINFO");
            case PLAYER_PLAYER_REQ_DEV:
                return new String ("PLAYER_PLAYER_REQ_DEV");
            case PLAYER_PLAYER_REQ_DATA:
                return new String ("PLAYER_PLAYER_REQ_DATA");
            case PLAYER_PLAYER_REQ_DATAMODE:
                return new String ("PLAYER_PLAYER_REQ_DATAMODE");
            case PLAYER_PLAYER_REQ_DATAFREQ:
                return new String ("PLAYER_PLAYER_REQ_DATAFREQ");
            case PLAYER_PLAYER_REQ_AUTH:
                return new String ("PLAYER_PLAYER_REQ_AUTH");
            case PLAYER_PLAYER_REQ_NAMESERVICE:
                return new String ("PLAYER_PLAYER_REQ_NAMESERVICE");
            case PLAYER_PLAYER_REQ_IDENT:
                return new String ("PLAYER_PLAYER_REQ_IDENT");
            case PLAYER_PLAYER_REQ_ADD_REPLACE_RULE:
                return new String ("PLAYER_PLAYER_REQ_ADD_REPLACE_RULE");
            default:
                return new String ("unknown");
        }
    }

    /**
     *
     * @param code code type
     * @return the type name as a String
     */
    public String lookupNameType (int code) {
        switch (code) {
            case PLAYER_MSGTYPE_DATA:
                return new String ("PLAYER_MSGTYPE_DATA");
            case PLAYER_MSGTYPE_CMD:
                return new String ("PLAYER_MSGTYPE_CMD");
            case PLAYER_MSGTYPE_REQ:
                return new String ("PLAYER_MSGTYPE_REQ");
            case PLAYER_MSGTYPE_RESP_ACK:
                return new String ("PLAYER_MSGTYPE_RESP_ACK");
            case PLAYER_MSGTYPE_SYNCH:
                return new String ("PLAYER_MSGTYPE_SYNCH");
            case PLAYER_MSGTYPE_RESP_NACK:
                return new String ("PLAYER_MSGTYPE_RESP_NACK");
            default:
                return new String ("unknown");
        }
    }

    /**
     *
     * @param code interface code
     * @return the interface name as a String
     */
    public String lookupName (int code) {
        switch (code) {
            case PLAYER_ACTARRAY_CODE:
                return PLAYER_ACTARRAY_STRING;

            case PLAYER_AIO_CODE:
                return PLAYER_AIO_STRING;

            case PLAYER_AUDIO_CODE:
                return PLAYER_AUDIO_STRING;

            case PLAYER_AUDIODSP_CODE:
                return PLAYER_AUDIODSP_STRING;

            case PLAYER_AUDIOMIXER_CODE:
                return PLAYER_AUDIOMIXER_STRING;

            case PLAYER_BLINKENLIGHT_CODE:
                return PLAYER_BLINKENLIGHT_STRING;

            case PLAYER_BLOBFINDER_CODE:
                return PLAYER_BLOBFINDER_STRING;

            case PLAYER_BUMPER_CODE:
                return PLAYER_BUMPER_STRING;

            case PLAYER_CAMERA_CODE:
                return PLAYER_CAMERA_STRING;

            case PLAYER_DIO_CODE:
                return PLAYER_DIO_STRING;

            case PLAYER_ENERGY_CODE:
                return PLAYER_ENERGY_STRING;

            case PLAYER_FIDUCIAL_CODE:
                return PLAYER_FIDUCIAL_STRING;

            case PLAYER_GPS_CODE:
                return PLAYER_GPS_STRING;

            case PLAYER_GRIPPER_CODE:
                return PLAYER_GRIPPER_STRING;

            case PLAYER_GRAPHICS2D_CODE:
                return PLAYER_GRAPHICS2D_STRING;

            case PLAYER_GRAPHICS3D_CODE:
                return PLAYER_GRAPHICS3D_STRING;

            case PLAYER_IR_CODE:
                return PLAYER_IR_STRING;

            case PLAYER_JOYSTICK_CODE:
                return PLAYER_JOYSTICK_STRING;

            case PLAYER_LASER_CODE:
                return PLAYER_LASER_STRING;

            case PLAYER_LIMB_CODE:
                return PLAYER_LIMB_STRING;

            case PLAYER_LOCALIZE_CODE:
                return PLAYER_LOCALIZE_STRING;

            case PLAYER_LOG_CODE:
                return PLAYER_LOG_STRING;

            case PLAYER_MAP_CODE:
                return PLAYER_MAP_STRING;

            case PLAYER_MCOM_CODE:
                return PLAYER_MCOM_STRING;

            case PLAYER_NOMAD_CODE:
                return PLAYER_NOMAD_STRING;

            case PLAYER_NULL_CODE:
                return PLAYER_NULL_STRING;

            case PLAYER_OPAQUE_CODE:
                return PLAYER_OPAQUE_STRING;

            case PLAYER_PLANNER_CODE:
                return PLAYER_PLANNER_STRING;

            case PLAYER_PLAYER_CODE:
                return PLAYER_PLAYER_STRING;

            case PLAYER_POSITION1D_CODE:
                return PLAYER_POSITION1D_STRING;

            case PLAYER_POSITION2D_CODE:
                return PLAYER_POSITION2D_STRING;

            case PLAYER_POSITION3D_CODE:
                return PLAYER_POSITION3D_STRING;

            case PLAYER_POWER_CODE:
                return PLAYER_POWER_STRING;

            case PLAYER_PTZ_CODE:
                return PLAYER_PTZ_STRING;

            case PLAYER_RFID_CODE:
                return PLAYER_RFID_STRING;

            case PLAYER_SERVICE_ADV_CODE:
                return PLAYER_SERVICE_ADV_STRING;

            case PLAYER_SONAR_CODE:
                return PLAYER_SONAR_STRING;

            case PLAYER_SOUND_CODE:
                return PLAYER_SOUND_STRING;

            case PLAYER_SPEECH_CODE:
                return PLAYER_SPEECH_STRING;

            case PLAYER_SPEECH_RECOGNITION_CODE:
                return PLAYER_SPEECH_RECOGNITION_STRING;

            case PLAYER_SIMULATION_CODE:
                return PLAYER_SIMULATION_STRING;

            case PLAYER_TRUTH_CODE:
                return PLAYER_TRUTH_STRING;

            case PLAYER_WAVEFORM_CODE:
                return PLAYER_WAVEFORM_STRING;

            case PLAYER_WIFI_CODE:
                return PLAYER_WIFI_STRING;

            case PLAYER_WSN_CODE:
                return PLAYER_WSN_STRING;

            case PLAYER_RANGER_CODE:
                return PLAYER_RANGER_STRING;

            default:
                return "unknown/" + code;
        }
    }

    /**
     *
     * @param name interface name
     * @return the interface code as a short
     */
    public short lookupCode (String name) {
        if (name.startsWith (PLAYER_ACTARRAY_STRING))
            return PLAYER_ACTARRAY_CODE;

        if (name.startsWith (PLAYER_AIO_STRING))
            return PLAYER_AIO_CODE;

        if (name.startsWith (PLAYER_AUDIO_STRING))
            return PLAYER_AUDIO_CODE;

        if (name.startsWith (PLAYER_AUDIODSP_STRING))
            return PLAYER_AUDIODSP_CODE;

        if (name.startsWith (PLAYER_AUDIOMIXER_STRING))
            return PLAYER_AUDIOMIXER_CODE;

        if (name.startsWith (PLAYER_BLINKENLIGHT_STRING))
            return PLAYER_BLINKENLIGHT_CODE;

        if (name.startsWith (PLAYER_BLOBFINDER_STRING))
            return PLAYER_BLOBFINDER_CODE;

        if (name.startsWith (PLAYER_BUMPER_STRING))
            return PLAYER_BUMPER_CODE;

        if (name.startsWith (PLAYER_CAMERA_STRING))
            return PLAYER_CAMERA_CODE;

        if (name.startsWith (PLAYER_DIO_STRING))
            return PLAYER_DIO_CODE;

        if (name.startsWith (PLAYER_ENERGY_STRING))
            return PLAYER_ENERGY_CODE;

        if (name.startsWith (PLAYER_FIDUCIAL_STRING))
            return PLAYER_FIDUCIAL_CODE;

        if (name.startsWith (PLAYER_GPS_STRING))
            return PLAYER_GPS_CODE;

        if (name.startsWith (PLAYER_GRIPPER_STRING))
            return PLAYER_GRIPPER_CODE;

        if (name.startsWith (PLAYER_GRAPHICS2D_STRING))
            return PLAYER_GRAPHICS2D_CODE;

        if (name.startsWith (PLAYER_GRAPHICS3D_STRING))
            return PLAYER_GRAPHICS3D_CODE;

        if (name.startsWith (PLAYER_IR_STRING))
            return PLAYER_IR_CODE;

        if (name.startsWith (PLAYER_JOYSTICK_STRING))
            return PLAYER_JOYSTICK_CODE;

        if (name.startsWith (PLAYER_LASER_STRING))
            return PLAYER_LASER_CODE;

        if (name.startsWith (PLAYER_LIMB_STRING))
            return PLAYER_LIMB_CODE;

        if (name.startsWith (PLAYER_LOCALIZE_STRING))
            return PLAYER_LOCALIZE_CODE;

        if (name.startsWith (PLAYER_LOG_STRING))
            return PLAYER_LOG_CODE;

        if (name.startsWith (PLAYER_MAP_STRING))
            return PLAYER_MAP_CODE;

        if (name.startsWith (PLAYER_MCOM_STRING))
            return PLAYER_MCOM_CODE;

        if (name.startsWith (PLAYER_NOMAD_STRING))
            return PLAYER_NOMAD_CODE;

        if (name.startsWith (PLAYER_NULL_STRING))
            return PLAYER_NULL_CODE;

        if (name.startsWith (PLAYER_OPAQUE_STRING))
            return PLAYER_OPAQUE_CODE;

        if (name.startsWith (PLAYER_PLANNER_STRING))
            return PLAYER_PLANNER_CODE;

        if (name.startsWith (PLAYER_PLAYER_STRING))
            return PLAYER_PLAYER_CODE;

        if (name.startsWith (PLAYER_POSITION1D_STRING))
            return PLAYER_POSITION1D_CODE;

        if (name.startsWith (PLAYER_POSITION2D_STRING))
            return PLAYER_POSITION2D_CODE;

        if (name.startsWith (PLAYER_POSITION3D_STRING))
            return PLAYER_POSITION3D_CODE;

        if (name.startsWith (PLAYER_POWER_STRING))
            return PLAYER_POWER_CODE;

        if (name.startsWith (PLAYER_PTZ_STRING))
            return PLAYER_PTZ_CODE;

        if (name.startsWith (PLAYER_RFID_STRING))
            return PLAYER_RFID_CODE;

        if (name.startsWith (PLAYER_SERVICE_ADV_STRING))
            return PLAYER_SERVICE_ADV_CODE;

        if (name.startsWith (PLAYER_SONAR_STRING))
            return PLAYER_SONAR_CODE;

        if (name.startsWith (PLAYER_SOUND_STRING))
            return PLAYER_SOUND_CODE;

        if (name.startsWith (PLAYER_SPEECH_STRING))
            return PLAYER_SPEECH_CODE;

        if (name.startsWith (PLAYER_SPEECH_RECOGNITION_STRING))
            return PLAYER_SPEECH_RECOGNITION_CODE;

        if (name.startsWith (PLAYER_SIMULATION_STRING))
            return PLAYER_SIMULATION_CODE;

        if (name.startsWith (PLAYER_TRUTH_STRING))
            return PLAYER_TRUTH_CODE;

        if (name.startsWith (PLAYER_WAVEFORM_STRING))
            return PLAYER_WAVEFORM_CODE;

        if (name.startsWith (PLAYER_WIFI_STRING))
            return PLAYER_WIFI_CODE;

        if (name.startsWith (PLAYER_WSN_STRING))
            return PLAYER_WSN_CODE;

        return -1;
    }
}
