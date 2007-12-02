/*
 * ContractorTableModel.java
 *
 * 23 Oct 2007
 */

package suncertify.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import suncertify.service.Contractor;

/**
 * Model for the contractor table.
 * 
 * @author Richard Wardle
 */
public final class ContractorTableModel extends AbstractTableModel {

    private final List<Contractor> contractors;

    /**
     * Creates a new instance of <code>ContractorTableModel</code> with the
     * specified list of contractors.
     * 
     * @param contractors
     *                List of contractors.
     */
    public ContractorTableModel(List<Contractor> contractors) {
        this.contractors = new ArrayList<Contractor>(contractors);
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        return contractors.size();
    }

    /** {@inheritDoc} */
    public int getColumnCount() {
        return PresentationConstants.TABLE_COLUMN_COUNT;
    }

    /** {@inheritDoc} */
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
            throw new IllegalArgumentException("invalid column index: "
                    + columnIndex);
        }
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == PresentationConstants.TABLE_OWNER_COLUMN_INDEX
                && "".equals(contractors.get(rowIndex).getOwner());
    }

    /**
     * Returns the contractor at the specified row.
     * 
     * @param rowNo
     *                Table row number.
     * @return The contractor.
     */
    public Contractor getContractorAtRow(int rowNo) {
        return contractors.get(rowNo);
    }

    /**
     * Updates the contractor at the specified row.
     * 
     * @param rowNo
     *                Table row number.
     * @param contractor
     *                Updated contractor data.
     */
    public void updateContractorAtRow(int rowNo, Contractor contractor) {
        contractors.remove(rowNo);
        contractors.add(rowNo, contractor);
        fireTableRowsUpdated(rowNo, rowNo);
    }
}
