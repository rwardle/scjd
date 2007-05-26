package suncertify.presentation;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.Configuration;

@RunWith(TestClassRunner.class)
@SuppressWarnings("boxing")
public class ConfigurationPresenterTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final String databaseFilePath = "databaseFilePath";
    private final String serverAddress = "serverAddress";
    private final String serverPort = "serverPort";
    private Configuration mockConfiguration;
    private ConfigurationView mockView;
    private ConfigurationPresenter presenter;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockView = this.context.mock(ConfigurationView.class);
        this.presenter = new ConfigurationPresenter(this.mockConfiguration,
                this.mockView);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = NullPointerException.class)
    public void constructorDisallowNullConfiguration() {
        new ConfigurationPresenter(null, this.mockView);
    }

    @Test(expected = NullPointerException.class)
    public void constructorDisallowNullView() {
        new ConfigurationPresenter(this.mockConfiguration, null);
    }

    @Test
    public void constructorReturnStatus() {
        Assert.assertEquals(ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }

    @Test
    public void realiseView() {
        this.context.checking(new Expectations() {{
            one(ConfigurationPresenterTest.this.mockConfiguration)
                    .getDatabaseFilePath();
                will(returnValue(ConfigurationPresenterTest.this.databaseFilePath));
            one(ConfigurationPresenterTest.this.mockConfiguration)
                    .getServerAddress();
                will(returnValue(ConfigurationPresenterTest.this.serverAddress));
            one(ConfigurationPresenterTest.this.mockConfiguration)
                    .getServerPort();
                will(returnValue(ConfigurationPresenterTest.this.serverPort));
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
        }});
        this.presenter.realiseView();
    }

    @Test
    public void okButtonActionPerformed() {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final String newServerPort = "newServerPort";

        this.context.checking(new Expectations() {{
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
                    .setDatabaseFilePath(with(equal(newDatabaseFilePath)));
            one(ConfigurationPresenterTest.this.mockConfiguration)
                    .setServerAddress(with(equal(newServerAddress)));
            one(ConfigurationPresenterTest.this.mockConfiguration)
                    .setServerPort(with(equal(newServerPort)));
        }});

        this.presenter.okButtonActionPerformed();
        Assert.assertEquals(ConfigurationPresenter.RETURN_OK, this.presenter
                .getReturnStatus());
    }

    @Test
    public void cancelButtonActionPerformed() {
        this.context.checking(new Expectations() {{
            one(ConfigurationPresenterTest.this.mockView).close();
        }});
        this.presenter.cancelButtonActionPerformed();
        Assert.assertEquals(ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }
}
