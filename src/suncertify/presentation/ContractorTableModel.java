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
 * 
 * @author Richard Wardle
 */
public final class ContractorTableModel extends AbstractTableModel {

    private final List<Contractor> contractors;

    public ContractorTableModel(List<Contractor> contractors) {
        this.contractors = new ArrayList<Contractor>(contractors);
    }

    public int getRowCount() {
        return contractors.size();
    }

    public int getColumnCount() {
        return PresentationConstants.TABLE_COLUMN_COUNT;
    }

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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == PresentationConstants.TABLE_OWNER_COLUMN_INDEX
                && "".equals(contractors.get(rowIndex).getOwner());
    }

    int getRecordNumberAt(int rowIndex) {
        return contractors.get(rowIndex).getRecordNumber();
    }

    public Contractor getContractorAtRow(int rowNo) {
        return contractors.get(rowNo);
    }

    public void updateContractorAtRow(int rowNo, Contractor contractor) {
        contractors.remove(rowNo);
        contractors.add(rowNo, contractor);
        fireTableRowsUpdated(rowNo, rowNo);
    }
}
