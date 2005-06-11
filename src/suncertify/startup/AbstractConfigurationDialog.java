/*
 * AbstractConfigurationDialog.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Provides a skeletal implementation of {@link ConfigurationView}.
 *
 * @author Richard Wardle
 */
public abstract class AbstractConfigurationDialog implements ConfigurationView {

    private JDialog dialog;
    private JButton okButton;
    private JButton cancelButton;

    /**
     * Creates a new instance of AbstractConfigurationDialog.
     */
    public AbstractConfigurationDialog() {
        this.dialog = new JDialog();
        this.dialog.setModal(true);
        this.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // TODO: Set title
    }

    /**
     * Gets the dialog.
     *
     * @return The dialog.
     */
    protected final JDialog getDialog() {
        return this.dialog;
    }

    /**
     * {@inheritDoc}
     */
    public final void initialiseComponents() {
        // TODO: Resizing doesn't work

        // TODO: Dialog needs centering properly
        this.dialog.setLocationRelativeTo(null);

        JPanel messagePanel = new JPanel();
        this.dialog.getContentPane().add(messagePanel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel();
        messageLabel.setText(getMessageText());
        messagePanel.add(messageLabel);

        initialiseInputPanel();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        this.okButton = new JButton("OK");
        buttonPanel.add(this.okButton);

        this.cancelButton = new JButton("Cancel");
        buttonPanel.add(this.cancelButton);
    }

    /**
     * Gets the text to display in the message area.
     *
     * @return The message text.
     */
    protected abstract String getMessageText();

    /**
     * Initialises the input panel.
     */
    protected abstract void initialiseInputPanel();

    /**
     * {@inheritDoc}
     */
    public final void realiseView() {
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    public final void close() {
        this.dialog.setVisible(false);
        this.dialog.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public final void addOkButtonListener(ActionListener listener) {
        this.okButton.addActionListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public final  void addCancelButtonListener(ActionListener listener) {
        this.cancelButton.addActionListener(listener);
    }

    /**
     * Gets the state of the OK button.
     *
     * @return true if it is enabled, false if it is disabled.
     */
    protected final boolean isOkButtonEnabled() {
        return this.okButton.isEnabled();
    }

    /**
     * Sets the state of the OK button.
     *
     * @param enabled true if it should be enabled, false if it should be
     * disabled.
     */
    protected final void setOkButtonEnabled(boolean enabled) {
        this.okButton.setEnabled(enabled);
    }
}
