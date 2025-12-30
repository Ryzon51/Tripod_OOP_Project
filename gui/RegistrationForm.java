package gui; // Package declaration: Groups this class with other GUI classes

import dao.UserDAO; // Import: UserDAO for user registration database operations
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import javax.swing.*; // Import: Swing components (JDialog, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.User; // Import: User model class

/**
 * RegistrationForm - Dialog for Creating New User Accounts
 * WHAT: Modal dialog form for registering new users (SELLER or BUYER roles)
 * WHY: Allows admins or authorized users to create new accounts without going through login screen
 * HOW: Extends JDialog, uses form fields with validation, saves to database via UserDAO
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener for button clicks
 */
public class RegistrationForm extends JDialog implements ActionListener {
    // WHAT: Currently logged-in user who opened this form
    // WHY: Tracks who is creating the new account (for audit purposes)
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Parent JFrame that opened this dialog
    // WHY: Needed for centering dialog relative to parent window
    // HOW: Stored as instance variable, passed from constructor
    private JFrame parentFrame;
    
    // WHAT: Text field for entering new username
    // WHY: Username is required and must be unique
    // HOW: JTextField allows single-line text input, validated for minimum length
    private JTextField usernameField;
    
    // WHAT: Password field for entering new password
    // WHY: Password is required for account security
    // HOW: JPasswordField hides input characters, validated for minimum length
    private JPasswordField passwordField;
    
    // WHAT: Password field for confirming password matches
    // WHY: Prevents typos - user must type password twice to confirm
    // HOW: JPasswordField that must match passwordField value
    private JPasswordField confirmPasswordField;
    
    // WHAT: Text field for entering user's full name
    // WHY: Name is required for display purposes
    // HOW: JTextField for text input, validated to ensure not empty
    private JTextField nameField;
    
    // WHAT: Dropdown combo box for selecting account role
    // WHY: User must select account type (SELLER or BUYER, not ADMIN)
    // HOW: JComboBox with predefined options
    private JComboBox<String> roleComboBox;
    
    // WHAT: Button to save/submit registration form
    // WHY: Triggers user account creation when clicked
    // HOW: ActionListener attached to handle click and call handleSave()
    private JButton saveButton;
    
    // WHAT: Button to clear all form fields
    // WHY: Allows users to reset form without closing and reopening
    // HOW: ActionListener attached to call clearFields() method
    private JButton clearButton;
    
    // WHAT: Button to close dialog and return to menu
    // WHY: Allows user to cancel registration
    // HOW: ActionListener attached to call confirmClose() method
    private JButton backButton;
    
    // WHAT: Label that displays registration status messages
    // WHY: Provides user feedback about registration attempts (success, validation errors)
    // HOW: Text and color change based on registration result
    private JLabel statusLabel;
    
    /**
     * Constructor - Creates and displays the RegistrationForm dialog
     * WHAT: Initializes all GUI components, sets up layout, and makes dialog visible
     * WHY: Called from MainMenuFrame to allow creating new user accounts
     * HOW: Calls helper methods to set up components and layout, then displays dialog
     * @param user Currently logged-in user who opened this form
     * @param parent Parent JFrame window
     */
    public RegistrationForm(User user, JFrame parent) {
        // WHAT: Call parent JDialog constructor with modal flag
        // WHY: Modal dialog blocks parent window until closed
        // HOW: super(parent, true) creates modal dialog
        super(parent, true);
        
        // WHAT: Store user object
        // WHY: Needed for tracking who created the account
        // HOW: this.currentUser = user assigns parameter
        this.currentUser = user;
        
        // WHAT: Store parent frame reference
        // WHY: Needed for centering dialog
        // HOW: this.parentFrame = parent assigns parameter
        this.parentFrame = parent;
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in dialog
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Set dialog title bar text
        // WHY: Identifies the dialog purpose
        // HOW: setTitle() sets title text
        setTitle("Registration Form - Create New User");
        
        // WHAT: Set dialog icon using application logo
        // WHY: Professional appearance with branded icon
        // HOW: setIconImage() sets the icon image for the dialog
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set dialog size to 550x550 pixels
        // WHY: Provides adequate space for registration form
        // HOW: setSize() sets width and height
        setSize(550, 550);
        
        // WHAT: Center dialog relative to parent window
        // WHY: Better user experience - dialog appears centered on parent
        // HOW: setLocationRelativeTo(parent) centers dialog
        setLocationRelativeTo(parent);
        
        // WHAT: Prevent user from resizing dialog
        // WHY: Maintains consistent layout
        // HOW: setResizable(false) disables resize handles
        setResizable(false);
        
        // WHAT: Prevent default close behavior
        // WHY: Want to show confirmation dialog before closing
        // HOW: DO_NOTHING_ON_CLOSE prevents automatic closing
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // WHAT: Add window close listener for confirmation dialog
        // WHY: Prevents accidental data loss
        // HOW: WindowAdapter overrides windowClosing() method
        addWindowListener(new WindowAdapter() {
            @Override
            // WHAT: Called when user clicks dialog close button (X)
            // WHY: Intercepts close event to show confirmation
            // HOW: Override windowClosing() to call confirmClose()
            public void windowClosing(WindowEvent e) {
                confirmClose(); // Show confirmation dialog
            }
        });
        
        // WHAT: Make dialog visible
        // WHY: Dialog is hidden by default
        // HOW: setVisible(true) displays dialog
        setVisible(true);
    }
    
    /**
     * confirmClose() - Shows confirmation dialog before closing
     * WHAT: Displays Yes/No dialog asking user to confirm close without saving
     * WHY: Prevents accidental data loss when user closes form
     * HOW: Uses JOptionPane to show dialog, closes only if user confirms
     */
    private void confirmClose() {
        // WHAT: Show confirmation dialog
        // WHY: Gives user chance to cancel close and save data
        // HOW: showConfirmDialog() displays modal dialog
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to close without saving?", // Message text
            "Confirm Close", // Dialog title
            JOptionPane.YES_NO_OPTION, // Show Yes and No buttons
            JOptionPane.QUESTION_MESSAGE); // Question icon
        
        // WHAT: Check if user clicked Yes
        // WHY: Only close if user confirms
        // HOW: YES_OPTION constant returned when Yes clicked
        if (confirm == JOptionPane.YES_OPTION) {
            // WHAT: Close and destroy dialog
            // WHY: User confirmed they want to close
            // HOW: dispose() closes dialog and releases resources
            dispose();
        }
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates form fields, buttons, and configures their properties
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components, sets fonts, attaches event listeners
     */
    private void initializeComponents() {
        // WHAT: Create text field for username input
        // WHY: Users need field to enter new username
        // HOW: JTextField constructor with column width
        usernameField = new JTextField(25);
        
        // WHAT: Create password field for password input
        // WHY: Users need field to enter new password
        // HOW: JPasswordField hides input characters
        passwordField = new JPasswordField(25);
        
        // WHAT: Create password field for password confirmation
        // WHY: User must type password twice to prevent typos
        // HOW: JPasswordField that will be compared with passwordField
        confirmPasswordField = new JPasswordField(25);
        
        // WHAT: Create text field for user's full name
        // WHY: Name is required field for display purposes
        // HOW: JTextField for text input
        nameField = new JTextField(25);
        
        // WHAT: Create combo box with role options (SELLER or BUYER)
        // WHY: User must select account type, ADMIN cannot be created through this form
        // HOW: JComboBox constructor with array of String options
        roleComboBox = new JComboBox<>(new String[]{"SELLER", "BUYER"});
        
        // WHAT: Create save button
        // WHY: Users need button to submit registration
        // HOW: JButton constructor with text and emoji
        saveButton = new JButton("Save");
        
        // WHAT: Create clear button
        // WHY: Allows users to reset form fields
        // HOW: JButton constructor
        clearButton = new JButton("Clear");
        
        // WHAT: Create back button
        // WHY: Allows users to close dialog and return to menu
        // HOW: JButton constructor
        backButton = new JButton("Back to Menu");
        
        // WHAT: Create status label for displaying messages
        // WHY: Provides feedback about registration success/failure
        // HOW: JLabel with space character to reserve space
        statusLabel = new JLabel(" ");
        
        // WHAT: Set status label text color to red (for errors)
        // WHY: Red indicates validation errors or registration failures
        // HOW: setForeground() sets text color
        statusLabel.setForeground(new Color(200, 0, 0));
        
        // WHAT: Center-align status label text
        // WHY: Centered text is more readable
        // HOW: SwingConstants.CENTER aligns text horizontally
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // WHAT: Style buttons with different colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(saveButton, new Color(76, 175, 80)); // Green
        styleButton(clearButton, new Color(255, 152, 0)); // Orange
        styleButton(backButton, new Color(158, 158, 158)); // Gray
        
        // WHAT: Attach action listeners to all buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        saveButton.addActionListener(this);
        clearButton.addActionListener(this);
        backButton.addActionListener(this);
        
        // WHAT: Attach action listener to confirm password field (Enter key triggers save)
        // WHY: Better UX - users can press Enter to submit form
        // HOW: JPasswordField can fire ActionEvent when Enter is pressed
        confirmPasswordField.addActionListener(this);
        
        // WHAT: Create font object for form fields
        // WHY: Consistent typography across all fields
        // HOW: Font constructor creates font
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        
        // WHAT: Apply font to all text fields and combo box
        // WHY: Consistent appearance
        // HOW: setFont() applies font to each component
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        nameField.setFont(fieldFont);
        roleComboBox.setFont(fieldFont);
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
        button.setPreferredSize(new Dimension(140, 35)); // Set button size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
    }
    
    /**
     * setupLayout() - Arranges components in dialog
     * WHAT: Creates layout structure with header, form fields, and buttons
     * WHY: Components must be organized visually
     * HOW: Uses BorderLayout for main structure, GridBagLayout for form fields
     */
    private void setupLayout() {
        // WHAT: Set main dialog layout to BorderLayout
        // WHY: BorderLayout divides dialog into regions
        // HOW: setLayout() sets layout manager
        setLayout(new BorderLayout());
        
        // WHAT: Set dialog background color
        // WHY: Subtle background makes content stand out
        // HOW: getContentPane().setBackground() sets color
        getContentPane().setBackground(new Color(245, 245, 250));
        
        // WHAT: Create header panel with title
        // WHY: Header provides visual identification
        // HOW: JPanel with BorderLayout, green background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 125, 50)); // Green
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25)); // Padding
        
        // WHAT: Create title label
        // WHY: Identifies the form purpose
        // HOW: JLabel with text and icon
        JLabel title = new JLabel("Registration Form");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        // WHAT: Add application icon to title label
        // WHY: Visual branding with logo
        // HOW: setIcon() adds icon to label
        try {
            title.setIcon(util.IconUtil.getSmallIcon());
            title.setIconTextGap(8); // Space between icon and text
            title.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of icon
        } catch (Exception e) {
            // Icon loading failed, use emoji fallback
            title.setText("Registration Form");
        }
        headerPanel.add(title, BorderLayout.CENTER);
        
        // WHAT: Add header to top of dialog
        // WHY: Header should appear at top
        // HOW: BorderLayout.NORTH positions at top
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create center panel with GridBagLayout for form fields
        // WHY: GridBagLayout allows precise field positioning
        // HOW: JPanel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // WHAT: Create constraints for component positioning
        // WHY: GridBagLayout requires constraints
        // HOW: GridBagConstraints object
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Spacing between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to left
        
        // --- Username Field ---
        gbc.gridx = 0; gbc.gridy = 0; // Position (0,0)
        centerPanel.add(createLabel("Username:"), gbc); // Add label
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; // Move to column 1, allow expansion
        gbc.weightx = 1.0; // Give priority for extra space
        centerPanel.add(usernameField, gbc); // Add text field
        
        // --- Password Field ---
        gbc.gridx = 0; gbc.gridy = 1; // Move to row 1
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Reset for label
        centerPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(passwordField, gbc);
        
        // --- Confirm Password Field ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(confirmPasswordField, gbc);
        
        // --- Name Field ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(nameField, gbc);
        
        // --- Role Combo Box ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Account Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(roleComboBox, gbc);
        
        // --- Status Label ---
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across both columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(statusLabel, gbc);
        
        // WHAT: Wrap center panel in scroll pane for scrollability
        // WHY: Form should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps centerPanel with scroll policies
        JScrollPane centerScrollPane = new JScrollPane(centerPanel);
        centerScrollPane.setBorder(null);
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.getViewport().setBackground(Color.WHITE);
        
        // WHAT: Add scroll pane to dialog center
        // WHY: Scrollable content should be in center
        // HOW: BorderLayout.CENTER positions in center
        add(centerScrollPane, BorderLayout.CENTER);
        
        // WHAT: Create south panel with buttons
        // WHY: Buttons should appear at bottom
        // HOW: JPanel with FlowLayout, centered
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        southPanel.setBackground(new Color(245, 245, 250));
        southPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        southPanel.add(saveButton);
        southPanel.add(clearButton);
        southPanel.add(backButton);
        
        // WHAT: Add south panel to bottom of dialog
        // WHY: Buttons should appear at bottom
        // HOW: BorderLayout.SOUTH positions at bottom
        add(southPanel, BorderLayout.SOUTH);
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
        JLabel label = new JLabel(text); // Create label
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Apply font
        return label; // Return completed label
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
        // WHAT: Check if save button or confirm password field (Enter key) triggered event
        // WHY: Both should trigger registration process
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == saveButton || e.getSource() == confirmPasswordField) {
            // WHAT: Call registration handler method
            // WHY: Registration logic is separated into its own method
            // HOW: Method call executes registration process
            handleSave();
        } 
        // WHAT: Check if clear button triggered event
        // WHY: Clear button should reset form fields
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == clearButton) {
            // WHAT: Call method to clear all form fields
            // WHY: Clear logic is separated into its own method
            // HOW: Method call clears all text fields and resets combo box
            clearFields();
        } 
        // WHAT: Check if back button triggered event
        // WHY: Back button should close dialog
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == backButton) {
            // WHAT: Show confirmation dialog before closing
            // WHY: Prevents accidental data loss
            // HOW: confirmClose() shows dialog and closes if confirmed
            confirmClose();
        }
    }
    
    /**
     * handleSave() - Processes new user registration
     * WHAT: Validates all registration fields, creates new user account in database
     * WHY: Separates registration logic from event handling
     * HOW: Gets all field values, validates each, calls UserDAO.signUp(), handles result
     */
    private void handleSave() {
        // WHAT: Get all registration form values and trim whitespace
        // WHY: Form data needed for validation and database insertion
        // HOW: getText() gets field values, trim() removes whitespace
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()); // Convert char array to String
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String name = nameField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        
        // --- Validation Checks ---
        // WHAT: Validate username is not empty and at least 3 characters
        // WHY: Username must be meaningful and meet minimum length requirement
        // HOW: isEmpty() checks empty, length() gets string length, < 3 checks minimum
        if (username.isEmpty() || username.length() < 3) {
            // WHAT: Display error message in status label
            // WHY: User needs feedback about validation error
            // HOW: setText() updates label text
            statusLabel.setText("⚠ Username must be at least 3 characters");
            statusLabel.setForeground(new Color(200, 0, 0)); // Red color
            return; // Exit early if validation fails
        }
        
        // WHAT: Validate password is not empty and at least 4 characters
        // WHY: Password must meet minimum security requirement
        // HOW: isEmpty() and length() check password requirements
        if (password.isEmpty() || password.length() < 4) {
            statusLabel.setText("⚠ Password must be at least 4 characters");
            statusLabel.setForeground(new Color(200, 0, 0));
            return;
        }
        
        // WHAT: Validate password and confirm password match
        // WHY: Prevents typos - user must type password correctly twice
        // HOW: equals() compares two strings for exact match
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("⚠ Passwords do not match");
            statusLabel.setForeground(new Color(200, 0, 0));
            confirmPasswordField.setText(""); // Clear confirm field for retry
            return;
        }
        
        // WHAT: Validate name field is not empty
        // WHY: Name is required for user identification
        // HOW: isEmpty() checks if name has content
        if (name.isEmpty()) {
            statusLabel.setText("⚠ Please enter full name");
            statusLabel.setForeground(new Color(200, 0, 0));
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
                statusLabel.setText("User registered successfully!");
                
                // WHAT: Change status label color to green
                // WHY: Green indicates success
                // HOW: setForeground() sets text color
                statusLabel.setForeground(new Color(76, 175, 80));
                
                // WHAT: Show success dialog with account details
                // WHY: Provides clear confirmation with account information
                // HOW: showMessageDialog() displays modal dialog
                JOptionPane.showMessageDialog(this, 
                    "User registered successfully!\nUsername: " + username + "\nRole: " + role, // Message with details
                    "Registration Success", // Dialog title
                    JOptionPane.INFORMATION_MESSAGE); // Information icon
                
                // WHAT: Clear all registration form fields
                // WHY: Form is ready for next registration
                // HOW: clearFields() resets all fields
                clearFields();
            }
        } catch (Exception ex) {
            // WHAT: Handle registration exceptions (e.g., username already exists)
            // WHY: Database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Display error message with exception details
            // WHY: User needs to know why registration failed
            // HOW: getMessage() gets exception description
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(new Color(200, 0, 0));
        }
    }
    
    /**
     * clearFields() - Resets all registration form fields
     * WHAT: Clears all text fields, resets combo box to first option, clears status label
     * WHY: Allows users to start over without closing window
     * HOW: Calls setText("") on all fields, setSelectedIndex(0) on combo box
     */
    private void clearFields() {
        // WHAT: Clear all text fields
        // WHY: Reset form to empty state
        // HOW: setText("") sets field to empty string
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        nameField.setText("");
        
        // WHAT: Reset role combo box to first option (SELLER)
        // WHY: Form should start with default selection
        // HOW: setSelectedIndex(0) selects first item in combo box
        roleComboBox.setSelectedIndex(0);
        
        // WHAT: Clear status label (reset to space character)
        // WHY: Remove any previous error/success messages
        // HOW: setText(" ") sets label to space (maintains layout space)
        statusLabel.setText(" ");
    }
}
