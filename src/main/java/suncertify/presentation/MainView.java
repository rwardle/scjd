/*
 * MainView.java
 *
 * 06 Jul 2007
 */

package suncertify.presentation;

import suncertify.service.Contractor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * An application main view.
 *
 * @author Richard Wardle
 */
public interface MainView {

    /**
     * Sets the main presenter.
     *
     * @param presenter Presenter to set.
     * @throws IllegalArgumentException If <code>presenter</code> is <code>null</code>.
     */
    void setPresenter(MainPresenter presenter);

    /**
     * Realises the view.
     */
    void realise();

    /**
     * Returns the name search criteria.
     *
     * @return The name search criteria.
     */
    String getNameCriteria();

    /**
     * Returns the location search criteria.
     *
     * @return The location search criteria.
     */
    String getLocationCriteria();

    /**
     * Sets the contractor data for the results table.
     *
     * @param contractors List of contractors.
     * @throws IllegalArgumentException If <code>contractors</code> is <code>null</code>.
     */
    void setTableData(List<Contractor> contractors);

    /**
     * Sets the status label text.
     *
     * @param text Status label text.
     */
    void setStatusLabelText(String text);

    /**
     * Returns the main frame.
     *
     * @return The main frame.
     */
    JFrame getFrame();

    /**
     * Disables user-interface controls.
     */
    void disableControls();

    /**
     * Enables user-interface controls and focuses the specified component (if non-<code>null</code>
     * ).
     *
     * @param componentToFocus Component to be focused (may be <code>null</code>).
     */
    void enableControls(Component componentToFocus);

    /**
     * Returns the contractor at the specified row in the table.
     *
     * @param rowNo Table row number.
     * @return The contractor.
     * @throws IndexOutOfBoundsException If <code>rowNo</code> is less than 0 or greater than the number of rows in the
     *                                   table.
     */
    Contractor getContractorAtRow(int rowNo);

    /**
     * Updates the contractor at the specified row in the table.
     *
     * @param rowNo      Table row number.
     * @param contractor The updated contractor data.
     * @throws IndexOutOfBoundsException If <code>rowNo</code> is less than 0 or greater than the number of rows in the
     *                                   table.
     * @throws IllegalArgumentException  If <code>contractor</code> is <code>null</code>.
     */
    void updateContractorAtRow(int rowNo, Contractor contractor);
}
