/*
 * ServerApplicationFactory.java
 *
 * 21 Aug 2007 
 */
package suncertify;

import suncertify.db.DatabaseFactoryImpl;
import suncertify.service.RmiServiceImpl;

/**
 * @author Richard Wardle
 */
public final class ServerApplicationFactory extends AbstractApplicationFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ServerApplication(configuration, new RmiServiceImpl(),
                new DatabaseFactoryImpl());
    }
}
