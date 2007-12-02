/*
 * Database.java
 *
 * 09 Oct 2007
 */

package suncertify.db;

import java.io.IOException;

/**
 * Interface defining methods for interacting with the contractor database.
 * Defines the same methods as {@link DBMain} but methods that interact with the
 * database file are declared to throw {@link IOException}. Clients should code
 * to this interface in preference to <code>DBMain</code>.
 * 
 * TODO Mention lock/action/unlock on same thread, maybe also in DBMain.
 * 
 * @author Richard Wardle
 */
public interface Database {

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
     * @throws IOException
     *                 If there is an error accessing the database.
     */
    String[] read(int recNo) throws RecordNotFoundException, IOException;

    /**
     * Modifies the fields of a record. The new value for field n appears in
     * data[n].
     * 
     * @param recNo
     *                Database record number.
     * @param data
     *                <code>String</code> array containing new record values.
     *                If <code>data[n]</code> is <code>null</code> field
     *                <code>n</code> will not be updated.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     * @throws IOException
     *                 If there is an error accessing the database.
     * @throws IllegalArgumentException
     *                 If <code>data</code> is <code>null</code> or is of
     *                 length not equal to the database schema field count.
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be updated.
     */
    void update(int recNo, String[] data) throws RecordNotFoundException,
            IOException;

    /**
     * Deletes a record, making the record number and associated disk storage
     * available for reuse. It is not necessary for clients to call
     * <code>unlock</code> after calling this method.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     * @throws IOException
     *                 If there is an error accessing the database.
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be updated.
     */
    void delete(int recNo) throws RecordNotFoundException, IOException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field <code>n</code> in the database file is described by
     * <code>criteria[n]</code>. A <code>null</code> value in
     * <code>criteria[n]</code> matches any field value. A non-<code>null</code>
     * value in <code>criteria[n]</code> matches any field value that begins
     * with <code>criteria[n]</code>. (For example, "Fred" matches "Fred" or
     * "Freddy".)
     * 
     * @param criteria
     *                <code>String</code> array containing search criteria.
     * @return An array of record numbers matching the specified criteria.
     * @throws IOException
     *                 If there is an error accessing the database.
     * @throws IllegalArgumentException
     *                 If <code>criteria</code> is <code>null</code> or is
     *                 of length not equal to the database schema field count.
     */
    int[] find(String[] criteria) throws IOException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry).
     * Inserts the given data, and returns the record number of the new record.
     * 
     * @param data
     *                <code>String</code> array containing new record values.
     * @return The record number of the new record.
     * @throws IOException
     *                 If there is an error accessing the database.
     * @throws IllegalArgumentException
     *                 If <code>data</code> is <code>null</code> or is of
     *                 length not equal to the database schema field count or
     *                 contains a <code>null</code> value.
     */
    int create(String[] data) throws IOException;

    /**
     * Locks a record so that it can only be updated or deleted by this thread.
     * If the specified record is already locked, the current thread gives up
     * the CPU and consumes no CPU cycles until the record is unlocked.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     * @throws InterruptedException
     *                 If the calling thread is interrupted while waiting to
     *                 acquire the lock.
     */
    void lock(int recNo) throws RecordNotFoundException, InterruptedException;

    /**
     * Releases the lock on a record.
     * 
     * @param recNo
     *                Database record number.
     * @throws RecordNotFoundException
     *                 If the specified record does not exist or is marked as
     *                 deleted in the database.
     * @throws IllegalStateException
     *                 If the calling thread does not hold the lock on the
     *                 record to be unlocked.
     */
    void unlock(int recNo) throws RecordNotFoundException;

    /**
     * Determines if a record is currently locked. Returns <code>true</code>
     * if the record is locked, <code>false</code> otherwise.
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
