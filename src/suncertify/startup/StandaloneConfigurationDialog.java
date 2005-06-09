/*
 * StandaloneConfigurationDialog.java
 *
 * Created on 07-Jun-2005
 */


package suncertify.startup;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


/**
 * @author Richard Wardle
 */
public class StandaloneConfigurationDialog extends AbstractConfigurationDialog {

    private JPanel inputPanel;
    private JTextField databaseFilePathField;
    private JLabel databaseFilePathLabel;

    /**
     * Creates a new StandaloneConfigurationDialog.
     */
    public StandaloneConfigurationDialog() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected JPanel getInputPanel() {
        // TODO: resizing doesn't work
        this.inputPanel = new JPanel();
        this.databaseFilePathLabel = new JLabel("Database file path:");
        this.inputPanel.add(this.databaseFilePathLabel);
        
        this.databaseFilePathField = new JTextField() {
            // TODO: move this to a nicer place so the other dialogs can use it
            protected Document createDefaultModel() {
                return new PlainDocument() {
                    public void insertString(int offs, String str,
                            AttributeSet a) throws BadLocationException {
                        if (!isOkEnabled() && str != null && str.length() >= 0) {
                            setOkEnabled();
                        }
                        super.insertString(offs, str, a);
                    }

                    protected void insertUpdate(DefaultDocumentEvent chng,
                            AttributeSet attr) {
                        if (!isOkEnabled() && chng.getLength() >= 0) {
                            setOkEnabled();
                        }
                        super.insertUpdate(chng, attr);
                    }

                    protected void removeUpdate(DefaultDocumentEvent chng) {
                        if (chng.getLength() >= databaseFilePathField.getText()
                                .length()) {
                            setOkDisabled();
                        }
                        super.removeUpdate(chng);
                    }
                };
            }
        };

        this.inputPanel.add(this.databaseFilePathField);
        return this.inputPanel;
    }

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        String message = "Text for the standalone dialog";
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public String getDatabaseFilePath() {
        // TODO change these implemetations?
        super.setDatabaseFilePath(this.databaseFilePathField.getText());
        return super.getDatabaseFilePath();
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        super.setDatabaseFilePath(databaseFilePath);
        this.databaseFilePathField.setText(databaseFilePath);
    }

}
