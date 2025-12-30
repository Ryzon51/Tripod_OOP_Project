package gui; // Package declaration: Groups this class with other GUI classes

import dao.InventoryDAO; // Import: InventoryDAO for updating inventory after purchase
import java.awt.*; // Import: UserDAO for getting seller location
import java.awt.event.ActionEvent; // Import: AWT classes for layout managers, colors, fonts, dimensions, cursors
import java.awt.event.ActionListener; // Import: ActionEvent for button click events
import java.awt.event.ItemEvent; // Import: ActionListener interface for event handling
import java.awt.event.ItemListener; // Import: ItemEvent for combo box changes
import java.text.DecimalFormat; // Import: ItemListener interface for combo box events
import javax.swing.*; // Import: DecimalFormat for currency formatting
import javax.swing.border.CompoundBorder; // Import: Swing components (JDialog, JPanel, JButton, etc.)
import javax.swing.border.EmptyBorder; // Import: EmptyBorder for padding/margins
import javax.swing.border.LineBorder; // Import: CompoundBorder for layered borders
import model.HarvestLot; // Import: LineBorder for solid borders
import model.User; // Import: HarvestLot class

/**
 * PurchaseDialog - Professional Purchase Interface for Buyers
 * WHAT: Modal dialog for buyers to purchase harvest items with quantity selection, delivery options, and automatic calculations
 * WHY: Provides complete purchase workflow with pricing, delivery, and payment method selection
 * HOW: Extends JDialog, uses form fields, validates input, calculates totals, updates inventory
 * 
 * OOP CONCEPT: Event-Driven Programming - implements ActionListener and ItemListener for user interactions
 */
public class PurchaseDialog extends JDialog implements ActionListener, ItemListener {
    // WHAT: Currently logged-in buyer user object
    // WHY: Needed to track who is making the purchase
    // HOW: Stored as instance variable, passed from constructor
    private User currentUser;
    
    // WHAT: HarvestLot item being purchased
    // WHY: Contains all harvest information (name, quantity, price, etc.)
    // HOW: Stored as instance variable, passed from constructor
    private HarvestLot harvestItem;
    
    // WHAT: Seller user object (who owns the harvest)
    // WHY: Needed to get seller location for pickup option
    // HOW: Retrieved from database using seller information
    private User sellerUser;
    
    // WHAT: Text field for entering purchase quantity
    // WHY: Buyers can purchase partial quantities (e.g., 100kg out of 1000kg)
    // HOW: JTextField for numeric input, validated and parsed to double
    private JTextField quantityField;
    
    // WHAT: Label displaying available quantity
    // WHY: Shows buyer how much is available
    // HOW: JLabel with formatted text
    private JLabel availableQuantityLabel;
    
    // WHAT: Label displaying price per unit
    // WHY: Shows buyer the unit price
    // HOW: JLabel with formatted currency text
    private JLabel pricePerUnitLabel;
    
    // WHAT: Dropdown combo box for selecting purchase method
    // WHY: Buyers can choose in-person or online transaction
    // HOW: JComboBox with purchase method options
    private JComboBox<String> purchaseMethodComboBox;
    
    // WHAT: Radio button group for delivery options
    // WHY: Buyers can choose delivery or pickup
    // HOW: ButtonGroup contains delivery and pickup radio buttons
    private ButtonGroup deliveryGroup;
    
    // WHAT: Radio button for delivery to buyer location
    // WHY: Buyer can choose to have item delivered
    // HOW: JRadioButton in deliveryGroup
    private JRadioButton deliveryRadioButton;
    
    // WHAT: Radio button for pickup at seller location
    // WHY: Buyer can choose to pick up at seller's location
    // HOW: JRadioButton in deliveryGroup
    private JRadioButton pickupRadioButton;
    
    // WHAT: Text field for entering delivery address
    // WHY: Required when buyer chooses delivery option
    // HOW: JTextField for address input
    private JTextField deliveryAddressField;
    
    // WHAT: Label displaying shipping fee
    // WHY: Shows calculated shipping cost
    // HOW: JLabel with formatted currency text
    private JLabel shippingFeeLabel;
    
    // WHAT: Label displaying subtotal (quantity * price)
    // WHY: Shows product cost before shipping
    // HOW: JLabel with formatted currency text
    private JLabel subtotalLabel;
    
    // WHAT: Label displaying total cost (subtotal + shipping)
    // WHY: Shows final amount buyer needs to pay
    // HOW: JLabel with formatted currency text, highlighted
    private JLabel totalLabel;
    
    // WHAT: Label displaying seller location/warehouse
    // WHY: Shows where buyer can pick up if choosing pickup
    // HOW: JLabel with seller location information
    private JLabel sellerLocationLabel;
    
    // WHAT: Button to confirm and complete purchase
    // WHY: Triggers purchase processing and inventory update
    // HOW: ActionListener attached to call processPurchase() method
    private JButton purchaseButton;
    
    // WHAT: Button to cancel and close dialog
    // WHY: Allows user to close without purchasing
    // HOW: ActionListener attached to call dispose() method
    private JButton cancelButton;
    
    // WHAT: Decimal formatter for currency display
    // WHY: Formats numbers as currency (e.g., 57.00 -> ₱57.00)
    // HOW: DecimalFormat with currency pattern
    private DecimalFormat currencyFormat;
    
    // WHAT: Constant shipping fee per unit (e.g., 5.00 per kg)
    // WHY: Shipping cost calculation based on quantity
    // HOW: Static final double constant
    private static final double SHIPPING_FEE_PER_UNIT = 5.00;
    
    /**
     * Constructor - Creates purchase dialog for buyer
     * WHAT: Initializes dialog with harvest item information and buyer details
     * WHY: Called when buyer wants to purchase a harvest item
     * HOW: Sets up GUI components, loads seller information, calculates initial values
     * @param parent Parent JFrame window
     * @param user Currently logged-in buyer user
     * @param item HarvestLot item to purchase
     */
    public PurchaseDialog(JFrame parent, User user, HarvestLot item) {
        // WHAT: Call parent JDialog constructor with modal flag
        // WHY: Modal dialog blocks parent window until closed
        // HOW: super(parent, true) creates modal dialog
        super(parent, true);
        
        // WHAT: Store user and item objects
        // WHY: Needed throughout the dialog for calculations and processing
        // HOW: Assign parameters to instance fields
        this.currentUser = user;
        this.harvestItem = item;
        
        // WHAT: Initialize currency formatter
        // WHY: Need to format prices as currency
        // HOW: DecimalFormat with currency pattern
        currencyFormat = new DecimalFormat("₱#,##0.00");
        
        // WHAT: Load seller information (if available)
        // WHY: Need seller location for pickup option
        // HOW: Try to get seller from database (may not always be available)
        try {
            // For now, seller info may not be directly linked, so we'll handle it gracefully
            sellerUser = null; // Will be set if seller information is available
        } catch (Exception e) {
            sellerUser = null;
        }
        
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
        setTitle("Purchase Harvest - " + item.getName());
        
        // WHAT: Set dialog icon using application logo
        // WHY: Professional appearance with branded icon
        // HOW: setIconImage() sets the icon image for the dialog
        try {
            setIconImage(util.IconUtil.getApplicationImage());
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // WHAT: Set dialog size to 650x700 pixels
        // WHY: Provides adequate space for all purchase options
        // HOW: setSize() sets width and height
        setSize(650, 700);
        
        // WHAT: Center dialog relative to parent window
        // WHY: Better user experience
        // HOW: setLocationRelativeTo(parent) centers dialog
        setLocationRelativeTo(parent);
        
        // WHAT: Prevent user from resizing dialog
        // WHY: Maintains consistent layout
        // HOW: setResizable(false) disables resize handles
        setResizable(false);
        
        // WHAT: Calculate and display initial values
        // WHY: Dialog should show current calculations on load
        // HOW: Calls updateCalculations() method
        updateCalculations();
        
        // WHAT: Make dialog visible
        // WHY: Dialog is hidden by default
        // HOW: setVisible(true) displays dialog
        setVisible(true);
    }
    
    /**
     * initializeComponents() - Creates all GUI components
     * WHAT: Instantiates form fields, buttons, labels, and configures their properties
     * WHY: Components must be created before adding to layout
     * HOW: Creates Swing components, sets fonts, attaches listeners
     */
    private void initializeComponents() {
        // WHAT: Create label showing available quantity
        // WHY: Buyer needs to know how much is available
        // HOW: JLabel with formatted text
        availableQuantityLabel = new JLabel("Available: " + harvestItem.getQuantity() + " " + harvestItem.getUnit());
        availableQuantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        availableQuantityLabel.setForeground(new Color(33, 150, 243));
        
        // WHAT: Create label showing price per unit
        // WHY: Buyer needs to see the unit price
        // HOW: JLabel with formatted currency text
        Double price = harvestItem.getPricePerUnit();
        if (price != null && price > 0) {
            pricePerUnitLabel = new JLabel("Price: " + currencyFormat.format(price) + " per " + harvestItem.getUnit());
        } else {
            pricePerUnitLabel = new JLabel("Price: Not set");
        }
        pricePerUnitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pricePerUnitLabel.setForeground(new Color(76, 175, 80));
        
        // WHAT: Create text field for purchase quantity
        // WHY: Buyer needs to enter desired quantity
        // HOW: JTextField constructor
        quantityField = new JTextField(20);
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // WHAT: Add action listener to quantity field
        // WHY: Recalculate totals when quantity changes
        // HOW: addActionListener() registers listener
        quantityField.addActionListener(this);
        
        // WHAT: Create combo box for purchase method
        // WHY: Buyer needs to choose in-person or online
        // HOW: JComboBox with purchase method options
        purchaseMethodComboBox = new JComboBox<>(new String[]{"In-Person", "Online Transaction"});
        purchaseMethodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        purchaseMethodComboBox.addItemListener(this);
        
        // WHAT: Create radio button group for delivery options
        // WHY: Buyer can choose delivery or pickup
        // HOW: ButtonGroup contains radio buttons
        deliveryGroup = new ButtonGroup();
        
        // WHAT: Create delivery radio button
        // WHY: Buyer can choose delivery option
        // HOW: JRadioButton constructor
        deliveryRadioButton = new JRadioButton("Delivery to my location");
        deliveryRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deliveryGroup.add(deliveryRadioButton);
        
        // WHAT: Create pickup radio button
        // WHY: Buyer can choose pickup option
        // HOW: JRadioButton constructor
        pickupRadioButton = new JRadioButton("Pickup at seller location");
        pickupRadioButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deliveryGroup.add(pickupRadioButton);
        
        // WHAT: Create text field for delivery address BEFORE setting radio button selection
        // WHY: Required when buyer chooses delivery, must exist before ItemEvent fires
        // HOW: JTextField constructor
        deliveryAddressField = new JTextField(30);
        deliveryAddressField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deliveryAddressField.setEnabled(false); // Disabled by default (pickup selected)
        deliveryAddressField.addActionListener(this);
        
        // WHAT: Create labels for calculations BEFORE setting radio button selection
        // WHY: updateCalculations() will be called when radio button is selected, labels must exist
        // HOW: JLabel with formatted text
        subtotalLabel = new JLabel("Subtotal: ₱0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        shippingFeeLabel = new JLabel("Shipping Fee: ₱0.00");
        shippingFeeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(244, 67, 54));
        
        // WHAT: Create label for seller location
        // WHY: Shows pickup location information
        // HOW: JLabel with seller location text
        sellerLocationLabel = new JLabel("Seller Location: Not available");
        sellerLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sellerLocationLabel.setForeground(new Color(117, 117, 117));
        
        // WHAT: Add ItemListener AFTER all components are created
        // WHY: ItemListener will access deliveryAddressField and calculation labels, so they must exist first
        // HOW: addItemListener() registers listener
        deliveryRadioButton.addItemListener(this);
        pickupRadioButton.addItemListener(this);
        
        // WHAT: Set pickup as default selection (AFTER all components and listeners are ready)
        // WHY: Most common option, but must be set after all fields exist to avoid null pointer
        // HOW: setSelected(true) selects radio button (this will trigger updateCalculations())
        pickupRadioButton.setSelected(true);
        
        // WHAT: Create purchase button
        // WHY: Buyer needs button to confirm purchase
        // HOW: JButton constructor
        purchaseButton = new JButton("💳 Complete Purchase");
        
        // WHAT: Create cancel button
        // WHY: Buyer needs button to cancel
        // HOW: JButton constructor
        cancelButton = new JButton("Cancel");
        
        // WHAT: Style buttons with colors
        // WHY: Color coding helps identify button functions
        // HOW: styleButton() applies colors, fonts, sizes
        styleButton(purchaseButton, new Color(76, 175, 80), new Color(56, 142, 60)); // Green
        styleButton(cancelButton, new Color(158, 158, 158), new Color(117, 117, 117)); // Gray
        
        // WHAT: Attach action listeners to buttons
        // WHY: Buttons need to respond to clicks
        // HOW: addActionListener() registers this class to receive events
        purchaseButton.addActionListener(this);
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
     * WHAT: Creates layout structure with header, form fields, calculations, and buttons
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
        
        JLabel title = new JLabel("Purchase " + harvestItem.getName());
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
            title.setText("Purchase " + harvestItem.getName());
        }
        headerPanel.add(title, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // WHAT: Create center panel with GridBagLayout
        // WHY: GridBagLayout allows precise field positioning
        // HOW: JPanel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Available quantity and price info
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(availableQuantityLabel, gbc);
        
        gbc.gridy = 1;
        centerPanel.add(pricePerUnitLabel, gbc);
        
        // Quantity field
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Quantity to Purchase:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(quantityField, gbc);
        
        // Purchase method
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("💳 Purchase Method:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(purchaseMethodComboBox, gbc);
        
        // Delivery options
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("🚚 Delivery Option:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel deliveryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        deliveryPanel.setBackground(Color.WHITE);
        deliveryPanel.add(deliveryRadioButton);
        deliveryPanel.add(pickupRadioButton);
        centerPanel.add(deliveryPanel, gbc);
        
        // Delivery address
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        centerPanel.add(createLabel("Delivery Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(deliveryAddressField, gbc);
        
        // Seller location
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(sellerLocationLabel, gbc);
        
        // Separator
        gbc.gridy = 7;
        centerPanel.add(new JSeparator(), gbc);
        
        // Calculations section
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel calcPanel = new JPanel(new GridBagLayout());
        calcPanel.setBackground(new Color(248, 249, 250));
        calcPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints calcGbc = new GridBagConstraints();
        calcGbc.insets = new Insets(5, 5, 5, 5);
        calcGbc.anchor = GridBagConstraints.EAST;
        
        calcGbc.gridx = 0; calcGbc.gridy = 0;
        calcPanel.add(subtotalLabel, calcGbc);
        
        calcGbc.gridy = 1;
        calcPanel.add(shippingFeeLabel, calcGbc);
        
        calcGbc.gridy = 2;
        calcPanel.add(new JSeparator(), calcGbc);
        
        calcGbc.gridy = 3;
        calcPanel.add(totalLabel, calcGbc);
        
        centerPanel.add(calcPanel, gbc);
        
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
        footerPanel.add(purchaseButton);
        
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
     * updateCalculations() - Calculates and updates all price displays
     * WHAT: Reads quantity input, calculates subtotal, shipping, and total
     * WHY: Automatic calculation provides real-time price updates
     * HOW: Parses quantity, calculates costs, updates labels
     */
    private void updateCalculations() {
        // WHAT: Check if calculation labels are initialized (safety check)
        // WHY: Prevents NullPointerException if called before initialization completes
        // HOW: null checks before accessing labels
        if (subtotalLabel == null || shippingFeeLabel == null || totalLabel == null) {
            return; // Exit early if labels not yet initialized
        }
        
        try {
            // WHAT: Parse quantity from text field
            // WHY: Need quantity for calculations
            // HOW: Double.parseDouble() converts string to double
            String qtyText = quantityField.getText().trim();
            if (qtyText.isEmpty()) {
                // WHAT: Reset all calculations if quantity is empty
                // WHY: No quantity means no cost
                // HOW: Set all labels to zero
                subtotalLabel.setText("Subtotal: ₱0.00");
                shippingFeeLabel.setText("Shipping Fee: ₱0.00");
                totalLabel.setText("Total: ₱0.00");
                return;
            }
            
            double quantity = Double.parseDouble(qtyText);
            
            // WHAT: Validate quantity is positive and not exceeding available
            // WHY: Cannot purchase negative or more than available
            // HOW: if statements check quantity bounds
            if (quantity <= 0) {
                subtotalLabel.setText("Subtotal: Invalid quantity");
                shippingFeeLabel.setText("Shipping Fee: ₱0.00");
                totalLabel.setText("Total: Invalid");
                return;
            }
            
            if (quantity > harvestItem.getQuantity()) {
                subtotalLabel.setText("Subtotal: Quantity exceeds available");
                shippingFeeLabel.setText("Shipping Fee: ₱0.00");
                totalLabel.setText("Total: Invalid");
                return;
            }
            
            // WHAT: Get price per unit
            // WHY: Need price to calculate subtotal
            // HOW: getPricePerUnit() returns price
            Double pricePerUnit = harvestItem.getPricePerUnit();
            if (pricePerUnit == null || pricePerUnit <= 0) {
                subtotalLabel.setText("Subtotal: Price not set");
                shippingFeeLabel.setText("Shipping Fee: ₱0.00");
                totalLabel.setText("Total: Price not set");
                return;
            }
            
            // WHAT: Calculate subtotal (quantity * price per unit)
            // WHY: Product cost before shipping
            // HOW: Multiply quantity by price per unit
            double subtotal = quantity * pricePerUnit;
            
            // WHAT: Calculate shipping fee (only if delivery selected)
            // WHY: Shipping only applies to delivery option
            // HOW: Check delivery radio button, calculate shipping
            double shippingFee = 0.0;
            if (deliveryRadioButton.isSelected()) {
                // WHAT: Calculate shipping based on quantity
                // WHY: Shipping cost depends on quantity
                // HOW: Multiply quantity by shipping fee per unit
                shippingFee = quantity * SHIPPING_FEE_PER_UNIT;
            }
            
            // WHAT: Calculate total (subtotal + shipping)
            // WHY: Final amount buyer needs to pay
            // HOW: Add subtotal and shipping fee
            double total = subtotal + shippingFee;
            
            // WHAT: Update labels with formatted currency
            // WHY: Display calculated values to user
            // HOW: setText() updates label text with formatted currency
            subtotalLabel.setText("Subtotal: " + currencyFormat.format(subtotal));
            shippingFeeLabel.setText("Shipping Fee: " + currencyFormat.format(shippingFee));
            totalLabel.setText("Total: " + currencyFormat.format(total));
            
        } catch (NumberFormatException e) {
            // WHAT: Handle invalid quantity input
            // WHY: User may enter non-numeric text
            // HOW: catch block sets error messages
            subtotalLabel.setText("Subtotal: Invalid quantity");
            shippingFeeLabel.setText("Shipping Fee: ₱0.00");
            totalLabel.setText("Total: Invalid");
        }
    }
    
    /**
     * itemStateChanged() - Handles combo box and radio button changes
     * WHAT: Called when purchase method or delivery option changes
     * WHY: Need to update UI and recalculate when options change
     * HOW: Enables/disables delivery address field, recalculates totals
     * @param e ItemEvent containing information about the change
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        // WHAT: Check if all required components are initialized (safety check)
        // WHY: Prevents NullPointerException if event fires before initialization completes
        // HOW: null checks before accessing fields
        if (deliveryAddressField == null || subtotalLabel == null || shippingFeeLabel == null || totalLabel == null) {
            return; // Exit early if components not yet initialized
        }
        
        // WHAT: Check if delivery radio button was selected
        // WHY: Need to enable delivery address field
        // HOW: isSelected() checks radio button state
        if (e.getSource() == deliveryRadioButton && deliveryRadioButton.isSelected()) {
            // WHAT: Enable delivery address field
            // WHY: Address required for delivery
            // HOW: setEnabled(true) enables field
            deliveryAddressField.setEnabled(true);
            // WHAT: Recalculate totals (shipping now applies)
            // WHY: Shipping fee changes when delivery is selected
            // HOW: updateCalculations() recalculates with shipping
            updateCalculations();
        } else if (e.getSource() == pickupRadioButton && pickupRadioButton.isSelected()) {
            // WHAT: Disable delivery address field
            // WHY: Address not needed for pickup
            // HOW: setEnabled(false) disables field
            deliveryAddressField.setEnabled(false);
            deliveryAddressField.setText(""); // Clear address
            // WHAT: Recalculate totals (no shipping for pickup)
            // WHY: Shipping fee removed when pickup is selected
            // HOW: updateCalculations() recalculates without shipping
            updateCalculations();
        }
    }
    
    /**
     * actionPerformed() - Handles all button click and text field events
     * WHAT: Called automatically when buttons are clicked or Enter is pressed
     * WHY: Implements ActionListener interface - required for event handling
     * HOW: Checks event source and performs appropriate action
     * @param e ActionEvent containing information about the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // WHAT: Check if quantity field triggered event (Enter key)
        // WHY: Recalculate when quantity changes
        // HOW: getSource() returns component, == compares references
        if (e.getSource() == quantityField || e.getSource() == deliveryAddressField) {
            // WHAT: Recalculate totals
            // WHY: Quantity or address change affects calculations
            // HOW: updateCalculations() recalculates prices
            updateCalculations();
        }
        // WHAT: Check if purchase button was clicked
        // WHY: Need to process purchase
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == purchaseButton) {
            // WHAT: Process the purchase
            // WHY: Buyer confirmed purchase
            // HOW: processPurchase() validates and completes purchase
            processPurchase();
        }
        // WHAT: Check if cancel button was clicked
        // WHY: Need to close dialog
        // HOW: getSource() returns component, == compares references
        else if (e.getSource() == cancelButton) {
            // WHAT: Close dialog
            // WHY: User cancelled purchase
            // HOW: dispose() closes and destroys dialog
            dispose();
        }
    }
    
    /**
     * processPurchase() - Validates and processes the purchase
     * WHAT: Validates all inputs, updates inventory, shows confirmation
     * WHY: Separates purchase logic from event handling
     * HOW: Validates quantity, checks availability, updates database, shows success message
     */
    private void processPurchase() {
        // WHAT: Validate quantity input
        // WHY: Need valid quantity to process purchase
        // HOW: Try-catch block attempts to parse quantity
        try {
            String qtyText = quantityField.getText().trim();
            if (qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double purchaseQuantity = Double.parseDouble(qtyText);
            
            // WHAT: Validate quantity is positive
            // WHY: Cannot purchase zero or negative quantity
            // HOW: if statement checks quantity > 0
            if (purchaseQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // WHAT: Validate quantity doesn't exceed available
            // WHY: Cannot purchase more than available
            // HOW: if statement checks quantity <= available
            if (purchaseQuantity > harvestItem.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Quantity exceeds available amount (" + harvestItem.getQuantity() + " " + harvestItem.getUnit() + ")", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // WHAT: Validate price is set
            // WHY: Cannot purchase without price
            // HOW: if statement checks price is not null and > 0
            Double pricePerUnit = harvestItem.getPricePerUnit();
            if (pricePerUnit == null || pricePerUnit <= 0) {
                JOptionPane.showMessageDialog(this, "Price is not set for this item. Please contact the seller.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // WHAT: Validate delivery address if delivery selected
            // WHY: Address required for delivery
            // HOW: if statement checks delivery option and address
            if (deliveryRadioButton.isSelected()) {
                String address = deliveryAddressField.getText().trim();
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter delivery address", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // WHAT: Calculate final totals
            // WHY: Need totals for confirmation message
            // HOW: Calculate subtotal, shipping, and total
            double subtotal = purchaseQuantity * pricePerUnit;
            double shippingFee = deliveryRadioButton.isSelected() ? purchaseQuantity * SHIPPING_FEE_PER_UNIT : 0.0;
            double total = subtotal + shippingFee;
            
            // WHAT: Show confirmation dialog with purchase details
            // WHY: Buyer should confirm before completing purchase
            // HOW: showConfirmDialog() displays purchase summary
            String deliveryOption = deliveryRadioButton.isSelected() ? 
                "Delivery to: " + deliveryAddressField.getText().trim() : 
                "Pickup at seller location";
            
            String purchaseMethod = (String) purchaseMethodComboBox.getSelectedItem();
            
            String message = String.format(
                "Purchase Summary:\n\n" +
                "Item: %s\n" +
                "Quantity: %.2f %s\n" +
                "Price per Unit: %s\n" +
                "Subtotal: %s\n" +
                "Shipping Fee: %s\n" +
                "Total: %s\n\n" +
                "Purchase Method: %s\n" +
                "Delivery Option: %s\n\n" +
                "Confirm purchase?",
                harvestItem.getName(),
                purchaseQuantity,
                harvestItem.getUnit(),
                currencyFormat.format(pricePerUnit),
                currencyFormat.format(subtotal),
                currencyFormat.format(shippingFee),
                currencyFormat.format(total),
                purchaseMethod,
                deliveryOption
            );
            
            int confirm = JOptionPane.showConfirmDialog(this, message, "Confirm Purchase", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            // WHAT: Check if user confirmed purchase
            // WHY: Only process if user confirms
            // HOW: YES_OPTION constant returned when Yes clicked
            if (confirm == JOptionPane.YES_OPTION) {
                // WHAT: Try to update inventory
                // WHY: Need to reduce available quantity after purchase
                // HOW: Try-catch block wraps database operations
                try {
                    InventoryDAO dao = new InventoryDAO();
                    
                    // WHAT: Calculate new quantity (available - purchased)
                    // WHY: Reduce inventory by purchased amount
                    // HOW: Subtract purchase quantity from available quantity
                    double newQuantity = harvestItem.getQuantity() - purchaseQuantity;
                    
                    // WHAT: Update harvest item with new quantity
                    // WHY: Reflect purchase in inventory
                    // HOW: Create updated HarvestLot with new quantity
                    HarvestLot updatedItem = new HarvestLot(
                        harvestItem.getId(),
                        harvestItem.getName(),
                        newQuantity,
                        harvestItem.getUnit(),
                        harvestItem.getDateAdded(),
                        harvestItem.getNotes(),
                        newQuantity > 0 ? "Available" : "Sold Out",
                        harvestItem.getPricePerUnit()
                    );
                    
                    // WHAT: Update record in database
                    // WHY: Save inventory changes
                    // HOW: updateRecord() executes UPDATE SQL statement
                    dao.updateRecord(updatedItem);
                    
                    // WHAT: Show success message
                    // WHY: User needs confirmation that purchase completed
                    // HOW: showMessageDialog() displays success message
                    JOptionPane.showMessageDialog(this, 
                        "Purchase completed successfully!\n\n" +
                        "Total Paid: " + currencyFormat.format(total) + "\n" +
                        "Remaining Quantity: " + newQuantity + " " + harvestItem.getUnit(),
                        "Purchase Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // WHAT: Close dialog
                    // WHY: Purchase is complete
                    // HOW: dispose() closes and destroys dialog
                    dispose();
                    
                } catch (Exception ex) {
                    // WHAT: Handle database errors
                    // WHY: Database operations can fail
                    // HOW: catch block executes when exception occurs
                    // WHAT: Show error message dialog
                    // WHY: User needs to know what went wrong
                    // HOW: getMessage() gets exception description, showMessageDialog() displays it
                    JOptionPane.showMessageDialog(this, "Error processing purchase: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            // WHAT: Handle invalid quantity input
            // WHY: User may enter non-numeric text
            // HOW: catch block executes when parsing fails
            // WHAT: Show error message dialog
            // WHY: User needs feedback about validation error
            // HOW: showMessageDialog() displays error message
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}




