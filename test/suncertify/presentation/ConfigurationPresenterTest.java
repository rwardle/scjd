package suncertify.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.ApplicationConstants;
import suncertify.Configuration;
import suncertify.ConfigurationManager;
import suncertify.ReturnStatus;

public class ConfigurationPresenterTest {

    private final Mockery context = new Mockery();
    private final String databaseFilePath = "databaseFilePath";
    private final String serverAddress = "serverAddress";
    private final String serverPort = "serverPort";
    private Configuration mockConfiguration;
    private ConfigurationView mockView;
    private ConfigurationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockView = this.context.mock(ConfigurationView.class);

        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .exists();
                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .load();

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(
                                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
                will(returnValue(ConfigurationPresenterTest.this.databaseFilePath));

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(
                                ApplicationConstants.SERVER_ADDRESS_PROPERTY);
                will(returnValue(ConfigurationPresenterTest.this.serverAddress));

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(ApplicationConstants.SERVER_PORT_PROPERTY);
                will(returnValue(ConfigurationPresenterTest.this.serverPort));
            }
        });
        this.presenter = new ConfigurationPresenter(new ConfigurationManager(
                this.mockConfiguration), this.mockView);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorDisallowNullConfiguration() {
        new ConfigurationPresenter(null, this.mockView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorDisallowNullView() {
        new ConfigurationPresenter(new ConfigurationManager(
                this.mockConfiguration), null);
    }

    @Test
    public void defaultReturnStatus() {
        assertThat(this.presenter.getReturnStatus(), Matchers
                .is(ReturnStatus.CANCEL));
    }

    @Test
    public void realiseView() {
        this.context.checking(new Expectations() {
            {
                one(ConfigurationPresenterTest.this.mockView)
                        .setDatabaseFilePath(
                                with(equal(ConfigurationPresenterTest.this.databaseFilePath)));
                one(ConfigurationPresenterTest.this.mockView)
                        .setServerAddress(
                                with(equal(ConfigurationPresenterTest.this.serverAddress)));
                one(ConfigurationPresenterTest.this.mockView)
                        .setServerPort(
                                with(equal(ConfigurationPresenterTest.this.serverPort)));
                one(ConfigurationPresenterTest.this.mockView).realise();
            }
        });
        this.presenter.realiseView();
    }

    @Test
    public void okButtonActionPerformed() {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final String newServerPort = "newServerPort";

        this.context.checking(new Expectations() {
            {
                one(ConfigurationPresenterTest.this.mockView)
                        .getDatabaseFilePath();
                will(returnValue(newDatabaseFilePath));

                one(ConfigurationPresenterTest.this.mockView)
                        .getServerAddress();
                will(returnValue(newServerAddress));

                one(ConfigurationPresenterTest.this.mockView).getServerPort();
                will(returnValue(newServerPort));

                one(ConfigurationPresenterTest.this.mockView).close();
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                                with(equal(newDatabaseFilePath)));
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                                with(equal(newServerAddress)));
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(equal(newServerPort)));
            }
        });

        this.presenter.okButtonActionPerformed();
        assertThat(this.presenter.getReturnStatus(), Matchers
                .is(ReturnStatus.OK));
    }

    @Test
    public void cancelButtonActionPerformed() {
        this.context.checking(new Expectations() {
            {
                one(ConfigurationPresenterTest.this.mockView).close();
            }
        });
        this.presenter.cancelButtonActionPerformed();
        assertThat(this.presenter.getReturnStatus(), Matchers
                .is(ReturnStatus.CANCEL));
    }
}
