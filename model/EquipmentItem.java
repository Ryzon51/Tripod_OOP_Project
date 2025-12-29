package model; // Package declaration: Groups this class with other model/data classes

import java.time.LocalDate; // Import: LocalDate for date handling
import java.time.format.DateTimeFormatter; // Import: DateTimeFormatter for date-to-string conversion

/**
 * EquipmentItem - Concrete Subclass for Farm Equipment
 * WHAT: Represents farm equipment items (tractors, tools, machinery) in the inventory system
 * WHY: Demonstrates OOP Inheritance - extends FarmItem to add equipment-specific fields (condition)
 * HOW: Inherits common fields from FarmItem and adds condition field, implements abstract methods
 * 
 * OOP CONCEPTS:
 * - Inheritance: Extends FarmItem abstract class (second subclass requirement)
 * - Polymorphism: Implements abstract methods getItemType() and toCSVString()
 * - Encapsulation: Private condition field with public getter/setter
 */
// Concrete subclass of FarmItem (Inheritance, Polymorphism)
// Demonstrates OOP: Second subclass requirement for inheritance
public class EquipmentItem extends FarmItem {
    // WHAT: Condition of the equipment (e.g., "Good", "Fair", "Needs Repair", "Under Maintenance")
    // WHY: Tracks the operational state of equipment items, different from harvest status
    // HOW: Stored as VARCHAR in database, displayed in GUI
    private String condition; // e.g., "Good", "Fair", "Needs Repair", "Under Maintenance"
    
    /**
     * Constructor - Creates a new EquipmentItem object
     * WHAT: Initializes equipment item with all fields including condition
     * WHY: Ensures equipment items are created with complete data including condition
     * HOW: Calls super() to initialize base FarmItem fields, then sets condition
     * @param id Unique item identifier
     * @param name Equipment name (e.g., "Tractor", "Plow")
     * @param quantity Number of equipment units
     * @param unit Unit of measurement (e.g., "pieces", "units")
     * @param dateAdded Date equipment was recorded
     * @param notes Optional notes about the equipment
     * @param condition Equipment condition (defaults to "Good" if null)
     */
    public EquipmentItem(int id, String name, double quantity, String unit, LocalDate dateAdded, String notes, String condition) {
        // WHAT: Call parent class constructor to initialize inherited fields
        // WHY: FarmItem constructor handles common fields (id, name, quantity, unit, dateAdded, notes)
        // HOW: super() must be first statement, passes parameters to parent constructor
        super(id, name, quantity, unit, dateAdded, notes);
        // WHAT: Set condition field, defaulting to "Good" if null
        // WHY: Ensures every equipment item has a valid condition, preventing null pointer exceptions
        // HOW: Ternary operator checks if condition is null, uses "Good" as default
        this.condition = condition != null ? condition : "Good";
    }
    
    /**
     * getItemType() - Implementation of Abstract Method
     * WHAT: Returns the string "EQUIPMENT" to identify this item type
     * WHY: Required by abstract parent class - demonstrates polymorphism
     * HOW: Simply returns the constant string "EQUIPMENT"
     * @return String "EQUIPMENT"
     */
    // Implementation of abstract method (Polymorphism)
    @Override
    public String getItemType() {
        // WHAT: Return the item type identifier
        // WHY: Used by InventoryDAO to determine which subclass to create when reading from database
        // HOW: Returns constant string that identifies this as an equipment item
        return "EQUIPMENT";
    }
    
    /**
     * toCSVString() - Implementation of Abstract Method for CSV Export
     * WHAT: Converts equipment item data to a comma-separated string for CSV file export
     * WHY: Required by abstract parent class - allows polymorphic CSV export
     * HOW: Formats all fields as CSV: ID,Name,Quantity,Unit,Date,Notes,Condition
     * @return CSV-formatted string of equipment data
     */
    // Implementation of abstract method for CSV export (Polymorphism)
    @Override
    public String toCSVString() {
        // WHAT: Create date formatter to convert LocalDate to string
        // WHY: CSV files store dates as strings in yyyy-MM-dd format
        // HOW: DateTimeFormatter.ofPattern() creates formatter with specified format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // WHAT: Format all equipment fields as comma-separated values
        // WHY: CSV format requires comma separation for import into spreadsheet programs
        // HOW: String.format() creates formatted string with placeholders filled by field values
        return String.format("%d,%s,%.2f,%s,%s,%s,%s",
            getId(), // %d: Integer ID
            getName(), // %s: String name
            getQuantity(), // %.2f: Double quantity with 2 decimal places
            getUnit(), // %s: String unit
            getDateAdded().format(formatter), // %s: Date formatted as yyyy-MM-dd string
            getNotes() != null ? getNotes() : "", // %s: Notes or empty string if null
            condition != null ? condition : "" // %s: Condition or empty string if null
        );
    }
    
    /**
     * getCondition() - Getter for Condition Field
     * WHAT: Returns the current condition of the equipment item
     * WHY: Needed to display condition in GUI and track equipment maintenance status
     * HOW: Returns private condition field value
     * @return String condition value
     */
    public String getCondition() {
        // WHAT: Return the condition field value
        // WHY: External code needs to read condition for display and business logic
        // HOW: Simple return statement accessing private field
        return condition;
    }
    
    /**
     * setCondition() - Setter for Condition Field
     * WHAT: Updates the condition of the equipment item
     * WHY: Allows condition updates (e.g., "Good" -> "Needs Repair" after maintenance check)
     * HOW: Assigns new condition value to private field
     * @param condition New condition value
     */
    public void setCondition(String condition) {
        // WHAT: Update the condition field with new value
        // WHY: Used when equipment condition changes due to use or maintenance
        // HOW: Assigns parameter value to instance field
        this.condition = condition;
    }
}
