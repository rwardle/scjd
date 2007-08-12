/*
 * MainPresenter.java
 *
 * Created on 06-Jul-2007
 */

package suncertify.presentation;

import java.io.IOException;
import suncertify.service.BrokerService;

/**
 * Encapsulates the presentation behaviour of the main frame.
 * 
 * @author Richard Wardle
 */
public class MainPresenter {

    private BrokerService service;
    private MainView view;
    
    /**
     * Creates a new instance of <code>MainPresenter</code>.
     * 
     * @param service The broker service.
     * @param view The main view.
     */
    public MainPresenter(BrokerService service, MainView view) {
        // TODO: Check args for null
        this.service = service;
        this.view = view;
    }
 
    /** Realises the view. */
    public void realiseView() {
        this.view.realise();
    }
    
    public final void helloButtonActionPerformed() {
        try {
            this.view.setLabelText(this.service.getHelloWorld());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
