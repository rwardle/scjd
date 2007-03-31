package suncertify.presentation;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.service.BrokerService;

@RunWith(TestClassRunner.class)
public class MainPresenterTest extends MockObjectTestCase {
    
    private Mock mockBrokerService;
    private Mock mockView;   
    private MainPresenter presenter;
    
    @Before
    public void setUp() {
        this.mockBrokerService = mock(BrokerService.class);
        this.mockView = mock(MainView.class);
        this.presenter = new MainPresenter((BrokerService) this.mockBrokerService.proxy(), 
                (MainView) this.mockView.proxy());
    }
 
    @After
    public void verify() {
        super.verify();
    }
    
    @Test
    public void realiseView() {
        this.mockView.expects(once()).method("realise");
        this.presenter.realiseView();
    }
    
    @Test
    public void helloButtonActionPerformed() {
        String text = "hello";
        this.mockBrokerService.expects(once()).method("getHelloWorld")
                .will(returnValue(text));
        this.mockView.expects(once()).method("setLabelText").with(eq(text));
        this.presenter.helloButtonActionPerformed();
    }
}
