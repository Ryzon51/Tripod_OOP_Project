package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.awt.event.MouseAdapter; // Import: MouseAdapter for hover effects
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import javax.swing.table.DefaultTableModel; // Import: DefaultTableModel for table data
import model.User; // Import: User model class
import java.io.File; // Import: File for checking if logo file exists
import javax.swing.BorderFactory; // Import: BorderFactory for creating borders
import util.ThemeColors; // Import: ThemeColors for consistent color theming

/**
 * MainMenuFrame - Professional Main Navigation Hub After Login
 * WHAT: Central menu window that provides access to registration form and records management
 * WHY: Acts as main navigation point - users can access key features from here
 * HOW: Displays buttons for registration and records, implements ActionListener for button clicks
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener to handle user interactions
 */
public class MainMenuFrame extends JFrame implements ActionListener {
    // WHAT: Currently logged-in user object
    // WHY: Needed to pass user information to other windows and display welcome message
    // HOW: Stored as instance variable, passed from LoginFrame constructor
    private User currentUser;
    
    // WHAT: Button to open registration form dialog
    // WHY: Allows users (especially admins) to create new user accounts
    // HOW: Opens RegistrationForm dialog when clicked
    private JButton registrationFormButton;
    
    // WHAT: Button to open records management window
    // WHY: Main way to access inventory records (view, add, edit, delete)
    // HOW: Opens RecordsWindow when clicked
    private JButton manageRecordsButton;
    
    // WHAT: Button to open reports and statistics window
    // WHY: Users need access to reports and statistics
    // HOW: Opens ReportsWindow when clicked
    private JButton reportsButton;
    
    // WHAT: Button to open database viewer dialog
    // WHY: Users need to view all database tables and their contents
    // HOW: Opens DatabaseViewerDialog when clicked
    private JButton databaseViewerButton;
    
    // WHAT: Button to exit the application
    // WHY: Provides way to close application
    // HOW: Shows confirmation dialog, exits if user confirms
    private JButton exitButton;
    
    // WHAT: Button to logout and return to login screen
    // WHY: Allows users to switch accounts or end session
    // HOW: Shows confirmation, closes window, opens LoginFrame
    private JButton logoutButton;
    
    // WHAT: CardLayout for switching between different content panels
    // WHY: Allows dynamic content switching in right panel
    // HOW: CardLayout manages multiple panels, shows one at a time
    private CardLayout cardLayout;
    
    // WHAT: Main content panel that holds all card panels
    // WHY: Container for CardLayout to manage different views
    // HOW: JPanel with CardLayout
    private JPanel contentCardPanel;
    
    // WHAT: Panel for displaying welcome/home content
    // WHY: Default view when no menu item is selected
    // HOW: JPanel with welcome message
    private JPanel homePanel;
    
    // WHAT: Panel for registration form content
    // WHY: Embedded registration form in right panel
    // HOW: JPanel containing registration form components
    private JPanel registrationPanel;
    
    // WHAT: Panel for records management content
    // WHY: Embedded records window in right panel
    // HOW: JPanel containing records table and controls
    private JPanel recordsPanel;
    
    // WHAT: Panel for reports content
    // WHY: Embedded reports window in right panel
    // HOW: JPanel containing reports and statistics
    private JPanel reportsPanel;
    
    // WHAT: Title label that changes based on current view
    // WHY: Shows current section name
    // HOW: JLabel that updates when cards switch
    private JLabel contentTitle;
    
    /**
     * Constructor - Creates and displays the MainMenuFrame window
     * WHAT: Initializes components, sets up layout, configures window properties
     * WHY: Called after successful login to show main menu
     * HOW: Calls helper methods to set up GUI, then displays window
     * @param user Authenticated User object from login
     */
    public MainMenuFrame(User user) {
        // WHAT: Store user object in instance variable
        // WHY: Needed for displaying user info and passing to other windows
        // HOW: this.currentUser = user assigns parameter to instance field
        this.currentUser = user;
        
        // WHAT: Initialize all GUI components (buttons, labels, etc.)
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in window using layout managers
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
        setTitle("AgriTrack - Main Menu");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 1000x700 pixels for two-pane layout
        // WHY: Provides adequate space for sidebar and content area
        // HOW: setSize() sets width and height
        setSize(1000, 700);
        
        // WHAT: Set minimum window size to prevent UI distortion
        // WHY: Maintains professional appearance at all sizes
        // HOW: setMinimumSize() sets minimum dimensions
        setMinimumSize(new Dimension(900, 600));
        
        // WHAT: Center window on screen
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(null) centers window
        setLocationRelativeTo(null);
        
        // WHAT: Allow user to resize the window
        // WHY: Users should be able to minimize, maximize, and resize for better UX
        // HOW: setResizable(true) enables window resize handles and minimize/maximize buttons
        setResizable(true);
        
        // WHAT: Enable window state change handling for better responsiveness
        // WHY: Ensures layout adapts properly when window is maximized or restored
        // HOW: Add window state listener to handle state changes
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            @Override
            public void windowStateChanged(java.awt.event.WindowEvent e) {
                // WHAT: Revalidate and repaint when window state changes
                // WHY: Ensures components are properly laid out after state change
                // HOW: revalidate() recalculates layout, repaint() refreshes display
                revalidate();
                repaint();
            }
        });
        
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
        // WHAT: Create registration form button with simple text
        // WHY: Users need button to open registration form
        // HOW: JButton constructor takes button text
        registrationFormButton = new JButton("Registration Form");
        
        // WHAT: Create manage records button
        // WHY: Users need button to access inventory records
        // HOW: JButton constructor
        manageRecordsButton = new JButton("Manage Records");
        
        // WHAT: Create reports button
        // WHY: Users need button to view reports and statistics
        // HOW: JButton constructor
        reportsButton = new JButton("View Reports");
        
        // WHAT: Create database viewer button
        // WHY: Users need button to view all database tables
        // HOW: JButton constructor
        databaseViewerButton = new JButton("View Database");
        
        // WHAT: Create exit button
        // WHY: Users need button to exit application
        // HOW: JButton constructor
        exitButton = new JButton("Exit");
        
        // WHAT: Create logout button (will be used as "Back" in sidebar)
        // WHY: Users need button to logout
        // HOW: JButton constructor
        logoutButton = new JButton("Back");
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        registrationFormButton.addActionListener(this);
        manageRecordsButton.addActionListener(this);
        reportsButton.addActionListener(this);
        databaseViewerButton.addActionListener(this);
        exitButton.addActionListener(this);
        logoutButton.addActionListener(this);
    }
    
    
    /**
     * setupLayout() - Arranges components in two-pane layout (left sidebar + right content)
     * WHAT: Creates layout structure matching the reference design with left navigation and right content area
     * WHY: Components must be organized visually with professional spacing matching the design
     * HOW: Uses BorderLayout for main structure, creates left sidebar and right content panels
     */
    private void setupLayout() {
        // WHAT: Set main window layout to BorderLayout
        // WHY: BorderLayout divides window into regions
        // HOW: setLayout() sets layout manager
        setLayout(new BorderLayout());
        
        // WHAT: Set window background color to white
        // WHY: Clean white background for content area
        // HOW: getContentPane().setBackground() sets color
        getContentPane().setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Create left sidebar panel for navigation
        // WHY: Left sidebar provides navigation menu matching the design
        // HOW: createLeftSidebar() returns styled sidebar panel
        JPanel leftSidebar = createLeftSidebar();
        
        // WHAT: Add left sidebar to west (left) of window
        // WHY: Sidebar should appear on left side
        // HOW: BorderLayout.WEST positions on left
        add(leftSidebar, BorderLayout.WEST);
        
        // WHAT: Create right content panel for main content
        // WHY: Right panel displays main content matching the design
        // HOW: createRightContentPanel() returns styled content panel
        JPanel rightContent = createRightContentPanel();
        
        // WHAT: Add right content panel to center of window
        // WHY: Content should fill remaining space
        // HOW: BorderLayout.CENTER positions in center
        add(rightContent, BorderLayout.CENTER);
    }
    
    /**
     * createLeftSidebar() - Creates left navigation sidebar
     * WHAT: Builds left sidebar with MENU header, navigation buttons, and back button
     * WHY: Provides navigation menu matching the reference design
     * HOW: Uses vertical BoxLayout for stacking components
     * @return JPanel containing sidebar components
     */
    private JPanel createLeftSidebar() {
        // WHAT: Create sidebar panel with vertical BoxLayout
        // WHY: BoxLayout stacks components vertically
        // HOW: BoxLayout with PAGE_AXIS for vertical stacking
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));
        
        // WHAT: Set sidebar background to light blue/gray-blue
        // WHY: Matches reference design color scheme
        // HOW: setBackground() applies color (Color(240, 248, 255) or Color(227, 242, 253))
        sidebar.setBackground(ThemeColors.SIDEBAR_BG);
        
        // WHAT: Add padding around sidebar content
        // WHY: Prevents content from touching edges
        // HOW: setBorder() with EmptyBorder adds padding
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // WHAT: Create MENU header label
        // WHY: Identifies navigation section matching the design
        // HOW: JLabel with bold, uppercase text
        JLabel menuHeader = new JLabel("MENU");
        menuHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuHeader.setForeground(ThemeColors.SIDEBAR_TEXT);
        menuHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuHeader.setBorder(new EmptyBorder(0, 0, 20, 0)); // Bottom margin
        
        // WHAT: Add MENU header to sidebar
        // WHY: Header identifies navigation section
        // HOW: add() adds component to panel
        sidebar.add(menuHeader);
        
        // WHAT: Add vertical spacing
        // WHY: Creates space between header and buttons
        // HOW: Box.createVerticalStrut() creates fixed vertical space
        sidebar.add(Box.createVerticalStrut(10));
        
        // WHAT: Style and add registration form button
        // WHY: Navigation button for registration feature
        // HOW: styleSidebarButton() applies teal/cyan styling, add to sidebar
        styleSidebarButton(registrationFormButton);
        registrationFormButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(registrationFormButton);
        
        // WHAT: Add vertical spacing between buttons
        // WHY: Professional spacing between navigation buttons
        // HOW: Box.createVerticalStrut() creates space
        sidebar.add(Box.createVerticalStrut(15));
        
        // WHAT: Style and add manage records button
        // WHY: Navigation button for records management
        // HOW: styleSidebarButton() applies teal/cyan styling, add to sidebar
        styleSidebarButton(manageRecordsButton);
        manageRecordsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(manageRecordsButton);
        
        // WHAT: Add vertical spacing between buttons
        // WHY: Professional spacing between navigation buttons
        // HOW: Box.createVerticalStrut() creates space
        sidebar.add(Box.createVerticalStrut(15));
        
        // WHAT: Style and add reports button (only for ADMIN users)
        // WHY: Reports feature should only be accessible to administrators
        // HOW: Check user role, only add button if ADMIN
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            styleSidebarButton(reportsButton);
            reportsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(reportsButton);
        }
        
        // WHAT: Add flexible vertical space to push back button down
        // WHY: Back button should be at bottom of sidebar
        // HOW: Box.createVerticalGlue() creates expandable space
        sidebar.add(Box.createVerticalGlue());
        
        // WHAT: Style and add back button (logout)
        // WHY: Secondary action button at bottom matching the design
        // HOW: styleBackButton() applies dark blue/gray styling, add to sidebar
        styleBackButton(logoutButton);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoutButton);
        
        // WHAT: Set preferred sidebar width to approximately 25% of window
        // WHY: Sidebar should occupy about one-quarter of total width
        // HOW: setPreferredSize() sets width to 250 pixels
        sidebar.setPreferredSize(new Dimension(250, 0));
        
        // WHAT: Return completed sidebar panel
        // WHY: Caller needs panel to add to window
        // HOW: return statement
        return sidebar;
    }
    
    /**
     * createRightContentPanel() - Creates right content area with CardLayout (responsive)
     * WHAT: Builds right content panel with dynamic content switching using CardLayout, with responsive centering
     * WHY: Displays different content panels based on menu selection, maintains centered layout when resized
     * HOW: Uses BorderLayout for structure, CardLayout for content switching, wraps content in centered container
     * @return JPanel containing content components
     */
    private JPanel createRightContentPanel() {
        // WHAT: Create outer content panel with BorderLayout for responsive centering
        // WHY: BorderLayout allows centering the content when window is resized
        // HOW: JPanel with BorderLayout
        JPanel outerContentPanel = new JPanel(new BorderLayout());
        outerContentPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create inner content panel with BorderLayout
        // WHY: BorderLayout allows title at top, content in center
        // HOW: JPanel with BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // WHAT: Set content background to white
        // WHY: Clean white background matching the design
        // HOW: setBackground() applies white color
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for content panel to prevent excessive stretching
        // WHY: Content should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        contentPanel.setMaximumSize(new Dimension(1200, Integer.MAX_VALUE));
        contentPanel.setPreferredSize(new Dimension(1000, 0));
        
        // WHAT: Create title label for content area (will update dynamically)
        // WHY: Identifies current content section
        // HOW: JLabel with large, bold text
        contentTitle = new JLabel("MAIN MENU");
        contentTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        contentTitle.setForeground(ThemeColors.TEXT_PRIMARY);
        contentTitle.setBorder(new EmptyBorder(30, 40, 20, 40)); // Padding around title
        
        // WHAT: Add title to top of content panel
        // WHY: Title should appear at top
        // HOW: BorderLayout.NORTH positions at top
        contentPanel.add(contentTitle, BorderLayout.NORTH);
        
        // WHAT: Create CardLayout for dynamic content switching
        // WHY: Allows switching between different content panels
        // HOW: CardLayout manages multiple panels, shows one at a time
        cardLayout = new CardLayout();
        
        // WHAT: Create main card panel with CardLayout
        // WHY: Container for all content cards
        // HOW: JPanel with CardLayout
        contentCardPanel = new JPanel(cardLayout);
        contentCardPanel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Create and add home panel (default view)
        // WHY: Default view when application starts
        // HOW: createHomePanel() returns welcome panel
        homePanel = createHomePanel();
        contentCardPanel.add(homePanel, "HOME");
        
        // WHAT: Create and add registration panel
        // WHY: Registration form embedded in right panel
        // HOW: createRegistrationPanel() returns registration form panel
        registrationPanel = createRegistrationPanel();
        contentCardPanel.add(registrationPanel, "REGISTRATION");
        
        // WHAT: Create and add records panel
        // WHY: Records management embedded in right panel
        // HOW: createRecordsPanel() returns records management panel
        recordsPanel = createRecordsPanel();
        contentCardPanel.add(recordsPanel, "RECORDS");
        
        // WHAT: Create and add reports panel (only for ADMIN users)
        // WHY: Reports should only be accessible to administrators
        // HOW: Check user role, only add panel if ADMIN
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            reportsPanel = createReportsPanel();
            contentCardPanel.add(reportsPanel, "REPORTS");
        }
        
        // WHAT: Show home panel by default
        // WHY: Default view when window opens
        // HOW: CardLayout.show() displays specified card
        cardLayout.show(contentCardPanel, "HOME");
        
        // WHAT: Wrap content card panel in scroll pane for scrollability
        // WHY: Content should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps contentCardPanel with scroll policies
        JScrollPane contentScrollPane = new JScrollPane(contentCardPanel);
        contentScrollPane.setBorder(null);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Add scroll pane to content panel center
        // WHY: Scrollable content should fill center area
        // HOW: BorderLayout.CENTER positions in center
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        
        // WHAT: Center the content panel in outer panel
        // WHY: Content should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerContentPanel.add(contentPanel, BorderLayout.CENTER);
        
        // WHAT: Return outer content panel with centered content
        // WHY: Caller needs panel to add to window
        // HOW: return statement
        return outerContentPanel;
    }
    
    /**
     * createHomePanel() - Creates home/welcome panel with logo and dynamic welcome message (responsive)
     * WHAT: Builds welcome panel with logo, personalized greeting based on user activity
     * WHY: Default view when no menu item is selected, provides personalized experience with branding
     * HOW: Checks user activity to determine if new or returning user, displays logo and appropriate message, with responsive centering
     * @return JPanel containing home content
     */
    private JPanel createHomePanel() {
        // WHAT: Create outer home panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the content when window is resized
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create home panel with BorderLayout
        // WHY: BorderLayout allows centered content
        // HOW: JPanel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for home panel to prevent excessive stretching
        // WHY: Panel should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(600, 0));
        
        // WHAT: Create welcome message panel
        // WHY: Displays welcome information
        // HOW: JPanel with centered content
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(new EmptyBorder(50, 40, 50, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // WHAT: Add logo image at the top
        // WHY: Branding and professional appearance
        // HOW: Load logo image and display in JLabel
        try {
            ImageIcon logoIcon = loadLogoImage();
            if (logoIcon != null) {
                // WHAT: Scale logo to appropriate size for display
                // WHY: Logo should be visible but not overwhelming
                // HOW: getScaledInstance() resizes image
                Image logoImage = logoIcon.getImage();
                Image scaledLogo = logoImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gbc.gridx = 0; gbc.gridy = 0;
                gbc.gridwidth = 1;
                welcomePanel.add(logoLabel, gbc);
            }
        } catch (Exception e) {
            // Logo loading failed, continue without logo
            System.out.println("Logo image not found: " + e.getMessage());
        }
        
        // WHAT: Determine if user is new or returning
        // WHY: Show personalized welcome message
        // HOW: Check if user has location set or if there are records in system
        boolean isReturningUser = checkIfReturningUser();
        
        // WHAT: Create dynamic welcome label based on user status
        // WHY: Personalized greeting improves user experience
        // HOW: JLabel with conditional text
        String welcomeText = isReturningUser ? "Welcome back, " + currentUser.getName() + "!" : "Welcome to AgriTrack";
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 1;
        welcomePanel.add(welcomeLabel, gbc);
        
        // WHAT: Create instruction label with role-specific message
        // WHY: Guides user to select menu option based on their role
        // HOW: JLabel with conditional instructions
        String instructionText;
        if (isReturningUser) {
            instructionText = "Select an option from the menu to continue";
        } else {
            instructionText = "Select an option from the menu to get started";
        }
        
        JLabel instructionLabel = new JLabel(instructionText);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instructionLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        gbc.gridy = 2;
        welcomePanel.add(instructionLabel, gbc);
        
        // WHAT: Add user role information for new users
        // WHY: Help new users understand their role
        // HOW: JLabel with role information
        if (!isReturningUser && currentUser != null) {
            String roleInfo = "You are logged in as: " + currentUser.getRole();
            JLabel roleLabel = new JLabel(roleInfo);
            roleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            roleLabel.setForeground(ThemeColors.TEXT_TERTIARY);
            gbc.gridy = 3;
            welcomePanel.add(roleLabel, gbc);
        }
        
        // WHAT: Add welcome panel to center
        // WHY: Content should be centered
        // HOW: BorderLayout.CENTER positions in center
        panel.add(welcomePanel, BorderLayout.CENTER);
        
        // WHAT: Center the home panel in outer panel
        // WHY: Panel should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(panel, BorderLayout.CENTER);
        
        return outerPanel;
    }
    
    /**
     * loadLogoImage() - Loads the application logo image
     * WHAT: Attempts to load the logo image from the images folder
     * WHY: Provides branding for the application
     * HOW: Tries multiple possible paths to find the logo file
     * @return ImageIcon with logo, or null if not found
     */
    private ImageIcon loadLogoImage() {
        // WHAT: Try multiple possible paths for the logo file
        // WHY: Logo file might be in different locations depending on how app is run
        // HOW: Check relative path first, then try absolute path
        String userDir = System.getProperty("user.dir");
        String[] possiblePaths = {
            "images/agritrack_icon.png",
            "./images/agritrack_icon.png",
            userDir + "/images/agritrack_icon.png",
            userDir + "\\images\\agritrack_icon.png",
            userDir + File.separator + "images" + File.separator + "agritrack_icon.png"
        };
        
        for (String path : possiblePaths) {
            try {
                File logoFile = new File(path);
                if (logoFile.exists() && logoFile.isFile()) {
                    // WHAT: Use absolute path for ImageIcon
                    // WHY: ImageIcon needs absolute path to load correctly
                    // HOW: getAbsolutePath() converts to absolute path
                    String absolutePath = logoFile.getAbsolutePath();
                    ImageIcon logoIcon = new ImageIcon(absolutePath);
                    
                    // WHAT: Verify logo was loaded successfully
                    // WHY: ImageIcon might be created but image not loaded
                    // HOW: Check if icon has valid image and wait for load
                    if (logoIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                        System.out.println("Logo loaded successfully from: " + absolutePath);
                        return logoIcon;
                    } else {
                        // WHAT: Wait a bit for image to load
                        // WHY: Image might still be loading
                        // HOW: Thread.sleep() gives time for async load
                        Thread.sleep(100);
                        if (logoIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                            System.out.println("Logo loaded successfully from: " + absolutePath);
                            return logoIcon;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to next path (exception handled, loop continues naturally)
                System.out.println("Failed to load logo from path: " + path + " - " + e.getMessage());
            }
        }
        
        // WHAT: Try using IconUtil as fallback
        // WHY: IconUtil has better path resolution
        // HOW: Call getApplicationIcon() which handles path resolution
        try {
            ImageIcon icon = util.IconUtil.getApplicationIcon();
            if (icon != null && icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                System.out.println("Logo loaded via IconUtil");
                return icon;
            }
        } catch (Exception e) {
            System.out.println("IconUtil fallback failed: " + e.getMessage());
        }
        
        // WHAT: Return null if logo not found
        // WHY: Indicates logo file doesn't exist
        // HOW: Return null
        System.out.println("Warning: Logo image not found in any of the checked paths");
        return null;
    }
    
    /**
     * checkIfReturningUser() - Checks if user is returning or new
     * WHAT: Determines if user has been active in the system before
     * WHY: Needed to show personalized welcome message
     * HOW: Checks if user has location set or if there are records in the system
     * @return true if user is returning, false if new
     */
    private boolean checkIfReturningUser() {
        // WHAT: Check if user has location set
        // WHY: Location being set indicates user has used profile feature before
        // HOW: Check if currentUser.getLocation() is not null and not empty
        if (currentUser != null && currentUser.getLocation() != null && !currentUser.getLocation().trim().isEmpty()) {
            return true; // User has set location, they're returning
        }
        
        // WHAT: Check if there are any records in the system
        // WHY: If system has records, user might have interacted before
        // HOW: Query database for record count
        try {
            dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
            java.util.List<model.FarmItem> records = inventoryDAO.getAllRecords();
            // WHAT: If there are records, user might be returning (system has been used)
            // WHY: System with records suggests it's not brand new
            // HOW: Check if records list is not empty
            if (!records.isEmpty()) {
                return true; // System has records, likely returning user
            }
        } catch (Exception e) {
            // If error checking records, assume new user
        }
        
        // WHAT: Default to new user if no indicators found
        // WHY: Better to welcome new users than assume they're returning
        // HOW: Return false
        return false;
    }
    
    /**
     * createRegistrationPanel() - Creates registration form panel
     * WHAT: Builds embedded registration form panel
     * WHY: Registration form appears in right panel instead of separate dialog
     * HOW: Extracts form components from RegistrationForm logic
     * @return JPanel containing registration form
     */
    private JPanel createRegistrationPanel() {
        // WHAT: Create outer panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the form content
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create registration panel with BorderLayout
        // WHY: BorderLayout allows form structure
        // HOW: JPanel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for form to prevent stretching
        // WHY: Form should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(500, 0));
        
        // WHAT: Create scrollable form panel
        // WHY: Form may be long, needs scrolling
        // HOW: JPanel in JScrollPane
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; // Changed from HORIZONTAL to prevent stretching
        gbc.weightx = 0; // Prevent horizontal expansion
        
        // WHAT: Create form fields (same as RegistrationForm)
        // WHY: User needs to input registration data
        // HOW: JTextField, JPasswordField, JComboBox components
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField(20);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField confirmPasswordField = new JPasswordField(20);
        
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField nameField = new JTextField(20);
        
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"SELLER", "BUYER"});
        
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(ThemeColors.ERROR);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // WHAT: Create buttons
        // WHY: User needs to save or clear form
        // HOW: JButton components
        JButton saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear");
        
        // WHAT: Style buttons
        // WHY: Professional appearance
        // HOW: styleActionButton() applies styling
        styleActionButton(saveButton, ThemeColors.PRIMARY);
        styleActionButton(clearButton, ThemeColors.TEXT_SECONDARY);
        
        // WHAT: Add action listeners
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() with lambda expressions
        saveButton.addActionListener(e -> {
            // Registration logic (simplified - can extract from RegistrationForm)
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String name = nameField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                statusLabel.setForeground(ThemeColors.ERROR);
            } else if (!password.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match");
                statusLabel.setForeground(ThemeColors.ERROR);
            } else {
                try {
                    dao.UserDAO userDAO = new dao.UserDAO();
                    userDAO.signUp(username, password, name, role);
                    statusLabel.setText("User registered successfully!");
                    statusLabel.setForeground(ThemeColors.SUCCESS);
                    // Clear fields
                    usernameField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    nameField.setText("");
                } catch (java.sql.SQLException | RuntimeException ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(ThemeColors.ERROR);
                }
            }
        });
        
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            nameField.setText("");
            statusLabel.setText(" ");
        });
        
        // WHAT: Add components to form panel
        // WHY: Form needs to be laid out
        // HOW: GridBagLayout with constraints
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // WHAT: Set preferred and maximum sizes for text fields to prevent stretching
        // WHY: Fields should maintain reasonable width even when window is maximized
        // HOW: setPreferredSize() and setMaximumSize() limit field width
        usernameField.setPreferredSize(new Dimension(250, 25));
        usernameField.setMaximumSize(new Dimension(250, 25));
        passwordField.setPreferredSize(new Dimension(250, 25));
        passwordField.setMaximumSize(new Dimension(250, 25));
        confirmPasswordField.setPreferredSize(new Dimension(250, 25));
        confirmPasswordField.setMaximumSize(new Dimension(250, 25));
        nameField.setPreferredSize(new Dimension(250, 25));
        nameField.setMaximumSize(new Dimension(250, 25));
        roleComboBox.setPreferredSize(new Dimension(250, 25));
        roleComboBox.setMaximumSize(new Dimension(250, 25));
        
        // WHAT: Create scroll pane for form
        // WHY: Form may overflow on small screens
        // HOW: JScrollPane wraps form panel
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // WHAT: Add scroll pane to panel center
        // WHY: Form should fill center area
        // HOW: BorderLayout.CENTER positions in center
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Center the form panel in outer panel
        // WHY: Form should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(panel, BorderLayout.CENTER);
        
        return outerPanel;
    }
    
    /**
     * createRecordsPanel() - Creates records management panel with full functionality (responsive)
     * WHAT: Builds embedded records management panel with table, search, and role-based buttons
     * WHY: Records window appears in right panel with all features including purchase for buyers
     * HOW: Creates JTable with records data, search bar, and role-based action buttons, with responsive centering
     * @return JPanel containing records management
     */
    private JPanel createRecordsPanel() {
        // WHAT: Create outer panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the content when window is resized
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create records panel with BorderLayout
        // WHY: BorderLayout allows search at top, table in center, controls at bottom
        // HOW: JPanel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for records panel to prevent excessive stretching
        // WHY: Panel should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(1400, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(1200, 0));
        
        // WHAT: Create search panel at top
        // WHY: Users need to search/filter records
        // HOW: JPanel with search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        // WHAT: Create table model for records
        // WHY: JTable needs data model
        // HOW: DefaultTableModel with column names
        String[] columnNames = {"ID", "Name", "Type", "Quantity", "Unit", "Date", "Status", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // WHAT: Create row sorter for search functionality
        // WHY: Allows filtering table rows as user types
        // HOW: TableRowSorter filters rows based on search text
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        
        // WHAT: Create JTable with model and sorter
        // WHY: Display records in tabular format with search capability
        // HOW: JTable with DefaultTableModel and TableRowSorter
        JTable recordsTable = new JTable(tableModel);
        recordsTable.setRowSorter(sorter);
        recordsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordsTable.setRowHeight(25);
        recordsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        recordsTable.getTableHeader().setBackground(ThemeColors.TABLE_HEADER_BG);
        recordsTable.getTableHeader().setForeground(ThemeColors.TABLE_HEADER_TEXT);
        
        // WHAT: Add search functionality
        // WHY: Users need to filter records in real-time
        // HOW: KeyListener updates row filter as user types
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        // WHAT: Load records into table
        // WHY: Table should show existing records
        // HOW: Query database and populate table
        loadRecordsIntoTable(tableModel);
        
        // WHAT: Create scroll pane for table
        // WHY: Table may have many rows
        // HOW: JScrollPane wraps JTable
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // WHAT: Create control panel with role-based buttons
        // WHY: Different users need different actions
        // HOW: JPanel with FlowLayout, show/hide buttons based on role
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton refreshButton = new JButton("Refresh");
        JButton openFullWindowButton = new JButton("Open Full Window");
        
        // WHAT: Style common buttons
        // WHY: Professional appearance
        // HOW: styleActionButton() applies styling
        styleActionButton(refreshButton, ThemeColors.PRIMARY);
        styleActionButton(openFullWindowButton, ThemeColors.INFO);
        
        // WHAT: Add action listeners for common buttons
        // WHY: Buttons need functionality
        // HOW: addActionListener() with lambda expressions
        refreshButton.addActionListener(e -> loadRecordsIntoTable(tableModel));
        openFullWindowButton.addActionListener(e -> {
            // WHAT: Open full RecordsWindow with all features
            // WHY: Users may want full functionality with menu bar, export, etc.
            // HOW: Create new RecordsWindow instance
            RecordsWindow fullWindow = new RecordsWindow(currentUser);
            fullWindow.setVisible(true);
        });
        
        controlPanel.add(refreshButton);
        controlPanel.add(openFullWindowButton);
        
        // WHAT: Add role-based buttons for Sellers/Admins
        // WHY: Sellers and admins need CRUD operations
        // HOW: Check user role, add buttons if SELLER or ADMIN
        if (currentUser != null && ("SELLER".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole()))) {
            JButton addButton = new JButton("Add Record");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            JButton exportButton = new JButton("Export CSV");
            JButton importButton = new JButton("Import CSV");
            
            styleActionButton(addButton, ThemeColors.PRIMARY);
            styleActionButton(editButton, ThemeColors.WARNING);
            styleActionButton(deleteButton, ThemeColors.ERROR);
            styleActionButton(exportButton, ThemeColors.INFO);
            styleActionButton(importButton, ThemeColors.INFO);
            
            addButton.addActionListener(e -> {
                HarvestForm form = new HarvestForm(currentUser, this);
                form.setVisible(true);
                loadRecordsIntoTable(tableModel);
            });
            
            editButton.addActionListener(e -> {
                int selectedRow = recordsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
                        Integer recordId = (Integer) tableModel.getValueAt(modelRow, 0);
                        dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                        model.FarmItem item = inventoryDAO.getRecordById(recordId);
                        if (item != null) {
                            HarvestForm form = new HarvestForm(currentUser, this, item);
                            form.setVisible(true);
                            loadRecordsIntoTable(tableModel);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error loading record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a record to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            deleteButton.addActionListener(e -> {
                int selectedRow = recordsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
                            Integer recordId = (Integer) tableModel.getValueAt(modelRow, 0);
                            dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                            inventoryDAO.deleteRecord(recordId);
                            loadRecordsIntoTable(tableModel);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a record to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            exportButton.addActionListener(e -> {
                try {
                    // WHAT: Get all records from database
                    // WHY: exportToCSV needs list of records
                    // HOW: InventoryDAO.getAllRecords() returns list
                    dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                    java.util.List<model.FarmItem> records = inventoryDAO.getAllRecords();
                    
                    JFileChooser exportFileChooser = new JFileChooser();
                    exportFileChooser.setDialogTitle("Export Records to CSV");
                    if (exportFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        java.io.File exportFile = exportFileChooser.getSelectedFile();
                        util.CSVExporter.exportToCSV(exportFile.getAbsolutePath(), records);
                        JOptionPane.showMessageDialog(this, "Records exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            importButton.addActionListener(e -> {
                try {
                    // WHAT: Import from CSV (FileImporter handles file chooser internally)
                    // WHY: FileImporter.importFromCSV() shows file chooser and imports
                    // HOW: Pass parent frame, method handles file selection and import
                    util.FileImporter.importFromCSV(this);
                    JOptionPane.showMessageDialog(this, "Records imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRecordsIntoTable(tableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error importing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            controlPanel.add(addButton);
            controlPanel.add(editButton);
            controlPanel.add(deleteButton);
            controlPanel.add(exportButton);
            controlPanel.add(importButton);
        }
        
        // WHAT: Add role-based buttons for Buyers
        // WHY: Buyers need purchase functionality
        // HOW: Check user role, add buy button if BUYER
        if (currentUser != null && "BUYER".equals(currentUser.getRole())) {
            JButton buyButton = new JButton("Buy Product");
            JButton interestedButton = new JButton("Mark as Interested");
            JButton exportButton = new JButton("Export CSV");
            
            styleActionButton(buyButton, ThemeColors.PRIMARY);
            styleActionButton(interestedButton, ThemeColors.WARNING);
            styleActionButton(exportButton, ThemeColors.INFO);
            
            buyButton.addActionListener(e -> {
                int selectedRow = recordsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
                        Integer recordId = (Integer) tableModel.getValueAt(modelRow, 0);
                        dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                        model.FarmItem item = inventoryDAO.getRecordById(recordId);
                        if (item != null && item instanceof model.HarvestLot) {
                            model.HarvestLot harvestLot = (model.HarvestLot) item;
                            // WHAT: Open PurchaseDialog for complete purchase workflow
                            // WHY: Buyers need full purchase interface with delivery options
                            // HOW: Create PurchaseDialog instance
                            PurchaseDialog purchaseDialog = new PurchaseDialog(this, currentUser, harvestLot);
                            purchaseDialog.setVisible(true);
                            loadRecordsIntoTable(tableModel); // Refresh after purchase
                        } else {
                            JOptionPane.showMessageDialog(this, "Only harvest items can be purchased", "Invalid Item", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a product to purchase", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            interestedButton.addActionListener(e -> {
                int selectedRow = recordsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int modelRow = recordsTable.convertRowIndexToModel(selectedRow);
                        Integer recordId = (Integer) tableModel.getValueAt(modelRow, 0);
                        dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                        model.FarmItem item = inventoryDAO.getRecordById(recordId);
                        if (item != null && item instanceof model.HarvestLot) {
                            model.HarvestLot harvestLot = (model.HarvestLot) item;
                            harvestLot.setStatus("Interested");
                            inventoryDAO.updateRecord(harvestLot);
                            JOptionPane.showMessageDialog(this, "Product marked as interested!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadRecordsIntoTable(tableModel);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a product", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            exportButton.addActionListener(e -> {
                try {
                    // WHAT: Get all records from database
                    // WHY: exportToCSV needs list of records
                    // HOW: InventoryDAO.getAllRecords() returns list
                    dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
                    java.util.List<model.FarmItem> records = inventoryDAO.getAllRecords();
                    
                    JFileChooser buyerExportChooser = new JFileChooser();
                    buyerExportChooser.setDialogTitle("Export Records to CSV");
                    if (buyerExportChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        java.io.File buyerExportFile = buyerExportChooser.getSelectedFile();
                        util.CSVExporter.exportToCSV(buyerExportFile.getAbsolutePath(), records);
                        JOptionPane.showMessageDialog(this, "Records exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            controlPanel.add(buyButton);
            controlPanel.add(interestedButton);
            controlPanel.add(exportButton);
        }
        
        // WHAT: Add components to panel
        // WHY: Panel needs structure
        // HOW: BorderLayout positions components
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Center the records panel in outer panel
        // WHY: Panel should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(panel, BorderLayout.CENTER);
        
        return outerPanel;
    }
    
    /**
     * loadRecordsIntoTable() - Loads records from database into table
     * WHAT: Queries database and populates table model with records
     * WHY: Table needs to display current records
     * HOW: Uses InventoryDAO to get records, adds rows to table model
     * @param tableModel Table model to populate
     */
    private void loadRecordsIntoTable(DefaultTableModel tableModel) {
        // WHAT: Clear existing rows
        // WHY: Start fresh before loading
        // HOW: setRowCount(0) removes all rows
        tableModel.setRowCount(0);
        
        try {
            // WHAT: Get all records from database
            // WHY: Need to display all inventory items
            // HOW: InventoryDAO.getAllRecords() returns list
            dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
            java.util.List<model.FarmItem> records = inventoryDAO.getAllRecords();
            
            // WHAT: Add each record as a row
            // WHY: Table needs data rows
            // HOW: Loop through records, add row for each
            for (model.FarmItem item : records) {
                String status = "N/A";
                String price = "Not set";
                
                if (item instanceof model.HarvestLot) {
                    model.HarvestLot harvestLot = (model.HarvestLot) item;
                    status = harvestLot.getStatus() != null ? harvestLot.getStatus() : "N/A";
                    if (harvestLot.getPricePerUnit() != null) {
                        price = String.format("₱%.2f", harvestLot.getPricePerUnit());
                    }
                }
                
                Object[] row = {
                    item.getId(),
                    item.getName(),
                    item.getItemType(),
                    item.getQuantity(),
                    item.getUnit(),
                    item.getDateAdded() != null ? item.getDateAdded().toString() : "N/A",
                    status,
                    price
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * createReportsPanel() - Creates reports panel
     * WHAT: Builds embedded reports panel with statistics
     * WHY: Reports appear in right panel instead of separate window
     * HOW: Creates labels displaying statistics
     * @return JPanel containing reports
     */
    private JPanel createReportsPanel() {
        // WHAT: Create outer panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the content when window is resized
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create reports panel with BorderLayout
        // WHY: BorderLayout allows centered content
        // HOW: JPanel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for reports panel to prevent excessive stretching
        // WHY: Panel should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(1200, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(1000, 0));
        
        // WHAT: Create tabbed pane for different report sections
        // WHY: Organizes multiple report types in tabs
        // HOW: JTabbedPane with multiple tabs
        JTabbedPane reportsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        reportsTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportsTabbedPane.setBackground(Color.WHITE);
        
        try {
            // WHAT: Get all records from database
            // WHY: Need data for all reports
            // HOW: InventoryDAO queries database
            dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
            java.util.List<model.FarmItem> records = inventoryDAO.getAllRecords();
            
            // WHAT: Create Statistical Reports tab
            // WHY: Shows overall statistics and summaries
            // HOW: createStatisticalReportsTab() returns panel with statistics
            reportsTabbedPane.addTab("Statistical Reports", createStatisticalReportsTab(records));
            
            // WHAT: Create Inventory Summaries tab
            // WHY: Shows detailed inventory breakdown
            // HOW: createInventorySummariesTab() returns panel with summaries
            reportsTabbedPane.addTab("Inventory Summaries", createInventorySummariesTab(records));
            
            // WHAT: Create Category Breakdowns tab
            // WHY: Shows breakdown by item type and categories
            // HOW: createCategoryBreakdownsTab() returns panel with category analysis
            reportsTabbedPane.addTab("Category Breakdowns", createCategoryBreakdownsTab(records));
            
            // WHAT: Create Status Distribution tab
            // WHY: Shows status distribution for harvests
            // HOW: createStatusDistributionTab() returns panel with status analysis
            reportsTabbedPane.addTab("Status Distribution", createStatusDistributionTab(records));
            
            // WHAT: Create Export tab with export functionality
            // WHY: Allows administrators to export reports
            // HOW: createExportTab() returns panel with export options
            reportsTabbedPane.addTab("Export Reports", createExportTab(records));
            
        } catch (Exception e) {
            // WHAT: Handle errors gracefully
            // WHY: User needs feedback if reports fail to load
            // HOW: Show error message
            System.err.println("Error loading reports: " + e.getMessage());
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading reports: " + e.getMessage());
            errorLabel.setForeground(new Color(200, 0, 0));
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
            reportsTabbedPane.addTab("Error", new JScrollPane(errorLabel));
        }
        
        // WHAT: Add tabbed pane to panel
        // WHY: Reports should fill center
        // HOW: BorderLayout.CENTER positions in center
        panel.add(reportsTabbedPane, BorderLayout.CENTER);
        
        // WHAT: Validate and repaint panel
        // WHY: Ensure panel is properly displayed
        // HOW: revalidate() and repaint()
        panel.revalidate();
        panel.repaint();
        
        // WHAT: Center the reports panel in outer panel
        // WHY: Panel should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(panel, BorderLayout.CENTER);
        
        // WHAT: Validate and repaint outer panel
        // WHY: Ensure outer panel is properly displayed
        // HOW: revalidate() and repaint()
        outerPanel.revalidate();
        outerPanel.repaint();
        
        return outerPanel;
    }
    
    /**
     * createStatCard() - Creates a modern statistics card
     * WHAT: Creates a card panel with icon, label, and value
     * WHY: Card-based design is modern and visually appealing
     * HOW: Creates JPanel with border, background, and formatted labels
     * @param title Card title with icon
     * @param value Statistic value
     * @param color Card accent color
     * @return JPanel formatted as statistics card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        // WHAT: Create card panel
        // WHY: Container for card content
        // HOW: JPanel with BorderLayout
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(300, 130));
        card.setMinimumSize(new Dimension(250, 120));
        card.setMaximumSize(new Dimension(400, 150));
        
        // WHAT: Create title label
        // WHY: Shows what the statistic represents
        // HOW: JLabel with icon and text
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        // WHAT: Create value label
        // WHY: Shows the statistic value
        // HOW: JLabel with large, bold font
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        
        // WHAT: Create accent line
        // WHY: Visual accent matching card color
        // HOW: JPanel with colored background
        JPanel accentLine = new JPanel();
        accentLine.setBackground(color);
        accentLine.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        accentLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        
        // WHAT: Add components to card
        // WHY: Organize card layout
        // HOW: BorderLayout positions components
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(accentLine, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * createStatusBreakdownSection() - Creates status breakdown section with progress bars
     * WHAT: Creates a section showing harvest status breakdown with visual progress indicators
     * WHY: Visual representation makes statistics easier to understand
     * HOW: Creates JPanel with labels and progress bars for each status
     * @param available Available count
     * @param interested Interested count
     * @param soldOut Sold out count
     * @param total Total harvest count
     * @return JPanel containing status breakdown
     */
    private JPanel createStatusBreakdownSection(int available, int interested, int soldOut, int total) {
        // WHAT: Create section panel
        // WHY: Container for status breakdown
        // HOW: JPanel with BorderLayout
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // WHAT: Create section title
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel sectionTitle = new JLabel("Harvest Status Breakdown");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(ThemeColors.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        // WHAT: Create status panel
        // WHY: Container for status items
        // HOW: JPanel with GridBagLayout
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // WHAT: Add status items with progress bars
        // WHY: Visual representation of status distribution
        // HOW: Create status item panels
        int row = 0;
        
        if (available > 0) {
            gbc.gridx = 0; gbc.gridy = row++;
            statusPanel.add(createStatusItem("Available", available, total, ThemeColors.PRIMARY), gbc);
        }
        
        if (interested > 0) {
            gbc.gridx = 0; gbc.gridy = row++;
            statusPanel.add(createStatusItem("Interested", interested, total, ThemeColors.WARNING), gbc);
        }
        
        if (soldOut > 0) {
            gbc.gridx = 0; gbc.gridy = row++;
            statusPanel.add(createStatusItem("Sold Out", soldOut, total, ThemeColors.ERROR), gbc);
        }
        
        section.add(statusPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    /**
     * createStatusItem() - Creates a status item with progress bar
     * WHAT: Creates a panel showing status name, count, and visual progress bar
     * WHY: Visual progress bars make statistics easier to understand
     * HOW: Creates JPanel with label and JProgressBar
     * @param label Status label
     * @param count Status count
     * @param total Total count for percentage calculation
     * @param color Progress bar color
     * @return JPanel containing status item
     */
    private JPanel createStatusItem(String label, int count, int total, Color color) {
        // WHAT: Create item panel
        // WHY: Container for status item
        // HOW: JPanel with BorderLayout
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        
        // WHAT: Create top panel for label and count
        // WHY: Group label and count together
        // HOW: JPanel with BorderLayout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        // WHAT: Create label
        // WHY: Shows status name
        // HOW: JLabel with styling
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComponent.setForeground(ThemeColors.TEXT_PRIMARY);
        
        // WHAT: Create count label
        // WHY: Shows count and percentage
        // HOW: JLabel with formatted text
        int percentage = total > 0 ? (count * 100 / total) : 0;
        JLabel countLabel = new JLabel(count + " (" + percentage + "%)");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(color);
        
        // WHAT: Add to top panel
        // WHY: Organize layout
        // HOW: BorderLayout positions components
        topPanel.add(labelComponent, BorderLayout.WEST);
        topPanel.add(countLabel, BorderLayout.EAST);
        
        // WHAT: Create progress bar
        // WHY: Visual representation of percentage
        // HOW: JProgressBar with custom styling
        JProgressBar progressBar = new JProgressBar(0, total);
        progressBar.setValue(count);
        progressBar.setStringPainted(false);
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        
        // WHAT: Add components to item
        // WHY: Organize layout
        // HOW: BorderLayout positions components
        item.add(topPanel, BorderLayout.NORTH);
        item.add(progressBar, BorderLayout.SOUTH);
        
        return item;
    }
    
    /**
     * createStatisticalReportsTab() - Creates statistical reports tab
     * WHAT: Displays overall statistics with cards showing key metrics
     * WHY: Provides quick overview of inventory statistics
     * HOW: Creates card-based layout with key statistics
     * @param records List of all inventory records
     * @return JPanel containing statistical reports
     */
    private JPanel createStatisticalReportsTab(java.util.List<model.FarmItem> records) {
        // WHAT: Create main panel with GridBagLayout
        // WHY: GridBagLayout allows flexible component positioning
        // HOW: JPanel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // WHAT: Calculate statistics
        // WHY: Need data for display
        // HOW: Stream operations and loops
        int totalItems = records.size();
        long harvestCount = records.stream().filter(r -> r instanceof model.HarvestLot).count();
        long equipmentCount = records.stream().filter(r -> r instanceof model.EquipmentItem).count();
        double totalQuantity = records.stream().mapToDouble(model.FarmItem::getQuantity).sum();
        double totalValue = 0.0;
        double avgQuantity = totalItems > 0 ? totalQuantity / totalItems : 0.0;
        
        for (model.FarmItem item : records) {
            if (item instanceof model.HarvestLot) {
                model.HarvestLot harvest = (model.HarvestLot) item;
                if (harvest.getPricePerUnit() != null) {
                    totalValue += harvest.getQuantity() * harvest.getPricePerUnit();
                }
            }
        }
        
        // WHAT: Create header
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel headerLabel = new JLabel("Statistical Reports");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(headerLabel, gbc);
        
        // WHAT: Add separator
        // WHY: Visual separation
        // HOW: JSeparator component
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        panel.add(separator, gbc);
        
        // WHAT: Create statistics cards
        // WHY: Visual representation of statistics
        // HOW: createStatCard() creates formatted cards
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStatCard("Total Items", String.valueOf(totalItems), ThemeColors.INFO), gbc);
        
        gbc.gridx = 1;
        panel.add(createStatCard("Harvest Records", String.valueOf(harvestCount), ThemeColors.PRIMARY), gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStatCard("Equipment Items", String.valueOf(equipmentCount), ThemeColors.WARNING), gbc);
        
        gbc.gridx = 1;
        panel.add(createStatCard("Total Quantity", String.format("%.2f", totalQuantity), ThemeColors.INFO), gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createStatCard("Average Quantity", String.format("%.2f", avgQuantity), ThemeColors.PRIMARY), gbc);
        
        if (totalValue > 0) {
            gbc.gridx = 1;
            panel.add(createStatCard("Total Value", String.format("₱%.2f", totalValue), ThemeColors.PRIMARY_DARK), gbc);
        }
        
        // WHAT: Add glue for spacing
        // WHY: Push content to top
        // HOW: Box.createVerticalGlue()
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        // WHAT: Create scroll pane wrapper panel
        // WHY: Need to return JPanel, not JScrollPane
        // HOW: Create panel with BorderLayout and add scroll pane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }
    
    /**
     * createInventorySummariesTab() - Creates inventory summaries tab
     * WHAT: Displays detailed inventory summaries with tables
     * WHY: Provides detailed view of inventory items
     * HOW: Creates JTable with inventory data
     * @param records List of all inventory records
     * @return JPanel containing inventory summaries
     */
    private JPanel createInventorySummariesTab(java.util.List<model.FarmItem> records) {
        // WHAT: Create main panel with BorderLayout
        // WHY: BorderLayout allows title at top, table in center
        // HOW: JPanel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // WHAT: Create header
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel headerLabel = new JLabel("Inventory Summaries");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(33, 33, 33));
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // WHAT: Create table model
        // WHY: JTable needs data model
        // HOW: DefaultTableModel with column names
        String[] columnNames = {"ID", "Name", "Type", "Quantity", "Unit", "Price/Unit", "Date Added", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        
        // WHAT: Populate table with data
        // WHY: Display inventory items
        // HOW: Loop through records and add rows
        for (model.FarmItem item : records) {
            String priceDisplay = "N/A";
            String status = "N/A";
            
            if (item instanceof model.HarvestLot) {
                model.HarvestLot harvest = (model.HarvestLot) item;
                if (harvest.getPricePerUnit() != null) {
                    priceDisplay = String.format("₱%.2f", harvest.getPricePerUnit());
                }
                status = harvest.getStatus();
            } else if (item instanceof model.EquipmentItem) {
                // Equipment items don't have price per unit in current implementation
                priceDisplay = "N/A";
            }
            
            Object[] row = {
                item.getId(),
                item.getName(),
                item.getItemType(),
                String.format("%.2f", item.getQuantity()),
                item.getUnit(),
                priceDisplay,
                item.getDateAdded().toString(),
                status
            };
            tableModel.addRow(row);
        }
        
        // WHAT: Create JTable
        // WHY: Display data in tabular format
        // HOW: JTable with table model
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(ThemeColors.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(ThemeColors.TABLE_HEADER_TEXT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // WHAT: Create scroll pane
        // WHY: Table may be large
        // HOW: JScrollPane wraps table
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * createCategoryBreakdownsTab() - Creates category breakdowns tab
     * WHAT: Displays breakdown by item type and categories
     * WHY: Shows distribution of items by category
     * HOW: Creates panels showing category statistics
     * @param records List of all inventory records
     * @return JPanel containing category breakdowns
     */
    private JPanel createCategoryBreakdownsTab(java.util.List<model.FarmItem> records) {
        // WHAT: Create main panel with GridBagLayout
        // WHY: GridBagLayout allows flexible component positioning
        // HOW: JPanel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // WHAT: Calculate category statistics
        // WHY: Need data for breakdown
        // HOW: Stream operations
        long harvestCount = records.stream().filter(r -> r instanceof model.HarvestLot).count();
        long equipmentCount = records.stream().filter(r -> r instanceof model.EquipmentItem).count();
        double harvestQuantity = records.stream()
            .filter(r -> r instanceof model.HarvestLot)
            .mapToDouble(model.FarmItem::getQuantity)
            .sum();
        double equipmentQuantity = records.stream()
            .filter(r -> r instanceof model.EquipmentItem)
            .mapToDouble(model.FarmItem::getQuantity)
            .sum();
        
        // WHAT: Create header
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel headerLabel = new JLabel("Category Breakdowns");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        // WHAT: Add separator
        // WHY: Visual separation
        // HOW: JSeparator component
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        panel.add(separator, gbc);
        
        // WHAT: Create category breakdown section
        // WHY: Shows distribution by category
        // HOW: createCategorySection() creates formatted section
        gbc.gridy = 2;
        panel.add(createCategorySection("Harvest Items", harvestCount, harvestQuantity, records.size(), ThemeColors.PRIMARY), gbc);
        
        gbc.gridy = 3;
        panel.add(createCategorySection("Equipment Items", equipmentCount, equipmentQuantity, records.size(), ThemeColors.WARNING), gbc);
        
        // WHAT: Add glue for spacing
        // WHY: Push content to top
        // HOW: Box.createVerticalGlue()
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        // WHAT: Create scroll pane wrapper panel
        // WHY: Need to return JPanel, not JScrollPane
        // HOW: Create panel with BorderLayout and add scroll pane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }
    
    /**
     * createCategorySection() - Creates a category breakdown section
     * WHAT: Creates panel showing category statistics with progress bar
     * WHY: Visual representation of category distribution
     * HOW: Creates JPanel with labels and progress bar
     * @param title Category title
     * @param count Item count
     * @param quantity Total quantity
     * @param total Total items for percentage
     * @param color Category color
     * @return JPanel containing category section
     */
    private JPanel createCategorySection(String title, long count, double quantity, int total, Color color) {
        // WHAT: Create section panel
        // WHY: Container for category information
        // HOW: JPanel with BorderLayout
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // WHAT: Create top panel
        // WHY: Groups title and statistics
        // HOW: JPanel with BorderLayout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        // WHAT: Create title label
        // WHY: Shows category name
        // HOW: JLabel with styling
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        // WHAT: Create stats label
        // WHY: Shows count and quantity
        // HOW: JLabel with formatted text
        int percentage = total > 0 ? (int)(count * 100 / total) : 0;
        JLabel statsLabel = new JLabel(String.format("Count: %d (%d%%) | Quantity: %.2f", count, percentage, quantity));
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(color);
        
        // WHAT: Add to top panel
        // WHY: Organize layout
        // HOW: BorderLayout positions components
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(statsLabel, BorderLayout.EAST);
        
        // WHAT: Create progress bar
        // WHY: Visual representation of percentage
        // HOW: JProgressBar with custom styling
        JProgressBar progressBar = new JProgressBar(0, total);
        progressBar.setValue((int)count);
        progressBar.setStringPainted(false);
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 10));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        
        // WHAT: Add components to section
        // WHY: Organize layout
        // HOW: BorderLayout positions components
        section.add(topPanel, BorderLayout.NORTH);
        section.add(progressBar, BorderLayout.SOUTH);
        
        return section;
    }
    
    /**
     * createStatusDistributionTab() - Creates status distribution tab
     * WHAT: Displays status distribution for harvest items
     * WHY: Shows how harvests are distributed by status
     * HOW: Creates panels showing status statistics with progress bars
     * @param records List of all inventory records
     * @return JPanel containing status distribution
     */
    private JPanel createStatusDistributionTab(java.util.List<model.FarmItem> records) {
        // WHAT: Create main panel with GridBagLayout
        // WHY: GridBagLayout allows flexible component positioning
        // HOW: JPanel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // WHAT: Calculate status statistics
        // WHY: Need data for distribution
        // HOW: Loop through harvest items
        int availableCount = 0;
        int interestedCount = 0;
        int soldOutCount = 0;
        int totalHarvests = 0;
        
        for (model.FarmItem item : records) {
            if (item instanceof model.HarvestLot) {
                totalHarvests++;
                model.HarvestLot harvest = (model.HarvestLot) item;
                String status = harvest.getStatus();
                if ("Available".equals(status)) {
                    availableCount++;
                } else if ("Interested".equals(status)) {
                    interestedCount++;
                } else if ("Sold Out".equals(status)) {
                    soldOutCount++;
                }
            }
        }
        
        // WHAT: Create header
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel headerLabel = new JLabel("Status Distribution");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        // WHAT: Add separator
        // WHY: Visual separation
        // HOW: JSeparator component
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        panel.add(separator, gbc);
        
        // WHAT: Add status breakdown section if harvests exist
        // WHY: Shows status distribution
        // HOW: createStatusBreakdownSection() creates formatted section
        if (totalHarvests > 0) {
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            panel.add(createStatusBreakdownSection(availableCount, interestedCount, soldOutCount, totalHarvests), gbc);
        } else {
            // WHAT: Show message if no harvests
            // WHY: User needs feedback
            // HOW: JLabel with message
            JLabel noDataLabel = new JLabel("No harvest records available for status distribution.");
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noDataLabel.setForeground(new Color(100, 100, 100));
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            panel.add(noDataLabel, gbc);
        }
        
        // WHAT: Add glue for spacing
        // WHY: Push content to top
        // HOW: Box.createVerticalGlue()
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        // WHAT: Create scroll pane wrapper panel
        // WHY: Need to return JPanel, not JScrollPane
        // HOW: Create panel with BorderLayout and add scroll pane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }
    
    /**
     * createExportTab() - Creates export functionality tab
     * WHAT: Provides export functionality for reports
     * WHY: Allows administrators to export report data
     * HOW: Creates panel with export buttons and options
     * @param records List of all inventory records
     * @return JPanel containing export functionality
     */
    private JPanel createExportTab(java.util.List<model.FarmItem> records) {
        // WHAT: Create main panel with GridBagLayout
        // WHY: GridBagLayout allows flexible component positioning
        // HOW: JPanel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // WHAT: Create header
        // WHY: Identifies the section
        // HOW: JLabel with styling
        JLabel headerLabel = new JLabel("Export Reports");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        // WHAT: Add separator
        // WHY: Visual separation
        // HOW: JSeparator component
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        panel.add(separator, gbc);
        
        // WHAT: Create description label
        // WHY: Explains export functionality
        // HOW: JLabel with text
        JLabel descLabel = new JLabel("<html>Export inventory data and reports to CSV format for external analysis or backup.<br/>Select an export option below:</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));
        gbc.gridy = 2;
        panel.add(descLabel, gbc);
        
        // WHAT: Create export buttons panel
        // WHY: Groups export buttons
        // HOW: JPanel with GridBagLayout
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(10, 10, 10, 10);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;
        
        // WHAT: Create export all button
        // WHY: Allows exporting all inventory data
        // HOW: JButton with action listener
        JButton exportAllButton = new JButton("Export All Inventory Data");
        styleExportButton(exportAllButton, ThemeColors.INFO);
        exportAllButton.addActionListener(e -> exportInventoryData(records, "All Inventory Data"));
        btnGbc.gridx = 0; btnGbc.gridy = 0;
        btnGbc.gridwidth = 2;
        buttonsPanel.add(exportAllButton, btnGbc);
        
        // WHAT: Create export harvests button
        // WHY: Allows exporting only harvest data
        // HOW: JButton with action listener
        JButton exportHarvestsButton = new JButton("Export Harvest Records Only");
        styleExportButton(exportHarvestsButton, ThemeColors.PRIMARY);
        exportHarvestsButton.addActionListener(e -> {
            java.util.List<model.FarmItem> harvests = records.stream()
                .filter(r -> r instanceof model.HarvestLot)
                .collect(java.util.stream.Collectors.toList());
            exportInventoryData(harvests, "Harvest Records");
        });
        btnGbc.gridy = 1;
        buttonsPanel.add(exportHarvestsButton, btnGbc);
        
        // WHAT: Create export equipment button
        // WHY: Allows exporting only equipment data
        // HOW: JButton with action listener
        JButton exportEquipmentButton = new JButton("Export Equipment Items Only");
        styleExportButton(exportEquipmentButton, ThemeColors.WARNING);
        exportEquipmentButton.addActionListener(e -> {
            java.util.List<model.FarmItem> equipment = records.stream()
                .filter(r -> r instanceof model.EquipmentItem)
                .collect(java.util.stream.Collectors.toList());
            exportInventoryData(equipment, "Equipment Items");
        });
        btnGbc.gridy = 2;
        buttonsPanel.add(exportEquipmentButton, btnGbc);
        
        // WHAT: Create export statistics button
        // WHY: Allows exporting statistics summary
        // HOW: JButton with action listener
        JButton exportStatsButton = new JButton("Export Statistics Summary");
        styleExportButton(exportStatsButton, ThemeColors.INFO);
        exportStatsButton.addActionListener(e -> exportStatisticsSummary(records));
        btnGbc.gridy = 3;
        buttonsPanel.add(exportStatsButton, btnGbc);
        
        // WHAT: Add buttons panel to main panel
        // WHY: Display export options
        // HOW: GridBagConstraints positions component
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonsPanel, gbc);
        
        // WHAT: Add glue for spacing
        // WHY: Push content to top
        // HOW: Box.createVerticalGlue()
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        // WHAT: Create scroll pane wrapper panel
        // WHY: Need to return JPanel, not JScrollPane
        // HOW: Create panel with BorderLayout and add scroll pane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }
    
    /**
     * styleExportButton() - Styles export buttons
     * WHAT: Applies consistent styling to export buttons
     * WHY: Professional appearance
     * HOW: Sets colors, fonts, and sizes
     * @param button Button to style
     * @param color Button color
     */
    private void styleExportButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(300, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    /**
     * exportInventoryData() - Exports inventory data to CSV
     * WHAT: Exports selected inventory data to CSV file
     * WHY: Allows data backup and external analysis
     * HOW: Uses CSVExporter to write data to file
     * @param records List of records to export
     * @param exportType Type of export for filename
     */
    private void exportInventoryData(java.util.List<model.FarmItem> records, String exportType) {
        try {
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data to export for " + exportType, "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export " + exportType + " to CSV");
            String defaultFileName = "agritrack_" + exportType.toLowerCase().replace(" ", "_") + "_" + 
                java.time.LocalDate.now().toString() + ".csv";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                util.CSVExporter.exportToCSV(filePath, records);
                JOptionPane.showMessageDialog(this, 
                    exportType + " exported successfully to:\n" + filePath, 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * exportStatisticsSummary() - Exports statistics summary to text file
     * WHAT: Exports calculated statistics to a text file
     * WHY: Allows saving statistics for reporting
     * HOW: Writes statistics to text file
     * @param records List of all inventory records
     */
    private void exportStatisticsSummary(java.util.List<model.FarmItem> records) {
        try {
            // WHAT: Calculate statistics
            // WHY: Need data for export
            // HOW: Stream operations and loops
            int totalItems = records.size();
            long harvestCount = records.stream().filter(r -> r instanceof model.HarvestLot).count();
            long equipmentCount = records.stream().filter(r -> r instanceof model.EquipmentItem).count();
            double totalQuantity = records.stream().mapToDouble(model.FarmItem::getQuantity).sum();
            double totalValue = 0.0;
            int availableCount = 0;
            int interestedCount = 0;
            int soldOutCount = 0;
            
            for (model.FarmItem item : records) {
                if (item instanceof model.HarvestLot) {
                    model.HarvestLot harvest = (model.HarvestLot) item;
                    String status = harvest.getStatus();
                    if ("Available".equals(status)) {
                        availableCount++;
                    } else if ("Interested".equals(status)) {
                        interestedCount++;
                    } else if ("Sold Out".equals(status)) {
                        soldOutCount++;
                    }
                    if (harvest.getPricePerUnit() != null) {
                        totalValue += harvest.getQuantity() * harvest.getPricePerUnit();
                    }
                }
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Statistics Summary");
            String defaultFileName = "agritrack_statistics_" + java.time.LocalDate.now().toString() + ".txt";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                
                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
                    writer.write("AgriTrack - Statistics Summary Report");
                    writer.newLine();
                    writer.write("Generated: " + java.time.LocalDateTime.now().toString());
                    writer.newLine();
                    writer.write("=".repeat(50));
                    writer.newLine();
                    writer.newLine();
                    writer.write("OVERALL STATISTICS");
                    writer.newLine();
                    writer.write("-".repeat(50));
                    writer.newLine();
                    writer.write("Total Items: " + totalItems);
                    writer.newLine();
                    writer.write("Harvest Records: " + harvestCount);
                    writer.newLine();
                    writer.write("Equipment Items: " + equipmentCount);
                    writer.newLine();
                    writer.write("Total Quantity: " + String.format("%.2f", totalQuantity));
                    writer.newLine();
                    if (totalValue > 0) {
                        writer.write("Total Inventory Value: ₱" + String.format("%.2f", totalValue));
                        writer.newLine();
                    }
                    writer.newLine();
                    writer.write("STATUS DISTRIBUTION");
                    writer.newLine();
                    writer.write("-".repeat(50));
                    writer.newLine();
                    writer.write("Available: " + availableCount);
                    writer.newLine();
                    writer.write("Interested: " + interestedCount);
                    writer.newLine();
                    writer.write("Sold Out: " + soldOutCount);
                    writer.newLine();
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Statistics summary exported successfully to:\n" + filePath, 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting statistics: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * styleSidebarButton() - Styles navigation buttons for sidebar
     * WHAT: Applies teal/cyan rounded button styling matching the design
     * WHY: Consistent navigation button appearance
     * HOW: Sets colors, fonts, rounded borders, and hover effects
     * @param button Button to style
     */
    private void styleSidebarButton(JButton button) {
        // WHAT: Set button background to primary theme color
        // WHY: Matches logo and creates brand consistency
        // HOW: ThemeColors.PRIMARY provides consistent brand color
        button.setBackground(ThemeColors.PRIMARY);
        
        // WHAT: Set button text color to white
        // WHY: White contrasts with primary background
        // HOW: setForeground() sets text color
        button.setForeground(ThemeColors.SIDEBAR_TEXT);
        
        // WHAT: Set button font
        // WHY: Professional typography
        // HOW: Font constructor creates font, setFont() applies it
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // WHAT: Remove focus border
        // WHY: Cleaner appearance
        // HOW: setFocusPainted(false) disables focus border
        button.setFocusPainted(false);
        
        // WHAT: Set rounded border
        // WHY: Modern rounded appearance matching the design
        // HOW: EmptyBorder with rounded border effect
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        
        // WHAT: Set button preferred size
        // WHY: Consistent button sizing
        // HOW: setPreferredSize() sets dimensions
        button.setPreferredSize(new Dimension(200, 45));
        button.setMaximumSize(new Dimension(200, 45));
        
        // WHAT: Change cursor to hand pointer
        // WHY: Indicates button is clickable
        // HOW: Cursor.HAND_CURSOR shows hand icon
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // WHAT: Add hover effect
        // WHY: Interactive feedback
        // HOW: MouseAdapter changes background on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ThemeColors.PRIMARY_DARK); // Darker green on hover
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ThemeColors.PRIMARY); // Original primary color
            }
        });
    }
    
    /**
     * styleBackButton() - Styles back button for sidebar bottom
     * WHAT: Applies dark blue/gray rounded button styling matching the design
     * WHY: Consistent back button appearance
     * HOW: Sets colors, fonts, rounded borders, and hover effects
     * @param button Button to style
     */
    private void styleBackButton(JButton button) {
        // WHAT: Set button background to dark blue/gray
        // WHY: Matches reference design color
        // HOW: setBackground() applies color
        button.setBackground(ThemeColors.SIDEBAR_BG);
        
        // WHAT: Set button text color to white
        // WHY: White contrasts with dark background
        // HOW: setForeground() sets text color
        button.setForeground(Color.WHITE);
        
        // WHAT: Set button font
        // WHY: Professional typography
        // HOW: Font constructor creates font, setFont() applies it
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // WHAT: Remove focus border
        // WHY: Cleaner appearance
        // HOW: setFocusPainted(false) disables focus border
        button.setFocusPainted(false);
        
        // WHAT: Set rounded border
        // WHY: Modern rounded appearance matching the design
        // HOW: EmptyBorder with padding
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        
        // WHAT: Set button preferred size
        // WHY: Consistent button sizing
        // HOW: setPreferredSize() sets dimensions
        button.setPreferredSize(new Dimension(200, 45));
        button.setMaximumSize(new Dimension(200, 45));
        
        // WHAT: Change cursor to hand pointer
        // WHY: Indicates button is clickable
        // HOW: Cursor.HAND_CURSOR shows hand icon
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // WHAT: Add hover effect
        // WHY: Interactive feedback
        // HOW: MouseAdapter changes background on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 49, 64)); // Darker on hover
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(48, 63, 159)); // Original dark blue
            }
        });
    }
    
    /**
     * styleActionButton() - Styles action buttons (approve/decline/exit)
     * WHAT: Applies colored rounded button styling for action buttons
     * WHY: Consistent action button appearance
     * HOW: Sets colors, fonts, rounded borders, and hover effects
     * @param button Button to style
     * @param bgColor Background color (red for exit, green for approve, etc.)
     */
    private void styleActionButton(JButton button, Color bgColor) {
        // WHAT: Set button background color
        // WHY: Matches reference design
        // HOW: setBackground() applies color
        button.setBackground(bgColor);
        
        // WHAT: Set button text color to white
        // WHY: White contrasts with colored background
        // HOW: setForeground() sets text color
        button.setForeground(Color.WHITE);
        
        // WHAT: Set button font
        // WHY: Professional typography
        // HOW: Font constructor creates font, setFont() applies it
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // WHAT: Remove focus border
        // WHY: Cleaner appearance
        // HOW: setFocusPainted(false) disables focus border
        button.setFocusPainted(false);
        
        // WHAT: Set rounded border with padding
        // WHY: Modern rounded appearance
        // HOW: EmptyBorder with padding
        button.setBorder(new EmptyBorder(12, 30, 12, 30));
        
        // WHAT: Set button preferred size
        // WHY: Consistent button sizing
        // HOW: setPreferredSize() sets dimensions
        button.setPreferredSize(new Dimension(150, 45));
        
        // WHAT: Change cursor to hand pointer
        // WHY: Indicates button is clickable
        // HOW: Cursor.HAND_CURSOR shows hand icon
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // WHAT: Add hover effect
        // WHY: Interactive feedback
        // HOW: MouseAdapter changes background on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Darker shade on hover
                if (bgColor.equals(ThemeColors.ERROR)) {
                    button.setBackground(ThemeColors.ERROR.darker()); // Darker error color
                } else if (bgColor.equals(ThemeColors.PRIMARY)) {
                    button.setBackground(ThemeColors.PRIMARY_DARK); // Darker primary
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor); // Original color
            }
        });
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
        // WHAT: Check if registration form button was clicked
        // WHY: Need to switch to registration panel
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == registrationFormButton) {
            // WHAT: Switch to registration card
            // WHY: User wants to see registration form
            // HOW: CardLayout.show() displays specified card
            cardLayout.show(contentCardPanel, "REGISTRATION");
            contentTitle.setText("REGISTRATION FORM");
        } 
        // WHAT: Check if manage records button was clicked
        // WHY: Need to switch to records panel
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == manageRecordsButton) {
            // WHAT: Switch to records card and refresh data
            // WHY: User wants to view/manage inventory
            // HOW: CardLayout.show() displays specified card, refresh table
            cardLayout.show(contentCardPanel, "RECORDS");
            contentTitle.setText("MANAGE RECORDS");
            // Refresh records table
            if (recordsPanel != null) {
                try {
                    // WHAT: Get the scroll pane from records panel
                    // WHY: Need to access table to refresh data
                    // HOW: Navigate through panel components
                    JPanel centerPanel = (JPanel) recordsPanel.getComponent(1);
                    if (centerPanel.getComponentCount() > 0 && centerPanel.getComponent(0) instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) centerPanel.getComponent(0);
                        JTable table = (JTable) scrollPane.getViewport().getView();
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        loadRecordsIntoTable(model);
                    }
                } catch (Exception ex) {
                    // Refresh failed, but continue - table will refresh on next access
                }
            }
        } 
        // WHAT: Check if reports button was clicked (only for ADMIN users)
        // WHY: Reports should only be accessible to administrators
        // HOW: Check user role and getSource() returns component, == compares references
        else if (e.getSource() == reportsButton) {
            // WHAT: Verify user is ADMIN before showing reports
            // WHY: Security - only admins should access reports
            // HOW: Check role, show reports if ADMIN, show error otherwise
            if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
                // WHAT: Switch to reports card
            // WHY: User wants to view reports and statistics
                // HOW: CardLayout.show() displays specified card
                cardLayout.show(contentCardPanel, "REPORTS");
                contentTitle.setText("REPORTS & STATISTICS");
                // WHAT: Refresh reports panel to ensure data is current
                // WHY: Statistics should be up-to-date when viewing
                // HOW: Recreate reports panel with latest data
                if (reportsPanel != null) {
                    contentCardPanel.remove(reportsPanel);
                }
                reportsPanel = createReportsPanel();
                contentCardPanel.add(reportsPanel, "REPORTS");
                cardLayout.show(contentCardPanel, "REPORTS");
                contentCardPanel.revalidate();
                contentCardPanel.repaint();
            } else {
                // WHAT: Show access denied message
                // WHY: Non-admin users should not access reports
                // HOW: JOptionPane shows error message
                JOptionPane.showMessageDialog(this, 
                    "Access Denied: Reports are only available to administrators.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getSource() == databaseViewerButton) {
            // WHAT: Open database viewer dialog
            // WHY: User wants to see all database tables and their contents
            // HOW: Create and display DatabaseViewerDialog
            DatabaseViewerDialog dbViewer = new DatabaseViewerDialog(this);
            dbViewer.setVisible(true);
        } 
        // WHAT: Check if logout button was clicked
        // WHY: Need to logout user
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
                // WHAT: Close main menu window
                // WHY: User is logging out
                // HOW: dispose() closes and destroys window
                this.dispose();
                
                // WHAT: Open login window
                // WHY: Return to login screen after logout
                // HOW: new LoginFrame() creates and displays login window
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true); // Show the login window
            }
        } 
        // WHAT: Check if exit button was clicked
        // WHY: Need to exit application
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == exitButton) {
            // WHAT: Show exit confirmation dialog
            // WHY: Prevents accidental exit
            // HOW: confirmExit() shows dialog and exits if confirmed
            confirmExit();
        }
    }
}
