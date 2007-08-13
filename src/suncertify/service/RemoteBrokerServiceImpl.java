/*
 * RemoteBrokerServiceImpl.java
 *
 * 11 June 2007
 */

package suncertify.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import suncertify.db.DBMain;

/**
 * 
 * 
 * @author Richard Wardle
 */
public final class RemoteBrokerServiceImpl extends UnicastRemoteObject
        implements RemoteBrokerService {

    // TODO: Make this a singleton?

    private static final long serialVersionUID = 1L;
    private final BrokerServiceImpl service;

    /**
     * Creates a new instance of RemoteBrokerServiceImpl.
     */
    public RemoteBrokerServiceImpl(DBMain data) throws RemoteException {
        // TODO: Check for null
        this.service = new BrokerServiceImpl(data);
    }

    public String getHelloWorld() {
        System.out.println("In remote getHelloWorld");
        return "Remote " + this.service.getHelloWorld();
    }
}
