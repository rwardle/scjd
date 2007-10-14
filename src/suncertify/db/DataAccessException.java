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

    private static final long serialVersionUID = 1L;

    public DataAccessException(Throwable e) {
        super(e);
    }
}
