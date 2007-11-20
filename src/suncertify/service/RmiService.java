/*
 * RmiService.java
 *
 * 11 Oct 2007
 */

package suncertify.service;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public interface RmiService {

    /**
     * Creates and exports a <code>Registry</code> instance on the local host
     * that accepts requests on the specified <code>port</code>.
     * 
     * <p>
     * The <code>Registry</code> instance is exported as if the static
     * {@link UnicastRemoteObject.exportObject(Remote,int)
     * UnicastRemoteObject.exportObject} method is invoked, passing the
     * <code>Registry</code> instance and the specified <code>port</code> as
     * arguments, except that the <code>Registry</code> instance is exported
     * with a well-known object identifier, an {@link ObjID} instance
     * constructed with the value {@link ObjID#REGISTRY_ID}.
     * 
     * @param port
     *                the port on which the registry accepts requests
     * @return the registry
     * @throws RemoteException
     *                 if the registry could not be exported
     */
    void createRegistry(int port) throws RemoteException;

    /**
     * Rebinds the specified name to a new remote object. Any existing binding
     * for the name is replaced.
     * 
     * @param name
     *                a name in URL format (without the scheme component)
     * @param obj
     *                new remote object to associate with the name
     * @throws MalformedURLException
     *                 if the name is not an appropriately formatted URL
     * @throws RemoteException
     *                 if registry could not be contacted
     * @throws AccessException
     *                 if this operation is not permitted (if originating from a
     *                 non-local host, for example)
     */
    void rebind(String name, Remote obj) throws RemoteException,
            MalformedURLException;

    /**
     * Returns a reference, a stub, for the remote object associated with the
     * specified <code>name</code>.
     * 
     * @param name
     *                a name in URL format (without the scheme component)
     * @return a reference for a remote object
     * @throws NotBoundException
     *                 if name is not currently bound
     * @throws RemoteException
     *                 if registry could not be contacted
     * @throws AccessException
     *                 if this operation is not permitted
     * @throws MalformedURLException
     *                 if the name is not an appropriately formatted URL
     */
    Remote lookup(String name) throws MalformedURLException, RemoteException,
            NotBoundException;
}
