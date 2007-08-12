/*
 * Data.java
 *
 * Created on 07-Jul-2007
 */

package suncertify.db;

/**
 * 
 * @author Richard Wardle
 */
public final class Data implements DBMain {

    private String databaseFilePath;
    
    /**
     * Creates a new instance of <code>Data</code>.
     * @param databaseFilePath 
     */
    public Data(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void update(int recNo, String[] data) throws RecordNotFoundException {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int recNo) throws RecordNotFoundException {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public int[] find(String[] criteria) throws RecordNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int create(String[] data) throws DuplicateKeyException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void lock(int recNo) throws RecordNotFoundException {
        // TODO Auto-generated method stub
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
