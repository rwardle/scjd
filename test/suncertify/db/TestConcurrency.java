package suncertify.db;

public class TestConcurrency {

    private static final int THREAD_COUNT = 1000;
    private static final int TXN_COUNT = 100;

    private final Data data;
    private Thread[] threads;
    private final Counter counter = new Counter();

    public TestConcurrency() throws Exception {
        data = new Data(new DatabaseFileImpl("suncertify.db"));
    }

    public void start() {
        threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new RunTest());
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }

        counter.check(THREAD_COUNT * TXN_COUNT, null);
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
                    LOG("Completed action:" + i + " on recNo:" + action.index);

                } catch (IllegalThreadStateException e) {
                    LOG("Interrupted action:" + i + ", will go again");
                    i--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void runSingle() throws Exception {
            action = new RandomAction();
            LOG("    <I>Index:" + action.getIndex() + "</I>");

            try {
                switch (action.getMethodIndex()) {
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
                LOG("<I>    Record Index not Valid :" + action.getIndex()
                        + "</I>");
            } catch (IllegalStateException ex) {
                TestConcurrency
                        .LOG("<I>    Thread does not hold lock on record :"
                                + action.getIndex() + "</I>");
            }

            counter.hit();
        }

        private void doCreate() throws Exception {
            LOG("    <I>Method: Create</I>");
            String[] record = data.read(action.getIndex());
            for (int i = 0; i < record.length; i++) {
                // Some value
                record[i] = "abcdefghijklmnopqrstuvwxyz";
            }
            data.create(record);
        }

        public void doRead() throws Exception {
            LOG("    <I>Method: Read</I>");
            data.read(action.getIndex());
        }

        public void doUpdate() throws Exception {
            LOG("    <I>Method: Update</I>");
            try {
                Thread.sleep(action.getExecutionTime());
            } catch (InterruptedException e) {
                // no-op
            }
            String[] record = data.read(action.getIndex());
            // Some updation
            record[record.length - 1] = "abcdefghijklmnopqrstuvwxyz";
            data.lock(action.getIndex());
            try {
                data.update(action.getIndex(), record);
            } finally {
                data.unlock(action.getIndex());
            }
        }

        public void doDelete() throws Exception {
            LOG("    <I>Method: Delete</I>");
            data.lock(action.getIndex());
            try {
                try {
                    Thread.sleep(action.getExecutionTime());
                } catch (InterruptedException e) {
                    // no-op
                }
                data.delete(action.getIndex());
            } finally {
                // No Unlocking that is taken care in the Delete method itself
                // data.unlock(action.getIndex());
            }

        }
    }

    private static final class RandomAction {
        private static final int TEST_METHODS = 4;

        private final int index;
        private final int methodIndex;
        private final int executionTime;

        public RandomAction() {
            index = (int) (Math.random() * 100);
            methodIndex = (int) (Math.random() * RandomAction.TEST_METHODS);
            executionTime = (int) (Math.random() * 500);
        }

        public int getIndex() {
            return index;
        }

        public int getMethodIndex() {
            return methodIndex;
        }

        public int getExecutionTime() {
            return executionTime;
        }
    }

    private static final class Counter {

        /**
         * The number of hits for this counter.
         */
        private int hits;

        /**
         * Builds an <code>Counter</code> instance.
         */
        public Counter() {
            // UNIMPLEMENTED
        }

        /**
         * Hits once this <code>Counter</code>.
         */
        public synchronized void hit() {
            hits++;
        }

        /**
         * Tests if this <code>Counter</code> was hit for a specified number
         * of times.
         *
         * @param toTest the number of hits.
         * @return true if this <code>Counter</code> was hit for a specified
         *         number of times.
         */
        private synchronized boolean isDone(int toTest) {
            return hits == toTest;
        }

        /**
         * Starts a threads which tests if this <code>Counter</code> was hit
         * for a specified number of times. After the test is done (and the
         * <code>Counter</code> was hit for a specified number of times) it
         * runs the <code>toRun</code> command.
         *
         * @param must  the specified number of hits.
         * @param toRun this command runs after the
         *              <code>Counter</code> <code>isDone</code> method
         *              returns true.
         */
        public synchronized void check(int must, Runnable toRun) {
            final Thread checkThread = new Thread(new CheckTarget(must, toRun),
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
             * @param must  the number of hits.
             * @param toRun this command runs after the
             *              <code>Counter</code> <code>isDone</code>
             *              method returns true.
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
                    while (!isDone(must)) {
                        try {
                            Counter.this.wait(100);
                        } catch (InterruptedException e) {
                            // only log must be enough for this case.
                            e.printStackTrace();
                        }
                    }
                }

                if (toRun != null) {
                    toRun.run();
                }

                System.out.println("Test succeed.");
            }
        }
    }
}