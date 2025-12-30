package model; // Package declaration: Groups this class with other model/data classes

import java.time.LocalDate; // Import: LocalDate class for date handling without timezone

/**
 * FarmItem - Abstract Base Class for Inventory Items
 * WHAT: Abstract class representing any item in the farm inventory system
 * WHY: Demonstrates OOP concepts: Abstraction (abstract class) and Inheritance (base for subclasses)
 * HOW: Defines common structure that all inventory items share, with abstract methods for subclasses to implement
 * 
 * OOP CONCEPTS:
 * - Abstraction: Abstract class that cannot be instantiated directly
 * - Inheritance: Base class for HarvestLot and EquipmentItem subclasses
 * - Polymorphism: Abstract methods that subclasses must implement differently
 */
// Abstract Class (Abstraction, Inheritance base)
public abstract class FarmItem {
    // WHAT: Unique identifier for each inventory item in the database
    // WHY: Primary key needed for database CRUD operations
    // HOW: Assigned by database AUTO_INCREMENT when item is created
    private int id;
    
    // WHAT: Name/description of the inventory item (e.g., "Rice", "Tractor")
    // WHY: Human-readable identifier for the item
    // HOW: Stored as VARCHAR in database, displayed in GUI tables
    private String name;
    
    // WHAT: Quantity of the item (can be decimal for measurements like kg, liters)
    // WHY: Tracks how much of the item exists
    // HOW: Stored as DOUBLE in database, displayed with unit
    private double quantity;
    
    // WHAT: Unit of measurement (e.g., "kg", "tons", "pieces", "liters")
    // WHY: Quantity is meaningless without knowing the unit
    // HOW: Stored as VARCHAR, displayed alongside quantity in GUI
    private String unit;
    
    // WHAT: Date when this item was added to the inventory
    // WHY: Tracks when items were recorded, useful for sorting and filtering
    // HOW: Stored as VARCHAR in database (format: yyyy-MM-dd), parsed to LocalDate in Java
    private LocalDate dateAdded;
    
    // WHAT: Optional notes or additional information about the item
    // WHY: Allows users to add context or details not captured by other fields
    // HOW: Stored as VARCHAR(1000) in database, displayed in text areas
    private String notes;

    /**
     * Constructor - Creates a new FarmItem object
     * WHAT: Initializes all common fields that all inventory items share
     * WHY: Ensures all subclasses have consistent base data structure
     * HOW: Assigns parameter values to corresponding instance fields
     * @param id Unique item identifier
     * @param name Item name/description
     * @param quantity Amount of the item
     * @param unit Unit of measurement
     * @param dateAdded Date item was added
     * @param notes Optional notes
     */
    public FarmItem(int id, String name, double quantity, String unit, LocalDate dateAdded, String notes) {
        this.id = id; // Assign id parameter to instance field
        this.name = name; // Assign name parameter to instance field
        this.quantity = quantity; // Assign quantity parameter to instance field
        this.unit = unit; // Assign unit parameter to instance field
        this.dateAdded = dateAdded; // Assign dateAdded parameter to instance field
        this.notes = notes; // Assign notes parameter to instance field
    }

    /**
     * getItemType() - Abstract Method for Polymorphism
     * WHAT: Returns a string identifying the type of item (e.g., "HARVEST", "EQUIPMENT")
     * WHY: Allows different item types to be identified and handled differently
     * HOW: Each subclass must implement this method to return its specific type
     * @return String representing the item type
     */
    // Abstract method (Polymorphism: Must be implemented by subclasses)
    public abstract String getItemType();

    /**
     * toCSVString() - Abstract Method for CSV Export
     * WHAT: Converts the item to a CSV-formatted string for file export
     * WHY: Different item types may have different fields to export (e.g., status vs condition)
     * HOW: Each subclass implements this to format its specific data as CSV
     * @return CSV-formatted string of the item's data
     */
    // Abstract method for CSV (Polymorphism: Export logic)
    public abstract String toCSVString();

    // --- Getters (Encapsulation) ---
    // WHAT: Public methods providing read access to private fields
    // WHY: Encapsulation - external code can read but not directly modify private fields
    // HOW: Return the value of the corresponding private field
    
    /**
     * WHAT: Returns the item's unique ID
     * WHY: Needed for database operations (update, delete)
     * HOW: Returns private id field value
     */
    public int getId() { return id; }
    
    /**
     * WHAT: Returns the item's name
     * WHY: Displayed in GUI tables and forms
     * HOW: Returns private name field value
     */
    public String getName() { return name; }
    
    /**
     * WHAT: Returns the item's quantity
     * WHY: Displayed in GUI and used in calculations
     * HOW: Returns private quantity field value
     */
    public double getQuantity() { return quantity; }
    
    /**
     * WHAT: Returns the unit of measurement
     * WHY: Displayed alongside quantity for clarity
     * HOW: Returns private unit field value
     */
    public String getUnit() { return unit; }
    
    /**
     * WHAT: Returns the date the item was added
     * WHY: Used for sorting and filtering by date
     * HOW: Returns private dateAdded field value
     */
    public LocalDate getDateAdded() { return dateAdded; }
    
    /**
     * WHAT: Returns the item's notes
     * WHY: Displayed in GUI for additional context
     * HOW: Returns private notes field value
     */
    public String getNotes() { return notes; }
    
    /**
     * setQuantity() - Setter for Quantity
     * WHAT: Updates the quantity of the item
     * WHY: Used in the 'Update' CRUD operation when editing inventory records
     * HOW: Assigns new quantity value to private field
     * @param quantity New quantity value
     */
    // Setter for Quantity (used in the 'Update' CRUD operation)
    public void setQuantity(double quantity) { this.quantity = quantity; }
}
