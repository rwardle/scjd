/*
 * BrokerService.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.io.IOException;
import java.util.List;

/**
 * Defines business level methods.
 * 
 * @author Richard Wardle
 */
public interface BrokerService {

    List<Contractor> search(SearchCriteria searchCriteria) throws IOException;

    void book(String customerId, Contractor contractor) throws IOException,
            ContractorDeletedException, ContractorModifiedException;
}
