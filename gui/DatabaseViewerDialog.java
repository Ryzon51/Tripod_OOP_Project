package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.sql.*; // Import: Swing components (JFrame, JPanel, JButton, JTable, etc.)
import javax.swing.*; // Import: EmptyBorder for padding/margins
import javax.swing.border.EmptyBorder; // Import: DefaultTableModel for table data
import javax.swing.table.DefaultTableModel; // Import: SQL classes for database operations
import util.DBConnection; // Import: DBConnection utility for database access

/**
 * DatabaseViewerDialog - Dialog to view all database tables and their contents
 * WHAT: Displays all tables in the database and allows viewing their data
 * WHY: Users need to see all data stored in their H2 database
 * HOW: Uses JTabbedPane to show each table in a separate tab with JTable for data display
 */
public class DatabaseViewerDialog extends JDialog implements ActionListener {
    // WHAT: Tabbed pane to display multiple tables
    // WHY: Each table gets its own tab for organized viewing
    // HOW: JTabbedPane with tabs for each database table
    private JTabbedPane tabbedPane;
    
    // WHAT: Button to refresh all tables
    // WHY: Users need to reload data after changes
    // HOW: JButton that triggers refresh
    private JButton refreshButton;
    
    // WHAT: Button to switch between user's database and application database
    // WHY: Users may want to view different databases
    // HOW: JButton that toggles database connection
    private JButton switchDatabaseButton;
    
    // WHAT: Label to show current database connection
    // WHY: Users need to know which database they're viewing
    // HOW: JLabel that displays connection URL
    private JLabel connectionLabel;
    
    /**
     * Constructor - Creates and displays the DatabaseViewerDialog
     * WHAT: Initializes components, sets up layout, loads database tables
     * WHY: Called to show database contents to user
     * HOW: Calls helper methods to set up GUI, then displays dialog
     * @param parent Parent JFrame for modal dialog
     */
    public DatabaseViewerDialog(JFrame parent) {
        // WHAT: Call parent constructor to create modal dialog
        // WHY: Modal dialog blocks parent window until closed
        // HOW: super() calls JDialog constructor with parent and modal flag
        super(parent, "Database Viewer", true);
        
        // WHAT: Set dialog size
        // WHY: Provides adequate space for tables
        // HOW: setSize() sets width and height
        setSize(1000, 700);
        
        // WHAT: Set minimum size to prevent UI distortion
        // WHY: Maintains professional appearance
        // HOW: setMinimumSize() sets minimum dimensions
        setMinimumSize(new Dimension(800, 600));
        
        // WHAT: Center dialog on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo() centers dialog
        setLocationRelativeTo(parent);
        
        // WHAT: Set dialog icon
        // WHY: Professional appearance
        // HOW: setIconImage() sets icon
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Initialize components and layout
        // WHY: Components must be created before displaying
        // HOW: Calls helper methods
        initializeComponents();
        setupLayout();
        
        // WHAT: Load all database tables
        // WHY: Dialog should show data when opened
        // HOW: Calls loadAllTables() method
        loadAllTables();
        
        // WHAT: Make dialog visible
        // WHY: User needs to see the dialog
        // HOW: setVisible(true) displays dialog
        setVisible(true);
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates buttons, labels, and tabbed pane
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components and configures their properties
     */
    private void initializeComponents() {
        // WHAT: Create tabbed pane for displaying tables
        // WHY: Each table needs its own tab
        // HOW: JTabbedPane constructor
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create refresh button
        // WHY: Users need to reload data
        // HOW: JButton constructor
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        styleButton(refreshButton, new Color(33, 150, 243)); // Blue
        
        // WHAT: Create switch database button
        // WHY: Users may want to view different databases
        // HOW: JButton constructor
        switchDatabaseButton = new JButton("Switch Database");
        switchDatabaseButton.addActionListener(this);
        styleButton(switchDatabaseButton, new Color(156, 39, 176)); // Purple
        
        // WHAT: Create connection label
        // WHY: Shows current database connection
        // HOW: JLabel constructor
        connectionLabel = new JLabel("Database: " + DBConnection.getConnectionURL());
        connectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        connectionLabel.setForeground(new Color(100, 100, 100));
    }
    
    /**
     * styleButton() - Applies consistent styling to buttons
     * WHAT: Sets button background color, text color, font, and size
     * WHY: Consistent button appearance
     * HOW: Calls setter methods on JButton
     * @param button The button to style
     * @param bgColor Background color for the button
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * setupLayout() - Arranges components in the dialog
     * WHAT: Creates layout structure with buttons at top and tabbed pane below
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure
     */
    private void setupLayout() {
        // WHAT: Set dialog layout to BorderLayout
        // WHY: BorderLayout divides dialog into regions
        // HOW: getContentPane().setLayout() sets layout manager
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // WHAT: Create top panel for buttons and connection info
        // WHY: Buttons should be at top
        // HOW: JPanel with FlowLayout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // WHAT: Create button panel
        // WHY: Groups buttons together
        // HOW: JPanel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshButton);
        buttonPanel.add(switchDatabaseButton);
        
        // WHAT: Add components to top panel
        // WHY: Organize layout
        // HOW: BorderLayout positions components
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(connectionLabel, BorderLayout.EAST);
        
        // WHAT: Add top panel to dialog
        // WHY: Buttons should appear at top
        // HOW: BorderLayout.NORTH positions at top
        getContentPane().add(topPanel, BorderLayout.NORTH);
        
        // WHAT: Add tabbed pane to center
        // WHY: Tables should fill center area
        // HOW: BorderLayout.CENTER positions in center
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * loadAllTables() - Loads all database tables into tabs
     * WHAT: Queries database metadata to get all tables, then loads each table's data
     * WHY: Users need to see all tables and their contents
     * HOW: Uses DatabaseMetaData to get table names, then queries each table
     */
    private void loadAllTables() {
        // WHAT: Clear existing tabs
        // WHY: Refresh removes old tabs
        // HOW: removeAll() removes all tabs
        tabbedPane.removeAll();
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try (Connection conn = DBConnection.getConnection()) {
            // WHAT: Get database metadata
            // WHY: Metadata contains information about tables
            // HOW: getMetaData() returns DatabaseMetaData object
            DatabaseMetaData metaData = conn.getMetaData();
            
            // WHAT: Get all tables from database
            // WHY: Need to know which tables exist
            // HOW: getTables() returns ResultSet with table information
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            // WHAT: Loop through each table
            // WHY: Each table needs its own tab
            // HOW: while loop iterates through ResultSet
            while (tables.next()) {
                // WHAT: Get table name
                // WHY: Need table name to query data
                // HOW: getString("TABLE_NAME") gets table name from ResultSet
                String tableName = tables.getString("TABLE_NAME");
                
                // WHAT: Skip H2 system tables
                // WHY: System tables are not user data
                // HOW: Check if table name starts with "INFORMATION_SCHEMA" or "SYSTEM"
                if (tableName.startsWith("INFORMATION_SCHEMA") || tableName.startsWith("SYSTEM")) {
                    continue; // Skip system tables
                }
                
                // WHAT: Load table data into a tab
                // WHY: Users need to see table contents
                // HOW: Calls loadTableData() method
                loadTableData(tableName, conn);
            }
            
            // WHAT: Update connection label
            // WHY: Show current database connection
            // HOW: setText() updates label text
            if (DBConnection.isUsingUserDatabase()) {
                connectionLabel.setText("Database: " + DBConnection.getUserDatabaseURL() + " (User's H2 Console)");
            } else {
                connectionLabel.setText("Database: " + DBConnection.getConnectionURL());
            }
            
        } catch (SQLException e) {
            // WHAT: Handle database errors
            // WHY: Database operations can fail
            // HOW: catch block executes when exception occurs
            JOptionPane.showMessageDialog(this, 
                "Error loading database tables: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * loadTableData() - Loads a specific table's data into a tab
     * WHAT: Queries a table and displays its data in a JTable within a tab
     * WHY: Users need to see table contents
     * HOW: Executes SELECT query, creates JTable with data, adds tab to tabbed pane
     * @param tableName Name of the table to load
     * @param conn Database connection
     */
    private void loadTableData(String tableName, Connection conn) {
        // WHAT: Query all data from table
        // WHY: Need all rows to display
        // HOW: SELECT * FROM table query
        String query = "SELECT * FROM \"" + tableName + "\"";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // WHAT: Get result set metadata
            // WHY: Need column names and count
            // HOW: getMetaData() returns ResultSetMetaData
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // WHAT: Create column names array
            // WHY: JTable needs column headers
            // HOW: Loop through columns, get column names
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            
            // WHAT: Create table model
            // WHY: JTable needs data model
            // HOW: DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Read-only
                }
            };
            
            // WHAT: Loop through result set rows
            // WHY: Each row needs to be added to table
            // HOW: while loop iterates through ResultSet
            while (rs.next()) {
                // WHAT: Create row array
                // WHY: Table model needs row data
                // HOW: Object array with column values
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                // WHAT: Add row to table model
                // WHY: Row must be added to appear in table
                // HOW: addRow() method adds row
                tableModel.addRow(row);
            }
            
            // WHAT: Create JTable with model
            // WHY: Display data in tabular format
            // HOW: JTable constructor takes TableModel
            JTable table = new JTable(tableModel);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(0, 188, 212)); // Teal
            table.getTableHeader().setForeground(Color.WHITE);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            // WHAT: Create scroll pane for table
            // WHY: Table may be large, needs scrolling
            // HOW: JScrollPane wraps JTable
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            // WHAT: Add tab to tabbed pane
            // WHY: Each table needs its own tab
            // HOW: addTab() creates new tab with label and component
            tabbedPane.addTab(tableName, scrollPane);
            
        } catch (SQLException e) {
            // WHAT: Handle errors loading table
            // WHY: Table query can fail
            // HOW: Show error message
            JOptionPane.showMessageDialog(this, 
                "Error loading table " + tableName + ": " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * actionPerformed() - Handles button click events
     * WHAT: Called automatically when buttons are clicked
     * WHY: Implements ActionListener interface
     * HOW: Checks event source and calls appropriate handler
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            // WHAT: Reload all tables
            // WHY: User wants to see latest data
            // HOW: Calls loadAllTables() method
            loadAllTables();
        } else if (e.getSource() == switchDatabaseButton) {
            // WHAT: Toggle between user's database and application database
            // WHY: User may want to view different databases
            // HOW: Toggles useUserDatabase flag and reloads tables
            boolean currentlyUsingUserDb = DBConnection.isUsingUserDatabase();
            DBConnection.setUseUserDatabase(!currentlyUsingUserDb);
            loadAllTables();
            
            // WHAT: Show confirmation message
            // WHY: User needs feedback
            // HOW: JOptionPane shows message
            String message = currentlyUsingUserDb 
                ? "Switched to application database" 
                : "Switched to user's H2 Console database (jdbc:h2:~/test)";
            JOptionPane.showMessageDialog(this, message, "Database Switched", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}





