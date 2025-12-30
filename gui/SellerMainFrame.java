package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.User; // Import: User model class

/**
 * SellerMainFrame - Seller-Specific Dashboard Window
 * WHAT: Main window for SELLER role users, provides access to harvest record management
 * WHY: Sellers need different features than buyers/admins - focused on adding and managing harvests
 * HOW: Displays seller-specific buttons, implements ActionListener for button clicks
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener to handle user interactions
 */
public class SellerMainFrame extends JFrame implements ActionListener {
    // WHAT: Currently logged-in seller user object
    // WHY: Needed to pass user information to other windows and display welcome message
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Button to open harvest form for adding new harvest records
    // WHY: Sellers need to add new harvest records to inventory
    // HOW: Opens HarvestForm dialog when clicked
    private JButton addHarvestButton;
    
    // WHAT: Button to view all harvest records
    // WHY: Sellers need to view and manage their harvest records
    // HOW: Opens RecordsWindow when clicked
    private JButton viewRecordsButton;
    
    // WHAT: Button to open user profile dialog
    // WHY: Sellers need to set their location/warehouse information
    // HOW: Opens UserProfileDialog when clicked
    private JButton profileButton;
    
    // WHAT: Button to logout and return to login screen
    // WHY: Allows seller to switch accounts or end session
    // HOW: Shows confirmation, closes window, opens LoginFrame
    private JButton logoutButton;
    
    /**
     * Constructor - Creates and displays the SellerMainFrame window
     * WHAT: Initializes components, sets up layout, configures window properties
     * WHY: Called after seller login to show seller dashboard
     * HOW: Calls helper methods to set up GUI, then displays window
     * @param user Authenticated User object with SELLER role
     */
    public SellerMainFrame(User user) {
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
        // WHY: Closing seller window exits entire application
        // HOW: EXIT_ON_CLOSE closes window and terminates application
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // WHAT: Set window title bar text
        // WHY: Identifies window in taskbar
        // HOW: setTitle() sets title text
        setTitle("AgriTrack - Seller Dashboard");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 800x650 pixels
        // WHY: Provides adequate space for seller dashboard with profile button
        // HOW: setSize() sets width and height
        setSize(800, 650);
        
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
        // WHAT: Create add harvest button
        // WHY: Sellers need button to add new harvest records
        // HOW: JButton constructor takes button text
        addHarvestButton = new JButton("Add Harvest Record");
        
        // WHAT: Create view records button
        // WHY: Sellers need button to view their harvest records
        // HOW: JButton constructor
        viewRecordsButton = new JButton("View My Records");
        
        // WHAT: Create profile button
        // WHY: Sellers need button to set location
        // HOW: JButton constructor
        profileButton = new JButton("My Profile");
        
        // WHAT: Create logout button
        // WHY: Sellers need button to logout
        // HOW: JButton constructor
        logoutButton = new JButton("Logout");
        
        // WHAT: Style buttons with different colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(addHarvestButton, new Color(76, 175, 80), new Color(56, 142, 60)); // Green
        styleButton(viewRecordsButton, new Color(33, 150, 243), new Color(25, 118, 210)); // Blue
        styleButton(profileButton, new Color(156, 39, 176), new Color(123, 31, 162)); // Purple
        styleButton(logoutButton, new Color(158, 158, 158), new Color(117, 117, 117)); // Gray
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        addHarvestButton.addActionListener(this);
        viewRecordsButton.addActionListener(this);
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
        button.setPreferredSize(new Dimension(250, 55));
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
        
        // WHAT: Add add harvest button at position (0,0)
        // WHY: First button in dashboard
        // HOW: gridx=0, gridy=0, add() adds component
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(addHarvestButton, gbc);
        
        // WHAT: Add view records button at position (0,1)
        // WHY: Second button below first
        // HOW: gridy=1 moves to next row
        gbc.gridy = 1;
        centerPanel.add(viewRecordsButton, gbc);
        
        // WHAT: Add profile button at position (0,2)
        // WHY: Third button below second
        // HOW: gridy=2 moves to next row
        gbc.gridy = 2;
        centerPanel.add(profileButton, gbc);
        
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
     * WHY: Provides visual branding and shows logged-in seller
     * HOW: Uses BorderLayout to stack title and user label
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()); // Create header panel
        header.setBackground(new Color(46, 125, 50)); // Green background
        header.setBorder(new EmptyBorder(25, 35, 25, 35)); // Professional padding
        
        JLabel title = new JLabel("Seller Dashboard"); // Create title label
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
            title.setText("Seller Dashboard");
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
        // WHAT: Check if add harvest button was clicked
        // WHY: Need to open harvest form for adding new record
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == addHarvestButton) {
            // WHAT: Open harvest form dialog
            // WHY: Seller wants to add new harvest record
            // HOW: new HarvestForm() creates and displays dialog
            new HarvestForm(currentUser, this);
        } 
        // WHAT: Check if view records button was clicked
        // WHY: Need to open records window
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == viewRecordsButton) {
            // WHAT: Open records management window
            // WHY: Seller wants to view their harvest records
            // HOW: new RecordsWindow() creates and displays window
            new RecordsWindow(currentUser);
        }
        // WHAT: Check if profile button was clicked
        // WHY: Need to open profile dialog
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == profileButton) {
            // WHAT: Open user profile dialog
            // WHY: Seller wants to set location information
            // HOW: new UserProfileDialog() creates and displays dialog
            new UserProfileDialog(this, currentUser);
        }
        // WHAT: Check if logout button was clicked
        // WHY: Need to logout seller
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
                // WHAT: Close seller window
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
