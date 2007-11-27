/*
 * RemoteBrokerServiceImpl.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.db.Database;

/**
 * 
 * 
 * @author Richard Wardle
 */
public final class RemoteBrokerServiceImpl extends UnicastRemoteObject
        implements RemoteBrokerService {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger
            .getLogger(RemoteBrokerServiceImpl.class.getName());

    private final BrokerServiceImpl service;

    /**
     * Creates a new instance of RemoteBrokerServiceImpl.
     */
    public RemoteBrokerServiceImpl(Database database) throws RemoteException {
        this.service = new BrokerServiceImpl(database);
    }

    public List<Contractor> search(SearchCriteria searchCriteria)
            throws IOException {
        try {
            return this.service.search(searchCriteria);
        } catch (IOException e) {
            RemoteBrokerServiceImpl.LOGGER.log(Level.SEVERE,
                    "IO error while searching for contractors with criteria: "
                            + searchCriteria, e);
            throw e;
        }
    }

    public void book(String customerId, Contractor contractor)
            throws IOException, ContractorDeletedException,
            ContractorModifiedException {
        try {
            this.service.book(customerId, contractor);
        } catch (IOException e) {
            RemoteBrokerServiceImpl.LOGGER.log(Level.SEVERE,
                    "IO error while booking contractor", e);
            throw e;
        } catch (ContractorDeletedException e) {
            RemoteBrokerServiceImpl.LOGGER.log(Level.SEVERE,
                    "Contractor to be booked has been deleted, recordNo: "
                            + contractor.getRecordNumber(), e);
            throw e;
        } catch (ContractorModifiedException e) {
            RemoteBrokerServiceImpl.LOGGER.log(Level.SEVERE,
                    "Contractor to be booked has been modified, recordNo: "
                            + contractor.getRecordNumber(), e);
            throw e;
        }
    }
}
