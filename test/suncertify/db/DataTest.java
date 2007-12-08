package suncertify.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DatabaseSchema.FieldDescription;

public class DataTest {

    private ExceptionHandler exceptionHandler;
    private Mockery context;
    private DatabaseFile mockDatabaseFile;
    private Data data;
    private long dataSectionOffset;
    private int recordCount;
    private SortedSet<Integer> deletedRecNos;

    @Before
    public void setUp() throws Exception {
        // Set a default exception handler to pickup failures in additional
        // threads
        exceptionHandler = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        context = new Mockery();
        mockDatabaseFile = context.mock(DatabaseFile.class);
        dataSectionOffset = 999; // Value is irrelevant
        recordCount = 6;
        deletedRecNos = new TreeSet<Integer>();
        deletedRecNos.add(5);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
        Throwable exception = exceptionHandler.getException();
        if (exception != null) {
            fail(exception.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenConstructedWithNullDatabaseFile()
            throws Exception {
        data = new Data(null);
    }

    @Test(expected = DataValidationException.class)
    public void shouldThrowExceptionWhenConstructedWithInvalidMagicCookie()
            throws Exception {
        int invalidMagicCookie = -1;
        assertThat(invalidMagicCookie, is(not(DatabaseConstants.MAGIC_COOKIE)));
        checkingMagicCookieRead(invalidMagicCookie, context
                .sequence("construction"));
        new Data(mockDatabaseFile);
    }

    private void checkingMagicCookieRead(final int magicCookie,
            final Sequence sequence) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).seek(with(equal(0L)));

                one(mockDatabaseFile).readInt();
                will(returnValue(magicCookie));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void shouldThrowExceptionWhenConstructedWithInvalidRecordLength()
            throws Exception {
        Sequence sequence = context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        int invalidRecordLength = -1;
        assertThat(invalidRecordLength,
                is(not(DataTestConstants.EXPECTED_RECORD_LENGTH)));
        checkingRecordLengthRead(invalidRecordLength, sequence);
        data = new Data(mockDatabaseFile);
    }

    private void checkingRecordLengthRead(final int recordLength,
            final Sequence sequence) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).readInt();
                will(returnValue(recordLength));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void shouldThrowExceptionWhenConstructedWithInvalidFieldCount()
            throws Exception {
        Sequence sequence = context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DataTestConstants.EXPECTED_RECORD_LENGTH,
                sequence);
        short invalidFieldCount = -1;
        assertThat(invalidFieldCount,
                is(not(DataTestConstants.EXPECTED_FIELD_COUNT)));
        checkingFieldCount(invalidFieldCount, sequence);
        data = new Data(mockDatabaseFile);
    }

    private void checkingFieldCount(final short fieldCount,
            final Sequence sequence) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).readShort();
                will(returnValue(fieldCount));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void shouldThrowExceptionWhenConstructedWithInvalidFieldDescription()
            throws Exception {
        Sequence sequence = context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DataTestConstants.EXPECTED_RECORD_LENGTH,
                sequence);
        checkingFieldCount(DataTestConstants.EXPECTED_FIELD_COUNT, sequence);

        short invalidFieldLength = 999;
        assertThat(invalidFieldLength,
                is(not((short) DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[0]
                        .getName().length())));
        FieldDescription invalidFieldDescription = new FieldDescription(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[0].getName(),
                invalidFieldLength, 0);
        checkingFieldDescriptions(
                new FieldDescription[] { invalidFieldDescription }, sequence);

        data = new Data(mockDatabaseFile);
    }

    private void checkingFieldDescriptions(
            final FieldDescription[] fieldDescriptions, final Sequence sequence)
            throws Exception {
        context.checking(new Expectations() {
            {
                for (FieldDescription element : fieldDescriptions) {
                    one(mockDatabaseFile).readShort();
                    will(returnValue((short) element.getName().length()));
                    inSequence(sequence);

                    one(mockDatabaseFile).readFully(with(any(byte[].class)));
                    will(readBytes(element.getName().getBytes()));
                    inSequence(sequence);

                    one(mockDatabaseFile).readShort();
                    will(returnValue(element.getLength()));
                    inSequence(sequence);
                }
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void shouldThrowExceptionWhenConstructedWithInvalidDataSectionLength()
            throws Exception {
        final Sequence sequence = context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DataTestConstants.EXPECTED_RECORD_LENGTH,
                sequence);
        checkingFieldCount(DataTestConstants.EXPECTED_FIELD_COUNT, sequence);
        checkingFieldDescriptions(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).length();
                long validLength = getOffsetForRecord(recordCount);
                will(returnValue(validLength - 1));
                inSequence(sequence);
            }
        });
        data = new Data(mockDatabaseFile);
    }

    private long getOffsetForRecord(int recNo) {
        return dataSectionOffset
                + recNo
                * (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + DataTestConstants.EXPECTED_RECORD_LENGTH);
    }

    @Test
    public void shouldConstructDatabaseSchema() throws Exception {
        standardSetup();
    }

    private void standardSetup() throws Exception {
        Sequence sequence = context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DataTestConstants.EXPECTED_RECORD_LENGTH,
                sequence);
        checkingFieldCount(DataTestConstants.EXPECTED_FIELD_COUNT, sequence);
        checkingFieldDescriptions(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        checkingRecordCount(sequence);
        checkingCacheDeletedRecordNumbers(sequence);
        data = new Data(mockDatabaseFile);
        assertSchema();
        assertThat(data.getDataSectionOffset(), is(dataSectionOffset));
        assertThat(data.getRecordCount(), is(recordCount));
        assertDeletedRecords();
    }

    private void checkingDataSectionOffset(final Sequence sequence)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).getFilePointer();
                will(returnValue(dataSectionOffset));
                inSequence(sequence);
            }
        });
    }

    private void checkingRecordCount(final Sequence sequence) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).length();
                will(returnValue(dataSectionOffset
                        + recordCount
                        * (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + DataTestConstants.EXPECTED_RECORD_LENGTH)));
                inSequence(sequence);
            }
        });
    }

    private void checkingCacheDeletedRecordNumbers(final Sequence sequence)
            throws Exception {
        context.checking(new Expectations() {
            {
                for (int recNo = 0; recNo < recordCount; recNo++) {
                    one(mockDatabaseFile).seek(
                            with(equal(getOffsetForRecord(recNo))));
                    inSequence(sequence);

                    one(mockDatabaseFile).readByte();
                    will(returnValue(deletedRecNos.contains(recNo) ? DatabaseConstants.DELETED_RECORD_FLAG
                            : DatabaseConstants.VALID_RECORD_FLAG));
                    inSequence(sequence);
                }
            }
        });
    }

    private void assertSchema() {
        DatabaseSchema schema = data.getDatabaseSchema();
        assertThat(schema.getRecordLength(),
                is(DataTestConstants.EXPECTED_RECORD_LENGTH));
        assertThat(schema.getFieldCount(),
                is(DataTestConstants.EXPECTED_FIELD_COUNT));

        FieldDescription[] fieldDescriptions = schema.getFieldDescriptions();
        assertThat(fieldDescriptions, is(notNullValue()));
        assertThat(fieldDescriptions.length,
                is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length));
        for (int i = 0; i < fieldDescriptions.length; i++) {
            assertThat(fieldDescriptions[i].getName(),
                    is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                            .getName()));
            assertThat(fieldDescriptions[i].getLength(),
                    is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                            .getLength()));
            assertThat(fieldDescriptions[i].getRecordOffset(),

            is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                    .getRecordOffset()));
        }
    }

    private void assertDeletedRecords() {
        for (int recNo = 0; recNo < recordCount; recNo++) {
            assertThat(data.isRecordDeleted(recNo), is(deletedRecNos
                    .contains(recNo)));
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenReadWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.read(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenReadWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.read(recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenReadingDeletedRecord() throws Exception {
        standardSetup();
        data.read(deletedRecNos.first());
    }

    @Test
    public void shouldTrimRecordWhenReadWithSpacePadding() throws Exception {
        standardSetup();
        read(1, DataTestConstants.RECORD_VALUES_SPACE_PADDED,
                DataTestConstants.RECORD_VALUES);
    }

    private void read(final int recordNumber,
            final String[] recordValuesToRead, String[] expectedRecordValues)
            throws Exception {
        final Sequence sequence = context.sequence("read");
        checkingSeek(getOffsetForRecord(recordNumber)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH, sequence);

        final StringBuilder recordBuilder = new StringBuilder();
        for (String element : recordValuesToRead) {
            recordBuilder.append(element);
        }

        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).readFully(with(any(byte[].class)));
                will(readBytes(recordBuilder.toString().getBytes()));
                inSequence(sequence);
            }
        });

        String[] actualRecordValues = data.read(recordNumber);
        assertThat(actualRecordValues.length, is(expectedRecordValues.length));
        for (int i = 0; i < actualRecordValues.length; i++) {
            assertThat(actualRecordValues[i], is(expectedRecordValues[i]));
        }
    }

    private void checkingSeek(final long pos, final Sequence sequence)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).seek(pos);
                inSequence(sequence);
            }
        });
    }

    @Test
    public void shouldTrimRecordWhenReadWithNullPadding() throws Exception {
        standardSetup();
        read(1, DataTestConstants.RECORD_VALUES_NULL_PADDED,
                DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUpdatedWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.update(-1, DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUpdatedWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.update(recordCount, DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdatedWithNullDataArray()
            throws Exception {
        standardSetup();
        int recNo = 0;
        data.lock(recNo);
        data.update(recNo, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdatedWithInvalidDataArrayLength()
            throws Exception {
        standardSetup();
        int recNo = 0;
        data.lock(recNo);
        String[] recordValues = new String[DataTestConstants.EXPECTED_FIELD_COUNT + 1];
        System.arraycopy(DataTestConstants.RECORD_VALUES, recNo, recordValues,
                recNo, DataTestConstants.RECORD_VALUES.length);
        data.update(recNo, recordValues);
    }

    @Test
    public void shouldNotWriteCorrespondingFieldWhenUpdatedWithNullRecordValue()
            throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        final Sequence sequence = context.sequence("update");
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
        recordValues[1] = null;
        checkingUpdateRecord(recNo, recordValues, sequence);
        data.update(recNo, recordValues);
    }

    @Test
    public void shouldTruncateDataWhenUpdatedWithTooLongRecordValue()
            throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        final Sequence sequence = context.sequence("update");
        String[] recordValues = DataTestConstants.RECORD_VALUES.clone();
        recordValues[1] = DataTestConstants.RECORD_VALUES_SPACE_PADDED[1]
                + " too long";
        checkingUpdateRecord(recNo,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        data.update(recNo, recordValues);
    }

    @Test
    public void shouldBlankFieldWhenUpdatedWithEmptyStringRecordValue()
            throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        final Sequence sequence = context.sequence("update");
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
        recordValues[1] = "";
        String[] paddedRecordValues = recordValues.clone();
        paddedRecordValues[1] = DataTestConstants.padField(
                paddedRecordValues[1],
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[1].getLength(),
                ' ');
        checkingUpdateRecord(recNo, paddedRecordValues, sequence);
        data.update(recNo, recordValues);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUpdatingDeletedRecord()
            throws Exception {
        standardSetup();
        Integer recNo = deletedRecNos.first();
        data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenNotHoldingLockOnRecordToBeUpdated()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    @Test
    public void shouldUpdateRecord() throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        final Sequence sequence = context.sequence("update");
        checkingUpdateRecord(recNo,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    private void checkingUpdateRecord(int recNo, final String[] recordValues,
            final Sequence sequence) throws Exception {
        final long recordValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        context.checking(new Expectations() {
            {
                for (int i = 0; i < DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length; i++) {
                    if (recordValues[i] != null) {
                        one(mockDatabaseFile)
                                .seek(
                                        with(equal(recordValuesStartPos
                                                + DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                                                        .getRecordOffset())));
                        inSequence(sequence);

                        one(mockDatabaseFile)
                                .write(
                                        with(equal(recordValues[i]
                                                .getBytes(DatabaseConstants.CHARACTER_SET))));
                        inSequence(sequence);
                    }
                }
            }
        });
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenDeleteCalledWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.delete(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenDeleteCalledWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.delete(recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenDeletingDeletedRecord()
            throws Exception {
        standardSetup();
        Integer recNo = deletedRecNos.first();
        data.delete(recNo);
    }

    @Test
    public void shouldDeleteRecord() throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        Sequence sequence = context.sequence("delete");
        checkingDeleteRecord(recNo, sequence);
        data.delete(recNo);
        data.isRecordDeleted(recNo);
    }

    private void checkingDeleteRecord(int recNo, final Sequence sequence)
            throws Exception {
        checkingSeek(getOffsetForRecord(recNo), sequence);
        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).writeByte(
                        DatabaseConstants.DELETED_RECORD_FLAG);
                inSequence(sequence);
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenNotHoldingLockOnRecordToBeDeleted()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        data.delete(recNo);
    }

    @Test
    public void shouldSignalWaitingRecordWhenDeletingRecord() throws Exception {
        standardSetup();
        final Integer recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));

        // Attempt to lock the same record on another thread - will block
        Thread lockingThread = createThreadThatLocksRecordAndExpectsException(recNo);
        lockingThread.start();

        // Wait for the lockingThread to become blocked before continuing
        Thread.sleep(1000);

        checkingDeleteRecord(recNo, context.sequence("delete"));
        data.delete(recNo);

        joinThread(lockingThread);
    }

    private Thread createThreadThatLocksRecordAndExpectsException(
            final int recNo) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    data.lock(recNo);
                    fail("Expected RecordNotFoundException in thread: "
                            + Thread.currentThread().getName());
                } catch (RecordNotFoundException e) {
                    // Expected since by the time this thread is unblocked the
                    // record will be deleted
                }
            }
        });
    }

    private void joinThread(Thread thread) throws InterruptedException {
        thread.join(1000);
        assertThat(thread.isAlive(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateCalledWithNullDataArray()
            throws Exception {
        standardSetup();
        data.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateCalledWithInvalidDataArrayLength()
            throws Exception {
        standardSetup();
        String[] recordValues = new String[DataTestConstants.EXPECTED_FIELD_COUNT + 1];
        System.arraycopy(DataTestConstants.RECORD_VALUES, 0, recordValues, 0,
                DataTestConstants.RECORD_VALUES.length);
        data.create(recordValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCreateCalledWithNullRecordValue()
            throws Exception {
        standardSetup();
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
        recordValues[1] = null;
        data.create(recordValues);
    }

    @Test
    public void shouldWriteToEndOfFileWhenCreateCalledAndNoDeletedRecordsExist()
            throws Exception {
        deletedRecNos.clear();
        standardSetup();
        int recNoToWrite = recordCount;
        Sequence sequence = context.sequence("create");
        checkingCreateRecord(recNoToWrite,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        assertThat(data.create(DataTestConstants.RECORD_VALUES),
                is(recNoToWrite));
        assertThat(data.getRecordCount(), is(recordCount + 1));
    }

    @Test
    public void shouldWriteToDeletedRecordWhenCreateCalledAndDeletedRecordAvailable()
            throws Exception {
        standardSetup();
        int recNoToWrite = deletedRecNos.first();
        Sequence sequence = context.sequence("create");
        checkingCreateRecord(recNoToWrite,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        assertThat(data.create(DataTestConstants.RECORD_VALUES),
                is(recNoToWrite));
        assertThat(data.getRecordCount(), is(recordCount));
    }

    private void checkingCreateRecord(final int firstAvailableRecNo,
            final String[] recordValues, final Sequence sequence)
            throws Exception {
        checkingSeek(getOffsetForRecord(firstAvailableRecNo), sequence);

        final StringBuilder recordBuilder = new StringBuilder();
        for (String recordValue : recordValues) {
            recordBuilder.append(recordValue);
        }

        context.checking(new Expectations() {
            {
                one(mockDatabaseFile).writeByte(
                        with(equal((int) DatabaseConstants.VALID_RECORD_FLAG)));
                inSequence(sequence);

                one(mockDatabaseFile).write(
                        with(equal(recordBuilder.toString().getBytes(
                                DatabaseConstants.CHARACTER_SET))));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFindCalledWithNullCriteriaArray()
            throws Exception {
        standardSetup();
        data.find(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFindCalledWithInvalidCriteriaArrayLength()
            throws Exception {
        standardSetup();
        String[] criteria = new String[DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length - 1];
        data.find(criteria);
    }

    @Test
    public void shouldFindContractors() throws Exception {
        standardSetup();
        int[] matchingRecNos = { 1, 2 };
        String[] criteria = { "Buonarotti", null, "", null, null, "12345678" };
        String[][] allRecordValues = {
                DataTestConstants.padRecord(new String[] { "nm", "m", "", "m",
                        "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] { "Buonarotti", "m",
                        "", "m", "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] {
                        "Buonarotti & Family", "m", "", "m", "m", "12345678" },
                        ' '),
                DataTestConstants.padRecord(
                        new String[] { "Family of Buonarotti", "m", "", "m",
                                "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] { "Buonarotti", "m",
                        "", "m", "m", "nm" }, ' ') };
        Sequence sequence = context.sequence("find");
        checkingFindRecords(allRecordValues, sequence);
        int[] recNos = data.find(criteria);
        assertArrayEquals(matchingRecNos, recNos);
    }

    @Test
    public void shouldReturnAllRecordsWhenFindCalledWithAllCriteriaNull()
            throws Exception {
        standardSetup();
        int[] matchingRecNos = { 0, 1, 2, 3, 4 };
        String[] criteria = { null, null, null, null, null, null };
        String[][] allRecordValues = {
                DataTestConstants.padRecord(new String[] { "nm", "m", "", "m",
                        "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] { "Buonarotti", "m",
                        "", "m", "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] {
                        "Buonarotti & Family", "m", "", "m", "m", "12345678" },
                        ' '),
                DataTestConstants.padRecord(
                        new String[] { "Family of Buonarotti", "m", "", "m",
                                "m", "12345678" }, ' '),
                DataTestConstants.padRecord(new String[] { "Buonarotti", "m",
                        "", "m", "m", "nm" }, ' ') };
        Sequence sequence = context.sequence("find");
        checkingFindRecords(allRecordValues, sequence);
        int[] recNos = data.find(criteria);
        assertArrayEquals(matchingRecNos, recNos);
    }

    private void checkingFindRecords(final String[][] allRecordValues,
            final Sequence sequence) throws Exception {
        for (int recNo = 0; recNo < recordCount; recNo++) {
            if (!deletedRecNos.contains(recNo)) {
                final long recValuesStartPos = getOffsetForRecord(recNo)
                        + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;

                final StringBuilder recordBuilder = new StringBuilder();
                for (String element : allRecordValues[recNo]) {
                    recordBuilder.append(element);
                }

                context.checking(new Expectations() {
                    {
                        one(mockDatabaseFile).seek(recValuesStartPos);
                        inSequence(sequence);

                        one(mockDatabaseFile)
                                .readFully(with(any(byte[].class)));
                        will(readBytes(recordBuilder.toString().getBytes()));
                        inSequence(sequence);
                    }
                });
            }
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenIsLockedCalledWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.isLocked(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenIsLockedCalledWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.isLocked(recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenIsLockedCalledOnDeletedRecord()
            throws Exception {
        standardSetup();
        data.isLocked(deletedRecNos.first());
    }

    @Test
    public void shouldReturnTrueWhenIsLockedCalledOnLockedRecord()
            throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));
    }

    @Test
    public void shouldReturnFalseWhenIsLockedCalledOnUnlockedRecord()
            throws Exception {
        standardSetup();
        int recNo = 1;
        assertThat(data.isLocked(recNo), is(false));
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenLockCalledWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.lock(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenLockCalledWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.lock(recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenLockCalledForDeletedRecord()
            throws Exception {
        standardSetup();
        data.lock(deletedRecNos.first());
    }

    @Test
    public void shouldLockRecord() throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));
    }

    @Test
    public void shouldBeAbleToLockDifferentRecordsConcurrently()
            throws Exception {
        standardSetup();
        int firstRecNo = 1;
        data.lock(firstRecNo);
        assertThat(data.isLocked(firstRecNo), is(true));
        int secondRecNo = 2;
        data.lock(secondRecNo);
        assertThat(data.isLocked(secondRecNo), is(true));
    }

    @Test
    public void shouldSignalWaitingThreadAndThrowExceptionFromCallToLockIfRecordDeletedWhileAwaitingCondition()
            throws Exception {
        standardSetup();
        final Integer recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));

        Thread thread1 = createThreadThatLocksRecordAndExpectsException(recNo);
        thread1.start();
        Thread thread2 = createThreadThatLocksRecordAndExpectsException(recNo);
        thread2.start();

        // Wait for the threads 1&2 to become blocked before continuing
        Thread.sleep(1000);

        checkingDeleteRecord(recNo, context.sequence("delete"));
        data.delete(recNo);

        joinThread(thread1);
        joinThread(thread2);
    }

    @Test
    public void shouldThrowExceptionFromCallToLockIfInterruptedWhileAwaitingCondition()
            throws Exception {
        standardSetup();
        final Integer recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));

        Thread lockingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    data.lock(recNo);
                    failTest();
                } catch (RecordNotFoundException e) {
                    failTest();
                } catch (IllegalThreadStateException e) {
                    assertTrue(
                            "Exception cause was not an InterruptedException",
                            e.getCause() instanceof InterruptedException);
                }
            }

            private void failTest() {
                fail("Expected IllegalThreadStateException in thread: "
                        + Thread.currentThread().getName());
            }
        });
        lockingThread.start();
        Thread.sleep(1000);

        lockingThread.interrupt();
        joinThread(lockingThread);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUnlockCalledWithNegativeRecordNumber()
            throws Exception {
        standardSetup();
        data.unlock(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUnlockCalledWithRecordNumberGreaterThanNumberOfRecords()
            throws Exception {
        standardSetup();
        data.unlock(recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void shouldThrowExceptionWhenUnlockCalledOnDeletedRecord()
            throws Exception {
        standardSetup();
        data.unlock(deletedRecNos.first());
    }

    @Test
    public void shouldUnlockRecord() throws Exception {
        standardSetup();
        int recNo = 1;
        data.lock(recNo);
        assertThat(data.isLocked(recNo), is(true));
        data.unlock(recNo);
        assertThat(data.isLocked(recNo), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionFromCallToUnlockIfRecordNotLocked()
            throws Exception {
        standardSetup();
        int recNo = 0;
        assertThat(data.isLocked(recNo), is(false));
        data.unlock(recNo);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionFromCalledToUnlockIfThreadDoesNotHoldLock()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        data.unlock(recNo);
    }

    private ReadBytesAction readBytes(byte[] bytes) {
        return new ReadBytesAction(bytes);
    }

    private static final class ReadBytesAction implements Action {

        private final byte[] bytes;

        public ReadBytesAction(byte[] bytes) {
            this.bytes = bytes;
        }

        public void describeTo(Description description) {
            description.appendText("reads '").appendText(new String(bytes))
                    .appendText("' into a byte array");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            byte[] target = (byte[]) invocation.getParameter(0);
            for (int i = 0; i < bytes.length; i++) {
                target[i] = bytes[i];
            }
            return null;
        }
    }

    private static final class ExceptionHandler implements
            Thread.UncaughtExceptionHandler {

        private Throwable exception;

        public void uncaughtException(Thread t, Throwable e) {
            exception = e;
        }

        public Throwable getException() {
            return exception;
        }
    }
}
