package gui; // Package declaration: Groups this class with other GUI classes

import dao.InventoryDAO; // Import: InventoryDAO for database CRUD operations
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for button clicks
import java.awt.event.KeyEvent; // Import: KeyEvent for keyboard input events
import java.awt.event.KeyListener; // Import: KeyListener interface for keyboard events
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import java.io.File; // Import: File class for file operations
import java.util.List; // Import: List interface for collections
import javax.swing.*; // Import: Swing components (JFrame, JTable, JButton, JMenuBar, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import javax.swing.table.DefaultTableModel; // Import: DefaultTableModel for table data management
import javax.swing.table.TableRowSorter; // Import: TableRowSorter for live search/filtering
import model.FarmItem; // Import: RowFilter for table row filtering
import model.HarvestLot; // Import: KeyStroke for menu keyboard shortcuts
import model.User; // Import: FarmItem base class
import util.CSVExporter; // Import: HarvestLot class for status operations
import util.FileImporter; // Import: User model class

/**
 * RecordsWindow - Main Inventory Records Management Window
 * WHAT: Displays all inventory records in a JTable with CRUD operations, search, and role-based features
 * WHY: Central window for viewing and managing inventory - supports all user roles with different permissions
 * HOW: Uses JTable with DefaultTableModel, TableRowSorter for live search, implements ActionListener and KeyListener
 * 
 * OOP CONCEPTS:
 * - Event-Driven Programming: Implements ActionListener and KeyListener for user interactions
 * - Polymorphism: Handles FarmItem objects, uses instanceof to check HarvestLot type
 * - Role-Based Access Control: Shows/hides buttons based on user role
 */
public class RecordsWindow extends JFrame implements ActionListener, KeyListener {
    // WHAT: Currently logged-in user object (final - cannot be reassigned)
    // WHY: Needed for role-based button visibility and passing to other windows
    // HOW: Stored as final instance variable, passed from constructor
    private final User currentUser;
    
    // WHAT: JTable component for displaying inventory records
    // WHY: Provides tabular view of all inventory items with sorting and filtering
    // HOW: JTable displays data from DefaultTableModel
    private JTable recordsTable;
    
    // WHAT: Table model that holds the data for JTable
    // WHY: JTable needs a TableModel to manage data (rows and columns)
    // HOW: DefaultTableModel stores data as Object[][] internally
    private DefaultTableModel tableModel;
    
    // WHAT: Row sorter for live search/filtering functionality
    // WHY: Allows filtering table rows as user types in search field
    // HOW: TableRowSorter filters rows based on RowFilter criteria
    private TableRowSorter<DefaultTableModel> sorter;
    
    // WHAT: Text field for entering search query
    // WHY: Users need field to search/filter records
    // HOW: JTextField with KeyListener for live search
    private JTextField searchField;
    
    // WHAT: Button to refresh/reload records from database
    // WHY: Updates table after database changes (add, edit, delete)
    // HOW: Calls loadRecords() method when clicked
    private JButton refreshButton;
    
    // WHAT: Button to add new harvest record
    // WHY: Sellers/admins need button to create new records
    // HOW: Opens HarvestForm dialog when clicked
    private JButton addButton;
    
    // WHAT: Button to edit selected record
    // WHY: Sellers/admins need button to modify existing records
    // HOW: Opens HarvestForm dialog with existing data when clicked
    private JButton editButton;
    
    // WHAT: Button to delete selected record
    // WHY: Sellers/admins need button to remove records
    // HOW: Shows confirmation, deletes record via DAO when confirmed
    private JButton deleteButton;
    
    // WHAT: Button to export records to CSV file
    // WHY: Users may want to backup data or use in spreadsheet programs
    // HOW: Opens file chooser, calls CSVExporter.exportToCSV()
    private JButton exportButton;
    
    // WHAT: Button to import records from CSV file
    // WHY: Admins may want to bulk import data from CSV
    // HOW: Opens file chooser, calls FileImporter.importFromCSV()
    private JButton importButton;
    
    // WHAT: Button to close window and return to previous window
    // WHY: Users need way to navigate back
    // HOW: Shows confirmation, closes window when confirmed
    private JButton backButton;
    
    // WHAT: Button for buyers to mark product as interested
    // WHY: Buyers can express interest in products
    // HOW: Updates status to "Interested" when clicked
    private JButton interestedButton;
    
    // WHAT: Button for buyers to purchase product
    // WHY: Buyers can initiate purchase process
    // HOW: Updates status to "Interested" (admin marks as sold out later)
    private JButton buyButton;
    
    // WHAT: Button for admins to mark product as sold out
    // WHY: Admins need to update product availability status
    // HOW: Updates status to "Sold Out" when clicked
    private JButton markSoldOutButton;
    
    // WHAT: InventoryDAO object for database operations (final - cannot be reassigned)
    // WHY: All database operations go through this DAO instance
    // HOW: Created once in constructor, used throughout class
    private final InventoryDAO inventoryDAO;
    
    /**
     * Constructor - Creates and displays the RecordsWindow
     * WHAT: Initializes components, sets up layout, loads records from database, configures window
     * WHY: Called to display inventory records management interface
     * HOW: Calls helper methods to set up GUI, loads data, then displays window
     * @param user Currently logged-in user object
     */
    public RecordsWindow(User user) {
        // WHAT: Store user object in instance variable
        // WHY: Needed for role-based features and displaying user info
        // HOW: this.currentUser = user assigns parameter
        this.currentUser = user;
        
        // WHAT: Create InventoryDAO instance for database operations
        // WHY: Need DAO to perform CRUD operations on inventory
        // HOW: new InventoryDAO() creates instance
        this.inventoryDAO = new InventoryDAO();
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Create and set menu bar
        // WHY: JMenuBar is a recommended Swing component requirement
        // HOW: Calls createMenuBar() method to create menu structure
        setJMenuBar(createMenuBar());
        
        // WHAT: Arrange components in window
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Load records from database and display in table
        // WHY: Table should show existing records when window opens
        // HOW: Calls loadRecords() method which queries database and populates table
        loadRecords();
        
        // WHAT: Prevent default close behavior
        // WHY: Want to show confirmation dialog before closing
        // HOW: DO_NOTHING_ON_CLOSE prevents automatic closing
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // WHAT: Set window title bar text
        // WHY: Identifies window in taskbar
        // HOW: setTitle() sets title text
        setTitle("AgriTrack - Inventory Records");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 1100x700 pixels
        // WHY: Provides adequate space for table and buttons
        // HOW: setSize() sets width and height
        setSize(1100, 700);
        
        // WHAT: Center window on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(null) centers window
        setLocationRelativeTo(null);
        
        // WHAT: Add window close listener for confirmation dialog
        // WHY: Prevents accidental window closure
        // HOW: WindowAdapter overrides windowClosing() method
        addWindowListener(new WindowAdapter() {
            @Override
            // WHAT: Called when user clicks window close button (X)
            // WHY: Intercepts close event to show confirmation
            // HOW: Override windowClosing() to call confirmClose()
            public void windowClosing(WindowEvent e) {
                confirmClose(); // Show confirmation dialog
            }
        });
        
        // WHAT: Make window visible
        // WHY: Window is hidden by default
        // HOW: setVisible(true) displays window
        setVisible(true);
    }
    
    /**
     * confirmClose() - Shows confirmation dialog before closing window
     * WHAT: Displays Yes/No dialog asking user to confirm close
     * WHY: Prevents accidental window closure
     * HOW: Uses JOptionPane to show dialog, closes only if user clicks Yes
     */
    private void confirmClose() {
        // WHAT: Show confirmation dialog
        // WHY: Gives user chance to cancel close
        // HOW: showConfirmDialog() displays modal dialog
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to close this window?", // Message text
            "Confirm Close", // Dialog title
            JOptionPane.YES_NO_OPTION, // Show Yes and No buttons
            JOptionPane.QUESTION_MESSAGE); // Question icon
        
        // WHAT: Check if user clicked Yes
        // WHY: Only close if user confirms
        // HOW: YES_OPTION constant returned when Yes clicked
        if (confirm == JOptionPane.YES_OPTION) {
            // WHAT: Close and destroy window
            // WHY: User confirmed they want to close
            // HOW: dispose() closes window and releases resources
            dispose();
        }
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates table, search field, buttons, and configures their properties
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components, sets properties, attaches event listeners, configures role-based visibility
     */
    private void initializeComponents() {
        // --- Table Setup ---
        // WHAT: Array of column names for table
        // WHY: Defines table structure and column headers
        // HOW: String array with 9 column names (added Price column)
        String[] columnNames = {"ID", "Name", "Quantity", "Unit", "Price", "Type", "Date Added", "Status", "Notes"};
        
        // WHAT: Create table model with column names and 0 initial rows
        // WHY: Table model manages table data structure
        // HOW: DefaultTableModel constructor takes column names and row count
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            // WHAT: Override method to make all cells non-editable
            // WHY: Prevents direct cell editing - users must use Edit button
            // HOW: Always return false to disable cell editing
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are read-only
            }
        };
        
        // WHAT: Create JTable with table model
        // WHY: JTable displays data in tabular format
        // HOW: JTable constructor takes TableModel
        recordsTable = new JTable(tableModel);
        
        // WHAT: Set selection mode to single row only
        // WHY: Edit/delete operations work on one record at a time
        // HOW: SINGLE_SELECTION allows only one row to be selected
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // WHAT: Set row height to 25 pixels
        // WHY: Consistent row height improves readability
        // HOW: setRowHeight() sets height in pixels
        recordsTable.setRowHeight(25);
        
        // WHAT: Set table font to Segoe UI, plain, size 13
        // WHY: Consistent typography
        // HOW: setFont() applies font to table cells
        recordsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Set table header font to Segoe UI, bold, size 13
        // WHY: Bold header makes column names stand out
        // HOW: getTableHeader() gets header, setFont() applies font
        recordsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // WHAT: Set table header background to green
        // WHY: Matches application theme
        // HOW: setBackground() applies color to header
        recordsTable.getTableHeader().setBackground(new Color(46, 125, 50));
        
        // WHAT: Set table header text color to white
        // WHY: White contrasts with green background
        // HOW: setForeground() sets text color
        recordsTable.getTableHeader().setForeground(Color.WHITE);
        
        // WHAT: Set custom renderer for status column (column index 6)
        // WHY: Status column needs color-coding and icons for visual feedback
        // HOW: setCellRenderer() applies custom StatusCellRenderer to specific column
        recordsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        // --- Column Width Configuration ---
        // WHAT: Set preferred width for each column
        // WHY: Columns should have appropriate widths for content
        // HOW: getColumnModel().getColumn(index).setPreferredWidth() sets width
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID column - narrow
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Name column - medium
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Quantity column - medium
        recordsTable.getColumnModel().getColumn(3).setPreferredWidth(70); // Unit column - narrow
        recordsTable.getColumnModel().getColumn(4).setPreferredWidth(90); // Type column - narrow
        recordsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Date column - medium
        recordsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status column - medium
        recordsTable.getColumnModel().getColumn(7).setPreferredWidth(250); // Notes column - wide
        
        // --- Live Search Setup ---
        // WHAT: Create TableRowSorter for table filtering
        // WHY: Enables live search functionality - filters rows as user types
        // HOW: TableRowSorter constructor takes TableModel
        sorter = new TableRowSorter<>(tableModel);
        
        // WHAT: Attach sorter to table
        // WHY: Table needs sorter to enable filtering
        // HOW: setRowSorter() connects sorter to table
        recordsTable.setRowSorter(sorter);
        
        // --- Search Field Setup ---
        // WHAT: Create search text field (30 characters wide)
        // WHY: Users need field to enter search query
        // HOW: JTextField constructor with column width
        searchField = new JTextField(30);
        
        // WHAT: Set search field font
        // WHY: Consistent typography
        // HOW: setFont() applies font
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create compound border for search field (line border + padding)
        // WHY: Visual styling - border and padding improve appearance
        // HOW: createCompoundBorder() combines two borders
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), // Outer border - gray line
            BorderFactory.createEmptyBorder(8, 12, 8, 12) // Inner border - padding
        ));
        
        // WHAT: Attach key listener to search field
        // WHY: Need to detect typing for live search
        // HOW: addKeyListener() registers this class (implements KeyListener) to receive events
        searchField.addKeyListener(this);
        
        // --- Button Creation ---
        // WHAT: Create all action buttons with text and emojis
        // WHY: Users need buttons to perform various operations
        // HOW: JButton constructor takes button text
        refreshButton = new JButton("Refresh");
        addButton = new JButton("Add Record");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        exportButton = new JButton("Export CSV");
        importButton = new JButton("Import CSV");
        backButton = new JButton("Back");
        interestedButton = new JButton("Mark as Interested");
        buyButton = new JButton("Buy Product");
        markSoldOutButton = new JButton("Mark as Sold Out");
        
        // WHAT: Style all buttons with different colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(refreshButton, new Color(158, 158, 158)); // Gray
        styleButton(addButton, new Color(76, 175, 80)); // Green
        styleButton(editButton, new Color(33, 150, 243)); // Blue
        styleButton(deleteButton, new Color(244, 67, 54)); // Red
        styleButton(exportButton, new Color(255, 152, 0)); // Orange
        styleButton(importButton, new Color(156, 39, 176)); // Purple
        styleButton(backButton, new Color(158, 158, 158)); // Gray
        styleButton(interestedButton, new Color(255, 193, 7)); // Yellow
        styleButton(buyButton, new Color(76, 175, 80)); // Green
        styleButton(markSoldOutButton, new Color(244, 67, 54)); // Red
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        refreshButton.addActionListener(this);
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        exportButton.addActionListener(this);
        importButton.addActionListener(this);
        backButton.addActionListener(this);
        interestedButton.addActionListener(this);
        buyButton.addActionListener(this);
        markSoldOutButton.addActionListener(this);
        
        // --- Role-Based Button Visibility ---
        // WHAT: Hide buyer-specific buttons for SELLER and ADMIN roles
        // WHY: Sellers and admins don't need "interested" or "buy" buttons
        // HOW: if statement checks role, setVisible(false) hides buttons
        if ("SELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            // SELLER and ADMIN can add/edit/delete
            interestedButton.setVisible(false); // Hide interested button
            buyButton.setVisible(false); // Hide buy button
        }
        
        // WHAT: Hide seller/admin-specific buttons for BUYER role
        // WHY: Buyers can only view and interact, not modify records
        // HOW: if statement checks role, setVisible(false) hides buttons
        if ("BUYER".equals(currentUser.getRole())) {
            // BUYER can only view, export, and interact with products
            addButton.setVisible(false); // Hide add button
            editButton.setVisible(false); // Hide edit button
            deleteButton.setVisible(false); // Hide delete button
            importButton.setVisible(false); // Hide import button
            markSoldOutButton.setVisible(false); // Hide mark sold out button
        }
        
        // WHAT: Hide mark sold out button for non-admin roles
        // WHY: Only admins can mark products as sold out
        // HOW: if statement checks if NOT admin, setVisible(false) hides button
        if (!"ADMIN".equals(currentUser.getRole())) {
            // Only ADMIN can mark as sold out
            markSoldOutButton.setVisible(false); // Hide mark sold out button
        }
    }
    
    /**
     * styleButton() - Applies consistent styling to buttons
     * WHAT: Sets button colors, fonts, sizes, and cursor
     * WHY: Consistent button appearance throughout application
     * HOW: Calls setter methods on JButton
     * @param button Button to style
     * @param bgColor Background color for button
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor); // Set background color
        button.setForeground(Color.WHITE); // Set text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Set font
        button.setFocusPainted(false); // Remove focus border
        button.setBorderPainted(false); // Remove button border
        button.setPreferredSize(new Dimension(120, 35)); // Set button size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
    }
    
    /**
     * createMenuBar() - Creates menu bar with File, Edit, and View menus
     * WHAT: Creates JMenuBar with menu items for common operations
     * WHY: JMenuBar is a recommended Swing component - provides alternative navigation
     * HOW: Creates JMenuBar, adds JMenu items, attaches ActionListeners
     * @return JMenuBar with File, Edit, and View menus
     */
    private JMenuBar createMenuBar() {
        // WHAT: Create menu bar
        // WHY: Menu bar provides alternative way to access features
        // HOW: JMenuBar constructor
        JMenuBar menuBar = new JMenuBar();
        
        // WHAT: Create File menu
        // WHY: File menu typically contains file operations
        // HOW: JMenu constructor with menu name
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create Export menu item
        // WHY: Users can export via menu
        // HOW: JMenuItem constructor with text and mnemonic
        JMenuItem exportItem = new JMenuItem("Export to CSV", KeyEvent.VK_E);
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        exportItem.addActionListener(e -> exportToCSV());
        fileMenu.add(exportItem);
        
        // WHAT: Add separator
        // WHY: Separates export from import
        // HOW: addSeparator() adds visual separator
        fileMenu.addSeparator();
        
        // WHAT: Create Import menu item (admin only)
        // WHY: Admins can import via menu
        // HOW: JMenuItem with conditional visibility
        JMenuItem importItem = new JMenuItem("Import from CSV", KeyEvent.VK_I);
        importItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        importItem.addActionListener(e -> {
            FileImporter.importFromCSV(this);
            loadRecords();
        });
        // WHAT: Only show import for admin
        // WHY: Import is admin-only feature
        // HOW: Check user role, add if admin
        if ("ADMIN".equals(currentUser.getRole())) {
            fileMenu.add(importItem);
        }
        
        // WHAT: Add separator
        // WHY: Separates file operations from exit
        // HOW: addSeparator()
        fileMenu.addSeparator();
        
        // WHAT: Create Exit menu item
        // WHY: Users can exit via menu
        // HOW: JMenuItem with action listener
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> confirmClose());
        fileMenu.add(exitItem);
        
        // WHAT: Create Edit menu
        // WHY: Edit menu contains record modification operations
        // HOW: JMenu constructor
        JMenu editMenu = new JMenu("Edit");
        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create Add Record menu item (seller/admin only)
        // WHY: Sellers and admins can add records via menu
        // HOW: JMenuItem with conditional visibility
        if ("SELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            JMenuItem addItem = new JMenuItem("Add Record", KeyEvent.VK_A);
            addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            addItem.addActionListener(e -> {
                new HarvestForm(currentUser, this);
                loadRecords();
            });
            editMenu.add(addItem);
            
            // WHAT: Create Edit Record menu item
            // WHY: Users can edit via menu
            // HOW: JMenuItem with action listener
            JMenuItem editItem = new JMenuItem("Edit Selected", KeyEvent.VK_E);
            editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            editItem.addActionListener(e -> editSelectedRecord());
            editMenu.add(editItem);
            
            // WHAT: Create Delete Record menu item
            // WHY: Users can delete via menu
            // HOW: JMenuItem with action listener
            JMenuItem deleteItem = new JMenuItem("Delete Selected", KeyEvent.VK_D);
            deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
            deleteItem.addActionListener(e -> deleteSelectedRecord());
            editMenu.add(deleteItem);
        }
        
        // WHAT: Create View menu
        // WHY: View menu contains display and refresh operations
        // HOW: JMenu constructor
        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create Refresh menu item
        // WHY: Users can refresh via menu
        // HOW: JMenuItem with action listener
        JMenuItem refreshItem = new JMenuItem("Refresh", KeyEvent.VK_R);
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        refreshItem.addActionListener(e -> {
            searchField.setText("");
            loadRecords();
        });
        viewMenu.add(refreshItem);
        
        // WHAT: Create Reports menu item
        // WHY: Users can open reports via menu
        // HOW: JMenuItem with action listener
        JMenuItem reportsItem = new JMenuItem("Reports & Statistics", KeyEvent.VK_S);
        reportsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        reportsItem.addActionListener(e -> new ReportsWindow(currentUser));
        viewMenu.add(reportsItem);
        
        // WHAT: Add all menus to menu bar
        // WHY: Menu bar needs menus to display
        // HOW: add() method adds menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        // WHAT: Return completed menu bar
        // WHY: Caller needs menu bar to set on frame
        // HOW: return statement
        return menuBar;
    }
    
    /**
     * setupLayout() - Arranges components in window
     * WHAT: Creates layout structure with header, search panel, table, and action buttons
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure, FlowLayout for button panels
     */
    private void setupLayout() {
        // WHAT: Set main window layout to BorderLayout
        // WHY: BorderLayout divides window into regions
        // HOW: setLayout() sets layout manager
        setLayout(new BorderLayout());
        
        // WHAT: Set window background color
        // WHY: Subtle background makes content stand out
        // HOW: getContentPane().setBackground() sets color
        getContentPane().setBackground(new Color(245, 245, 250));
        
        // WHAT: Create and add header panel
        // WHY: Header provides branding and user info
        // HOW: createHeaderPanel() returns JPanel, add to NORTH
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create top panel for search and status legend
        // WHY: Search and legend should be at top, below header
        // HOW: JPanel with BorderLayout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 250));
        
        // WHAT: Create search panel with search field and refresh button
        // WHY: Users need search functionality
        // HOW: JPanel with FlowLayout, left-aligned
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBackground(new Color(245, 245, 250));
        searchPanel.setBorder(new EmptyBorder(15, 20, 10, 20));
        
        // WHAT: Create search label
        // WHY: Identifies the search field
        // HOW: JLabel with text and emoji
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField); // Add search field
        searchPanel.add(refreshButton); // Add refresh button
        
        // WHAT: Create status legend panel (only for buyers and admins)
        // WHY: Buyers and admins need to understand status color-coding
        // HOW: if statement checks role, creates legend if buyer or admin
        if ("BUYER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            // WHAT: Create legend panel with status examples
            // WHY: Helps users understand status color meanings
            // HOW: JPanel with FlowLayout, right-aligned
            JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            legendPanel.setBackground(new Color(245, 245, 250));
            legendPanel.setBorder(new EmptyBorder(5, 20, 10, 20));
            
            // WHAT: Create legend title label
            // WHY: Identifies the legend section
            // HOW: JLabel with text
            JLabel legendTitle = new JLabel("Status:");
            legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            legendPanel.add(legendTitle);
            
            // WHAT: Create "Available" status label with green background
            // WHY: Shows what green status means
            // HOW: JLabel with background color, opaque, padding
            JLabel availableLabel = new JLabel("Available");
            availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            availableLabel.setBackground(new Color(232, 245, 233)); // Light green
            availableLabel.setOpaque(true); // Enable background color
            availableLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8)); // Padding
            legendPanel.add(availableLabel);
            
            // WHAT: Create "Interested" status label with yellow background
            // WHY: Shows what yellow status means
            // HOW: JLabel with background color, opaque, padding
            JLabel interestedLabel = new JLabel("Interested");
            interestedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            interestedLabel.setBackground(new Color(255, 248, 225)); // Light yellow
            interestedLabel.setOpaque(true);
            interestedLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            legendPanel.add(interestedLabel);
            
            // WHAT: Create "Sold Out" status label with red background
            // WHY: Shows what red status means
            // HOW: JLabel with background color, opaque, padding
            JLabel soldOutLabel = new JLabel("Sold Out");
            soldOutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            soldOutLabel.setBackground(new Color(255, 235, 238)); // Light red
            soldOutLabel.setOpaque(true);
            soldOutLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            legendPanel.add(soldOutLabel);
            
            // WHAT: Add legend panel to bottom of top panel
            // WHY: Legend should appear below search field
            // HOW: BorderLayout.SOUTH positions at bottom
            topPanel.add(legendPanel, BorderLayout.SOUTH);
        }
        
        // WHAT: Add search panel to center of top panel
        // WHY: Search should be main element in top panel
        // HOW: BorderLayout.CENTER positions in center
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // WHAT: Add top panel to window (below header)
        // WHY: Search and legend should be at top
        // HOW: BorderLayout.NORTH positions at top (but after header in layout hierarchy)
        add(topPanel, BorderLayout.NORTH);
        
        // WHAT: Create scroll pane for table
        // WHY: Table may have many rows, needs scrolling capability
        // HOW: JScrollPane wraps JTable, provides scrollbars
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        
        // WHAT: Set scroll pane border
        // WHY: Visual separation from other components
        // HOW: createLineBorder() creates border, setBorder() applies it
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setBackground(Color.WHITE);
        
        // WHAT: Add scroll pane to center of window
        // WHY: Table is main content, should take most space
        // HOW: BorderLayout.CENTER positions in center, expands to fill space
        add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Create south panel for action buttons
        // WHY: Buttons should appear at bottom
        // HOW: JPanel with FlowLayout, left-aligned
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        southPanel.setBackground(new Color(245, 245, 250));
        southPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // WHAT: Add seller/admin buttons (add, edit, delete)
        // WHY: Only sellers and admins can modify records
        // HOW: if statement checks role, adds buttons if seller or admin
        if ("SELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            southPanel.add(addButton);
            southPanel.add(editButton);
            southPanel.add(deleteButton);
        }
        
        // WHAT: Add buyer/admin buttons (export, import for admin only)
        // WHY: Buyers and admins can export, only admins can import
        // HOW: if statement checks role, adds buttons accordingly
        if ("BUYER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            southPanel.add(exportButton);
            // WHAT: Add import button only for admin
            // WHY: Only admins can import data
            // HOW: Nested if statement checks if admin
            if ("ADMIN".equals(currentUser.getRole())) {
                southPanel.add(importButton);
            }
        }
        
        // WHAT: Add buyer-specific buttons (interested, buy)
        // WHY: Only buyers can express interest or purchase
        // HOW: if statement checks if buyer, adds buttons
        if ("BUYER".equals(currentUser.getRole())) {
            southPanel.add(interestedButton);
            southPanel.add(buyButton);
        }
        
        // WHAT: Add admin-specific button (mark sold out)
        // WHY: Only admins can mark products as sold out
        // HOW: if statement checks if admin, adds button
        if ("ADMIN".equals(currentUser.getRole())) {
            southPanel.add(markSoldOutButton);
        }
        
        // WHAT: Add back button (visible for all roles)
        // WHY: All users need way to close window
        // HOW: add() adds button to panel
        southPanel.add(backButton);
        
        // WHAT: Add south panel to bottom of window
        // WHY: Buttons should appear at bottom
        // HOW: BorderLayout.SOUTH positions at bottom
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * createHeaderPanel() - Creates header with title and user info
     * WHAT: Builds green header panel with role-specific title and user information
     * WHY: Provides visual branding and shows logged-in user and role
     * HOW: Uses BorderLayout to stack title and user label, role-specific icons
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        // WHAT: Create header panel with BorderLayout
        // WHY: BorderLayout allows stacking components
        // HOW: JPanel with BorderLayout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 125, 50)); // Green background
        header.setBorder(new EmptyBorder(15, 25, 15, 25)); // Padding
        
        // WHAT: Determine role icon based on user role
        // WHY: Different roles should have different icons
        // HOW: Ternary operators check role, assign appropriate emoji
        String roleIcon = ""; // No icon, just text
        
        // WHAT: Determine role text based on user role
        // WHY: Display human-readable role name
        // HOW: Ternary operators check role, assign appropriate text
        String roleText = "SELLER".equals(currentUser.getRole()) ? "Seller" : 
                         "BUYER".equals(currentUser.getRole()) ? "Buyer" : "Admin";
        
        // WHAT: Create title label with role icon and text
        // WHY: Identifies the window and user role
        // HOW: JLabel with concatenated icon and text
        JLabel title = new JLabel(roleIcon + " " + roleText + " - Inventory Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        
        // WHAT: Create user label with name and role
        // WHY: Shows who is logged in
        // HOW: JLabel with user name and role from currentUser object
        JLabel userLabel = new JLabel("User: " + currentUser.getName() + " (" + roleText + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(200, 230, 201)); // Light green text
        
        // WHAT: Create panel to hold title and user label
        // WHY: BorderLayout allows vertical stacking
        // HOW: JPanel with BorderLayout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(46, 125, 50));
        leftPanel.add(title, BorderLayout.CENTER); // Title in center
        leftPanel.add(userLabel, BorderLayout.SOUTH); // User label below
        
        // WHAT: Add left panel to header
        // WHY: Positions title and user info in header
        // HOW: BorderLayout.WEST positions on left
        header.add(leftPanel, BorderLayout.WEST);
        
        // WHAT: Return completed header panel
        // WHY: Caller needs panel to add to window
        // HOW: return statement
        return header;
    }
    
    /**
     * loadRecords() - Loads all inventory records from database and displays in table
     * WHAT: Queries database for all records, converts to table rows, populates JTable
     * WHY: Table needs data to display - called on window open and after add/edit/delete
     * HOW: Calls inventoryDAO.getAllRecords(), loops through items, adds each as table row
     */
    private void loadRecords() {
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Get all inventory records from database
            // WHY: Need all records to display in table
            // HOW: getAllRecords() queries database, returns List<FarmItem>
            List<FarmItem> items = inventoryDAO.getAllRecords();
            
            // WHAT: Clear all existing rows from table
            // WHY: Prevents duplicate rows when refreshing
            // HOW: setRowCount(0) removes all rows from table model
            tableModel.setRowCount(0);
            
            // WHAT: Loop through each inventory item
            // WHY: Each item must be converted to table row
            // HOW: Enhanced for loop iterates through List<FarmItem>
            for (FarmItem item : items) {
                // WHAT: Extract status if item is HarvestLot, empty string otherwise
                // WHY: Only HarvestLot has status field, other types don't
                // HOW: instanceof operator checks type, casts to HarvestLot if true
                String status = item instanceof HarvestLot ? ((HarvestLot) item).getStatus() : "";
                
                // WHAT: Extract price if item is HarvestLot, empty string otherwise
                // WHY: Only HarvestLot has price field, other types don't
                // HOW: instanceof operator checks type, casts to HarvestLot if true
                String priceDisplay = "";
                if (item instanceof HarvestLot) {
                    Double price = ((HarvestLot) item).getPricePerUnit();
                    if (price != null && price > 0) {
                        priceDisplay = String.format("₱%.2f", price);
                    } else {
                        priceDisplay = "Not set";
                    }
                }
                
                // WHAT: Create array representing one table row
                // WHY: Table model requires Object[] for each row
                // HOW: Array with values matching column order
                Object[] row = {
                    item.getId(), // Column 0: ID
                    item.getName(), // Column 1: Name
                    String.format("%.2f", item.getQuantity()), // Column 2: Quantity (formatted to 2 decimals)
                    item.getUnit(), // Column 3: Unit
                    priceDisplay, // Column 4: Price (formatted currency or "Not set")
                    item.getItemType(), // Column 5: Type (HARVEST or EQUIPMENT)
                    item.getDateAdded().toString(), // Column 6: Date (formatted as string)
                    status, // Column 7: Status (empty for non-HarvestLot items)
                    item.getNotes() != null ? item.getNotes() : "" // Column 8: Notes (empty string if null)
                };
                
                // WHAT: Add row to table model
                // WHY: Row must be added to model to appear in table
                // HOW: addRow() method adds Object[] to table model
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            // WHAT: Handle database errors
            // WHY: Database operations can fail (connection issues, SQL errors)
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message dialog
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Error loading records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * keyTyped() - Called when key is typed in search field
     * WHAT: Part of KeyListener interface - called when character is typed
     * WHY: Triggers live search as user types
     * HOW: Calls performSearch() method to filter table
     * @param e KeyEvent containing information about the key typed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // WHAT: Call search method to filter table
        // WHY: Live search should update as user types
        // HOW: performSearch() applies filter to table sorter
        performSearch();
    }
    
    /**
     * keyPressed() - Called when key is pressed in search field
     * WHAT: Part of KeyListener interface - called when key is pressed down
     * WHY: Triggers live search for all key events
     * HOW: Calls performSearch() method to filter table
     * @param e KeyEvent containing information about the key pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // WHAT: Call search method to filter table
        // WHY: Live search should update on any key event
        // HOW: performSearch() applies filter to table sorter
        performSearch();
    }
    
    /**
     * keyReleased() - Called when key is released in search field
     * WHAT: Part of KeyListener interface - called when key is released
     * WHY: Triggers live search for all key events
     * HOW: Calls performSearch() method to filter table
     * @param e KeyEvent containing information about the key released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // WHAT: Call search method to filter table
        // WHY: Live search should update on any key event
        // HOW: performSearch() applies filter to table sorter
        performSearch();
    }
    
    /**
     * performSearch() - Filters table rows based on search text
     * WHAT: Gets search text, creates RowFilter, applies to table sorter
     * WHY: Provides live search functionality - filters table as user types
     * HOW: Uses RowFilter.regexFilter() with case-insensitive pattern matching
     */
    private void performSearch() {
        // WHAT: Get search text from search field and remove whitespace
        // WHY: trim() prevents errors from accidental spaces
        // HOW: getText() gets field value, trim() removes whitespace
        String searchText = searchField.getText().trim();
        
        // WHAT: Check if search field is empty
        // WHY: Empty search should show all records
        // HOW: length() gets string length, == 0 checks if empty
        if (searchText.length() == 0) {
            // WHAT: Remove filter to show all rows
            // WHY: Empty search means show everything
            // HOW: setRowFilter(null) removes current filter
            sorter.setRowFilter(null);
        } else {
            // WHAT: Create regex filter for case-insensitive search
            // WHY: Search should match regardless of case (uppercase/lowercase)
            // HOW: RowFilter.regexFilter() creates filter, (?i) makes it case-insensitive
            // Search in columns: 1 (Name), 4 (Type), 7 (Notes)
            RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 1, 4, 7);
            
            // WHAT: Apply filter to table sorter
            // WHY: Filter must be applied to actually filter rows
            // HOW: setRowFilter() applies filter to sorter
            sorter.setRowFilter(rf);
        }
    }
    
    /**
     * actionPerformed() - Handles all button click events
     * WHAT: Called automatically when any registered button is clicked
     * WHY: Implements ActionListener interface - required for event handling
     * HOW: Checks event source and calls appropriate handler method
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // WHAT: Check if refresh button was clicked
        // WHY: Need to reload records from database
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == refreshButton) {
            // WHAT: Clear search field
            // WHY: Refresh should reset search
            // HOW: setText("") clears field
            searchField.setText("");
            // WHAT: Reload records from database
            // WHY: Updates table with latest data
            // HOW: loadRecords() queries database and repopulates table
            loadRecords();
        } 
        // WHAT: Check if add button was clicked
        // WHY: Need to open form for adding new record
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == addButton) {
            // WHAT: Open harvest form dialog for adding new record
            // WHY: User wants to create new harvest record
            // HOW: new HarvestForm() creates and displays dialog (editingItem=null for new record)
            new HarvestForm(currentUser, this);
            // WHAT: Reload records after form closes
            // WHY: New record should appear in table
            // HOW: loadRecords() refreshes table data
            loadRecords();
        } 
        // WHAT: Check if edit button was clicked
        // WHY: Need to open form for editing selected record
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == editButton) {
            // WHAT: Call method to edit selected record
            // WHY: Edit logic is separated into its own method
            // HOW: Method call opens form with existing data
            editSelectedRecord();
        } 
        // WHAT: Check if delete button was clicked
        // WHY: Need to delete selected record
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == deleteButton) {
            // WHAT: Call method to delete selected record
            // WHY: Delete logic is separated into its own method
            // HOW: Method call shows confirmation and deletes record
            deleteSelectedRecord();
        } 
        // WHAT: Check if export button was clicked
        // WHY: Need to export data to CSV file
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == exportButton) {
            // WHAT: Call method to export records to CSV
            // WHY: Export logic is separated into its own method
            // HOW: Method call opens file chooser and exports data
            exportToCSV();
        } 
        // WHAT: Check if import button was clicked
        // WHY: Need to import data from CSV file
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == importButton) {
            // WHAT: Call static method to import CSV file
            // WHY: Import functionality is in FileImporter utility class
            // HOW: importFromCSV() opens file chooser and imports data
            FileImporter.importFromCSV(this);
            // WHAT: Reload records after import
            // WHY: Imported records should appear in table
            // HOW: loadRecords() refreshes table data
            loadRecords(); // Refresh after import
        } 
        // WHAT: Check if back button was clicked
        // WHY: Need to close window
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == backButton) {
            // WHAT: Show confirmation dialog before closing
            // WHY: Prevents accidental window closure
            // HOW: confirmClose() shows dialog and closes if confirmed
            confirmClose();
        } 
        // WHAT: Check if interested button was clicked
        // WHY: Buyer wants to mark product as interested
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == interestedButton) {
            // WHAT: Call method to mark product as interested
            // WHY: Mark interested logic is separated into its own method
            // HOW: Method call updates status to "Interested"
            markAsInterested();
        } 
        // WHAT: Check if buy button was clicked
        // WHY: Buyer wants to purchase product
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == buyButton) {
            // WHAT: Call method to process purchase
            // WHY: Purchase logic is separated into its own method
            // HOW: Method call updates status to "Interested"
            buyProduct();
        } 
        // WHAT: Check if mark sold out button was clicked
        // WHY: Admin wants to mark product as sold out
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == markSoldOutButton) {
            // WHAT: Call method to mark product as sold out
            // WHY: Mark sold out logic is separated into its own method
            // HOW: Method call updates status to "Sold Out"
            markAsSoldOut();
        }
    }
    
    /**
     * markAsInterested() - Marks selected product as interested (Buyer function)
     * WHAT: Gets selected row, retrieves item, updates status to "Interested", saves to database
     * WHY: Allows buyers to express interest in products
     * HOW: Gets selected row, converts to model row, retrieves item, updates status, saves via DAO
     */
    private void markAsInterested() {
        // WHAT: Get currently selected row index in view (may be filtered)
        // WHY: Need to know which record user selected
        // HOW: getSelectedRow() returns row index, -1 if no selection
        int selectedRow = recordsTable.getSelectedRow();
        
        // WHAT: Check if no row is selected
        // WHY: Cannot mark interest without selecting a product
        // HOW: -1 means no selection
        if (selectedRow == -1) {
            // WHAT: Show warning message
            // WHY: User needs feedback about missing selection
            // HOW: showMessageDialog() displays warning
            JOptionPane.showMessageDialog(this, "Please select a product to mark as interested", "No Selection", JOptionPane.WARNING_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Convert view row index to model row index
            // WHY: Table may be filtered/sorted, need actual model row index
            // HOW: convertRowIndexToModel() converts view index to model index
            int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
            
            // WHAT: Get item ID from table model (column 0)
            // WHY: Need ID to retrieve full item from database
            // HOW: getValueAt() gets cell value, cast to Integer
            int itemId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // WHAT: Retrieve full item object from database
            // WHY: Need full item to update status
            // HOW: getRecordById() queries database and returns FarmItem
            FarmItem item = inventoryDAO.getRecordById(itemId);
            
            // WHAT: Check if item exists and is HarvestLot type
            // WHY: Only HarvestLot items have status field
            // HOW: != null checks existence, instanceof checks type
            if (item != null && item instanceof HarvestLot) {
                // WHAT: Cast to HarvestLot to access status methods
                // WHY: Need to call getStatus() and setStatus() methods
                // HOW: instanceof check ensures safe cast
                HarvestLot harvestLot = (HarvestLot) item;
                
                // WHAT: Get current status of the item
                // WHY: Need to check if already sold out
                // HOW: getStatus() returns current status string
                String currentStatus = harvestLot.getStatus();
                
                // WHAT: Check if product is already sold out
                // WHY: Cannot mark interest in sold out products
                // HOW: equals() compares status strings
                if ("Sold Out".equals(currentStatus)) {
                    // WHAT: Show warning message
                    // WHY: User needs feedback that product is unavailable
                    // HOW: showMessageDialog() displays warning
                    JOptionPane.showMessageDialog(this, "This product is already sold out!", "Not Available", JOptionPane.WARNING_MESSAGE);
                    return; // Exit method early
                }
                
                // WHAT: Show confirmation dialog with product details
                // WHY: User should confirm before marking interest
                // HOW: showConfirmDialog() displays Yes/No dialog with product info
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Mark this product as interested?\n\nProduct: " + item.getName() + "\nQuantity: " + item.getQuantity() + " " + item.getUnit(), // Message with details
                    "Mark as Interested", // Dialog title
                    JOptionPane.YES_NO_OPTION); // Buttons
                
                // WHAT: Check if user confirmed
                // WHY: Only update if user confirms
                // HOW: YES_OPTION constant returned when Yes clicked
                if (confirm == JOptionPane.YES_OPTION) {
                    // WHAT: Update status to "Interested"
                    // WHY: Mark product as having buyer interest
                    // HOW: setStatus() updates HarvestLot status field
                    harvestLot.setStatus("Interested");
                    
                    // WHAT: Save updated item to database
                    // WHY: Status change must be persisted
                    // HOW: updateRecord() executes UPDATE SQL statement
                    inventoryDAO.updateRecord(harvestLot);
                    
                    // WHAT: Show success message
                    // WHY: User needs confirmation that action succeeded
                    // HOW: showMessageDialog() displays success message
                    JOptionPane.showMessageDialog(this, 
                        "Product marked as interested!\nThe seller will be notified.", // Message text
                        "Success", // Dialog title
                        JOptionPane.INFORMATION_MESSAGE); // Information icon
                    
                    // WHAT: Reload records to show updated status
                    // WHY: Table should reflect status change
                    // HOW: loadRecords() refreshes table data
                    loadRecords();
                }
            }
        } catch (Exception ex) {
            // WHAT: Handle exceptions
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * buyProduct() - Opens purchase dialog for buyer
     * WHAT: Gets selected row, retrieves item, opens PurchaseDialog for complete purchase workflow
     * WHY: Allows buyers to purchase with quantity selection, delivery options, and automatic calculations
     * HOW: Gets selected row, retrieves item, opens PurchaseDialog, refreshes table after purchase
     */
    private void buyProduct() {
        // WHAT: Get currently selected row index
        // WHY: Need to know which product buyer wants to purchase
        // HOW: getSelectedRow() returns row index, -1 if no selection
        int selectedRow = recordsTable.getSelectedRow();
        
        // WHAT: Check if no row is selected
        // WHY: Cannot purchase without selecting a product
        // HOW: -1 means no selection
        if (selectedRow == -1) {
            // WHAT: Show warning message
            // WHY: User needs feedback about missing selection
            // HOW: showMessageDialog() displays warning
            JOptionPane.showMessageDialog(this, "Please select a product to buy", "No Selection", JOptionPane.WARNING_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Convert view row index to model row index
            // WHY: Table may be filtered/sorted
            // HOW: convertRowIndexToModel() converts view index to model index
            int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
            
            // WHAT: Get item ID from table model
            // WHY: Need ID to retrieve full item
            // HOW: getValueAt() gets cell value, cast to Integer
            int itemId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // WHAT: Retrieve full item object from database
            // WHY: Need full item to pass to PurchaseDialog
            // HOW: getRecordById() queries database
            FarmItem item = inventoryDAO.getRecordById(itemId);
            
            // WHAT: Check if item exists and is HarvestLot type
            // WHY: Only HarvestLot items can be purchased
            // HOW: != null and instanceof checks
            if (item != null && item instanceof HarvestLot) {
                // WHAT: Cast to HarvestLot
                // WHY: Need to access HarvestLot-specific methods
                // HOW: instanceof ensures safe cast
                HarvestLot harvestLot = (HarvestLot) item;
                
                // WHAT: Get current status
                // WHY: Need to check if already sold out
                // HOW: getStatus() returns status string
                String currentStatus = harvestLot.getStatus();
                
                // WHAT: Check if product is already sold out
                // WHY: Cannot purchase sold out products
                // HOW: equals() compares status strings
                if ("Sold Out".equals(currentStatus)) {
                    // WHAT: Show warning message
                    // WHY: User needs feedback that product is unavailable
                    // HOW: showMessageDialog() displays warning
                    JOptionPane.showMessageDialog(this, "This product is already sold out!", "Not Available", JOptionPane.WARNING_MESSAGE);
                    return; // Exit method early
                }
                
                // WHAT: Check if quantity is available
                // WHY: Cannot purchase if no quantity available
                // HOW: getQuantity() returns available quantity
                if (harvestLot.getQuantity() <= 0) {
                    // WHAT: Show warning message
                    // WHY: User needs feedback that product is unavailable
                    // HOW: showMessageDialog() displays warning
                    JOptionPane.showMessageDialog(this, "This product is out of stock!", "Not Available", JOptionPane.WARNING_MESSAGE);
                    return; // Exit method early
                }
                
                // WHAT: Open PurchaseDialog for complete purchase workflow
                // WHY: Provides professional purchase interface with all options
                // HOW: new PurchaseDialog() creates and displays dialog
                PurchaseDialog purchaseDialog = new PurchaseDialog(this, currentUser, harvestLot);
                
                // WHAT: Refresh table after purchase dialog closes
                // WHY: Table should reflect any inventory changes from purchase
                // HOW: loadRecords() queries database and repopulates table
                loadRecords();
            } else {
                // WHAT: Show error if item is not a HarvestLot
                // WHY: Only harvest items can be purchased
                // HOW: showMessageDialog() displays error
                JOptionPane.showMessageDialog(this, "Only harvest items can be purchased", "Invalid Item", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // WHAT: Handle exceptions
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * markAsSoldOut() - Marks selected product as sold out (Admin function)
     * WHAT: Gets selected row, retrieves item, shows confirmation, updates status to "Sold Out", saves to database
     * WHY: Allows admins to mark products as sold out after purchase
     * HOW: Gets selected row, retrieves item, shows confirmation dialog, updates status, saves via DAO
     */
    private void markAsSoldOut() {
        // WHAT: Get currently selected row index
        // WHY: Need to know which product admin wants to mark as sold out
        // HOW: getSelectedRow() returns row index, -1 if no selection
        int selectedRow = recordsTable.getSelectedRow();
        
        // WHAT: Check if no row is selected
        // WHY: Cannot mark sold out without selecting a product
        // HOW: -1 means no selection
        if (selectedRow == -1) {
            // WHAT: Show warning message
            // WHY: User needs feedback about missing selection
            // HOW: showMessageDialog() displays warning
            JOptionPane.showMessageDialog(this, "Please select a product to mark as sold out", "No Selection", JOptionPane.WARNING_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Convert view row index to model row index
            // WHY: Table may be filtered/sorted
            // HOW: convertRowIndexToModel() converts view index to model index
            int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
            
            // WHAT: Get item ID from table model
            // WHY: Need ID to retrieve full item
            // HOW: getValueAt() gets cell value, cast to Integer
            int itemId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // WHAT: Retrieve full item object from database
            // WHY: Need full item to update status
            // HOW: getRecordById() queries database
            FarmItem item = inventoryDAO.getRecordById(itemId);
            
            // WHAT: Check if item exists and is HarvestLot type
            // WHY: Only HarvestLot items have status field
            // HOW: != null and instanceof checks
            if (item != null && item instanceof HarvestLot) {
                // WHAT: Cast to HarvestLot
                // WHY: Need to access status methods
                // HOW: instanceof ensures safe cast
                HarvestLot harvestLot = (HarvestLot) item;
                
                // WHAT: Get current status
                // WHY: Need to check if already sold out
                // HOW: getStatus() returns status string
                String currentStatus = harvestLot.getStatus();
                
                // WHAT: Check if product is already sold out
                // WHY: No need to mark as sold out if already sold out
                // HOW: equals() compares status strings
                if ("Sold Out".equals(currentStatus)) {
                    // WHAT: Show information message
                    // WHY: User needs feedback that product is already sold out
                    // HOW: showMessageDialog() displays information
                    JOptionPane.showMessageDialog(this, "This product is already marked as sold out!", "Already Sold", JOptionPane.INFORMATION_MESSAGE);
                    return; // Exit method early
                }
                
                // WHAT: Show confirmation dialog with product details
                // WHY: Admin should confirm before marking as sold out
                // HOW: showConfirmDialog() displays Yes/No dialog with product info
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Mark this product as SOLD OUT?\n\nProduct: " + item.getName() + "\nQuantity: " + item.getQuantity() + " " + item.getUnit() + "\nCurrent Status: " + currentStatus, // Message with details
                    "Mark as Sold Out", // Dialog title
                    JOptionPane.YES_NO_OPTION, // Buttons
                    JOptionPane.WARNING_MESSAGE); // Warning icon
                
                // WHAT: Check if admin confirmed
                // WHY: Only update if admin confirms
                // HOW: YES_OPTION constant returned when Yes clicked
                if (confirm == JOptionPane.YES_OPTION) {
                    // WHAT: Update status to "Sold Out"
                    // WHY: Mark product as no longer available
                    // HOW: setStatus() updates HarvestLot status field
                    harvestLot.setStatus("Sold Out");
                    
                    // WHAT: Save updated item to database
                    // WHY: Status change must be persisted
                    // HOW: updateRecord() executes UPDATE SQL statement
                    inventoryDAO.updateRecord(harvestLot);
                    
                    // WHAT: Show success message
                    // WHY: Admin needs confirmation that status was updated
                    // HOW: showMessageDialog() displays success message
                    JOptionPane.showMessageDialog(this, 
                        "Product marked as SOLD OUT successfully!", // Message text
                        "Status Updated", // Dialog title
                        JOptionPane.INFORMATION_MESSAGE); // Information icon
                    
                    // WHAT: Reload records to show updated status
                    // WHY: Table should reflect status change
                    // HOW: loadRecords() refreshes table data
                    loadRecords();
                }
            }
        } catch (Exception ex) {
            // WHAT: Handle exceptions
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * editSelectedRecord() - Opens edit form for selected record
     * WHAT: Gets selected row, retrieves item from database, opens HarvestForm in edit mode
     * WHY: Allows sellers/admins to modify existing records
     * HOW: Gets selected row, converts to model row, gets ID, retrieves item, opens form with item
     */
    private void editSelectedRecord() {
        // WHAT: Get currently selected row index
        // WHY: Need to know which record user wants to edit
        // HOW: getSelectedRow() returns row index, -1 if no selection
        int selectedRow = recordsTable.getSelectedRow();
        
        // WHAT: Check if no row is selected
        // WHY: Cannot edit without selecting a record
        // HOW: -1 means no selection
        if (selectedRow == -1) {
            // WHAT: Show warning message
            // WHY: User needs feedback about missing selection
            // HOW: showMessageDialog() displays warning
            JOptionPane.showMessageDialog(this, "Please select a record to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Convert view row index to model row index
            // WHY: Table may be filtered/sorted
            // HOW: convertRowIndexToModel() converts view index to model index
            int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
            
            // WHAT: Get item ID from table model (column 0)
            // WHY: Need ID to retrieve full item from database
            // HOW: getValueAt() gets cell value, cast to Integer
            int itemId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // WHAT: Retrieve full item object from database
            // WHY: Need full item to populate edit form
            // HOW: getRecordById() queries database and returns FarmItem
            FarmItem item = inventoryDAO.getRecordById(itemId);
            
            // WHAT: Check if item was found
            // WHY: Item may not exist (deleted by another user)
            // HOW: != null checks if item exists
            if (item != null) {
                // WHAT: Open harvest form dialog in edit mode
                // WHY: User wants to edit existing record
                // HOW: new HarvestForm() with item parameter opens form with existing data
                new HarvestForm(currentUser, this, item);
                
                // WHAT: Reload records after form closes
                // WHY: Changes should appear in table
                // HOW: loadRecords() refreshes table data
                loadRecords();
            }
        } catch (Exception ex) {
            // WHAT: Handle exceptions
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error
            JOptionPane.showMessageDialog(this, "Error loading record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * deleteSelectedRecord() - Deletes selected record from database
     * WHAT: Gets selected row, shows confirmation dialog, deletes record if confirmed
     * WHY: Allows sellers/admins to remove unwanted records
     * HOW: Gets selected row, converts to model row, gets ID, shows confirmation, deletes via DAO
     */
    private void deleteSelectedRecord() {
        // WHAT: Get currently selected row index
        // WHY: Need to know which record user wants to delete
        // HOW: getSelectedRow() returns row index, -1 if no selection
        int selectedRow = recordsTable.getSelectedRow();
        
        // WHAT: Check if no row is selected
        // WHY: Cannot delete without selecting a record
        // HOW: -1 means no selection
        if (selectedRow == -1) {
            // WHAT: Show warning message
            // WHY: User needs feedback about missing selection
            // HOW: showMessageDialog() displays warning
            JOptionPane.showMessageDialog(this, "Please select a record to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Show delete confirmation dialog
        // WHY: Prevents accidental deletion
        // HOW: showConfirmDialog() displays Yes/No dialog
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this record?", // Message text
            "Confirm Delete", // Dialog title
            JOptionPane.YES_NO_OPTION); // Buttons
        
        // WHAT: Check if user confirmed deletion
        // WHY: Only delete if user confirms
        // HOW: YES_OPTION constant returned when Yes clicked
        if (confirm == JOptionPane.YES_OPTION) {
            // WHAT: Try block to handle database exceptions
            // WHY: Database operations can fail
            // HOW: try-catch block wraps database code
            try {
                // WHAT: Convert view row index to model row index
                // WHY: Table may be filtered/sorted
                // HOW: convertRowIndexToModel() converts view index to model index
                int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
                
                // WHAT: Get item ID from table model (column 0)
                // WHY: Need ID to delete record from database
                // HOW: getValueAt() gets cell value, cast to Integer
                int itemId = (Integer) tableModel.getValueAt(modelRow, 0);
                
                // WHAT: Delete record from database
                // WHY: Remove unwanted record
                // HOW: deleteRecord() executes DELETE SQL statement
                inventoryDAO.deleteRecord(itemId);
                
                // WHAT: Show success message
                // WHY: User needs confirmation that record was deleted
                // HOW: showMessageDialog() displays success message
                JOptionPane.showMessageDialog(this, "Record deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // WHAT: Reload records to remove deleted record from table
                // WHY: Table should reflect deletion
                // HOW: loadRecords() refreshes table data
                loadRecords();
            } catch (Exception ex) {
                // WHAT: Handle exceptions
                // WHY: Database errors must be shown to user
                // HOW: catch block executes when exception occurs
                // WHAT: Show error message
                // WHY: User needs to know what went wrong
                // HOW: showMessageDialog() displays error
                JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * exportToCSV() - Exports all records to CSV file
     * WHAT: Gets all records, opens file chooser, exports to selected CSV file
     * WHY: Allows users to backup data or use in spreadsheet programs
     * HOW: Queries database, opens file chooser, calls CSVExporter.exportToCSV()
     */
    public void exportToCSV() {
        // WHAT: Try block to handle file I/O and database exceptions
        // WHY: File operations and database operations can fail
        // HOW: try-catch block wraps all operations
        try {
            // WHAT: Get all inventory records from database
            // WHY: Need all records to export
            // HOW: getAllRecords() queries database, returns List<FarmItem>
            List<FarmItem> items = inventoryDAO.getAllRecords();
            
            // WHAT: Check if there are any records to export
            // WHY: Cannot export empty list
            // HOW: isEmpty() checks if list has no elements
            if (items.isEmpty()) {
                // WHAT: Show information message
                // WHY: User needs feedback that there's nothing to export
                // HOW: showMessageDialog() displays information
                JOptionPane.showMessageDialog(this, "No records to export", "Info", JOptionPane.INFORMATION_MESSAGE);
                return; // Exit method early
            }
            
            // WHAT: Create file chooser dialog for saving file
            // WHY: User needs to choose where to save CSV file
            // HOW: JFileChooser creates native file selection dialog
            JFileChooser fileChooser = new JFileChooser();
            
            // WHAT: Set dialog title text
            // WHY: Identifies the purpose of the file chooser
            // HOW: setDialogTitle() sets text displayed in dialog title bar
            fileChooser.setDialogTitle("Save CSV File");
            
            // WHAT: Set default file name
            // WHY: Provides convenient default filename
            // HOW: setSelectedFile() sets default file object
            fileChooser.setSelectedFile(new File("agritrack_export.csv"));
            
            // WHAT: Show file chooser dialog and get user's selection
            // WHY: User needs to choose save location
            // HOW: showSaveDialog() displays dialog, returns APPROVE_OPTION if user selects file
            int userSelection = fileChooser.showSaveDialog(this);
            
            // WHAT: Check if user selected a file (didn't cancel)
            // WHY: Only proceed if user actually selected a file
            // HOW: APPROVE_OPTION constant returned when user clicks Save/OK
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // WHAT: Get the file object that user selected
                // WHY: Need file path to save CSV data
                // HOW: getSelectedFile() returns File object representing selected file
                File fileToSave = fileChooser.getSelectedFile();
                
                // WHAT: Get absolute file path as string
                // WHY: CSVExporter needs file path string
                // HOW: getAbsolutePath() returns full file path
                String filePath = fileToSave.getAbsolutePath();
                
                // WHAT: Check if file path doesn't end with .csv extension
                // WHY: CSV files should have .csv extension
                // HOW: toLowerCase() converts to lowercase, endsWith() checks extension
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    // WHAT: Append .csv extension to file path
                    // WHY: Ensures file has correct extension
                    // HOW: String concatenation adds ".csv"
                    filePath += ".csv";
                }
                
                // WHAT: Export records to CSV file
                // WHY: Save data to file for backup or external use
                // HOW: exportToCSV() writes all items to CSV file using polymorphic toCSVString()
                CSVExporter.exportToCSV(filePath, items);
                
                // WHAT: Show success message with file path
                // WHY: User needs confirmation that export succeeded
                // HOW: showMessageDialog() displays success message
                JOptionPane.showMessageDialog(this, "Data exported successfully to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            // If user cancelled file chooser, no action needed (silently return)
        } catch (Exception ex) {
            // WHAT: Handle exceptions
            // WHY: File I/O or database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error
            JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
