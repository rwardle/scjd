/*
 * MainPresenter.java
 *
 * 06 Jul 2007
 */

package suncertify.presentation;

import java.io.IOException;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import suncertify.service.BrokerService;
import suncertify.service.Contractor;
import suncertify.service.SearchCriteria;

/**
 * Encapsulates the presentation behaviour of the main frame.
 * 
 * @author Richard Wardle
 */
public class MainPresenter {
    private final BrokerService service;
    private final MainView view;
    private final ResourceBundle resourceBundle;

    /**
     * Creates a new instance of <code>MainPresenter</code>.
     * 
     * @param service
     *                The broker service.
     * @param view
     *                The main view.
     */
    public MainPresenter(BrokerService service, MainView view) {
        if (service == null) {
            throw new IllegalArgumentException("service cannot be null");
        }
        if (view == null) {
            throw new IllegalArgumentException("view cannot be null");
        }
        this.service = service;
        this.view = view;
        this.resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
    }

    /** Realises the view. */
    public void realiseView() {
        this.view.realise();
    }

    public final void searchButtonActionPerformed() {
        // TODO Use SwingWorker
        try {
            String nameCriteria = substituteNullForEmptyString(this.view
                    .getNameCriteria());
            String locationCriteria = substituteNullForEmptyString(this.view
                    .getLocationCriteria());
            SearchCriteria searchCriteria = new SearchCriteria().setName(
                    nameCriteria).setLocation(locationCriteria);
            List<Contractor> contractors = this.service.search(searchCriteria);

            ContractorTableModel tableModel = new ContractorTableModel(
                    contractors);
            this.view.setTableModel(tableModel);
            this.view.setStatusLabelText(buildStatusLabelText(searchCriteria,
                    contractors.size()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String substituteNullForEmptyString(String str) {
        return "".equals(str) ? null : str;
    }

    private String buildStatusLabelText(SearchCriteria searchCriteria,
            int contractorsCount) {
        String oneContractorFormat = this.resourceBundle
                .getString("MainFrame.statusLabel.oneContractor.text");
        String manyContractorsFormat = this.resourceBundle
                .getString("MainFrame.statusLabel.manyContractors.text");
        double[] limits = { 0, 1, ChoiceFormat.nextDouble(1) };
        String[] formats = { manyContractorsFormat, oneContractorFormat,
                manyContractorsFormat };
        ChoiceFormat choiceFormat = new ChoiceFormat(limits, formats);

        MessageFormat messageFormat = new MessageFormat(
                getMessageFormatPattern(searchCriteria));
        messageFormat.setFormatByArgumentIndex(0, choiceFormat);

        return messageFormat.format(new Object[] { contractorsCount,
                contractorsCount, searchCriteria.getName(),
                searchCriteria.getLocation() });
    }

    private String getMessageFormatPattern(SearchCriteria searchCriteria) {
        String pattern;
        if (searchCriteria.getName() != null
                && searchCriteria.getLocation() != null) {
            pattern = this.resourceBundle
                    .getString("MainFrame.statusLabel.nameAndLocationCriteria.text");
        } else if (searchCriteria.getName() != null) {
            pattern = this.resourceBundle
                    .getString("MainFrame.statusLabel.nameCriteria.text");
        } else if (searchCriteria.getLocation() != null) {
            pattern = this.resourceBundle
                    .getString("MainFrame.statusLabel.locationCriteria.text");
        } else {
            pattern = this.resourceBundle
                    .getString("MainFrame.statusLabel.noCriteria.text");
        }
        return pattern;
    }
}
