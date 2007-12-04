/*
 * DBMain.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

/**
 * Interface defining methods for interacting with a contractor database.
 * 
 * @author Richard Wardle
 */
public interface DBMain {

    /**
     * Reads a record from the file. Returns an array where each element is a
     * record value.
     * 
     * @param recNo
     *                Database record number.
     * @return A <code>String</code> array containing the record values.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    String[] read(int recNo) throws RecordNotFoundException;

    /**
     * Modifies the fields of a record. The new value for field n appears in
     * data[n].
     * 
     * @param recNo
     *                Database record number.
     * @param data
     *                <code>String</code> array containing new record values.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    void update(int recNo, String[] data) throws RecordNotFoundException;

    /**
     * Deletes a record, making the record number and associated disk storage
     * available for reuse.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    void delete(int recNo) throws RecordNotFoundException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field n in the database file is described by criteria[n]. A null value in
     * criteria[n] matches any field value. A non-null value in criteria[n]
     * matches any field value that begins with criteria[n]. (For example,
     * "Fred" matches "Fred" or "Freddy".)
     * 
     * @param criteria
     *                <code>String</code> array containing search criteria.
     * @return An array of record numbers matching the specified criteria.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    int[] find(String[] criteria) throws RecordNotFoundException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry).
     * Inserts the given data, and returns the record number of the new record.
     * 
     * @param data
     *                <code>String</code> array containing new record values.
     * @return The record number of the new record.
     * @throws DuplicateKeyException
     *                 If there is a duplicate key.
     */
    int create(String[] data) throws DuplicateKeyException;

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * If the specified record is already locked, the current thread gives up
     * the CPU and consumes no CPU cycles until the record is unlocked.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    void lock(int recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    void unlock(int recNo) throws RecordNotFoundException;

    /**
     * Determines if a record is currently locked. Returns true if the record is
     * locked, false otherwise.
     * 
     * @param recNo
     *                Database record number.
     * @return <code>true</code> if the record is locked, <code>false</code>
     *         otherwise.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     */
    boolean isLocked(int recNo) throws RecordNotFoundException;
}
