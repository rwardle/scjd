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
public final class DatabaseFileImpl implements DatabaseFile {

    private final RandomAccessFile file;

    public DatabaseFileImpl(String databaseFilePath)
            throws FileNotFoundException {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException(
                    "databaseFilePath cannot be null");
        }
        if (!new File(databaseFilePath).exists()) {
            throw new FileNotFoundException("Database file does not exist at: "
                    + databaseFilePath);
        }

        // TODO Consider rws and rwd modes
        file = new RandomAccessFile(new File(databaseFilePath), "rw");
    }

    public long getFilePointer() throws IOException {
        return file.getFilePointer();
    }

    public long length() throws IOException {
        return file.length();
    }

    public void seek(long pos) throws IOException {
        file.seek(pos);
    }

    public byte readByte() throws IOException {
        return file.readByte();
    }

    public short readShort() throws IOException {
        return file.readShort();
    }

    public int readInt() throws IOException {
        return file.readInt();
    }

    public void readFully(byte[] b) throws IOException {
        file.readFully(b);
    }

    public void write(byte[] b) throws IOException {
        file.write(b);
    }

    public void writeByte(int v) throws IOException {
        file.writeByte(v);
    }
}
