/*
 * Data.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import suncertify.db.DatabaseSchema.FieldDescription;

/**
 * 
 * @author Richard Wardle
 */
public final class Data implements DBMain {
    
    // TODO Make this a singleton?

    private final DatabaseFile databaseFile;
    private final DatabaseSchema databaseSchema;
    private final long dataSectionOffset;
    private final SortedSet<Integer> deletedRecNos = Collections
            .synchronizedSortedSet(new TreeSet<Integer>());
    private final Map<Integer, Long> lockMap = Collections
            .synchronizedMap(new HashMap<Integer, Long>());
    // TODO This is the count of all records including deleted ones - conside
    // renaming this to make it clearer. Should this be volatile?
    private volatile int recordCount;

    /**
     * Creates a new instance of <code>Data</code>.
     * 
     * @param databaseFilePath
     * @throws DataValidationException
     */
    public Data(DatabaseFile databaseFile) throws DataValidationException {
        if (databaseFile == null) {
            throw new IllegalArgumentException("databaseFile cannot be null");
        }

        this.databaseFile = databaseFile;
        this.databaseSchema = new DatabaseSchema();

        try {
            validateMagicCookie();
            validateSchema();
            this.dataSectionOffset = this.databaseFile.getFilePointer();
            this.recordCount = validateRecordCount();
            cacheDeletedRecordNumbers();
        } catch (IOException e) {
            throw new DataAccessException(e);
        }
    }

    private void validateMagicCookie() throws IOException,
            DataValidationException {
        int magicCookie = this.databaseFile.readInt();
        if (magicCookie != DatabaseConstants.MAGIC_COOKIE) {
            throw new DataValidationException("Invalid magic cookie: "
                    + magicCookie);
        }
    }

    private void validateSchema() throws IOException, DataValidationException {
        validateRecordLength();
        validateFieldCount();
        validateFieldDescriptions();
    }

    private void validateRecordLength() throws IOException,
            DataValidationException {
        int recordLength = this.databaseFile.readInt();
        if (recordLength != this.databaseSchema.getRecordLength()) {
            throw new DataValidationException("Invalid record length: "
                    + recordLength);
        }
    }

    private void validateFieldCount() throws IOException,
            DataValidationException {
        short fieldCount = this.databaseFile.readShort();
        if (fieldCount != this.databaseSchema.getFieldCount()) {
            throw new DataValidationException("Invalid field count: "
                    + fieldCount);
        }
    }

    private void validateFieldDescriptions() throws IOException,
            DataValidationException {
        FieldDescription[] fieldDescriptions = this.databaseSchema
                .getFieldDescriptions();
        for (FieldDescription fieldDescription : fieldDescriptions) {
            short fieldNameLength = this.databaseFile.readShort();
            if (fieldNameLength != fieldDescription.getName().length()) {
                throw new DataValidationException("Invalid field name length: "
                        + fieldNameLength + ", for field: "
                        + fieldDescription.getName());
            }

            byte[] bytes = new byte[fieldNameLength];
            this.databaseFile.readFully(bytes);
            String fieldName = new String(bytes,
                    DatabaseConstants.CHARACTER_SET);
            if (!fieldName.equals(fieldDescription.getName())) {
                throw new DataValidationException("Invalid field name: "
                        + fieldName + ", for field: "
                        + fieldDescription.getName());
            }

            short fieldLength = this.databaseFile.readShort();
            if (fieldLength != fieldDescription.getLength()) {
                throw new DataValidationException("Invalid field length: "
                        + fieldLength + ", for field: "
                        + fieldDescription.getName());
            }
        }
    }

    private int validateRecordCount() throws IOException,
            DataValidationException {
        long dataSectionLength = this.databaseFile.length()
                - this.dataSectionOffset;
        if (dataSectionLength
                % (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + this.databaseSchema
                        .getRecordLength()) != 0) {
            throw new DataValidationException(
                    "Data section length cannot contain a whole number of records: "
                            + dataSectionLength);
        }
        // TODO Record count should not be larger than max int
        return (int) (dataSectionLength / (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + this.databaseSchema
                .getRecordLength()));
    }

    private void cacheDeletedRecordNumbers() throws IOException {
        for (int recNo = 0; recNo < this.recordCount; recNo++) {
            this.databaseFile.seek(getOffsetForRecord(recNo));
            if (this.databaseFile.readByte() == DatabaseConstants.DELETED_RECORD_FLAG) {
                this.deletedRecNos.add(recNo);
            }
        }
    }

    DatabaseSchema getDatabaseSchema() {
        return this.databaseSchema;
    }

    long getDataSectionOffset() {
        return this.dataSectionOffset;
    }

    int getRecordCount() {
        return this.recordCount;
    }

    boolean isRecordDeleted(int recNo) {
        return this.deletedRecNos.contains(recNo);
    }

    /**
     * {@inheritDoc}
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);

        String[] recordValues = new String[this.databaseSchema.getFieldCount()];
        synchronized (this.databaseFile) {
            if (isRecordDeleted(recNo)) {
                throw new RecordNotFoundException("Record " + recNo
                        + " has been deleted");
            }

            try {
                this.databaseFile.seek(getOffsetForRecord(recNo)
                        + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH);
                // TODO Read all record in one go?
                FieldDescription[] fieldDescriptions = this.databaseSchema
                        .getFieldDescriptions();
                for (int i = 0; i < fieldDescriptions.length; i++) {
                    byte[] bytes = new byte[fieldDescriptions[i].getLength()];
                    this.databaseFile.readFully(bytes);
                    recordValues[i] = new String(bytes,
                            DatabaseConstants.CHARACTER_SET).trim();
                }
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }

        return recordValues;
    }

    private void validateRecordNumber(int recNo) throws RecordNotFoundException {
        if (recNo < 0 || recNo >= this.recordCount) {
            throw new RecordNotFoundException("Invalid record number: " + recNo);
        }
    }

    private long getOffsetForRecord(int recNo) {
        return this.dataSectionOffset
                + recNo
                * (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + this.databaseSchema
                        .getRecordLength());
    }

    /**
     * {@inheritDoc}
     */
    public void update(int recNo, String[] data) throws RecordNotFoundException {
        // TODO Check lock
        validateRecordNumber(recNo);
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (data.length != this.databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException("data array must be of length: "
                    + this.databaseSchema.getFieldCount());
        }
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }

        synchronized (this.databaseFile) {
            try {
                writeRecord(recNo, data);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
    }

    private void writeRecord(int recNo, String[] data) throws IOException {
        long recValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        FieldDescription[] fieldDescriptions = this.databaseSchema
                .getFieldDescriptions();
        for (int i = 0; i < fieldDescriptions.length; i++) {
            if (data[i] != null) {
                // TODO Check if file pointer is already at the correct location
                // before seeking
                this.databaseFile.seek(recValuesStartPos
                        + fieldDescriptions[i].getRecordOffset());
                this.databaseFile.write(padOrTruncateData(data[i],
                        fieldDescriptions[i].getLength()).getBytes(
                        DatabaseConstants.CHARACTER_SET));
            }
        }
    }

    private String padOrTruncateData(String data, short fieldLength) {
        String sizedData;
        if (data.length() < fieldLength) {
            StringBuilder builder = new StringBuilder(data);
            while (builder.length() < fieldLength) {
                builder.append(DatabaseConstants.PAD_CHARACTER);
            }
            sizedData = builder.toString();
        } else if (data.length() > fieldLength) {
            sizedData = data.substring(0, fieldLength);
        } else {
            sizedData = data;
        }
        return sizedData;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int recNo) throws RecordNotFoundException {
        // TODO Check lock
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }
        synchronized (this.databaseFile) {
            try {
                this.databaseFile.seek(getOffsetForRecord(recNo));
                this.databaseFile
                        .writeByte(DatabaseConstants.DELETED_RECORD_FLAG);
                this.deletedRecNos.add(recNo);
                // TODO add to record deleted cache
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does not throw <code>RecordNotFoundException</code>.
     */
    public int[] find(String[] criteria) throws RecordNotFoundException {
        if (criteria == null) {
            throw new IllegalArgumentException("criteria cannot be null");
        }
        if (criteria.length != this.databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException(
                    "criteria array must be of length: "
                            + this.databaseSchema.getFieldCount());
        }

        // TODO Should this be case insensitive?
        List<Integer> matchingRecNos = new ArrayList<Integer>();
        // TODO Should the sync block just be around the seek/read of an
        // individual record - i.e. we don't care if we read a record and it is
        // deleted before we return the find results. isRecordDeleted should
        // stay inside the sync block
        synchronized (this.databaseFile) {
            for (int recNo = 0; recNo < this.recordCount; recNo++) {
                try {
                    if (!isRecordDeleted(recNo)) {
                        FieldDescription[] fieldDescriptions = this.databaseSchema
                                .getFieldDescriptions();
                        long recValuesStartPos = getOffsetForRecord(recNo)
                                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
                        for (int i = 0; i < fieldDescriptions.length; i++) {
                            if (criteria[i] != null) {
                                this.databaseFile.seek(recValuesStartPos
                                        + fieldDescriptions[i]
                                                .getRecordOffset());

                                byte[] b = new byte[fieldDescriptions[i]
                                        .getLength()];
                                this.databaseFile.readFully(b);

                                if (!new String(b,
                                        DatabaseConstants.CHARACTER_SET)
                                        .startsWith(criteria[i])) {
                                    break;
                                }

                                if (i == fieldDescriptions.length - 1) {
                                    matchingRecNos.add(recNo);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new DataAccessException(e);
                }
            }
        }

        int[] recNosArray = new int[matchingRecNos.size()];
        int index = 0;
        for (Integer recNo : matchingRecNos) {
            recNosArray[index] = recNo;
            index++;
        }
        return recNosArray;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does not throw <code>DuplicateKeyException</code>.
     */
    public int create(String[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (data.length != this.databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException("data array must be of length: "
                    + this.databaseSchema.getFieldCount());
        }
        for (String element : data) {
            if (element == null) {
                throw new IllegalArgumentException(
                        "record values must be non-null");
            }
        }

        int recNoToWrite;
        synchronized (this.databaseFile) {
            if (this.deletedRecNos.isEmpty()) {
                recNoToWrite = this.recordCount;
            } else {
                recNoToWrite = this.deletedRecNos.first();
            }

            try {
                this.databaseFile.seek(getOffsetForRecord(recNoToWrite));
                // TODO Write record in one go otherwise we could mark this
                // record as valid and then have junk afterwards
                this.databaseFile
                        .writeByte(DatabaseConstants.VALID_RECORD_FLAG);
                writeRecord(recNoToWrite, data);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }

            if (recNoToWrite == this.recordCount) {
                this.recordCount++;
            } else {
                this.deletedRecNos.remove(recNoToWrite);
            }
        }

        return recNoToWrite;
    }

    /**
     * {@inheritDoc}
     * <p>
     * TODO Mention lock/operation/unlock on single thread contract.
     */
    public void lock(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        synchronized (this.lockMap) {
            while (this.lockMap.containsKey(recNo)) {
                try {
                    this.lockMap.wait();
                } catch (InterruptedException e) {
                    // TODO Find out how to handle this
                    Thread.currentThread().interrupt();
                }
            }

            if (isRecordDeleted(recNo)) {
                throw new RecordNotFoundException("Record " + recNo
                        + " has been deleted");
            }

            this.lockMap.put(recNo, Thread.currentThread().getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unlock(int recNo) throws RecordNotFoundException {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLocked(int recNo) throws RecordNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }
}
