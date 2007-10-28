/*
 * DataConstants.java
 *
 * 28 Aug 2007 
 */
package suncertify.db;

/**
 * @author Richard Wardle
 */
public class DatabaseConstants {

    static final String CHARACTER_SET = "ISO-8859-1";
    static final char PAD_CHARACTER = ' ';

    static final int MAGIC_COOKIE = 513;
    static final int RECORD_LENGTH = 182;
    static final short FIELD_COUNT = 6;

    static final String[] FIELD_NAMES = { "name", "location", "specialties",
            "size", "rate", "owner" };
    static final short[] FIELD_LENGTHS = { 32, 64, 64, 6, 8, 8 };

    static final int RECORD_VALIDITY_FLAG_LENGTH = 1;
    static final byte VALID_RECORD_FLAG = 0;
    static final byte DELETED_RECORD_FLAG = 1;

    private DatabaseConstants() {
        // Prevent instantiation
    }
}
