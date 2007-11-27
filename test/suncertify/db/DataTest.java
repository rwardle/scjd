package suncertify.db;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DatabaseSchema.FieldDescription;
import suncertify.util.ExceptionHandler;

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
            Assert.fail(exception.getMessage());
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
        Assert.assertThat(invalidMagicCookie, CoreMatchers.is(CoreMatchers
                .not(DatabaseConstants.MAGIC_COOKIE)));
        checkingMagicCookieRead(invalidMagicCookie, this.context
                .sequence("construction"));
        new Data(this.mockDatabaseFile);
    }

    private void checkingMagicCookieRead(final int magicCookie,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readInt();
                will(Expectations.returnValue(magicCookie));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidRecordLengthThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        int invalidRecordLength = -1;
        Assert.assertThat(invalidRecordLength, CoreMatchers.is(CoreMatchers
                .not(DatabaseConstants.RECORD_LENGTH)));
        checkingRecordLengthRead(invalidRecordLength, sequence);
        this.data = new Data(this.mockDatabaseFile);
    }

    private void checkingRecordLengthRead(final int recordLength,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readInt();
                will(Expectations.returnValue(recordLength));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidFieldCountThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DatabaseConstants.RECORD_LENGTH, sequence);
        short invalidFieldCount = -1;
        Assert.assertThat(invalidFieldCount, CoreMatchers.is(CoreMatchers
                .not(DatabaseConstants.FIELD_COUNT)));
        checkingFieldCount(invalidFieldCount, sequence);
        this.data = new Data(this.mockDatabaseFile);
    }

    private void checkingFieldCount(final short fieldCount,
            final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).readShort();
                will(Expectations.returnValue(fieldCount));
                inSequence(sequence);
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidFieldDescriptionThrowsException()
            throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DatabaseConstants.RECORD_LENGTH, sequence);
        checkingFieldCount(DatabaseConstants.FIELD_COUNT, sequence);

        short invalidFieldLength = -1;
        Assert.assertThat(invalidFieldLength, CoreMatchers.is(CoreMatchers
                .not((short) DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[0]
                        .getName().length())));
        FieldDescription invalidFieldDescription = new FieldDescription(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[0].getName(),
                invalidFieldLength, 0);
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
                    will(Expectations.returnValue((short) element.getName()
                            .length()));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readFully(
                            with(Expectations.any(byte[].class)));
                    will(readBytes(element.getName().getBytes()));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readShort();
                    will(Expectations.returnValue(element.getLength()));
                    inSequence(sequence);
                }
            }
        });
    }

    @Test(expected = DataValidationException.class)
    public void constructionWithInvalidDataSectionLengthThrowsException()
            throws Exception {
        final Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DatabaseConstants.RECORD_LENGTH, sequence);
        checkingFieldCount(DatabaseConstants.FIELD_COUNT, sequence);
        checkingFieldDescriptions(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).length();
                long validLength = getOffsetForRecord(DataTest.this.recordCount);
                will(Expectations.returnValue(validLength - 1));
                inSequence(sequence);
            }
        });
        this.data = new Data(this.mockDatabaseFile);
    }

    private long getOffsetForRecord(int recNo) {
        return this.dataSectionOffset
                + recNo
                * (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + DatabaseConstants.RECORD_LENGTH);
    }

    @Test
    public void construction() throws Exception {
        standardSetup();
    }

    private void standardSetup() throws Exception {
        Sequence sequence = this.context.sequence("construction");
        checkingMagicCookieRead(DatabaseConstants.MAGIC_COOKIE, sequence);
        checkingRecordLengthRead(DatabaseConstants.RECORD_LENGTH, sequence);
        checkingFieldCount(DatabaseConstants.FIELD_COUNT, sequence);
        checkingFieldDescriptions(
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS, sequence);
        checkingDataSectionOffset(sequence);
        checkingRecordCount(sequence);
        checkingCacheDeletedRecordNumbers(sequence);
        this.data = new Data(this.mockDatabaseFile);
        assertSchema();
        Assert.assertThat(this.data.getDataSectionOffset(), CoreMatchers
                .is(this.dataSectionOffset));
        Assert.assertThat(this.data.getRecordCount(), CoreMatchers
                .is(this.recordCount));
        assertDeletedRecords();
    }

    private void checkingDataSectionOffset(final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).getFilePointer();
                will(Expectations.returnValue(DataTest.this.dataSectionOffset));
                inSequence(sequence);
            }
        });
    }

    private void checkingRecordCount(final Sequence sequence) throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataTest.this.mockDatabaseFile).length();
                will(Expectations
                        .returnValue(DataTest.this.dataSectionOffset
                                + DataTest.this.recordCount
                                * (DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH + DatabaseConstants.RECORD_LENGTH)));
                inSequence(sequence);
            }
        });
    }

    private void checkingCacheDeletedRecordNumbers(final Sequence sequence)
            throws Exception {
        this.context.checking(new Expectations() {
            {
                for (int recNo = 0; recNo < DataTest.this.recordCount; recNo++) {
                    one(DataTest.this.mockDatabaseFile)
                            .seek(
                                    with(Expectations
                                            .equal(getOffsetForRecord(recNo))));
                    inSequence(sequence);

                    one(DataTest.this.mockDatabaseFile).readByte();
                    will(Expectations
                            .returnValue(DataTest.this.deletedRecNos
                                    .contains(recNo) ? DatabaseConstants.DELETED_RECORD_FLAG
                                    : DatabaseConstants.VALID_RECORD_FLAG));
                    inSequence(sequence);
                }
            }
        });
    }

    private void assertSchema() {
        DatabaseSchema schema = this.data.getDatabaseSchema();
        Assert.assertThat(schema.getRecordLength(), CoreMatchers
                .is(DatabaseConstants.RECORD_LENGTH));
        Assert.assertThat(schema.getFieldCount(), CoreMatchers
                .is(DatabaseConstants.FIELD_COUNT));

        FieldDescription[] fieldDescriptions = schema.getFieldDescriptions();
        Assert.assertThat(fieldDescriptions, CoreMatchers.is(CoreMatchers
                .notNullValue()));
        Assert.assertThat(fieldDescriptions.length, CoreMatchers
                .is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length));
        for (int i = 0; i < fieldDescriptions.length; i++) {
            Assert.assertThat(fieldDescriptions[i].getName(), CoreMatchers
                    .is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                            .getName()));
            Assert.assertThat(fieldDescriptions[i].getLength(), CoreMatchers
                    .is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                            .getLength()));
            Assert
                    .assertThat(
                            fieldDescriptions[i].getRecordOffset(),
                            CoreMatchers
                                    .is(DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                                            .getRecordOffset()));
        }
    }

    private void assertDeletedRecords() {
        for (int recNo = 0; recNo < this.recordCount; recNo++) {
            Assert.assertThat(this.data.isRecordDeleted(recNo), CoreMatchers
                    .is(this.deletedRecNos.contains(recNo)));
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
        read(1, DataTestConstants.RECORD_VALUES_SPACE_PADDED,
                DataTestConstants.RECORD_VALUES);
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
                        with(Expectations.any(byte[].class)));
                will(readBytes(recordBuilder.toString().getBytes()));
                inSequence(sequence);
            }
        });

        String[] actualRecordValues = this.data.read(recordNumber);
        Assert.assertThat(actualRecordValues.length, CoreMatchers
                .is(expectedRecordValues.length));
        for (int i = 0; i < actualRecordValues.length; i++) {
            Assert.assertThat(actualRecordValues[i], CoreMatchers
                    .is(expectedRecordValues[i]));
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
        read(1, DataTestConstants.RECORD_VALUES_NULL_PADDED,
                DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateWithNegativeRecordNumberThrowsException()
            throws Exception {
        standardSetup();
        this.data.update(-1, DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateWithRecordNumberGreaterThanNumberOfRecordsThrowsException()
            throws Exception {
        standardSetup();
        this.data.update(this.recordCount, DataTestConstants.RECORD_VALUES);
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
        String[] recordValues = new String[DatabaseConstants.FIELD_COUNT + 1];
        System.arraycopy(DataTestConstants.RECORD_VALUES, recNo, recordValues,
                recNo, DataTestConstants.RECORD_VALUES.length);
        this.data.update(recNo, recordValues);
    }

    @Test
    public void updateWithNullRecordValueDoesNotWriteCorrespondingField()
            throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
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
        String[] recordValues = DataTestConstants.RECORD_VALUES.clone();
        recordValues[1] = DataTestConstants.RECORD_VALUES_SPACE_PADDED[1]
                + " too long";
        checkingUpdateRecord(recNo,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        this.data.update(recNo, recordValues);
    }

    @Test
    public void updateWithEmptyStringRecordValueBlanksField() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
        recordValues[1] = "";
        String[] paddedRecordValues = recordValues.clone();
        paddedRecordValues[1] = DataTestConstants.padField(
                paddedRecordValues[1],
                DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[1].getLength(),
                ' ');
        checkingUpdateRecord(recNo, paddedRecordValues, sequence);
        this.data.update(recNo, recordValues);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateDeletedRecordThrowsException() throws Exception {
        standardSetup();
        Integer recNo = this.deletedRecNos.first();
        this.data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    @Test(expected = IllegalStateException.class)
    public void updateRecordWhenNotHoldingLockThrowsException()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    Assert.fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
                }
            }
        });
        lockThread.start();
        lockThread.join();
        this.data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    @Test
    public void update() throws Exception {
        standardSetup();
        int recNo = 1;
        this.data.lock(recNo);
        final Sequence sequence = this.context.sequence("update");
        checkingUpdateRecord(recNo,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        this.data.update(recNo, DataTestConstants.RECORD_VALUES);
    }

    private void checkingUpdateRecord(int recNo, final String[] recordValues,
            final Sequence sequence) throws Exception {
        final long recordValuesStartPos = getOffsetForRecord(recNo)
                + DatabaseConstants.RECORD_VALIDITY_FLAG_LENGTH;
        this.context.checking(new Expectations() {
            {
                for (int i = 0; i < DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length; i++) {
                    if (recordValues[i] != null) {
                        one(DataTest.this.mockDatabaseFile)
                                .seek(
                                        with(Expectations
                                                .equal(recordValuesStartPos
                                                        + DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS[i]
                                                                .getRecordOffset())));
                        inSequence(sequence);

                        one(DataTest.this.mockDatabaseFile)
                                .write(
                                        with(Expectations
                                                .equal(recordValues[i]
                                                        .getBytes(DatabaseConstants.CHARACTER_SET))));
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
                        DatabaseConstants.DELETED_RECORD_FLAG);
                inSequence(sequence);
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void deleteRecordWhenNotHoldingLockThrowsException()
            throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    Assert.fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
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
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));

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
                    Assert.fail("Expected RecordNotFoundException in thread: "
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
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(false));
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
        String[] recordValues = new String[DatabaseConstants.FIELD_COUNT + 1];
        System.arraycopy(DataTestConstants.RECORD_VALUES, 0, recordValues, 0,
                DataTestConstants.RECORD_VALUES.length);
        this.data.create(recordValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullRecordValueThrowsException() throws Exception {
        standardSetup();
        String[] recordValues = DataTestConstants.RECORD_VALUES_SPACE_PADDED
                .clone();
        recordValues[1] = null;
        this.data.create(recordValues);
    }

    @Test
    public void createWritesToEndOfFileWhenNoDeletedRecords() throws Exception {
        this.deletedRecNos.clear();
        standardSetup();
        int recNoToWrite = this.recordCount;
        Sequence sequence = this.context.sequence("create");
        checkingCreateRecord(recNoToWrite,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        Assert.assertThat(this.data.create(DataTestConstants.RECORD_VALUES),
                CoreMatchers.is(recNoToWrite));
        Assert.assertThat(this.data.getRecordCount(), CoreMatchers
                .is(this.recordCount + 1));
    }

    @Test
    public void createWritesDeletedRecordWhenAvailable() throws Exception {
        standardSetup();
        int recNoToWrite = this.deletedRecNos.first();
        Sequence sequence = this.context.sequence("create");
        checkingCreateRecord(recNoToWrite,
                DataTestConstants.RECORD_VALUES_SPACE_PADDED, sequence);
        Assert.assertThat(this.data.create(DataTestConstants.RECORD_VALUES),
                CoreMatchers.is(recNoToWrite));
        Assert.assertThat(this.data.getRecordCount(), CoreMatchers
                .is(this.recordCount));
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
                one(DataTest.this.mockDatabaseFile)
                        .writeByte(
                                with(Expectations
                                        .equal((int) DatabaseConstants.VALID_RECORD_FLAG)));
                inSequence(sequence);

                one(DataTest.this.mockDatabaseFile).write(
                        with(Expectations.equal(recordBuilder.toString()
                                .getBytes(DatabaseConstants.CHARACTER_SET))));
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
        String[] criteria = new String[DataTestConstants.EXPECTED_FIELD_DESCRIPTIONS.length - 1];
        this.data.find(criteria);
    }

    @Test
    public void find() throws Exception {
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
        Sequence sequence = this.context.sequence("find");
        checkingFindRecords(allRecordValues, sequence);
        int[] recNos = this.data.find(criteria);
        Assert.assertArrayEquals(matchingRecNos, recNos);
    }

    @Test
    public void findWithAllCriteriaNullReturnsAllRecords() throws Exception {
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
        Sequence sequence = this.context.sequence("find");
        checkingFindRecords(allRecordValues, sequence);
        int[] recNos = this.data.find(criteria);
        Assert.assertArrayEquals(matchingRecNos, recNos);
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
                                with(Expectations.any(byte[].class)));
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
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));
    }

    @Test
    public void isLockedOnUnlockedRecordReturnsFalse() throws Exception {
        standardSetup();
        int recNo = 1;
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(false));
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
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));
    }

    @Test
    public void canLockDifferentRecordsConcurrently() throws Exception {
        standardSetup();
        int firstRecNo = 1;
        this.data.lock(firstRecNo);
        Assert
                .assertThat(this.data.isLocked(firstRecNo), CoreMatchers
                        .is(true));
        int secondRecNo = 2;
        this.data.lock(secondRecNo);
        Assert.assertThat(this.data.isLocked(secondRecNo), CoreMatchers
                .is(true));
    }

    @Test
    public void lockSignalsWaitingThreadAndThrowsExceptionIfRecordDeletedWhileAwaitingCondition()
            throws Exception {
        standardSetup();
        final Integer recNo = 1;
        this.data.lock(recNo);
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));

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
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));

        Thread lockingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                    fail();
                } catch (RecordNotFoundException e) {
                    fail();
                } catch (IllegalThreadStateException e) {
                    Assert.assertTrue(
                            "Exception cause was not an InterruptedException",
                            e.getCause() instanceof InterruptedException);
                }
            }

            private void fail() {
                Assert.fail("Expected IllegalThreadStateException in thread: "
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
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(true));
        this.data.unlock(recNo);
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void unlockThrowsExceptionIfRecordNotLocked() throws Exception {
        standardSetup();
        int recNo = 0;
        Assert.assertThat(this.data.isLocked(recNo), CoreMatchers.is(false));
        this.data.unlock(recNo);
    }

    @Test(expected = IllegalStateException.class)
    public void unlockThrowsExceptionIfThreadDoesNotHoldLock() throws Exception {
        standardSetup();
        final int recNo = 1;
        Thread lockThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DataTest.this.data.lock(recNo);
                } catch (RecordNotFoundException e) {
                    Assert.fail(Thread.currentThread().getName() + ": "
                            + e.getMessage());
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
}
