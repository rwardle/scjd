/*
 * DatabaseImpl.java
 *
 * 09 Oct 2007
 */

package suncertify.db;

import java.io.IOException;

public final class DataAdapter implements Database {

    private final Data data;

    public DataAdapter(Data data) {
        this.data = data;
    }

    public String[] read(int recNo) throws RecordNotFoundException, IOException {
        try {
            return data.read(recNo);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    public void update(int recNo, String[] recordData)
            throws RecordNotFoundException, IOException {
        try {
            data.update(recNo, recordData);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    public void delete(int recNo) throws RecordNotFoundException, IOException {
        try {
            data.delete(recNo);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    public int[] find(String[] criteria) throws IOException {
        try {
            return data.find(criteria);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    public int create(String[] recordData) throws IOException {
        try {
            return data.create(recordData);
        } catch (DataAccessException e) {
            throw (IOException) e.getCause();
        }
    }

    public void lock(int recNo) throws RecordNotFoundException,
            InterruptedException {
        try {
            data.lock(recNo);
        } catch (IllegalThreadStateException e) {
            throw (InterruptedException) e.getCause();
        }
    }

    public void unlock(int recNo) throws RecordNotFoundException {
        data.unlock(recNo);
    }

    public boolean isLocked(int recNo) throws RecordNotFoundException {
        return data.isLocked(recNo);
    }
}
