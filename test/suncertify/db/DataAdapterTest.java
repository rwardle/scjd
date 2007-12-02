package suncertify.db;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataAdapterTest {

    private final Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private Data mockData;
    private DataAdapter dataAdapter;

    @Before
    public void setUp() throws Exception {
        mockData = context.mock(Data.class);
        dataAdapter = new DataAdapter(mockData);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IOException.class)
    public void readMapsDataAccessException() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).read(with(any(int.class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        dataAdapter.read(-1);
    }

    @Test(expected = IOException.class)
    public void updateMapsDataAccessException() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).update(with(any(int.class)),
                        with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        dataAdapter.update(-1, new String[0]);
    }

    @Test(expected = IOException.class)
    public void deleteMapsDataAccessException() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).delete(with(any(int.class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        dataAdapter.delete(-1);
    }

    @Test(expected = IOException.class)
    public void findMapsDataAccessException() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).find(with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        dataAdapter.find(new String[0]);
    }

    @Test(expected = IOException.class)
    public void createMapsDataAccessException() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).create(with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        dataAdapter.create(new String[0]);
    }

    @Test(expected = InterruptedException.class)
    public void lockMapsIllegalThreadStateException() throws Exception {
        final IllegalThreadStateException exception = new IllegalThreadStateException();
        exception.initCause(new InterruptedException());

        context.checking(new Expectations() {
            {
                one(mockData).lock(with(any(int.class)));
                will(throwException(exception));
            }
        });
        dataAdapter.lock(-1);
    }

    @Test
    public void unlockCallDelegates() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).unlock(with(any(int.class)));
            }
        });
        dataAdapter.unlock(-1);
    }

    @Test
    public void isLockedCallDelegates() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockData).isLocked(with(any(int.class)));
                will(returnValue(false));
            }
        });
        assertThat(dataAdapter.isLocked(-1), is(false));
    }
}
