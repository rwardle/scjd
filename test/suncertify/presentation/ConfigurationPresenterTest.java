package suncertify.presentation;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncertify.ApplicationConstants;
import suncertify.Configuration;
import suncertify.ConfigurationManager;
import suncertify.ReturnStatus;

public class ConfigurationPresenterTest {

    private final Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final String databaseFilePath = "databaseFilePath";
    private final String serverAddress = "serverAddress";
    private final String serverPort = "3333";
    private Configuration mockConfiguration;
    private ConfigurationView mockView;
    private JFileChooser mockFileChooser;
    private ConfigurationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockView = this.context.mock(ConfigurationView.class);
        this.mockFileChooser = this.context.mock(JFileChooser.class);

        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .exists();
                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .load();

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(
                                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.databaseFilePath));

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(
                                ApplicationConstants.SERVER_ADDRESS_PROPERTY);
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.serverAddress));

                ignoring(ConfigurationPresenterTest.this.mockConfiguration)
                        .getProperty(ApplicationConstants.SERVER_PORT_PROPERTY);
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.serverPort));
            }
        });
        this.presenter = new StubConfigurationPresenter(
                new ConfigurationManager(this.mockConfiguration), this.mockView);
    }

    @After
    public void tearDown() {
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
        Assert.assertThat(this.presenter.getReturnStatus(), CoreMatchers
                .is(ReturnStatus.CANCEL));
    }

    @Test
    public void realiseView() {
        this.context.checking(new Expectations() {
            {
                one(ConfigurationPresenterTest.this.mockView)
                        .setDatabaseFilePath(
                                with(Expectations
                                        .equal(ConfigurationPresenterTest.this.databaseFilePath)));
                one(ConfigurationPresenterTest.this.mockView)
                        .setServerAddress(
                                with(Expectations
                                        .equal(ConfigurationPresenterTest.this.serverAddress)));
                one(ConfigurationPresenterTest.this.mockView)
                        .setServerPort(
                                with(Expectations
                                        .equal(Integer
                                                .valueOf(ConfigurationPresenterTest.this.serverPort))));
                one(ConfigurationPresenterTest.this.mockView).realise();
            }
        });
        this.presenter.realiseView();
    }

    @Test
    public void okButtonActionPerformed() {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final Integer newServerPort = 9999;

        this.context.checking(new Expectations() {
            {
                one(ConfigurationPresenterTest.this.mockView)
                        .getDatabaseFilePath();
                will(Expectations.returnValue(newDatabaseFilePath));

                one(ConfigurationPresenterTest.this.mockView)
                        .getServerAddress();
                will(Expectations.returnValue(newServerAddress));

                one(ConfigurationPresenterTest.this.mockView).getServerPort();
                will(Expectations.returnValue(newServerPort));

                one(ConfigurationPresenterTest.this.mockView).close();
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                                with(Expectations.equal(newDatabaseFilePath)));
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                                with(Expectations.equal(newServerAddress)));
                one(ConfigurationPresenterTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(Expectations.equal(newServerPort
                                        .toString())));
            }
        });

        this.presenter.okButtonActionPerformed();
        Assert.assertThat(this.presenter.getReturnStatus(), CoreMatchers
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
        Assert.assertThat(this.presenter.getReturnStatus(), CoreMatchers
                .is(ReturnStatus.CANCEL));
    }

    @Test
    public void databaseFilePathNotUpdatedIfErrorInFileChooser() {
        this.context.checking(new Expectations() {
            {
                allowing(ConfigurationPresenterTest.this.mockView)
                        .getDatabaseFilePath();
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.databaseFilePath));

                allowing(ConfigurationPresenterTest.this.mockView)
                        .getComponent();
                will(Expectations.returnValue(null));

                one(ConfigurationPresenterTest.this.mockFileChooser)
                        .showDialog(with(Expectations.any(Component.class)),
                                with(Expectations.any(String.class)));
                will(Expectations.returnValue(JFileChooser.ERROR_OPTION));

                never(ConfigurationPresenterTest.this.mockFileChooser)
                        .getSelectedFile();
                never(ConfigurationPresenterTest.this.mockView)
                        .setDatabaseFilePath(
                                with(Expectations.any(String.class)));
            }
        });
        this.presenter.browseButtonActionPerformed();
    }

    @Test
    public void databaseFilePathNotUpdatedIfFileChooserIsCancelled() {
        this.context.checking(new Expectations() {
            {
                allowing(ConfigurationPresenterTest.this.mockView)
                        .getDatabaseFilePath();
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.databaseFilePath));

                allowing(ConfigurationPresenterTest.this.mockView)
                        .getComponent();
                will(Expectations.returnValue(null));

                one(ConfigurationPresenterTest.this.mockFileChooser)
                        .showDialog(with(Expectations.any(Component.class)),
                                with(Expectations.any(String.class)));
                will(Expectations.returnValue(JFileChooser.CANCEL_OPTION));

                never(ConfigurationPresenterTest.this.mockFileChooser)
                        .getSelectedFile();
                never(ConfigurationPresenterTest.this.mockView)
                        .setDatabaseFilePath(
                                with(Expectations.any(String.class)));
            }
        });
        this.presenter.browseButtonActionPerformed();
    }

    @Test
    public void databaseFilePathUpdatedIfFileChooserIsApproved() {
        final File newDatabaseFile = new File("newDatabaseFilePath");
        this.context.checking(new Expectations() {
            {
                allowing(ConfigurationPresenterTest.this.mockView)
                        .getDatabaseFilePath();
                will(Expectations
                        .returnValue(ConfigurationPresenterTest.this.databaseFilePath));

                allowing(ConfigurationPresenterTest.this.mockView)
                        .getComponent();
                will(Expectations.returnValue(null));

                one(ConfigurationPresenterTest.this.mockFileChooser)
                        .showDialog(with(Expectations.any(Component.class)),
                                with(Expectations.any(String.class)));
                will(Expectations.returnValue(JFileChooser.APPROVE_OPTION));

                one(ConfigurationPresenterTest.this.mockFileChooser)
                        .getSelectedFile();
                will(Expectations.returnValue(newDatabaseFile));

                one(ConfigurationPresenterTest.this.mockView)
                        .setDatabaseFilePath(
                                with(Expectations.equal(newDatabaseFile
                                        .getAbsolutePath())));
            }
        });
        this.presenter.browseButtonActionPerformed();
    }

    private class StubConfigurationPresenter extends ConfigurationPresenter {

        public StubConfigurationPresenter(
                ConfigurationManager configurationManager,
                ConfigurationView view) {
            super(configurationManager, view);
        }

        @Override
        JFileChooser createFileChooser(String directoryPath) {
            return ConfigurationPresenterTest.this.mockFileChooser;
        }
    }
}
