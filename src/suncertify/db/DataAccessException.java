/*
 * DataAccessException.java
 *
 * 29 Aug 2007 
 */

package suncertify.db;

/**
 * @author Richard Wardle
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(Throwable e) {
        super(e);
    }
}
