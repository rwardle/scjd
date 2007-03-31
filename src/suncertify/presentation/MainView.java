/*
 * MainView.java
 *
 * Created on 06-Jul-2005
 */

package suncertify.presentation;

/**
 * 
 * @author Richard Wardle
 */
public interface MainView {

    void setPresenter(MainPresenter presenter);
    
    /**
     * Realises the view and its components.
     */
    void realise();

    void setLabelText(String text);
}
