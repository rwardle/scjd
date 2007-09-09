package suncertify.db;

import suncertify.db.DatabaseSchema.FieldDescription;

@SuppressWarnings("boxing")
class DataTestConstants {

    static final FieldDescription[] EXPECTED_FIELD_DESCRIPTIONS = new FieldDescription[DatabaseConstants.FIELD_COUNT];
    static {
        int recordOffset = 0;
        for (int i = 0; i < DatabaseConstants.FIELD_COUNT; i++) {
            EXPECTED_FIELD_DESCRIPTIONS[i] = new FieldDescription(
                    DatabaseConstants.FIELD_NAMES[i],
                    DatabaseConstants.FIELD_LENGTHS[i], recordOffset);
            recordOffset += EXPECTED_FIELD_DESCRIPTIONS[i].getLength();
        }
    }

    static final String[] RECORD_VALUES = { "Buonarotti & Company",
            "Smallville", "Air Conditioning, Painting, Painting", "10",
            "$40.00", "1245678" };
    static final String[] RECORD_VALUES_SPACE_PADDED,
            RECORD_VALUES_NULL_PADDED;
    static {
        RECORD_VALUES_SPACE_PADDED = buildRecord(RECORD_VALUES, ' ');
        RECORD_VALUES_NULL_PADDED = buildRecord(RECORD_VALUES, '\u0000');
    }

    static String[] buildRecord(String[] values, char padChar) {
        String[] paddedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            paddedValues[i] = padField(values[i],
                    DatabaseConstants.FIELD_LENGTHS[i], padChar);
        }
        return paddedValues;
    }

    static String padField(String value, short length, char padChar) {
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
