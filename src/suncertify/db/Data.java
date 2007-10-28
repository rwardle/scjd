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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import suncertify.db.DatabaseSchema.FieldDescription;

/**
 * @author Richard Wardle TODO Javadoc that any DataAccessExceptions will have
 *         an IOException as their root cause.
 */
class Data implements DBMain {

    // TODO Use a lock manager class?

    private static final Logger LOGGER = Logger.getLogger(Data.class.getName());
    private final DatabaseFile databaseFile;
    private final DatabaseSchema databaseSchema;
    private final long dataSectionOffset;

    // TODO Make these collections volatile and update them using this technique
    // http://www.ibm.com/developerworks/java/library/j-hashmap.html?ca=drs-
    private final SortedSet<Integer> deletedRecNos = Collections
            .synchronizedSortedSet(new TreeSet<Integer>());
    private final Map<Integer, Long> lockedRecords = Collections
            .synchronizedMap(new HashMap<Integer, Long>());

    // TODO This is the count of all records including deleted ones - consider
    // renaming this to make it clearer. Hard for a junior programmer to
    // understand volatile?
    private volatile int recordCount;

    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Integer, Condition> conditionsMap = new HashMap<Integer, Condition>();

    /**
     * Creates a new instance of <code>Data</code>.
     * 
     * @param databaseFile
     * @throws IllegalArgumentException
     *                 If the <code>databaseFile</code> is <code>null</code>.
     * @throws DataValidationException
     *                 If the database is invalid.
     * @throws IOException
     *                 If there is an error accessing the database.
     */
    public Data(DatabaseFile databaseFile) throws DataValidationException,
            IOException {
        if (databaseFile == null) {
            throw new IllegalArgumentException("databaseFile cannot be null");
        }

        this.databaseFile = databaseFile;
        this.databaseSchema = new DatabaseSchema();

        // TODO Move validation to a separate class?
        validateMagicCookie();
        validateSchema();
        this.dataSectionOffset = this.databaseFile.getFilePointer();
        this.recordCount = validateRecordCount();
        cacheDeletedRecordNumbers();
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
     * {@inheritDoc} <p/>
     * 
     * @throws DataAccessException
     *                 If there is an error accessing the database.
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);

        String record;
        synchronized (this.databaseFile) {
            if (isRecordDeleted(recNo)) {
                throw new RecordNotFoundException("Record " + recNo
                        + " has been deleted");
            }

            try {
                this.databaseFile.seek(getOffsetForRecord(recNo)
                        + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH);
                byte[] bytes = new byte[this.databaseSchema.getRecordLength()];
                this.databaseFile.readFully(bytes);
                record = new String(bytes, DatabaseConstants.CHARACTER_SET);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }

        return splitRecord(record);
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

    private String[] splitRecord(String record) {
        String[] recordValues = new String[this.databaseSchema.getFieldCount()];
        FieldDescription[] fieldDescriptions = this.databaseSchema
                .getFieldDescriptions();
        for (int i = 0; i < fieldDescriptions.length; i++) {
            recordValues[i] = record.substring(
                    fieldDescriptions[i].getRecordOffset(),
                    fieldDescriptions[i].getRecordOffset()
                            + fieldDescriptions[i].getLength()).trim();
        }
        return recordValues;
    }

    /**
     * TODO Mention that if a data element is null, the field is not udpated
     * {@inheritDoc} <p/>
     * 
     * @throws IllegalArgumentException
     *                 If <code>data</code> is <code>null</code> or is of
     *                 length not equal to the database schema field count.
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be updated.
     * @throws DataAccessException
     *                 If there is an error accessing the database.
     */
    public void update(int recNo, String[] data) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (data.length != this.databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException("data array must be of length: "
                    + this.databaseSchema.getFieldCount());
        }
        if (!isCurrentThreadHoldingLock(recNo)) {
            throw new IllegalStateException(
                    "Calling thread does not hold the lock on record " + recNo);
        }

        synchronized (this.databaseFile) {
            try {
                updateRecord(recNo, data);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
    }

    private boolean isCurrentThreadHoldingLock(int recNo) {
        Long threadIdHoldingLock = this.lockedRecords.get(recNo);
        return threadIdHoldingLock != null
                && threadIdHoldingLock.equals(Thread.currentThread().getId());
    }

    private void updateRecord(int recNo, String[] data) throws IOException {
        long recValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        FieldDescription[] fieldDescriptions = this.databaseSchema
                .getFieldDescriptions();
        for (int i = 0; i < fieldDescriptions.length; i++) {
            if (data[i] != null) {
                this.databaseFile.seek(recValuesStartPos
                        + fieldDescriptions[i].getRecordOffset());
                this.databaseFile.write(padOrTruncateData(data[i],
                        fieldDescriptions[i].getLength()).getBytes(
                        DatabaseConstants.CHARACTER_SET));
            }
        }
    }

    private String padOrTruncateData(String data, short fieldLength) {
        // TODO Start with a string of record length and then use StringBuilder
        // to replace the fields in
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
     * {@inheritDoc} TODO Mention that there is no need for clients to call
     * unlock after delete. <p/>
     * 
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be updated.
     * @throws DataAccessException
     *                 If there is an error accessing the database.
     */
    public void delete(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }
        if (!isCurrentThreadHoldingLock(recNo)) {
            throw new IllegalStateException(
                    "Calling thread does not hold the lock on record " + recNo);
        }

        synchronized (this.databaseFile) {
            try {
                this.databaseFile.seek(getOffsetForRecord(recNo));
                this.databaseFile
                        .writeByte(DatabaseConstants.DELETED_RECORD_FLAG);
                this.deletedRecNos.add(recNo);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
        unlockRecord(recNo);
    }

    /**
     * {@inheritDoc} <p/> This implementation does not throw
     * <code>RecordNotFoundException</code>.
     * 
     * @throws IllegalArgumentException
     *                 If <code>criteria</code> is <code>null</code> or is
     *                 of length not equal to the database schema field count.
     * @throws DataAccessException
     *                 If there is an error accessing the database.
     */
    public int[] find(String[] criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("criteria cannot be null");
        }
        if (criteria.length != this.databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException(
                    "criteria array must be of length: "
                            + this.databaseSchema.getFieldCount());
        }

        List<Integer> matchingRecNos = new ArrayList<Integer>();
        for (int recNo = 0; recNo < this.recordCount; recNo++) {
            try {
                if (!isRecordDeleted(recNo)) {
                    long recValuesStartPos = getOffsetForRecord(recNo)
                            + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
                    byte[] bytes;
                    synchronized (this.databaseFile) {
                        this.databaseFile.seek(recValuesStartPos);

                        bytes = new byte[this.databaseSchema.getRecordLength()];
                        this.databaseFile.readFully(bytes);
                    }

                    String record = new String(bytes,
                            DatabaseConstants.CHARACTER_SET);
                    FieldDescription[] fieldDescriptions = this.databaseSchema
                            .getFieldDescriptions();
                    for (int i = 0; i < fieldDescriptions.length; i++) {
                        if (criteria[i] != null) {
                            String recordValue = record.substring(
                                    fieldDescriptions[i].getRecordOffset(),
                                    fieldDescriptions[i].getRecordOffset()
                                            + fieldDescriptions[i].getLength());
                            if (!recordValue.startsWith(criteria[i])) {
                                break;
                            }
                        }

                        if (i == fieldDescriptions.length - 1) {
                            matchingRecNos.add(recNo);
                        }
                    }
                }
            } catch (IOException e) {
                throw new DataAccessException(e);
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
     * {@inheritDoc} <p/> This implementation does not throw
     * <code>DuplicateKeyException</code>.
     * 
     * @throws IllegalArgumentException
     *                 If <code>data</code> is <code>null</code> or is of
     *                 length not equal to the database schema field count or
     *                 contains a <code>null</code> value.
     * @throws DataAccessException
     *                 If there is an error accessing the database.
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
                writeRecord(data);
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

    private void writeRecord(String[] data) throws IOException {
        this.databaseFile.writeByte(DatabaseConstants.VALID_RECORD_FLAG);

        StringBuilder recordBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            recordBuilder.append(padOrTruncateData(data[i], this.databaseSchema
                    .getFieldDescriptions()[i].getLength()));
        }
        this.databaseFile.write(recordBuilder.toString().getBytes(
                DatabaseConstants.CHARACTER_SET));
    }

    /**
     * {@inheritDoc}
     * <p>
     * TODO Mention lock/operation/unlock on single thread contract.
     * 
     * @throws IllegalThreadStateException
     *                 If the calling thread is interrupted while waiting to
     *                 acquire the lock. The cause of this exception will be the
     *                 will be the original <code>InterruptedException</code>
     *                 which can be accessed via the <code>getCause</code>
     *                 method.
     */
    public void lock(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }

        this.lock.lock();
        try {
            Condition condition = this.conditionsMap.get(recNo);
            if (condition == null) {
                condition = this.lock.newCondition();
                this.conditionsMap.put(recNo, condition);
            }

            try {
                while (this.lockedRecords.containsKey(recNo)) {
                    condition.await();
                }
            } catch (InterruptedException e) {
                IllegalThreadStateException exception = new IllegalThreadStateException(
                        "Thread has been interrupted while waiting for the lock on record "
                                + recNo);
                exception.initCause(e);
                throw exception;
            }

            if (isRecordDeleted(recNo)) {
                condition.signal();
                throw new RecordNotFoundException("Record " + recNo
                        + " has been deleted");
            }

            this.lockedRecords.put(recNo, Thread.currentThread().getId());
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be unlocked.
     */
    public void unlock(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }

        if (isCurrentThreadHoldingLock(recNo)) {
            unlockRecord(recNo);
        } else {
            throw new IllegalStateException(
                    "Calling thread does not hold the lock on record " + recNo);
        }
    }

    private void unlockRecord(int recNo) {
        this.lock.lock();
        try {
            this.lockedRecords.remove(recNo);
            // TODO Could use signalAll here and still meet the spec
            // requirements
            this.conditionsMap.get(recNo).signal();
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLocked(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }
        return this.lockedRecords.containsKey(recNo);
    }
}
