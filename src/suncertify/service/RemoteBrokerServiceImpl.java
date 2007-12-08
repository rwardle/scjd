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
 * Remote object implementation of {@link RemoteBrokerService} that uses a
 * {@link Database} object to obtain and store contractor data. All method calls
 * are delegated to an instance of {@link BrokerServiceImpl}.
 * 
 * @author Richard Wardle
 */
public final class RemoteBrokerServiceImpl extends UnicastRemoteObject
        implements RemoteBrokerService {

    private static final Logger LOGGER = Logger
            .getLogger(RemoteBrokerServiceImpl.class.getName());

    private final BrokerServiceImpl service;

    /**
     * Creates and exports a new instance of
     * <code>RemoteBrokerServiceImpl</code> using the specified database.
     * 
     * @param database
     *                Database of contractors.
     * @throws RemoteException
     *                 If the export failed.
     * @throws IllegalArgumentException
     *                 If <code>database</code> is <code>null</code>.
     */
    public RemoteBrokerServiceImpl(Database database) throws RemoteException {
        if (database == null) {
            throw new IllegalArgumentException("database cannot be null");
        }
        service = new BrokerServiceImpl(database);
    }

    /** {@inheritDoc} */
    public List<Contractor> search(SearchCriteria searchCriteria)
            throws IOException {
        try {
            List<Contractor> contractors = service.search(searchCriteria);
            LOGGER.info("Found " + contractors.size()
                    + " contractors exactly matching criteria: "
                    + searchCriteria);
            return contractors;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "IO error while searching for contractors with criteria: "
                            + searchCriteria, e);
            throw e;
        }
    }

    /** {@inheritDoc} */
    public void book(String customerId, Contractor contractor)
            throws IOException, ContractorDeletedException,
            ContractorModifiedException {
        try {
            service.book(customerId, contractor);
            LOGGER.info("Customer with ID=" + customerId
                    + " has booked contractor: " + contractor);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error while booking contractor", e);
            throw e;
        } catch (ContractorDeletedException e) {
            LOGGER.log(Level.SEVERE,
                    "Contractor to be booked has been deleted, recordNo: "
                            + contractor.getRecordNumber(), e);
            throw e;
        } catch (ContractorModifiedException e) {
            LOGGER.log(Level.SEVERE,
                    "Contractor to be booked has been modified, recordNo: "
                            + contractor.getRecordNumber(), e);
            throw e;
        }
    }
}
