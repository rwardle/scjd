/*
 * DatabaseFactoryImpl.java
 *
 * 10 Oct 2007 
 */

package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of {@link DatabaseFactory}.
 * 
 * @author Richard Wardle
 */
public final class DatabaseFactoryImpl implements DatabaseFactory {

    private static final Logger LOGGER = Logger
            .getLogger(DatabaseFactoryImpl.class.getName());

    /**
     * Creates a new instance of <code>DatabaseFactoryImpl</code>.
     */
    public DatabaseFactoryImpl() {
        super();
    }

    /** {@inheritDoc} */
    public Database createDatabase(String databaseFilePath)
            throws FileNotFoundException, DataValidationException, IOException {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException(
                    "databaseFilePath cannot be null");
        }

        LOGGER.info("Creating database for file: " + databaseFilePath);
        return new DataAdapter(new Data(new DatabaseFileImpl(databaseFilePath)));
    }
}
