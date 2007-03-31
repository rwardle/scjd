/*
 * RemoteBrokerService.java
 *
 * Created on 11 June 2005
 */

package suncertify.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Extends {@link BrokerService} for use with RMI.
 *
 * @author Richard Wardle
 */
public interface RemoteBrokerService extends Remote, BrokerService {

    String getHelloWorld() throws RemoteException;
}
