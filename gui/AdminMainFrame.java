package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.User; // Import: User model class

/**
 * AdminMainFrame - Admin-Specific Dashboard Window
 * WHAT: Main window for ADMIN role users, provides access to user management and inventory viewing
 * WHY: Admins need different features than sellers/buyers - centralized admin functions
 * HOW: Displays admin-specific buttons, implements ActionListener for button clicks
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener to handle user interactions
 */
public class AdminMainFrame extends JFrame implements ActionListener {
    // WHAT: Currently logged-in admin user object
    // WHY: Needed to pass user information to other windows and display welcome message
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Button to manage users (view, edit, delete user accounts)
    // WHY: Admins need to manage user accounts
    // HOW: Opens user management interface when clicked (currently shows placeholder message)
    private JButton viewUsersButton;
    
    // WHAT: Button to view inventory records
    // WHY: Admins need to view all inventory items
    // HOW: Opens RecordsWindow when clicked
    private JButton viewInventoryButton;
    
    // WHAT: Button to logout and return to login screen
    // WHY: Allows admin to switch accounts or end session
    // HOW: Shows confirmation, closes window, opens LoginFrame
    private JButton logoutButton;
    
    /**
     * Constructor - Creates and displays the AdminMainFrame window
     * WHAT: Initializes components, sets up layout, configures window properties
     * WHY: Called after admin login to show admin dashboard
     * HOW: Calls helper methods to set up GUI, then displays window
     * @param user Authenticated User object with ADMIN role
     */
    public AdminMainFrame(User user) {
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
        
        // WHAT: Set default close operation to exit application
        // WHY: Closing admin window exits entire application
        // HOW: EXIT_ON_CLOSE closes window and terminates application
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // WHAT: Set window title bar text
        // WHY: Identifies window in taskbar
        // HOW: setTitle() sets title text
        setTitle("AgriTrack - Admin Dashboard");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 800x600 pixels
        // WHY: Provides adequate space for admin dashboard
        // HOW: setSize() sets width and height
        setSize(800, 600);
        
        // WHAT: Center window on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(null) centers window
        setLocationRelativeTo(null);
        
        // WHAT: Make window visible
        // WHY: Window is hidden by default
        // HOW: setVisible(true) displays window
        setVisible(true);
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates buttons, styles them, and attaches event listeners
     * WHY: Components must be created before adding to layout
     * HOW: Creates JButton objects, calls styleButton(), adds ActionListener
     */
    private void initializeComponents() {
        // WHAT: Create view users button
        // WHY: Admins need button to manage user accounts
        // HOW: JButton constructor takes button text
        viewUsersButton = new JButton("👥 Manage Users");
        
        // WHAT: Create view inventory button
        // WHY: Admins need button to view inventory
        // HOW: JButton constructor
        viewInventoryButton = new JButton("View Inventory");
        
        // WHAT: Create logout button
        // WHY: Admins need button to logout
        // HOW: JButton constructor
        logoutButton = new JButton("Logout");
        
        // WHAT: Style buttons with different colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(viewUsersButton, new Color(156, 39, 176)); // Purple
        styleButton(viewInventoryButton, new Color(33, 150, 243)); // Blue
        styleButton(logoutButton, new Color(158, 158, 158)); // Gray
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        viewUsersButton.addActionListener(this);
        viewInventoryButton.addActionListener(this);
        logoutButton.addActionListener(this);
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set font
        button.setFocusPainted(false); // Remove focus border
        button.setBorderPainted(false); // Remove button border
        button.setPreferredSize(new Dimension(250, 50)); // Set button size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
    }
    
    /**
     * setupLayout() - Arranges components in window
     * WHAT: Creates layout structure with header, center buttons, and footer
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure, GridBagLayout for buttons
     */
    private void setupLayout() {
        setLayout(new BorderLayout()); // Set main layout
        getContentPane().setBackground(new Color(245, 245, 250)); // Set background color
        
        // WHAT: Create and add header panel
        // WHY: Header provides branding
        // HOW: createHeaderPanel() returns JPanel, add to NORTH
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create center panel with GridBagLayout
        // WHY: GridBagLayout allows precise button positioning
        // HOW: JPanel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 250));
        centerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // WHAT: Create constraints for component positioning
        // WHY: GridBagLayout requires constraints
        // HOW: GridBagConstraints object
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion
        
        // WHAT: Add view users button at position (0,0)
        // WHY: First button in dashboard
        // HOW: gridx=0, gridy=0, add() adds component
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(viewUsersButton, gbc);
        
        // WHAT: Add view inventory button at position (0,1)
        // WHY: Second button below first
        // HOW: gridy=1 moves to next row
        gbc.gridy = 1;
        centerPanel.add(viewInventoryButton, gbc);
        
        // WHAT: Wrap center panel in scroll pane for scrollability
        // WHY: Content should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps centerPanel with scroll policies
        JScrollPane centerScrollPane = new JScrollPane(centerPanel);
        centerScrollPane.setBorder(null);
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.getViewport().setBackground(new Color(245, 245, 250));
        
        // WHAT: Add scroll pane to window center
        // WHY: Scrollable content should be in center
        // HOW: BorderLayout.CENTER positions in center
        add(centerScrollPane, BorderLayout.CENTER);
        
        // WHAT: Create footer panel with logout button
        // WHY: Footer holds secondary actions
        // HOW: JPanel with FlowLayout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 245, 250));
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 20));
        footerPanel.add(logoutButton);
        
        // WHAT: Add footer to bottom of window
        // WHY: Footer should appear at bottom
        // HOW: BorderLayout.SOUTH positions at bottom
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * createHeaderPanel() - Creates header with title and user info
     * WHAT: Builds green header panel with application title and welcome message
     * WHY: Provides visual branding and shows logged-in admin
     * HOW: Uses BorderLayout to stack title and user label
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()); // Create header panel
        header.setBackground(new Color(46, 125, 50)); // Green background
        header.setBorder(new EmptyBorder(20, 30, 20, 30)); // Padding
        
        JLabel title = new JLabel("Admin Dashboard"); // Create title label
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
            title.setText("Admin Dashboard");
        }
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName()); // Create user label
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        userLabel.setForeground(new Color(200, 230, 201)); // Light green text
        
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
        // WHAT: Check if view users button was clicked
        // WHY: Need to open user management interface
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == viewUsersButton) {
            // WHAT: Show placeholder message (feature not yet implemented)
            // WHY: User management feature is planned but not implemented
            // HOW: showMessageDialog() displays information message
            JOptionPane.showMessageDialog(this, 
                "User management feature - To be implemented", // Message text
                "Info", // Dialog title
                JOptionPane.INFORMATION_MESSAGE); // Information icon
        } 
        // WHAT: Check if view inventory button was clicked
        // WHY: Need to open inventory records window
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == viewInventoryButton) {
            // WHAT: Open records management window
            // WHY: Admin wants to view inventory
            // HOW: new RecordsWindow() creates and displays window
            new RecordsWindow(currentUser);
        } 
        // WHAT: Check if logout button was clicked
        // WHY: Need to logout admin
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
                // WHAT: Close admin window
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
