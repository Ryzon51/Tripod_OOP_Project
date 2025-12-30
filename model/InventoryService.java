package model; // Package declaration: Groups this interface with other model classes

import java.util.List; // Import: List interface for returning collections of FarmItem objects

/**
 * InventoryService - Interface for Inventory Operations
 * WHAT: Defines the contract (method signatures) for inventory CRUD operations
 * WHY: Demonstrates OOP Abstraction - interface allows different implementations (DAO pattern)
 * HOW: Interface declares method signatures that InventoryDAO must implement
 * 
 * OOP CONCEPT: Abstraction - Interface defines what operations are available without specifying how
 */
// Interface for Abstraction
public interface InventoryService {
    /**
     * addRecord() - Create Operation (CRUD)
     * WHAT: Adds a new inventory item to the database
     * WHY: Required for creating new inventory records
     * HOW: Implementations will insert item data into database
     * @param item FarmItem object to add (can be HarvestLot or EquipmentItem)
     * @throws Exception If database operation fails
     */
    // CRUD Operations signatures
    void addRecord(FarmItem item) throws Exception; // Create
    
    /**
     * getAllRecords() - Read Operation (CRUD)
     * WHAT: Retrieves all inventory items from the database
     * WHY: Required for displaying all records in GUI tables
     * HOW: Implementations will query database and return list of FarmItem objects
     * @return List of all FarmItem objects in database
     * @throws Exception If database operation fails
     */
    List<FarmItem> getAllRecords() throws Exception; // Read
    
    /**
     * updateRecord() - Update Operation (CRUD)
     * WHAT: Updates an existing inventory item in the database
     * WHY: Required for editing/modifying existing records
     * HOW: Implementations will update database record with new item data
     * @param item FarmItem object with updated data (must have valid ID)
     * @throws Exception If database operation fails
     */
    void updateRecord(FarmItem item) throws Exception; // Update
    
    /**
     * deleteRecord() - Delete Operation (CRUD)
     * WHAT: Removes an inventory item from the database
     * WHY: Required for deleting unwanted records
     * HOW: Implementations will delete database record by ID
     * @param id Unique identifier of item to delete
     * @throws Exception If database operation fails
     */
    void deleteRecord(int id) throws Exception; // Delete
    
    /**
     * searchRecords() - Search/Filter Operation
     * WHAT: Searches inventory items matching a query string
     * WHY: Required for filtering records by name, notes, or item type
     * HOW: Implementations will query database with LIKE pattern matching
     * @param query Search string to match against item fields
     * @return List of FarmItem objects matching the search query
     * @throws Exception If database operation fails
     */
    // Search/Filter 
    List<FarmItem> searchRecords(String query) throws Exception;
}
