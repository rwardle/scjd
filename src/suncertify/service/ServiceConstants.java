/*
 * ServiceConstants.java
 *
 * 30 Nov 2007 
 */

package suncertify.service;

/**
 * Constants for the service layer.
 * 
 * @author Richard Wardle
 */
public class ServiceConstants {

    /** Number of fields in a database record. */
    public static final int FIELD_COUNT = 6;

    /** Index of the name field in a database record. */
    public static final int NAME_FIELD_INDEX = 0;

    /** Index of the location field in a database record. */
    public static final int LOCATION_FIELD_INDEX = 1;

    /** Index of the specialties field in a database record. */
    public static final int SPECIALTIES_FIELD_INDEX = 2;

    /** Index of the size field in a database record. */
    public static final int SIZE_FIELD_INDEX = 3;

    /** Index of the rate field in a database record. */
    public static final int RATE_FIELD_INDEX = 4;

    /** Index of the owner field in a database record. */
    public static final int OWNER_FIELD_INDEX = 5;

    private ServiceConstants() {
        // Prevent instantiation
    }
}
