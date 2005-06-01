/*
 * DuplicateKeyExceptionTest.java
 * JUnit based test
 *
 * Created on 30 May 2005, 22:52
 */

package suncertify.db;

import junit.framework.*;

/**
 *
 * @author Richard Wardle
 */
public class DuplicateKeyExceptionTest extends TestCase {
    
    public DuplicateKeyExceptionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DuplicateKeyExceptionTest.class);
        
        return suite;
    }
    
    public void testSomething() {
        //test
        fail();
    }
    
}
