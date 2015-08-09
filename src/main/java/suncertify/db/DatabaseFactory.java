/*
 * DatabaseFactory.java
 *
 * 10 Oct 2007 
 */

package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Creates contractor databases.
 *
 * @author Richard Wardle
 */
public interface DatabaseFactory {

    /**
     * Creates a database.
     *
     * @param databaseFilePath Path to the database file.
     * @return The database.
     * @throws FileNotFoundException    If there is no file at <code>databaseFilePath</code>.
     * @throws DataValidationException  If the database file is invalid.
     * @throws IOException              If there is an error accessing the database file.
     * @throws IllegalArgumentException If <code>databaseFilePath</code> is <code>null</code>.
     */
    Database createDatabase(String databaseFilePath) throws FileNotFoundException,
            DataValidationException, IOException;
}
