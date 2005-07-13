/*
 * MainFrame.java
 *
 * Created on 06-Jul-2005
 */


package suncertify.presentation;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * 
 * @author Richard Wardle
 */
public class MainFrame implements MainView {

    private JFrame frame;
    private JButton helloButton;
    private JLabel label;
    
    /**
     * Creates a new instance of <code>MainFrame</code>.
     */
    public MainFrame() {
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * {@inheritDoc}
     */
    public void initialiseComponents() {
        JPanel panel = new JPanel();
        this.frame.getContentPane().add(panel, BorderLayout.CENTER);
        this.helloButton = new JButton("Say hello ...");
        panel.add(this.helloButton);
        this.label = new JLabel();
        panel.add(this.label);
    }

    /**
     * {@inheritDoc}
     */
    public void realise() {
        this.frame.pack();
        this.frame.setVisible(true);
    }

    public void addHelloButtonListener(ActionListener listener) {
        this.helloButton.addActionListener(listener);
    }

    public void setLabelText(String text) {
        this.label.setText(text);
    }
}
