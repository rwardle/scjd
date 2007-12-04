/*
 * DataConstants.java
 *
 * 28 Aug 2007 
 */

package suncertify.db;

/**
 * Constants for the database layer.
 * 
 * @author Richard Wardle
 */
public class DatabaseConstants {

    /** Database character set. */
    public static final String CHARACTER_SET = "ISO-8859-1";

    /** Character used to pad record values. */
    public static final char PAD_CHARACTER = ' ';

    /** Magic cookie value used to identify a contractor database. */
    public static final int MAGIC_COOKIE = 513;

    /** Length of the flag that indicates a valid record. */
    public static final int RECORD_VALIDITY_FLAG_LENGTH = 1;

    /** Flag that indicates a valid record. */
    public static final byte VALID_RECORD_FLAG = 0;

    /** Flag that indicates an invalid record. */
    public static final byte DELETED_RECORD_FLAG = 1;

    private DatabaseConstants() {
        // Prevent instantiation
    }
}
