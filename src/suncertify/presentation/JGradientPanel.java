package suncertify.presentation;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

/**
 * Extends {@link JPanel} to use a gradient paint from top-to-bottom using the
 * colours specified in the constructor.
 * 
 * @author Richard Wardle
 */
public final class JGradientPanel extends JPanel {

    private final Color topColor;
    private final Color bottomColor;

    /**
     * Creates a new instance of <code>JGradientPanel</code> with the
     * specified colours.
     * 
     * @param topColor
     *                Colour to use for the top of the gradient paint.
     * @param bottomColor
     *                Colour to use for the bottom of the gradient paint.
     */
    public JGradientPanel(Color topColor, Color bottomColor) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

    /**
     * Paints a gradient paint using the <code>topColor</code> and
     * <code>bottomColor</code> in the rectangle that makes up the bounds of
     * this panel.
     * 
     * @param g
     *                <code>Graphics</code> object.
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle bounds = getBounds();
        GradientPaint gradientPaint = new GradientPaint(0, 0, topColor, 0,
                bounds.height, bottomColor);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, bounds.width, bounds.height);
    }
}
