package suncertify.presentation;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

public final class JGradientPanel extends JPanel {

    private final Color topColor;
    private final Color bottomColor;

    public JGradientPanel(Color topColor, Color bottomColor) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

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
