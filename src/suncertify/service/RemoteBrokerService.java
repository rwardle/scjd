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
 * Extends {@link BrokerService} for use with RMI.
 * 
 * @author Richard Wardle
 */
public interface RemoteBrokerService extends Remote, BrokerService {

    List<Contractor> search(SearchCriteria searchCriteria) throws IOException;

    void book(String customerId, Contractor contractor) throws RemoteException,
            IOException, ContractorDeletedException,
            ContractorModifiedException;
}
