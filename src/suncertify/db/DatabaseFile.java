/*
 * DatabaseFile.java
 *
 * 23 Aug 2007 
 */

package suncertify.db;

import java.io.IOException;

/**
 * A database file.
 *
 * @author Richard Wardle
 */
public interface DatabaseFile {

    /**
     * Returns the current offset in this file.
     *
     * @return The offset from the beginning of the file, in bytes, at which the
     *         next read or write occurs.
     * @throws IOException If an I/O error occurs.
     */
    long getFilePointer() throws IOException;

    /**
     * Returns the length of this file.
     *
     * @return The length of this file, measured in bytes.
     * @throws IOException If an I/O error occurs.
     */
    long length() throws IOException;

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs. The offset may be set beyond the
     * end of the file. Setting the offset beyond the end of the file does not
     * change the file length. The file length will change only by writing after
     * the offset has been set beyond the end of the file.
     *
     * @param pos The offset position, measured in bytes from the beginning
     *            of the file, at which to set the file pointer.
     * @throws IOException If <code>pos</code> is less than <code>0</code> or if
     *                     an I/O error occurs.
     */
    void seek(long pos) throws IOException;

    /**
     * Reads a signed eight-bit value from this file. This method reads a byte
     * from the file, starting from the current file pointer. If the byte read
     * is <code>b</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>, then the result
     * is: <blockquote>
     * <p/>
     * <pre>
     * (byte) (b)
     * </pre>
     * <p/>
     * </blockquote>
     * <p/>
     * This method blocks until the byte is read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return The next byte of this file as a signed eight-bit
     *         <code>byte</code>.
     * @throws IOException If an I/O error occurs.
     */
    byte readByte() throws IOException;

    /**
     * Reads a signed 16-bit number from this file. The method reads two bytes
     * from this file, starting at the current file pointer. If the two bytes
     * read, in order, are <code>b1</code> and <code>b2</code>, where each
     * of the two values is between <code>0</code> and <code>255</code>,
     * inclusive, then the result is equal to: <blockquote>
     * <p/>
     * <pre>
     * (short) ((b1 &lt;&lt; 8) | b2)
     * </pre>
     * <p/>
     * </blockquote>
     * <p/>
     * This method blocks until the two bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @return The next two bytes of this file, interpreted as a signed 16-bit
     *         number.
     * @throws IOException If an I/O error occurs.
     */
    short readShort() throws IOException;

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer. If the bytes read,
     * in order, are <code>b1</code>, <code>b2</code>, <code>b3</code>,
     * and <code>b4</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to: <blockquote>
     * <p/>
     * <pre>
     * (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre>
     * <p/>
     * </blockquote>
     * <p/>
     * This method blocks until the four bytes are read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return The next four bytes of this file, interpreted as an
     *         <code>int</code>.
     * @throws IOException If an I/O error occurs.
     */
    int readInt() throws IOException;

    /**
     * Reads <code>b.length</code> bytes from this file into the byte array,
     * starting at the current file pointer. This method reads repeatedly from
     * the file until the requested number of bytes are read. This method blocks
     * until the requested number of bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @param b The buffer into which the data is read. bytes.
     * @throws IOException If an I/O error occurs.
     */
    void readFully(byte[] b) throws IOException;

    /**
     * Writes <code>b.length</code> bytes from the specified byte array to
     * this file, starting at the current file pointer.
     *
     * @param b The data.
     * @throws IOException If an I/O error occurs.
     */
    void write(byte[] b) throws IOException;

    /**
     * Writes a <code>byte</code> to the file as a one-byte value. The write
     * starts at the current position of the file pointer.
     *
     * @param v A <code>byte</code> value to be written.
     * @throws IOException If an I/O error occurs.
     */
    void writeByte(int v) throws IOException;
}
