/*
 * DataValidationException.java
 *
 * 29 Aug 2007 
 */

package suncertify.db;

/**
 * @author Richard Wardle
 */
public class DataValidationException extends Exception {

    public DataValidationException(String message) {
        super(message);
    }
}
