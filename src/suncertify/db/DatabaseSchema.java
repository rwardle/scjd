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

    private final int recordLength;
    private final short fieldCount;
    private final FieldDescription[] fieldDescriptions;

    /**
     * Creates a new instance of <code>DatabaseSchema</code>.
     */
    public DatabaseSchema() {
        recordLength = DatabaseConstants.RECORD_LENGTH;
        fieldCount = DatabaseConstants.FIELD_COUNT;

        fieldDescriptions = new FieldDescription[fieldCount];
        int recordOffset = 0;
        for (int i = 0; i < fieldCount; i++) {
            fieldDescriptions[i] = new FieldDescription(
                    DatabaseConstants.FIELD_NAMES[i],
                    DatabaseConstants.FIELD_LENGTHS[i], recordOffset);
            recordOffset += fieldDescriptions[i].getLength();
        }
    }

    /**
     * Returns the record length.
     * 
     * @return The record length.
     */
    public int getRecordLength() {
        return recordLength;
    }

    /**
     * Returns the field count.
     * 
     * @return The field count.
     */
    public short getFieldCount() {
        return fieldCount;
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
         * @param fieldName
         *                Field name.
         * @param fieldLength
         *                Field length.
         * @param recordOffset
         *                Offset within the database record.
         */
        public FieldDescription(String fieldName, short fieldLength,
                int recordOffset) {
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
