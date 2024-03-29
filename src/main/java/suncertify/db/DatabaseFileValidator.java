/*
 * DatabaseFileValidator.java
 *
 * 06 Dec 2007 
 */

package suncertify.db;

import suncertify.db.DatabaseSchema.FieldDescription;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class that can be used to validate that the contractor database file is in the correct format.
 * Note that the methods <code>getDataSectionOffset</code> and <code>getRecordCount</code> can only
 * be called if <code>validate</code> has previously been called and it returned successfully.
 *
 * @author Richard Wardle
 */
public final class DatabaseFileValidator {

    private static final Logger LOGGER = Logger.getLogger(DatabaseFileValidator.class.getName());

    private final DatabaseFile databaseFile;
    private final DatabaseSchema databaseSchema;

    // Boolean flag to indicate if the database file has been validated or not
    private boolean validated;

    /*
     * Offset of the data section (where the records start) in the database file.
     */
    private long dataSectionOffset;

    // Number of records in the database
    private int recordCount;

    /**
     * Creates a new instance of <code>DatabaseFileValidator</code> with the specified database file
     * and schema.
     *
     * @param databaseFile   Database file to validate.
     * @param databaseSchema Database schema to validate against.
     * @throws IllegalArgumentException If <code>databaseFile</code> or <code>databaseSchema</code> is <code>null</code>.
     */
    public DatabaseFileValidator(DatabaseFile databaseFile, DatabaseSchema databaseSchema) {
        if (databaseFile == null) {
            throw new IllegalArgumentException("databaseFile cannot be null");
        }
        if (databaseSchema == null) {
            throw new IllegalArgumentException("databaseSchema cannot be null");
        }

        this.databaseFile = databaseFile;
        this.databaseSchema = databaseSchema;
    }

    /**
     * Validates the database file. In the process of validating the database file, the data section
     * offset and record count are stored. They can be accessed using the
     * <code>getDataSectionOffset</code> and <code>getRecordCount</code> after this method has
     * completed.
     *
     * @throws IOException             If there is an error accessing the database file.
     * @throws DataValidationException If the database file is invalid.
     */
    public void validate() throws IOException, DataValidationException {
        validated = false;

        // Ensure the file pointer is at the beginning before starting
        databaseFile.seek(0);
        validateMagicCookie();
        validateSchema();

        dataSectionOffset = databaseFile.getFilePointer();
        recordCount = validateRecordCount();

        LOGGER.info("Database file is valid, record count is: " + recordCount);

        validated = true;
    }

    private void validateMagicCookie() throws IOException, DataValidationException {
        int magicCookie = databaseFile.readInt();
        if (magicCookie != DatabaseConstants.MAGIC_COOKIE) {
            throw new DataValidationException("Invalid magic cookie: " + magicCookie);
        }
    }

    private void validateSchema() throws IOException, DataValidationException {
        validateRecordLength();
        validateFieldCount();
        validateFieldDescriptions();
    }

    private void validateRecordLength() throws IOException, DataValidationException {
        int recordLength = databaseFile.readInt();
        if (recordLength != databaseSchema.getRecordLength()) {
            throw new DataValidationException("Invalid record length: " + recordLength);
        }
    }

    private void validateFieldCount() throws IOException, DataValidationException {
        short fieldCount = databaseFile.readShort();
        if (fieldCount != databaseSchema.getFieldCount()) {
            throw new DataValidationException("Invalid field count: " + fieldCount);
        }
    }

    private void validateFieldDescriptions() throws IOException, DataValidationException {
        FieldDescription[] fieldDescriptions = databaseSchema.getFieldDescriptions();
        for (FieldDescription fieldDescription : fieldDescriptions) {
            short fieldNameLength = databaseFile.readShort();
            if (fieldNameLength != fieldDescription.getName().length()) {
                throw new DataValidationException("Invalid field name length: " + fieldNameLength
                        + ", for field: " + fieldDescription.getName());
            }

            byte[] bytes = new byte[fieldNameLength];
            databaseFile.readFully(bytes);
            String fieldName = new String(bytes, DatabaseConstants.CHARACTER_SET);
            if (!fieldName.equals(fieldDescription.getName())) {
                throw new DataValidationException("Invalid field name: " + fieldName
                        + ", for field: " + fieldDescription.getName());
            }

            short fieldLength = databaseFile.readShort();
            if (fieldLength != fieldDescription.getLength()) {
                throw new DataValidationException("Invalid field length: " + fieldLength
                        + ", for field: " + fieldDescription.getName());
            }
        }
    }

    private int validateRecordCount() throws IOException, DataValidationException {
        long dataSectionLength = databaseFile.length() - dataSectionOffset;
        if (dataSectionLength
                % (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + databaseSchema.getRecordLength()) != 0) {
            throw new DataValidationException(
                    "Data section length cannot contain a whole number of records: "
                            + dataSectionLength);
        }

        /*
         * Record count not expected to exceed Integer.MAX_VALUE since DBMain is using integers for
         * all recNo parameters
         */
        return (int) (dataSectionLength / (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + databaseSchema
                .getRecordLength()));
    }

    /**
     * Gets the offset of the data section of the database file.
     *
     * @return The offset in bytes from the beginning of the file.
     * @throws IllegalStateException If <code>validate</code> has not been called successfully before this method is
     *                               called.
     */
    public long getDataSectionOffset() {
        if (!validated) {
            throw new IllegalStateException(
                    "method cannot be invoked until database file has been validated");
        }
        return dataSectionOffset;
    }

    /**
     * Gets the number of records in the database.
     *
     * @return The number of records.
     * @throws IllegalStateException If <code>validate</code> has not been called successfully before this method is
     *                               called.
     */
    public int getRecordCount() {
        if (!validated) {
            throw new IllegalStateException(
                    "method cannot be invoked until database file has been validated");
        }
        return recordCount;
    }
}
