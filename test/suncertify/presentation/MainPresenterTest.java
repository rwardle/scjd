package suncertify.presentation;

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

import suncertify.ApplicationConstants;
import suncertify.service.BrokerService;
import suncertify.service.Contractor;
import suncertify.service.ContractorDeletedException;
import suncertify.service.ContractorModifiedException;
import suncertify.service.SearchCriteria;

@SuppressWarnings("boxing")
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

    @Before
    public void setUp() {
        this.workerRunning = false;
        this.mockBrokerService = this.context.mock(BrokerService.class);
        this.mockView = this.context.mock(MainView.class);
        this.presenter = new StubMainPresenter(this.mockBrokerService,
                this.mockView);
        this.customerId = "12345678";
    }

    @After
    public void tearDown() {
        for (int i = 0; this.workerRunning && i < 10; i++) {
            // Wait for any swing workers to complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionWithNullServiceThrowsException() {
        new MainPresenter(null, this.mockView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionWithNullViewThrowsException() {
        new MainPresenter(this.mockBrokerService, null);
    }

    @Test
    public void realiseView() {
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).realise();
            }
        });
        this.presenter.realiseView();
    }

    @Test
    public void searchButtonActionPerformed() throws Exception {
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
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(equal(new SearchCriteria().setName(nameCriteria)
                                .setLocation(locationCriteria))));
                will(returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void searchButtonActionPerformedNameCriteriaOnly() throws Exception {
        final String nameCriteria = "name";
        final String locationCriteria = "";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing <b>" + contractors.size()
                + " contractors</b> where Name is <b>" + nameCriteria
                + "</b></html>";
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService)
                        .search(
                                with(equal(new SearchCriteria()
                                        .setName(nameCriteria))));
                will(returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void searchButtonActionPerformedLocationCriteriaOnly()
            throws Exception {
        final String nameCriteria = "";
        final String locationCriteria = "location";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing <b>" + contractors.size()
                + " contractors</b> where Location is <b>" + locationCriteria
                + "</b></html>";
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(equal(new SearchCriteria()
                                .setLocation(locationCriteria))));
                will(returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void searchButtonActionPerformedNoCriteria() throws Exception {
        final String nameCriteria = "";
        final String locationCriteria = "";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        contractors.add(new Contractor(1, new String[] { "name", "location",
                "spec1", "size1", "rate1", "owner1" }));
        final String statusLabelText = "<html>Viewing all <b>"
                + contractors.size() + " contractor</b></html>";
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(equal(new SearchCriteria())));
                will(returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void shouldTrimSearchCriteria() throws Exception {
        final String nameCriteria = " ";
        final String locationCriteria = " ";
        final List<Contractor> contractors = new ArrayList<Contractor>();
        final String statusLabelText = "<html>Viewing all <b>"
                + contractors.size() + " contractors</b></html>";
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockView).getNameCriteria();
                will(returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(equal(new SearchCriteria())));
                will(returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void shouldNotUpdateInterfaceIfSearchThrowsException()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).disableControls();

                allowing(MainPresenterTest.this.mockView).getNameCriteria();

                allowing(MainPresenterTest.this.mockView).getLocationCriteria();

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(any(SearchCriteria.class)));
                will(throwException(new IOException()));

                never(MainPresenterTest.this.mockView).setTableModel(
                        with(any(ContractorTableModel.class)));

                never(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(any(String.class)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.searchActionPerformed();
    }

    @Test
    public void bookButtonActionPerformed() throws Exception {
        final int rowNo = 1;
        String[] preBookingContractorData = new String[] { "name", "location",
                "spec", "size", "rate", "owner" };
        final Contractor preBookingContractor = new Contractor(rowNo,
                preBookingContractorData);
        String[] postBookingContractorData = preBookingContractorData.clone();
        postBookingContractorData[ApplicationConstants.TABLE_OWNER_COLUMN_INDEX] = this.customerId;
        final Contractor postBookingContractor = new Contractor(rowNo,
                postBookingContractorData);
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(equal(rowNo)));
                will(returnValue(preBookingContractor));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(equal(MainPresenterTest.this.customerId)),
                        with(equal(preBookingContractor)));

                one(MainPresenterTest.this.mockView).updateContractorAtRow(
                        with(equal(rowNo)), with(equal(postBookingContractor)));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.bookActionPerformed(rowNo);
    }

    @Test
    public void shouldNotBookIfUserCancelsCustomerIdDialog() throws Exception {
        // Null returned from customer dialog means that it was cancelled
        this.customerId = null;
        final int rowNo = 1;
        this.presenter.bookActionPerformed(rowNo);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsIOException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(any(String.class)), with(any(Contractor.class)));
                will(throwException(new IOException()));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.bookActionPerformed(rowNo);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorDeletedException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(any(String.class)), with(any(Contractor.class)));
                will(throwException(new ContractorDeletedException()));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.bookActionPerformed(rowNo);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorModifiedException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(any(String.class)), with(any(Contractor.class)));
                will(throwException(new ContractorModifiedException()));

                one(MainPresenterTest.this.mockView).enableControls();
            }
        });
        this.presenter.bookActionPerformed(rowNo);
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
            if (this.contractors.size() != tableModel.getRowCount()) {
                return false;
            }

            boolean match = true;
            for (int i = 0; i < this.contractors.size(); i++) {
                String[] contractorData = new String[6];
                for (int j = 0; j < contractorData.length; j++) {
                    contractorData[j] = (String) tableModel.getValueAt(i, j);
                }

                Contractor actualContractor = new Contractor(tableModel
                        .getRecordNumberAt(i), contractorData);
                match = this.contractors.get(i).equals(actualContractor);
            }
            return match;
        }

        public void describeTo(Description description) {
            description.appendValueList("a table model containing: ", " & ",
                    "", this.contractors);
        }
    }

    private class StubMainPresenter extends MainPresenter {

        public StubMainPresenter(BrokerService brokerService, MainView view) {
            super(brokerService, view);
        }

        @Override
        SwingWorker<List<Contractor>, Void> createSearchWorker(
                SearchCriteria searchCriteria) {
            SwingWorker<List<Contractor>, Void> worker = super
                    .createSearchWorker(searchCriteria);
            addWorkerPropertyChangeListener(worker);
            return worker;
        }

        @Override
        String showCustomerIdDialog() {
            return MainPresenterTest.this.customerId;
        }

        @Override
        SwingWorker<Void, Void> createBookWorker(String id,
                Contractor contractor, int rowNo) {
            SwingWorker<Void, Void> worker = super.createBookWorker(id,
                    contractor, rowNo);
            addWorkerPropertyChangeListener(worker);
            return worker;
        }

        @SuppressWarnings("unchecked")
        private void addWorkerPropertyChangeListener(SwingWorker worker) {
            MainPresenterTest.this.workerRunning = true;
            worker.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("state".equals(evt.getPropertyName())
                            && evt.getNewValue() == StateValue.DONE) {
                        MainPresenterTest.this.workerRunning = false;
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
