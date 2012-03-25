/*
 * StandaloneConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import suncertify.ApplicationMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Configuration dialog for applications running in
 * {@link ApplicationMode#STANDALONE STANDALONE} mode.
 *
 * @author Richard Wardle
 */
public final class StandaloneConfigurationDialog extends
        AbstractConfigurationDialog {

    private JTextField databaseFilePathField;
    private JButton browseButton;

    /**
     * Creates a new instance of <code>StandaloneConfigurationDialog</code>.
     */
    public StandaloneConfigurationDialog() {
        setTitle(getResourceBundle().getString(
                "StandaloneConfigurationDialog.title"));
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StandaloneConfigurationDialog.this.getPresenter()
                        .browseButtonActionPerformed();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessageText() {
        return getResourceBundle().getString(
                "StandaloneConfigurationDialog.message");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseFilePath() {
        return databaseFilePathField.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseFilePath(String databaseFilePath) {
        databaseFilePathField.setText(databaseFilePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "StandaloneConfigurationDialog.databaseFilePathLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        databaseFilePathField = new JTextField();
        databaseFilePathField.setEditable(false);
        databaseFilePathField
                .setToolTipText(getResourceBundle()
                        .getString(
                                "StandaloneConfigurationDialog.databaseFilePathTextField.tooltip"));
        panel.add(databaseFilePathField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        browseButton = new JButton(getResourceBundle().getString(
                "StandaloneConfigurationDialog.browseButton.text"));
        browseButton.setMnemonic(getResourceBundle().getString(
                "StandaloneConfigurationDialog.browseButton.mnemonic")
                .charAt(0));
        panel.add(browseButton, constraints);

        return panel;
    }
}
