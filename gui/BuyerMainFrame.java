package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.User; // Import: User model class

/**
 * BuyerMainFrame - Buyer-Specific Dashboard Window
 * WHAT: Main window for BUYER role users, provides access to browsing harvests and exporting data
 * WHY: Buyers need different features than sellers/admins - focused on viewing and purchasing harvests
 * HOW: Displays buyer-specific buttons, implements ActionListener for button clicks
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener to handle user interactions
 */
public class BuyerMainFrame extends JFrame implements ActionListener {
    // WHAT: Currently logged-in buyer user object
    // WHY: Needed to pass user information to other windows and display welcome message
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Button to browse available harvest records
    // WHY: Buyers need to view and purchase available harvests
    // HOW: Opens RecordsWindow when clicked
    private JButton browseHarvestsButton;
    
    // WHAT: Button to export inventory data to CSV file
    // WHY: Buyers may want to export data for analysis or backup
    // HOW: Opens RecordsWindow and triggers CSV export when clicked
    private JButton exportDataButton;
    
    // WHAT: Button to open user profile dialog
    // WHY: Buyers need to set their location for delivery
    // HOW: Opens UserProfileDialog when clicked
    private JButton profileButton;
    
    // WHAT: Button to logout and return to login screen
    // WHY: Allows buyer to switch accounts or end session
    // HOW: Shows confirmation, closes window, opens LoginFrame
    private JButton logoutButton;
    
    /**
     * Constructor - Creates and displays the BuyerMainFrame window
     * WHAT: Initializes components, sets up layout, configures window properties
     * WHY: Called after buyer login to show buyer dashboard
     * HOW: Calls helper methods to set up GUI, then displays window
     * @param user Authenticated User object with BUYER role
     */
    public BuyerMainFrame(User user) {
        // WHAT: Store user object in instance variable
        // WHY: Needed for displaying user info and passing to other windows
        // HOW: this.currentUser = user assigns parameter to instance field
        this.currentUser = user;
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in window
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Prevent default close behavior (immediate exit)
        // WHY: Want to show confirmation dialog before closing
        // HOW: DO_NOTHING_ON_CLOSE prevents automatic closing
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // WHAT: Set window title bar text
        // WHY: Identifies window in taskbar
        // HOW: setTitle() sets title text
        setTitle("AgriTrack - Buyer Dashboard");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 800x650 pixels
        // WHY: Provides adequate space for buyer dashboard with profile button
        // HOW: setSize() sets width and height
        setSize(800, 650);
        
        // WHAT: Center window on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(null) centers window
        setLocationRelativeTo(null);
        
        // WHAT: Add window close listener for confirmation dialog
        // WHY: Prevents accidental application exit
        // HOW: WindowAdapter overrides windowClosing() method
        addWindowListener(new WindowAdapter() {
            @Override
            // WHAT: Called when user clicks window close button (X)
            // WHY: Intercepts close event to show confirmation
            // HOW: Override windowClosing() to call confirmExit()
            public void windowClosing(WindowEvent e) {
                confirmExit(); // Show confirmation dialog
            }
        });
        
        // WHAT: Make window visible
        // WHY: Window is hidden by default
        // HOW: setVisible(true) displays window
        setVisible(true);
    }
    
    /**
     * confirmExit() - Shows confirmation dialog before exiting
     * WHAT: Displays Yes/No dialog asking user to confirm exit
     * WHY: Prevents accidental application closure
     * HOW: Uses JOptionPane to show dialog, exits only if user clicks Yes
     */
    private void confirmExit() {
        // WHAT: Show confirmation dialog
        // WHY: Gives user chance to cancel exit
        // HOW: showConfirmDialog() displays modal dialog
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to exit the application?", // Message text
            "Confirm Exit", // Dialog title
            JOptionPane.YES_NO_OPTION, // Show Yes and No buttons
            JOptionPane.QUESTION_MESSAGE); // Question icon
        
        // WHAT: Check if user clicked Yes
        // WHY: Only exit if user confirms
        // HOW: YES_OPTION constant returned when Yes clicked
        if (confirm == JOptionPane.YES_OPTION) {
            // WHAT: Terminate Java application
            // WHY: User confirmed exit
            // HOW: System.exit(0) stops JVM
            System.exit(0);
        }
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates buttons, styles them, and attaches event listeners
     * WHY: Components must be created before adding to layout
     * HOW: Creates JButton objects, calls styleButton(), adds ActionListener
     */
    private void initializeComponents() {
        // WHAT: Create browse harvests button
        // WHY: Buyers need button to view available harvests
        // HOW: JButton constructor takes button text
        browseHarvestsButton = new JButton("Browse Available Harvests");
        
        // WHAT: Create export data button
        // WHY: Buyers may want to export data to CSV
        // HOW: JButton constructor
        exportDataButton = new JButton("Export to CSV");
        
        // WHAT: Create profile button
        // WHY: Buyers need button to set location
        // HOW: JButton constructor
        profileButton = new JButton("My Profile");
        
        // WHAT: Create logout button
        // WHY: Buyers need button to logout
        // HOW: JButton constructor
        logoutButton = new JButton("Logout");
        
        // WHAT: Style buttons with different colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(browseHarvestsButton, new Color(33, 150, 243), new Color(25, 118, 210)); // Blue
        styleButton(exportDataButton, new Color(255, 152, 0), new Color(230, 126, 34)); // Orange
        styleButton(profileButton, new Color(156, 39, 176), new Color(123, 31, 162)); // Purple
        styleButton(logoutButton, new Color(158, 158, 158), new Color(117, 117, 117)); // Gray
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        browseHarvestsButton.addActionListener(this);
        exportDataButton.addActionListener(this);
        profileButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }
    
    /**
     * styleButton() - Applies professional styling to buttons
     * WHAT: Sets button colors, fonts, sizes, borders, and hover effects
     * WHY: Consistent, professional button appearance
     * HOW: Calls setter methods on JButton and adds hover effects
     * @param button Button to style
     * @param bgColor Background color for button
     * @param hoverColor Color for hover effect
     */
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(hoverColor, 1, true),
            new javax.swing.border.EmptyBorder(12, 20, 12, 20)
        ));
        button.setPreferredSize(new Dimension(300, 55));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    /**
     * setupLayout() - Arranges components in window
     * WHAT: Creates layout structure with header, center buttons, and footer
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure, GridBagLayout for buttons
     */
    private void setupLayout() {
        setLayout(new BorderLayout()); // Set main layout
        getContentPane().setBackground(new Color(248, 249, 250)); // Set professional background color
        
        // WHAT: Create and add header panel
        // WHY: Header provides branding
        // HOW: createHeaderPanel() returns JPanel, add to NORTH
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create center panel with GridBagLayout
        // WHY: GridBagLayout allows precise button positioning
        // HOW: JPanel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 249, 250));
        centerPanel.setBorder(new EmptyBorder(50, 40, 50, 40));
        
        // WHAT: Create constraints for component positioning
        // WHY: GridBagLayout requires constraints
        // HOW: GridBagConstraints object
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion
        
        // WHAT: Add browse harvests button at position (0,0)
        // WHY: First button in dashboard
        // HOW: gridx=0, gridy=0, add() adds component
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(browseHarvestsButton, gbc);
        
        // WHAT: Add export data button at position (0,1)
        // WHY: Second button below first
        // HOW: gridy=1 moves to next row
        gbc.gridy = 1;
        centerPanel.add(exportDataButton, gbc);
        
        // WHAT: Wrap center panel in scroll pane for scrollability
        // WHY: Content should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps centerPanel with scroll policies
        JScrollPane centerScrollPane = new JScrollPane(centerPanel);
        centerScrollPane.setBorder(null);
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.getViewport().setBackground(new Color(248, 249, 250));
        
        // WHAT: Add scroll pane to window center
        // WHY: Scrollable content should be in center
        // HOW: BorderLayout.CENTER positions in center
        add(centerScrollPane, BorderLayout.CENTER);
        
        // WHAT: Create footer panel with logout button
        // WHY: Footer holds secondary actions
        // HOW: JPanel with FlowLayout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 20)
        ));
        footerPanel.add(logoutButton);
        
        // WHAT: Add footer to bottom of window
        // WHY: Footer should appear at bottom
        // HOW: BorderLayout.SOUTH positions at bottom
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * createHeaderPanel() - Creates header with title and user info
     * WHAT: Builds green header panel with application title and welcome message
     * WHY: Provides visual branding and shows logged-in buyer
     * HOW: Uses BorderLayout to stack title and user label
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()); // Create header panel
        header.setBackground(new Color(46, 125, 50)); // Green background
        header.setBorder(new EmptyBorder(25, 35, 25, 35)); // Professional padding
        
        JLabel title = new JLabel("Buyer Dashboard"); // Create title label
        title.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Professional larger font
        title.setForeground(Color.WHITE); // White text
        // WHAT: Add application icon to title label
        // WHY: Visual branding with logo
        // HOW: setIcon() adds icon to label
        try {
            title.setIcon(util.IconUtil.getApplicationIcon());
            title.setIconTextGap(10); // Space between icon and text
            title.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of icon
        } catch (Exception e) {
            // Icon loading failed, use emoji fallback
            title.setText("Buyer Dashboard");
        }
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName()); // Create user label
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        userLabel.setForeground(new Color(220, 240, 220)); // Lighter green text for better contrast
        
        JPanel leftPanel = new JPanel(new BorderLayout()); // Panel to hold title and user label
        leftPanel.setBackground(new Color(46, 125, 50));
        leftPanel.add(title, BorderLayout.CENTER); // Title in center
        leftPanel.add(userLabel, BorderLayout.SOUTH); // User label below
        
        header.add(leftPanel, BorderLayout.WEST); // Add to header
        return header; // Return completed header
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
        // WHAT: Check if browse harvests button was clicked
        // WHY: Need to open records window for browsing
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == browseHarvestsButton) {
            // WHAT: Open records management window
            // WHY: Buyer wants to browse available harvests
            // HOW: new RecordsWindow() creates and displays window
            new RecordsWindow(currentUser);
        } 
        // WHAT: Check if export data button was clicked
        // WHY: Need to export data to CSV file
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == exportDataButton) {
            // WHAT: Create records window and trigger CSV export
            // WHY: Export functionality is in RecordsWindow class
            // HOW: Create RecordsWindow instance, call exportToCSV() method
            RecordsWindow rw = new RecordsWindow(currentUser); // Create window
            rw.exportToCSV(); // Trigger export immediately
        }
        // WHAT: Check if profile button was clicked
        // WHY: Need to open profile dialog
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == profileButton) {
            // WHAT: Open user profile dialog
            // WHY: Buyer wants to set location information
            // HOW: new UserProfileDialog() creates and displays dialog
            new UserProfileDialog(this, currentUser);
        }
        // WHAT: Check if logout button was clicked
        // WHY: Need to logout buyer
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == logoutButton) {
            // WHAT: Show logout confirmation dialog
            // WHY: Prevents accidental logout
            // HOW: showConfirmDialog() displays Yes/No dialog
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", // Message
                "Confirm Logout", // Title
                JOptionPane.YES_NO_OPTION); // Buttons
            
            // WHAT: Check if user confirmed logout
            // WHY: Only logout if user confirms
            // HOW: YES_OPTION constant returned when Yes clicked
            if (confirm == JOptionPane.YES_OPTION) {
                // WHAT: Close buyer window
                // WHY: User is logging out
                // HOW: dispose() closes and destroys window
                this.dispose();
                
                // WHAT: Open login window
                // WHY: Return to login screen after logout
                // HOW: new LoginFrame() creates and displays login window
                new LoginFrame();
            }
        }
    }
}
