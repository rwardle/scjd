/*
 * MainView.java
 *
 * 06 Jul 2007
 */

package suncertify.presentation;

import java.awt.Component;

import javax.swing.JFrame;

import suncertify.service.Contractor;

/**
 * 
 * @author Richard Wardle
 */
public interface MainView {

    /**
     * Sets the main presenter.
     * 
     * @param presenter
     *                The presenter.
     */
    void setPresenter(MainPresenter presenter);

    /** Realises the view and its components. */
    void realise();

    String getNameCriteria();

    void setNameCriteria(String nameCriteria);

    String getLocationCriteria();

    void setLocationCriteria(String locationCriteria);

    void setTableModel(ContractorTableModel tableModel);

    void setStatusLabelText(String text);

    JFrame getFrame();

    void disableControls();

    /**
     * @param componentToFocus
     *                May be null.
     */
    void enableControls(Component componentToFocus);

    Contractor getContractorAtRow(int rowNo);

    void updateContractorAtRow(int rowNo, Contractor contractor);
}
