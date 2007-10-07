package suncertify.db;


public class TestConcurrency {

    private static final int THREAD_COUNT = 1000;
    private static final int TXN_COUNT = 100;

    private final Data data;
    private Thread[] threads;
    private final Counter counter = new Counter();

    public TestConcurrency() throws Exception {
        this.data = new Data(new DatabaseFileImpl("suncertify.db"));
    }

    public void start() {
        this.threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < this.threads.length; i++) {
            this.threads[i] = new Thread(new RunTest());
            this.threads[i].start();
        }

        for (Thread thread : this.threads) {
            thread.interrupt();
        }

        this.counter.check(THREAD_COUNT * TXN_COUNT, null);
    }

    public static void main(String[] args) throws Exception {
        TestConcurrency test = new TestConcurrency();
        test.start();
    }

    public static void LOG(String logs) {
        System.out.println("<BR><B>" + Thread.currentThread().getName()
                + "</B>: " + logs);
    }

    private class RunTest implements Runnable {
        private RandomAction action;

        public void run() {
            for (int i = 0; i < TXN_COUNT; i++) {
                try {
                    LOG("Performing action:" + i);
                    runSingle();
                    LOG("Completed action:" + i + " on recNo:"
                            + this.action.index);

                } catch (IllegalThreadStateException e) {
                    LOG("Interrupted action:" + i + ", will go again");
                    i--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void runSingle() throws Exception {
            this.action = new RandomAction();
            LOG("    <I>Index:" + this.action.getIndex() + "</I>");

            try {
                switch (this.action.getMethodIndex()) {
                case 0:
                    doCreate();
                    break;
                case 1:
                    doRead();
                    break;
                case 2:
                    doUpdate();
                    break;
                case 3:
                    doDelete();
                    break;
                default:
                    doRead();
                    break;
                }
            } catch (RecordNotFoundException ex) {
                LOG("<I>    Record Index not Valid :" + this.action.getIndex()
                        + "</I>");
            }
            TestConcurrency.this.counter.hit();
        }

        private void doCreate() throws Exception {
            LOG("    <I>Method: Create</I>");
            String[] record = TestConcurrency.this.data.read(this.action
                    .getIndex());
            for (int i = 0; i < record.length; i++) {
                // Some value
                record[i] = "2";
            }
            TestConcurrency.this.data.create(record);
        }

        public void doRead() throws Exception {
            LOG("    <I>Method: Read</I>");
            TestConcurrency.this.data.read(this.action.getIndex());
        }

        public void doUpdate() throws Exception {
            LOG("    <I>Method: Update</I>");
            try {
                Thread.sleep(this.action.getExecutionTime());
            } catch (InterruptedException e) {
                // no-op
            }
            String[] record = TestConcurrency.this.data.read(this.action
                    .getIndex());
            // Some updation
            record[record.length - 1] = "1";
            TestConcurrency.this.data.lock(this.action.getIndex());
            try {
                TestConcurrency.this.data
                        .update(this.action.getIndex(), record);
            } finally {
                TestConcurrency.this.data.unlock(this.action.getIndex());
            }
        }

        public void doDelete() throws Exception {
            LOG("    <I>Method: Delete</I>");
            TestConcurrency.this.data.lock(this.action.getIndex());
            try {
                try {
                    Thread.sleep(this.action.getExecutionTime());
                } catch (InterruptedException e) {
                    // no-op
                }
                TestConcurrency.this.data.delete(this.action.getIndex());
            } finally {
                TestConcurrency.this.data.unlock(this.action.getIndex());
            }

            // No Unlocking that is taken care in the Delete method itself
        }
    }

    private static final class RandomAction {
        private static final int TEST_METHODS = 4;

        private final int index;
        private final int methodIndex;
        private final int executionTime;

        public RandomAction() {
            this.index = (int) (Math.random() * 100);
            this.methodIndex = (int) (Math.random() * TEST_METHODS);
            this.executionTime = (int) (Math.random() * 500);
        }

        public int getIndex() {
            return this.index;
        }

        public int getMethodIndex() {
            return this.methodIndex;
        }

        public int getExecutionTime() {
            return this.executionTime;
        }
    }

    private static final class Counter {

        /**
         * The number of hits for this counter.
         */
        private int hits;

        /**
         * Builds an <code>Counter</code> instance.
         * 
         */
        public Counter() {
            // UNIMPLEMENTED
        }

        /**
         * Hits once this <code>Counter</code>.
         * 
         */
        public synchronized void hit() {
            this.hits++;
        }

        /**
         * Tests if this <code>Counter</code> was hit for a specified number
         * of times.
         * 
         * @param toTest
         *                the number of hits.
         * @return true if this <code>Counter</code> was hit for a specified
         *         number of times.
         */
        private synchronized boolean isDone(int toTest) {
            return this.hits == toTest;
        }

        /**
         * Starts a threads which tests if this <code>Counter</code> was hit
         * for a specified number of times. After the test is done (and the
         * <code>Counter</code> was hit for a specified number of times) it
         * runs the <code>toRun</code> command.
         * 
         * @param hits
         *                the specified number of hits.
         * @param toRun
         *                this command runs after the
         *                <code>Counter</code> <code>isDone</code> method
         *                returns true.
         */
        public synchronized void check(int hits, Runnable toRun) {
            final Thread checkThread = new Thread(new CheckTarget(hits, toRun),
                    "checkThread");
            checkThread.start();
        }

        /**
         * Checks if this <code>Counter</code> was used for a specified number
         * of times by using the <code>isDone</code> method. <br>
         * If the test fails and the <code>isDone</code> method return false
         * then this thread must wait until this
         * <code>Counter</code> <code>isDone</code> method returns true (and
         * this is happen only if the Counter will be "hit" by a specified
         * number of times). <br>
         * If the test succeeds then it display at the standard output the
         * message : <i>Test succeed.</i> and it runs# a specified command (a
         * Runnable instance).
         */
        private final class CheckTarget implements Runnable {

            /**
             * The number of hits to check.
             */
            private final int must;

            /**
             * Runs after the Counter in done.
             */
            private final Runnable toRun;

            /**
             * Builds a <code>CheckTarget</code> for a specified number if
             * hits.
             * 
             * @param must
             *                the number of hits.
             * @param toRun
             *                this command runs after the
             *                <code>Counter</code> <code>isDone</code>
             *                method returns true.
             */
            private CheckTarget(int must, Runnable toRun) {
                this.must = must;
                this.toRun = toRun;
            }

            /**
             * Runs this client.
             */
            public void run() {
                synchronized (Counter.this) {
                    while (!isDone(this.must)) {
                        try {
                            Counter.this.wait(100);
                        } catch (InterruptedException e) {
                            // only log must be enough for this case.
                            e.printStackTrace();
                        }
                    }
                }

                if (this.toRun != null) {
                    this.toRun.run();
                }

                System.out.println("Test succeed.");
            }
        }
    }
}