package suncertify.presentation;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.service.BrokerService;

@RunWith(TestClassRunner.class)
public class MainPresenterTest {

    private final Mockery context = new Mockery();
    private BrokerService mockBrokerService;
    private MainView mockView;
    private MainPresenter presenter;

    @Before
    public void setUp() {
        this.mockBrokerService = this.context.mock(BrokerService.class);
        this.mockView = this.context.mock(MainView.class);
        this.presenter = new MainPresenter(this.mockBrokerService,
                this.mockView);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void realiseView() {
        this.context.checking(new Expectations() {{
            one(MainPresenterTest.this.mockView).realise();
        }});
        this.presenter.realiseView();
    }

    @Test
    public void helloButtonActionPerformed() throws Exception {
        final String text = "hello";
        this.context.checking(new Expectations() {{
            one(MainPresenterTest.this.mockBrokerService).getHelloWorld();
               will(returnValue(text));
            one(MainPresenterTest.this.mockView).setLabelText(
                    with(equal(text)));
        }});
        this.presenter.helloButtonActionPerformed();
    }
}
