/*
 * RmiService.java
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

/**
 * An interface grouping all methods for performing Remote Method Invocation
 * (RMI) operations.
 *
 * @author Richard Wardle
 */
public interface RmiService {

    /**
     * Creates an RMI registry on the local host at the specified port.
     *
     * @param port Port on which the registry accepts requests.
     * @throws RemoteException If the registry could not be created.
     * @see LocateRegistry#createRegistry(int)
     */
    void createRegistry(int port) throws RemoteException;

    /**
     * Rebinds the specified name to the specified remote object.
     *
     * @param name Name of the remote object in URL form.
     * @param obj  Remote object to associate with the name.
     * @throws RemoteException          If the registry could not be contacted.
     * @throws MalformedURLException    If <code>name</code> is malformed.
     * @throws IllegalArgumentException If <code>name</code> or <code>obj</code> is
     *                                  <code>null</code>.
     * @see Naming#rebind(String, Remote)
     */
    void rebind(String name, Remote obj) throws RemoteException,
            MalformedURLException;

    /**
     * Returns a reference to the remote object associated with the specified
     * name.
     *
     * @param name Name of the remote object in URL form.
     * @return The remote object reference.
     * @throws NotBoundException        If <code>name</code> is not currently bound.
     * @throws RemoteException          If the registry could not be contacted.
     * @throws MalformedURLException    If <code>name</code> is malformed.
     * @throws IllegalArgumentException If <code>name</code> is <code>null</code>.
     * @see Naming#lookup(String)
     */
    Remote lookup(String name) throws NotBoundException, RemoteException,
            MalformedURLException;
}
