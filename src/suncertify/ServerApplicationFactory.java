/*
 * ServerApplicationFactory.java
 *
 * 21 Aug 2007 
 */
package suncertify;

/**
 * @author Richard Wardle
 */
public final class ServerApplicationFactory extends AbstractApplicationFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ServerApplication(configuration,
                new SysErrExceptionHandler(), new SysExitShudownHandler());
    }
}
