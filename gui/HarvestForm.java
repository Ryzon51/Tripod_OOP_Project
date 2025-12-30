package gui; // Package declaration: Groups this class with other GUI classes

import dao.InventoryDAO; // Import: InventoryDAO for database operations (add, update)
import java.awt.*; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionEvent; // Import: ActionEvent for button click events
import java.awt.event.ActionListener; // Import: ActionListener interface for event handling
import java.awt.event.WindowAdapter; // Import: WindowAdapter for window close events
import java.awt.event.WindowEvent; // Import: WindowEvent for window state changes
import java.time.LocalDate; // Import: LocalDate for date handling
import java.time.format.DateTimeParseException; // Import: DateTimeParseException for date parsing errors
import javax.swing.*; // Import: Swing components (JDialog, JPanel, JButton, JTextField, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import model.FarmItem; // Import: FarmItem base class
import model.HarvestLot; // Import: HarvestLot class for creating/editing harvest records
import model.User; // Import: User model class

/**
 * HarvestForm - Dialog for Adding and Editing Harvest Records
 * WHAT: Modal dialog form for creating new harvest records or editing existing ones
 * WHY: Provides user-friendly interface for harvest data entry with validation
 * HOW: Extends JDialog, uses form fields, validates input, saves to database via InventoryDAO
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener for button clicks
 */
public class HarvestForm extends JDialog implements ActionListener {
    // WHAT: Currently logged-in user object
    // WHY: Needed to track who is creating/editing the record
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: Parent JFrame that opened this dialog
    // WHY: Needed for centering dialog relative to parent window
    // HOW: Stored as instance variable, passed from constructor
    private JFrame parentFrame;
    
    // WHAT: FarmItem object being edited (null if adding new record)
    // WHY: When editing, need to populate form with existing data
    // HOW: null for new records, non-null for editing existing records
    private FarmItem editingItem;
    
    // WHAT: Text field for entering harvest name (e.g., "Rice", "Corn")
    // WHY: Name is required field for identifying the harvest
    // HOW: JTextField allows single-line text input
    private JTextField nameField;
    
    // WHAT: Text field for entering harvest quantity
    // WHY: Quantity is required numeric field
    // HOW: JTextField for text input, validated and parsed to double
    private JTextField quantityField;
    
    // WHAT: Dropdown combo box for selecting unit of measurement
    // WHY: Quantity needs unit (kg, tons, bags, pieces, liters)
    // HOW: JComboBox with predefined unit options
    private JComboBox<String> unitComboBox;
    
    // WHAT: Text field for entering date (format: YYYY-MM-DD)
    // WHY: Date is required field for tracking when harvest was recorded
    // HOW: JTextField with date string, validated and parsed to LocalDate
    private JTextField dateField;
    
    // WHAT: Dropdown combo box for selecting harvest status
    // WHY: Status tracks availability (Available, Fresh, Processed, Stored)
    // HOW: JComboBox with status options, defaults to "Available"
    private JComboBox<String> statusComboBox;
    
    // WHAT: Text field for entering price per unit (e.g., 57.00 per kg)
    // WHY: Sellers need to set prices for their harvests
    // HOW: JTextField for numeric input, validated and parsed to double
    private JTextField priceField;
    
    // WHAT: Text area for entering optional notes
    // WHY: Allows users to add additional information about harvest
    // HOW: JTextArea with line wrapping for multi-line text
    private JTextArea notesArea;
    
    // WHAT: Button to save/submit the form
    // WHY: Triggers validation and database save operation
    // HOW: ActionListener attached to call saveRecord() method
    private JButton saveButton;
    
    // WHAT: Button to cancel and close dialog
    // WHY: Allows user to close without saving
    // HOW: ActionListener attached to call confirmClose() method
    private JButton cancelButton;
    
    /**
     * Constructor - Creates form for adding new harvest record
     * WHAT: Initializes form with empty fields for new record
     * WHY: Sellers need to add new harvest records
     * HOW: Calls main constructor with null editingItem parameter
     * @param user Currently logged-in user
     * @param parent Parent JFrame window
     */
    public HarvestForm(User user, JFrame parent) {
        // WHAT: Call main constructor with null editingItem
        // WHY: null indicates this is a new record, not editing existing
        // HOW: this() calls other constructor with null as third parameter
        this(user, parent, null);
    }
    
    /**
     * Constructor - Creates form for adding or editing harvest record
     * WHAT: Initializes form, populates fields if editing existing record
     * WHY: Handles both add and edit scenarios in one constructor
     * HOW: Checks if editingItem is null (add) or not (edit), populates fields accordingly
     * @param user Currently logged-in user
     * @param parent Parent JFrame window
     * @param item FarmItem to edit (null for new record)
     */
    public HarvestForm(User user, JFrame parent, FarmItem item) {
        // WHAT: Call parent JDialog constructor with modal flag
        // WHY: Modal dialog blocks parent window until closed
        // HOW: super(parent, true) creates modal dialog
        super(parent, true);
        
        // WHAT: Store user object
        // WHY: Needed for tracking who created/edited record
        // HOW: this.currentUser = user assigns parameter
        this.currentUser = user;
        
        // WHAT: Store parent frame reference
        // WHY: Needed for centering dialog
        // HOW: this.parentFrame = parent assigns parameter
        this.parentFrame = parent;
        
        // WHAT: Store item being edited (null if adding new)
        // WHY: Determines if form is for add or edit
        // HOW: this.editingItem = item assigns parameter
        this.editingItem = item;
        
        // WHAT: Initialize all GUI components
        // WHY: Components must be created before adding to layout
        // HOW: Calls initializeComponents() method
        initializeComponents();
        
        // WHAT: Arrange components in dialog
        // WHY: Components need to be positioned visually
        // HOW: Calls setupLayout() method
        setupLayout();
        
        // WHAT: Set dialog title based on add/edit mode
        // WHY: Title should indicate whether adding or editing
        // HOW: Ternary operator checks if editingItem is null
        setTitle(item == null ? "Add Harvest Record" : "Edit Harvest Record");
        
        // WHAT: Set dialog icon using application logo
        // WHY: Professional appearance with branded icon
        // HOW: setIconImage() sets the icon image for the dialog
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set dialog size to 550x550 pixels
        // WHY: Provides adequate space for form fields including price field
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
     * HOW: Creates Swing components, sets fonts, populates fields if editing
     */
    private void initializeComponents() {
        // WHAT: Create text field for harvest name
        // WHY: Users need field to enter harvest name
        // HOW: JTextField constructor with column width
        nameField = new JTextField(25);
        
        // WHAT: Create text field for quantity
        // WHY: Users need field to enter quantity
        // HOW: JTextField constructor
        quantityField = new JTextField(25);
        
        // WHAT: Create combo box with unit options
        // WHY: Users need to select unit of measurement
        // HOW: JComboBox constructor with array of unit strings
        unitComboBox = new JComboBox<>(new String[]{"kg", "tons", "bags", "pieces", "liters"});
        
        // WHAT: Create text field for date
        // WHY: Users need field to enter date
        // HOW: JTextField constructor
        dateField = new JTextField(25);
        
        // WHAT: Set date field to current date as default
        // WHY: Most harvests are recorded on current date
        // HOW: LocalDate.now() gets today's date, toString() converts to string
        dateField.setText(LocalDate.now().toString());
        
        // WHAT: Create combo box with status options
        // WHY: Users need to select harvest status
        // HOW: JComboBox constructor with array of status strings
        statusComboBox = new JComboBox<>(new String[]{"Available", "Fresh", "Processed", "Stored"});
        
        // WHAT: Set default status to "Available"
        // WHY: New harvests should default to available for buyers
        // HOW: setSelectedItem() selects "Available" option
        statusComboBox.setSelectedItem("Available"); // Default status
        
        // WHAT: Create text field for price per unit
        // WHY: Sellers need field to enter price
        // HOW: JTextField constructor
        priceField = new JTextField(25);
        
        // WHAT: Create text area for notes (5 rows, 25 columns)
        // WHY: Users need multi-line field for additional information
        // HOW: JTextArea constructor with row and column dimensions
        notesArea = new JTextArea(5, 25);
        
        // WHAT: Enable line wrapping in text area
        // WHY: Long lines should wrap instead of requiring horizontal scrolling
        // HOW: setLineWrap(true) enables automatic line wrapping
        notesArea.setLineWrap(true);
        
        // WHAT: Enable word wrapping (wrap at word boundaries)
        // WHY: Prevents words from being split across lines
        // HOW: setWrapStyleWord(true) wraps at word boundaries
        notesArea.setWrapStyleWord(true);
        
        // WHAT: Set font for notes area
        // WHY: Consistent typography
        // HOW: setFont() applies font object
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // WHAT: Create save button
        // WHY: Users need button to submit form
        // HOW: JButton constructor with text and emoji
        saveButton = new JButton("Save");
        
        // WHAT: Create cancel button
        // WHY: Users need button to close without saving
        // HOW: JButton constructor
        cancelButton = new JButton("Cancel");
        
        // WHAT: Style buttons with colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(saveButton, new Color(76, 175, 80)); // Green
        styleButton(cancelButton, new Color(158, 158, 158)); // Gray
        
        // WHAT: Attach action listeners to buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // WHAT: Create font object for form fields
        // WHY: Consistent typography across all fields
        // HOW: Font constructor creates font
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        
        // WHAT: Apply font to all text fields and combo boxes
        // WHY: Consistent appearance
        // HOW: setFont() applies font to each component
        nameField.setFont(fieldFont);
        quantityField.setFont(fieldFont);
        dateField.setFont(fieldFont);
        unitComboBox.setFont(fieldFont);
        statusComboBox.setFont(fieldFont);
        priceField.setFont(fieldFont);
        
        // WHAT: Check if editing existing record
        // WHY: If editing, need to populate fields with existing data
        // HOW: if statement checks if editingItem is not null
        if (editingItem != null) {
            // WHAT: Populate name field with existing value
            // WHY: User should see current name when editing
            // HOW: setText() sets field value from item
            nameField.setText(editingItem.getName());
            
            // WHAT: Populate quantity field with existing value
            // WHY: User should see current quantity when editing
            // HOW: String.valueOf() converts double to string, setText() sets field
            quantityField.setText(String.valueOf(editingItem.getQuantity()));
            
            // WHAT: Select existing unit in combo box
            // WHY: User should see current unit when editing
            // HOW: setSelectedItem() selects matching option
            unitComboBox.setSelectedItem(editingItem.getUnit());
            
            // WHAT: Populate date field with existing value
            // WHY: User should see current date when editing
            // HOW: toString() converts LocalDate to string, setText() sets field
            dateField.setText(editingItem.getDateAdded().toString());
            
            // WHAT: Populate notes area with existing value
            // WHY: User should see current notes when editing
            // HOW: setText() sets text area content
            notesArea.setText(editingItem.getNotes());
            
            // WHAT: Check if item is HarvestLot (has status field)
            // WHY: Only HarvestLot has status, other types may not
            // HOW: instanceof operator checks type
            if (editingItem instanceof HarvestLot) {
                HarvestLot harvest = (HarvestLot) editingItem;
                // WHAT: Select existing status in combo box
                // WHY: User should see current status when editing
                // HOW: Cast to HarvestLot, getStatus(), setSelectedItem()
                statusComboBox.setSelectedItem(harvest.getStatus());
                // WHAT: Populate price field with existing value
                // WHY: User should see current price when editing
                // HOW: getPricePerUnit() gets price, setText() sets field
                if (harvest.getPricePerUnit() != null) {
                    priceField.setText(String.valueOf(harvest.getPricePerUnit()));
                }
            }
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
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // Padding
        
        // WHAT: Create title label (different text for add vs edit)
        // WHY: Title should indicate whether adding or editing
        // HOW: Ternary operator checks editingItem, creates appropriate label
        String titleText = editingItem == null ? "Add New Harvest Record" : "Edit Harvest Record";
        JLabel title = new JLabel(titleText);
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
            title.setText(editingItem == null ? "Add New Harvest Record" : "Edit Harvest Record");
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
        centerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // WHAT: Create constraints for component positioning
        // WHY: GridBagLayout requires constraints
        // HOW: GridBagConstraints object
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to left
        
        // --- Name Field ---
        gbc.gridx = 0; gbc.gridy = 0; // Position (0,0)
        centerPanel.add(createLabel("Name:"), gbc); // Add label
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; // Move to column 1, allow expansion
        gbc.weightx = 1.0; // Give priority for extra space
        centerPanel.add(nameField, gbc); // Add text field
        
        // --- Quantity Field ---
        gbc.gridx = 0; gbc.gridy = 1; // Move to row 1
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Reset for label
        centerPanel.add(createLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(quantityField, gbc);
        
        // --- Unit Combo Box ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Unit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(unitComboBox, gbc);
        
        // --- Date Field ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(dateField, gbc);
        
        // --- Status Combo Box ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(statusComboBox, gbc);
        
        // --- Price Field ---
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Price per Unit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(priceField, gbc);
        
        // --- Notes Text Area ---
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align label to top-left
        centerPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; // Allow both horizontal and vertical expansion
        gbc.weightx = 1.0; gbc.weighty = 1.0; // Give priority for extra space
        centerPanel.add(new JScrollPane(notesArea), gbc); // Wrap in JScrollPane for scrolling
        
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
        // HOW: JPanel with FlowLayout, right-aligned
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        southPanel.setBackground(new Color(245, 245, 250));
        southPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        southPanel.add(saveButton);
        southPanel.add(cancelButton);
        
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
     * actionPerformed() - Handles all button click events
     * WHAT: Called automatically when any registered button is clicked
     * WHY: Implements ActionListener interface - required for event handling
     * HOW: Checks event source and calls appropriate handler method
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // WHAT: Check if save button was clicked
        // WHY: Need to validate and save record
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == saveButton) {
            // WHAT: Call method to save record
            // WHY: Save logic is separated into its own method
            // HOW: Method call executes save process
            saveRecord();
        } 
        // WHAT: Check if cancel button was clicked
        // WHY: Need to close dialog without saving
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == cancelButton) {
            // WHAT: Show confirmation dialog before closing
            // WHY: Prevents accidental data loss
            // HOW: confirmClose() shows dialog and closes if confirmed
            confirmClose();
        }
    }
    
    /**
     * saveRecord() - Validates and saves harvest record to database
     * WHAT: Validates all form fields, creates/updates HarvestLot object, saves via DAO
     * WHY: Separates save logic from event handling for better code organization
     * HOW: Gets field values, validates each, creates HarvestLot, calls DAO add/update method
     */
    private void saveRecord() {
        // WHAT: Get name from text field and remove whitespace
        // WHY: trim() prevents errors from accidental spaces
        // HOW: getText() gets field value, trim() removes whitespace
        String name = nameField.getText().trim();
        
        // WHAT: Validate name is not empty
        // WHY: Name is required field
        // HOW: isEmpty() checks if string has no characters
        if (name.isEmpty()) {
            // WHAT: Show error message dialog
            // WHY: User needs feedback about validation error
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Please enter a name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Parse quantity from text field
        // WHY: Quantity must be a valid positive number
        // HOW: Try-catch block attempts to parse string to double
        double quantity;
        try {
            // WHAT: Convert quantity string to double
            // WHY: Database stores quantity as DOUBLE
            // HOW: Double.parseDouble() converts string to double
            quantity = Double.parseDouble(quantityField.getText().trim());
            
            // WHAT: Validate quantity is positive
            // WHY: Negative or zero quantities don't make sense
            // HOW: if statement checks if quantity <= 0
            if (quantity <= 0) {
                // WHAT: Throw exception to trigger catch block
                // WHY: Invalid quantity should be treated as parsing error
                // HOW: throw new NumberFormatException() triggers catch
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            // WHAT: Handle invalid quantity input
            // WHY: User needs feedback about validation error
            // HOW: catch block executes when parsing fails
            // WHAT: Show error message dialog
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Get selected unit from combo box
        // WHY: Unit is required field
        // HOW: getSelectedItem() returns selected option, cast to String
        String unit = (String) unitComboBox.getSelectedItem();
        
        // WHAT: Parse date from text field
        // WHY: Date must be valid LocalDate object
        // HOW: Try-catch block attempts to parse date string
        LocalDate date;
        try {
            // WHAT: Convert date string to LocalDate
            // WHY: Database and FarmItem use LocalDate for dates
            // HOW: LocalDate.parse() converts yyyy-MM-dd string to LocalDate
            date = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            // WHAT: Handle invalid date format
            // WHY: User needs feedback about validation error
            // HOW: catch block executes when parsing fails
            // WHAT: Show error message dialog
            // WHY: User needs to know expected date format
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit method early
        }
        
        // WHAT: Get selected status from combo box
        // WHY: Status is required field for HarvestLot
        // HOW: getSelectedItem() returns selected option, cast to String
        String status = (String) statusComboBox.getSelectedItem();
        
        // WHAT: Parse price from text field (optional)
        // WHY: Price is optional field, but if provided must be valid positive number
        // HOW: Try-catch block attempts to parse string to double
        Double pricePerUnit = null;
        String priceText = priceField.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                // WHAT: Convert price string to double
                // WHY: Database stores price as DOUBLE
                // HOW: Double.parseDouble() converts string to double
                double price = Double.parseDouble(priceText);
                
                // WHAT: Validate price is non-negative
                // WHY: Negative prices don't make sense
                // HOW: if statement checks if price < 0
                if (price < 0) {
                    // WHAT: Show error message dialog
                    // WHY: User needs feedback about validation error
                    // HOW: showMessageDialog() displays error message
                    JOptionPane.showMessageDialog(this, "Price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit method early
                }
                pricePerUnit = price;
            } catch (NumberFormatException e) {
                // WHAT: Handle invalid price input
                // WHY: User needs feedback about validation error
                // HOW: catch block executes when parsing fails
                // WHAT: Show error message dialog
                // WHY: User needs to know what went wrong
                // HOW: showMessageDialog() displays error message
                JOptionPane.showMessageDialog(this, "Please enter a valid price (e.g., 57.00)", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit method early
            }
        }
        
        // WHAT: Get notes from text area and remove whitespace
        // WHY: Notes is optional field
        // HOW: getText() gets text area content, trim() removes whitespace
        String notes = notesArea.getText().trim();
        
        // WHAT: Try block to handle database exceptions
        // WHY: Database operations can fail
        // HOW: try-catch block wraps database code
        try {
            // WHAT: Create InventoryDAO object for database operations
            // WHY: DAO handles database insert/update operations
            // HOW: new InventoryDAO() creates instance
            InventoryDAO dao = new InventoryDAO();
            
            // WHAT: Check if adding new record or editing existing
            // WHY: Different operations needed (INSERT vs UPDATE)
            // HOW: if statement checks if editingItem is null
            if (editingItem == null) {
                // WHAT: Create new HarvestLot object with form data
                // WHY: Need FarmItem object to save to database
                // HOW: new HarvestLot() constructor with all form values, ID=0 (auto-generated)
                HarvestLot newItem = new HarvestLot(0, name, quantity, unit, date, notes, status, pricePerUnit);
                
                // WHAT: Insert new record into database
                // WHY: Save new harvest to database
                // HOW: addRecord() executes INSERT SQL statement
                dao.addRecord(newItem);
                
                // WHAT: Show success message dialog
                // WHY: User needs confirmation that record was saved
                // HOW: showMessageDialog() displays success message
                JOptionPane.showMessageDialog(this, "Harvest record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // WHAT: Create updated HarvestLot object with form data and existing ID
                // WHY: Update requires existing ID to identify which record to update
                // HOW: new HarvestLot() constructor with editingItem.getId() and new form values
                HarvestLot updatedItem = new HarvestLot(
                    editingItem.getId(), // Keep existing ID
                    name, // New name from form
                    quantity, // New quantity from form
                    unit, // New unit from form
                    date, // New date from form
                    notes, // New notes from form
                    status, // New status from form
                    pricePerUnit // New price from form
                );
                
                // WHAT: Update existing record in database
                // WHY: Save changes to existing harvest
                // HOW: updateRecord() executes UPDATE SQL statement
                dao.updateRecord(updatedItem);
                
                // WHAT: Show success message dialog
                // WHY: User needs confirmation that record was updated
                // HOW: showMessageDialog() displays success message
                JOptionPane.showMessageDialog(this, "Harvest record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // WHAT: Close dialog after successful save
            // WHY: User is done with form
            // HOW: dispose() closes and destroys dialog
            dispose();
        } catch (Exception ex) {
            // WHAT: Handle database errors
            // WHY: Database operations can fail (connection issues, constraint violations)
            // HOW: catch block executes when exception occurs
            // WHAT: Show error message dialog with exception details
            // WHY: User needs to know what went wrong
            // HOW: getMessage() gets exception description, showMessageDialog() displays it
            JOptionPane.showMessageDialog(this, "Error saving record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
