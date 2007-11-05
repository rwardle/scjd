package suncertify.presentation;

import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

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
    private JDialog mockCustomerIdDialog;

    @Before
    public void setUp() {
        this.workerRunning = false;
        this.mockBrokerService = this.context.mock(BrokerService.class);
        this.mockView = this.context.mock(MainView.class);
        this.presenter = new StubMainPresenter(this.mockBrokerService,
                this.mockView);
        this.mockCustomerIdDialog = this.context.mock(JDialog.class);
    }

    @After
    public void verify() {
        while (this.workerRunning) {
            // Wait for any swing workers to complete
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
                one(MainPresenterTest.this.mockView).showGlassPane();

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

                one(MainPresenterTest.this.mockView).hideGlassPane();
            }
        });
        this.presenter.searchButtonActionPerformed();
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
                one(MainPresenterTest.this.mockView).showGlassPane();

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

                one(MainPresenterTest.this.mockView).hideGlassPane();
            }
        });
        this.presenter.searchButtonActionPerformed();
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
                one(MainPresenterTest.this.mockView).showGlassPane();

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

                one(MainPresenterTest.this.mockView).hideGlassPane();
            }
        });
        this.presenter.searchButtonActionPerformed();
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
                one(MainPresenterTest.this.mockView).showGlassPane();

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

                one(MainPresenterTest.this.mockView).hideGlassPane();
            }
        });
        this.presenter.searchButtonActionPerformed();
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
                one(MainPresenterTest.this.mockView).showGlassPane();

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

                one(MainPresenterTest.this.mockView).hideGlassPane();
            }
        });
        this.presenter.searchButtonActionPerformed();
    }

    @Test
    public void bookButtonActionPerformed() throws Exception {
        // TODO
        fail();
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
            MainPresenterTest.this.workerRunning = true;
            worker.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("state".equals(evt.getPropertyName())
                            && evt.getNewValue() == StateValue.DONE) {
                        MainPresenterTest.this.workerRunning = false;
                    }
                }
            });
            return worker;
        }
    }
}
