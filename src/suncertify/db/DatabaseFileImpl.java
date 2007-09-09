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
 * @author Richard Wardle
 */
public class DatabaseFileImpl implements DatabaseFile {

    private final RandomAccessFile file;

    public DatabaseFileImpl(String databaseFilePath)
            throws FileNotFoundException {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException(
                    "databaseFilePath cannot be null");
        }

        // TODO Consider rws and rwd modes
        this.file = new RandomAccessFile(new File(databaseFilePath), "rw");
    }

    public long getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public long length() throws IOException {
        return this.file.length();
    }

    public void seek(long pos) throws IOException {
        this.file.seek(pos);
    }

    public byte readByte() throws IOException {
        return this.file.readByte();
    }

    public short readShort() throws IOException {
        return this.file.readShort();
    }

    public int readInt() throws IOException {
        return this.file.readInt();
    }

    public void readFully(byte[] b) throws IOException {
        this.file.readFully(b);
    }

    public void write(byte[] b) throws IOException {
        this.file.write(b);
    }

    public void writeByte(int v) throws IOException {
        this.file.writeByte(v);
    }
}
