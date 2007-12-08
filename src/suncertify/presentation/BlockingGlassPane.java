/*
 * GlassPane.java
 *
 * 06 Dec 2007 
 */

package suncertify.presentation;

import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

/**
 * A component for designed for use as a glass pane that will block all mouse
 * and keyboard input. In order to ensure that all keyboard events are blocked,
 * the glass pane will request the focus when it is made visible and will not
 * release it while it remains visible. It is up to clients to restore the focus
 * to its previous owner when the glass pane is hidden, if this is required.
 * This glass pane will also set the cursor to the {@link Cursor#WAIT_CURSOR}
 * while it is visible.
 * <p>
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/rootpane.html">How
 * to Use Root Panes</a> for more information on the glass pane.
 * 
 * @author Richard Wardle
 */
public final class BlockingGlassPane extends JComponent {

    /**
     * Creates a new instance of <code>GlassPane</code>.
     */
    public BlockingGlassPane() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        addMouseListener(new MouseAdapter() {
            // Blocking all mouse events
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            // Blocking all mouse motion events
        });
        addKeyListener(new KeyAdapter() {
            // Blocking all key events
        });

        // Don't let the focus leave the glasspane if it's visible
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                // no-op
            }

            public void focusLost(FocusEvent e) {
                if (isVisible()) {
                    requestFocus();
                }
            }
        });
    }

    /**
     * Makes the glasspane visible or invisible. Making the glasspane visible
     * will also make it request the focus.
     * 
     * @param visible
     *                <code>true</code> to make the component visible;
     *                <code>false</code> to make it invisible.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // Get the focus if we're showing the glasspane
        if (visible) {
            requestFocus();
        }
    }
}
