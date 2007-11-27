package suncertify.service;

import java.io.IOException;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.Database;
import suncertify.db.RecordNotFoundException;

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
                        with(Expectations.any(String[].class)));
                will(Expectations.throwException(new IOException()));
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
                        with(Expectations.any(String[].class)));
                will(Expectations.returnValue(new int[0]));
            }
        });
        Assert.assertThat(this.brokerService.search(new SearchCriteria())
                .size(), CoreMatchers.is(0));
    }

    @Test
    public void searchReturnsAllContractorsWhenCriteriaAreNull()
            throws Exception {
        final int[] recNos = { 0, 2 };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase)
                        .find(
                                with(Expectations
                                        .equal(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT])));
                will(Expectations.returnValue(recNos));

                for (int recNo : recNos) {
                    one(BrokerServiceImplTest.this.mockDatabase).read(
                            with(Expectations.equal(recNo)));
                    will(Expectations
                            .returnValue(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT]));
                }
            }
        });
        Assert.assertThat(this.brokerService.search(new SearchCriteria())
                .size(), CoreMatchers.is(recNos.length));
    }

    @Test(expected = IOException.class)
    public void searchThrowsIOExceptionWhenReadThrowsIOException()
            throws Exception {
        final int[] recNos = { 0, 2 };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase)
                        .find(
                                with(Expectations
                                        .equal(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT])));
                will(Expectations.returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(Expectations.equal(recNos[0])));
                will(Expectations.throwException(new IOException()));
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
                one(BrokerServiceImplTest.this.mockDatabase)
                        .find(
                                with(Expectations
                                        .equal(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT])));
                will(Expectations.returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(Expectations.equal(recNos[0])));
                will(Expectations.throwException(new RecordNotFoundException()));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(Expectations.equal(recNos[1])));
                will(Expectations
                        .returnValue(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT]));
            }
        });
        Assert.assertThat(this.brokerService.search(new SearchCriteria())
                .size(), CoreMatchers.is(1));
    }

    @Test
    public void searchMapsRecordValuesIntoContractor() throws Exception {
        final int[] recNos = { 0 };
        final String[] recordData = { "Buonarotti & Company", "Smallville",
                "Air Conditioning, Painting, Painting", "10", "$40.00",
                "1245678" };
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase)
                        .find(
                                with(Expectations
                                        .equal(new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT])));
                will(Expectations.returnValue(recNos));

                one(BrokerServiceImplTest.this.mockDatabase).read(
                        with(Expectations.equal(recNos[0])));
                will(Expectations.returnValue(recordData));
            }
        });
        List<Contractor> contractors = this.brokerService
                .search(new SearchCriteria());
        Assert.assertThat(contractors.size(), CoreMatchers.is(1));
        assertContractor(recNos[0], recordData, contractors.get(0));
    }

    private void assertContractor(int recNo, String[] recordData,
            Contractor contractor) {
        Assert.assertThat(contractor.getRecordNumber(), CoreMatchers.is(recNo));
        Assert.assertThat(contractor.getName(), CoreMatchers.is(recordData[0]));
        Assert.assertThat(contractor.getLocation(), CoreMatchers
                .is(recordData[1]));
        Assert.assertThat(contractor.getSpecialties(), CoreMatchers
                .is(recordData[2]));
        Assert.assertThat(contractor.getSize(), CoreMatchers.is(recordData[3]));
        Assert.assertThat(contractor.getRate(), CoreMatchers.is(recordData[4]));
        Assert
                .assertThat(contractor.getOwner(), CoreMatchers
                        .is(recordData[5]));
    }

    @Test
    public void searchMapsSearchCriteriaToFindCriteria() throws Exception {
        final SearchCriteria criteria = new SearchCriteria().setName("name")
                .setLocation("location").setSpecialties("specialties").setSize(
                        "size").setRate("rate").setOwner("owner");
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).find(
                        with(Expectations.equal(new String[] {
                                criteria.getName(), criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner() })));
                will(Expectations.returnValue(new int[0]));
            }
        });
        this.brokerService.search(criteria);
    }

    @Test
    public void searchOnlyReturnsExactMatches() throws Exception {
        final int[] recNos = { 0, 1, 2, 3, 4, 5, 6 };
        final String[] matchingData = BrokerServiceImplTest.RECORD_DATA.clone();
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
                        with(Expectations.equal(new String[] {
                                criteria.getName(), criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner() })));
                will(Expectations.returnValue(recNos));

                for (int i = 0; i < recNos.length; i++) {
                    one(BrokerServiceImplTest.this.mockDatabase).read(
                            with(Expectations.equal(recNos[i])));
                    will(Expectations.returnValue(records[i]));
                }
            }
        });
        List<Contractor> contractors = this.brokerService.search(criteria);
        Assert.assertThat(contractors.size(), CoreMatchers.is(1));
        assertContractor(recNos[recNos.length - 1], records[recNos.length - 1],
                contractors.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullCustomerIdThrowsException() throws Exception {
        this.brokerService.book(null, new Contractor(0,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullContractorThrowsException() throws Exception {
        this.brokerService.book("1234678", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithNullContractorFieldThrowsException() throws Exception {
        String[] data = BrokerServiceImplTest.RECORD_DATA.clone();
        data[5] = null;
        this.brokerService.book("12345678", new Contractor(0, data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookWithInvalidCustomerIdThrowsException() throws Exception {
        this.brokerService.book("123456789", new Contractor(0,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void bookContractorThatHasBeenDeletedThrowsException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));
                will(Expectations.throwException(new RecordNotFoundException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = IOException.class)
    public void bookThrowsExceptionIfLockThrowsInterruptedException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));
                will(Expectations.throwException(new InterruptedException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void bookThrowsExceptionIfReadThrowsRecordNotFoundException()
            throws Exception {
        final int recNo = 0;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations.throwException(new RecordNotFoundException()));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void bookContractorThatHasBeenModifiedThrowsException()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = BrokerServiceImplTest.RECORD_DATA.clone();
        modifiedData[4] += " this is modified";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations.returnValue(modifiedData));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void bookContractorThatHasAlreadyBeenBookedThrowsException()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = BrokerServiceImplTest.RECORD_DATA.clone();
        modifiedData[5] = "87654321";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations.returnValue(modifiedData));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test
    public void bookContractorThatHasBeenUnbookedDoesNotThrowException()
            throws Exception {
        final int recNo = 0;
        String[] data = BrokerServiceImplTest.RECORD_DATA.clone();
        data[5] = "87654321";
        final String[] modifiedData = BrokerServiceImplTest.RECORD_DATA.clone();
        modifiedData[5] = "";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations.returnValue(modifiedData));

                allowing(BrokerServiceImplTest.this.mockDatabase).update(
                        with(Expectations.any(int.class)),
                        with(Expectations.any(String[].class)));

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
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations
                        .returnValue(BrokerServiceImplTest.RECORD_DATA));

                allowing(BrokerServiceImplTest.this.mockDatabase).update(
                        with(Expectations.any(int.class)),
                        with(Expectations.any(String[].class)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
                will(Expectations.throwException(new RecordNotFoundException()));
            }
        });
        this.brokerService.book("12345678", new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test
    public void bookHappyPath() throws Exception {
        final int recNo = 0;
        String customerId = "12345678";
        final String[] data = new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT];
        data[BrokerServiceImplTest.DATABASE_FIELD_COUNT - 1] = customerId;
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations
                        .returnValue(BrokerServiceImplTest.RECORD_DATA));

                one(BrokerServiceImplTest.this.mockDatabase).update(
                        with(CoreMatchers.is(recNo)),
                        with(CoreMatchers.is(data)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book(customerId, new Contractor(recNo,
                BrokerServiceImplTest.RECORD_DATA));
    }

    @Test
    public void unbookHappyPath() throws Exception {
        final int recNo = 0;
        final String[] data = BrokerServiceImplTest.RECORD_DATA.clone();
        data[BrokerServiceImplTest.DATABASE_FIELD_COUNT - 1] = "12345678";
        final String[] updateData = new String[BrokerServiceImplTest.DATABASE_FIELD_COUNT];
        updateData[BrokerServiceImplTest.DATABASE_FIELD_COUNT - 1] = "";
        this.context.checking(new Expectations() {
            {
                one(BrokerServiceImplTest.this.mockDatabase).lock(
                        with(Expectations.equal(recNo)));

                one(BrokerServiceImplTest.this.mockDatabase).read(recNo);
                will(Expectations.returnValue(data));

                one(BrokerServiceImplTest.this.mockDatabase).update(
                        with(CoreMatchers.is(recNo)),
                        with(CoreMatchers.is(updateData)));

                one(BrokerServiceImplTest.this.mockDatabase).unlock(recNo);
            }
        });
        this.brokerService.book("", new Contractor(recNo, data));
    }
}
