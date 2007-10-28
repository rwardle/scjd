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
import java.util.ResourceBundle;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
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

    public ContractorTableColumnModel() {
        for (int i = 0; i < ApplicationConstants.TABLE_COLUMN_NAMES.length; i++) {
            TableColumn column = new TableColumn(i, COLUMN_WIDTHS[i]);
            column.setHeaderValue(ApplicationConstants.TABLE_COLUMN_NAMES[i]);
            addColumn(column);
        }

        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        String bookButtonText = resourceBundle
                .getString("MainFrame.bookButton.text");

        TableColumn ownerColumn = getColumn(ApplicationConstants.TABLE_OWNER_COLUMN_INDEX);
        ownerColumn.setCellRenderer(new OwnerTableCellRenderer(
                createOwnerCellComponent(bookButtonText)));
        ownerColumn.setCellEditor(new OwnerTableCellEditor(
                createOwnerCellComponent(bookButtonText)));
    }

    public Component createOwnerCellComponent(String bookButtonText) {
        JButton bookButton = new JButton(bookButtonText);
        bookButton.setOpaque(false);
        bookButton.setFocusPainted(false);
        Font defaultButtonFont = bookButton.getFont();
        bookButton.setFont(new Font(defaultButtonFont.getName(),
                defaultButtonFont.getStyle(), BOOK_BUTTON_FONT_SIZE));
        bookButton.setPreferredSize(BOOK_BUTTON_DIMENSIONS);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(bookButton, constraints);
        return panel;
    }

    private static final class OwnerTableCellRenderer implements
            TableCellRenderer {

        private final Component ownerCellComponent;

        public OwnerTableCellRenderer(Component ownerCellComponent) {
            this.ownerCellComponent = ownerCellComponent;
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Component component;
            if (value == null || value.equals("")) {
                component = this.ownerCellComponent;
                component.setBackground(isSelected ? table
                        .getSelectionBackground() : table.getBackground());
            } else {
                component = table.getDefaultRenderer(String.class)
                        .getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
            }
            return component;
        }
    }

    private static final class OwnerTableCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        private static final long serialVersionUID = 1L;
        private final Component ownerCellComponent;

        public OwnerTableCellEditor(Component ownerCellComponent) {
            this.ownerCellComponent = ownerCellComponent;
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            Component component;
            if (value == null || value.equals("")) {
                component = this.ownerCellComponent;
                component.setBackground(isSelected ? table
                        .getSelectionBackground() : table.getBackground());
            } else {
                component = table.getDefaultEditor(String.class)
                        .getTableCellEditorComponent(table, value, isSelected,
                                row, column);
            }
            return component;
        }

        public Object getCellEditorValue() {
            // TODO
            return null;
        }
    }
}
