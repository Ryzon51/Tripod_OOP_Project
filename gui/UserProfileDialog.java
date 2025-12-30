package gui; // Package declaration: Groups this class with other GUI classes

import dao.UserDAO; // Import: UserDAO for updating user location
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import javax.swing.*; // Import: Swing components (JDialog, JPanel, JButton, etc.)
import javax.swing.border.CompoundBorder; // Import: EmptyBorder for padding/margins
import javax.swing.border.EmptyBorder; // Import: CompoundBorder for layered borders
import javax.swing.border.LineBorder; // Import: LineBorder for solid borders
import model.User; // Import: User model class

/**
 * UserProfileDialog - Professional User Profile Management Dialog
 * WHAT: Modal dialog for users to set or update their location/warehouse information
 * WHY: Sellers and buyers need to provide location for delivery and pickup coordination
 * HOW: Extends JDialog, uses form fields, validates input, saves to database via UserDAO
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener for button clicks
 */
public class UserProfileDialog extends JDialog implements ActionListener {
    // WHAT: Currently logged-in user object
    // WHY: Needed to update user's location information
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Parent JFrame that opened this dialog
    // WHY: Needed for centering dialog relative to parent window
    // HOW: Stored as instance variable, passed from constructor
    private JFrame parentFrame;
    
    // WHAT: Text field for entering location/warehouse address
    // WHY: Users need field to enter their location
    // HOW: JTextField for text input
    private JTextField locationField;
    
    // WHAT: Label displaying current location
    // WHY: Shows existing location if already set
    // HOW: JLabel with formatted text
    private JLabel currentLocationLabel;
    
    // WHAT: Button to save location information
    // WHY: Users need button to submit location
    // HOW: ActionListener attached to call saveLocation() method
    private JButton saveButton;
    
    // WHAT: Button to cancel and close dialog
    // WHY: Allows user to close without saving
    // HOW: ActionListener attached to call dispose() method
    private JButton cancelButton;
    
    /**
     * Constructor - Creates user profile dialog
     * WHAT: Initializes dialog with user information and location field
     * WHY: Called when user wants to set or update their location
     * HOW: Sets up GUI components, populates existing location, displays dialog
     * @param parent Parent JFrame window
     * @param user Currently logged-in user
     */
    public UserProfileDialog(JFrame parent, User user) {
        // WHAT: Call parent JDialog constructor with modal flag
        // WHY: Modal dialog blocks parent window until closed
        // HOW: super(parent, true) creates modal dialog
        super(parent, true);
        
        // WHAT: Store user and parent objects
        // WHY: Needed throughout the dialog
        // HOW: Assign parameters to instance fields
        this.currentUser = user;
        this.parentFrame = parent;
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in dialog
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Set dialog title
        // WHY: Identifies the dialog purpose
        // HOW: setTitle() sets title text
        setTitle("User Profile - Location Information");
        
        // WHAT: Set dialog icon using application logo
        // WHY: Professional appearance with branded icon
        // HOW: setIconImage() sets the icon image for the dialog
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set dialog size to 600x400 pixels
        // WHY: Provides adequate space for form fields
        // HOW: setSize() sets width and height
        setSize(600, 400);
        
        // WHAT: Center dialog relative to parent window
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(parent) centers dialog
        setLocationRelativeTo(parent);
        
        // WHAT: Prevent user from resizing dialog
        // WHY: Maintains consistent layout
        // HOW: setResizable(false) disables resize handles
        setResizable(false);
        
        // WHAT: Make dialog visible
        // WHY: Dialog is hidden by default
        // HOW: setVisible(true) displays dialog
        setVisible(true);
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates form fields, buttons, labels, and configures their properties
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components, sets fonts, populates existing location
     */
    private void initializeComponents() {
        // WHAT: Create text field for location input
        // WHY: Users need field to enter location
        // HOW: JTextField constructor
        locationField = new JTextField(40);
        locationField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Populate location field with existing location if available
        // WHY: User should see current location when editing
        // HOW: getLocation() gets location, setText() sets field
        if (currentUser.getLocation() != null && !currentUser.getLocation().isEmpty()) {
            locationField.setText(currentUser.getLocation());
        }
        
        // WHAT: Create label showing current location
        // WHY: Provides context about existing location
        // HOW: JLabel with formatted text
        String locationText = currentUser.getLocation();
        if (locationText != null && !locationText.isEmpty()) {
            currentLocationLabel = new JLabel("Current Location: " + locationText);
        } else {
            currentLocationLabel = new JLabel("Current Location: Not set");
        }
        currentLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currentLocationLabel.setForeground(new Color(117, 117, 117));
        
        // WHAT: Create save button
        // WHY: Users need button to submit location
        // HOW: JButton constructor
        saveButton = new JButton("Save Location");
        
        // WHAT: Create cancel button
        // WHY: Users need button to cancel
        // HOW: JButton constructor
        cancelButton = new JButton("Cancel");
        
        // WHAT: Style buttons with colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(saveButton, new Color(76, 175, 80), new Color(56, 142, 60)); // Green
        styleButton(cancelButton, new Color(158, 158, 158), new Color(117, 117, 117)); // Gray
        
        // WHAT: Attach action listeners to buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new CompoundBorder(
            new LineBorder(hoverColor, 1, true),
            new EmptyBorder(10, 20, 10, 20)
        ));
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
     * setupLayout() - Arranges components in dialog with professional layout
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
        getContentPane().setBackground(new Color(248, 249, 250));
        
        // WHAT: Create header panel with title
        // WHY: Header provides visual identification
        // HOW: JPanel with BorderLayout, green background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 125, 50));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel title = new JLabel("Location & Warehouse Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
            title.setText("Location & Warehouse Information");
        }
        headerPanel.add(title, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create center panel with GridBagLayout
        // WHY: GridBagLayout allows precise field positioning
        // HOW: JPanel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // User info
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel userInfoLabel = new JLabel("User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userInfoLabel.setForeground(new Color(33, 150, 243));
        centerPanel.add(userInfoLabel, gbc);
        
        // Current location
        gbc.gridy = 1;
        centerPanel.add(currentLocationLabel, gbc);
        
        // Separator
        gbc.gridy = 2;
        centerPanel.add(new JSeparator(), gbc);
        
        // Location field
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Location/Warehouse Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(locationField, gbc);
        
        // Help text
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel helpLabel = new JLabel("<html><div style='color: #757575; font-size: 11px;'>" +
            "Enter your complete address, warehouse location, or pickup point.<br>" +
            "This information helps buyers and sellers coordinate deliveries and pickups.</div></html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        centerPanel.add(helpLabel, gbc);
        
        // WHAT: Wrap center panel in scroll pane for scrollability
        // WHY: Form should be scrollable when window is minimized or fullscreen
        // HOW: JScrollPane wraps centerPanel with scroll policies
        JScrollPane centerScrollPane = new JScrollPane(centerPanel);
        centerScrollPane.setBorder(null);
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.getViewport().setBackground(Color.WHITE);
        
        add(centerScrollPane, BorderLayout.CENTER);
        
        // Footer with buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        footerPanel.add(cancelButton);
        footerPanel.add(saveButton);
        
        add(footerPanel, BorderLayout.SOUTH);
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
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
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
        // WHAT: Check if save button was clicked
        // WHY: Need to save location
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == saveButton) {
            // WHAT: Save location information
            // WHY: User confirmed save
            // HOW: saveLocation() validates and saves location
            saveLocation();
        }
        // WHAT: Check if cancel button was clicked
        // WHY: Need to close dialog
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == cancelButton) {
            // WHAT: Close dialog
            // WHY: User cancelled
            // HOW: dispose() closes and destroys dialog
            dispose();
        }
    }
    
    /**
     * saveLocation() - Validates and saves location information
     * WHAT: Validates location input, updates user in database, shows confirmation
     * WHY: Separates save logic from event handling
     * HOW: Gets location text, validates, calls UserDAO.updateUserLocation(), shows success message
     */
    private void saveLocation() {
        // WHAT: Get location from text field and remove whitespace
        // WHY: trim() prevents errors from accidental spaces
        // HOW: getText() gets field value, trim() removes whitespace
        String location = locationField.getText().trim();
        
        // WHAT: Validate location is not empty
        // WHY: Location should not be empty
        // HOW: isEmpty() checks if string has no characters
        if (location.isEmpty()) {
            // WHAT: Show error message dialog
            // WHY: User needs feedback about validation error
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Please enter a location/warehouse address", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Create UserDAO object for database operations
            // WHY: UserDAO handles user location updates
            // HOW: new UserDAO() creates instance
            UserDAO userDAO = new UserDAO();
            
            // WHAT: Update user location in database
            // WHY: Save location information
            // HOW: updateUserLocation() executes UPDATE SQL statement
            userDAO.updateUserLocation(currentUser.getId(), location);
            
            // WHAT: Update current user object with new location
            // WHY: Keep user object in sync with database
            // HOW: setLocation() updates user object
            currentUser.setLocation(location);
            
            // WHAT: Show success message dialog
            // WHY: User needs confirmation that location was saved
            // HOW: showMessageDialog() displays success message
            JOptionPane.showMessageDialog(this, "Location saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // WHAT: Close dialog
            // WHY: Location is saved
            // HOW: dispose() closes and destroys dialog
            dispose();
            
        } catch (Exception ex) {
            // WHAT: Handle database errors
            // WHY: Database operations can fail
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message dialog with exception details
            // WHY: User needs to know what went wrong
            // HOW: getMessage() gets exception description, showMessageDialog() displays it
            JOptionPane.showMessageDialog(this, "Error saving location: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}




