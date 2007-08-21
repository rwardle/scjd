/*
 * StandaloneApplicationFactory.java
 *
 * 21 Aug 2007 
 */
package suncertify;

/**
 * @author Richard Wardle
 */
public final class StandaloneApplicationFactory extends
        AbstractApplicationFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new StandaloneApplication(configuration,
                new SysErrExceptionHandler(), new SysExitShudownHandler());
    }
}
