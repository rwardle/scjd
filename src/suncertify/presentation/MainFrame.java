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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import suncertify.service.Contractor;

/**
 * An application main frame.
 * 
 * @author Richard Wardle
 */
public final class MainFrame extends JFrame implements MainView {

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class
            .getName());
    private static final String BLANK_STATUS_LABEL = " ";
    private static final Dimension MINIMUM_SIZE = new Dimension(320, 240);
    private static final Dimension PREFERRED_SIZE = new Dimension(640, 480);
    private static final int TABLE_ROW_HEIGHT = 18;
    private static final Dimension TEXT_FIELD_PREFERRED_SIZE = new Dimension(
            100, 25);
    private static final String USER_GUIDE_PATH = "suncertify/presentation/userguide.html";

    private final ResourceBundle resourceBundle;
    private final JLabel statusLabel;
    private final ContractorTableColumnModel contractorTableColumnModel;
    private final JTable resultsTable;
    private final JTextField nameTextField;
    private final JTextField locationTextField;
    private final SearchAction searchAction;
    private final ClearCriteriaAction clearCriteriaAction;
    private final Component glassPane;
    private final ContractorTableModel tableModel;
    private MainPresenter presenter;

    /**
     * Creates a new instance of <code>MainFrame</code>.
     */
    public MainFrame() {
        resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");

        /*
         * Set a blank status label initially to ensure that it does not have
         * zero height in the layout.
         */
        statusLabel = new JLabel(BLANK_STATUS_LABEL);

        // Display an empty table initially
        tableModel = new ContractorTableModel(new ArrayList<Contractor>());

        contractorTableColumnModel = new ContractorTableColumnModel();
        resultsTable = createResultsTable();
        resultsTable.setRowHeight(TABLE_ROW_HEIGHT);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setSurrendersFocusOnKeystroke(true);

        nameTextField = new JTextField();
        nameTextField.setToolTipText(resourceBundle
                .getString("MainFrame.nameTextField.tooltip"));
        nameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.searchActionPerformed(e);
            }
        });

        locationTextField = new JTextField();
        locationTextField.setToolTipText(resourceBundle
                .getString("MainFrame.locationTextField.tooltip"));
        locationTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.searchActionPerformed(e);
            }
        });

        searchAction = new SearchAction(this);
        clearCriteriaAction = new ClearCriteriaAction(this);

        glassPane = new BlockingGlassPane();
        setGlassPane(glassPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(initialiseMenuBar());
        setMinimumSize(MINIMUM_SIZE);
        setPreferredSize(PREFERRED_SIZE);
        setTitle(resourceBundle.getString("MainFrame.title"));

        setLayout(new GridBagLayout());
        initialiseComponents();
    }

    private JTable createResultsTable() {
        /*
         * Extend JTable to override method that allow tooltips to be set on the
         * column headers.
         */
        return new JTable(tableModel, contractorTableColumnModel) {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {

                    @Override
                    public String getToolTipText(MouseEvent e) {
                        /*
                         * Get the mouse pointer location, find the column under
                         * it and lookup the tooltip in the model.
                         */
                        Point point = e.getPoint();
                        int columnIndex = columnModel
                                .getColumnIndexAtX(point.x);
                        int modelIndex = columnModel.getColumn(columnIndex)
                                .getModelIndex();
                        return ((ContractorTableColumnModel) columnModel)
                                .getColumnHeaderToolTipText(modelIndex);
                    }
                };
            }
        };
    }

    private void searchActionPerformed(ActionEvent e) {
        Component componentToFocus = null;
        if (e.getSource() instanceof Component) {
            componentToFocus = (Component) e.getSource();
        }
        presenter.searchActionPerformed(componentToFocus);
    }

    /** {@inheritDoc} */
    public void setPresenter(MainPresenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("presenter cannot be null");
        }
        this.presenter = presenter;
        contractorTableColumnModel.setPresenter(presenter);
    }

    /** {@inheritDoc} */
    public void realise() {
        pack();

        // Centre the frame on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        setVisible(true);
    }

    /** {@inheritDoc} */
    public String getNameCriteria() {
        return nameTextField.getText();
    }

    /** {@inheritDoc} */
    public String getLocationCriteria() {
        return locationTextField.getText();
    }

    /** {@inheritDoc} */
    public void setTableData(List<Contractor> contractors) {
        if (contractors == null) {
            throw new IllegalArgumentException("contractors cannot be null");
        }
        tableModel.replaceContractors(contractors);

        /*
         * Don't want to continue any editing operation now that the table data
         * has been updated.
         */
        resultsTable.removeEditor();
    }

    /** {@inheritDoc} */
    public void setStatusLabelText(String text) {
        statusLabel.setText(text);
    }

    /** {@inheritDoc} */
    public JFrame getFrame() {
        return this;
    }

    /** {@inheritDoc} */
    public void disableControls() {
        glassPane.setVisible(true);
        searchAction.setEnabled(false);
        contractorTableColumnModel.disableRendererBookButton();
    }

    /** {@inheritDoc} */
    public void enableControls(Component componentToFocus) {
        glassPane.setVisible(false);
        searchAction.setEnabled(true);
        contractorTableColumnModel.enableRendererBookButton();
        if (componentToFocus != null) {
            componentToFocus.requestFocus();
        }
    }

    /** {@inheritDoc} */
    public Contractor getContractorAtRow(int rowNo) {
        return tableModel.getContractorAtRow(rowNo);
    }

    /** {@inheritDoc} */
    public void updateContractorAtRow(int rowNo, Contractor contractor) {
        tableModel.updateContractorAtRow(rowNo, contractor);
    }

    private JMenuBar initialiseMenuBar() {
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setAction(new ExitAction(this));

        JMenu fileMenu = new JMenu();
        fileMenu.setText(resourceBundle.getString("MainFrame.fileMenu.text"));
        fileMenu.setMnemonic(resourceBundle.getString(
                "MainFrame.fileMenu.mnemonic").charAt(0));
        fileMenu.add(exitMenuItem);

        JMenuItem searchMenuItem = new JMenuItem(searchAction);
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                InputEvent.CTRL_DOWN_MASK));

        JMenuItem clearCriteriaMenuItem = new JMenuItem(clearCriteriaAction);
        clearCriteriaMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));

        JMenu editMenu = new JMenu();
        editMenu.setText(resourceBundle.getString("MainFrame.editMenu.text"));
        editMenu.setMnemonic(resourceBundle.getString(
                "MainFrame.editMenu.mnemonic").charAt(0));
        editMenu.add(searchMenuItem);
        editMenu.add(clearCriteriaMenuItem);

        JMenuItem helpContentsMenuItem = new JMenuItem();
        helpContentsMenuItem.setAction(new HelpContentsAction(this));
        helpContentsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F1, 0));

        JMenu helpMenu = new JMenu();
        helpMenu.setText(resourceBundle.getString("MainFrame.helpMenu.text"));
        helpMenu.setMnemonic(resourceBundle.getString(
                "MainFrame.helpMenu.mnemonic").charAt(0));
        helpMenu.add(helpContentsMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
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
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(initialiseStatusPanel(), constraints);
    }

    private JPanel initialiseSearchPanel() {
        JGradientPanel panel = new JGradientPanel(
                PresentationConstants.DARK_BLUE,
                PresentationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 6, 0, 4);
        panel.add(new JLabel(resourceBundle
                .getString("MainFrame.preambleLabel.text")), constraints);

        Insets criteriaLabelInsets = new Insets(0, 6, 0, 4);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = criteriaLabelInsets;
        panel.add(new JLabel(resourceBundle
                .getString("MainFrame.nameLabel.text")), constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = criteriaLabelInsets;
        panel.add(new JLabel(resourceBundle
                .getString("MainFrame.locationLabel.text")), constraints);

        Insets insetsZeroTop = new Insets(0, 4, 15, 4);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        constraints.weightx = 1;
        nameTextField.setPreferredSize(TEXT_FIELD_PREFERRED_SIZE);
        panel.add(nameTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        constraints.weightx = 1;
        locationTextField.setPreferredSize(TEXT_FIELD_PREFERRED_SIZE);
        panel.add(locationTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        JButton searchButton = new JButton(searchAction);
        searchButton.setToolTipText(resourceBundle
                .getString("MainFrame.searchButton.tooltip"));
        panel.add(searchButton, constraints);

        return panel;
    }

    private Component initialiseResultsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PresentationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport()
                .setBackground(PresentationConstants.LIGHT_BLUE);
        scrollPane.setViewportView(resultsTable);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(scrollPane, constraints);

        return panel;
    }

    private JPanel initialiseStatusPanel() {
        JGradientPanel panel = new JGradientPanel(
                PresentationConstants.LIGHT_BLUE,
                PresentationConstants.DARK_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 4, 2, 4);
        constraints.weightx = 1;
        panel.add(statusLabel, constraints);

        return panel;
    }

    private void clearCriteriaActionPerformed() {
        nameTextField.setText(null);
        locationTextField.setText(null);
    }

    // Performs the search action
    private static final class SearchAction extends AbstractAction {

        private final MainFrame mainFrame;

        public SearchAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.searchAction.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, Integer
                    .valueOf(this.mainFrame.resourceBundle.getString(
                            "MainFrame.searchAction.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            mainFrame.searchActionPerformed(e);
        }
    }

    // Clears the search criteria text fields
    private static final class ClearCriteriaAction extends AbstractAction {

        private final MainFrame mainFrame;

        public ClearCriteriaAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.clearCriteriaAction.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY,
                    Integer
                            .valueOf(this.mainFrame.resourceBundle.getString(
                                    "MainFrame.clearCriteriaAction.mnemonic")
                                    .charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            mainFrame.clearCriteriaActionPerformed();
        }
    }

    // Displays the help contents frame
    private static final class HelpContentsAction extends AbstractAction {

        private final MainFrame mainFrame;

        public HelpContentsAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsAction.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, Integer
                    .valueOf(this.mainFrame.resourceBundle.getString(
                            "MainFrame.helpContentsAction.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent evt) {
            URL helpContentsUrl = ClassLoader
                    .getSystemResource(USER_GUIDE_PATH);

            if (helpContentsUrl == null) {
                LOGGER.warning("Help contents resource not found: "
                        + helpContentsUrl);
                showErrorDialog();
            } else {
                try {
                    // Display the help HTML page in a new frame
                    JEditorPane editorPane = new JEditorPane(helpContentsUrl);
                    JFrame frame = new JFrame(mainFrame.resourceBundle
                            .getString("MainFrame.helpContents.title"));
                    frame.add(editorPane);
                    frame.pack();
                    frame.setLocationRelativeTo(mainFrame);
                    frame.setVisible(true);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING,
                            "Error creating help contents editor pane for URL: "
                                    + helpContentsUrl, e);
                    showErrorDialog();
                }
            }
        }

        private void showErrorDialog() {
            String message = mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsErrorDialog.message");
            String title = mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsErrorDialog.title");
            JOptionPane.showMessageDialog(mainFrame, message, title,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Exits the application
    private static final class ExitAction extends AbstractAction {

        private final MainFrame mainFrame;

        public ExitAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.exitMenuItem.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, Integer
                    .valueOf(this.mainFrame.resourceBundle.getString(
                            "MainFrame.exitMenuItem.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            LOGGER.info("Exiting application from menu bar option");
            System.exit(0);
        }
    }
}
