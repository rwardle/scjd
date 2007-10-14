/*
 * DatabaseFactory.java
 *
 * 10 Oct 2007 
 */

package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Richard Wardle
 */
public interface DatabaseFactory {

    Database createDatabase(String databaseFilePath)
            throws FileNotFoundException, DataValidationException, IOException;
}
