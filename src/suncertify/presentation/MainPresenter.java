/*
 * MainPresenter.java
 *
 * 06 Jul 2007
 */

package suncertify.presentation;

import java.awt.Component;
import java.io.IOException;
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
 * A controller that is responsible for handling user events delegated to it
 * from the {@link MainView} and for updating the view based on data obtained
 * from the {@link BrokerService}.
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
     *                Broker service.
     * @param view
     *                Main view.
     * @throws IllegalArgumentException
     *                 If <code>service</code> or <code>view</code> is
     *                 <code>null</code>.
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
        resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
    }

    /**
     * Realises the view.
     */
    public void realiseView() {
        view.realise();
    }

    /**
     * Performs the search action. The work of the search action is performed on
     * a {@link SwingWorker} thread and the user interface controls are disabled
     * until the search action completes.
     * 
     * @param componentToFocus
     *                Component to focus when the search action has completed
     *                successfully.
     */
    public final void searchActionPerformed(Component componentToFocus) {
        /*
         * View returns an empty string if a criteria field is empty, map this
         * to null criteria field to indicate that the field should not be
         * searched on.
         */
        String nameCriteria = substituteNullForEmptyString(view
                .getNameCriteria().trim());
        String locationCriteria = substituteNullForEmptyString(view
                .getLocationCriteria().trim());

        final SearchCriteria searchCriteria = new SearchCriteria().setName(
                nameCriteria).setLocation(locationCriteria);

        SwingWorker<List<Contractor>, Void> searchWorker = createSearchWorker(
                searchCriteria, componentToFocus);
        view.disableControls();
        searchWorker.execute();
    }

    SwingWorker<List<Contractor>, Void> createSearchWorker(
            final SearchCriteria searchCriteria, Component componentToFocus) {
        return new SearchWorker(this, searchCriteria, componentToFocus);
    }

    private String substituteNullForEmptyString(String str) {
        String result = str;
        if ("".equals(str)) {
            result = null;
        }
        return result;
    }

    /**
     * Performs the book action after first displaying a dialog to capture the
     * ID of the customer making the booking. The work of the book action is
     * performed on a {@link SwingWorker} thread and the user interface controls
     * are disabled until the book action completes.
     * 
     * @param rowNo
     *                Table row number that contains the contractor to be
     *                booked.
     * @param componentToFocus
     *                Component to focus when the search action has completed
     *                successfully.
     */
    public final void bookActionPerformed(int rowNo, Component componentToFocus) {
        String customerId = showCustomerIdDialog();
        if (customerId == null) {
            // Dialog cancelled, return the focus to where it was previously
            componentToFocus.requestFocus();
        } else {
            Contractor contractor = view.getContractorAtRow(rowNo);
            SwingWorker<Void, Void> bookWorker = createBookWorker(customerId,
                    contractor, rowNo, componentToFocus);
            view.disableControls();
            bookWorker.execute();
        }
    }

    String showCustomerIdDialog() {
        CustomerIdDialog dialog = new CustomerIdDialog(view.getFrame());
        dialog.setVisible(true);
        return dialog.getCustomerId();
    }

    SwingWorker<Void, Void> createBookWorker(String customerId,
            Contractor contractor, int rowNo, Component componentToFocus) {
        return new BookWorker(this, customerId, contractor, rowNo,
                componentToFocus);
    }

    void showOptionPane(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(view.getFrame(), message, title,
                messageType);
    }

    private static final class SearchWorker extends
            SwingWorker<List<Contractor>, Void> {

        private final MainPresenter presenter;
        private final SearchCriteria searchCriteria;
        private final Component componentToFocus;

        public SearchWorker(MainPresenter presenter,
                SearchCriteria searchCriteria, Component componentToFocus) {
            this.presenter = presenter;
            this.searchCriteria = searchCriteria;
            this.componentToFocus = componentToFocus;
        }

        // This method is executed on a background thread
        @Override
        protected List<Contractor> doInBackground() throws IOException {
            return presenter.service.search(searchCriteria);
        }

        // This method is executed on the Event Dispatch Thread
        @Override
        protected void done() {
            try {
                /*
                 * Get the search results. The call to the get method will block
                 * the EDT but since we are in the done method we already know
                 * that doInBackground has finished and the search results are
                 * available.
                 */
                List<Contractor> contractors = get();

                LOGGER.info("Found " + contractors.size()
                        + " contractors matching criteria: " + searchCriteria);

                // Update the view
                presenter.view.setTableData(contractors);
                presenter.view
                        .setStatusLabelText(buildStatusLabelText(contractors
                                .size()));
            } catch (InterruptedException e) {
                LOGGER
                        .log(
                                Level.WARNING,
                                "Thread was interruped while waiting for result of search SwingWorker",
                                e);
                showSearchErrorDialog();
            } catch (ExecutionException e) {
                LOGGER.log(Level.SEVERE,
                        "Error searching for contractors with criteria: "
                                + searchCriteria, e);
                showSearchErrorDialog();
            } finally {
                presenter.view.enableControls(componentToFocus);
            }
        }

        private String buildStatusLabelText(int contractorsCount) {
            String oneContractorFormat = presenter.resourceBundle
                    .getString("MainPresenter.statusLabel.oneContractor.text");
            String manyContractorsFormat = presenter.resourceBundle
                    .getString("MainPresenter.statusLabel.manyContractors.text");

            // Use a choice format to get the correct pluralisation
            double[] limits = { 0, 1, ChoiceFormat.nextDouble(1) };
            String[] formats = { manyContractorsFormat, oneContractorFormat,
                    manyContractorsFormat };
            ChoiceFormat choiceFormat = new ChoiceFormat(limits, formats);

            MessageFormat messageFormat = new MessageFormat(
                    getMessageFormatPattern());
            messageFormat.setFormatByArgumentIndex(0, choiceFormat);

            return messageFormat.format(new Object[] { contractorsCount,
                    contractorsCount, searchCriteria.getName(),
                    searchCriteria.getLocation() });
        }

        private String getMessageFormatPattern() {
            String pattern;
            if (searchCriteria.getName() == null
                    && searchCriteria.getLocation() == null) {
                pattern = presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.noCriteria.text");
            } else if (searchCriteria.getName() == null) {
                pattern = presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.locationCriteria.text");
            } else if (searchCriteria.getLocation() == null) {
                pattern = presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.nameCriteria.text");
            } else {
                pattern = presenter.resourceBundle
                        .getString("MainPresenter.statusLabel.nameAndLocationCriteria.text");
            }
            return pattern;
        }

        private void showSearchErrorDialog() {
            String message = presenter.resourceBundle
                    .getString("MainPresenter.searchErrorDialog.message");
            String title = presenter.resourceBundle
                    .getString("MainPresenter.searchErrorDialog.title");
            presenter.showOptionPane(message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class BookWorker extends SwingWorker<Void, Void> {

        private final MainPresenter presenter;
        private final String customerId;
        private final Contractor contractor;
        private final int rowNo;
        private final Component componentToFocus;

        public BookWorker(MainPresenter presenter, String customerId,
                Contractor contractor, int rowNo, Component componentToFocus) {
            this.presenter = presenter;
            this.customerId = customerId;
            this.contractor = contractor;
            this.rowNo = rowNo;
            this.componentToFocus = componentToFocus;
        }

        // This method is executed on a background thread
        @Override
        protected Void doInBackground() throws IOException,
                ContractorDeletedException, ContractorModifiedException {
            presenter.service.book(customerId, contractor);
            return null;
        }

        // This method is executed on the Event Dispatch Thread
        @Override
        protected void done() {
            try {
                /*
                 * Calling the get method to determine if any exceptions were
                 * thrown in the background thread. No result to retrieve.
                 */
                get();

                /*
                 * Booking has succeeded, set the customer ID as the owner in
                 * the contractor.
                 */
                Contractor updatedContractor = new Contractor(contractor
                        .getRecordNumber(), new String[] {
                        contractor.getName(), contractor.getLocation(),
                        contractor.getSpecialties(), contractor.getSize(),
                        contractor.getRate(), customerId });

                LOGGER.info("Customer with ID=" + customerId
                        + " has booked contractor: " + updatedContractor);

                // Now update the table row containing this contractor
                presenter.view.updateContractorAtRow(rowNo, updatedContractor);
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
                presenter.view.enableControls(componentToFocus);
            }
        }

        private void showBookErrorDialog() {
            String message = presenter.resourceBundle
                    .getString("MainPresenter.bookErrorDialog.message");
            String title = presenter.resourceBundle
                    .getString("MainPresenter.bookErrorDialog.title");
            presenter.showOptionPane(message, title, JOptionPane.ERROR_MESSAGE);
        }

        private void handleBookException(ExecutionException e) {
            if (e.getCause() instanceof ContractorDeletedException) {
                LOGGER
                        .log(
                                Level.WARNING,
                                "Customer "
                                        + customerId
                                        + " attempted to book a contractor that has been deleted: "
                                        + contractor, e);
                showBookWarningDialog(presenter.resourceBundle
                        .getString("MainPresenter.bookWarningDialog.contractorDeleted.message"));
            } else if (e.getCause() instanceof ContractorModifiedException) {
                LOGGER
                        .log(
                                Level.WARNING,
                                "Customer "
                                        + customerId
                                        + " attempted to book a contractor that has been modified: "
                                        + contractor, e);
                showBookWarningDialog(presenter.resourceBundle
                        .getString("MainPresenter.bookWarningDialog.contractorModified.message"));
            } else {
                LOGGER.log(Level.SEVERE, "Customer " + customerId
                        + " got an error booking contractor: " + contractor, e);
                showBookErrorDialog();
            }
        }

        private void showBookWarningDialog(String message) {
            String title = presenter.resourceBundle
                    .getString("MainPresenter.bookWarningDialog.title.text");
            presenter.showOptionPane(message, title,
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
