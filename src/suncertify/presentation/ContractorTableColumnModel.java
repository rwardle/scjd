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

/**
 * @author Richard Wardle
 */
public class ContractorTableColumnModel extends DefaultTableColumnModel {

    private static final int BOOK_BUTTON_FONT_SIZE = 10;
    private static final Dimension BOOK_BUTTON_DIMENSIONS = new Dimension(65,
            15);

    private final String bookButtonText;
    private final String[] columnHeaderToolTips;
    private final JButton rendererBookButton;
    private MainPresenter presenter;

    public ContractorTableColumnModel() {
        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");

        String[] tableColumnNames = new String[PresentationConstants.TABLE_COLUMN_COUNT];
        for (int i = 0; i < tableColumnNames.length; i++) {
            tableColumnNames[i] = resourceBundle
                    .getString("MainFrame.resultsTable.column" + i + ".text");
        }

        for (int i = 0; i < PresentationConstants.TABLE_COLUMN_COUNT; i++) {
            TableColumn column = new TableColumn(i,
                    PresentationConstants.TABLE_COLUMN_WIDTHS.get(i));
            column.setHeaderValue(tableColumnNames[i]);
            addColumn(column);
        }

        this.bookButtonText = resourceBundle
                .getString("MainFrame.bookButton.text");

        this.columnHeaderToolTips = new String[PresentationConstants.TABLE_COLUMN_COUNT];
        for (int i = 0; i < tableColumnNames.length; i++) {
            this.columnHeaderToolTips[i] = resourceBundle
                    .getString("MainFrame.resultsTable.column" + i + ".tooltip");
        }

        TableColumn ownerColumn = getColumn(PresentationConstants.TABLE_OWNER_COLUMN_INDEX);
        this.rendererBookButton = createBookButton();
        String bookButtonTooltip = resourceBundle
                .getString("MainFrame.bookButton.tooltip");
        ownerColumn.setCellRenderer(new OwnerTableCellRenderer(this,
                bookButtonTooltip));
        ownerColumn.setCellEditor(new OwnerTableCellEditor(this,
                bookButtonTooltip));
    }

    String getColumnHeaderToolTipText(int columnIndex) {
        return this.columnHeaderToolTips[columnIndex];
    }

    void disableRendererBookButton() {
        this.rendererBookButton.setEnabled(false);
    }

    void enableRendererBookButton() {
        this.rendererBookButton.setEnabled(true);
    }

    void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    private JButton createBookButton() {
        JButton button = new JButton(this.bookButtonText);
        button.setOpaque(false);
        button.setFocusPainted(false);
        Font defaultButtonFont = button.getFont();
        button.setFont(new Font(defaultButtonFont.getName(), defaultButtonFont
                .getStyle(), ContractorTableColumnModel.BOOK_BUTTON_FONT_SIZE));
        button
                .setPreferredSize(ContractorTableColumnModel.BOOK_BUTTON_DIMENSIONS);
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
        private final String bookButtonTooltip;
        private Border focusBorder;

        public OwnerTableCellRenderer(
                ContractorTableColumnModel contractorTableColumnModel,
                String bookButtonTooltip) {
            this.bookButtonTooltip = bookButtonTooltip;
            this.bookButtonPanel = contractorTableColumnModel
                    .createBookButtonPanel(contractorTableColumnModel.rendererBookButton);
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
                    border = OwnerTableCellRenderer.EMPTY_BORDER;
                }
                this.bookButtonPanel.setBorder(border);

                this.bookButtonPanel.setBackground(isSelected ? table
                        .getSelectionBackground() : table.getBackground());

                component = this.bookButtonPanel;
                component.setToolTipText(this.bookButtonTooltip);
            } else {
                component = (JComponent) table.getDefaultRenderer(String.class)
                        .getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                if (this.focusBorder == null && hasFocus) {
                    // Store the default focus border for future use
                    this.focusBorder = component.getBorder();
                }
                component.setToolTipText(null);
            }
            return component;
        }
    }

    private static final class OwnerTableCellEditor extends AbstractCellEditor
            implements TableCellEditor, ActionListener {

        private final JPanel bookButtonPanel;
        private final ContractorTableColumnModel contractorTableColumnModel;
        private final String bookButtonTooltip;
        private JTable resultsTable;
        private int currentRow;

        public OwnerTableCellEditor(
                ContractorTableColumnModel contractorTableColumnModel,
                String bookButtonTooltip) {
            this.contractorTableColumnModel = contractorTableColumnModel;
            this.bookButtonTooltip = bookButtonTooltip;

            final JButton bookButton = contractorTableColumnModel
                    .createBookButton();
            bookButton.setToolTipText(this.bookButtonTooltip);
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
            this.contractorTableColumnModel.presenter.bookActionPerformed(
                    this.currentRow, this.resultsTable);
            fireEditingStopped();
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            this.resultsTable = table;
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
