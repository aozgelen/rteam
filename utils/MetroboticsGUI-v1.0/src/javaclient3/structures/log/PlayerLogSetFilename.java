/*
 *  Player Java Client 3 - PlayerLogSetFilename.java
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

package javaclient3.structures.log;

import javaclient3.structures.*;

/**
 * Request/reply: Set filename
 * To set the name of the file to write to when logging, send a
 * PLAYER_LOG_REQ_SET_FILENAME request.  Null response. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLogSetFilename implements PlayerConstants {

    // Length of filename 
    private int filename_count;
    // Filename; max 255 chars + terminating NULL 
    private char[] filename = new char[256];


    /**
     * @return  Length of filename 
     **/
    public synchronized int getFilename_count () {
        return this.filename_count;
    }

    /**
     * @param newFilename_count  Length of filename 
     *
     */
    public synchronized void setFilename_count (int newFilename_count) {
        this.filename_count = newFilename_count;
    }
    /**
     * @return  Filename; max 255 chars + terminating NULL 
     **/
    public synchronized char[] getFilename () {
        return this.filename;
    }

    /**
     * @param newFilename  Filename; max 255 chars + terminating NULL 
     *
     */
    public synchronized void setFilename (char[] newFilename) {
        this.filename = newFilename;
    }

}