/*
 * DatabaseFile.java
 *
 * 23 Aug 2007 
 */

package suncertify.db;

import java.io.IOException;

/**
 * @author Richard Wardle
 */
public interface DatabaseFile {

    long getFilePointer() throws IOException;

    long length() throws IOException;

    void seek(long pos) throws IOException;

    byte readByte() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    void readFully(byte[] b) throws IOException;

    void write(byte[] b) throws IOException;

    void writeByte(int v) throws IOException;
}
