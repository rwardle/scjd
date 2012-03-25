package suncertify.db;

import suncertify.db.DatabaseSchema.FieldDescription;

public class DataTestConstants {

    public static final int EXPECTED_RECORD_LENGTH = 182;
    public static final short EXPECTED_FIELD_COUNT = 6;

    private static final String[] FIELD_NAMES = {"name", "location",
            "specialties", "size", "rate", "owner"};
    private static final short[] FIELD_LENGTHS = {32, 64, 64, 6, 8, 8};

    public static final FieldDescription[] EXPECTED_FIELD_DESCRIPTIONS = new FieldDescription[EXPECTED_FIELD_COUNT];

    static {
        int recordOffset = 0;
        for (int i = 0; i < EXPECTED_FIELD_COUNT; i++) {
            EXPECTED_FIELD_DESCRIPTIONS[i] = new FieldDescription(
                    FIELD_NAMES[i], FIELD_LENGTHS[i], recordOffset);
            recordOffset += EXPECTED_FIELD_DESCRIPTIONS[i].getLength();
        }
    }

    public static final String[] RECORD_VALUES = {"Buonarotti & Company",
            "Smallville", "Air Conditioning, Painting, Painting", "10",
            "$40.00", "1245678"};
    public static final String[] RECORD_VALUES_SPACE_PADDED,
            RECORD_VALUES_NULL_PADDED;

    static {
        RECORD_VALUES_SPACE_PADDED = padRecord(RECORD_VALUES, ' ');
        RECORD_VALUES_NULL_PADDED = padRecord(RECORD_VALUES, '\u0000');
    }

    public static String[] padRecord(String[] values, char padChar) {
        String[] paddedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            paddedValues[i] = padField(values[i], FIELD_LENGTHS[i], padChar);
        }
        return paddedValues;
    }

    public static String padField(String value, short length, char padChar) {
        StringBuilder builder = new StringBuilder(value);
        while (builder.length() < length) {
            builder.append(padChar);
        }
        return builder.toString();
    }

    private DataTestConstants() {
        // Prevent instantiation
    }
}
