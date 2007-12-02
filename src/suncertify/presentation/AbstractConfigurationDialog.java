/*
 * AbstractConfigurationDialog.java
 *
 * 10 Aug 2007
 */

package suncertify.presentation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * Abstract base class for application configuration dialogs.
 * 
 * @author Richard Wardle
 */
public abstract class AbstractConfigurationDialog extends JDialog implements
        ConfigurationView {

    private static final int SERVER_PORT_SPINNER_INITIAL_VALUE = 1;
    private static final int SERVER_PORT_SPINNER_MINIMUM_VALUE = 1;
    private static final int SERVER_PORT_SPINNER_MAXIMUM_VALUE = Integer.MAX_VALUE;
    private static final int SERVER_PORT_SPINNER_STEP_SIZE = 1;
    private static final int SERVER_PORT_SPINNER_COLUMN_COUNT = 5;
    private static final String SERVER_PORT_SPINNER_FORMAT_PATTERN = "#";

    private final ResourceBundle resourceBundle;
    private final JButton okButton;
    private final JButton cancelButton;
    private ConfigurationPresenter presenter;
    private String databaseFilePath;
    private String serverAddress;
    private Integer serverPort;

    /**
     * Creates a new instance of <code>AbstractConfigurationDialog</code>.
     */
    protected AbstractConfigurationDialog() {
        resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");

        okButton = new JButton(resourceBundle
                .getString("AbstractConfigurationDialog.okButton.text"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.okButtonActionPerformed();
            }
        });

        cancelButton = new JButton(resourceBundle
                .getString("AbstractConfigurationDialog.cancelButton.text"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.cancelButtonActionPerformed();
            }
        });

        setModal(true);
        setResizable(false);
        setLayout(new GridBagLayout());
        initialiseComponents();
    }

    /**
     * Returns the resource bundle.
     * 
     * @return The resource bundle.
     */
    protected final ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Returns the configuration presenter.
     * 
     * @return The configuration presenter.
     */
    protected final ConfigurationPresenter getPresenter() {
        return presenter;
    }

    /** {@inheritDoc} */
    public final void setPresenter(ConfigurationPresenter presenter) {
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    public final void realise() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = getSize();
        setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        okButton.requestFocus();
        setVisible(true);
    }

    /** {@inheritDoc} */
    public final void close() {
        setVisible(false);
        dispose();
    }

    /**
     * {@inheritDoc}
     */
    public final Component getComponent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getDatabaseFilePath() {
        return databaseFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getServerPort() {
        return serverPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    private void initialiseComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        add(initialiseMessagePanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        add(new JSeparator(SwingConstants.HORIZONTAL), constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(15, 0, 10, 0);
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(initialiseInputPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1;
        add(new JPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.ipadx = 25;
        // TODO Stops window resize icon overlaying button on Mac
        constraints.insets = new Insets(4, 4, 15, 4);
        add(initialiseButtonPanel(), constraints);
    }

    private Component initialiseButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 4, 4));
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private JPanel initialiseMessagePanel() {
        JGradientPanel panel = new JGradientPanel(
                PresentationConstants.DARK_BLUE,
                PresentationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        panel.add(new JLabel(getMessageText()), constraints);
        return panel;
    }

    /**
     * Returns the message text to display.
     * 
     * @return The message text.
     */
    protected abstract String getMessageText();

    /**
     * Initialises the input panel.
     * 
     * @return The input panel.
     */
    protected abstract JPanel initialiseInputPanel();

    /**
     * Creates a server port spinner with the specified font.
     * 
     * @param font
     *                Server port spinner font.
     * @return The server port spinner.
     */
    protected final JSpinner createServerPortSpinner(Font font) {
        JSpinner serverPortSpinner = new JSpinner(new SpinnerNumberModel(
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_INITIAL_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_MINIMUM_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_MAXIMUM_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_STEP_SIZE));
        serverPortSpinner.setFont(font);
        serverPortSpinner.setToolTipText(getResourceBundle().getString(
                "ServerConfigurationDialog.serverPortSpinner.tooltip"));
        JSpinner.DefaultEditor spinnerEditor = new JSpinner.NumberEditor(
                serverPortSpinner,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_FORMAT_PATTERN);
        spinnerEditor.getTextField().setColumns(
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_COLUMN_COUNT);
        serverPortSpinner.setEditor(spinnerEditor);
        return serverPortSpinner;
    }
}
