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
        this.recordLength = DatabaseConstants.RECORD_LENGTH;
        this.fieldCount = DatabaseConstants.FIELD_COUNT;

        this.fieldDescriptions = new FieldDescription[this.fieldCount];
        int recordOffset = 0;
        for (int i = 0; i < this.fieldCount; i++) {
            this.fieldDescriptions[i] = new FieldDescription(
                    DatabaseConstants.FIELD_NAMES[i],
                    DatabaseConstants.FIELD_LENGTHS[i], recordOffset);
            recordOffset += this.fieldDescriptions[i].getLength();
        }
    }

    int getRecordLength() {
        return this.recordLength;
    }

    short getFieldCount() {
        return this.fieldCount;
    }

    FieldDescription[] getFieldDescriptions() {
        return this.fieldDescriptions;
    }

    static final class FieldDescription {

        private final String name;
        private final short length;
        private final int recordOffset;

        FieldDescription(String fieldName, short fieldLength, int recordOffset) {
            this.name = fieldName;
            this.length = fieldLength;
            this.recordOffset = recordOffset;
        }

        String getName() {
            return this.name;
        }

        short getLength() {
            return this.length;
        }

        int getRecordOffset() {
            return this.recordOffset;
        }
    }
}
