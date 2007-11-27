/*
 * ServerApplicationFactory.java
 *
 * 21 Aug 2007 
 */

package suncertify;

import suncertify.db.DatabaseFactoryImpl;
import suncertify.service.RmiServiceImpl;

/**
 * Creates server applications.
 * 
 * @author Richard Wardle
 */
public final class ServerApplicationFactory extends AbstractApplicationFactory {

    /**
     * Creates a new instance of <code>ServerApplicationFactory</code>.
     */
    public ServerApplicationFactory() {
        super();
    }

    /**
     * This implementation creates a server application.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ServerApplication(configuration, new RmiServiceImpl(),
                new DatabaseFactoryImpl());
    }
}
