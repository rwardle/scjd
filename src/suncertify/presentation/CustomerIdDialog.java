/*
 * CustomerIdDialog.java
 *
 * 7 Nov 2007 
 */
package suncertify.presentation;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.MaskFormatter;

/**
 * @author Richard Wardle
 */
public final class CustomerIdDialog extends JDialog {

    private static final String CUSTOMER_ID_MASK = "########";
    private static final int CUSTOMER_ID_LENGTH = 8;

    private final ResourceBundle resourceBundle;
    private final JFormattedTextField customerIdTextField;
    private final JButton okButton;
    private final JButton cancelButton;
    private String customerId;

    public CustomerIdDialog(JFrame parentFrame) {
        super(parentFrame);
        resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");

        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(CUSTOMER_ID_MASK);
        } catch (ParseException e) {
            // TODO
            throw new RuntimeException(e);
        }
        customerIdTextField = new JFormattedTextField(formatter);
        customerIdTextField.setToolTipText(resourceBundle
                .getString("CustomerIdDialog.customerIdTextField.toolTip"));
        customerIdTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        okButton = new JButton(resourceBundle
                .getString("CustomerIdDialog.okButton.text"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        cancelButton = new JButton(resourceBundle
                .getString("CustomerIdDialog.cancelButton.text"));
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
        // TODO Stops window resize icon overlaying button on Mac
        constraints.insets = new Insets(4, 4, 15, 4);
        add(initialiseButtonPanel(), constraints);
    }

    private JPanel initialiseMessagePanel() {
        JGradientPanel panel = new JGradientPanel(
                PresentationConstants.DARK_BLUE,
                PresentationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        panel.add(new JLabel(resourceBundle
                .getString("CustomerIdDialog.message")), constraints);
        return panel;
    }

    private JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(resourceBundle
                .getString("CustomerIdDialog.customerIdLabel.text")),
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
        if (text.length() == CUSTOMER_ID_LENGTH) {
            customerId = text;
            setVisible(false);
        } else {
            customerIdTextField.setText(null);
            customerIdTextField.requestFocus();
        }
    }

    private void cancelButtonActionPerformed() {
        setVisible(false);
    }

    public String getCustomerId() {
        return customerId;
    }
}
