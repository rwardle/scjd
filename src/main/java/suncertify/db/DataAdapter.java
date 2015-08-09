/*
 * DatabaseImpl.java
 *
 * 09 Oct 2007
 */

package suncertify.db;

import java.io.IOException;


/**
 * Adapts {@link Data} to the {@link Database} interface. The main purpose of this class is to map
 * the <code>DataAccessException</code>s thrown from <code>Data</code> into the
 * <code>IOException</code>s required by the <code>Database</code> interface.
 * 
 * @author Richard Wardle
 */
public final class DataAdapter implements Database {

    private final Data data;

    /**
     * Creates a new instance of <code>DataAdapter</code>.
     * 
     * @param data
     *            Data instance.
     * @throws IllegalArgumentException
     *             If <code>data</code> is <code>null</code>.
     */
    public DataAdapter(Data data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        this.data = data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#read(int)
     */
    public String[] read(int recNo) throws RecordNotFoundException, IOException {
        try {
            return data.read(recNo);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#update(int, String[])
     */
    public void update(int recNo, String[] recordData) throws RecordNotFoundException, IOException {
        try {
            data.update(recNo, recordData);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#delete(int)
     */
    public void delete(int recNo) throws RecordNotFoundException, IOException {
        try {
            data.delete(recNo);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#find(String[])
     */
    public int[] find(String[] criteria) throws IOException {
        try {
            return data.find(criteria);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#create(String[])
     */
    public int create(String[] recordData) throws IOException {
        try {
            return data.create(recordData);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#lock(int)
     */
    public void lock(int recNo) throws RecordNotFoundException, InterruptedException {
        try {
            data.lock(recNo);
        } catch (IllegalThreadStateException e) {
            throw (InterruptedException) e.getCause();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#unlock(int)
     */
    public void unlock(int recNo) throws RecordNotFoundException {
        data.unlock(recNo);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Data#isLocked(int)
     */
    public boolean isLocked(int recNo) throws RecordNotFoundException {
        return data.isLocked(recNo);
    }
}
