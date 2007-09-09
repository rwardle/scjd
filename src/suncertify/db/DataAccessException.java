/*
 * DataAccessException.java
 *
 * 29 Aug 2007 
 */
package suncertify.db;

import java.io.IOException;

/**
 * @author Richard Wardle
 */
public class DataAccessException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DataAccessException(String string, IOException e) {
        // TODO Auto-generated constructor stub
    }

    public DataAccessException(Throwable e) {
        // TODO Auto-generated constructor stub
    }

}
