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
        this.workerRunning = false;
        this.mockBrokerService = this.context.mock(BrokerService.class);
        this.mockView = this.context.mock(MainView.class);
        this.presenter = new StubMainPresenter(this.mockBrokerService,
                this.mockView);
        this.customerId = "12345678";
        this.componentToFocus = new Container();
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
                will(Expectations.returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(Expectations.returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(Expectations.equal(new SearchCriteria().setName(
                                nameCriteria).setLocation(locationCriteria))));
                will(Expectations.returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.equal(statusLabelText)));

                one(MainPresenterTest.this.mockView)
                        .enableControls(
                                with(Expectations
                                        .equal(MainPresenterTest.this.componentToFocus)));
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
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
                will(Expectations.returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(Expectations.returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(Expectations.equal(new SearchCriteria()
                                .setName(nameCriteria))));
                will(Expectations.returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
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
                will(Expectations.returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(Expectations.returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(Expectations.equal(new SearchCriteria()
                                .setLocation(locationCriteria))));
                will(Expectations.returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
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
                will(Expectations.returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(Expectations.returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(Expectations.equal(new SearchCriteria())));
                will(Expectations.returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
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
                will(Expectations.returnValue(nameCriteria));

                one(MainPresenterTest.this.mockView).getLocationCriteria();
                will(Expectations.returnValue(locationCriteria));

                one(MainPresenterTest.this.mockBrokerService).search(
                        with(Expectations.equal(new SearchCriteria())));
                will(Expectations.returnValue(contractors));

                one(MainPresenterTest.this.mockView).setTableModel(
                        with(aContractorTableModelContaining(contractors)));

                one(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.equal(statusLabelText)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
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
                        with(Expectations.any(SearchCriteria.class)));
                will(Expectations.throwException(new IOException()));

                never(MainPresenterTest.this.mockView).setTableModel(
                        with(Expectations.any(ContractorTableModel.class)));

                never(MainPresenterTest.this.mockView).setStatusLabelText(
                        with(Expectations.any(String.class)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.searchActionPerformed(this.componentToFocus);
    }

    @Test
    public void bookButtonActionPerformed() throws Exception {
        final int rowNo = 1;
        String[] preBookingContractorData = new String[] { "name", "location",
                "spec", "size", "rate", "owner" };
        final Contractor preBookingContractor = new Contractor(rowNo,
                preBookingContractorData);
        String[] postBookingContractorData = preBookingContractorData.clone();
        postBookingContractorData[PresentationConstants.TABLE_OWNER_COLUMN_INDEX] = this.customerId;
        final Contractor postBookingContractor = new Contractor(rowNo,
                postBookingContractorData);
        this.context.checking(new Expectations() {
            {
                one(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(Expectations.equal(rowNo)));
                will(Expectations.returnValue(preBookingContractor));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(Expectations
                                .equal(MainPresenterTest.this.customerId)),
                        with(Expectations.equal(preBookingContractor)));

                one(MainPresenterTest.this.mockView).updateContractorAtRow(
                        with(Expectations.equal(rowNo)),
                        with(Expectations.equal(postBookingContractor)));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.bookActionPerformed(rowNo, this.componentToFocus);
    }

    @Test
    public void shouldNotBookIfUserCancelsCustomerIdDialog() throws Exception {
        // Null returned from customer dialog means that it was cancelled
        this.customerId = null;
        final int rowNo = 1;
        this.presenter.bookActionPerformed(rowNo, this.componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsIOException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(Expectations.any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(Expectations.any(String.class)),
                        with(Expectations.any(Contractor.class)));
                will(Expectations.throwException(new IOException()));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.bookActionPerformed(rowNo, this.componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorDeletedException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(Expectations.any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(Expectations.any(String.class)),
                        with(Expectations.any(Contractor.class)));
                will(Expectations
                        .throwException(new ContractorDeletedException()));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.bookActionPerformed(rowNo, this.componentToFocus);
    }

    @Test
    public void shouldNotUpdateInterfaceIfBookThrowsContractorModifiedException()
            throws Exception {
        final int rowNo = 1;
        this.context.checking(new Expectations() {
            {
                allowing(MainPresenterTest.this.mockView).getContractorAtRow(
                        with(Expectations.any(int.class)));

                one(MainPresenterTest.this.mockView).disableControls();

                one(MainPresenterTest.this.mockBrokerService).book(
                        with(Expectations.any(String.class)),
                        with(Expectations.any(Contractor.class)));
                will(Expectations
                        .throwException(new ContractorModifiedException()));

                one(MainPresenterTest.this.mockView).enableControls(
                        MainPresenterTest.this.componentToFocus);
            }
        });
        this.presenter.bookActionPerformed(rowNo, this.componentToFocus);
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
                SearchCriteria searchCriteria, Component component) {
            SwingWorker<List<Contractor>, Void> worker = super
                    .createSearchWorker(searchCriteria, component);
            addWorkerPropertyChangeListener(worker);
            return worker;
        }

        @Override
        String showCustomerIdDialog() {
            return MainPresenterTest.this.customerId;
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
