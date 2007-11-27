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

public class RmiServiceImpl implements RmiService {

    /**
     * {@inheritDoc}
     */
    public void createRegistry(int port) throws RemoteException {
        LocateRegistry.createRegistry(port);
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Remote obj) throws RemoteException,
            MalformedURLException {
        Naming.rebind(name, obj);
    }

    /**
     * {@inheritDoc}
     */
    public Remote lookup(String name) throws MalformedURLException,
            RemoteException, NotBoundException {
        return Naming.lookup(name);
    }
}
