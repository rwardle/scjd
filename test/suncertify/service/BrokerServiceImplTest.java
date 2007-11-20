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

@SuppressWarnings("boxing")
public class BrokerServiceImplTest {

    private static final int DATABASE_FIELD_COUNT = 6;
    private static final String[] RECORD_DATA = { "Buonarotti & Company",
            "Smallville", "Air Conditioning, Painting, Painting", "10",
            "$40.00", "" };

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
    public void searchReturnsAllContractorsWhenCriteriaAreNull()
            throws Exception {
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
    public void searchDoesNotReturnContractorForWhichReadThrowsRecordNotFoundException()
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
        assertContractor(recNos[0], recordData, contractors.get(0));
    }

    private void assertContractor(int recNo, String[] recordData,
            Contractor contractor) {
        assertThat(contractor.getRecordNumber(), is(recNo));
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
        final String[] matchingData = RECORD_DATA.clone();
        matchingData[5] = "12345678";
        final SearchCriteria criteria = new SearchCriteria().setName(
                matchingData[0]).setLocation(matchingData[1]).setSpecialties(
                matchingData[2]).setSize(matchingData[3]).setRate(
                matchingData[4]).setOwner(matchingData[5]);

        final String[][] records = new String[recNos.length][6];
        for (int i = 0; i < recNos.length - 1; i++) {
            records[i] = matchingData.clone();
            records[i][i] += " - this won't match";
        }
        records[recNos.length - 1] = matchingData;

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
        assertContractor(recNos[recNos.length - 1], records[recNos.length - 1],
                contractors.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullCustomerIdThrowsException() throws Exception {
        this.brokerService.book(null, new Contractor(0, RECORD_DATA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullContractorThrowsException() throws Exception {
        this.brokerService.book("1234678", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullContractorFieldThrowsException() throws Exception {
        String[] data = RECORD_DATA.clone();
        data[5] = null;
        this.brokerService.book("12345678", new Contractor(0, data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithInvalidCustomerIdThrowsException() throws Exception {
        this.brokerService.book("123456789", new Contractor(0, RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void bookContractorThatHasBeenDeletedThrowsException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));
                will(throwException(new RecordNotFoundException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = IOException.class)
    public void bookThrowsExceptionIfLockThrowsInterruptedException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));
                will(throwException(new InterruptedException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void bookThrowsExceptionIfReadThrowsRecordNotFoundException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(throwException(new RecordNotFoundException()));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void bookContractorThatHasBeenModifiedThrowsException()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[4] += " this is modified";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void bookContractorThatHasAlreadyBeenBookedThrowsException()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[5] = "87654321";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void bookContractorThatHasBeenUnbookedDoesNotThrowException()
            throws Exception {
        final int recNo = 0;
        String[] data = RECORD_DATA.clone();
        data[5] = "87654321";
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[5] = "";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                allowing(BrokerServiceImplTest.this.mockDatabase).update(
                        with(any(int.class)), with(any(String[].class)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, data));
    }

    @Test
    public void bookDoesNotThrowExceptionIfUnlockThrowsException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(RECORD_DATA));

                allowing(BrokerServiceImplTest.this.mockDatabase).update(
                        with(any(int.class)), with(any(String[].class)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
                will(throwException(new RecordNotFoundException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void bookHappyPath() throws Exception {
        final int recNo = 0;
        String customerId = "12345678";
        final String[] data = new String[DATABASE_FIELD_COUNT];
        data[DATABASE_FIELD_COUNT - 1] = customerId;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(RECORD_DATA));

                one(BrokerServiceImplTest.this.mockDatabase).update(
                        with(is(recNo)), with(is(data)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book(customerId, new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void unbookHappyPath() throws Exception {
        final int recNo = 0;
        final String[] data = RECORD_DATA.clone();
        data[DATABASE_FIELD_COUNT - 1] = "12345678";
        final String[] updateData = new String[DATABASE_FIELD_COUNT];
        updateData[DATABASE_FIELD_COUNT - 1] = "";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(returnValue(data));

                one(BrokerServiceImplTest.this.mockDatabase).update(
                        with(is(recNo)), with(is(updateData)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("", new Contractor(recNo, data));
    }
}
