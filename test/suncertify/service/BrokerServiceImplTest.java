package suncertify.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.db.Database;
import suncertify.db.RecordNotFoundException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BrokerServiceImplTest {

    private static final int DATABASE_FIELD_COUNT = 6;
    private static final String[] RECORD_DATA = {"Buonarotti & Company",
            "Smallville", "Air Conditioning, Painting, Painting", "10",
            "$40.00", ""};

    private Mockery context;
    private Database mockDatabase;
    private BrokerService brokerService;

    @Before
    public void setUp() {
        context = new Mockery();
        mockDatabase = context.mock(Database.class);
        brokerService = new BrokerServiceImpl(mockDatabase);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreatedWithNullDatabase() {
        new BrokerServiceImpl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSearchingWithNullCriteriaObject()
            throws Exception {
        brokerService.search(null);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenSearchingAndFindThrowsIOException()
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(with(any(String[].class)));
                will(throwException(new IOException()));
            }
        });
        brokerService.search(new SearchCriteria());
    }

    @Test
    public void shouldReturnEmptyListWhenSearchingAndFindReturnsNoRecords()
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(with(any(String[].class)));
                will(returnValue(new int[0]));
            }
        });
        assertThat(brokerService.search(new SearchCriteria()).size(), is(0));
    }

    @Test
    public void shouldReturnAllContractorsWhenSearchingWithNullCriteria()
            throws Exception {
        final int[] recNos = {0, 2};
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                for (int recNo : recNos) {
                    one(mockDatabase).read(with(equal(recNo)));
                    will(returnValue(new String[DATABASE_FIELD_COUNT]));
                }
            }
        });
        assertThat(brokerService.search(new SearchCriteria()).size(),
                is(recNos.length));
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenSearchingAndReadThrowsIOException()
            throws Exception {
        final int[] recNos = {0, 2};
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(mockDatabase).read(with(equal(recNos[0])));
                will(throwException(new IOException()));
            }
        });
        brokerService.search(new SearchCriteria());
    }

    @Test
    public void shouldNotReturnContractorForWhichReadThrowsRecordNotFoundExceptionWhenSearching()
            throws Exception {
        final int[] recNos = {0, 2};
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(mockDatabase).read(with(equal(recNos[0])));
                will(throwException(new RecordNotFoundException()));

                one(mockDatabase).read(with(equal(recNos[1])));
                will(returnValue(new String[DATABASE_FIELD_COUNT]));
            }
        });
        assertThat(brokerService.search(new SearchCriteria()).size(), is(1));
    }

    @Test
    public void shouldMapRecordValuesIntoContractorWhenSearching()
            throws Exception {
        final int[] recNos = {0};
        final String[] recordData = {"Buonarotti & Company", "Smallville",
                "Air Conditioning, Painting, Painting", "10", "$40.00",
                "1245678"};
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[DATABASE_FIELD_COUNT])));
                will(returnValue(recNos));

                one(mockDatabase).read(with(equal(recNos[0])));
                will(returnValue(recordData));
            }
        });
        List<Contractor> contractors = brokerService
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
    public void shouldMapSearchCriteriaToFindCriteriaWhenSearching()
            throws Exception {
        final SearchCriteria criteria = new SearchCriteria().setName("name")
                .setLocation("location").setSpecialties("specialties").setSize(
                        "size").setRate("rate").setOwner("owner");
        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[] {criteria.getName(),
                                criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner()})));
                will(returnValue(new int[0]));
            }
        });
        brokerService.search(criteria);
    }

    @Test
    public void shouldOnlyReturnsExactMatchesWhenSearching() throws Exception {
        final int[] recNos = {0, 1, 2, 3, 4, 5, 6};
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

        context.checking(new Expectations() {
            {
                one(mockDatabase).find(
                        with(equal(new String[] {criteria.getName(),
                                criteria.getLocation(),
                                criteria.getSpecialties(), criteria.getSize(),
                                criteria.getRate(), criteria.getOwner()})));
                will(returnValue(recNos));

                for (int i = 0; i < recNos.length; i++) {
                    one(mockDatabase).read(with(equal(recNos[i])));
                    will(returnValue(records[i]));
                }
            }
        });
        List<Contractor> contractors = brokerService.search(criteria);
        assertThat(contractors.size(), is(1));
        assertContractor(recNos[recNos.length - 1], records[recNos.length - 1],
                contractors.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithNullCustomerId()
            throws Exception {
        brokerService.book(null, new Contractor(0, RECORD_DATA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithNullContractor()
            throws Exception {
        brokerService.book("12345678", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithNullContractorField()
            throws Exception {
        String[] data = RECORD_DATA.clone();
        data[5] = null;
        brokerService.book("12345678", new Contractor(0, data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithTooLongCustomerId()
            throws Exception {
        brokerService.book("123456789", new Contractor(0, RECORD_DATA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithTooShortCustomerId()
            throws Exception {
        brokerService.book("1234567", new Contractor(0, RECORD_DATA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBookingWithCustomerIdContainingNonDigits()
            throws Exception {
        brokerService.book("12a45678", new Contractor(0, RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void shouldThrowExceptionWhenBookingContractorThatHasBeenDeleted()
            throws Exception {
        final int recNo = 0;
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));
                will(throwException(new RecordNotFoundException()));
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenBookingIfLockThrowsInterruptedException()
            throws Exception {
        final int recNo = 0;
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));
                will(throwException(new InterruptedException()));
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorDeletedException.class)
    public void shouldThrowExceptionIfReadThrowsRecordNotFoundExceptionWhenBooking()
            throws Exception {
        final int recNo = 0;
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(throwException(new RecordNotFoundException()));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void shouldThrowExceptionWhenBookingContractorThatHasBeenModified()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[4] += " this is modified";
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test(expected = ContractorModifiedException.class)
    public void shouldThrowExceptionWhenBookingContractorThatHasAlreadyBeenBooked()
            throws Exception {
        final int recNo = 0;
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[5] = "87654321";
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void shouldNotThrowExceptionWhenBookingContractorThatHasBeenUnbooked()
            throws Exception {
        final int recNo = 0;
        String[] data = RECORD_DATA.clone();
        data[5] = "87654321";
        final String[] modifiedData = RECORD_DATA.clone();
        modifiedData[5] = "";
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(modifiedData));

                allowing(mockDatabase).update(with(any(int.class)),
                        with(any(String[].class)));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book("12345678", new Contractor(recNo, data));
    }

    @Test
    public void shouldNotThrowExceptionIfUnlockThrowsExceptionWhenBooking()
            throws Exception {
        final int recNo = 0;
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(RECORD_DATA));

                allowing(mockDatabase).update(with(any(int.class)),
                        with(any(String[].class)));

                one(mockDatabase).unlock(recNo);
                will(throwException(new RecordNotFoundException()));
            }
        });
        brokerService.book("12345678", new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void shouldBookContractor() throws Exception {
        final int recNo = 0;
        String customerId = "12345678";
        final String[] data = new String[DATABASE_FIELD_COUNT];
        data[DATABASE_FIELD_COUNT - 1] = customerId;
        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(RECORD_DATA));

                one(mockDatabase).update(with(is(recNo)), with(is(data)));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book(customerId, new Contractor(recNo, RECORD_DATA));
    }

    @Test
    public void shouldOverwriteBooking() throws Exception {
        final int recNo = 0;

        String existingCustomerId = "87654321";
        final String[] existingData = RECORD_DATA.clone();
        existingData[DATABASE_FIELD_COUNT - 1] = existingCustomerId;

        String newCustomerId = "12345678";
        final String[] updateData = new String[DATABASE_FIELD_COUNT];
        updateData[DATABASE_FIELD_COUNT - 1] = newCustomerId;

        context.checking(new Expectations() {
            {
                one(mockDatabase).lock(with(equal(recNo)));

                one(mockDatabase).read(recNo);
                will(returnValue(existingData));

                one(mockDatabase).update(with(is(recNo)), with(is(updateData)));

                one(mockDatabase).unlock(recNo);
            }
        });
        brokerService.book(newCustomerId, new Contractor(recNo, existingData));
    }
}
