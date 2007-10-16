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

    String getHelloWorld() throws IOException;

    List<Contractor> search(SearchCriteria searchCriteria) throws IOException;

    void book(Contractor contractor) throws IOException;
}
