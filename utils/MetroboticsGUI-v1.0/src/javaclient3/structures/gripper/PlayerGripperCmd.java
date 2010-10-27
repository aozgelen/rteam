/*
 *  Player Java Client 3 - PlayerGripperCmd.java
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

package javaclient3.structures.gripper;

import javaclient3.structures.*;

/**
 * Command: state (PLAYER_GRIPPER_CMD_STATE)
 * The gripper interface accepts 2-byte commands, the format of which
 * is given below.  These two bytes are sent directly to the gripper;
 * refer to Table 3-3 page 10 in the Pioneer 2 Gripper Manual for a list of
 * commands. The first byte is the command. The second is the argument for
 * the LIFTcarry and GRIPpress commands, but for all others it is ignored. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerGripperCmd implements PlayerConstants {

    // the command 
    private int cmd;
    // optional argument 
    private int arg;


    /**
     * @return  the command 
     **/
    public synchronized int getCmd () {
        return this.cmd;
    }

    /**
     * @param newCmd  the command 
     *
     */
    public synchronized void setCmd (int newCmd) {
        this.cmd = newCmd;
    }
    /**
     * @return  optional argument 
     **/
    public synchronized int getArg () {
        return this.arg;
    }

    /**
     * @param newArg  optional argument 
     *
     */
    public synchronized void setArg (int newArg) {
        this.arg = newArg;
    }

}