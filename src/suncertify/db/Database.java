/*
 * Database.java
 *
 * 09 Oct 2007
 */

package suncertify.db;

import java.io.IOException;

/**
 * TODO
 * 
 * @author Richard Wardle
 */
public interface Database {

    /**
     * Reads a record from the file. Returns an array where each element is a
     * record value.
     * 
     * @param recNo
     * @return
     * @throws RecordNotFoundException
     * @throws IOException
     */
    String[] read(int recNo) throws RecordNotFoundException, IOException;

    /**
     * Modifies the fields of a record. The new value for field n appears in
     * data[n].
     * 
     * @param recNo
     * @param data
     * @throws RecordNotFoundException
     * @throws IOException
     */
    void update(int recNo, String[] data) throws RecordNotFoundException,
            IOException;

    /**
     * Deletes a record, making the record number and associated disk storage
     * available for reuse.
     * 
     * @param recNo
     * @throws RecordNotFoundException
     * @throws IOException
     */
    void delete(int recNo) throws RecordNotFoundException, IOException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field n in the database file is described by criteria[n]. A null value in
     * criteria[n] matches any field value. A non-null value in criteria[n]
     * matches any field value that begins with criteria[n]. (For example,
     * "Fred" matches "Fred" or "Freddy".)
     * 
     * @param criteria
     * @return
     * @throws IOException
     */
    int[] find(String[] criteria) throws IOException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry).
     * Inserts the given data, and returns the record number of the new record.
     * 
     * @param data
     * @return
     * @throws IOException
     */
    int create(String[] data) throws IOException;

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * If the specified record is already locked, the current thread gives up
     * the CPU and consumes no CPU cycles until the record is unlocked.
     * 
     * @param recNo
     * @throws RecordNotFoundException
     * @throws InterruptedException
     */
    void lock(int recNo) throws RecordNotFoundException, InterruptedException;

    /**
     * Releases the lock on a record.
     * 
     * @param recNo
     * @throws RecordNotFoundException
     */
    void unlock(int recNo) throws RecordNotFoundException;

    /**
     * Determines if a record is currently locked. Returns true if the record is
     * locked, false otherwise.
     * 
     * @param recNo
     * @return
     * @throws RecordNotFoundException
     */
    boolean isLocked(int recNo) throws RecordNotFoundException;
}