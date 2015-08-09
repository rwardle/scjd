/*
 * DatabaseFileImpl.java
 *
 * 23 Aug 2007 
 */

package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Implementation of {@link DatabaseFile} that delegates to {@link RandomAccessFile}.
 * 
 * @author Richard Wardle
 */
public final class DatabaseFileImpl implements DatabaseFile {

    private final RandomAccessFile file;

    /**
     * Creates a new instance of <code>DatabaseFileImpl</code>.
     * 
     * @param databaseFilePath
     *            Path to the database file.
     * @throws IllegalArgumentException
     *             If <code>databaseFilePath</code> is <code>null</code>.
     * @throws FileNotFoundException
     *             If the database file cannot be found.
     */
    public DatabaseFileImpl(String databaseFilePath) throws FileNotFoundException {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException("databaseFilePath cannot be null");
        }
        if (!new File(databaseFilePath).exists()) {
            throw new FileNotFoundException("Database file does not exist at: " + databaseFilePath);
        }

        file = new RandomAccessFile(new File(databaseFilePath), "rw");
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#getFilePointer()
     */
    public long getFilePointer() throws IOException {
        return file.getFilePointer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#length()
     */
    public long length() throws IOException {
        return file.length();
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#seek(long)
     */
    public void seek(long pos) throws IOException {
        file.seek(pos);
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#readByte()
     */
    public byte readByte() throws IOException {
        return file.readByte();
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#readShort()
     */
    public short readShort() throws IOException {
        return file.readShort();
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#readInt()
     */
    public int readInt() throws IOException {
        return file.readInt();
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#readFully(byte[])
     */
    public void readFully(byte[] b) throws IOException {
        file.readFully(b);
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        file.write(b);
    }

    /**
     * {@inheritDoc}
     * 
     * @see RandomAccessFile#writeByte(int)
     */
    public void writeByte(int v) throws IOException {
        file.writeByte(v);
    }
}
