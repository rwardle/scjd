package suncertify.presentation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
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
        mockConfiguration = context.mock(Configuration.class);
        mockView = context.mock(ConfigurationView.class);
        mockFileChooser = context.mock(JFileChooser.class);

        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration).load();

                ignoring(mockConfiguration).getProperty(
                        ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
                will(returnValue(databaseFilePath));

                ignoring(mockConfiguration).getProperty(
                        ApplicationConstants.SERVER_ADDRESS_PROPERTY);
                will(returnValue(serverAddress));

                ignoring(mockConfiguration).getProperty(
                        ApplicationConstants.SERVER_PORT_PROPERTY);
                will(returnValue(serverPort));
            }
        });
        presenter = new StubConfigurationPresenter(new ConfigurationManager(
                mockConfiguration), mockView);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConfigurationIsNull() {
        new ConfigurationPresenter(null, mockView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfViewIsNull() {
        new ConfigurationPresenter(new ConfigurationManager(mockConfiguration),
                null);
    }

    @Test
    public void shouldReturnCancelledByDefault() {
        assertThat(presenter.getReturnStatus(), is(ReturnStatus.CANCEL));
    }

    @Test
    public void shouldRealiseView() {
        context.checking(new Expectations() {
            {
                one(mockView)
                        .setDatabaseFilePath(with(equal(databaseFilePath)));
                one(mockView).setServerAddress(with(equal(serverAddress)));
                one(mockView).setServerPort(
                        with(equal(Integer.valueOf(serverPort))));
                one(mockView).realise();
            }
        });
        presenter.realiseView();
    }

    @Test
    public void shouldReturnOkWhenOkButtonActionPerformed() {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final Integer newServerPort = 9999;

        context.checking(new Expectations() {
            {
                one(mockView).getDatabaseFilePath();
                will(returnValue(newDatabaseFilePath));

                one(mockView).getServerAddress();
                will(returnValue(newServerAddress));

                one(mockView).getServerPort();
                will(returnValue(newServerPort));

                one(mockView).close();
                one(mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                                with(equal(newDatabaseFilePath)));
                one(mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                                with(equal(newServerAddress)));
                one(mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(equal(newServerPort.toString())));
            }
        });

        presenter.okButtonActionPerformed();
        assertThat(presenter.getReturnStatus(), is(ReturnStatus.OK));
    }

    @Test
    public void shouldReturnCancelWhenCancelButtonActionPerformed() {
        context.checking(new Expectations() {
            {
                one(mockView).close();
            }
        });
        presenter.cancelButtonActionPerformed();
        assertThat(presenter.getReturnStatus(), is(ReturnStatus.CANCEL));
    }

    @Test
    public void shouldNotUpdateDatabaseFilePathWhenErrorInFileChooser() {
        context.checking(new Expectations() {
            {
                allowing(mockView).getDatabaseFilePath();
                will(returnValue(databaseFilePath));

                allowing(mockView).getComponent();
                will(returnValue(null));

                one(mockFileChooser).showDialog(with(any(Component.class)),
                        with(any(String.class)));
                will(returnValue(JFileChooser.ERROR_OPTION));

                never(mockFileChooser).getSelectedFile();
                never(mockView).setDatabaseFilePath(with(any(String.class)));
            }
        });
        presenter.browseButtonActionPerformed();
    }

    @Test
    public void shouldNotUpdateDatabaseFilePathWhenFileChooserIsCancelled() {
        context.checking(new Expectations() {
            {
                allowing(mockView).getDatabaseFilePath();
                will(returnValue(databaseFilePath));

                allowing(mockView).getComponent();
                will(returnValue(null));

                one(mockFileChooser).showDialog(with(any(Component.class)),
                        with(any(String.class)));
                will(returnValue(JFileChooser.CANCEL_OPTION));

                never(mockFileChooser).getSelectedFile();
                never(mockView).setDatabaseFilePath(with(any(String.class)));
            }
        });
        presenter.browseButtonActionPerformed();
    }

    @Test
    public void shouldUpdateDatabaseFilePathWhenFileChooserIsApproved() {
        final File newDatabaseFile = new File("newDatabaseFilePath");
        context.checking(new Expectations() {
            {
                allowing(mockView).getDatabaseFilePath();
                will(returnValue(databaseFilePath));

                allowing(mockView).getComponent();
                will(returnValue(null));

                one(mockFileChooser).showDialog(with(any(Component.class)),
                        with(any(String.class)));
                will(returnValue(JFileChooser.APPROVE_OPTION));

                one(mockFileChooser).getSelectedFile();
                will(returnValue(newDatabaseFile));

                one(mockView).setDatabaseFilePath(
                        with(equal(newDatabaseFile.getAbsolutePath())));
            }
        });
        presenter.browseButtonActionPerformed();
    }

    private class StubConfigurationPresenter extends ConfigurationPresenter {

        public StubConfigurationPresenter(
                ConfigurationManager configurationManager,
                ConfigurationView view) {
            super(configurationManager, view);
        }

        @Override
        JFileChooser createFileChooser(String directoryPath) {
            return mockFileChooser;
        }
    }
}
