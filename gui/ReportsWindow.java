package gui; // Package declaration: Groups this class with other GUI classes

import dao.InventoryDAO; // Import: InventoryDAO for database operations
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import java.util.List; // Import: List interface for collections
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.FarmItem; // Import: FarmItem base class
import model.HarvestLot; // Import: HarvestLot class for status operations
import model.User; // Import: User model class

/**
 * ReportsWindow - Reports and Statistics Window
 * WHAT: Displays inventory statistics, summaries, and reports in a formatted view
 * WHY: Provides users with insights into inventory data (total items, harvests, equipment, status breakdown)
 * HOW: Queries database, calculates statistics, displays in formatted panels with labels
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener for button clicks
 */
public class ReportsWindow extends JFrame implements ActionListener {
    // WHAT: Currently logged-in user object
    // WHY: Needed for displaying user info in header
    // HOW: Stored as instance variable, passed from constructor
    private final User currentUser;
    
    // WHAT: InventoryDAO object for database operations
    // WHY: Need to query database for statistics
    // HOW: Created once in constructor, used throughout class
    private final InventoryDAO inventoryDAO;
    
    // WHAT: Button to refresh statistics
    // WHY: Users need to update statistics after data changes
    // HOW: Calls loadStatistics() method when clicked
    private JButton refreshButton;
    
    // WHAT: Button to close window
    // WHY: Users need way to close window
    // HOW: Shows confirmation, closes window when confirmed
    private JButton closeButton;
    
    // WHAT: Panel displaying statistics
    // WHY: Container for all statistic labels
    // HOW: JPanel with GridBagLayout for organized display
    private JPanel statisticsPanel;
    
    /**
     * Constructor - Creates and displays the ReportsWindow
     * WHAT: Initializes components, sets up layout, loads statistics, configures window
     * WHY: Called to display inventory reports and statistics
     * HOW: Calls helper methods to set up GUI, loads data, then displays window
     * @param user Currently logged-in user object
     */
    public ReportsWindow(User user) {
        // WHAT: Store user object
        // WHY: Needed for displaying user info
        // HOW: this.currentUser = user assigns parameter
        this.currentUser = user;
        
        // WHAT: Create InventoryDAO instance
        // WHY: Need DAO to query database for statistics
        // HOW: new InventoryDAO() creates instance
        this.inventoryDAO = new InventoryDAO();
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in window
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Load statistics from database
        // WHY: Window should show statistics when opened
        // HOW: Calls loadStatistics() method
        loadStatistics();
        
        // WHAT: Prevent default close behavior
        // WHY: Want to show confirmation dialog before closing
        // HOW: DO_NOTHING_ON_CLOSE prevents automatic closing
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // WHAT: Set window title bar text
        // WHY: Identifies window in taskbar
        // HOW: setTitle() sets title text
        setTitle("AgriTrack - Reports & Statistics");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 800x600 pixels
        // WHY: Provides adequate space for statistics display
        // HOW: setSize() sets width and height
        setSize(800, 600);
        
        // WHAT: Center window on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(null) centers window
        setLocationRelativeTo(null);
        
        // WHAT: Add window close listener for confirmation dialog
        // WHY: Prevents accidental window closure
        // HOW: WindowAdapter overrides windowClosing() method
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmClose();
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
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to close this window?", 
            "Confirm Close", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates buttons and configures their properties
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components, sets fonts, attaches event listeners
     */
    private void initializeComponents() {
        // WHAT: Create refresh button
        // WHY: Users need button to update statistics
        // HOW: JButton constructor with text and emoji
        refreshButton = new JButton("Refresh");
        
        // WHAT: Create close button
        // WHY: Users need button to close window
        // HOW: JButton constructor
        closeButton = new JButton("Close");
        
        // WHAT: Style buttons with colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(refreshButton, new Color(33, 150, 243)); // Blue
        styleButton(closeButton, new Color(158, 158, 158)); // Gray
        
        // WHAT: Attach action listeners to buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        refreshButton.addActionListener(this);
        closeButton.addActionListener(this);
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
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * setupLayout() - Arranges components in window
     * WHAT: Creates layout structure with header, statistics panel, and buttons
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure
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
        
        // WHAT: Create statistics panel with GridBagLayout
        // WHY: GridBagLayout allows precise positioning of statistics
        // HOW: JPanel with GridBagLayout
        statisticsPanel = new JPanel(new GridBagLayout());
        statisticsPanel.setBackground(Color.WHITE);
        statisticsPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // WHAT: Create scroll pane for statistics panel
        // WHY: Statistics may overflow if many items
        // HOW: JScrollPane wraps statisticsPanel
        JScrollPane scrollPane = new JScrollPane(statisticsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setBackground(Color.WHITE);
        
        // WHAT: Add scroll pane to center of window
        // WHY: Statistics are main content
        // HOW: BorderLayout.CENTER positions in center
        add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Create south panel for buttons
        // WHY: Buttons should appear at bottom
        // HOW: JPanel with FlowLayout, centered
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        southPanel.setBackground(new Color(245, 245, 250));
        southPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        southPanel.add(refreshButton);
        southPanel.add(closeButton);
        
        // WHAT: Add south panel to bottom of window
        // WHY: Buttons should appear at bottom
        // HOW: BorderLayout.SOUTH positions at bottom
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * createHeaderPanel() - Creates header with title and user info
     * WHAT: Builds green header panel with title and user information
     * WHY: Provides visual branding and shows logged-in user
     * HOW: Uses BorderLayout to stack title and user label
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 125, 50)); // Green
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel title = new JLabel("Reports & Statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        // WHAT: Add application icon to title label
        // WHY: Visual branding with logo
        // HOW: setIcon() adds icon to label
        try {
            title.setIcon(util.IconUtil.getApplicationIcon());
            title.setIconTextGap(10); // Space between icon and text
            title.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of icon
        } catch (Exception e) {
            // Icon loading failed, use emoji fallback
            title.setText("Reports & Statistics");
        }
        
        JLabel userLabel = new JLabel("User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(200, 230, 201)); // Light green
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(46, 125, 50));
        leftPanel.add(title, BorderLayout.CENTER);
        leftPanel.add(userLabel, BorderLayout.SOUTH);
        
        header.add(leftPanel, BorderLayout.WEST);
        return header;
    }
    
    /**
     * loadStatistics() - Loads and displays inventory statistics
     * WHAT: Queries database, calculates statistics, displays in formatted labels
     * WHY: Window needs to show current statistics
     * HOW: Gets all records, calculates counts and totals, creates formatted labels
     */
    private void loadStatistics() {
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Get all inventory records from database
            // WHY: Need all records to calculate statistics
            // HOW: getAllRecords() queries database, returns List<FarmItem>
            List<FarmItem> items = inventoryDAO.getAllRecords();
            
            // WHAT: Clear existing statistics
            // WHY: Prevents duplicate labels when refreshing
            // HOW: removeAll() removes all components from panel
            statisticsPanel.removeAll();
            
            // WHAT: Create constraints for component positioning
            // WHY: GridBagLayout requires constraints
            // HOW: GridBagConstraints object
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 10, 15, 10);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            int row = 0;
            
            // WHAT: Calculate total number of items
            // WHY: Basic statistic users want to see
            // HOW: size() returns number of elements in list
            int totalItems = items.size();
            
            // WHAT: Count harvest items
            // WHY: Users want to know how many harvests vs equipment
            // HOW: Loop through items, check getItemType() equals "HARVEST"
            int harvestCount = 0;
            int equipmentCount = 0;
            double totalQuantity = 0.0;
            int availableCount = 0;
            int interestedCount = 0;
            int soldOutCount = 0;
            
            for (FarmItem item : items) {
                if ("HARVEST".equals(item.getItemType())) {
                    harvestCount++;
                    if (item instanceof HarvestLot) {
                        String status = ((HarvestLot) item).getStatus();
                        if ("Available".equals(status)) {
                            availableCount++;
                        } else if ("Interested".equals(status)) {
                            interestedCount++;
                        } else if ("Sold Out".equals(status)) {
                            soldOutCount++;
                        }
                    }
                } else if ("EQUIPMENT".equals(item.getItemType())) {
                    equipmentCount++;
                }
                totalQuantity += item.getQuantity();
            }
            
            // WHAT: Add statistics labels to panel
            // WHY: Display calculated statistics to user
            // HOW: createStatLabel() creates formatted label, add() adds to panel
            
            // Total Items
            gbc.gridy = row++;
            statisticsPanel.add(createStatLabel("Total Items", String.valueOf(totalItems)), gbc);
            
            // Harvest Count
            gbc.gridy = row++;
            statisticsPanel.add(createStatLabel("Harvest Items", String.valueOf(harvestCount)), gbc);
            
            // Equipment Count
            gbc.gridy = row++;
            statisticsPanel.add(createStatLabel("Equipment Items", String.valueOf(equipmentCount)), gbc);
            
            // Total Quantity
            gbc.gridy = row++;
            statisticsPanel.add(createStatLabel("Total Quantity", String.format("%.2f", totalQuantity)), gbc);
            
            // Separator
            gbc.gridy = row++;
            statisticsPanel.add(createSeparator(), gbc);
            
            // Status Breakdown (only if harvests exist)
            if (harvestCount > 0) {
                gbc.gridy = row++;
                statisticsPanel.add(createSectionLabel("Harvest Status Breakdown"), gbc);
                
                gbc.gridy = row++;
                statisticsPanel.add(createStatLabel("Available", String.valueOf(availableCount)), gbc);
                
                gbc.gridy = row++;
                statisticsPanel.add(createStatLabel("Interested", String.valueOf(interestedCount)), gbc);
                
                gbc.gridy = row++;
                statisticsPanel.add(createStatLabel("Sold Out", String.valueOf(soldOutCount)), gbc);
            }
            
            // WHAT: Repaint panel to show new statistics
            // WHY: Panel needs to refresh to display new labels
            // HOW: revalidate() and repaint() update the display
            statisticsPanel.revalidate();
            statisticsPanel.repaint();
            
        } catch (Exception ex) {
            // WHAT: Handle database errors
            // WHY: Database operations can fail
            // HOW: catch block executes when exception occurs
            JOptionPane.showMessageDialog(this, "Error loading statistics: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * createStatLabel() - Creates a formatted statistics label
     * WHAT: Creates JLabel with label and value in formatted layout
     * WHY: Consistent formatting for all statistics
     * HOW: Creates JPanel with two labels (label and value)
     * @param label Label text (e.g., "Total Items")
     * @param value Value text (e.g., "25")
     * @return JPanel containing formatted statistics label
     */
    private JPanel createStatLabel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueComponent.setForeground(new Color(46, 125, 50)); // Green
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * createSectionLabel() - Creates a section header label
     * WHAT: Creates bold label for section headers
     * WHY: Separates different statistic groups
     * HOW: Creates JLabel with bold font
     * @param text Section header text
     * @return JLabel formatted as section header
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(46, 125, 50)); // Green
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        return label;
    }
    
    /**
     * createSeparator() - Creates a visual separator
     * WHAT: Creates horizontal line separator
     * WHY: Visually separates statistic groups
     * HOW: Creates JPanel with line border
     * @return JPanel formatted as separator
     */
    private JPanel createSeparator() {
        JPanel separator = new JPanel();
        separator.setBackground(new Color(200, 200, 200));
        separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }
    
    /**
     * actionPerformed() - Handles all button click events
     * WHAT: Called automatically when any registered button is clicked
     * WHY: Implements ActionListener interface - required for event handling
     * HOW: Checks event source and performs appropriate action
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            loadStatistics();
        } else if (e.getSource() == closeButton) {
            confirmClose();
        }
    }
}






