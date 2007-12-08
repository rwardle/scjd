/*
 * Data.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * <p>
 * This class synchronizes access to the supplied <code>databaseFile</code> to
 * prevent corruption by concurrent operations from multiple threads. To ensure
 * database integrity, users of the class must ensure that no operations are
 * called on the supplied <code>databaseFile</code> externally to this class.
 * 
 * @author Richard Wardle
 */
public class Data implements DBMain {

    private static final Logger LOGGER = Logger.getLogger(Data.class.getName());

    /*
     * Methods called on this <code>databaseFile</code> should be synchronized
     * on the <code>databaseFile</code> instance to prevent multiple threads
     * from modifying the file pointer concurrently.
     */
    private final DatabaseFile databaseFile;

    // Describes the structure of the database
    private final DatabaseSchema databaseSchema;

    /*
     * Offset of the data section (where the records start) in the database
     * file.
     */
    private final long dataSectionOffset;

    /*
     * Sorted set of the record numbers in the database that have been marked as
     * deleted. Modification of the set is synchronized on databaseFile but
     * reads may happen concurrently, so the set is created as synchronized to
     * prevent corrupted reads.
     */
    private final SortedSet<Integer> deletedRecNos = Collections
            .synchronizedSortedSet(new TreeSet<Integer>());

    /*
     * Mutual exclusion lock used for acquiring the logical record lock on
     * database records. Multiple <code>Condition</code>objects are used with
     * this lock, one for each record in the database. This allows fine-grained
     * signal operations when unlocking a record, i.e. only waking up threads
     * that are waiting for the lock on that particular record.
     */
    private final ReentrantLock lock = new ReentrantLock();

    // Map of record numbers and <code>Condition</code> objects
    private final Map<Integer, Condition> conditionsMap = new HashMap<Integer, Condition>();

    /*
     * Map of record numbers and the corresponding IDs of the threads that
     * currently hold the lock on those records. Modification of the map is
     * synchronized on the ReentrantLock lock but reads may happen concurrently,
     * so the map is created as synchronized to prevent corrupted reads.
     */
    private final Map<Integer, Long> lockedRecords = Collections
            .synchronizedMap(new HashMap<Integer, Long>());

    /*
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
     * @throws DataValidationException
     *                 If the database is invalid.
     * @throws IOException
     *                 If there is an error accessing the database.
     * @throws IllegalArgumentException
     *                 If the <code>databaseFile</code> is <code>null</code>.
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

        /*
         * Store a set of the deleted record numbers - allows checking if a
         * record is deleted without accessing the database file.
         */
        cacheDeletedRecordNumbers();
    }

    private void cacheDeletedRecordNumbers() throws IOException {
        for (int recNo = 0; recNo < recordCount; recNo++) {
            databaseFile.seek(getOffsetForRecord(recNo));
            if (databaseFile.readByte() == DatabaseConstants.DELETED_RECORD_FLAG) {
                deletedRecNos.add(recNo);
            }
        }
        LOGGER.info("Database contains " + deletedRecNos.size()
                + " deleted records");
    }

    final boolean isRecordDeleted(int recNo) {
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

    /** {@inheritDoc} */
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

    // Returns the offset of the record in bytes from the start of the file
    private long getOffsetForRecord(int recNo) {
        long recordLengthWithFlag = DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH
                + databaseSchema.getRecordLength();
        return dataSectionOffset + recNo * recordLengthWithFlag;
    }

    /*
     * Splits a record into its separate field values. Any whitespace at the end
     * of a field value is trimmed.
     */
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

    /** {@inheritDoc} */
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
                LOGGER.info("Updated record " + recNo + " with: "
                        + Arrays.toString(data));
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
            // Don't update fields where the data element is null
            if (data[i] != null) {
                databaseFile.seek(recValuesStartPos
                        + fieldDescriptions[i].getRecordOffset());

                // Pad or truncate the data to fit the field
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

    /** {@inheritDoc} */
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

        // The record is deleted just by writing the deleted record flag
        synchronized (databaseFile) {
            try {
                databaseFile.seek(getOffsetForRecord(recNo));
                databaseFile.writeByte(DatabaseConstants.DELETED_RECORD_FLAG);
                deletedRecNos.add(recNo);
                LOGGER.info("Deleted record " + recNo);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }

        /*
         * Now the record is deleted it needs to be unlocked since an external
         * call to the unlock method will now throw RecordNotFoundException..
         */
        unlockRecord(recNo);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does not throw <code>RecordNotFoundException</code>.
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

        // Loop over all the database records to find matches
        List<Integer> matchingRecNos = new ArrayList<Integer>();
        for (int recNo = 0; recNo < recordCount; recNo++) {
            try {
                // Don't try to match deleted records
                if (!isRecordDeleted(recNo)) {
                    long recValuesStartPos = getOffsetForRecord(recNo)
                            + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
                    byte[] bytes;
                    synchronized (databaseFile) {
                        databaseFile.seek(recValuesStartPos);

                        // Read the record from the database
                        bytes = new byte[databaseSchema.getRecordLength()];
                        databaseFile.readFully(bytes);
                    }

                    String record = new String(bytes,
                            DatabaseConstants.CHARACTER_SET);
                    if (isMatchingRecord(criteria, record)) {
                        // If the record matches add it to the list
                        matchingRecNos.add(recNo);
                    }
                }
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }

        // Convert the list of matching record numbers to an array
        int[] recNosArray = new int[matchingRecNos.size()];
        int index = 0;
        for (Integer recNo : matchingRecNos) {
            recNosArray[index] = recNo;
            index++;
        }

        LOGGER.info("Found " + recNosArray.length
                + " records matching criteria: " + Arrays.toString(criteria));
        return recNosArray;
    }

    private boolean isMatchingRecord(String[] criteria, String record) {
        boolean match = false;

        // Check each field against the criteria using a "startsWith" comparison
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
            /*
             * If there are any deleted records, use the first available as the
             * location to write the new record to. If not, write it to the end
             * of the file.
             */
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

            /*
             * If the record was written to the end of the file, increment the
             * record count. If not, remove the record number of the new record
             * from the set of deleted record numbers.
             */
            if (recNoToWrite == recordCount) {
                recordCount++;
            } else {
                deletedRecNos.remove(recNoToWrite);
            }
        }

        LOGGER.info("Created record at recNo: " + recNoToWrite
                + ", with data: " + Arrays.toString(data));
        return recNoToWrite;
    }

    private void writeRecord(String[] data) throws IOException {
        databaseFile.writeByte(DatabaseConstants.VALID_RECORD_FLAG);

        /*
         * Build up the complete record as a string and write it out in one
         * operation. Each field is padded or truncated as appropriate.
         */
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
     * <p>
     * The cause of the <code>IllegalThreadStateException</code> thrown from
     * this method will be the original <code>InterruptedException</code>
     * which can be accessed via the <code>getCause</code> method.
     */
    public void lock(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);

        String deletedMessage = "Record " + recNo + " has been deleted";
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException(deletedMessage);
        }

        lock.lock();
        try {
            // Get or create a lock condition for the record to be locked
            Condition condition = conditionsMap.get(recNo);
            if (condition == null) {
                condition = lock.newCondition();
                conditionsMap.put(recNo, condition);
            }

            try {
                while (lockedRecords.containsKey(recNo)) {
                    // If the record is currently locked, wait on the condition
                    LOGGER.info("Thread with ID="
                            + Thread.currentThread().getId()
                            + " waiting for lock on record: " + recNo);
                    condition.await();
                }
            } catch (InterruptedException e) {
                // Thread has been interrupted, throw a runtime exception
                String message = "Thread with ID="
                        + Thread.currentThread().getId()
                        + " has been interrupted while waiting for lock on record: "
                        + recNo;
                LOGGER.info(message);
                IllegalThreadStateException exception = new IllegalThreadStateException(
                        message);
                exception.initCause(e);
                throw exception;
            }

            LOGGER.info("Thread with ID=" + Thread.currentThread().getId()
                    + " acquired lock on record: " + recNo);

            if (isRecordDeleted(recNo)) {
                /*
                 * Record is deleted, wake another thread waiting on the
                 * condition for this record and throw an exception.
                 */
                LOGGER.info(deletedMessage + ", thread with ID="
                        + Thread.currentThread().getId()
                        + " released lock on record: " + recNo);
                condition.signal();
                throw new RecordNotFoundException(deletedMessage);
            }

            // Store the ID of the thread that has locked this record
            lockedRecords.put(recNo, Thread.currentThread().getId());
        } finally {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    public void unlock(int recNo) throws RecordNotFoundException {
        validateRecordNumber(recNo);
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record " + recNo
                    + " has been deleted");
        }

        // Can't unlock unless the thread is currently holding the lock
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
            // Remove the entry for this record from locked records map
            lockedRecords.remove(recNo);

            // Wake a thread that is waiting for the lock on this record
            LOGGER.info("Thread with ID=" + Thread.currentThread().getId()
                    + " released lock on record: " + recNo);
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
