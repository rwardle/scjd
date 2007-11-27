/*
 * MainFrame.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
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

import suncertify.ApplicationConstants;
import suncertify.service.Contractor;

/**
 * 
 * @author Richard Wardle
 */
public final class MainFrame extends JFrame implements MainView {

    // TODO Check on small screen resultions 640x480

    private static final String INITIAL_STATUS_LABEL = " ";
    // TODO Adjust these sizes
    private static final Dimension PREFERRED_SIZE = new Dimension(640, 480);
    // TODO Minimum size has no effect on Mac
    private static final Dimension MINIMUM_SIZE = new Dimension(320, 240);
    private static final Dimension TEXT_FIELD_PREFERRED_SIZE = new Dimension(
            100, 25);
    private static final int TABLE_ROW_HEIGHT = 18;
    private static final String USER_GUIDE_PATH = "suncertify/presentation/help/userguide.html";

    private final ResourceBundle resourceBundle;
    private final JLabel statusLabel;
    private final ContractorTableColumnModel contractorTableColumnModel;
    private final JTable resultsTable;
    private final JTextField nameTextField;
    private final JTextField locationTextField;
    private final SearchAction searchAction;
    private final ClearCriteriaAction clearCriteriaAction;
    private final Component glassPane;
    private MainPresenter presenter;
    private ContractorTableModel tableModel;

    public MainFrame() {
        this.resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        this.statusLabel = new JLabel(MainFrame.INITIAL_STATUS_LABEL);

        // Display an empty table initially
        this.tableModel = new ContractorTableModel(new ArrayList<Contractor>());
        this.contractorTableColumnModel = new ContractorTableColumnModel();
        this.resultsTable = createResultsTable();
        this.resultsTable.setRowHeight(MainFrame.TABLE_ROW_HEIGHT);
        this.resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.resultsTable.setSurrendersFocusOnKeystroke(true);

        this.nameTextField = new JTextField();
        this.nameTextField.setToolTipText(this.resourceBundle
                .getString("MainFrame.nameField.tooltip"));
        this.nameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.searchActionPerformed(e);
            }
        });

        this.locationTextField = new JTextField();
        this.locationTextField.setToolTipText(this.resourceBundle
                .getString("MainFrame.locationField.tooltip"));
        this.locationTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.searchActionPerformed(e);
            }
        });

        this.searchAction = new SearchAction(this);
        this.clearCriteriaAction = new ClearCriteriaAction(this);

        this.glassPane = new GlassPane();
        setGlassPane(this.glassPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(initialiseMenuBar());
        setMinimumSize(MainFrame.MINIMUM_SIZE);
        setPreferredSize(MainFrame.PREFERRED_SIZE);
        setTitle(this.resourceBundle.getString("MainFrame.title.text"));
        setLayout(new GridBagLayout());
        initialiseComponents();
    }

    private JTable createResultsTable() {
        return new JTable(this.tableModel, this.contractorTableColumnModel) {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(this.columnModel) {

                    @Override
                    public String getToolTipText(MouseEvent e) {
                        Point point = e.getPoint();
                        int columnIndex = this.columnModel
                                .getColumnIndexAtX(point.x);
                        int modelIndex = this.columnModel
                                .getColumn(columnIndex).getModelIndex();
                        return ((ContractorTableColumnModel) this.columnModel)
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
        this.presenter.searchActionPerformed(componentToFocus);
    }

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
        this.contractorTableColumnModel.setPresenter(presenter);
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
        this.tableModel = tableModel;
        /*
         * Don't want an edit from the previous table model to continue once the
         * new one has been set
         */
        this.resultsTable.removeEditor();
        this.resultsTable.setModel(this.tableModel);
    }

    public void setStatusLabelText(String text) {
        this.statusLabel.setText(text);
    }

    public JFrame getFrame() {
        return this;
    }

    public void disableControls() {
        this.glassPane.setVisible(true);
        this.searchAction.setEnabled(false);
        this.contractorTableColumnModel.disableRendererBookButton();
    }

    public void enableControls(Component componentToFocus) {
        this.glassPane.setVisible(false);
        this.searchAction.setEnabled(true);
        this.contractorTableColumnModel.enableRendererBookButton();
        this.resultsTable.requestFocus();
        if (componentToFocus != null) {
            componentToFocus.requestFocus();
        }
    }

    public Contractor getContractorAtRow(int rowNo) {
        return this.tableModel.getContractorAtRow(rowNo);
    }

    public void updateContractorAtRow(int rowNo, Contractor contractor) {
        this.tableModel.updateContractorAtRow(rowNo, contractor);
    }

    private JMenuBar initialiseMenuBar() {
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setAction(new ExitAction(this));

        JMenu fileMenu = new JMenu();
        fileMenu.setText(this.resourceBundle
                .getString("MainFrame.fileMenu.text"));
        fileMenu.setMnemonic(this.resourceBundle.getString(
                "MainFrame.fileMenu.mnemonic").charAt(0));
        fileMenu.add(exitMenuItem);

        JMenuItem searchMenuItem = new JMenuItem(this.searchAction);
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                InputEvent.CTRL_DOWN_MASK));

        JMenuItem clearCriteriaMenuItem = new JMenuItem(
                this.clearCriteriaAction);
        clearCriteriaMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));

        JMenu editMenu = new JMenu();
        editMenu.setText(this.resourceBundle
                .getString("MainFrame.editMenu.text"));
        editMenu.setMnemonic(this.resourceBundle.getString(
                "MainFrame.editMenu.mnemonic").charAt(0));
        editMenu.add(searchMenuItem);
        editMenu.add(clearCriteriaMenuItem);

        JMenuItem helpContentsMenuItem = new JMenuItem();
        helpContentsMenuItem.setAction(new HelpContentsAction(this));
        helpContentsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F1, 0));

        JMenu helpMenu = new JMenu();
        helpMenu.setText(this.resourceBundle
                .getString("MainFrame.helpMenu.text"));
        helpMenu.setMnemonic(this.resourceBundle.getString(
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
                ApplicationConstants.DARK_BLUE, ApplicationConstants.LIGHT_BLUE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
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
        this.nameTextField
                .setPreferredSize(MainFrame.TEXT_FIELD_PREFERRED_SIZE);
        panel.add(this.nameTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        constraints.weightx = 1;
        this.locationTextField
                .setPreferredSize(MainFrame.TEXT_FIELD_PREFERRED_SIZE);
        panel.add(this.locationTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.insets = insetsZeroTop;
        JButton searchButton = new JButton(this.searchAction);
        searchButton.setOpaque(false); // Mac VM bug?
        searchButton.setToolTipText(this.resourceBundle
                .getString("MainFrame.searchButton.tooltip"));
        panel.add(searchButton, constraints);

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

    private void clearCriteriaActionPerformed() {
        this.nameTextField.setText(null);
        this.locationTextField.setText(null);
    }

    private static final class SearchAction extends AbstractAction {

        private final MainFrame mainFrame;

        public SearchAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.searchAction.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, new Integer(
                    this.mainFrame.resourceBundle.getString(
                            "MainFrame.searchAction.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            this.mainFrame.searchActionPerformed(e);
        }
    }

    private static final class ClearCriteriaAction extends AbstractAction {

        private final MainFrame mainFrame;

        public ClearCriteriaAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.clearCriteriaAction.text"));
            this.mainFrame = mainFrame;
            putValue(
                    Action.MNEMONIC_KEY,
                    new Integer(this.mainFrame.resourceBundle.getString(
                            "MainFrame.clearCriteriaAction.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            this.mainFrame.clearCriteriaActionPerformed();
        }
    }

    private static final class HelpContentsAction extends AbstractAction {

        private final MainFrame mainFrame;

        public HelpContentsAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsAction.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, new Integer(
                    this.mainFrame.resourceBundle.getString(
                            "MainFrame.helpContentsAction.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent evt) {
            URL helpContentsUrl = ClassLoader
                    .getSystemResource(MainFrame.USER_GUIDE_PATH);
            if (helpContentsUrl == null) {
                showErrorDialog();
            } else {
                try {
                    JEditorPane editorPane = new JEditorPane(helpContentsUrl);
                    JFrame frame = new JFrame(this.mainFrame.resourceBundle
                            .getString("MainFrame.helpContents.title"));
                    frame.add(editorPane);
                    frame.pack();
                    frame.setLocationRelativeTo(this.mainFrame);
                    frame.setVisible(true);
                } catch (IOException e) {
                    showErrorDialog();
                }
            }
        }

        private void showErrorDialog() {
            String message = this.mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsErrorDialog.message");
            String title = this.mainFrame.resourceBundle
                    .getString("MainFrame.helpContentsErrorDialog.title");
            JOptionPane.showMessageDialog(this.mainFrame, message, title,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class ExitAction extends AbstractAction {

        private final MainFrame mainFrame;

        public ExitAction(MainFrame mainFrame) {
            super(mainFrame.resourceBundle
                    .getString("MainFrame.exitMenuItem.text"));
            this.mainFrame = mainFrame;
            putValue(Action.MNEMONIC_KEY, new Integer(
                    this.mainFrame.resourceBundle.getString(
                            "MainFrame.exitMenuItem.mnemonic").charAt(0)));
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private static final class GlassPane extends JComponent {

        public GlassPane() {
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

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);

            // Get the focus if we're showing the glasspane
            if (visible) {
                requestFocus();
            }
        }
    }
}
