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

import suncertify.ApplicationMode;

/**
 * Configuration dialog for applications running in
 * {@link ApplicationMode#CLIENT CLIENT} mode.
 * 
 * @author Richard Wardle
 */
public final class ClientConfigurationDialog extends
        AbstractConfigurationDialog {

    private JTextField serverAddressField;
    private JSpinner serverPortSpinner;

    /**
     * Creates a new instance of <code>ClientConfigurationDialog</code>.
     */
    public ClientConfigurationDialog() {
        setTitle(getResourceBundle().getString(
                "ClientConfigurationDialog.title"));
    }

    /** {@inheritDoc} */
    @Override
    protected String getMessageText() {
        return getResourceBundle().getString(
                "ClientConfigurationDialog.message");
    }

    /** {@inheritDoc} */
    @Override
    public String getServerAddress() {
        return serverAddressField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setServerAddress(String serverAddress) {
        serverAddressField.setText(serverAddress);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getServerPort() {
        return (Integer) serverPortSpinner.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setServerPort(Integer serverPort) {
        if (serverPort == null) {
            throw new IllegalArgumentException("serverPort cannot be null");
        }
        serverPortSpinner.setValue(serverPort);
    }

    /** {@inheritDoc} */
    @Override
    protected JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "ClientConfigurationDialog.serverAddressLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        serverAddressField = new JTextField();
        serverAddressField.setToolTipText(getResourceBundle().getString(
                "ClientConfigurationDialog.serverAddressTextField.tooltip"));
        panel.add(serverAddressField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel.add(new JPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel
                .add(new JLabel(getResourceBundle().getString(
                        "ClientConfigurationDialog.serverPortLabel.text")),
                        constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;

        serverPortSpinner = createServerPortSpinner(serverAddressField
                .getFont());
        panel.add(serverPortSpinner, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 1;
        panel.add(new JPanel(), constraints);

        return panel;
    }
}
