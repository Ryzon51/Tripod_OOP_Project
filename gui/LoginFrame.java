package gui; // Package declaration: Groups this class with other GUI/window classes

import dao.UserDAO; // Import: UserDAO for database authentication and user registration operations
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent class for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for handling button clicks
import java.awt.event.MouseAdapter; // Import: MouseAdapter for hover effects
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close event handling
import java.awt.event.WindowEvent; // Import: WindowEvent for window state change events
import javax.swing.*; // Import: Swing components (JFrame, JPanel, JButton, JTextField, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for adding padding/margins to components
import model.User; // Import: User model class representing authenticated user
import util.ThemeColors; // Import: ThemeColors for consistent color theming

/**
 * LoginFrame - Main Authentication and Registration Window
 * WHAT: GUI window that handles user login and new user registration in a tabbed interface
 * WHY: First window users see - provides authentication and account creation in one place
 * HOW: Uses JTabbedPane to combine Login and Registration Form tabs, implements ActionListener for button events
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener to handle user interactions
 */
public class LoginFrame extends JFrame implements ActionListener {
    // WHAT: Tabbed pane container that holds Login and Registration Form tabs
    // WHY: Allows users to switch between login and registration without opening separate windows
    // HOW: JTabbedPane displays tabs at top, each tab contains a JPanel with form components
    private JTabbedPane tabbedPane;
    
    // --- Login Tab Components ---
    // WHAT: Text field for entering username during login
    // WHY: Users need to input their username to authenticate
    // HOW: JTextField allows single-line text input, 25 columns wide
    private JTextField loginUsernameField;
    
    // WHAT: Password field for entering password during login (hides characters)
    // WHY: Security - passwords should not be visible when typing
    // HOW: JPasswordField displays asterisks/dots instead of actual characters
    private JPasswordField loginPasswordField;
    
    // WHAT: Button to submit login credentials
    // WHY: Triggers authentication process when clicked
    // HOW: ActionListener attached to handle click event and call handleLogin()
    private JButton loginButton;
    
    // WHAT: Label that displays login status messages (success, error, validation)
    // WHY: Provides user feedback about login attempts
    // HOW: Text and color change based on login result (green for success, red for error)
    private JLabel loginStatusLabel;
    
    // --- Registration Form Tab Components (merged from RegistrationForm.java) ---
    // WHAT: Text field for entering new username during registration
    // WHY: New users need to choose a unique username
    // HOW: JTextField for text input, validated for minimum length (3 characters)
    private JTextField signupUsernameField;
    
    // WHAT: Password field for entering new password during registration
    // WHY: New users need to set a password for their account
    // HOW: JPasswordField hides input, validated for minimum length (4 characters)
    private JPasswordField signupPasswordField;
    
    // WHAT: Password field for confirming the password matches
    // WHY: Prevents typos in password - user must type password twice to confirm
    // HOW: JPasswordField that must match signupPasswordField value
    private JPasswordField confirmPasswordField;
    
    // WHAT: Text field for entering user's full name
    // WHY: Stores display name for the account (shown in GUI headers)
    // HOW: JTextField for text input, validated to ensure not empty
    private JTextField nameField;
    
    // WHAT: Dropdown combo box for selecting account role (SELLER or BUYER)
    // WHY: Determines user permissions and available features in the system
    // HOW: JComboBox with predefined options, only SELLER and BUYER available (not ADMIN)
    private JComboBox<String> roleComboBox;
    
    // WHAT: Button to save/submit registration form
    // WHY: Triggers user account creation when clicked
    // HOW: ActionListener attached to handle click and call handleSignUp()
    private JButton signUpButton;
    
    // WHAT: Button to clear all registration form fields
    // WHY: Allows users to reset form without closing and reopening
    // HOW: ActionListener attached to call clearSignUpFields() method
    private JButton clearButton;
    
    // WHAT: Label that displays registration status messages
    // WHY: Provides user feedback about registration attempts (success, validation errors)
    // HOW: Text and color change based on registration result
    private JLabel signupStatusLabel;
    
    /**
     * Constructor - Creates and displays the LoginFrame window
     * WHAT: Initializes all GUI components, sets up layout, and makes window visible
     * WHY: Called when application starts to show login/registration interface
     * HOW: Calls helper methods to set up components and layout, then displays window
     */
    public LoginFrame() {
        // WHAT: Initialize all GUI components (text fields, buttons, labels)
        // WHY: Components must be created before they can be added to layout
        // HOW: Calls initializeComponents() method to create all Swing components
        initializeComponents();
        
        // WHAT: Arrange components in the window using layout managers
        // WHY: Components need to be positioned and organized visually
        // HOW: Calls setupLayout() to add components to panels and panels to frame
        setupLayout();
        
        // WHAT: Prevent default close behavior (immediate exit)
        // WHY: We want to show confirmation dialog before closing, not exit immediately
        // HOW: DO_NOTHING_ON_CLOSE prevents automatic closing, we handle it in WindowListener
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // WHAT: Set the window title bar text
        // WHY: Identifies the application window in taskbar/window manager
        // HOW: setTitle() sets the text displayed in window title bar
        setTitle("AgriTrack - Agriculture Management System");
        
        // WHAT: Set window icon using application logo
        // WHY: Professional appearance with branded icon in taskbar and title bar
        // HOW: setIconImage() sets the icon image for the window
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set window size to 500x500 pixels
        // WHY: Provides adequate space for login and registration forms
        // HOW: setSize() sets width and height in pixels
        setSize(500, 500);
        
        // WHAT: Set minimum window size to prevent UI distortion
        // WHY: Window should not be resized smaller than minimum usable size
        // HOW: setMinimumSize() sets minimum dimensions
        setMinimumSize(new Dimension(450, 450));
        
        // WHAT: Set maximum window size to prevent excessive stretching
        // WHY: Very large windows can make the centered form look too small
        // HOW: setMaximumSize() limits maximum dimensions (optional, can be removed if not needed)
        // Note: Commented out to allow fullscreen, but form will remain centered and fixed-width
        // setMaximumSize(new Dimension(1200, 900));
        
        // WHAT: Center the window on the screen
        // WHY: Better user experience - window appears in center rather than top-left corner
        // HOW: setLocationRelativeTo(null) centers window relative to screen
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
        
        // WHAT: Add window close listener to show confirmation dialog
        // WHY: Prevents accidental application exit - user must confirm
        // HOW: WindowAdapter is anonymous class that overrides windowClosing() method
        addWindowListener(new WindowAdapter() {
            @Override
            // WHAT: Called when user clicks window close button (X)
            // WHY: Intercepts close event to show confirmation instead of immediate exit
            // HOW: Override windowClosing() to call confirmExit() method
            public void windowClosing(WindowEvent e) {
                confirmExit(); // Show confirmation dialog before closing
            }
        });
        
        // WHAT: Make the window visible on screen
        // WHY: Window is created but hidden by default - must be explicitly shown
        // HOW: setVisible(true) displays the window and all its components
        setVisible(true);
    }
    
    /**
     * confirmExit() - Shows confirmation dialog before exiting application
     * WHAT: Displays a Yes/No dialog asking user to confirm exit
     * WHY: Prevents accidental application closure
     * HOW: Uses JOptionPane to show dialog, exits only if user clicks Yes
     */
    private void confirmExit() {
        // WHAT: Show confirmation dialog with Yes/No options
        // WHY: Gives user chance to cancel exit if clicked accidentally
        // HOW: showConfirmDialog() displays modal dialog, returns user's choice
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to exit the application?", // Message text
            "Confirm Exit", // Dialog title
            JOptionPane.YES_NO_OPTION, // Show Yes and No buttons
            JOptionPane.QUESTION_MESSAGE); // Question icon
        
        // WHAT: Check if user clicked Yes button
        // WHY: Only exit if user explicitly confirms
        // HOW: YES_OPTION constant (value 0) returned when Yes is clicked
        if (confirm == JOptionPane.YES_OPTION) {
            // WHAT: Terminate the Java application
            // WHY: User confirmed they want to exit
            // HOW: System.exit(0) stops JVM with exit code 0 (success)
            System.exit(0);
        }
        // If user clicks No, dialog closes and window remains open (no action needed)
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates all text fields, buttons, labels, and sets their properties
     * WHY: Components must be created before they can be added to the layout
     * HOW: Creates Swing components and configures their appearance and behavior
     */
    private void initializeComponents() {
        // --- Login Tab Components ---
        // WHAT: Create text field for username input (25 characters wide)
        // WHY: Users need a place to type their username
        // HOW: JTextField constructor takes column width parameter
        loginUsernameField = new JTextField(25);
        
        // WHAT: Create password field for password input (hides characters)
        // WHY: Security - passwords should not be visible
        // HOW: JPasswordField automatically masks input with dots/asterisks
        loginPasswordField = new JPasswordField(25);
        
        // WHAT: Create login button with text and emoji icon
        // WHY: Users need a button to submit login credentials
        // HOW: JButton constructor takes button text as parameter
        loginButton = new JButton("Login");
        
        // WHAT: Create label for displaying login status messages
        // WHY: Provides feedback about login success/failure
        // HOW: JLabel with space character to reserve space even when empty
        loginStatusLabel = new JLabel(" ");
        
        // WHAT: Set status label text color to error color (for error messages)
        // WHY: Error color indicates errors/warnings to user
        // HOW: ThemeColors.ERROR provides consistent error color
        loginStatusLabel.setForeground(ThemeColors.ERROR);
        
        // WHAT: Center-align the status label text
        // WHY: Centered text looks more professional and is easier to read
        // HOW: SwingConstants.CENTER aligns text horizontally in center
        loginStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // --- Registration Form Tab Components ---
        // WHAT: Create text field for new username input
        // WHY: Registration form needs username field
        // HOW: JTextField for single-line text input
        signupUsernameField = new JTextField(25);
        
        // WHAT: Create password field for new password
        // WHY: Registration form needs password field
        // HOW: JPasswordField hides input characters
        signupPasswordField = new JPasswordField(25);
        
        // WHAT: Create password field for password confirmation
        // WHY: User must type password twice to prevent typos
        // HOW: JPasswordField that will be compared with signupPasswordField
        confirmPasswordField = new JPasswordField(25);
        
        // WHAT: Create text field for user's full name
        // WHY: Registration form needs name field for display purposes
        // HOW: JTextField for text input
        nameField = new JTextField(25);
        
        // WHAT: Create dropdown combo box with role options
        // WHY: User must select account type (SELLER or BUYER)
        // HOW: JComboBox constructor takes array of String options
        roleComboBox = new JComboBox<>(new String[]{"SELLER", "BUYER"});
        
        // WHAT: Create save button for registration form
        // WHY: Users need button to submit registration
        // HOW: JButton with save icon emoji
        signUpButton = new JButton("Save");
        
        // WHAT: Create clear button for registration form
        // WHY: Allows users to reset form fields without closing window
        // HOW: JButton with clear icon emoji
        clearButton = new JButton("Clear");
        
        // WHAT: Create status label for registration form
        // WHY: Provides feedback about registration success/failure
        // HOW: JLabel with space character to reserve space
        signupStatusLabel = new JLabel(" ");
        
        // WHAT: Set registration status label color to error color (for errors)
        // WHY: Error color indicates validation errors or registration failures
        // HOW: ThemeColors.ERROR provides consistent error color
        signupStatusLabel.setForeground(ThemeColors.ERROR);
        
        // WHAT: Center-align registration status label text
        // WHY: Centered text is more readable
        // HOW: SwingConstants.CENTER aligns text
        signupStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // WHAT: Create font object for form fields (Segoe UI, plain style, size 13)
        // WHY: Consistent font styling across all form fields
        // HOW: Font constructor takes family name, style constant, and size
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        
        // WHAT: Apply font to all registration form text fields
        // WHY: Consistent appearance and readability
        // HOW: setFont() method applies font object to component
        signupUsernameField.setFont(fieldFont);
        signupPasswordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        nameField.setFont(fieldFont);
        roleComboBox.setFont(fieldFont);
        
        // --- Event Listeners ---
        // WHAT: Attach action listener to login button
        // WHY: Button needs to respond to clicks
        // HOW: addActionListener() registers this class (implements ActionListener) to receive events
        loginButton.addActionListener(this);
        
        // WHAT: Attach action listener to sign up button
        // WHY: Button needs to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        signUpButton.addActionListener(this);
        
        // WHAT: Attach action listener to clear button
        // WHY: Button needs to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        clearButton.addActionListener(this);
        
        // WHAT: Attach action listener to password field (Enter key triggers login)
        // WHY: Better UX - users can press Enter instead of clicking button
        // HOW: JPasswordField can fire ActionEvent when Enter is pressed
        loginPasswordField.addActionListener(this);
        
        // WHAT: Attach action listener to confirm password field (Enter key triggers save)
        // WHY: Better UX - users can press Enter to submit form
        // HOW: JPasswordField fires ActionEvent when Enter is pressed
        confirmPasswordField.addActionListener(this);
        
        // --- Button Styling ---
        // WHAT: Style login button with primary theme color
        // WHY: Primary color indicates positive action (login)
        // HOW: styleButton() helper method applies colors, fonts, and sizes
        styleButton(loginButton, ThemeColors.PRIMARY);
        
        // WHAT: Style sign up button with primary theme color
        // WHY: Primary color indicates positive action (save)
        // HOW: styleButton() applies styling
        styleButton(signUpButton, ThemeColors.PRIMARY);
        
        // WHAT: Style clear button with warning color
        // WHY: Warning color indicates secondary action (clear/reset)
        // HOW: styleButton() applies styling
        styleButton(clearButton, ThemeColors.WARNING);
    }
    
    /**
     * styleButton() - Applies consistent styling to buttons
     * WHAT: Sets button background color, text color, font, size, and cursor
     * WHY: Consistent button appearance throughout the application
     * HOW: Calls setter methods on JButton to configure appearance
     * @param button The button to style
     * @param bgColor Background color for the button
     */
    private void styleButton(JButton button, Color bgColor) {
        // WHAT: Set button background color
        // WHY: Makes buttons visually distinct and attractive
        // HOW: setBackground() applies color to button
        button.setBackground(bgColor);
        
        // WHAT: Set button text color to white
        // WHY: White text contrasts well with colored backgrounds
        // HOW: setForeground() sets text color
        button.setForeground(Color.WHITE);
        
        // WHAT: Add hover effect for primary buttons
        // WHY: Interactive feedback enhances UX
        // HOW: MouseAdapter changes background on hover
        if (bgColor.equals(ThemeColors.PRIMARY)) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(ThemeColors.PRIMARY_DARK);
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(ThemeColors.PRIMARY);
                }
            });
        }
        
        // WHAT: Set button font to Segoe UI, bold, size 14
        // WHY: Bold, larger font makes buttons more prominent
        // HOW: Font constructor creates font, setFont() applies it
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // WHAT: Disable focus painting (removes dotted border when focused)
        // WHY: Cleaner appearance - no focus indicator
        // HOW: setFocusPainted(false) removes focus border
        button.setFocusPainted(false);
        
        // WHAT: Disable border painting (removes default button border)
        // WHY: Modern flat design - no 3D border effect
        // HOW: setBorderPainted(false) removes border
        button.setBorderPainted(false);
        
        // WHAT: Set preferred button size to 200x35 pixels
        // WHY: Consistent button sizes throughout application
        // HOW: Dimension object specifies width and height
        button.setPreferredSize(new Dimension(200, 35));
        
        // WHAT: Change cursor to hand pointer when hovering over button
        // WHY: Indicates button is clickable
        // HOW: Cursor.HAND_CURSOR shows hand icon on hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * setupLayout() - Arranges components in the window
     * WHAT: Creates layout structure with header, tabbed pane, and adds components
     * WHY: Components must be organized visually in the window
     * HOW: Uses BorderLayout for main structure, GridBagLayout for form panels
     */
    private void setupLayout() {
        // WHAT: Set main window layout to BorderLayout
        // WHY: BorderLayout divides window into 5 regions (North, South, East, West, Center)
        // HOW: setLayout() sets the layout manager for the frame's content pane
        setLayout(new BorderLayout());
        
        // WHAT: Set window background color to secondary background
        // WHY: Subtle background color makes content stand out
        // HOW: ThemeColors.BACKGROUND_SECONDARY provides consistent background
        getContentPane().setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create header panel with application title
        // WHY: Header provides branding and visual hierarchy
        // HOW: createHeaderPanel() returns JPanel with title labels
        JPanel headerPanel = createHeaderPanel();
        
        // WHAT: Add header panel to top of window (North region)
        // WHY: Header should appear at top of window
        // HOW: BorderLayout.NORTH positions component at top
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create tabbed pane container for Login and Registration tabs
        // WHY: Allows switching between login and registration without separate windows
        // HOW: JTabbedPane displays tabs at top, content panels below
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        // WHAT: Set tab font to Segoe UI, plain style, size 13
        // WHY: Consistent typography with rest of application
        // HOW: setFont() applies font to tab labels
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Add Login tab with login panel as content
        // WHY: First tab allows existing users to log in
        // HOW: addTab() creates new tab with label and panel content
        tabbedPane.addTab("Login", createLoginPanel());
        
        // WHAT: Add Registration Form tab with signup panel as content
        // WHY: Second tab allows new users to create accounts
        // HOW: addTab() creates new tab with label and panel content
        tabbedPane.addTab("Registration Form", createSignUpPanel());
        
        // WHAT: Add tabbed pane to center of window (Center region)
        // WHY: Main content should be in center, taking most space
        // HOW: BorderLayout.CENTER positions component in center, expands to fill space
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * createHeaderPanel() - Creates the header with application title
     * WHAT: Builds a green header panel with title and subtitle labels
     * WHY: Provides visual branding and identifies the application
     * HOW: Uses BorderLayout to stack title and subtitle vertically
     * @return JPanel containing header components
     */
    private JPanel createHeaderPanel() {
        // WHAT: Create header panel with BorderLayout
        // WHY: BorderLayout allows stacking title and subtitle vertically
        // HOW: JPanel constructor takes layout manager
        JPanel header = new JPanel(new BorderLayout());
        
        // WHAT: Set header background to dark blue for better logo visibility
        // WHY: Dark blue provides better contrast for logo visibility
        // HOW: ThemeColors.HEADER_BG provides dark blue background
        header.setBackground(ThemeColors.HEADER_BG);
        
        // WHAT: Add padding around header content (20 pixels on all sides)
        // WHY: Padding prevents content from touching edges
        // HOW: EmptyBorder constructor takes top, left, bottom, right padding
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // WHAT: Create main title label with icon and text
        // WHY: Title identifies the application with visual branding
        // HOW: JLabel with text, SwingConstants.CENTER centers text
        JLabel title = new JLabel("AgriTrack", SwingConstants.CENTER);
        
        // WHAT: Set title font to Segoe UI, bold, size 28
        // WHY: Large, bold font makes title prominent
        // HOW: Font constructor creates font, setFont() applies it
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        // WHAT: Set title text color to white
        // WHY: White contrasts with dark blue background
        // HOW: ThemeColors.HEADER_TEXT provides consistent header text color
        title.setForeground(ThemeColors.HEADER_TEXT);
        
        // WHAT: Add application icon to title label
        // WHY: Visual branding with logo
        // HOW: setIcon() adds icon to label, positions text below icon
        try {
            title.setIcon(util.IconUtil.getApplicationIcon());
            title.setIconTextGap(10); // Space between icon and text
            title.setVerticalTextPosition(SwingConstants.BOTTOM); // Text below icon
            title.setHorizontalTextPosition(SwingConstants.CENTER); // Center text below icon
        } catch (Exception e) {
            // Icon loading failed, use emoji fallback
            title.setText("AgriTrack");
        }
        
        // WHAT: Create subtitle label with application description
        // WHY: Subtitle explains what the application does
        // HOW: JLabel with descriptive text, centered
        JLabel subtitle = new JLabel("Agriculture Management System", SwingConstants.CENTER);
        
        // WHAT: Set subtitle font to Segoe UI, plain style, size 14
        // WHY: Smaller, plain font differentiates from title
        // HOW: Font constructor creates font, setFont() applies it
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // WHAT: Set subtitle text color to header subtitle color
        // WHY: Light gray-blue provides subtle contrast on dark blue
        // HOW: ThemeColors.HEADER_SUBTITLE provides consistent subtitle color
        subtitle.setForeground(ThemeColors.HEADER_SUBTITLE);
        
        // WHAT: Create panel to hold title and subtitle vertically
        // WHY: BorderLayout allows stacking components vertically
        // HOW: JPanel with BorderLayout
        JPanel titlePanel = new JPanel(new BorderLayout());
        
        // WHAT: Set title panel background to header background color (matches header)
        // WHY: Consistent background color
        // HOW: ThemeColors.HEADER_BG provides consistent dark blue background
        titlePanel.setBackground(ThemeColors.HEADER_BG);
        
        // WHAT: Add title to center of title panel
        // WHY: Title should be centered horizontally
        // HOW: BorderLayout.CENTER positions component in center
        titlePanel.add(title, BorderLayout.CENTER);
        
        // WHAT: Add subtitle below title (South region)
        // WHY: Subtitle should appear below title
        // HOW: BorderLayout.SOUTH positions component at bottom
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        // WHAT: Add title panel to center of header
        // WHY: Centers the title and subtitle in header
        // HOW: BorderLayout.CENTER positions component
        header.add(titlePanel, BorderLayout.CENTER);
        
        // WHAT: Return completed header panel
        // WHY: Caller needs the panel to add to window
        // HOW: return statement returns the JPanel object
        return header;
    }
    
    /**
     * createLoginPanel() - Creates the login form panel with fixed-width, centered layout
     * WHAT: Builds a panel with username field, password field, status label, and login button
     * WHY: Provides UI for existing users to authenticate
     * HOW: Uses nested panels to center form and prevent stretching in fullscreen
     * @return JPanel containing login form components
     */
    private JPanel createLoginPanel() {
        // WHAT: Create outer panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the form content
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create inner form panel with GridBagLayout
        // WHY: GridBagLayout allows precise control over component placement
        // HOW: JPanel constructor takes layout manager
        JPanel panel = new JPanel(new GridBagLayout());
        
        // WHAT: Set panel background to white
        // WHY: White background makes form fields stand out
        // HOW: setBackground() sets color
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for form to prevent stretching
        // WHY: Form should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(400, 0));
        
        // WHAT: Add padding around panel content (30px top/bottom, 40px left/right)
        // WHY: Padding prevents components from touching edges
        // HOW: EmptyBorder constructor takes padding values
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // WHAT: Create GridBagConstraints object to control component positioning
        // WHY: GridBagLayout requires constraints to specify where/how components are placed
        // HOW: GridBagConstraints object holds positioning rules
        GridBagConstraints gbc = new GridBagConstraints();
        
        // WHAT: Set spacing between components (10 pixels on all sides)
        // WHY: Spacing prevents components from being too close together
        // HOW: insets property sets margins around components
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // WHAT: Align components to left (west) by default
        // WHY: Labels should align left for readability
        // HOW: anchor property sets component alignment
        gbc.anchor = GridBagConstraints.WEST;
        
        // --- Username Field ---
        // WHAT: Set grid position to column 0, row 0 (top-left)
        // WHY: Username field should be first in form
        // HOW: gridx and gridy specify column and row positions
        gbc.gridx = 0; gbc.gridy = 0;
        
        // WHAT: Add username label to panel at position (0,0)
        // WHY: Label identifies the input field
        // HOW: add() method adds component to panel at specified constraints
        panel.add(createLabel("Username:"), gbc);
        
        // WHAT: Move to column 1 (right of label)
        // WHY: Input field should be next to label
        // HOW: gridx = 1 moves to next column
        gbc.gridx = 1;
        
        // WHAT: Do not allow field to expand horizontally
        // WHY: Field should maintain fixed width, not stretch in fullscreen
        // HOW: fill = NONE prevents component expansion
        gbc.fill = GridBagConstraints.NONE;
        
        // WHAT: Do not give field priority for extra horizontal space
        // WHY: Field should maintain its size, not expand
        // HOW: weightx = 0 removes priority for extra space
        gbc.weightx = 0;
        
        // WHAT: Add username input field to panel
        // WHY: Users need field to type username
        // HOW: add() adds component with current constraints
        panel.add(loginUsernameField, gbc);
        
        // --- Password Field ---
        // WHAT: Move to row 1 (below username)
        // WHY: Password field should be below username
        // HOW: gridy = 1 moves to next row
        gbc.gridx = 0; gbc.gridy = 1;
        
        // WHAT: Reset fill and weightx for label
        // WHY: Label should not expand, only field should
        // HOW: NONE prevents expansion, weightx = 0 removes priority
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        
        // WHAT: Add password label to panel
        // WHY: Label identifies password field
        // HOW: add() adds component
        panel.add(createLabel("Password:"), gbc);
        
        // WHAT: Move to column 1, enable horizontal expansion
        // WHY: Password field should expand like username field
        // HOW: Set gridx, fill, and weightx
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // WHAT: Add password input field to panel
        // WHY: Users need field to type password
        // HOW: add() adds component
        panel.add(loginPasswordField, gbc);
        
        // --- Status Label ---
        // WHAT: Move to row 2, span across both columns
        // WHY: Status message should be full width below fields
        // HOW: gridy = 2, gridwidth = 2 spans two columns
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // WHAT: Add status label to panel
        // WHY: Displays login success/error messages
        // HOW: add() adds component
        panel.add(loginStatusLabel, gbc);
        
        // --- Login Button ---
        // WHAT: Move to row 3, center the button
        // WHY: Button should be centered below status label
        // HOW: gridy = 3, anchor = CENTER centers component
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // WHAT: Create panel to hold button (for centering)
        // WHY: FlowLayout centers button within its container
        // HOW: JPanel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        
        // WHAT: Add button panel to form panel
        // WHY: Button needs to be part of form layout
        // HOW: add() adds component
        panel.add(buttonPanel, gbc);
        
        // WHAT: Set preferred and maximum sizes for text fields to prevent stretching
        // WHY: Fields should maintain reasonable width even when window is maximized
        // HOW: setPreferredSize() and setMaximumSize() limit field width
        loginUsernameField.setPreferredSize(new Dimension(250, 25));
        loginUsernameField.setMaximumSize(new Dimension(250, 25));
        loginPasswordField.setPreferredSize(new Dimension(250, 25));
        loginPasswordField.setMaximumSize(new Dimension(250, 25));
        
        // WHAT: Wrap form panel in scroll pane for scrollability
        // WHY: Form should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps the panel, enables vertical scrolling
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null); // Remove default border for cleaner look
        scrollPane.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // WHAT: Center the scroll pane in outer panel
        // WHY: Form should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Return outer panel with scrollable form
        // WHY: Caller needs panel to add to tabbed pane
        // HOW: return statement
        return outerPanel;
    }
    
    /**
     * createSignUpPanel() - Creates the registration form panel with fixed-width, centered layout
     * WHAT: Builds a panel with all registration fields (username, password, confirm, name, role)
     * WHY: Provides UI for new users to create accounts
     * HOW: Uses nested panels to center form and prevent stretching in fullscreen
     * @return JPanel containing registration form components
     */
    private JPanel createSignUpPanel() {
        // WHAT: Create outer panel with BorderLayout for centering
        // WHY: BorderLayout allows centering the form content
        // HOW: JPanel with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        
        // WHAT: Create inner form panel with GridBagLayout
        // WHY: GridBagLayout allows precise control over component placement
        // HOW: JPanel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // WHAT: Set maximum width for form to prevent stretching
        // WHY: Form should maintain reasonable width even in fullscreen
        // HOW: setMaximumSize() limits panel width
        panel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension(500, 0));
        
        // WHAT: Add padding around panel content
        // WHY: Padding prevents components from touching edges
        // HOW: EmptyBorder constructor takes padding values
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // WHAT: Create constraints object for component positioning
        // WHY: GridBagLayout requires constraints
        // HOW: GridBagConstraints object
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Slightly more vertical spacing
        gbc.anchor = GridBagConstraints.WEST;
        
        // --- Username Field ---
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(signupUsernameField, gbc);
        
        // --- Password Field ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(signupPasswordField, gbc);
        
        // --- Confirm Password Field ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(confirmPasswordField, gbc);
        
        // --- Name Field ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(nameField, gbc);
        
        // --- Role Combo Box ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(createLabel("Account Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(roleComboBox, gbc);
        
        // --- Status Label ---
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(signupStatusLabel, gbc);
        
        // --- Buttons (Save and Clear) ---
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ThemeColors.BACKGROUND);
        buttonPanel.add(signUpButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, gbc);
        
        // WHAT: Set preferred and maximum sizes for text fields to prevent stretching
        // WHY: Fields should maintain reasonable width even when window is maximized
        // HOW: setPreferredSize() and setMaximumSize() limit field width
        signupUsernameField.setPreferredSize(new Dimension(250, 25));
        signupUsernameField.setMaximumSize(new Dimension(250, 25));
        signupPasswordField.setPreferredSize(new Dimension(250, 25));
        signupPasswordField.setMaximumSize(new Dimension(250, 25));
        confirmPasswordField.setPreferredSize(new Dimension(250, 25));
        confirmPasswordField.setMaximumSize(new Dimension(250, 25));
        nameField.setPreferredSize(new Dimension(250, 25));
        nameField.setMaximumSize(new Dimension(250, 25));
        roleComboBox.setPreferredSize(new Dimension(250, 25));
        roleComboBox.setMaximumSize(new Dimension(250, 25));
        
        // WHAT: Create scroll pane to wrap the form panel
        // WHY: Allows scrolling when window is minimized and form fields are cut off
        // HOW: JScrollPane wraps the panel, enables vertical scrolling
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null); // Remove default border for cleaner look
        scrollPane.setBackground(ThemeColors.BACKGROUND_SECONDARY);
        scrollPane.getViewport().setBackground(ThemeColors.BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // WHAT: Center the scroll pane in outer panel
        // WHY: Form should be centered horizontally when window is wide
        // HOW: BorderLayout.CENTER centers component
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // WHAT: Return outer panel with scrollable form
        // WHY: Caller needs panel to add to tabbed pane
        // HOW: return statement
        return outerPanel;
    }
    
    /**
     * createLabel() - Helper method to create styled labels
     * WHAT: Creates a JLabel with consistent font styling
     * WHY: Reduces code duplication, ensures consistent label appearance
     * HOW: Creates JLabel and applies font
     * @param text Label text to display
     * @return Styled JLabel object
     */
    private JLabel createLabel(String text) {
        // WHAT: Create label with specified text
        // WHY: Labels identify form fields
        // HOW: JLabel constructor takes text string
        JLabel label = new JLabel(text);
        
        // WHAT: Apply consistent font to label
        // WHY: Consistent typography throughout forms
        // HOW: setFont() applies font object
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Return completed label
        // WHY: Caller needs label to add to panel
        // HOW: return statement
        return label;
    }
    
    /**
     * actionPerformed() - Handles all button click and Enter key events
     * WHAT: Called automatically when any registered component fires an ActionEvent
     * WHY: Implements ActionListener interface - required for event handling
     * HOW: Checks event source and calls appropriate handler method
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // WHAT: Check if login button or password field (Enter key) triggered event
        // WHY: Both should trigger login process
        // HOW: getSource() returns component that fired event, == compares references
        if (e.getSource() == loginButton || e.getSource() == loginPasswordField) {
            // WHAT: Call login handler method
            // WHY: Login logic is separated into its own method for organization
            // HOW: Method call executes login process
            handleLogin();
        } 
        // WHAT: Check if sign up button or confirm password field (Enter key) triggered event
        // WHY: Both should trigger registration process
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == signUpButton || e.getSource() == confirmPasswordField) {
            // WHAT: Call registration handler method
            // WHY: Registration logic is separated into its own method
            // HOW: Method call executes registration process
            handleSignUp();
        } 
        // WHAT: Check if clear button triggered event
        // WHY: Clear button should reset form fields
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == clearButton) {
            // WHAT: Call method to clear all registration fields
            // WHY: Clear logic is separated into its own method
            // HOW: Method call clears all text fields and resets combo box
            clearSignUpFields();
        }
    }
    
    /**
     * handleLogin() - Processes user login attempt
     * WHAT: Validates input, authenticates user with database, opens main window on success
     * WHY: Separates login logic from event handling for better code organization
     * HOW: Gets input values, validates, calls UserDAO.login(), handles result
     */
    private void handleLogin() {
        // WHAT: Get username from text field and remove leading/trailing whitespace
        // WHY: trim() prevents errors from accidental spaces
        // HOW: getText() gets field value, trim() removes whitespace
        String username = loginUsernameField.getText().trim();
        
        // WHAT: Get password from password field and convert char array to String
        // WHY: getPassword() returns char[] for security, but we need String for comparison
        // HOW: new String() constructor converts char array to String
        String password = new String(loginPasswordField.getPassword());
        
        // WHAT: Validate that both username and password are not empty
        // WHY: Empty fields cannot be used for authentication
        // HOW: isEmpty() checks if string has no characters, || means OR (either empty is invalid)
        if (username.isEmpty() || password.isEmpty()) {
            // WHAT: Display error message in status label
            // WHY: User needs feedback about validation error
            // HOW: setText() updates label text
            loginStatusLabel.setText("⚠ Please enter both username and password");
            // WHAT: Exit method early without attempting login
            // WHY: Cannot proceed with empty fields
            // HOW: return statement exits method immediately
            return;
        }
        
        // WHAT: Try block to handle potential database exceptions
        // WHY: Database operations can throw exceptions that must be caught
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Create UserDAO object for database operations
            // WHY: UserDAO handles all user-related database queries
            // HOW: new UserDAO() creates instance
            UserDAO userDAO = new UserDAO();
            
            // WHAT: Attempt to authenticate user with database
            // WHY: Verifies username and password match database record
            // HOW: login() method queries database, returns User object if successful, null if failed
            User user = userDAO.login(username, password);
            
            // WHAT: Check if authentication was successful (user object not null)
            // WHY: null means no matching user found in database
            // HOW: != null checks if object exists
            if (user != null) {
                // WHAT: Display success message in status label
                // WHY: User needs confirmation that login succeeded
                // HOW: setText() updates label text
                loginStatusLabel.setText("Login successful!");
                
                // WHAT: Change status label color to green
                // WHY: Green indicates success
                // HOW: setForeground() sets text color
                loginStatusLabel.setForeground(ThemeColors.SUCCESS);
                
                // WHAT: Close the login window
                // WHY: User is authenticated, no longer need login window
                // HOW: dispose() closes and destroys the window
                this.dispose();
                
                // WHAT: Open main menu window with authenticated user
                // WHY: User should see main application interface after login
                // HOW: openMainFrame() creates and displays MainMenuFrame
                openMainFrame(user);
            } else {
                // WHAT: Display error message for failed authentication
                // WHY: User needs feedback that login failed
                // HOW: setText() updates label text
                loginStatusLabel.setText("Invalid username or password");
                
                // WHAT: Change status label color to red
                // WHY: Red indicates error
                // HOW: setForeground() sets text color
                loginStatusLabel.setForeground(ThemeColors.ERROR);
                
                // WHAT: Clear password field for security
                // WHY: Prevents password from remaining visible after failed attempt
                // HOW: setText("") clears field content
                loginPasswordField.setText("");
            }
        } catch (Exception ex) {
            // WHAT: Handle any exceptions during login process
            // WHY: Database errors, network issues, etc. must be handled gracefully
            // HOW: catch block executes when exception occurs
            // WHAT: Check if error is database lock error
            // WHY: Database lock errors need special user-friendly message
            // HOW: Check exception message for lock-related keywords
            String errorMsg = ex.getMessage();
            if (errorMsg != null && (errorMsg.contains("locked") || errorMsg.contains("in use") || errorMsg.contains("90020"))) {
                // WHAT: Display user-friendly database lock error message with detailed instructions
                // WHY: User needs clear instructions on how to fix the problem
                // HOW: setText() with helpful multi-line message using HTML
                String lockFilePath = util.DBConnection.getLockFilePath();
                loginStatusLabel.setText("<html><center><b>⚠ Database is locked!</b><br/>" +
                    "1. Close ALL H2 Console browser tabs<br/>" +
                    "2. Wait 5-10 seconds, then click Login again<br/><br/>" +
                    "<small>Or manually delete: " + lockFilePath + "</small></center></html>");
                loginStatusLabel.setForeground(ThemeColors.WARNING);
            } else {
                // WHAT: Display error message with exception details
                // WHY: User and developer need to know what went wrong
                // HOW: getMessage() gets exception description, concatenated with error text
                loginStatusLabel.setText("Error: " + (errorMsg != null ? errorMsg : ex.getClass().getSimpleName()));
                loginStatusLabel.setForeground(ThemeColors.ERROR);
            }
        }
    }
    
    /**
     * handleSignUp() - Processes new user registration
     * WHAT: Validates all registration fields, creates new user account in database
     * WHY: Separates registration logic from event handling
     * HOW: Gets all field values, validates each, calls UserDAO.signUp(), handles result
     */
    private void handleSignUp() {
        // WHAT: Get all registration form values and trim whitespace
        // WHY: Form data needed for validation and database insertion
        // HOW: getText() gets field values, trim() removes whitespace
        String username = signupUsernameField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String name = nameField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        
        // --- Validation Checks ---
        // WHAT: Validate username is not empty and at least 3 characters
        // WHY: Username must be meaningful and meet minimum length requirement
        // HOW: isEmpty() checks empty, length() gets string length, < 3 checks minimum
        if (username.isEmpty() || username.length() < 3) {
            signupStatusLabel.setText("⚠ Username must be at least 3 characters");
            signupStatusLabel.setForeground(new Color(200, 0, 0));
            return; // Exit early if validation fails
        }
        
        // WHAT: Validate password is not empty and at least 4 characters
        // WHY: Password must meet minimum security requirement
        // HOW: isEmpty() and length() check password requirements
        if (password.isEmpty() || password.length() < 4) {
            signupStatusLabel.setText("⚠ Password must be at least 4 characters");
            signupStatusLabel.setForeground(new Color(200, 0, 0));
            return;
        }
        
        // WHAT: Validate password and confirm password match
        // WHY: Prevents typos - user must type password correctly twice
        // HOW: equals() compares two strings for exact match
        if (!password.equals(confirmPassword)) {
            signupStatusLabel.setText("⚠ Passwords do not match");
            signupStatusLabel.setForeground(new Color(200, 0, 0));
            confirmPasswordField.setText(""); // Clear confirm field for retry
            return;
        }
        
        // WHAT: Validate name field is not empty
        // WHY: Name is required for user identification
        // HOW: isEmpty() checks if name has content
        if (name.isEmpty()) {
            signupStatusLabel.setText("⚠ Please enter full name");
            signupStatusLabel.setForeground(new Color(200, 0, 0));
            return;
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail (e.g., username already exists)
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Create UserDAO object for database operations
            // WHY: UserDAO handles user registration in database
            // HOW: new UserDAO() creates instance
            UserDAO userDAO = new UserDAO();
            
            // WHAT: Attempt to create new user account in database
            // WHY: Registers new user so they can log in later
            // HOW: signUp() inserts user record, returns User object if successful, throws exception if username exists
            User newUser = userDAO.signUp(username, password, name, role);
            
            // WHAT: Check if registration was successful (user object not null)
            // WHY: null means registration failed (shouldn't happen if no exception)
            // HOW: != null checks if object exists
            if (newUser != null) {
                // WHAT: Display success message in status label
                // WHY: User needs confirmation that account was created
                // HOW: setText() updates label text
                signupStatusLabel.setText("User registered successfully!");
                
                // WHAT: Change status label color to green
                // WHY: Green indicates success
                // HOW: setForeground() sets text color
                signupStatusLabel.setForeground(ThemeColors.SUCCESS);
                
                // WHAT: Show success dialog with account details
                // WHY: Provides clear confirmation with account information
                // HOW: showMessageDialog() displays modal dialog
                JOptionPane.showMessageDialog(this, 
                    "User registered successfully!\nUsername: " + username + "\nRole: " + role, 
                    "Registration Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // WHAT: Clear all registration form fields
                // WHY: Form is ready for next registration or user can switch to login
                // HOW: clearSignUpFields() resets all fields
                clearSignUpFields();
                
                // WHAT: Switch to login tab automatically
                // WHY: User can immediately log in with new account
                // HOW: setSelectedIndex(0) selects first tab (Login tab)
                tabbedPane.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            // WHAT: Handle registration exceptions (e.g., username already exists)
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Display error message with exception details
            // WHY: User needs to know why registration failed
            // HOW: getMessage() gets exception description
            signupStatusLabel.setText("Error: " + ex.getMessage());
            signupStatusLabel.setForeground(new Color(200, 0, 0));
        }
    }
    
    /**
     * clearSignUpFields() - Resets all registration form fields
     * WHAT: Clears all text fields, resets combo box to first option, clears status label
     * WHY: Allows users to start over without closing window
     * HOW: Calls setText("") on all fields, setSelectedIndex(0) on combo box
     */
    private void clearSignUpFields() {
        // WHAT: Clear username field
        // WHY: Reset form to empty state
        // HOW: setText("") sets field to empty string
        signupUsernameField.setText("");
        signupPasswordField.setText("");
        confirmPasswordField.setText("");
        nameField.setText("");
        
        // WHAT: Reset role combo box to first option (SELLER)
        // WHY: Form should start with default selection
        // HOW: setSelectedIndex(0) selects first item in combo box
        roleComboBox.setSelectedIndex(0);
        
        // WHAT: Clear status label (reset to space character)
        // WHY: Remove any previous error/success messages
        // HOW: setText(" ") sets label to space (maintains layout space)
        signupStatusLabel.setText(" ");
    }
    
    /**
     * openMainFrame() - Opens the main menu window after successful login
     * WHAT: Closes login window and displays MainMenuFrame with authenticated user
     * WHY: User should see main application interface after authentication
     * HOW: Disposes current window, creates new MainMenuFrame with user object
     * @param user Authenticated User object containing user information
     */
    private void openMainFrame(User user) {
        // WHAT: Close and destroy the login window
        // WHY: User is authenticated, login window no longer needed
        // HOW: dispose() releases window resources and removes from screen
        this.dispose();
        
        // WHAT: Create and display main menu window with user information
        // WHY: Main menu is the application's main interface after login
        // HOW: new MainMenuFrame(user) creates window, constructor makes it visible
        new MainMenuFrame(user);
    }
}
