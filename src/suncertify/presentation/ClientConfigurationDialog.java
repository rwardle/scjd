/*
 * ClientConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import suncertify.ApplicationConstants;

/**
 * Client mode configuration dialog.
 * 
 * @author Richard Wardle
 */
public final class ClientConfigurationDialog extends
        AbstractConfigurationDialog {

    private static final long serialVersionUID = 1L;
    private JTextField serverAddressField;
    private JSpinner serverPortSpinner;

    /**
     * Creates a new instance of <code>ClientConfigurationDialog</code>.
     */
    public ClientConfigurationDialog() {
        setTitle(getResourceBundle().getString(
                "ClientConfigurationDialog.title"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessageText() {
        return getResourceBundle().getString(
                "ClientConfigurationDialog.message.text");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerAddress() {
        return this.serverAddressField.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerAddress(String serverAddress) {
        this.serverAddressField.setText(serverAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getServerPort() {
        return (Integer) this.serverPortSpinner.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerPort(Integer serverPort) {
        this.serverPortSpinner.setValue(serverPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "ClientConfigurationDialog.serverAddressLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        this.serverAddressField = new JTextField();
        this.serverAddressField.setToolTipText(getResourceBundle().getString(
                "ClientConfigurationDialog.serverAddressField.tooltip"));
        panel.add(this.serverAddressField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel.add(new JPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel
                .add(new JLabel(getResourceBundle().getString(
                        "ClientConfigurationDialog.serverPortLabel.text")),
                        constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;

        this.serverPortSpinner = new JSpinner(new SpinnerNumberModel(
                SERVER_PORT_SPINNER_INITIAL_VALUE,
                SERVER_PORT_SPINNER_MINIMUM_VALUE,
                SERVER_PORT_SPINNER_MAXIMUM_VALUE,
                SERVER_PORT_SPINNER_STEP_SIZE));
        this.serverPortSpinner.setFont(this.serverAddressField.getFont());
        this.serverPortSpinner.setToolTipText(getResourceBundle().getString(
                "ClientConfigurationDialog.serverPortSpinner.tooltip"));
        JSpinner.DefaultEditor spinnerEditor = new JSpinner.NumberEditor(
                this.serverPortSpinner, SERVER_PORT_SPINNER_FORMAT_PATTERN);
        spinnerEditor.getTextField().setColumns(SERVER_PORT_SPINNER_COLUMNS);
        this.serverPortSpinner.setEditor(spinnerEditor);
        panel.add(this.serverPortSpinner, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 1;
        panel.add(new JPanel(), constraints);

        return panel;
    }
}
