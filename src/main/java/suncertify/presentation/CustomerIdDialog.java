/*
 * CustomerIdDialog.java
 *
 * 07 Nov 2007 
 */

package suncertify.presentation;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A dialog for entering the customer ID when making a booking.
 *
 * @author Richard Wardle
 */
public final class CustomerIdDialog extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(CustomerIdDialog.class.getName());
    private static final String CUSTOMER_ID_MASK = "########";
    private static final int CUSTOMER_ID_LENGTH = 8;

    private final ResourceBundle resourceBundle;
    private final JTextField customerIdTextField;
    private final JButton okButton;
    private final JButton cancelButton;
    private String customerId;

    /**
     * Creates a new <code>CustomerIdDialog</code> with the specified parent frame.
     *
     * @param parentFrame Parent frame.
     */
    public CustomerIdDialog(JFrame parentFrame) {
        super(parentFrame);
        resourceBundle = ResourceBundle.getBundle("suncertify/presentation/Bundle");

        // Enforcing customer ID to be 8 digits using a mask formatter
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(CUSTOMER_ID_MASK);
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Error setting customer ID formatter mask to: "
                    + CUSTOMER_ID_MASK, e);
        }

        if (formatter == null) {
            customerIdTextField = new JTextField();
        } else {
            customerIdTextField = new JFormattedTextField(formatter);
        }

        customerIdTextField.setToolTipText(resourceBundle
                .getString("CustomerIdDialog.customerIdTextField.toolTip"));
        customerIdTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        okButton = new JButton(resourceBundle.getString("CustomerIdDialog.okButton.text"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        cancelButton = new JButton(resourceBundle.getString("CustomerIdDialog.cancelButton.text"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });

        setModal(true);
        setResizable(false);
        setTitle(resourceBundle.getString("CustomerIdDialog.title"));
        setLayout(new GridBagLayout());
        initialiseComponents();
        pack();
        setLocationRelativeTo(parentFrame);
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
        constraints.insets = new Insets(15, 0, 5, 0);
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
        constraints.insets = new Insets(4, 4, 15, 4);
        add(initialiseButtonPanel(), constraints);
    }

    private JPanel initialiseMessagePanel() {
        JGradientPanel panel = new JGradientPanel(PresentationConstants.DARK_BLUE,
                PresentationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        panel.add(new JLabel(resourceBundle.getString("CustomerIdDialog.message")), constraints);
        return panel;
    }

    private JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(resourceBundle.getString("CustomerIdDialog.customerIdLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        panel.add(customerIdTextField, constraints);

        return panel;
    }

    private Component initialiseButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 4, 4));
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private void okButtonActionPerformed() {
        String text = customerIdTextField.getText().trim();
        if (isValidCustomerId(text)) {
            // Customer ID has been entered, store it and close the dialog
            customerId = text;
            setVisible(false);
        } else {
            /*
             * Invalid customer ID, clear the text field and give it focus so the user can try
             * again.
             */
            customerIdTextField.setText(null);
            customerIdTextField.requestFocus();
        }
    }

    private boolean isValidCustomerId(String customerIdText) {
        // Must be the correct length
        boolean valid = customerIdText.length() == CUSTOMER_ID_LENGTH;

        /*
         * Must only be digits. Formatted text field should enforce this but check here just in case
         * it couldn't be created for some reason.
         */
        for (int i = 0; valid && i < CUSTOMER_ID_LENGTH; i++) {
            valid = Character.isDigit(customerIdText.charAt(i));
        }

        return valid;
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
    }

    /**
     * Returns the customer ID.
     *
     * @return The customer ID entered in the dialog, or <code>null</code> if the dialog was
     * cancelled.
     */
    public String getCustomerId() {
        return customerId;
    }
}
