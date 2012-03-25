/*
 * RmiServiceImpl.java
 *
 * 11 Oct 2007
 */

package suncertify.service;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;

/**
 * Implementation of {@link RmiService} that delegates to {@link LocateRegistry}
 * and {@link Naming}.
 *
 * @author Richard Wardle
 */
public final class RmiServiceImpl implements RmiService {

    private static final Logger LOGGER = Logger.getLogger(RmiServiceImpl.class
            .getName());

    /**
     * Creates a new instance of <code>RmiServiceImpl</code>.
     */
    public RmiServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void createRegistry(int port) throws RemoteException {
        LOGGER.info("Creating RMI registry on port: " + port);
        LocateRegistry.createRegistry(port);
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Remote obj) throws RemoteException,
            MalformedURLException {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }

        LOGGER.info("Rebinding remote object: " + name);
        Naming.rebind(name, obj);
    }

    /**
     * {@inheritDoc}
     */
    public Remote lookup(String name) throws NotBoundException,
            MalformedURLException, RemoteException {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        LOGGER.info("Looking up remote object: " + name);
        return Naming.lookup(name);
    }
}
