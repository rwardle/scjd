/*
 * ClientApplicationFactory.java
 *
 * 21 Aug 2007 
 */
package suncertify;

import suncertify.service.RmiServiceImpl;

/**
 * @author Richard Wardle
 */
public final class ClientApplicationFactory extends AbstractApplicationFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ClientApplication(configuration, new RmiServiceImpl());
    }
}
