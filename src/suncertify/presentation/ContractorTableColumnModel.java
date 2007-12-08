/*
 * ContractorTableColumnModel.java
 *
 * 26 Oct 2007 
 */

package suncertify.presentation;

import java.awt.Color;
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
 * Column model for the contractor table.
 * 
 * @author Richard Wardle
 */
public final class ContractorTableColumnModel extends DefaultTableColumnModel {

    private static final Dimension BOOK_BUTTON_DIMENSIONS = new Dimension(65,
            15);
    private static final int BOOK_BUTTON_FONT_SIZE = 10;

    private final String bookButtonText;
    private final String[] columnHeaderToolTips;
    private final JButton rendererBookButton;
    private MainPresenter presenter;

    /**
     * Creates a new instance of <code>ContractorTableColumnModel</code>.
     */
    public ContractorTableColumnModel() {
        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        bookButtonText = resourceBundle.getString("MainFrame.bookButton.text");
        rendererBookButton = createBookButton();

        // Get the column names
        String[] tableColumnNames = new String[PresentationConstants.TABLE_COLUMN_COUNT];
        for (int i = 0; i < tableColumnNames.length; i++) {
            tableColumnNames[i] = resourceBundle
                    .getString("MainFrame.resultsTable.column" + i + ".text");
        }

        // Add the columns
        for (int i = 0; i < PresentationConstants.TABLE_COLUMN_COUNT; i++) {
            TableColumn column = new TableColumn(i,
                    PresentationConstants.TABLE_COLUMN_WIDTHS.get(i));
            column.setHeaderValue(tableColumnNames[i]);
            addColumn(column);
        }

        // Get the column header tool tips
        columnHeaderToolTips = new String[PresentationConstants.TABLE_COLUMN_COUNT];
        for (int i = 0; i < tableColumnNames.length; i++) {
            columnHeaderToolTips[i] = resourceBundle
                    .getString("MainFrame.resultsTable.column" + i + ".tooltip");
        }

        // Setup the renderer and editor for the owner column
        TableColumn ownerColumn = getColumn(PresentationConstants.TABLE_OWNER_COLUMN_INDEX);
        String bookButtonTooltip = resourceBundle
                .getString("MainFrame.bookButton.tooltip");
        ownerColumn.setCellRenderer(new OwnerTableCellRenderer(this,
                bookButtonTooltip));
        ownerColumn.setCellEditor(new OwnerTableCellEditor(this,
                bookButtonTooltip));
    }

    /**
     * Returns the tooltip text for the column at the specified index.
     * 
     * @param columnIndex
     *                Column index
     * @return The tooltip text.
     */
    public String getColumnHeaderToolTipText(int columnIndex) {
        return columnHeaderToolTips[columnIndex];
    }

    /**
     * Disables the book button in the owner cell renderer.
     */
    public void disableRendererBookButton() {
        rendererBookButton.setEnabled(false);
    }

    /**
     * Enables the book button in the owner cell renderer.
     */
    public void enableRendererBookButton() {
        rendererBookButton.setEnabled(true);
    }

    /**
     * Sets the main presenter.
     * 
     * @param presenter
     *                Presenter to set.
     */
    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    private JButton createBookButton() {
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

    // Renders the owner column as a book button if there is no owner
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
            bookButtonPanel = contractorTableColumnModel
                    .createBookButtonPanel(contractorTableColumnModel.rendererBookButton);
            this.bookButtonTooltip = bookButtonTooltip;
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JComponent component;
            if ("".equals(value)) {
                // Owner column is empty, renderer the book button
                Border border;
                if (hasFocus) {
                    border = getFocusBorder(table, value, isSelected, row,
                            column);
                } else {
                    border = EMPTY_BORDER;
                }
                bookButtonPanel.setBorder(border);

                Color backgroundColor;
                if (isSelected) {
                    backgroundColor = table.getSelectionBackground();
                } else {
                    backgroundColor = table.getBackground();
                }
                bookButtonPanel.setBackground(backgroundColor);

                component = bookButtonPanel;
                component.setToolTipText(bookButtonTooltip);
            } else {
                component = (JComponent) table.getDefaultRenderer(String.class)
                        .getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                if (focusBorder == null && hasFocus) {
                    // Store the default focus border for future use
                    focusBorder = component.getBorder();
                }
                component.setToolTipText(null);
            }
            return component;
        }

        private Border getFocusBorder(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (focusBorder == null) {
                // Get and store the default focus border for future use
                JComponent defaultRendererComponent = (JComponent) table
                        .getDefaultRenderer(String.class)
                        .getTableCellRendererComponent(table, value,
                                isSelected, true, row, column);
                focusBorder = defaultRendererComponent.getBorder();
            }
            return focusBorder;
        }
    }

    // Owner column editor is a book button
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

            bookButtonPanel = contractorTableColumnModel
                    .createBookButtonPanel(bookButton);
            bookButtonPanel.addFocusListener(new FocusListener() {
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
            contractorTableColumnModel.presenter.bookActionPerformed(
                    currentRow, resultsTable);
            fireEditingStopped();
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            /*
             * Editor always shows the book button since it can only be invoked
             * when there is no owner.
             */

            /*
             * Store the table and the current row so they are available to the
             * actionPerformed method if the user clicks the button.
             */
            resultsTable = table;
            currentRow = row;

            bookButtonPanel.setBackground(table.getSelectionBackground());
            bookButtonPanel.setBorder(UIManager
                    .getBorder("Table.focusCellHighlightBorder"));
            return bookButtonPanel;
        }

        public Object getCellEditorValue() {
            /*
             * Although the "owner" column is editable, the cell value is not
             * updated through the editor, so always return null here.
             */
            return null;
        }
    }
}
