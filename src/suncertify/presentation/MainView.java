/*
 * MainView.java
 *
 * Created on 06-Jul-2005
 */


package suncertify.presentation;

import java.awt.event.ActionListener;


/**
 * 
 * @author Richard Wardle
 */
public interface MainView {

    /**
     * Initialise the view's components. This method should not realise the
     * components.
     */
    void initialiseComponents();


    /**
     * Realises the view and its components.
     */
    void realiseView();


    void addHelloButtonListener(ActionListener listener);


    void setLabelText(String text);
}
