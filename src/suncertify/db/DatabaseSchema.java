/*
 * DatabaseSchema.java
 *
 * 29 Aug 2007 
 */
package suncertify.db;

/**
 * @author Richard Wardle
 */
class DatabaseSchema {

    private final int recordLength;
    private final short fieldCount;
    private final FieldDescription[] fieldDescriptions;

    DatabaseSchema() {
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

    int getRecordLength() {
        return recordLength;
    }

    short getFieldCount() {
        return fieldCount;
    }

    FieldDescription[] getFieldDescriptions() {
        return fieldDescriptions.clone();
    }

    static final class FieldDescription {

        private final String name;
        private final short length;
        private final int recordOffset;

        FieldDescription(String fieldName, short fieldLength, int recordOffset) {
            name = fieldName;
            length = fieldLength;
            this.recordOffset = recordOffset;
        }

        String getName() {
            return name;
        }

        short getLength() {
            return length;
        }

        int getRecordOffset() {
            return recordOffset;
        }
    }
}
