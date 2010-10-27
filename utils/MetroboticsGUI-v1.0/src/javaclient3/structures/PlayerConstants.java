/*
 *  Player Java Client 3 - PlayerConstants.java
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
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public interface PlayerConstants {

    // the player message types (see player.h)
	/**
	 * A data message.  Such messages are asynchronously published from
	 * devices, and are usually used to reflect some part of the device's
	 * state.
	 */
    public final short PLAYER_MSGTYPE_DATA           = 1;

    /**
     * A command message.  Such messages are asynchronously published to
     * devices, and are usually used to change some aspect of the device's
     * state.
     */
    public final short PLAYER_MSGTYPE_CMD            = 2;

    /**
     * A request message.  Such messages are published synchronously to
     * devices, usually to get or set some aspect of the device's state that is
     * not available in data or command messages.  Every request message gets
     * a response message (either PLAYER_MSGTYPE_RESP_ACK or
     * PLAYER_MSGTYPE_RESP_NACK).
     */
    public final short PLAYER_MSGTYPE_REQ            = 3;

    /**
     * A positive response message.  Such messages are published in response
     * to a PLAYER_MSGTYPE_REQ.  This message indicates that the underlying
     * driver received, interpreted, and processed the request.  Any requested
     * data is in the body of this response message.
     */
    public final short PLAYER_MSGTYPE_RESP_ACK       = 4;

    /** A synch message.  @todo Deprecate this message type? */
    public final short PLAYER_MSGTYPE_SYNCH          = 5;

    /**
     * A negative response message.  Such messages are published in response
     * to a PLAYER_MSGTYPE_REQ.  This messages indicates that the underlying
     * driver did not process the message.  Possible causes include: the
     * driver's message queue was full, the driver failed to interpret the
     * request, or the driver does not support the request. This message will
     * have no data in the body.
     */
    public final short PLAYER_MSGTYPE_RESP_NACK      = 6;

    // the request subtypes (see player.h)
    public final short PLAYER_PLAYER_REQ_DEVLIST     = 1;
    public final short PLAYER_PLAYER_REQ_DRIVERINFO  = 2;
    public final short PLAYER_PLAYER_REQ_DEV         = 3;
    public final short PLAYER_PLAYER_REQ_DATA        = 4;
    public final short PLAYER_PLAYER_REQ_DATAMODE    = 5;
    public final short PLAYER_PLAYER_REQ_DATAFREQ    = 6;
    public final short PLAYER_PLAYER_REQ_AUTH        = 7;
    public final short PLAYER_PLAYER_REQ_NAMESERVICE = 8;
    public final short PLAYER_PLAYER_REQ_IDENT       = 9;
    public final short PLAYER_PLAYER_REQ_ADD_REPLACE_RULE = 10;

    // the current assigned interface codes for Player 2.0
    public final short PLAYER_NULL_CODE         = 256; // /dev/null analogue
    public final short PLAYER_PLAYER_CODE       = 1;   // the server itself
    public final short PLAYER_POWER_CODE        = 2;   // power subsystem
    public final short PLAYER_GRIPPER_CODE      = 3;   // gripper
    public final short PLAYER_POSITION2D_CODE   = 4;   // device that moves
    public final short PLAYER_SONAR_CODE        = 5;   // Ultra-sound range-finder
    public final short PLAYER_LASER_CODE        = 6;   // scanning range-finder
    public final short PLAYER_BLOBFINDER_CODE   = 7;   // visual blobfinder
    public final short PLAYER_PTZ_CODE          = 8;   // pan-tilt-zoom unit
    public final short PLAYER_AUDIO_CODE        = 9;   // audio I/O
    public final short PLAYER_FIDUCIAL_CODE     = 10;  // fiducial detector
    public final short PLAYER_SPEECH_CODE       = 12;  // speech I/O
    public final short PLAYER_GPS_CODE          = 13;  // GPS unit
    public final short PLAYER_BUMPER_CODE       = 14;  // bumper array
    public final short PLAYER_TRUTH_CODE        = 15;  // ground-truth (Stage)
    public final short PLAYER_DIO_CODE          = 20;  // digital I/O
    public final short PLAYER_AIO_CODE          = 21;  // analog I/O
    public final short PLAYER_IR_CODE           = 22;  // IR array
    public final short PLAYER_WIFI_CODE         = 23;  // wifi card status
    public final short PLAYER_WAVEFORM_CODE     = 24;  // fetch raw waveforms
    public final short PLAYER_LOCALIZE_CODE     = 25;  // localization
    public final short PLAYER_MCOM_CODE         = 26;  // multicoms
    public final short PLAYER_SOUND_CODE        = 27;  // sound file playback
    public final short PLAYER_AUDIODSP_CODE     = 28;  // audio dsp I/O
    public final short PLAYER_AUDIOMIXER_CODE   = 29;  // audio I/O
    public final short PLAYER_POSITION3D_CODE   = 30;  // 3-D position
    public final short PLAYER_SIMULATION_CODE   = 31;  // simulators
    public final short PLAYER_SERVICE_ADV_CODE  = 32;  // LAN advertisement
    public final short PLAYER_BLINKENLIGHT_CODE = 33;  // blinking lights
    public final short PLAYER_NOMAD_CODE        = 34;  // Nomad robot
    public final short PLAYER_CAMERA_CODE       = 40;  // camera device(gazebo)
    public final short PLAYER_MAP_CODE          = 42;  // get a map
    public final short PLAYER_PLANNER_CODE      = 44;  // 2D motion planner
    public final short PLAYER_LOG_CODE          = 45;  // log R/W control
    public final short PLAYER_ENERGY_CODE       = 46;  // energy charging
    public final short PLAYER_JOYSTICK_CODE     = 49;  // Joystick
    public final short PLAYER_SPEECH_RECOGNITION_CODE = 50;  // speech I/O
    public final short PLAYER_OPAQUE_CODE       = 51;  // plugin interface
    public final short PLAYER_POSITION1D_CODE   = 52;  // 1-D position
    public final short PLAYER_ACTARRAY_CODE     = 53;  // Actuator Array interface
    public final short PLAYER_LIMB_CODE         = 54;  // Limb interface
    public final short PLAYER_GRAPHICS2D_CODE   = 55;  // Graphics2D interface
    public final short PLAYER_RFID_CODE         = 56;  // RFID reader interface
    public final short PLAYER_WSN_CODE          = 57;  // WSN interface
    public final short PLAYER_GRAPHICS3D_CODE   = 58;  // Graphics3D interface
    public final short PLAYER_HEALTH_CODE       = 59;  // Statgrab Health interface
    public final short PLAYER_IMU_CODE          = 60;  // Inertial Measurement Unit interface
    public final short PLAYER_POINTCLOUD3D_CODE = 61;  // 3-D point cloud
    public final short PLAYER_RANGER_CODE       = 62;  // Array of generic range-finders

    public final String PLAYER_ACTARRAY_STRING       = "actarray";
    public final String PLAYER_AIO_STRING            = "aio";
    public final String PLAYER_AUDIO_STRING          = "audio";
    public final String PLAYER_AUDIODSP_STRING       = "audiodsp";
    public final String PLAYER_AUDIOMIXER_STRING     = "audiomixer";
	public final String PLAYER_BLINKENLIGHT_STRING   = "blinkenlight";
	public final String PLAYER_BLOBFINDER_STRING     = "blobfinder";
	public final String PLAYER_BUMPER_STRING         = "bumper";
	public final String PLAYER_CAMERA_STRING         = "camera";
	public final String PLAYER_ENERGY_STRING         = "energy";
	public final String PLAYER_DIO_STRING            = "dio";
	public final String PLAYER_GRIPPER_STRING        = "gripper";
	public final String PLAYER_FIDUCIAL_STRING       = "fiducial";
	public final String PLAYER_GPS_STRING            = "gps";
	public final String PLAYER_IR_STRING             = "ir";
	public final String PLAYER_JOYSTICK_STRING       = "joystick";
	public final String PLAYER_LASER_STRING          = "laser";
	public final String PLAYER_LIMB_STRING           = "limb";
	public final String PLAYER_LOCALIZE_STRING       = "localize";
	public final String PLAYER_LOG_STRING            = "log";
	public final String PLAYER_MAP_STRING            = "map";
	public final String PLAYER_MCOM_STRING           = "mcom";
	public final String PLAYER_NOMAD_STRING          = "nomad";
	public final String PLAYER_NULL_STRING           = "null";
	public final String PLAYER_OPAQUE_STRING         = "opaque";
	public final String PLAYER_PLANNER_STRING        = "planner";
	public final String PLAYER_PLAYER_STRING         = "player";
	public final String PLAYER_POSITION1D_STRING     = "position1d";
	public final String PLAYER_POSITION2D_STRING     = "position2d";
	public final String PLAYER_POSITION3D_STRING     = "position3d";
	public final String PLAYER_POWER_STRING          = "power";
	public final String PLAYER_PTZ_STRING            = "ptz";
	public final String PLAYER_RFID_STRING           = "rfid";
	public final String PLAYER_SERVICE_ADV_STRING    = "service_adv";
	public final String PLAYER_SIMULATION_STRING     = "simulation";
	public final String PLAYER_SONAR_STRING          = "sonar";
	public final String PLAYER_SOUND_STRING          = "sound";
	public final String PLAYER_SPEECH_STRING         = "speech";
	public final String PLAYER_SPEECH_RECOGNITION_STRING = "speech_recognition";
	public final String PLAYER_TRUTH_STRING          = "truth";
	public final String PLAYER_WAVEFORM_STRING       = "waveform";
	public final String PLAYER_WIFI_STRING           = "wifi";
	public final String PLAYER_WSN_STRING            = "wsn";
	public final String PLAYER_GRAPHICS2D_STRING     = "graphics2d";
	public final String PLAYER_GRAPHICS3D_STRING     = "graphics3d";
	public final String PLAYER_HEALTH_STRING         = "health";
	public final String PLAYER_IMU_STRING            = "imu";
	public final String PLAYER_POINTCLOUD3D_STRING   = "pointcloud3d";
	public final String PLAYER_RANGER_STRING         = "ranger"; 

	// the device access modes
	/**
	 * Device access mode: open.
	 */
    public static final short PLAYER_OPEN_MODE   = 1;
    /**
     * Device access mode: close.
     */
    public static final short PLAYER_CLOSE_MODE  = 2;
    /**
     * Device access mode: error.
     */
    public static final short PLAYER_ERROR_MODE  = 3;


    // data delivery modes
    /**
     * Data delivery mode: Send data from all subscribed devices all the time
     * (i.e. when it's ready on the server).
     */
    public final int PLAYER_DATAMODE_PUSH  = 1;
    /**
     * Data delivery mode: Only on request, send data from all subscribed
     * devices. A PLAYER_MSGTYPE_SYNCH packet follows each set of data.
     * Request should be made automatically by client libraries when they
     * begin reading.
     */
    public final int PLAYER_DATAMODE_PULL  = 2;


    /** The largest possible message */
    public final int PLAYER_MAX_MESSAGE_SIZE            = 8388608; // 8MB
    /** Maximum length for a driver name */
    public final short PLAYER_MAX_DRIVER_STRING_LEN     = 64;
    /** The maximum number of devices the server will support. */
    public final int PLAYER_MAX_DEVICES                 = 10;
    /** Default maximum length for a message queue */
    public final int PLAYER_MSGQUEUE_DEFAULT_MAXLEN     = 32;
    /** Length of string that is spit back as a banner on connection */
    public final int PLAYER_IDENT_STRLEN                = 32;
    /** Length of authentication  */
    public final int PLAYER_KEYLEN                      = 32;

    /* maximum size for request/reply.
     * this is a convenience so that the PlayerQueue can used fixed size elements.
     */
    public final short PLAYER_MAX_REQREP_SIZE = 4096; /* 4KB */

    public final int PLAYER_ACTARRAY_NUM_ACTUATORS    = 16;
    public final int PLAYER_ACTARRAY_ACTSTATE_IDLE    = 1;
    public final int PLAYER_ACTARRAY_ACTSTATE_MOVING  = 2;
    public final int PLAYER_ACTARRAY_ACTSTATE_BRAKED  = 3;
    public final int PLAYER_ACTARRAY_ACTSTATE_STALLED = 4;
    public final int PLAYER_ACTARRAY_TYPE_LINEAR      = 1;
    public final int PLAYER_ACTARRAY_TYPE_ROTARY      = 2;
    public final int PLAYER_ACTARRAY_POWER_REQ        = 1;
    public final int PLAYER_ACTARRAY_BRAKES_REQ       = 2;
    public final int PLAYER_ACTARRAY_GET_GEOM_REQ     = 3;
    public final int PLAYER_ACTARRAY_SPEED_REQ        = 4;
    public final int PLAYER_ACTARRAY_POS_CMD	      = 1;
    public final int PLAYER_ACTARRAY_SPEED_CMD        = 2;
    public final int PLAYER_ACTARRAY_HOME_CMD         = 3;
    public final int PLAYER_ACTARRAY_DATA_STATE       = 1;

    public final int PLAYER_AIO_MAX_INPUTS  = 8;
    public final int PLAYER_AIO_MAX_OUTPUTS = 8;
    public final int PLAYER_AIO_CMD_STATE   = 1;
    public final int PLAYER_AIO_DATA_STATE  = 1;

    public final int PLAYER_AUDIO_DATA_BUFFER_SIZE    = 20;
    public final int PLAYER_AUDIO_COMMAND_BUFFER_SIZE = 6;
    public final int PLAYER_AUDIO_PAIRS               = 5;

    public final int PLAYER_AUDIODSP_MAX_FREQS         = 8;
    public final int PLAYER_AUDIODSP_MAX_BITSTRING_LEN = 64;
    public final int PLAYER_AUDIODSP_SET_CONFIG        = 1;
    public final int PLAYER_AUDIODSP_GET_CONFIG        = 2;
    public final int PLAYER_AUDIODSP_PLAY_TONE         = 1;
    public final int PLAYER_AUDIODSP_PLAY_CHIRP        = 2;
    public final int PLAYER_AUDIODSP_REPLAY            = 3;
    public final int PLAYER_AUDIODSP_DATA_TONES        = 1;

    public final int PLAYER_AUDIOMIXER_SET_MASTER = 1;
    public final int PLAYER_AUDIOMIXER_SET_PCM    = 2;
	public final int PLAYER_AUDIOMIXER_SET_LINE   = 3;
	public final int PLAYER_AUDIOMIXER_SET_MIC    = 4;
	public final int PLAYER_AUDIOMIXER_SET_IGAIN  = 5;
	public final int PLAYER_AUDIOMIXER_SET_OGAIN  = 6;
	public final int PLAYER_AUDIOMIXER_GET_LEVELS = 1;

	public final int PLAYER_BLINKENLIGHT_DATA_STATE    = 1;
	public final int PLAYER_BLINKENLIGHT_CMD_STATE     = 1;
	public final int PLAYER_BLINKENLIGHT_CMD_POWER     = 2;
	public final int PLAYER_BLINKENLIGHT_CMD_COLOR     = 3;
	public final int PLAYER_BLINKENLIGHT_CMD_PERIOD    = 4;
	public final int PLAYER_BLINKENLIGHT_CMD_DUTYCYCLE = 5;

	public final int PLAYER_BLOBFINDER_MAX_BLOBS             = 256;
	public final int PLAYER_BLOBFINDER_REQ_SET_COLOR         = 1;
	public final int PLAYER_BLOBFINDER_REQ_SET_IMAGER_PARAMS = 2;
	public final int PLAYER_BLOBFINDER_DATA_BLOBS            = 1;

	public final int PLAYER_BUMPER_MAX_SAMPLES = 32;
	public final int PLAYER_BUMPER_GET_GEOM    = 1;
	public final int PLAYER_BUMPER_DATA_STATE  = 1;
	public final int PLAYER_BUMPER_DATA_GEOM   = 2;

	public final int PLAYER_CAMERA_DATA_STATE    = 1;
	public final int PLAYER_CAMERA_IMAGE_WIDTH   = 1920;
	public final int PLAYER_CAMERA_IMAGE_HEIGHT  = 1080;
	public final int PLAYER_CAMERA_IMAGE_SIZE    = 1920 * 1080 * 4;
	public final int PLAYER_CAMERA_FORMAT_MONO8  = 1;
	public final int PLAYER_CAMERA_FORMAT_MONO16 = 2;
	public final int PLAYER_CAMERA_FORMAT_RGB565 = 4;
	public final int PLAYER_CAMERA_FORMAT_RGB888 = 5;
	public final int PLAYER_CAMERA_COMPRESS_RAW  = 0;
	public final int PLAYER_CAMERA_COMPRESS_JPEG = 1;

	public final int PLAYER_DIO_DATA_VALUES = 1;
	public final int PLAYER_DIO_CMD_VALUES  = 1;

	public final int PLAYER_ENERGY_DATA_STATE              = 1;
	public final int PLAYER_ENERGY_SET_CHARGING_POLICY_REQ = 1;

	public final int PLAYER_FIDUCIAL_MAX_SAMPLES  = 32;
	public final int PLAYER_FIDUCIAL_DATA_SCAN    = 1;
	public final int PLAYER_FIDUCIAL_REQ_GET_GEOM = 1;
	public final int PLAYER_FIDUCIAL_REQ_GET_FOV  = 2;
	public final int PLAYER_FIDUCIAL_REQ_SET_FOV  = 3;
	public final int PLAYER_FIDUCIAL_REQ_GET_ID   = 7;
	public final int PLAYER_FIDUCIAL_REQ_SET_ID   = 8;

	public final int PLAYER_GPS_DATA_STATE = 1;

	public final int PLAYER_GRIPPER_DATA_STATE   = 1;
	public final int PLAYER_GRIPPER_CMD_STATE    = 1;
	public final int PLAYER_GRIPPER_REQ_GET_GEOM = 1;

	public final int PLAYER_IR_MAX_SAMPLES = 32;
	public final int PLAYER_IR_REQ_POSE    = 1;
	public final int PLAYER_IR_REQ_POWER   = 2;
	public final int PLAYER_IR_DATA_RANGES = 1;

	public final int PLAYER_JOYSTICK_DATA_STATE = 1;

	public final int PLAYER_LASER_MAX_SAMPLES    = 1024;
	public final int PLAYER_LASER_DATA_SCAN      = 1;
	public final int PLAYER_LASER_DATA_SCANPOSE  = 2;
	public final int PLAYER_LASER_REQ_GET_GEOM   = 1;
	public final int PLAYER_LASER_REQ_SET_CONFIG = 2;
	public final int PLAYER_LASER_REQ_GET_CONFIG = 3;
	public final int PLAYER_LASER_REQ_POWER      = 4;

	public final int PLAYER_LIMB_STATE_IDLE      = 1;
	public final int PLAYER_LIMB_STATE_BRAKED    = 2;
	public final int PLAYER_LIMB_STATE_MOVING    = 3;
	public final int PLAYER_LIMB_STATE_OOR       = 4;
	public final int PLAYER_LIMB_STATE_COLL      = 5;
	public final int PLAYER_LIMB_DATA            = 1;
	public final int PLAYER_LIMB_HOME_CMD        = 1;
	public final int PLAYER_LIMB_STOP_CMD        = 2;
	public final int PLAYER_LIMB_SETPOSE_CMD     = 3;
	public final int PLAYER_LIMB_SETPOSITION_CMD = 4;
	public final int PLAYER_LIMB_VECMOVE_CMD     = 5;
	public final int PLAYER_LIMB_POWER_REQ       = 1;
	public final int PLAYER_LIMB_BRAKES_REQ      = 2;
	public final int PLAYER_LIMB_GEOM_REQ        = 3;
	public final int PLAYER_LIMB_SPEED_REQ       = 4;

	public final int PLAYER_LOCALIZE_MAX_HYPOTHS       = 10;
	public final int PLAYER_LOCALIZE_PARTICLES_MAX     = 100;
	public final int PLAYER_LOCALIZE_DATA_HYPOTHS      = 1;
	public final int PLAYER_LOCALIZE_REQ_SET_POSE      = 1;
	public final int PLAYER_LOCALIZE_REQ_GET_PARTICLES = 2;

	public final int PLAYER_LOG_TYPE_READ           = 1;
	public final int PLAYER_LOG_TYPE_WRITE          = 2;
	public final int PLAYER_LOG_REQ_SET_WRITE_STATE = 1;
	public final int PLAYER_LOG_REQ_SET_READ_STATE  = 2;
	public final int PLAYER_LOG_REQ_GET_STATE       = 3;
	public final int PLAYER_LOG_REQ_SET_READ_REWIND = 4;
	public final int PLAYER_LOG_REQ_SET_FILENAME    = 5;

	public final int PLAYER_MAP_MAX_TILE_SIZE  = 2097102;
	public final int PLAYER_MAP_MAX_SEGMENTS   = 131068;
	public final int PLAYER_MAP_DATA_INFO      = 1;
	public final int PLAYER_MAP_REQ_GET_INFO   = 1;
	public final int PLAYER_MAP_REQ_GET_DATA   = 2;
	public final int PLAYER_MAP_REQ_GET_VECTOR = 3;

	public final int MCOM_DATA_LEN            = 128;
	public final int MCOM_DATA_BUFFER_SIZE    = 0;
	public final int MCOM_N_BUFS              = 10;
	public final int MCOM_CHANNEL_LEN         = 8;
	public final int PLAYER_MCOM_PUSH         = 0;
	public final int PLAYER_MCOM_POP          = 1;
	public final int PLAYER_MCOM_READ         = 2;
	public final int PLAYER_MCOM_CLEAR        = 3;
	public final int PLAYER_MCOM_SET_CAPACITY = 4;

	public final int PLAYER_PLANNER_DATA_STATE        = 1;
	public final int PLAYER_PLANNER_CMD_GOAL          = 1;
	public final int PLAYER_PLANNER_REQ_GET_WAYPOINTS = 1;
	public final int PLAYER_PLANNER_REQ_ENABLE        = 2;

	public final int PLAYER_POSITION1D_REQ_GET_GEOM      = 1;
	public final int PLAYER_POSITION1D_REQ_MOTOR_POWER   = 2;
	public final int PLAYER_POSITION1D_REQ_VELOCITY_MODE = 3;
	public final int PLAYER_POSITION1D_REQ_POSITION_MODE = 4;
	public final int PLAYER_POSITION1D_REQ_SET_ODOM      = 5;
	public final int PLAYER_POSITION1D_REQ_RESET_ODOM    = 6;
	public final int PLAYER_POSITION1D_REQ_SPEED_PID     = 7;
	public final int PLAYER_POSITION1D_REQ_POSITION_PID  = 8;
	public final int PLAYER_POSITION1D_REQ_SPEED_PROF    = 9;
	public final int PLAYER_POSITION1D_DATA_STATE        = 1;
	public final int PLAYER_POSITION1D_DATA_GEOM         = 2;
	public final int PLAYER_POSITION1D_CMD_VEL           = 1;
	public final int PLAYER_POSITION1D_CMD_POS           = 2;
	// Status byte: limit min
	public final int PLAYER_POSITION1D_STATUS_LIMIT_MIN  = 0;
	// Status byte: limit center
	public final int PLAYER_POSITION1D_STATUS_LIMIT_CEN  = 1;
	// Status byte: limit max
	public final int PLAYER_POSITION1D_STATUS_LIMIT_MAX  = 2;
	// Status byte: limit over current
	public final int PLAYER_POSITION1D_STATUS_OC         = 3;
	// Status byte: limit trajectory complete
	public final int PLAYER_POSITION1D_STATUS_TRAJ_COMPLETE = 4;
	// Status byte: enabled
	public final int PLAYER_POSITION1D_STATUS_ENABLED    = 5;


	public final int PLAYER_POSITION2D_REQ_GET_GEOM      = 1;
	public final int PLAYER_POSITION2D_REQ_MOTOR_POWER   = 2;
	public final int PLAYER_POSITION2D_REQ_VELOCITY_MODE = 3;
	public final int PLAYER_POSITION2D_REQ_POSITION_MODE = 4;
	public final int PLAYER_POSITION2D_REQ_SET_ODOM      = 5;
	public final int PLAYER_POSITION2D_REQ_RESET_ODOM    = 6;
	public final int PLAYER_POSITION2D_REQ_SPEED_PID     = 7;
	public final int PLAYER_POSITION2D_REQ_POSITION_PID  = 8;
	public final int PLAYER_POSITION2D_REQ_SPEED_PROF    = 9;
	public final int PLAYER_POSITION2D_DATA_STATE        = 1;
	public final int PLAYER_POSITION2D_DATA_GEOM         = 2;
	public final int PLAYER_POSITION2D_CMD_VEL           = 1;
	public final int PLAYER_POSITION2D_CMD_POS           = 2;
	public final int PLAYER_POSITION2D_CMD_CAR           = 3;

	public final int PLAYER_POSITION3D_DATA_STATE    = 1;
	public final int PLAYER_POSITION3D_DATA_GEOMETRY = 2;
	public final int PLAYER_POSITION3D_CMD_SET_VEL   = 1;
	public final int PLAYER_POSITION3D_CMD_SET_POS   = 2;
	public final int PLAYER_POSITION3D_GET_GEOM      = 1;
	public final int PLAYER_POSITION3D_MOTOR_POWER   = 2;
	public final int PLAYER_POSITION3D_VELOCITY_MODE = 3;
	public final int PLAYER_POSITION3D_POSITION_MODE = 4;
	public final int PLAYER_POSITION3D_RESET_ODOM    = 5;
	public final int PLAYER_POSITION3D_SET_ODOM      = 6;
	public final int PLAYER_POSITION3D_SPEED_PID     = 7;
	public final int PLAYER_POSITION3D_POSITION_PID  = 8;
	public final int PLAYER_POSITION3D_SPEED_PROF    = 9;

	public final int PLAYER_POWER_DATA_STATE              = 1;
	public final int PLAYER_POWER_SET_CHARGING_POLICY_REQ = 1;
	public final int PLAYER_POWER_MASK_VOLTS              = 1;
	public final int PLAYER_POWER_MASK_WATTS              = 2;
	public final int PLAYER_POWER_MASK_JOULES             = 4;
	public final int PLAYER_POWER_MASK_PERCENT            = 8;
	public final int PLAYER_POWER_MASK_CHARGING           = 16;

	public final int PLAYER_PTZ_MAX_CONFIG_LEN   = 32;
	public final int PLAYER_PTZ_VELOCITY_CONTROL = 0;
	public final int PLAYER_PTZ_POSITION_CONTROL = 1;
	public final int PLAYER_PTZ_REQ_GENERIC      = 1;
	public final int PLAYER_PTZ_REQ_CONTROL_MODE = 2;
	public final int PLAYER_PTZ_REQ_GEOM         = 4;
    public final int PLAYER_PTZ_REQ_STATUS       = 5;
	public final int PLAYER_PTZ_DATA_STATE       = 1;
	public final int PLAYER_PTZ_DATA_GEOM        = 2;
	public final int PLAYER_PTZ_CMD_STATE        = 1;

    public final int PLAYER_RANGER_REQ_GET_GEOM   = 1;
    public final int PLAYER_RANGER_REQ_POWER      = 2;
    public final int PLAYER_RANGER_REQ_INTNS      = 3;
    public final int PLAYER_RANGER_REQ_SET_CONFIG = 4;
    public final int PLAYER_RANGER_REQ_GET_CONFIG = 5;
    public final int PLAYER_RANGER_DATA_RANGE     = 1;
    public final int PLAYER_RANGER_DATA_RANGEPOSE = 2;
    public final int PLAYER_RANGER_DATA_INTNS     = 3;
    public final int PLAYER_RANGER_DATA_INTNSPOSE = 4;
    public final int PLAYER_RANGER_DATA_GEOM      = 5;

    public final int PLAYER_SIMULATION_IDENTIFIER_MAXLEN = 64;
	public final int PLAYER_SIMULATION_REQ_GET_POSE2D    = 1;
	public final int PLAYER_SIMULATION_REQ_SET_POSE2D    = 2;

	public final int PLAYER_SONAR_MAX_SAMPLES  = 64;
	public final int PLAYER_SONAR_REQ_GET_GEOM = 1;
	public final int PLAYER_SONAR_REQ_POWER    = 2;
	public final int PLAYER_SONAR_DATA_RANGES  = 1;
	public final int PLAYER_SONAR_DATA_GEOM    = 2;

	public final int PLAYER_SOUND_CMD_IDX = 1;

	public final int PLAYER_SPEECH_MAX_STRING_LEN = 256;
	public final int PLAYER_SPEECH_CMD_SAY        = 1;

	public final int SPEECH_RECOGNITION_TEXT_LEN    = 256;
	public final int SPEECH_RECOGNITION_DATA_STRING = 1;

	public final int PLAYER_TRUTH_DATA_POSE           = 0x01;
	public final int PLAYER_TRUTH_DATA_FIDUCIAL_ID    = 0x02;
	public final int PLAYER_TRUTH_REQ_SET_POSE        = 0x03;
	public final int PLAYER_TRUTH_REQ_SET_FIDUCIAL_ID = 0x04;
	public final int PLAYER_TRUTH_REQ_GET_FIDUCIAL_ID = 0x05;

	public final int PLAYER_WAVEFORM_DATA_MAX    = 4096;
	public final int PLAYER_WAVEFORM_DATA_SAMPLE = 1;

	public final int PLAYER_WIFI_MAX_LINKS    = 32;
	public final int PLAYER_WIFI_QUAL_DBM     = 1;
	public final int PLAYER_WIFI_QUAL_REL     = 2;
	public final int PLAYER_WIFI_QUAL_UNKNOWN = 3;
	public final int PLAYER_WIFI_MODE_UNKNOWN = 0;
	public final int PLAYER_WIFI_MODE_AUTO    = 1;
	public final int PLAYER_WIFI_MODE_ADHOC   = 2;
	public final int PLAYER_WIFI_MODE_INFRA   = 3;
	public final int PLAYER_WIFI_MODE_MASTER  = 4;
	public final int PLAYER_WIFI_MODE_REPEAT  = 5;
	public final int PLAYER_WIFI_MODE_SECOND  = 6;
	public final int PLAYER_WIFI_MAC          = 1;
	public final int PLAYER_WIFI_IWSPY_ADD    = 2;
	public final int PLAYER_WIFI_IWSPY_DEL    = 3;
	public final int PLAYER_WIFI_IWSPY_PING   = 4;
	public final int PLAYER_WIFI_DATA_STATE   = 1;

	public final int PLAYER_RFID_MAX_TAGS     = 30;
	public final int PLAYER_RFID_MAX_GUID     = 8;
	public final int PLAYER_RFID_DATA         = 1;
	public final int PLAYER_RFID_REQ_POWER    = 1;
	public final int PLAYER_RFID_REQ_READTAG  = 2;
	public final int PLAYER_RFID_REQ_WRITETAG = 3;
	public final int PLAYER_RFID_REQ_LOCKTAG  = 4;

	// The maximum number of points that can be described in a packet
	public final int PLAYER_GRAPHICS2D_MAX_POINTS   = 64;
	// Command subtype: clear the drawing area (send an empty message)
	public final int PLAYER_GRAPHICS2D_CMD_CLEAR    = 1;
	// Command subtype: draw points
	public final int PLAYER_GRAPHICS2D_CMD_POINTS   = 2;
	// Command subtype: draw a polyline
	public final int PLAYER_GRAPHICS2D_CMD_POLYLINE = 3;
	// Command subtype: draw a polygon
	public final int PLAYER_GRAPHICS2D_CMD_POLYGON  = 4;

	// The maximum nr of nodes that can work together in the WSN.
	public final int PLAYER_WSN_MAX_NODES    = 100;
	public final int PLAYER_WSN_DATA         = 1;
	public final int PLAYER_WSN_CMD_DEVSTATE = 1;

	// Request/reply: put the reader in sleep mode (0) or wake it up (1).
	public final int PLAYER_WSN_REQ_POWER    = 1;
	// Request/reply: change the data type to RAW or converted engineering units
	public final int PLAYER_WSN_REQ_DATATYPE = 2;
	// Request/reply: change the receiving data frequency
	public final int PLAYER_WSN_REQ_DATAFREQ = 3;

	// The maximum number of points that can be described in a packet
	public final int PLAYER_GRAPHICS3D_MAX_POINTS = 1024;
	// Command subtype: clear the drawing area (send an empty message)
	public final int PLAYER_GRAPHICS3D_CMD_CLEAR  = 1;
	// Command subtype: draw subitems
	public final int PLAYER_GRAPHICS2D_CMD_DRAW   = 2;

	public final int PLAYER_HEALTH_DATA = 1;

	// Data subtype: IMU position/orientation data
	public final int PLAYER_IMU_DATA_STATE       = 1;
	// Data subtype: Calibrated IMU data
	public final int PLAYER_IMU_DATA_CALIB       = 2;
	// Data subtype: Quaternions orientation data
	public final int PLAYER_IMU_DATA_QUAT        = 3;
	// Data subtype: Euler orientation data
	public final int PLAYER_IMU_DATA_EULER       = 4;
	// Request/reply subtype: set data type
	public final int PLAYER_IMU_REQ_SET_DATATYPE = 1;

	// Maximum number of points that can be included in a data packet
	public final int PLAYER_POINTCLOUD3D_MAX_POINTS = 8192;
	// Data subtype: state
	public final int PLAYER_POINTCLOUD3D_DATA_STATE = 1;

}
