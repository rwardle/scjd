/*
 * StandaloneConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import suncertify.ApplicationConstants;

/**
 * Standalone mode configuration dialog.
 * 
 * @author Richard Wardle
 */
public final class StandaloneConfigurationDialog extends
        AbstractConfigurationDialog {

    private static final long serialVersionUID = 1L;
    private JTextField databaseFilePathField;
    private JButton browseButton;

    public StandaloneConfigurationDialog() {
        setTitle(getResourceBundle().getString(
                "StandaloneConfigurationDialog.title"));
        this.browseButton.addActionListener(new ActionListener() {
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
                "StandaloneConfigurationDialog.message.text");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseFilePath() {
        return this.databaseFilePathField.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePathField.setText(databaseFilePath);
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
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "StandaloneConfigurationDialog.databaseFilePathLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        this.databaseFilePathField = new JTextField();
        this.databaseFilePathField.setEditable(false);
        this.databaseFilePathField
                .setToolTipText(getResourceBundle()
                        .getString(
                                "StandaloneConfigurationDialog.databaseFilePathField.tooltip"));
        panel.add(this.databaseFilePathField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        this.browseButton = new JButton(getResourceBundle().getString(
                "StandaloneConfigurationDialog.browseButton.text"));
        this.browseButton.setMnemonic(new Integer(getResourceBundle()
                .getString(
                        "StandaloneConfigurationDialog.browseButton.mnemonic")
                .charAt(0)).intValue());
        panel.add(this.browseButton, constraints);

        return panel;
    }
}
