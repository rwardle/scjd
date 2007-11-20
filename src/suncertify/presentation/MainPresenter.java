/*
 * MainPresenter.java
 *
 * 06 Jul 2007
 */

package suncertify.presentation;

import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import suncertify.service.BrokerService;
import suncertify.service.Contractor;
import suncertify.service.ContractorDeletedException;
import suncertify.service.ContractorModifiedException;
import suncertify.service.SearchCriteria;

/**
 * Encapsulates the presentation behaviour of the main frame.
 * 
 * @author Richard Wardle
 */
public class MainPresenter {

    private static final Logger LOGGER = Logger.getLogger(MainPresenter.class
            .getName());

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

    public final void searchActionPerformed() {
        // TODO Currently edited text field loses focus
        String nameCriteria = substituteNullForEmptyString(this.view
                .getNameCriteria().trim());
        String locationCriteria = substituteNullForEmptyString(this.view
                .getLocationCriteria().trim());
        final SearchCriteria searchCriteria = new SearchCriteria().setName(
                nameCriteria).setLocation(locationCriteria);

        SwingWorker<List<Contractor>, Void> searchWorker = createSearchWorker(searchCriteria);
        this.view.disableControls();
        searchWorker.execute();
    }

    SwingWorker<List<Contractor>, Void> createSearchWorker(
            final SearchCriteria searchCriteria) {
        return new SearchWorker(this, searchCriteria);
    }

    private String substituteNullForEmptyString(String str) {
        return "".equals(str) ? null : str;
    }

    public void bookActionPerformed(int rowNo) {
        String customerId = showCustomerIdDialog();
        if (customerId != null) {
            Contractor contractor = this.view.getContractorAtRow(rowNo);
            SwingWorker<Void, Void> bookWorker = createBookWorker(customerId,
                    contractor, rowNo);
            this.view.disableControls();
            bookWorker.execute();
        }
    }

    String showCustomerIdDialog() {
        CustomerIdDialog dialog = new CustomerIdDialog(this.view.getFrame());
        dialog.setVisible(true);
        return dialog.getCustomerId();
    }

    SwingWorker<Void, Void> createBookWorker(String customerId,
            Contractor contractor, int rowNo) {
        return new BookWorker(this, customerId, contractor, rowNo);
    }

    void showOptionPane(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this.view.getFrame(), message, title,
                messageType);
    }

    private static final class SearchWorker extends
            SwingWorker<List<Contractor>, Void> {

        private final MainPresenter presenter;
        private final SearchCriteria searchCriteria;

        public SearchWorker(MainPresenter mainPresenter,
                SearchCriteria searchCriteria) {
            this.presenter = mainPresenter;
            this.searchCriteria = searchCriteria;
        }

        @Override
        protected List<Contractor> doInBackground() throws Exception {
            return this.presenter.service.search(this.searchCriteria);
        }

        @Override
        protected void done() {
            try {
                List<Contractor> contractors = get();
                ContractorTableModel tableModel = new ContractorTableModel(
                        contractors);
                this.presenter.view.setTableModel(tableModel);
                this.presenter.view
                        .setStatusLabelText(buildStatusLabelText(contractors
                                .size()));
            } catch (InterruptedException e) {
                MainPresenter.LOGGER
                        .log(
                                Level.WARNING,
                                "Thread was interruped while waiting for result of search SwingWorker",
                                e);
                showSearchErrorDialog();
            } catch (ExecutionException e) {
                MainPresenter.LOGGER.log(Level.SEVERE,
                        "Error searching for contractors with criteria: "
                                + this.searchCriteria, e);
                showSearchErrorDialog();
            } finally {
                this.presenter.view.enableControls();
            }
        }

        private String buildStatusLabelText(int contractorsCount) {
            String oneContractorFormat = this.presenter.resourceBundle
                    .getString("MainPresenter.statusLabel.oneContractor.text");
            String manyContractorsFormat = this.presenter.resourceBundle
                    .getString("MainPresenter.statusLabel.manyContractors.text");
            double[] limits = { 0, 1, ChoiceFormat.nextDouble(1) };
            String[] formats = { manyContractorsFormat, oneContractorFormat,
                    manyContractorsFormat };
            ChoiceFormat choiceFormat = new ChoiceFormat(limits, formats);

            MessageFormat messageFormat = new MessageFormat(
                    getMessageFormatPattern());
            messageFormat.setFormatByArgumentIndex(0, choiceFormat);

            return messageFormat.format(new Object[] { contractorsCount,
                    contractorsCount, this.searchCriteria.getName(),
                    this.searchCriteria.getLocation() });
        }

        private String getMessageFormatPattern() {
            String pattern;
            if (this.searchCriteria.getName() != null
                    && this.searchCriteria.getLocation() != null) {
                pattern = this.presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.nameAndLocationCriteria.text");
            } else if (this.searchCriteria.getName() != null) {
                pattern = this.presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.nameCriteria.text");
            } else if (this.searchCriteria.getLocation() != null) {
                pattern = this.presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.locationCriteria.text");
            } else {
                pattern = this.presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.noCriteria.text");
            }
            return pattern;
        }

        private void showSearchErrorDialog() {
            String message = this.presenter.resourceBundle
                    .getString("MainPresenter.searchErrorDialog.message.text");
            String title = this.presenter.resourceBundle
                    .getString("MainPresenter.searchErrorDialog.title.text");
            this.presenter.showOptionPane(message, title,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class BookWorker extends SwingWorker<Void, Void> {

        private final MainPresenter presenter;
        private final String customerId;
        private final Contractor contractor;
        private final int rowNo;

        public BookWorker(MainPresenter mainPresenter, String customerId,
                Contractor contractor, int rowNo) {
            this.presenter = mainPresenter;
            this.customerId = customerId;
            this.contractor = contractor;
            this.rowNo = rowNo;
        }

        @Override
        protected Void doInBackground() throws Exception {
            this.presenter.service.book(this.customerId, this.contractor);
            return null;
        }

        @Override
        protected void done() {
            try {
                // Although we don't need to call "get" to retrieve a
                // result, we do need to know if any exceptions were thrown
                get();
                Contractor updatedContractor = new Contractor(this.contractor
                        .getRecordNumber(), new String[] {
                        this.contractor.getName(),
                        this.contractor.getLocation(),
                        this.contractor.getSpecialties(),
                        this.contractor.getSize(), this.contractor.getRate(),
                        this.customerId });
                this.presenter.view.updateContractorAtRow(this.rowNo,
                        updatedContractor);
            } catch (InterruptedException e) {
                LOGGER
                        .log(
                                Level.WARNING,
                                "Thread was interruped while waiting for result of book SwingWorker",
                                e);
                showBookErrorDialog();
            } catch (ExecutionException e) {
                handleBookException(e);
            } finally {
                this.presenter.view.enableControls();
            }
        }

        private void showBookErrorDialog() {
            String message = this.presenter.resourceBundle
                    .getString("MainPresenter.bookErrorDialog.message.text");
            String title = this.presenter.resourceBundle
                    .getString("MainPresenter.bookErrorDialog.title.text");
            this.presenter.showOptionPane(message, title,
                    JOptionPane.ERROR_MESSAGE);
        }

        private void handleBookException(ExecutionException e) {
            if (e.getCause() instanceof ContractorDeletedException) {
                MainPresenter.LOGGER
                        .log(
                                Level.WARNING,
                                "Customer "
                                        + this.customerId
                                        + " attempted to book a contractor that has been deleted: "
                                        + this.contractor, e);
                showBookWarningDialog(this.presenter.resourceBundle
                        .getString("MainPresenter.bookWarningDialog.contractorDeletedMessage.text"));
            } else if (e.getCause() instanceof ContractorModifiedException) {
                MainPresenter.LOGGER
                        .log(
                                Level.WARNING,
                                "Customer "
                                        + this.customerId
                                        + " attempted to book a contractor that has been modified: "
                                        + this.contractor, e);
                showBookWarningDialog(this.presenter.resourceBundle
                        .getString("MainPresenter.bookWarningDialog.contractorModifiedMessage.text"));
            } else {
                MainPresenter.LOGGER.log(Level.SEVERE, "Customer "
                        + this.customerId
                        + " got an error booking contractor: "
                        + this.contractor, e);
                showBookErrorDialog();
            }
        }

        private void showBookWarningDialog(String message) {
            String title = this.presenter.resourceBundle
                    .getString("MainPresenter.bookWarningDialog.title.text");
            this.presenter.showOptionPane(message, title,
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
