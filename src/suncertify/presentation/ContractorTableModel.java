/*
 * ContractorTableModel.java
 *
 * 23 Oct 2007
 */

package suncertify.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import suncertify.ApplicationConstants;
import suncertify.service.Contractor;

/**
 * 
 * @author Richard Wardle
 */
public class ContractorTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final List<Contractor> contractors;

    public ContractorTableModel(List<Contractor> contractors) {
        this.contractors = new ArrayList<Contractor>(contractors);
    }

    public int getRowCount() {
        return this.contractors.size();
    }

    public int getColumnCount() {
        return ApplicationConstants.TABLE_COLUMN_NAMES.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Contractor contractor = this.contractors.get(rowIndex);
        String value;
        switch (columnIndex) {
        case ApplicationConstants.TABLE_NAME_COLUMN_INDEX:
            value = contractor.getName();
            break;
        case ApplicationConstants.TABLE_LOCATION_COLUMN_INDEX:
            value = contractor.getLocation();
            break;
        case ApplicationConstants.TABLE_SPECIALTIES_COLUMN_INDEX:
            value = contractor.getSpecialties();
            break;
        case ApplicationConstants.TABLE_SIZE_COLUMN_INDEX:
            value = contractor.getSize();
            break;
        case ApplicationConstants.TABLE_RATE_COLUMN_INDEX:
            value = contractor.getRate();
            break;
        case ApplicationConstants.TABLE_OWNER_COLUMN_INDEX:
            value = contractor.getOwner();
            break;
        default:
            throw new IllegalArgumentException("invalid column index: "
                    + columnIndex);
        }
        return value;
    }

    public int getRecordNumberAt(int rowIndex) {
        return this.contractors.get(rowIndex).getRecordNumber();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == ApplicationConstants.TABLE_OWNER_COLUMN_INDEX;
    }
}
