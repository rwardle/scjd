package suncertify.service;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.Database;

public class BrokerServiceImplTest {

    private Mockery context;
    private Database mockDatabase;
    private BrokerService brokerService;

    @Before
    public void setUp() {
        this.context = new Mockery();
        this.mockDatabase = this.context.mock(Database.class);
        this.brokerService = new BrokerServiceImpl(this.mockDatabase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullDatabaseThrowsException() {
        new BrokerServiceImpl(null);
    }
}
