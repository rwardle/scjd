/*
 * StandaloneConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Standalone mode configuration dialog.
 * 
 * @author Richard Wardle
 */
public final class StandaloneConfigurationDialog extends
        AbstractConfigurationDialog {

    private static final long serialVersionUID = 1L;
    private String serverAddress;
    private String serverPort;
    private JTextField databaseFilePathField;

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
    protected void initInputPanel(JPanel inputPanel) {
        JLabel databaseFilePathLabel = new JLabel(
                getResourceBundle()
                        .getString(
                                "StandaloneConfigurationDialog.databaseFilePathLabel.text"));
        inputPanel.add(databaseFilePathLabel);

        this.databaseFilePathField = new JTextField() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            // TODO: move this to a nicer place so the other dialogs can use it
            @Override
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void insertString(int offs, String str,
                            AttributeSet a) throws BadLocationException {
                        if (!isOkButtonEnabled() && str != null
                                && str.length() >= 0) {
                            setOkButtonEnabled(true);
                        }
                        super.insertString(offs, str, a);
                    }

                    @Override
                    protected void insertUpdate(DefaultDocumentEvent chng,
                            AttributeSet attr) {
                        if (!isOkButtonEnabled() && chng.getLength() >= 0) {
                            setOkButtonEnabled(true);
                        }
                        super.insertUpdate(chng, attr);
                    }

                    @Override
                    protected void removeUpdate(DefaultDocumentEvent chng) {
                        if (chng.getLength() >= StandaloneConfigurationDialog.this.databaseFilePathField
                                .getText().length()) {
                            setOkButtonEnabled(false);
                        }
                        super.removeUpdate(chng);
                    }
                };
            }
        };

        this.databaseFilePathField
                .setName("StandaloneConfigurationDialog.databaseFilePathField.name");
        inputPanel.add(this.databaseFilePathField);
        getContentPane().add(inputPanel, BorderLayout.CENTER);
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
    public String getServerPort() {
        return this.serverPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * {@inheritDoc}
     */
    public String getDatabaseFilePath() {
        return this.databaseFilePathField.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePathField.setText(databaseFilePath);
    }
}
