package suncertify.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.Database;
import suncertify.db.RecordNotFoundException;

public class BrokerServiceImplTest {

    private static final int DATABASE_FIELD_COUNT = 6;
    private Mockery context;
    private Database mockDatabase;
    private BrokerService brokerService;

    @Before
    public void setUp() {
        this.context = new Mockery();
        this.mockDatabase = this.context.mock(Database.class);
        this.brokerService = new BrokerServiceImpl(this.mockDatabase);
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullDatabaseThrowsException() {
        new BrokerServiceImpl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchWithNullCriteriaObjectThrowsException() throws Exception {
        this.brokerService.search(null);
    }

    @Test(expected = IOException.class)
    public void searchThrowsIOExceptionWhenFindThrowsIOException()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(any(String[].class)));
                will(throwException(new IOException()));
            }
        });
        this.brokerService.search(new SearchCriteria());
    }

    @Test
    public void searchReturnsEmptyListWhenFindReturnsNoRecords()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(any(String[].class)));
                will(returnValue(new int[0]));
            }
        });
        assertThat(this.brokerService.search(new SearchCriteria()).size(),
                is(0));
    }

    @Test
    public void searchReturnsAllRecordsWhenCriteriaAreNull() throws Exception {
        final int[] recNos = { 0, 2 };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                for (int recNo : recNos) {
                    one(BrokerServiceImplTest.this.mockDatabase).read(
                            with(equal(recNo)));
                    will(returnValue(new String[DATABASE_FIELD_COUNT]));
                }
            }
        });
        assertThat(this.brokerService.search(new SearchCriteria()).size(),
                is(recNos.length));
    }

    @Test(expected = IOException.class)
    public void searchThrowsIOExceptionWhenReadThrowsIOException()
            throws Exception {
        final int[] recNos = { 0, 2 };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(equal(recNos[0])));
                will(throwException(new IOException()));
            }
        });
        this.brokerService.search(new SearchCriteria());
    }

    @Test
    public void searchDoesNotReturnRecordForWhichReadThrowsRecordNotFoundException()
            throws Exception {
        final int[] recNos = { 0, 2 };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(equal(recNos[0])));
                will(throwException(new RecordNotFoundException()));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(equal(recNos[1])));
                will(returnValue(new String[DATABASE_FIELD_COUNT]));
            }
        });
        assertThat(this.brokerService.search(new SearchCriteria()).size(),
                is(1));
    }

    @Test
    public void searchMapsRecordValuesIntoContractor() throws Exception {
        final int[] recNos = { 0 };
        final String[] recordData = { "Buonarotti & Company", "Smallville",
                "Air Conditioning, Painting, Painting", "10", "$40.00",
                "1245678" };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(equal(recNos[0])));
                will(returnValue(recordData));
            }
        });
        List<Contractor> contractors = this.brokerService
                .search(new SearchCriteria());
        assertThat(contractors.size(), is(1));
        assertContractor(recordData, contractors.get(0));
    }

    private void assertContractor(String[] recordData, Contractor contractor) {
        assertThat(contractor.getName(), is(recordData[0]));
        assertThat(contractor.getLocation(), is(recordData[1]));
        assertThat(contractor.getSpecialties(), is(recordData[2]));
        assertThat(contractor.getSize(), is(recordData[3]));
        assertThat(contractor.getRate(), is(recordData[4]));
        assertThat(contractor.getOwner(), is(recordData[5]));
    }

    @Test
    public void searchMapsSearchCriteriaToFindCriteria() throws Exception {
        final SearchCriteria criteria = new SearchCriteria().setName("name")
                .setLocation("location").setSpecialties("specialties").setSize(
                        "size").setRate("rate").setOwner("owner");
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[] { criteria.getName(),
                                criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner() })));
                will(returnValue(new int[0]));
            }
        });
        this.brokerService.search(criteria);
    }

    @Test
    public void searchOnlyReturnsExactMatches() throws Exception {
        final int[] recNos = { 0, 1, 2, 3, 4, 5, 6 };
        final SearchCriteria criteria = new SearchCriteria().setName(
                "Buonarotti & Company").setLocation("Smallville")
                .setSpecialties("Air Conditioning, Painting, Painting")
                .setSize("10").setRate("$40.00").setOwner("1245678");
        final String[] matchingRecordData = { "Buonarotti & Company",
                "Smallville", "Air Conditioning, Painting, Painting", "10",
                "$40.00", "1245678" };

        final String[][] records = new String[recNos.length][6];
        for (int i = 0; i < recNos.length - 1; i++) {
            records[i] = matchingRecordData.clone();
            records[i][i] += " - this won't match";
        }
        records[recNos.length - 1] = matchingRecordData;

        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(equal(new String[] { criteria.getName(),
                                criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner() })));
                will(returnValue(recNos));

                for (int i = 0; i < recNos.length; i++) {
                    one(BrokerServiceImplTest.this.mockDatabase).read(
                            with(equal(recNos[i])));
                    will(returnValue(records[i]));
                }
            }
        });
        List<Contractor> contractors = this.brokerService.search(criteria);
        assertThat(contractors.size(), is(1));
        assertContractor(records[recNos.length - 1], contractors.get(0));
    }
}
