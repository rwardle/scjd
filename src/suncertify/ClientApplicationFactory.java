/*
 * ClientApplicationFactory.java
 *
 * 21 Aug 2007 
 */
package suncertify;

/**
 * @author Richard Wardle
 */
public final class ClientApplicationFactory extends AbstractApplicationFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ClientApplication(configuration,
                new SysErrExceptionHandler(), new SysExitShudownHandler());
    }
}
