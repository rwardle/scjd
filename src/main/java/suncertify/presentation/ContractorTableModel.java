/*
 * ContractorTableModel.java
 *
 * 23 Oct 2007
 */

package suncertify.presentation;

import suncertify.service.Contractor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for the contractor table.
 *
 * @author Richard Wardle
 */
public final class ContractorTableModel extends AbstractTableModel {

    private final List<Contractor> contractors;

    /**
     * Creates a new instance of <code>ContractorTableModel</code> with the specified list of
     * contractors.
     *
     * @param contractors List of contractors.
     * @throws IllegalArgumentException If <code>contractors</code> is <code>null</code>.
     */
    public ContractorTableModel(List<Contractor> contractors) {
        if (contractors == null) {
            throw new IllegalArgumentException("contractors cannot be null");
        }
        this.contractors = new ArrayList<Contractor>(contractors);
    }

    /**
     * Returns the number of rows in the model.
     *
     * @return The number of rows in the model
     */
    public int getRowCount() {
        return contractors.size();
    }

    /**
     * Returns the number of columns in the model.
     *
     * @return The number of columns in the model
     */
    public int getColumnCount() {
        return PresentationConstants.TABLE_COLUMN_COUNT;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex    Row whose value is to be queried.
     * @param columnIndex Column whose value is to be queried.
     * @return The value at the specified cell.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contractor contractor = contractors.get(rowIndex);

        String value;
        switch (columnIndex) {
            case PresentationConstants.TABLE_NAME_COLUMN_INDEX:
                value = contractor.getName();
                break;
            case PresentationConstants.TABLE_LOCATION_COLUMN_INDEX:
                value = contractor.getLocation();
                break;
            case PresentationConstants.TABLE_SPECIALTIES_COLUMN_INDEX:
                value = contractor.getSpecialties();
                break;
            case PresentationConstants.TABLE_SIZE_COLUMN_INDEX:
                value = contractor.getSize();
                break;
            case PresentationConstants.TABLE_RATE_COLUMN_INDEX:
                value = contractor.getRate();
                break;
            case PresentationConstants.TABLE_OWNER_COLUMN_INDEX:
                value = contractor.getOwner();
                break;
            default:
                throw new IllegalArgumentException("invalid column index: " + columnIndex);
        }

        return value;
    }

    /**
     * Returns a boolean flag indicating if the cell at the specified row and column is editable.
     *
     * @param rowIndex    Row being queried.
     * @param columnIndex Column being queried.
     * @return <code>true</code> if the cell is editable, <code>false</code> otherwise.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // "owner" column is editable but only if it is empty
        return columnIndex == PresentationConstants.TABLE_OWNER_COLUMN_INDEX
                && "".equals(contractors.get(rowIndex).getOwner());
    }

    /**
     * Replaces the current contractors in the model with the specified contractors.
     *
     * @param newContractors List of new contractors.
     */
    public void replaceContractors(List<Contractor> newContractors) {
        /*
         * Remove all the old contractors from the list and add all the new ones (making sure the
         * list they are added from cannot be modified while this operation is performed).
         */
        contractors.clear();
        contractors.addAll(Collections.unmodifiableList(newContractors));
        fireTableDataChanged();
    }

    /**
     * Returns the contractor at the specified row.
     *
     * @param rowNo Table row number.
     * @return The contractor.
     * @throws IndexOutOfBoundsException If <code>rowNo</code> is less than 0 or greater than the number of rows in the
     *                                   table.
     */
    public Contractor getContractorAtRow(int rowNo) {
        return contractors.get(rowNo);
    }

    /**
     * Updates the contractor at the specified row.
     *
     * @param rowNo      Table row number.
     * @param contractor Updated contractor data.
     * @throws IndexOutOfBoundsException If <code>rowNo</code> is less than 0 or greater than the number of rows in the
     *                                   table.
     * @throws IllegalArgumentException  If <code>contractor</code> is <code>null</code>.
     */
    public void updateContractorAtRow(int rowNo, Contractor contractor) {
        if (contractor == null) {
            throw new IllegalArgumentException("contractor cannot be null");
        }

        contractors.remove(rowNo);
        contractors.add(rowNo, contractor);
        fireTableRowsUpdated(rowNo, rowNo);
    }
}
