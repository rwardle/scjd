/*
 * RemoteBrokerServiceImpl.java
 *
 * Created on 11 June 2005
 */


package suncertify.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;


/**
 *
 *
 * @author Richard Wardle
 */
public class RemoteBrokerServiceImpl extends UnicastRemoteObject implements
        RemoteBrokerService {

    // TODO: Make this a singleton?

    private static Logger logger = Logger.getLogger(RemoteBrokerServiceImpl
            .class.getName());

    private BrokerServiceImpl service;

    /**
     * Creates a new instance of RemoteBrokerServiceImpl.
     */
    public RemoteBrokerServiceImpl(String databaseFilePath) throws
            RemoteException {
        super();
        this.service = new BrokerServiceImpl(databaseFilePath);
        RemoteBrokerServiceImpl.logger.info("Database file path: '"
                + databaseFilePath + "'");
    }

    public String getHelloWorld() {
        return this.service.getHelloWorld();
    }
}
