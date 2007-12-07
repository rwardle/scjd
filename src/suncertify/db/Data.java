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
 * Implementation of {@link DBMain} that obtains contractor data using an
 * implementation of {@link DatabaseFile}. Any <code>DataAccessException</code>s
 * thrown by the methods of this class will have an <code>IOException</code>
 * as their root cause.
 * 
 * @author Richard Wardle
 */
public class Data implements DBMain {

    private static final Logger LOGGER = Logger.getLogger(Data.class.getName());

    private final DatabaseFile databaseFile;
    private final DatabaseSchema databaseSchema;

    /**
     * Offset of the data section (where the records start) in the database
     * file.
     */
    private final long dataSectionOffset;

    /**
     * Sorted set of the record numbers in the database that have been marked as
     * deleted.
     */
    private final SortedSet<Integer> deletedRecNos = Collections
            .synchronizedSortedSet(new TreeSet<Integer>());

    /**
     * Mutual exclusion lock used for acquiring the logical record lock on
     * database records. Multiple <code>Condition</code>objects are used with
     * this lock, one for each record in the database. This allows fine-grained
     * signal operations when unlocking a record, i.e. only waking up threads
     * that are waiting for the lock on that particular record.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /** Map of record numbers and <code>Condition</code> objects. */
    private final Map<Integer, Condition> conditionsMap = new HashMap<Integer, Condition>();

    /**
     * Map of record numbers and the corresponding IDs of the threads that
     * currently hold the lock on those records.
     */
    private final Map<Integer, Long> lockedRecords = Collections
            .synchronizedMap(new HashMap<Integer, Long>());

    /**
     * Number of records in the database, including deleted records.
     * Modification to this field is synchronized on <code>databaseFile</code>.
     * Reads are not synchronized but the field is marked <code>volatile</code>
     * to ensure that threads see the most up-to-date data for the field.
     */
    private volatile int recordCount;

    /**
     * Creates a new instance of <code>Data</code> using the specified
     * database file.
     * 
     * @param databaseFile
     *                Database file.
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
        databaseSchema = new DatabaseSchema();

        DatabaseFileValidator validator = new DatabaseFileValidator(
                this.databaseFile, databaseSchema);
        validator.validate();
        dataSectionOffset = validator.getDataSectionOffset();
        recordCount = validator.getRecordCount();

        cacheDeletedRecordNumbers();
    }

    private void cacheDeletedRecordNumbers() throws IOException {
        for (int recNo = 0; recNo < recordCount; recNo++) {
            databaseFile.seek(getOffsetForRecord(recNo));
            if (databaseFile.readByte() == DatabaseConstants.DELETED_RECORD_FLAG) {
                deletedRecNos.add(recNo);
            }
        }
    }

    /**
     * Reports whether the specified record has been deleted.
     * 
     * @param recNo
     *                Database record number.
     * @return <code>true</code> if the record has been deleted,
     *         <code>false</code> otherwise.
     */
    public final boolean isRecordDeleted(int recNo) {
        return deletedRecNos.contains(recNo);
    }

    final DatabaseSchema getDatabaseSchema() {
        return databaseSchema;
    }

    final long getDataSectionOffset() {
        return dataSectionOffset;
    }

    final int getRecordCount() {
        return recordCount;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws DataAccessException
     *                 If there is an error accessing the database.
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);

        String record;
        synchronized (databaseFile) {
            if (isRecordDeleted(recNo)) {
                throw new RecordNotFoundException("Record " + recNo
                        + " has been deleted");
            }

            try {
                databaseFile.seek(getOffsetForRecord(recNo)
                        + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH);
                byte[] bytes = new byte[databaseSchema.getRecordLength()];
                databaseFile.readFully(bytes);
                record = new String(bytes, DatabaseConstants.CHARACTER_SET);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }

        return splitRecord(record);
    }

    private void validateRecordNumber(int recNo) throws RecordNotFoundException {
        if (recNo < 0 || recNo >= recordCount) {
            throw new RecordNotFoundException("Invalid record number: " + recNo);
        }
    }

    private long getOffsetForRecord(int recNo) {
        long recordLengthWithFlag = DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH
                + databaseSchema.getRecordLength();
        return dataSectionOffset + recNo * recordLengthWithFlag;
    }

    private String[] splitRecord(String record) {
        String[] recordValues = new String[databaseSchema.getFieldCount()];
        FieldDescription[] fieldDescriptions = databaseSchema
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
     * {@inheritDoc}
     * <p>
     * If <code>data[n]</code> is <code>null</code> field <code>n</code>
     * will not be updated.
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
        if (data.length != databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException("data array must be of length: "
                    + databaseSchema.getFieldCount());
        }
        if (!isCurrentThreadHoldingLock(recNo)) {
            throw new IllegalStateException(
                    "Calling thread does not hold the lock on record " + recNo);
        }

        synchronized (databaseFile) {
            try {
                updateRecord(recNo, data);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
    }

    private boolean isCurrentThreadHoldingLock(int recNo) {
        Long threadIdHoldingLock = lockedRecords.get(recNo);
        return threadIdHoldingLock != null
                && threadIdHoldingLock.equals(Thread.currentThread().getId());
    }

    private void updateRecord(int recNo, String[] data) throws IOException {
        long recValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        FieldDescription[] fieldDescriptions = databaseSchema
                .getFieldDescriptions();
        for (int i = 0; i < fieldDescriptions.length; i++) {
            if (data[i] != null) {
                databaseFile.seek(recValuesStartPos
                        + fieldDescriptions[i].getRecordOffset());
                databaseFile.write(padOrTruncateData(data[i],
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
     * <p>
     * It is not necessary for clients to call <code>unlock</code> after
     * calling this method.
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

        synchronized (databaseFile) {
            try {
                databaseFile.seek(getOffsetForRecord(recNo));
                databaseFile.writeByte(DatabaseConstants.DELETED_RECORD_FLAG);
                deletedRecNos.add(recNo);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
        unlockRecord(recNo);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does not throw <code>RecordNotFoundException</code>.
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
        if (criteria.length != databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException(
                    "criteria array must be of length: "
                            + databaseSchema.getFieldCount());
        }

        List<Integer> matchingRecNos = new ArrayList<Integer>();
        for (int recNo = 0; recNo < recordCount; recNo++) {
            try {
                if (!isRecordDeleted(recNo)) {
                    long recValuesStartPos = getOffsetForRecord(recNo)
                            + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
                    byte[] bytes;
                    synchronized (databaseFile) {
                        databaseFile.seek(recValuesStartPos);

                        bytes = new byte[databaseSchema.getRecordLength()];
                        databaseFile.readFully(bytes);
                    }

                    String record = new String(bytes,
                            DatabaseConstants.CHARACTER_SET);
                    if (isMatchingRecord(criteria, record)) {
                        matchingRecNos.add(recNo);
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

    private boolean isMatchingRecord(String[] criteria, String record) {
        boolean match = false;

        FieldDescription[] fieldDescriptions = databaseSchema
                .getFieldDescriptions();
        for (int i = 0; i < fieldDescriptions.length; i++) {
            if (criteria[i] != null) {
                String recordValue = record.substring(fieldDescriptions[i]
                        .getRecordOffset(), fieldDescriptions[i]
                        .getRecordOffset()
                        + fieldDescriptions[i].getLength());
                if (!recordValue.startsWith(criteria[i])) {
                    // Criteria element doesn't match so break out of the loop
                    break;
                }
            }

            if (i == fieldDescriptions.length - 1) {
                // Reached the last criteria element so we must have a match
                match = true;
            }
        }

        return match;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does not throw <code>DuplicateKeyException</code>.
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
        if (data.length != databaseSchema.getFieldCount()) {
            throw new IllegalArgumentException("data array must be of length: "
                    + databaseSchema.getFieldCount());
        }
        for (String element : data) {
            if (element == null) {
                throw new IllegalArgumentException(
                        "record values cannot be null");
            }
        }

        int recNoToWrite;
        synchronized (databaseFile) {
            if (deletedRecNos.isEmpty()) {
                recNoToWrite = recordCount;
            } else {
                recNoToWrite = deletedRecNos.first();
            }

            try {
                databaseFile.seek(getOffsetForRecord(recNoToWrite));
                writeRecord(data);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }

            if (recNoToWrite == recordCount) {
                recordCount++;
            } else {
                deletedRecNos.remove(recNoToWrite);
            }
        }

        return recNoToWrite;
    }

    private void writeRecord(String[] data) throws IOException {
        databaseFile.writeByte(DatabaseConstants.VALID_RECORD_FLAG);

        StringBuilder recordBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            recordBuilder.append(padOrTruncateData(data[i], databaseSchema
                    .getFieldDescriptions()[i].getLength()));
        }
        databaseFile.write(recordBuilder.toString().getBytes(
                DatabaseConstants.CHARACTER_SET));
    }

    /**
     * {@inheritDoc}
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

        lock.lock();
        try {
            Condition condition = conditionsMap.get(recNo);
            if (condition == null) {
                condition = lock.newCondition();
                conditionsMap.put(recNo, condition);
            }

            try {
                while (lockedRecords.containsKey(recNo)) {
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

            lockedRecords.put(recNo, Thread.currentThread().getId());
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            lockedRecords.remove(recNo);
            // Wake a thread that is waiting for the lock on this record
            conditionsMap.get(recNo).signal();
        } finally {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    public boolean isLocked(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }
        return lockedRecords.containsKey(recNo);
    }
}
