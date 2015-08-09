/*
 * DBMain.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

/**
 * Interface defining methods for interacting with a contractor database. Clients should code to the
 * {@link Database} interface in preference to this one (see <code>suncertify.db</code> package
 * documentation).
 * <p/>
 * A logical record locking system is employed to ensure that clients cannot corrupt the database
 * through simultaneous operations that modify the same record. The methods that modify the database
 * are <code>update</code> and <code>delete</code>.
 * <p/>
 * Clients that want to call the <code>update</code> method for a record must first call the
 * <code>lock</code> method to prevent any other client from operating on this record. After the
 * <code>update</code> method has completed the client must then call the <code>unlock</code> method
 * to make this record available for modification by other clients. Note that the <code>lock</code>,
 * <code>update</code>, <code>unlock</code> method call sequence must be made on the same thread of
 * execution.
 * <p/>
 * Clients that want to call the <code>delete</code> method for a record must first call the
 * <code>lock</code> method to prevent any other client from operating on this record. Clients need
 * NOT call the <code>unlock</code> method after the <code>delete</code> method has completed, doing
 * so will throw a <code>RecordNotFoundException</code> since the record will have been deleted.
 * Note that the <code>lock</code>, <code>delete</code> method call sequence must be made on the
 * same thread of execution.
 * 
 * @author Richard Wardle
 */
public interface DBMain {

    /**
     * Reads a record from the file. Returns an array where each element is a record value.
     * 
     * @param recNo
     *            Database record number.
     * @return A <code>String</code> array containing the record values.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws DataAccessException
     *             If there is an error accessing the database.
     */
    String[] read(int recNo) throws RecordNotFoundException;

    /**
     * Modifies the fields of a record. The new value for field n appears in data[n]. The calling
     * thread must hold the lock on the record to be updated.
     * 
     * @param recNo
     *            Database record number.
     * @param data
     *            <code>String</code> array containing new record values. If <code>data[n]</code> is
     *            <code>null</code> field <code>n</code> will not be updated.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws IllegalArgumentException
     *             If <code>data</code> is <code>null</code> or is of length not equal to the
     *             database schema field count.
     * @throws IllegalStateException
     *             If the calling thread does not hold the lock on the record to be updated.
     * @throws DataAccessException
     *             If there is an error accessing the database.
     */
    void update(int recNo, String[] data) throws RecordNotFoundException;

    /**
     * Deletes a record, making the record number and associated disk storage available for reuse.
     * The calling thread must hold the lock on the record to be deleted. It is not necessary to
     * call <code>unlock</code> after calling this method.
     * 
     * @param recNo
     *            Database record number.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws IllegalStateException
     *             If the calling thread does not hold the lock on the record to be updated.
     * @throws DataAccessException
     *             If there is an error accessing the database.
     */
    void delete(int recNo) throws RecordNotFoundException;

    /**
     * Returns an array of record numbers that match the specified criteria. Field <code>n</code> in
     * the database file is described by <code>criteria[n]</code>. A <code>null</code> value in
     * <code>criteria[n]</code> matches any field value. A non-<code>null</code> value in
     * <code>criteria[n]</code> matches any field value that begins with <code>criteria[n]</code>.
     * (For example, "Fred" matches "Fred" or "Freddy".)
     * 
     * @param criteria
     *            <code>String</code> array containing search criteria.
     * @return An array of record numbers matching the specified criteria.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws IllegalArgumentException
     *             If <code>criteria</code> is <code>null</code> or is of length not equal to the
     *             database schema field count.
     * @throws DataAccessException
     *             If there is an error accessing the database.
     */
    int[] find(String[] criteria) throws RecordNotFoundException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry). Inserts the given
     * data, and returns the record number of the new record.
     * 
     * @param data
     *            <code>String</code> array containing new record values.
     * @return The record number of the new record.
     * @throws DuplicateKeyException
     *             If there is a duplicate key.
     * @throws IllegalArgumentException
     *             If <code>data</code> is <code>null</code> or is of length not equal to the
     *             database schema field count or contains a <code>null</code> value.
     * @throws DataAccessException
     *             If there is an error accessing the database.
     */
    int create(String[] data) throws DuplicateKeyException;

    /**
     * Locks a record so that it can only be updated or deleted by this thread. If the specified
     * record is already locked, the current thread gives up the CPU and consumes no CPU cycles
     * until the record is unlocked.
     * 
     * @param recNo
     *            Database record number.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws IllegalThreadStateException
     *             If the calling thread is interrupted while waiting to acquire the lock.
     */
    void lock(int recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. The calling thread must hold the lock on the record to be
     * unlocked.
     * 
     * @param recNo
     *            Database record number.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     * @throws IllegalStateException
     *             If the calling thread does not hold the lock on the record to be unlocked.
     */
    void unlock(int recNo) throws RecordNotFoundException;

    /**
     * Determines if a record is currently locked. Returns <code>true</code> if the record is
     * locked, <code>false</code> otherwise.
     * 
     * @param recNo
     *            Database record number.
     * @return <code>true</code> if the record is locked, <code>false</code> otherwise.
     * @throws RecordNotFoundException
     *             If the specified record does not exist or is marked as deleted in the database.
     */
    boolean isLocked(int recNo) throws RecordNotFoundException;
}
