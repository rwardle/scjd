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
        this.mockData = this.context.mock(Data.class);
        this.dataAdapter = new DataAdapter(this.mockData);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IOException.class)
    public void readMapsDataAccessException() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).read(with(any(int.class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        this.dataAdapter.read(-1);
    }

    @Test(expected = IOException.class)
    public void updateMapsDataAccessException() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).update(with(any(int.class)),
                        with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        this.dataAdapter.update(-1, new String[0]);
    }

    @Test(expected = IOException.class)
    public void deleteMapsDataAccessException() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).delete(with(any(int.class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        this.dataAdapter.delete(-1);
    }

    @Test(expected = IOException.class)
    public void findMapsDataAccessException() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).find(
                        with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        this.dataAdapter.find(new String[0]);
    }

    @Test(expected = IOException.class)
    public void createMapsDataAccessException() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).create(
                        with(any(String[].class)));
                will(throwException(new DataAccessException(new IOException())));
            }
        });
        this.dataAdapter.create(new String[0]);
    }

    @Test(expected = InterruptedException.class)
    public void lockMapsIllegalThreadStateException() throws Exception {
        final IllegalThreadStateException exception = new IllegalThreadStateException();
        exception.initCause(new InterruptedException());

        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).lock(with(any(int.class)));
                will(throwException(exception));
            }
        });
        this.dataAdapter.lock(-1);
    }

    @Test
    public void unlockCallDelegates() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).unlock(with(any(int.class)));
            }
        });
        this.dataAdapter.unlock(-1);
    }

    @Test
    public void isLockedCallDelegates() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(DataAdapterTest.this.mockData).isLocked(
                        with(any(int.class)));
                will(returnValue(false));
            }
        });
        assertThat(this.dataAdapter.isLocked(-1), is(false));
    }
}
