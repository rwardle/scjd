/*
 * RemoteBrokerServiceImpl.java
 *
 * Created on 11 June 2005
 */

package suncertify.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import suncertify.db.DBMain;

/**
 *
 *
 * @author Richard Wardle
 */
public final class RemoteBrokerServiceImpl extends UnicastRemoteObject implements
        RemoteBrokerService {

    // TODO: Make this a singleton?

    private static Logger logger = Logger.getLogger(RemoteBrokerServiceImpl
            .class.getName());

    private BrokerServiceImpl service;

    /**
     * Creates a new instance of RemoteBrokerServiceImpl.
     */
    public RemoteBrokerServiceImpl(DBMain data) throws
            RemoteException {
        // TODO: Check for null
        this.service = new BrokerServiceImpl(data);
    }

    public String getHelloWorld() {
        return "Remote " + this.service.getHelloWorld();
    }
}
