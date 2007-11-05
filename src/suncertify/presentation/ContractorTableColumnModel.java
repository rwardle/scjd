/*
 * ContractorTableColumnModel.java
 *
 * 26 Oct 2007 
 */
package suncertify.presentation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import suncertify.ApplicationConstants;

/**
 * @author Richard Wardle
 */
public class ContractorTableColumnModel extends DefaultTableColumnModel {

    // TODO Panel does not get selection background if click on button directly
    private static final long serialVersionUID = 1L;
    private static final int[] COLUMN_WIDTHS = { 90, 70, 90, 45, 45, 60 };
    private static final int BOOK_BUTTON_FONT_SIZE = 10;
    private static final Dimension BOOK_BUTTON_DIMENSIONS = new Dimension(60,
            15);
    private final String bookButtonText;
    private MainPresenter presenter;

    public ContractorTableColumnModel() {
        for (int i = 0; i < ApplicationConstants.TABLE_COLUMN_NAMES.length; i++) {
            TableColumn column = new TableColumn(i, COLUMN_WIDTHS[i]);
            column.setHeaderValue(ApplicationConstants.TABLE_COLUMN_NAMES[i]);
            addColumn(column);
        }

        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        this.bookButtonText = resourceBundle
                .getString("MainFrame.bookButton.text");

        TableColumn ownerColumn = getColumn(ApplicationConstants.TABLE_OWNER_COLUMN_INDEX);
        ownerColumn.setCellRenderer(new OwnerTableCellRenderer(this));
        ownerColumn.setCellEditor(new OwnerTableCellEditor(this));
    }

    void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    private JButton createBookButton(String bookButtonText) {
        JButton button = new JButton(bookButtonText);
        button.setOpaque(false);
        button.setFocusPainted(false);
        Font defaultButtonFont = button.getFont();
        button.setFont(new Font(defaultButtonFont.getName(), defaultButtonFont
                .getStyle(), BOOK_BUTTON_FONT_SIZE));
        button.setPreferredSize(BOOK_BUTTON_DIMENSIONS);
        return button;
    }

    private JPanel createBookButtonPanel(JButton bookButton) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 2, 0, 2);
        constraints.weightx = 1;
        panel.add(bookButton, constraints);
        return panel;
    }

    private static final class OwnerTableCellRenderer implements
            TableCellRenderer {

        private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(1, 1,
                1, 1);
        private final JPanel bookButtonPanel;
        private Border focusBorder;

        public OwnerTableCellRenderer(
                ContractorTableColumnModel contractorTableColumnModel) {
            JButton bookButton = contractorTableColumnModel
                    .createBookButton(contractorTableColumnModel.bookButtonText);
            this.bookButtonPanel = contractorTableColumnModel
                    .createBookButtonPanel(bookButton);
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JComponent component;
            if ("".equals(value)) {
                Border border;
                if (hasFocus) {
                    if (this.focusBorder == null) {
                        // Get and store the default focus border for future use
                        JComponent defaultRendererComponent = (JComponent) table
                                .getDefaultRenderer(String.class)
                                .getTableCellRendererComponent(table, value,
                                        isSelected, hasFocus, row, column);
                        this.focusBorder = defaultRendererComponent.getBorder();
                    }
                    border = this.focusBorder;
                } else {
                    border = EMPTY_BORDER;
                }
                this.bookButtonPanel.setBorder(border);

                this.bookButtonPanel.setBackground(isSelected ? table
                        .getSelectionBackground() : table.getBackground());

                component = this.bookButtonPanel;
            } else {
                component = (JComponent) table.getDefaultRenderer(String.class)
                        .getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                if (this.focusBorder == null && hasFocus) {
                    // Store the default focus border for future use
                    this.focusBorder = component.getBorder();
                }
            }
            return component;
        }
    }

    private static final class OwnerTableCellEditor extends AbstractCellEditor
            implements TableCellEditor, ActionListener {

        private static final long serialVersionUID = 1L;
        private final JPanel bookButtonPanel;
        private final ContractorTableColumnModel contractorTableColumnModel;
        private int currentRow;

        public OwnerTableCellEditor(
                ContractorTableColumnModel contractorTableColumnModel) {
            this.contractorTableColumnModel = contractorTableColumnModel;

            final JButton bookButton = contractorTableColumnModel
                    .createBookButton(contractorTableColumnModel.bookButtonText);
            bookButton.addActionListener(this);

            this.bookButtonPanel = contractorTableColumnModel
                    .createBookButtonPanel(bookButton);
            this.bookButtonPanel.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    // Pass the focus to the book button so it can be actioned
                    // with the keyboard
                    bookButton.requestFocus();
                }

                public void focusLost(FocusEvent e) {
                    // no-op
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            this.contractorTableColumnModel.presenter
                    .bookButtonActionPerformed(this.currentRow);
            fireEditingStopped();
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            this.bookButtonPanel.setBackground(table.getSelectionBackground());
            this.bookButtonPanel.setBorder(UIManager
                    .getBorder("Table.focusCellHighlightBorder"));
            return this.bookButtonPanel;
        }

        public Object getCellEditorValue() {
            // TODO
            return null;
        }
    }
}
