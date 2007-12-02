/*
 * RemoteBrokerService.java
 *
 * 11 June 2007
 */

package suncertify.service;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Specialisation of {@link BrokerService} that enables its methods to be called
 * from a remote virtual machine via Remote Method Invocation (RMI).
 * 
 * @author Richard Wardle
 */
public interface RemoteBrokerService extends Remote, BrokerService {

    /**
     * {@inheritDoc}
     * 
     * @throws RemoteException
     *                 If there is an error executing the remote method call.
     */
    List<Contractor> search(SearchCriteria searchCriteria)
            throws RemoteException, IOException;

    /**
     * {@inheritDoc}
     * 
     * @throws RemoteException
     *                 If there is an error executing the remote method call.
     */
    void book(String customerId, Contractor contractor) throws RemoteException,
            IOException, ContractorDeletedException,
            ContractorModifiedException;
}
