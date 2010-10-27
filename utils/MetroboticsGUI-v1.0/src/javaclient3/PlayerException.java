/*
 *  Player Java Client 3 - PlayerException.java
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

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * All Javaclient exceptions are of this type.
 * @author Gareth Randall, Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerException extends RuntimeException {

    // Logging support
    private Logger logger = Logger.getLogger (PlayerException.class.getName ()); 
    
    public PlayerException () { }
    
    public PlayerException (String msg) { 
        super (msg); 
        logger.log (Level.SEVERE, msg);
    }
    
    public PlayerException (String msg, Throwable cause) {
        super (msg, cause);
        logger.log (Level.SEVERE, msg);
    }
    
    public PlayerException (Throwable cause) { super (cause); }
}
