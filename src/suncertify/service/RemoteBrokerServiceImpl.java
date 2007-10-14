/*
 * RemoteBrokerServiceImpl.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.Database;

/**
 * 
 * 
 * @author Richard Wardle
 */
public final class RemoteBrokerServiceImpl extends UnicastRemoteObject
        implements RemoteBrokerService {

    private static final long serialVersionUID = 1L;
    private final BrokerServiceImpl service;

    /**
     * Creates a new instance of RemoteBrokerServiceImpl.
     */
    public RemoteBrokerServiceImpl(Database database) throws RemoteException {
        this.service = new BrokerServiceImpl(database);
    }

    public String getHelloWorld() {
        return "Remote " + this.service.getHelloWorld();
    }
}
