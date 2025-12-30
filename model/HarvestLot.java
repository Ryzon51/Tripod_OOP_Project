package model; // Package declaration: Groups this class with other model/data classes

import java.time.LocalDate; // Import: LocalDate for date handling
import java.time.format.DateTimeFormatter; // Import: DateTimeFormatter for date-to-string conversion

/**
 * HarvestLot - Concrete Subclass for Harvest Records
 * WHAT: Represents agricultural harvest items (crops, produce) in the inventory system
 * WHY: Demonstrates OOP Inheritance - extends FarmItem to add harvest-specific fields (status)
 * HOW: Inherits common fields from FarmItem and adds status field, implements abstract methods
 * 
 * OOP CONCEPTS:
 * - Inheritance: Extends FarmItem abstract class
 * - Polymorphism: Implements abstract methods getItemType() and toCSVString()
 * - Encapsulation: Private status field with public getter/setter
 */
// Concrete subclass of FarmItem (Inheritance, Polymorphism)
public class HarvestLot extends FarmItem {
    // WHAT: Status of the harvest (e.g., "Available", "Interested", "Sold Out", "Fresh", "Processed")
    // WHY: Tracks the availability and state of harvest items for buyer-seller interactions
    // HOW: Stored as VARCHAR in database, displayed in GUI with color-coding
    private String status; // e.g., "Available", "Interested", "Sold Out", "Fresh", "Processed"
    
    // WHAT: Price per unit (e.g., 57.00 per kg)
    // WHY: Sellers need to set prices for their harvests, buyers need to see prices
    // HOW: Stored as DOUBLE in database, displayed in GUI with currency formatting
    private Double pricePerUnit; // Price per unit (e.g., 57.00 per kg)
    
    /**
     * Constructor - Creates a new HarvestLot object
     * WHAT: Initializes harvest item with all fields including status and price
     * WHY: Ensures harvest items are created with complete data including status and price
     * HOW: Calls super() to initialize base FarmItem fields, then sets status and price
     * @param id Unique item identifier
     * @param name Harvest name (e.g., "Rice", "Corn")
     * @param quantity Amount of harvest
     * @param unit Unit of measurement (e.g., "kg", "tons")
     * @param dateAdded Date harvest was recorded
     * @param notes Optional notes about the harvest
     * @param status Harvest status (defaults to "Available" if null)
     * @param pricePerUnit Price per unit (e.g., 57.00 per kg, can be null)
     */
    public HarvestLot(int id, String name, double quantity, String unit, LocalDate dateAdded, String notes, String status, Double pricePerUnit) {
        // WHAT: Call parent class constructor to initialize inherited fields
        // WHY: FarmItem constructor handles common fields (id, name, quantity, unit, dateAdded, notes)
        // HOW: super() must be first statement, passes parameters to parent constructor
        super(id, name, quantity, unit, dateAdded, notes);
        // WHAT: Set status field, defaulting to "Available" if null
        // WHY: Ensures every harvest has a valid status, preventing null pointer exceptions
        // HOW: Ternary operator checks if status is null, uses "Available" as default
        this.status = status != null ? status : "Available";
        // WHAT: Set price per unit field
        // WHY: Stores the price per unit for the harvest
        // HOW: Assigns parameter value to instance field
        this.pricePerUnit = pricePerUnit;
    }
    
    /**
     * Constructor - Creates a new HarvestLot object (backward compatibility)
     * WHAT: Initializes harvest item without price (for backward compatibility)
     * WHY: Existing code may call constructor without price parameter
     * HOW: Calls main constructor with null price
     */
    public HarvestLot(int id, String name, double quantity, String unit, LocalDate dateAdded, String notes, String status) {
        this(id, name, quantity, unit, dateAdded, notes, status, null);
    }
    
    /**
     * getItemType() - Implementation of Abstract Method
     * WHAT: Returns the string "HARVEST" to identify this item type
     * WHY: Required by abstract parent class - demonstrates polymorphism
     * HOW: Simply returns the constant string "HARVEST"
     * @return String "HARVEST"
     */
    // Implementation of abstract method (Polymorphism)
    @Override
    public String getItemType() {
        // WHAT: Return the item type identifier
        // WHY: Used by InventoryDAO to determine which subclass to create when reading from database
        // HOW: Returns constant string that identifies this as a harvest item
        return "HARVEST";
    }
    
    /**
     * toCSVString() - Implementation of Abstract Method for CSV Export
     * WHAT: Converts harvest item data to a comma-separated string for CSV file export
     * WHY: Required by abstract parent class - allows polymorphic CSV export
     * HOW: Formats all fields as CSV: ID,Name,Quantity,Unit,Date,Notes,Status
     * @return CSV-formatted string of harvest data
     */
    // Implementation of abstract method for CSV export (Polymorphism)
    @Override
    public String toCSVString() {
        // WHAT: Create date formatter to convert LocalDate to string
        // WHY: CSV files store dates as strings in yyyy-MM-dd format
        // HOW: DateTimeFormatter.ofPattern() creates formatter with specified format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // WHAT: Format all harvest fields as comma-separated values
        // WHY: CSV format requires comma separation for import into spreadsheet programs
        // HOW: String.format() creates formatted string with placeholders filled by field values
        return String.format("%d,%s,%.2f,%s,%s,%s,%s,%.2f",
            getId(), // %d: Integer ID
            getName(), // %s: String name
            getQuantity(), // %.2f: Double quantity with 2 decimal places
            getUnit(), // %s: String unit
            getDateAdded().format(formatter), // %s: Date formatted as yyyy-MM-dd string
            getNotes() != null ? getNotes() : "", // %s: Notes or empty string if null
            status != null ? status : "", // %s: Status or empty string if null
            pricePerUnit != null ? pricePerUnit : 0.0 // %.2f: Price per unit or 0.0 if null
        );
    }
    
    /**
     * getStatus() - Getter for Status Field
     * WHAT: Returns the current status of the harvest item
     * WHY: Needed to display status in GUI and check availability for buyers
     * HOW: Returns private status field value
     * @return String status value
     */
    public String getStatus() {
        // WHAT: Return the status field value
        // WHY: External code needs to read status for display and business logic
        // HOW: Simple return statement accessing private field
        return status;
    }
    
    /**
     * setStatus() - Setter for Status Field
     * WHAT: Updates the status of the harvest item
     * WHY: Allows status changes (e.g., "Available" -> "Interested" -> "Sold Out")
     * HOW: Assigns new status value to private field
     * @param status New status value
     */
    public void setStatus(String status) {
        // WHAT: Update the status field with new value
        // WHY: Used when buyers mark items as interested or admin marks as sold out
        // HOW: Assigns parameter value to instance field
        this.status = status;
    }
    
    /**
     * getPricePerUnit() - Getter for Price Per Unit Field
     * WHAT: Returns the price per unit of the harvest
     * WHY: Needed to display price in GUI and calculate total cost for buyers
     * HOW: Returns private pricePerUnit field value
     * @return Double price per unit (can be null if not set)
     */
    public Double getPricePerUnit() {
        // WHAT: Return the price per unit field value
        // WHY: External code needs to read price for display and calculations
        // HOW: Simple return statement accessing private field
        return pricePerUnit;
    }
    
    /**
     * setPricePerUnit() - Setter for Price Per Unit Field
     * WHAT: Updates the price per unit of the harvest
     * WHY: Allows sellers to set or update prices for their harvests
     * HOW: Assigns new price value to private field
     * @param pricePerUnit New price per unit value (can be null)
     */
    public void setPricePerUnit(Double pricePerUnit) {
        // WHAT: Update the price per unit field with new value
        // WHY: Used when sellers set or update prices
        // HOW: Assigns parameter value to instance field
        this.pricePerUnit = pricePerUnit;
    }
}












