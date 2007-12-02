/*
 * DatabaseFactoryImpl.java
 *
 * 10 Oct 2007 
 */

package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseFactoryImpl implements DatabaseFactory {

    public DatabaseFactoryImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Database createDatabase(String databaseFilePath)
            throws FileNotFoundException, DataValidationException, IOException {
        return new DataAdapter(new Data(new DatabaseFileImpl(databaseFilePath)));
    }
}
