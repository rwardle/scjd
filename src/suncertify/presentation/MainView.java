/*
 * MainView.java
 *
 * Created on 06-Jul-2007
 */

package suncertify.presentation;

/**
 * 
 * @author Richard Wardle
 */
public interface MainView {

    /**
     * Sets the main presenter.
     * 
     * @param presenter The presenter.
     */
    void setPresenter(MainPresenter presenter);
    
    /** Realises the view and its components. */
    void realise();

    void setLabelText(String text);
}
