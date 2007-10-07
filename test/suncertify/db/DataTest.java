package suncertify.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static suncertify.db.DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS;
import static suncertify.db.DataTestConstants.RECORD_VALUES;
import static suncertify.db.DataTestConstants.RECORD_VALUES_NULL_PADDED;
import static suncertify.db.DataTestConstants.RECORD_VALUES_SPACE_PADDED;
import static suncertify.db.DataTestConstants.padField;
import static suncertify.db.DataTestConstants.padRecord;
import static suncertify.db.DatabaseConstants.CHARACTER_SET;
import static suncertify.db.DatabaseConstants.DELETED_RECORD_FLAG;
import static suncertify.db.DatabaseConstants.FIELD_COUNT;
import static suncertify.db.DatabaseConstants.MAGIC_COOKIE;
import static suncertify.db.DatabaseConstants.RECORD_LENGTH;
import static suncertify.db.DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
import static suncertify.db.DatabaseConstants.VALID_RECORD_FLAG;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.AssertionFailedError;

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

@SuppressWarnings("boxing")
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
        // Set a default exception handler to pick failures in addtional threads
        this.exceptionHandler = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this.exceptionHandler);

        this.context = new Mockery();
        this.mockDatabaseFile = this.context.mock(DatabaseFile.class);
        this.dataSectionOffset = 999; // Value is irrelevant
        this.recordCount = 6;
        this.deletedRecNos = new TreeSet<Integer>();
        this.deletedRecNos.add(5);
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
        Throwable exception = this.exceptionHandler.getException();
        if (exception != null) {
            fail(exception.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionWithNullDatabaseFileThrowsException()
            throws Exception {
        this.data = new Data(null);
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidMagicCookieThrowsException()
            throws Exception {
        int invalidMagicCookie = -1;
        assertThat(invalidMagicCookie, is(not(MAGIC_COOKIE)));
        checkingMagicCookieRead(invalidMagicCookie, this.context
                .sequence("construction"));
        new Data(this.mockDatabaseFile);
    }

    private void checkingMagicCookieRead(final int magicCookie,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readInt();
                will(returnValue(magicCookie));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidRecordLengthThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(MAGIC_COOKIE, sequence);
        int invalidRecordLength = -1;
        assertThat(invalidRecordLength, is(not(RECORD_LENGTH)));
        checkingRecordLengthRead(invalidRecordLength, sequence);
        this.data = new Data(this.mockDatabaseFile);
    }

    private void checkingRecordLengthRead(final int recordLength,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readInt();
                will(returnValue(recordLength));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidFieldCountThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(RECORD_LENGTH, sequence);
        short invalidFieldCount = -1;
        assertThat(invalidFieldCount, is(not(FIELD_COUNT)));
        checkingFieldCount(invalidFieldCount, sequence);
        this.data = new Data(this.mockDatabaseFile);
    }

    private void checkingFieldCount(final short fieldCount,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readShort();
                will(returnValue(fieldCount));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidFieldDescriptionThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(RECORD_LENGTH, sequence);
        checkingFieldCount(FIELD_COUNT, sequence);

        short invalidFieldLength = -1;
        assertThat(invalidFieldLength,
                is(not((short) EXPECTED_FIELD_DESCRIPTIONS[0].getName()
                        .length())));
        FieldDescription invalidFieldDescription = new FieldDescription(
                EXPECTED_FIELD_DESCRIPTIONS[0].getName(), invalidFieldLength, 0);
        checkingFieldDescriptions(
                new FieldDescription[] { invalidFieldDescription }, sequence);

        this.data = new Data(this.mockDatabaseFile);
    }

    private void checkingFieldDescriptions(
            final FieldDescription[] fieldDescriptions, final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                for (FieldDescription element : fieldDescriptions) {
                    one(DataTest.this.mockDatabaseFile).readShort();
                    will(returnValue((short) element.getName().length()));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readFully(
                            with(any(byte[].class)));
                    will(readBytes(element.getName().getBytes()));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readShort();
                    will(returnValue(element.getLength()));
                    inSequence(sequence);
                }
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidDataSectionLengthThrowsException()
            throws Exception {
        final Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(RECORD_LENGTH, sequence);
        checkingFieldCount(FIELD_COUNT, sequence);
        checkingFieldDescriptions(EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).length();
                long validLength = getOffsetForRecord(DataTest.this.recordCount);
                will(returnValue(validLength - 1));
                inSequence(sequence);
            }
        });
        this.data = new Data(this.mockDatabaseFile);
    }

    private long getOffsetForRecord(int recNo) {
        return this.dataSectionOffset + recNo
                * (RECORD_VALIDITY_FLAG_LENGTH + RECORD_LENGTH);
    }

    @Test
    public void construction() throws Exception {
        standardSetup();
    }

    private void standardSetup() throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(RECORD_LENGTH, sequence);
        checkingFieldCount(FIELD_COUNT, sequence);
        checkingFieldDescriptions(EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        checkingRecordCount(sequence);
        checkingCacheDeletedRecordNumbers(sequence);
        this.data = new Data(this.mockDatabaseFile);
        assertSchema();
        assertThat(this.data.getDataSectionOffset(), is(this.dataSectionOffset));
        assertThat(this.data.getRecordCount(), is(this.recordCount));
        assertDeletedRecords();
    }

    private void checkingDataSectionOffset(final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).getFilePointer();
                will(returnValue(DataTest.this.dataSectionOffset));
                inSequence(sequence);
            }
        });
    }

    private void checkingRecordCount(final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).length();
                will(returnValue(DataTest.this.dataSectionOffset
                        + DataTest.this.recordCount
                        * (RECORD_VALIDITY_FLAG_LENGTH + RECORD_LENGTH)));
                inSequence(sequence);
            }
        });
    }

    private void checkingCacheDeletedRecordNumbers(final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                for (int recNo = 0; recNo < DataTest.this.recordCount; recNo++) {
                    one(DataTest.this.mockDatabaseFile).seek(
                            with(equal(getOffsetForRecord(recNo))));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readByte();
                    will(returnValue(DataTest.this.deletedRecNos
                            .contains(recNo) ? DELETED_RECORD_FLAG
                            : VALID_RECORD_FLAG));
                    inSequence(sequence);
                }
            }
        });
    }

    private void assertSchema() {
        DatabaseSchema schema = this.data.getDatabaseSchema();
        assertThat(schema.getRecordLength(), is(RECORD_LENGTH));
        assertThat(schema.getFieldCount(), is(FIELD_COUNT));

        FieldDescription[] fieldDescriptions = schema.getFieldDescriptions();
        assertThat(fieldDescriptions, is(notNullValue()));
        assertThat(fieldDescriptions.length,
                is(EXPECTED_FIELD_DESCRIPTIONS.length));
        for (int i = 0; i < fieldDescriptions.length; i++) {
            assertThat(fieldDescriptions[i].getName(),
                    is(EXPECTED_FIELD_DESCRIPTIONS[i].getName()));
            assertThat(fieldDescriptions[i].getLength(),
                    is(EXPECTED_FIELD_DESCRIPTIONS[i].getLength()));
            assertThat(fieldDescriptions[i].getRecordOffset(),
                    is(EXPECTED_FIELD_DESCRIPTIONS[i].getRecordOffset()));
        }
    }

    private void assertDeletedRecords() {
        for (int recNo = 0; recNo < this.recordCount; recNo++) {
            assertThat(this.data.isRecordDeleted(recNo), is(this.deletedRecNos
                    .contains(recNo)));
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void readWithNegativeRecordNumberThrowsException() throws Exception {
        standardSetup();
        this.data.read(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.read(this.recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readingDeletedRecordThrowsException() throws Exception {
        standardSetup();
        this.data.read(this.deletedRecNos.first());
    }

    @Test
    public void readSpacePadded() throws Exception {
        standardSetup();
        read(1, RECORD_VALUES_SPACE_PADDED, RECORD_VALUES);
    }

    private void read(final int recordNumber,
            final String[] recordValuesToRead, String[] expectedRecordValues)
            throws Exception {
        final Sequence sequence = this.context.sequence("read");
        checkingSeek(getOffsetForRecord(recordNumber)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH, sequence);

        final StringBuilder recordBuilder = new StringBuilder();
        for (String element : recordValuesToRead) {
            recordBuilder.append(element);
        }

        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readFully(
                        with(any(byte[].class)));
                will(readBytes(recordBuilder.toString().getBytes()));
                inSequence(sequence);
            }
        });

        String[] actualRecordValues = this.data.read(recordNumber);
        assertThat(actualRecordValues.length, is(expectedRecordValues.length));
        for (int i = 0; i < actualRecordValues.length; i++) {
            assertThat(actualRecordValues[i], is(expectedRecordValues[i]));
        }
    }

    private void checkingSeek(final long pos, final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).seek(pos);
                inSequence(sequence);
            }
        });
    }

    @Test
    public void readNullPadded() throws Exception {
        standardSetup();
        read(1, RECORD_VALUES_NULL_PADDED, RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateWithNegativeRecordNumberThrowsException()
            throws Exception {
        standardSetup();
        this.data.update(-1, RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.update(this.recordCount, RECORD_VALUES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullDataArrayThrowsException() throws Exception {
        standardSetup();
        int recNo = 0;
        this.data.lock(recNo);
        this.data.update(recNo, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithInvalidDataArrayLengthThrowsException()
            throws Exception {
        standardSetup();
        int recNo = 0;
        this.data.lock(recNo);
        String[] recordValues = new String[FIELD_COUNT + 1];
        System.arraycopy(RECORD_VALUES, recNo, recordValues, recNo,
                RECORD_VALUES.length);
        this.data.update(recNo, recordValues);
    }

    @Test
    public void updateWithNullRecordValueDoesNotWriteCorrespondingField()
            throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        String[] recordValues = RECORD_VALUES_SPACE_PADDED.clone();
        recordValues[1] = null;
        checkingUpdateRecord(recNo, recordValues, sequence);
        this.data.update(recNo, recordValues);
    }

    @Test
    public void updateWithTooLongRecordValueTruncatesData() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        String[] recordValues = RECORD_VALUES.clone();
        recordValues[1] = RECORD_VALUES_SPACE_PADDED[1] + " too long";
        checkingUpdateRecord(recNo, RECORD_VALUES_SPACE_PADDED, sequence);
        this.data.update(recNo, recordValues);
    }

    @Test
    public void updateWithEmptyStringRecordValueBlanksField() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        String[] recordValues = RECORD_VALUES_SPACE_PADDED.clone();
        recordValues[1] = "";
        String[] paddedRecordValues = recordValues.clone();
        paddedRecordValues[1] = padField(paddedRecordValues[1],
                EXPECTED_FIELD_DESCRIPTIONS[1].getLength(), ' ');
        checkingUpdateRecord(recNo, paddedRecordValues, sequence);
        this.data.update(recNo, recordValues);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateDeletedRecordThrowsException() throws Exception {
        standardSetup();
        Integer recNo = this.deletedRecNos.first();
        this.data.update(recNo, RECORD_VALUES);
    }

    @Test(expected = IllegalThreadStateException.class)
    public void updateRecordWhenNotHoldingLockThrowsException()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    throw new AssertionFailedError(Thread.currentThread()
                            .getName()
                            + ": " + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        this.data.update(recNo, RECORD_VALUES);
    }

    @Test
    public void update() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        checkingUpdateRecord(recNo, RECORD_VALUES_SPACE_PADDED, sequence);
        this.data.update(recNo, RECORD_VALUES);
    }

    private void checkingUpdateRecord(int recNo, final String[] recordValues,
            final Sequence sequence) throws Exception {
        final long recordValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        this.context.checking(new Expectations() {
            {
                for (int i = 0; i < EXPECTED_FIELD_DESCRIPTIONS.length; i++) {
                    if (recordValues[i] != null) {
                        one(DataTest.this.mockDatabaseFile).seek(
                                with(equal(recordValuesStartPos
                                        + EXPECTED_FIELD_DESCRIPTIONS[i]
                                                .getRecordOffset())));
                        inSequence(sequence);

                        one(DataTest.this.mockDatabaseFile).write(
                                with(equal(recordValues[i]
                                        .getBytes(CHARACTER_SET))));
                        inSequence(sequence);
                    }
                }
            }
        });
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteWithNegativeRecordNumberThrowsException()
            throws Exception {
        standardSetup();
        this.data.delete(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.delete(this.recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteDeletedRecordThrowsException() throws Exception {
        standardSetup();
        Integer recNo = this.deletedRecNos.first();
        this.data.delete(recNo);
    }

    @Test
    public void delete() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        Sequence sequence = this.context.sequence("delete");
        checkingDeleteRecord(recNo, sequence);
        this.data.delete(recNo);
        this.data.isRecordDeleted(recNo);
    }

    private void checkingDeleteRecord(int recNo, final Sequence sequence)
            throws Exception {
        checkingSeek(getOffsetForRecord(recNo), sequence);
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).writeByte(
                        DELETED_RECORD_FLAG);
                inSequence(sequence);
            }
        });
    }

    @Test(expected = IllegalThreadStateException.class)
    public void deleteRecordWhenNotHoldingLockThrowsException()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    throw new AssertionFailedError(Thread.currentThread()
                            .getName()
                            + ": " + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        this.data.delete(recNo);
    }

    @Test
    public void deleteRecordSignalsWaitingRecord() throws Exception {
        standardSetup();
        final Integer recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));

        // Attempt to lock the same record on another thread - will block
        Thread lockingThread = createThreadThatLocksRecordAndExpectsException(recNo);
        lockingThread.start();

        // Wait for the lockingThread to become blocked before continuing
        Thread.sleep(1000);

        checkingDeleteRecord(recNo, this.context.sequence("delete"));
        this.data.delete(recNo);

        joinThread(lockingThread);
    }

    private Thread createThreadThatLocksRecordAndExpectsException(
            final int recNo) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                    throw new AssertionFailedError(
                            "Expected RecordNotFoundException in thread: "
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
    public void createWithNullDataArrayThrowsException() throws Exception {
        standardSetup();
        this.data.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithInvalidDataArrayLengthThrowsException()
            throws Exception {
        standardSetup();
        String[] recordValues = new String[FIELD_COUNT + 1];
        System.arraycopy(RECORD_VALUES, 0, recordValues, 0,
                RECORD_VALUES.length);
        this.data.create(recordValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullRecordValueThrowsException() throws Exception {
        standardSetup();
        String[] recordValues = RECORD_VALUES_SPACE_PADDED.clone();
        recordValues[1] = null;
        this.data.create(recordValues);
    }

    @Test
    public void createWritesToEndOfFileWhenNoDeletedRecords() throws Exception {
        this.deletedRecNos.clear();
        standardSetup();
        int recNoToWrite = this.recordCount;
        Sequence sequence = this.context.sequence("create");
        checkingCreateRecord(recNoToWrite, RECORD_VALUES_SPACE_PADDED, sequence);
        assertThat(this.data.create(RECORD_VALUES), is(recNoToWrite));
        assertThat(this.data.getRecordCount(), is(this.recordCount + 1));
    }

    @Test
    public void createWritesDeletedRecordWhenAvailable() throws Exception {
        standardSetup();
        int recNoToWrite = this.deletedRecNos.first();
        Sequence sequence = this.context.sequence("create");
        checkingCreateRecord(recNoToWrite, RECORD_VALUES_SPACE_PADDED, sequence);
        assertThat(this.data.create(RECORD_VALUES), is(recNoToWrite));
        assertThat(this.data.getRecordCount(), is(this.recordCount));
    }

    private void checkingCreateRecord(final int firstAvailableRecNo,
            final String[] recordValues, final Sequence sequence)
            throws Exception {
        checkingSeek(getOffsetForRecord(firstAvailableRecNo), sequence);

        final StringBuilder recordBuilder = new StringBuilder();
        for (String recordValue : recordValues) {
            recordBuilder.append(recordValue);
        }

        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).writeByte(
                        with(equal((int) DatabaseConstants.VALID_RECORD_FLAG)));
                inSequence(sequence);

                one(DataTest.this.mockDatabaseFile).write(
                        with(equal(recordBuilder.toString().getBytes(
                                CHARACTER_SET))));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void findWithNullCriteriaArrayThrowsException() throws Exception {
        standardSetup();
        this.data.find(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findWithInvalidCriteriaArrayLengthThrowsException()
            throws Exception {
        standardSetup();
        String[] criteria = new String[EXPECTED_FIELD_DESCRIPTIONS.length - 1];
        this.data.find(criteria);
    }

    @Test
    public void find() throws Exception {
        standardSetup();
        int[] matchingRecNos = { 1, 2 };
        String[] criteria = { "Buonarotti", null, "", null, null, "12345678" };
        String[][] allRecordValues = {
                padRecord(new String[] { "nm", "m", "", "m", "m", "12345678" },
                        ' '),
                padRecord(new String[] { "Buonarotti", "m", "", "m", "m",
                        "12345678" }, ' '),
                padRecord(new String[] { "Buonarotti & Family", "m", "", "m",
                        "m", "12345678" }, ' '),
                padRecord(new String[] { "Family of Buonarotti", "m", "", "m",
                        "m", "12345678" }, ' '),
                padRecord(
                        new String[] { "Buonarotti", "m", "", "m", "m", "nm" },
                        ' ') };
        Sequence sequence = this.context.sequence("find");
        checkingFindRecords(allRecordValues, sequence);
        int[] recNos = this.data.find(criteria);
        assertArrayEquals(matchingRecNos, recNos);
    }

    private void checkingFindRecords(final String[][] allRecordValues,
            final Sequence sequence) throws Exception {
        for (int recNo = 0; recNo < this.recordCount; recNo++) {
            if (!this.deletedRecNos.contains(recNo)) {
                final long recValuesStartPos = getOffsetForRecord(recNo)
                        + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;

                final StringBuilder recordBuilder = new StringBuilder();
                for (String element : allRecordValues[recNo]) {
                    recordBuilder.append(element);
                }

                this.context.checking(new Expectations() {
                    {
                        one(DataTest.this.mockDatabaseFile).seek(
                                recValuesStartPos);
                        inSequence(sequence);

                        one(DataTest.this.mockDatabaseFile).readFully(
                                with(any(byte[].class)));
                        will(readBytes(recordBuilder.toString().getBytes()));
                        inSequence(sequence);
                    }
                });
            }
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void isLockedWithNegativeRecordNumberThrowsException()
            throws Exception {
        standardSetup();
        this.data.isLocked(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void isLockedWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.isLocked(this.recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void isLockedOnDeletedRecordThrowsException() throws Exception {
        standardSetup();
        this.data.isLocked(this.deletedRecNos.first());
    }

    @Test
    public void isLockedOnLockedRecordReturnsTrue() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));
    }

    @Test
    public void isLockedOnUnlockedRecordReturnsFalse() throws Exception {
        standardSetup();
        int recNo = 1;
        assertThat(this.data.isLocked(recNo), is(false));
    }

    @Test(expected = RecordNotFoundException.class)
    public void lockWithNegativeRecordNumberThrowsException() throws Exception {
        standardSetup();
        this.data.lock(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void lockWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.lock(this.recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void lockDeletedRecordThrowsException() throws Exception {
        standardSetup();
        this.data.lock(this.deletedRecNos.first());
    }

    @Test
    public void lock() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));
    }

    @Test
    public void canLockDifferentRecordsConcurrently() throws Exception {
        standardSetup();
        int firstRecNo = 1;
        this.data.lock(firstRecNo);
        assertThat(this.data.isLocked(firstRecNo), is(true));
        int secondRecNo = 2;
        this.data.lock(secondRecNo);
        assertThat(this.data.isLocked(secondRecNo), is(true));
    }

    @Test
    public void lockSignalsWaitingThreadAndThrowsExceptionIfRecordDeletedWhileAwaitingCondition()
            throws Exception {
        standardSetup();
        final Integer recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));

        Thread thread1 = createThreadThatLocksRecordAndExpectsException(recNo);
        thread1.start();
        Thread thread2 = createThreadThatLocksRecordAndExpectsException(recNo);
        thread2.start();

        // Wait for the threads 1&2 to become blocked before continuing
        Thread.sleep(1000);

        checkingDeleteRecord(recNo, this.context.sequence("delete"));
        this.data.delete(recNo);

        joinThread(thread1);
        joinThread(thread2);
    }

    @Test
    public void lockThrowsExceptionIfInterruptedWhileAwaitingCondition()
            throws Exception {
        standardSetup();
        final Integer recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));

        Thread lockingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                    throwError();
                } catch (RecordNotFoundException e) {
                    throwError();
                } catch (IllegalThreadStateException e) {
                    // Expected
                }
            }

            private void throwError() throws AssertionFailedError {
                throw new AssertionFailedError(
                        "Expected IllegalThreadStateException in thread: "
                                + Thread.currentThread().getName());
            }
        });
        lockingThread.start();
        Thread.sleep(1000);

        lockingThread.interrupt();
        joinThread(lockingThread);
    }

    @Test(expected = RecordNotFoundException.class)
    public void unlockWithNegativeRecordNumberThrowsException()
            throws Exception {
        standardSetup();
        this.data.unlock(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void unlockWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.unlock(this.recordCount);
    }

    @Test(expected = RecordNotFoundException.class)
    public void unlockDeletedRecordThrowsException() throws Exception {
        standardSetup();
        this.data.unlock(this.deletedRecNos.first());
    }

    @Test
    public void unlock() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        assertThat(this.data.isLocked(recNo), is(true));
        this.data.unlock(recNo);
        assertThat(this.data.isLocked(recNo), is(false));
    }

    @Test(expected = IllegalThreadStateException.class)
    public void unlockThrowsExceptionIfRecordNotLocked() throws Exception {
        standardSetup();
        int recNo = 0;
        assertThat(this.data.isLocked(recNo), is(false));
        this.data.unlock(recNo);
    }

    @Test(expected = IllegalThreadStateException.class)
    public void unlockThrowsExceptionIfThreadDoesNotHoldLock() throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    throw new AssertionFailedError(Thread.currentThread()
                            .getName()
                            + ": " + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        this.data.unlock(recNo);
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
            description.appendText("reads '")
                    .appendText(new String(this.bytes)).appendText(
                            "' into a byte array");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            byte[] target = (byte[]) invocation.getParameter(0);
            for (int i = 0; i < this.bytes.length; i++) {
                target[i] = this.bytes[i];
            }
            return null;
        }
    }

    private static final class ExceptionHandler implements
            UncaughtExceptionHandler {

        private Throwable exception;

        public void uncaughtException(Thread t, Throwable e) {
            this.exception = e;
        }

        public Throwable getException() {
            return this.exception;
        }
    }
}
