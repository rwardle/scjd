/*
 * DatabaseSchema.java
 *
 * 29 Aug 2007 
 */

package suncertify.db;

/**
 * Represents the schema of the contractor database.
 *
 * @author Richard Wardle
 */
public final class DatabaseSchema {

    private static final int RECORD_LENGTH = 182;
    private static final short FIELD_COUNT = 6;
    private static final String[] FIELD_NAMES = {"name", "location", "specialties", "size",
            "rate", "owner"};
    private static final short[] FIELD_LENGTHS = {32, 64, 64, 6, 8, 8};

    private final FieldDescription[] fieldDescriptions;

    /**
     * Creates a new instance of <code>DatabaseSchema</code>.
     */
    public DatabaseSchema() {
        fieldDescriptions = new FieldDescription[FIELD_COUNT];
        int recordOffset = 0;
        for (int i = 0; i < FIELD_COUNT; i++) {
            fieldDescriptions[i] = new FieldDescription(FIELD_NAMES[i], FIELD_LENGTHS[i],
                    recordOffset);
            recordOffset += fieldDescriptions[i].getLength();
        }
    }

    /**
     * Returns the record length. This does not include the length of the flag used to indicate the
     * validity of the record (defined in {@link DatabaseConstants#RECORD_VALIDITY_FLAG_LENGTH}.
     *
     * @return The record length.
     */
    public int getRecordLength() {
        return RECORD_LENGTH;
    }

    /**
     * Returns the field count.
     *
     * @return The field count.
     */
    public short getFieldCount() {
        return FIELD_COUNT;
    }

    /**
     * Returns the field descriptions.
     *
     * @return An array of field descriptions.
     */
    public FieldDescription[] getFieldDescriptions() {
        return fieldDescriptions.clone();
    }

    /**
     * Describes a database field.
     *
     * @author Richard Wardle
     */
    public static final class FieldDescription {

        private final String name;
        private final short length;
        private final int recordOffset;

        /**
         * Creates a new instance of <code>FieldDescription</code>.
         *
         * @param fieldName    Field name.
         * @param fieldLength  Field length.
         * @param recordOffset Offset within the database record.
         * @throws IllegalArgumentException If <code>fieldName</code> is <code>null</code>, <code>fieldLength</code> is
         *                                  less than or equal to 0, or <code>recordOffset</code> is less than 0.
         */
        public FieldDescription(String fieldName, short fieldLength, int recordOffset) {
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldName must be non-null");
            }
            if (fieldLength <= 0) {
                throw new IllegalArgumentException("fieldLength must be greater than zero");
            }
            if (recordOffset < 0) {
                throw new IllegalArgumentException(
                        "recordOffset must be greater than or equal to 0");
            }

            name = fieldName;
            length = fieldLength;
            this.recordOffset = recordOffset;
        }

        /**
         * Returns the field name.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the field length.
         *
         * @return The length.
         */
        public short getLength() {
            return length;
        }

        /**
         * Returns the offset with the database record.
         *
         * @return The record offset.
         */
        public int getRecordOffset() {
            return recordOffset;
        }
    }
}
