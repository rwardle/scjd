/*
 * AbstractConfigurationDialog.java
 *
 * 10 Aug 2007
 */

package suncertify.presentation;

import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;

import suncertify.ApplicationConstants;

/**
 * 
 * @author Richard Wardle
 */
public abstract class AbstractConfigurationDialog extends JDialog implements
        ConfigurationView {

    // TODO Change this to a JFrame?

    protected static final int SERVER_PORT_SPINNER_INITIAL_VALUE = 1;
    protected static final int SERVER_PORT_SPINNER_MINIMUM_VALUE = 1;
    protected static final int SERVER_PORT_SPINNER_MAXIMUM_VALUE = Integer.MAX_VALUE;
    protected static final int SERVER_PORT_SPINNER_STEP_SIZE = 1;
    protected static final int SERVER_PORT_SPINNER_COLUMNS = 5;
    protected static final String SERVER_PORT_SPINNER_FORMAT_PATTERN = "#";

    private final ResourceBundle resourceBundle;
    private final JButton okButton;
    private final JButton cancelButton;
    private ConfigurationPresenter presenter;
    private String databaseFilePath;
    private String serverAddress;
    private Integer serverPort;

    /** Constructor. */
    protected AbstractConfigurationDialog() {
        this.resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");

        this.okButton = new JButton(this.resourceBundle
                .getString("AbstractConfigurationDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractConfigurationDialog.this.presenter
                        .okButtonActionPerformed();
            }
        });

        this.cancelButton = new JButton(this.resourceBundle
                .getString("AbstractConfigurationDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractConfigurationDialog.this.presenter
                        .cancelButtonActionPerformed();
            }
        });

        setModal(true);
        setLayout(new GridBagLayout());
        initialiseComponents();
    }

    /**
     * Gets the resource bundle.
     * 
     * @return The resource bundle.
     */
    protected final ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    protected final ConfigurationPresenter getPresenter() {
        return this.presenter;
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
        return this.databaseFilePath;
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
        return this.serverAddress;
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
        return this.serverPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets the state of the OK button.
     * 
     * @return <code>true</code> if the button is enabled, <code>false</code>
     *         if the button is disabled.
     */
    protected final boolean isOkButtonEnabled() {
        return this.okButton.isEnabled();
    }

    /**
     * Sets the state of the OK button.
     * 
     * @param enabled
     *                <code>true</code> if it should be enabled,
     *                <code>false</code> if it should be disabled.
     */
    protected final void setOkButtonEnabled(boolean enabled) {
        this.okButton.setEnabled(enabled);
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
        constraints.ipady = ApplicationConstants.DEFAULT_INSETS.top * 10;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 2;
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
        // TODO Stops window resize icon overlaying button on Mac
        constraints.insets = new Insets(4, 4, 15, 4);
        // TODO Fix this
        constraints.weightx = 0.15;
        add(initialiseButtonPanel(), constraints);
    }

    private Component initialiseButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 4, 4));
        panel.add(this.okButton);
        panel.add(this.cancelButton);
        return panel;
    }

    private JPanel initialiseMessagePanel() {
        JGradientPanel panel = new JGradientPanel(
                ApplicationConstants.DARK_BLUE, ApplicationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        panel.add(new JLabel(getMessageText()), constraints);
        return panel;
    }

    protected abstract String getMessageText();

    protected abstract JPanel initialiseInputPanel();
}
