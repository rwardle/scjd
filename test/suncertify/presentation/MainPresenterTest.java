package suncertify.presentation;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingworker.SwingWorker.StateValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.service.BrokerService;
import suncertify.service.Contractor;
import suncertify.service.ContractorDeletedException;
import suncertify.service.ContractorModifiedException;
import suncertify.service.SearchCriteria;

public class MainPresenterTest {

    private final Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private boolean workerRunning;
    private BrokerService mockBrokerService;
    private MainView mockView;
    private MainPresenter presenter;
    private String customerId;
    private Container componentToFocus;

    @Before
    public void setUp() {
        workerRunning = false;
        mockBrokerService = context.mock(BrokerService.class);
        mockView = context.mock(MainView.class);
        presenter = new StubMainPresenter(mockBrokerService, mockView);
        customerId = "12345678";
        componentToFocus = new Container();
    }

    @After
    public void tearDown() {
        for (int i = 0; workerRunning && i < 10; i++) {
            // Wait for any swing workers to complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNullServicePassedToConstructor() {
        new MainPresenter(null, mockView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNullViewPassedToConstructor() {
        new MainPresenter(mockBrokerService, null);
    }

    @Test
    public void shouldRealiseView() {
        context.checking(new Expectations() {
            {
                one(mockView).realise();
            }
        });
        presenter.realiseView();
    }

    @Test
    public void shouldPerformSearchAction() throws Exception {
        final String nameCriteria = "name";
        final String locationCriteria = "location";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        contractors.add(new Contractor(1, new String[] { "name", "location",
                "spec1", "size1", "rate1", "owner1" }));
        contractors.add(new Contractor(2, new String[] { "name", "location",
                "spec2", "size2", "rate2", "owner2" }));
        final String statusLabelText = "<html>Viewing <b>" + contractors.size()
                + " contractors</b> where Name is <b>" + nameCriteria
                + "</b> and Location is <b>" + locationCriteria + "</b></html>";
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                one(mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(mockBrokerService).search(
                        with(searchCriteriaMatching(nameCriteria,
                                locationCriteria)));
                will(returnValue(contractors));

                one(mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(mockView).setStatusLabelText(with(equal(statusLabelText)));

                one(mockView).enableControls(with(equal(componentToFocus)));
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldPerformSearchActionWhenOnlyNameCriteriaIsSpecified()
            throws Exception {
        final String nameCriteria = "name";
        final String locationCriteria = "";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing <b>" + contractors.size()
                + " contractors</b> where Name is <b>" + nameCriteria
                + "</b></html>";
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                one(mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(mockBrokerService).search(
                        with(searchCriteriaMatching(nameCriteria, null)));
                will(returnValue(contractors));

                one(mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(mockView).setStatusLabelText(with(equal(statusLabelText)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldPerformSearchActionWhenOnlyLocationCriteriaIsSpecified()
            throws Exception {
        final String nameCriteria = "";
        final String locationCriteria = "location";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing <b>" + contractors.size()
                + " contractors</b> where Location is <b>" + locationCriteria
                + "</b></html>";
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                one(mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(mockBrokerService).search(
                        with(searchCriteriaMatching(null, locationCriteria)));
                will(returnValue(contractors));

                one(mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(mockView).setStatusLabelText(with(equal(statusLabelText)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldPerformSearchActionWhenNoCriteriaAreSpecified()
            throws Exception {
        final String nameCriteria = "";
        final String locationCriteria = "";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        contractors.add(new Contractor(1, new String[] { "name", "location",
                "spec1", "size1", "rate1", "owner1" }));
        final String statusLabelText = "<html>Viewing all <b>"
                + contractors.size() + " contractor</b></html>";
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                one(mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(mockBrokerService).search(
                        with(searchCriteriaMatching(null, null)));
                will(returnValue(contractors));

                one(mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(mockView).setStatusLabelText(with(equal(statusLabelText)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldTrimSearchCriteria() throws Exception {
        final String nameCriteria = " ";
        final String locationCriteria = " ";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing all <b>"
                + contractors.size() + " contractors</b></html>";
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                one(mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(mockBrokerService).search(
                        with(searchCriteriaMatching(null, null)));
                will(returnValue(contractors));

                one(mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(mockView).setStatusLabelText(with(equal(statusLabelText)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfSearchThrowsException()
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockView).disableControls();

                allowing(mockView).getNameCriteria();

                allowing(mockView).getLocationCriteria();

                one(mockBrokerService).search(with(any(SearchCriteria.class)));
                will(throwException(new IOException()));

                never(mockView).setTableModel(
                        with(any(ContractorTableModel.class)));

                never(mockView).setStatusLabelText(with(any(String.class)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.searchActionPerformed(componentToFocus);
    }

    @Test
    public void shouldPerformBookAction() throws Exception {
        final int rowNo = 1;
        String[] preBookingContractorData = new String[] { "name", "location",
                "spec", "size", "rate", "owner" };
        final Contractor preBookingContractor = new Contractor(rowNo,
                preBookingContractorData);
        String[] postBookingContractorData = preBookingContractorData.clone();
        postBookingContractorData[PresentationConstants.TABLE_OWNER_COLUMN_INDEX] = customerId;
        final Contractor postBookingContractor = new Contractor(rowNo,
                postBookingContractorData);
        context.checking(new Expectations() {
            {
                one(mockView).getContractorAtRow(with(equal(rowNo)));
                will(returnValue(preBookingContractor));

                one(mockView).disableControls();

                one(mockBrokerService).book(with(equal(customerId)),
                        with(aContractorMatching(preBookingContractor)));

                one(mockView).updateContractorAtRow(with(equal(rowNo)),
                        with(aContractorMatching(postBookingContractor)));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.bookActionPerformed(rowNo, componentToFocus);
    }

    @Test
    public void shouldNotBookIfUserCancelsCustomerIdDialog() throws Exception {
        // Null returned from customer dialog means that it was cancelled
        customerId = null;
        final int rowNo = 1;
        presenter.bookActionPerformed(rowNo, componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsIOException()
            throws Exception {
        final int rowNo = 1;
        context.checking(new Expectations() {
            {
                allowing(mockView).getContractorAtRow(with(any(int.class)));

                one(mockView).disableControls();

                one(mockBrokerService).book(with(any(String.class)),
                        with(any(Contractor.class)));
                will(throwException(new IOException()));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.bookActionPerformed(rowNo, componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorDeletedException()
            throws Exception {
        final int rowNo = 1;
        context.checking(new Expectations() {
            {
                allowing(mockView).getContractorAtRow(with(any(int.class)));

                one(mockView).disableControls();

                one(mockBrokerService).book(with(any(String.class)),
                        with(any(Contractor.class)));
                will(throwException(new ContractorDeletedException()));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.bookActionPerformed(rowNo, componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorModifiedException()
            throws Exception {
        final int rowNo = 1;
        context.checking(new Expectations() {
            {
                allowing(mockView).getContractorAtRow(with(any(int.class)));

                one(mockView).disableControls();

                one(mockBrokerService).book(with(any(String.class)),
                        with(any(Contractor.class)));
                will(throwException(new ContractorModifiedException()));

                one(mockView).enableControls(componentToFocus);
            }
        });
        presenter.bookActionPerformed(rowNo, componentToFocus);
    }

    private Matcher<ContractorTableModel> aContractorTableModelContaining(
            List<Contractor> contractors) {
        return new ContractorTableModelContaining(contractors);
    }

    private static final class ContractorTableModelContaining extends
            BaseMatcher<ContractorTableModel> {

        private final List<Contractor> contractors;

        public ContractorTableModelContaining(List<Contractor> contractors) {
            this.contractors = contractors;
        }

        public boolean matches(Object item) {
            ContractorTableModel tableModel = (ContractorTableModel) item;
            if (contractors.size() != tableModel.getRowCount()) {
                return false;
            }

            boolean match = true;
            for (int i = 0; i < contractors.size(); i++) {
                match = new ContractorMatching(contractors.get(i))
                        .matches(tableModel.getContractorAtRow(i));
            }
            return match;
        }

        public void describeTo(Description description) {
            description.appendValueList("a table model containing: ", " & ",
                    "", contractors);
        }
    }

    private Matcher<SearchCriteria> searchCriteriaMatching(String nameCriteria,
            String locationCriteria) {
        return new SearchCriteriaMatching(nameCriteria, locationCriteria);
    }

    private static final class SearchCriteriaMatching extends
            BaseMatcher<SearchCriteria> {

        private final String nameCriteria;
        private final String locationCriteria;

        public SearchCriteriaMatching(String nameCriteria,
                String locationCriteria) {
            this.nameCriteria = nameCriteria;
            this.locationCriteria = locationCriteria;
        }

        public boolean matches(Object item) {
            SearchCriteria searchCriteria = (SearchCriteria) item;
            return nameCriteria == null ? searchCriteria.getName() == null
                    : nameCriteria.equals(searchCriteria.getName())
                            && locationCriteria == null ? searchCriteria
                            .getLocation() == null : locationCriteria
                            .equals(searchCriteria.getLocation());
        }

        public void describeTo(Description description) {
            description.appendText("search criteria matching: name=")
                    .appendText(nameCriteria).appendText(", location=")
                    .appendText(locationCriteria);
        }
    }

    private Matcher<Contractor> aContractorMatching(Contractor contractor) {
        return new ContractorMatching(contractor);
    }

    private static final class ContractorMatching extends
            BaseMatcher<Contractor> {

        private final Contractor expectedContractor;

        public ContractorMatching(Contractor contractor) {
            expectedContractor = contractor;
        }

        public boolean matches(Object item) {
            Contractor actualContractor = (Contractor) item;
            return expectedContractor.getRecordNumber() == actualContractor
                    .getRecordNumber()
                    && expectedContractor.getName() == null ? actualContractor
                    .getName() == null
                    : expectedContractor.getName().equals(
                            actualContractor.getName())
                            && expectedContractor.getLocation() == null ? actualContractor
                            .getLocation() == null
                            : expectedContractor.getLocation().equals(
                                    actualContractor.getLocation())
                                    && expectedContractor.getSpecialties() == null ? actualContractor
                                    .getSpecialties() == null
                                    : expectedContractor.getSpecialties()
                                            .equals(
                                                    actualContractor
                                                            .getSpecialties())
                                            && expectedContractor.getSize() == null ? actualContractor
                                            .getSize() == null
                                            : expectedContractor.getSize()
                                                    .equals(
                                                            actualContractor
                                                                    .getSize())
                                                    && expectedContractor
                                                            .getRate() == null ? actualContractor
                                                    .getRate() == null
                                                    : expectedContractor
                                                            .getRate()
                                                            .equals(
                                                                    actualContractor
                                                                            .getRate())
                                                            && expectedContractor
                                                                    .getOwner() == null ? actualContractor
                                                            .getOwner() == null
                                                            : expectedContractor
                                                                    .getOwner()
                                                                    .equals(
                                                                            actualContractor
                                                                                    .getOwner());
        }

        public void describeTo(Description description) {
            description.appendText("a contractor matching: "
                    + expectedContractor);
        }
    }

    private class StubMainPresenter extends MainPresenter {

        public StubMainPresenter(BrokerService brokerService, MainView view) {
            super(brokerService, view);
        }

        @Override
        SwingWorker<List<Contractor>, Void> createSearchWorker(
                SearchCriteria searchCriteria, Component component) {
            SwingWorker<List<Contractor>, Void> worker = super
                    .createSearchWorker(searchCriteria, component);
            addWorkerPropertyChangeListener(worker);
            return worker;
        }

        @Override
        String showCustomerIdDialog() {
            return customerId;
        }

        @Override
        SwingWorker<Void, Void> createBookWorker(String id,
                Contractor contractor, int rowNo, Component component) {
            SwingWorker<Void, Void> worker = super.createBookWorker(id,
                    contractor, rowNo, component);
            addWorkerPropertyChangeListener(worker);
            return worker;
        }

        @SuppressWarnings("unchecked")
        private void addWorkerPropertyChangeListener(SwingWorker worker) {
            workerRunning = true;
            worker.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("state".equals(evt.getPropertyName())
                            && evt.getNewValue() == StateValue.DONE) {
                        workerRunning = false;
                    }
                }
            });
        }

        @Override
        void showOptionPane(String message, String title, int messageType) {
            // Prevent dialog from showing in tests
        }
    }
}
