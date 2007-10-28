/*
 * MainFrame.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import suncertify.ApplicationConstants;
import suncertify.service.Contractor;

/**
 * 
 * @author Richard Wardle
 */
public final class MainFrame extends JFrame implements MainView {

    // TODO
    // Add button to "Retreive All" - this could also be a menu option?
    // Colour-code table rows based on contractor status
    // Add toolbar? What to put in it?
    // About box could show current configuration details
    // Add help question marks (like in Eclipse).
    // Use Glasspane and busy cursor when making search calls
    // Check on small screen resultions 640x480
    // Make escape key clear text fields?
    // Mac scrolling bug - just in java 6 beta?
    // Focus goes to text field when clicking on button in table

    private static final long serialVersionUID = 1L;
    // TODO Adjust these sizes
    private static final Dimension PREFERRED_SIZE = new Dimension(640, 480);
    // TODO Minimum size has no effect on Mac
    private static final Dimension MINIMUM_SIZE = new Dimension(320, 240);
    private static final Dimension TEXT_FIELD_PREFERRED_SIZE = new Dimension(
            100, 25);

    private final ResourceBundle resourceBundle;
    private final JLabel statusLabel;
    private final JTable resultsTable;
    private final JTextField nameTextField;
    private final JTextField locationTextField;
    private final JButton searchButton;
    private MainPresenter presenter;

    public MainFrame() {
        this.resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        this.statusLabel = new JLabel();

        // Display an empty table initially
        this.resultsTable = new JTable(new ContractorTableModel(
                new ArrayList<Contractor>()), new ContractorTableColumnModel());
        this.resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.nameTextField = new JTextField();
        this.nameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.presenter.searchButtonActionPerformed();
            }
        });

        this.locationTextField = new JTextField();
        this.locationTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.presenter.searchButtonActionPerformed();
            }
        });

        this.searchButton = new JButton(this.resourceBundle
                .getString("MainFrame.searchButton.text"));
        this.searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.presenter.searchButtonActionPerformed();
            }
        });

        // TODO Hook this into shutdown handler
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(initialiseMenuBar());
        setPreferredSize(PREFERRED_SIZE);
        setMinimumSize(MINIMUM_SIZE);
        setLayout(new GridBagLayout());
        initialiseComponents();
    }

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    public void realise() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        setVisible(true);
    }

    public String getNameCriteria() {
        return this.nameTextField.getText();
    }

    public void setNameCriteria(String nameCriteria) {
        this.nameTextField.setText(null);
    }

    public String getLocationCriteria() {
        return this.locationTextField.getText();
    }

    public void setLocationCriteria(String locationCriteria) {
        this.locationTextField.setText(null);
    }

    public void setTableModel(ContractorTableModel tableModel) {
        this.resultsTable.setModel(tableModel);
    }

    public void setStatusLabelText(String text) {
        this.statusLabel.setText(text);
    }

    private JMenuBar initialiseMenuBar() {
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText(this.resourceBundle
                .getString("MainFrame.exitMenuItem.text"));

        JMenu fileMenu = new JMenu();
        fileMenu.setText(this.resourceBundle
                .getString("MainFrame.fileMenu.text"));
        fileMenu.add(exitMenuItem);

        JMenuItem helpContentsMenuItem = new JMenuItem();
        helpContentsMenuItem.setText(this.resourceBundle
                .getString("MainFrame.helpContentsMenuItem.text"));

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText(this.resourceBundle
                .getString("MainFrame.aboutMenuItem.text"));

        JMenu helpMenu = new JMenu();
        helpMenu.setText(this.resourceBundle
                .getString("MainFrame.helpMenu.text"));
        helpMenu.add(helpContentsMenuItem);
        helpMenu.add(aboutMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void initialiseComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(initialiseSearchPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(initialiseResultsPanel(), constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(initialiseStatusPanel(), constraints);
    }

    private JPanel initialiseSearchPanel() {
        JGradientPanel panel = new JGradientPanel(
                ApplicationConstants.DARK_BLUE, ApplicationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 6, 0, 4);
        panel.add(new JLabel(this.resourceBundle
                .getString("MainFrame.preambleLabel.text")), constraints);

        Insets criteriaLabelInsets = new Insets(0, 6, 0, 4);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = criteriaLabelInsets;
        panel.add(new JLabel(this.resourceBundle
                .getString("MainFrame.nameLabel.text")), constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = criteriaLabelInsets;
        panel.add(new JLabel(this.resourceBundle
                .getString("MainFrame.locationLabel.text")), constraints);

        Insets insetsZeroTop = new Insets(0, 4, 15, 4);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        constraints.weightx = 1;
        this.nameTextField.setPreferredSize(TEXT_FIELD_PREFERRED_SIZE);
        panel.add(this.nameTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        constraints.weightx = 1;
        this.locationTextField.setPreferredSize(TEXT_FIELD_PREFERRED_SIZE);
        panel.add(this.locationTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        this.searchButton.setOpaque(false); // Mac VM bug?
        panel.add(this.searchButton, constraints);

        return panel;
    }

    private Component initialiseResultsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ApplicationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setBackground(ApplicationConstants.LIGHT_BLUE);
        scrollPane.setViewportView(this.resultsTable);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel.add(scrollPane, constraints);

        return panel;
    }

    private JPanel initialiseStatusPanel() {
        JGradientPanel panel = new JGradientPanel(
                ApplicationConstants.LIGHT_BLUE, ApplicationConstants.DARK_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 4, 2, 4);
        constraints.weightx = 1;
        panel.add(this.statusLabel, constraints);

        return panel;
    }
}
