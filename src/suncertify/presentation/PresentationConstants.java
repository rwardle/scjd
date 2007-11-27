/*
 * PresentationConstants.java
 *
 * Nov 27 2007 
 */

package suncertify.presentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Presentation constants.
 * 
 * @author Richard Wardle
 */
public class PresentationConstants {

    /** Number of table columns. */
    public static final int TABLE_COLUMN_COUNT = 6;

    /** Index of the name column in the table. */
    public static final int TABLE_NAME_COLUMN_INDEX = 0;

    /** Index of the location column in the table. */
    public static final int TABLE_LOCATION_COLUMN_INDEX = 1;

    /** Index of the specialties column in the table. */
    public static final int TABLE_SPECIALTIES_COLUMN_INDEX = 2;

    /** Index of the size column in the table. */
    public static final int TABLE_SIZE_COLUMN_INDEX = 3;

    /** Index of the rate column in the table. */
    public static final int TABLE_RATE_COLUMN_INDEX = 4;

    /** Index of the owner column in the table. */
    public static final int TABLE_OWNER_COLUMN_INDEX = 5;

    private static final int TABLE_NAME_COLUMN_WIDTH = 90;
    private static final int TABLE_LOCATION_COLUMN_WIDTH = 70;
    private static final int TABLE_SPECIALTIES_COLUMN_WIDTH = 90;
    private static final int TABLE_SIZE_COLUMN_WIDTH = 45;
    private static final int TABLE_RATE_COLUMN_WIDTH = 45;
    private static final int TABLE_OWNER_COLUMN_WIDTH = 60;

    private static final Map<Integer, Integer> WIDTHS = new HashMap<Integer, Integer>();
    static {
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_NAME_COLUMN_INDEX,
                PresentationConstants.TABLE_NAME_COLUMN_WIDTH);
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_LOCATION_COLUMN_INDEX,
                PresentationConstants.TABLE_LOCATION_COLUMN_WIDTH);
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_SPECIALTIES_COLUMN_INDEX,
                PresentationConstants.TABLE_SPECIALTIES_COLUMN_WIDTH);
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_SIZE_COLUMN_INDEX,
                PresentationConstants.TABLE_SIZE_COLUMN_WIDTH);
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_RATE_COLUMN_INDEX,
                PresentationConstants.TABLE_RATE_COLUMN_WIDTH);
        PresentationConstants.WIDTHS.put(
                PresentationConstants.TABLE_OWNER_COLUMN_INDEX,
                PresentationConstants.TABLE_OWNER_COLUMN_WIDTH);
    }

    /** Unmodifiable map of table column indices against column widths. */
    public static final Map<Integer, Integer> TABLE_COLUMN_WIDTHS = Collections
            .unmodifiableMap(PresentationConstants.WIDTHS);

    private PresentationConstants() {
        // Prevent instantiation
    }
}
