/*
 * StandaloneConfigurationDialog.java
 *
 * Created on 07-Jun-2005
 */

package suncertify.presentation;

import java.awt.BorderLayout;
import java.util.ResourceBundle;
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

    private String serverAddress;
    private String serverPort;
    private JTextField databaseFilePathField;

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        return this.getResourceBundle().getString("StandaloneConfigurationDialog.message.text");
    }

    /**
     * {@inheritDoc}
     */
    protected void initInputPanel(JPanel inputPanel) {
        JLabel databaseFilePathLabel = new JLabel(
                this.getResourceBundle().getString("StandaloneConfigurationDialog.databaseFilePathLabel.text"));
        inputPanel.add(databaseFilePathLabel);

        this.databaseFilePathField = new JTextField() {
            // TODO: move this to a nicer place so the other dialogs can use it
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    public void insertString(int offs, String str,
                            AttributeSet a) throws BadLocationException {
                        if (!isOkButtonEnabled() && str != null
                                && str.length() >= 0) {
                            setOkButtonEnabled(true);
                        }
                        super.insertString(offs, str, a);
                    }

                    protected void insertUpdate(DefaultDocumentEvent chng,
                            AttributeSet attr) {
                        if (!isOkButtonEnabled() && chng.getLength() >= 0) {
                            setOkButtonEnabled(true);
                        }
                        super.insertUpdate(chng, attr);
                    }

                    protected void removeUpdate(DefaultDocumentEvent chng) {
                        if (chng.getLength()
                                >= StandaloneConfigurationDialog.this
                                        .databaseFilePathField.getText()
                                        .length()) {
                            setOkButtonEnabled(false);
                        }
                        super.removeUpdate(chng);
                    }
                };
            }
        };

        this.databaseFilePathField.setName(
                "StandaloneConfigurationDialog.databaseFilePathField.name");
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
