package dao; // Package declaration: Groups this class with other Data Access Object classes

import java.sql.Connection; // Import: Connection interface for database connections
import java.sql.PreparedStatement; // Import: PreparedStatement for parameterized SQL queries
import java.sql.ResultSet; // Import: ResultSet for reading query results
import java.sql.SQLException; // Import: SQLException for database error handling
import java.sql.Statement; // Import: Statement for executing SQL DDL statements
import java.time.LocalDate; // Import: LocalDate for date handling
import java.time.format.DateTimeFormatter; // Import: DateTimeFormatter for date string formatting
import java.util.ArrayList; // Import: ArrayList for storing query results
import java.util.List; // Import: List interface for collections
import model.EquipmentItem; // Import: EquipmentItem subclass
import model.FarmItem; // Import: FarmItem base class
import model.HarvestLot; // Import: HarvestLot subclass
import model.InventoryService; // Import: InventoryService interface
import util.DBConnection; // Import: DBConnection utility for database connections

/**
 * InventoryDAO - Data Access Object for Inventory Operations
 * WHAT: Implements InventoryService interface, handles all database operations for inventory items
 * WHY: Separates database logic from business logic (DAO pattern), implements interface for abstraction
 * HOW: Uses JDBC PreparedStatement for CRUD operations, uses polymorphism to handle different item types
 * 
 * DESIGN PATTERN: DAO (Data Access Object) - abstracts database operations
 * OOP CONCEPT: Implements interface (abstraction), uses polymorphism to handle HarvestLot and EquipmentItem
 */
public class InventoryDAO implements InventoryService {
    // WHAT: Date formatter for converting LocalDate to/from database string format
    // WHY: Database stores dates as VARCHAR strings (yyyy-MM-dd), need formatter for conversion
    // HOW: DateTimeFormatter.ofPattern() creates formatter matching database date format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // WHAT: Alternative date formatter for MM/dd/yyyy format (from H2 Console manual entries)
    // WHY: Users may enter dates in MM/dd/yyyy format in H2 Console, need to handle both formats
    // HOW: DateTimeFormatter.ofPattern() creates formatter for alternative date format
    private static final DateTimeFormatter ALT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    /**
     * addRecord() - Create Operation (CRUD)
     * WHAT: Inserts new inventory item into database
     * WHY: Required for adding new inventory records (harvests, equipment)
     * HOW: INSERT SQL statement with item data, uses polymorphism to handle different item types
     * @param item FarmItem object to add (HarvestLot or EquipmentItem)
     * @throws Exception If database error occurs
     */
    @Override
    public void addRecord(FarmItem item) throws Exception {
        // WHAT: SQL INSERT statement to add new inventory item
        // WHY: Need to insert item data into INVENTORY_ITEM table
        // HOW: INSERT INTO with column names and ? placeholders for values
        String sql = "INSERT INTO INVENTORY_ITEM (name, quantity, unit, item_type, date_added, status, notes, price_per_unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // WHAT: Try-with-resources block to automatically close database resources
        // WHY: Ensures Connection and PreparedStatement are closed even if exception occurs
        // HOW: try (resource) syntax automatically calls close() when block exits
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set first parameter (?) to item name
            // WHY: Name is required field for inventory items
            // HOW: setString(1, item.getName()) sets first ? parameter
            pstmt.setString(1, item.getName());
            
            // WHAT: Set second parameter (?) to item quantity
            // WHY: Quantity is required numeric field
            // HOW: setDouble(2, item.getQuantity()) sets second ? parameter
            pstmt.setDouble(2, item.getQuantity());
            
            // WHAT: Set third parameter (?) to item unit
            // WHY: Unit is required field (kg, tons, pieces, etc.)
            // HOW: setString(3, item.getUnit()) sets third ? parameter
            pstmt.setString(3, item.getUnit());
            
            // WHAT: Set fourth parameter (?) to item type
            // WHY: item_type identifies whether item is HARVEST or EQUIPMENT
            // HOW: getItemType() uses polymorphism - each subclass returns its type
            pstmt.setString(4, item.getItemType());
            
            // WHAT: Set fifth parameter (?) to formatted date string
            // WHY: Database stores dates as VARCHAR strings, not date type
            // HOW: format() converts LocalDate to yyyy-MM-dd string format
            pstmt.setString(5, item.getDateAdded().format(DATE_FORMATTER));
            
            // WHAT: Handle status/condition based on item type (polymorphism)
            // WHY: HarvestLot has status, EquipmentItem has condition - different fields
            // HOW: instanceof operator checks item type, casts to appropriate subclass
            if (item instanceof HarvestLot) {
                // WHAT: Set status for HarvestLot items
                // WHY: HarvestLot uses status field (Available, Interested, Sold Out)
                // HOW: Cast to HarvestLot and call getStatus() method
                pstmt.setString(6, ((HarvestLot) item).getStatus());
            } else if (item instanceof EquipmentItem) {
                // WHAT: Set condition for EquipmentItem items
                // WHY: EquipmentItem uses condition field (Good, Fair, Needs Repair)
                // HOW: Cast to EquipmentItem and call getCondition() method
                pstmt.setString(6, ((EquipmentItem) item).getCondition());
            } else {
                // WHAT: Set null for unknown item types
                // WHY: Unknown types don't have status/condition field
                // HOW: setString(6, null) sets parameter to null
                pstmt.setString(6, null);
            }
            
            // WHAT: Set seventh parameter (?) to item notes
            // WHY: Notes is optional field for additional information
            // HOW: setString(7, item.getNotes()) sets seventh ? parameter
            pstmt.setString(7, item.getNotes());
            
            // WHAT: Handle price per unit for HarvestLot items (polymorphism)
            // WHY: HarvestLot has pricePerUnit field, other types may not
            // HOW: instanceof operator checks type, casts to HarvestLot
            if (item instanceof HarvestLot) {
                // WHAT: Set price per unit for HarvestLot
                // WHY: HarvestLot uses pricePerUnit field
                // HOW: Cast to HarvestLot and call getPricePerUnit()
                Double price = ((HarvestLot) item).getPricePerUnit();
                if (price != null) {
                    pstmt.setDouble(8, price);
                } else {
                    pstmt.setNull(8, java.sql.Types.DOUBLE);
                }
            } else {
                // WHAT: Set null for non-HarvestLot items
                // WHY: Other item types don't use price field
                // HOW: setNull(8, Types.DOUBLE) sets parameter to null
                pstmt.setNull(8, java.sql.Types.DOUBLE);
            }
            
            // WHAT: Execute INSERT statement
            // WHY: Actually inserts the record into database
            // HOW: executeUpdate() executes the SQL statement
            int rowsAffected = pstmt.executeUpdate();
            
            // WHAT: Log successful insertion for debugging
            // WHY: Confirms data was saved to database
            // HOW: System.out.println() writes confirmation message
            if (rowsAffected > 0) {
                System.out.println("✓ Successfully added item to database: " + item.getName() + " (Type: " + item.getItemType() + ")");
            }
        } catch (SQLException e) {
            // WHAT: Check if error is primary key violation (AUTO_INCREMENT sequence out of sync)
            // WHY: When records are manually inserted via H2 Console, AUTO_INCREMENT sequence doesn't update
            // HOW: Check error message and SQL state for primary key violation
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("PRIMARY KEY") || errorMsg.contains("23505"))) {
                // WHAT: Try to fix AUTO_INCREMENT sequence and retry insert
                // WHY: Sequence is out of sync, need to reset it to max ID + 1
                // HOW: Find max ID, reset sequence, then retry insert
                try {
                    System.out.println("⚠ Primary key violation detected. Attempting to fix AUTO_INCREMENT sequence...");
                    fixAutoIncrementSequence();
                    // Retry the insert after fixing sequence
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement retryPstmt = conn.prepareStatement(sql)) {
                        // Set all parameters again
                        retryPstmt.setString(1, item.getName());
                        retryPstmt.setDouble(2, item.getQuantity());
                        retryPstmt.setString(3, item.getUnit());
                        retryPstmt.setString(4, item.getItemType());
                        retryPstmt.setString(5, item.getDateAdded().format(DATE_FORMATTER));
                        if (item instanceof HarvestLot) {
                            retryPstmt.setString(6, ((HarvestLot) item).getStatus());
                        } else if (item instanceof EquipmentItem) {
                            retryPstmt.setString(6, ((EquipmentItem) item).getCondition());
                        } else {
                            retryPstmt.setString(6, null);
                        }
                        retryPstmt.setString(7, item.getNotes());
                        if (item instanceof HarvestLot) {
                            Double price = ((HarvestLot) item).getPricePerUnit();
                            if (price != null) {
                                retryPstmt.setDouble(8, price);
                            } else {
                                retryPstmt.setNull(8, java.sql.Types.DOUBLE);
                            }
                        } else {
                            retryPstmt.setNull(8, java.sql.Types.DOUBLE);
                        }
                        int retryRowsAffected = retryPstmt.executeUpdate();
                        if (retryRowsAffected > 0) {
                            System.out.println("✓ Successfully added item to database after fixing sequence: " + item.getName());
                        }
                        return; // Success, exit method
                    }
                } catch (Exception fixEx) {
                    // If fixing sequence fails, throw original error
                    throw new Exception("Failed to add record due to primary key violation. Please restart the application to fix AUTO_INCREMENT sequence. Original error: " + errorMsg, e);
                }
            }
            // If not a primary key violation, re-throw the original exception
            throw new Exception("Error adding record: " + errorMsg, e);
        }
    }
    
    /**
     * fixAutoIncrementSequence() - Fixes AUTO_INCREMENT sequence for INVENTORY_ITEM table
     * WHAT: Finds the maximum existing item_id and resets AUTO_INCREMENT to max+1
     * WHY: When records are manually inserted via H2 Console, AUTO_INCREMENT sequence doesn't update automatically
     * HOW: Queries MAX(item_id), then uses ALTER TABLE to reset the sequence
     */
    private void fixAutoIncrementSequence() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Find maximum existing ID
            ResultSet rs = stmt.executeQuery("SELECT MAX(item_id) FROM INVENTORY_ITEM");
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            if (maxId > 0) {
                // Reset sequence to max+1
                try {
                    stmt.execute("ALTER TABLE INVENTORY_ITEM ALTER COLUMN item_id RESTART WITH " + (maxId + 1));
                    System.out.println("✓ Fixed AUTO_INCREMENT sequence: next ID will be " + (maxId + 1));
                } catch (SQLException e) {
                    // Try alternative H2 syntax
                    try {
                        stmt.execute("ALTER TABLE INVENTORY_ITEM ALTER COLUMN item_id RESTART " + (maxId + 1));
                        System.out.println("✓ Fixed AUTO_INCREMENT sequence: next ID will be " + (maxId + 1));
                    } catch (SQLException e2) {
                        throw new Exception("Could not reset AUTO_INCREMENT sequence. Please manually fix in H2 Console: ALTER TABLE INVENTORY_ITEM ALTER COLUMN item_id RESTART WITH " + (maxId + 1), e2);
                    }
                }
            }
        }
    }
    
    /**
     * getAllRecords() - Read Operation (CRUD)
     * WHAT: Retrieves all inventory items from database, ordered by date (newest first)
     * WHY: Required for displaying all records in GUI tables
     * HOW: SELECT query with ORDER BY clause, uses polymorphism to create appropriate subclass objects
     * @return List of all FarmItem objects in database
     * @throws Exception If database error occurs
     */
    @Override
    public List<FarmItem> getAllRecords() throws Exception {
        // WHAT: Create empty list to store query results
        // WHY: Need collection to hold FarmItem objects
        // HOW: new ArrayList<>() creates empty list
        List<FarmItem> items = new ArrayList<>();
        
        // WHAT: SQL SELECT statement to get all inventory items
        // WHY: Need to retrieve all records from database
        // HOW: SELECT with all columns, ORDER BY date_added DESC (newest first)
        String sql = "SELECT item_id, name, quantity, unit, item_type, date_added, status, notes, price_per_unit FROM INVENTORY_ITEM ORDER BY date_added DESC";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // WHAT: Loop through each row in result set
            // WHY: Each row represents one inventory item
            // HOW: while loop with next() method moves to next row
            while (rs.next()) {
                // WHAT: Create FarmItem object from database row
                // WHY: Convert database data to Java objects
                // HOW: createFarmItemFromResultSet() uses polymorphism to create correct subclass
                items.add(createFarmItemFromResultSet(rs));
            }
        }
        // WHAT: Return list of all items
        // WHY: Caller needs the items to display in GUI
        // HOW: return statement
        return items;
    }
    
    /**
     * updateRecord() - Update Operation (CRUD)
     * WHAT: Updates existing inventory item in database
     * WHY: Required for editing/modifying existing records
     * HOW: UPDATE SQL statement with new item data, uses polymorphism for status/condition
     * @param item FarmItem object with updated data (must have valid ID)
     * @throws Exception If database error occurs
     */
    @Override
    public void updateRecord(FarmItem item) throws Exception {
        // WHAT: SQL UPDATE statement to modify existing inventory item
        // WHY: Need to update item data in database
        // HOW: UPDATE SET with column names and ? placeholders, WHERE clause for item_id
        String sql = "UPDATE INVENTORY_ITEM SET name = ?, quantity = ?, unit = ?, date_added = ?, status = ?, notes = ?, price_per_unit = ? WHERE item_id = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set parameters for updated values
            // WHY: PreparedStatement uses ? placeholders
            // HOW: setString() and setDouble() methods set parameter values
            pstmt.setString(1, item.getName()); // Set name
            pstmt.setDouble(2, item.getQuantity()); // Set quantity
            pstmt.setString(3, item.getUnit()); // Set unit
            pstmt.setString(4, item.getDateAdded().format(DATE_FORMATTER)); // Set formatted date
            
            // WHAT: Handle status for HarvestLot items (polymorphism)
            // WHY: HarvestLot has status field, other types may not
            // HOW: instanceof operator checks type, casts to HarvestLot
            if (item instanceof HarvestLot) {
                // WHAT: Set status for HarvestLot
                // WHY: HarvestLot uses status field
                // HOW: Cast to HarvestLot and call getStatus()
                pstmt.setString(5, ((HarvestLot) item).getStatus());
            } else {
                // WHAT: Set null for non-HarvestLot items
                // WHY: Other item types don't use status field
                // HOW: setString(5, null) sets parameter to null
                pstmt.setString(5, null);
            }
            
            // WHAT: Set notes parameter
            // WHY: Notes may have been updated
            // HOW: setString(6, item.getNotes()) sets notes
            pstmt.setString(6, item.getNotes());
            
            // WHAT: Handle price per unit for HarvestLot items (polymorphism)
            // WHY: HarvestLot has pricePerUnit field, other types may not
            // HOW: instanceof operator checks type, casts to HarvestLot
            if (item instanceof HarvestLot) {
                // WHAT: Set price per unit for HarvestLot
                // WHY: HarvestLot uses pricePerUnit field
                // HOW: Cast to HarvestLot and call getPricePerUnit()
                Double price = ((HarvestLot) item).getPricePerUnit();
                if (price != null) {
                    pstmt.setDouble(7, price);
                } else {
                    pstmt.setNull(7, java.sql.Types.DOUBLE);
                }
            } else {
                // WHAT: Set null for non-HarvestLot items
                // WHY: Other item types don't use price field
                // HOW: setNull(7, Types.DOUBLE) sets parameter to null
                pstmt.setNull(7, java.sql.Types.DOUBLE);
            }
            
            // WHAT: Set item_id parameter for WHERE clause
            // WHY: WHERE clause identifies which record to update
            // HOW: setInt(8, item.getId()) sets item ID
            pstmt.setInt(8, item.getId());
            
            // WHAT: Execute UPDATE statement
            // WHY: Actually updates the record in database
            // HOW: executeUpdate() executes the SQL statement
            int rowsAffected = pstmt.executeUpdate();
            
            // WHAT: Log successful update for debugging
            // WHY: Confirms data was updated in database
            // HOW: System.out.println() writes confirmation message
            if (rowsAffected > 0) {
                System.out.println("✓ Successfully updated item in database: " + item.getName() + " (ID: " + item.getId() + ")");
            }
        }
    }
    
    /**
     * deleteRecord() - Delete Operation (CRUD)
     * WHAT: Removes inventory item from database by ID
     * WHY: Required for deleting unwanted records
     * HOW: DELETE SQL statement with WHERE clause matching item_id
     * @param id Unique identifier of item to delete
     * @throws Exception If database error occurs
     */
    @Override
    public void deleteRecord(int id) throws Exception {
        // WHAT: SQL DELETE statement to remove inventory item
        // WHY: Need to delete record from database
        // HOW: DELETE FROM with WHERE clause matching item_id
        String sql = "DELETE FROM INVENTORY_ITEM WHERE item_id = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set parameter (?) to item ID
            // WHY: WHERE clause needs item ID to identify which record to delete
            // HOW: setInt(1, id) sets first ? parameter to id
            pstmt.setInt(1, id);
            
            // WHAT: Execute DELETE statement
            // WHY: Actually deletes the record from database
            // HOW: executeUpdate() executes the SQL statement
            int rowsAffected = pstmt.executeUpdate();
            
            // WHAT: Log successful deletion for debugging
            // WHY: Confirms data was deleted from database
            // HOW: System.out.println() writes confirmation message
            if (rowsAffected > 0) {
                System.out.println("✓ Successfully deleted item from database (ID: " + id + ")");
            }
        }
    }
    
    /**
     * searchRecords() - Search/Filter Operation
     * WHAT: Searches inventory items matching query string in name, notes, or item_type
     * WHY: Required for filtering records by search criteria
     * HOW: SELECT with LIKE pattern matching in WHERE clause, uses OR to search multiple columns
     * @param query Search string to match against item fields
     * @return List of FarmItem objects matching the search query
     * @throws Exception If database error occurs
     */
    @Override
    public List<FarmItem> searchRecords(String query) throws Exception {
        // WHAT: Create empty list to store search results
        // WHY: Need collection to hold matching items
        // HOW: new ArrayList<>() creates empty list
        List<FarmItem> items = new ArrayList<>();
        
        // WHAT: SQL SELECT statement with LIKE pattern matching
        // WHY: Need to search across multiple columns (name, notes, item_type)
        // HOW: WHERE clause with OR conditions, LIKE uses % wildcards for partial matching
        String sql = "SELECT item_id, name, quantity, unit, item_type, date_added, status, notes, price_per_unit FROM INVENTORY_ITEM " +
                     "WHERE name LIKE ? OR notes LIKE ? OR item_type LIKE ? ORDER BY date_added DESC";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Create search pattern with wildcards (%query%)
            // WHY: LIKE operator needs % wildcards to match partial strings
            // HOW: String concatenation adds % before and after query
            String searchPattern = "%" + query + "%";
            
            // WHAT: Set all three LIKE parameters to same search pattern
            // WHY: Search same pattern across name, notes, and item_type columns
            // HOW: setString() sets each ? parameter to searchPattern
            pstmt.setString(1, searchPattern); // Search in name column
            pstmt.setString(2, searchPattern); // Search in notes column
            pstmt.setString(3, searchPattern); // Search in item_type column
            
            // WHAT: Execute query and process results
            // WHY: Need to read matching records
            // HOW: executeQuery() returns ResultSet, try-with-resources closes ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // WHAT: Loop through each matching row
                // WHY: Each row represents one matching inventory item
                // HOW: while loop with next() method
                while (rs.next()) {
                    // WHAT: Create FarmItem object from database row
                    // WHY: Convert database data to Java objects
                    // HOW: createFarmItemFromResultSet() uses polymorphism
                    items.add(createFarmItemFromResultSet(rs));
                }
            }
        }
        // WHAT: Return list of matching items
        // WHY: Caller needs the search results
        // HOW: return statement
        return items;
    }
    
    /**
     * createFarmItemFromResultSet() - Helper method to create FarmItem objects from database rows
     * WHAT: Reads ResultSet row and creates appropriate FarmItem subclass (HarvestLot or EquipmentItem)
     * WHY: Demonstrates polymorphism - creates correct subclass based on item_type
     * HOW: Reads item_type from database, uses if-else to create appropriate subclass object
     * @param rs ResultSet positioned at row to read
     * @return FarmItem object (HarvestLot or EquipmentItem) created from database row
     * @throws SQLException If database error occurs while reading ResultSet
     */
    private FarmItem createFarmItemFromResultSet(ResultSet rs) throws SQLException {
        // WHAT: Extract all column values from ResultSet
        // WHY: Need all data to create FarmItem object
        // HOW: getInt(), getString(), getDouble() methods read column values
        int id = rs.getInt("item_id"); // Get item ID
        String name = rs.getString("name"); // Get item name
        double quantity = rs.getDouble("quantity"); // Get quantity
        String unit = rs.getString("unit"); // Get unit
        String itemType = rs.getString("item_type"); // Get item type (HARVEST or EQUIPMENT)
        
        // WHAT: Parse date string with flexible format support
        // WHY: Database may have dates in yyyy-MM-dd or MM/dd/yyyy format (from H2 Console)
        // HOW: Try primary format first, fallback to alternative format if parsing fails
        String dateString = rs.getString("date_added");
        LocalDate dateAdded;
        try {
            // WHAT: Try parsing with primary format (yyyy-MM-dd)
            // WHY: Application uses this format by default
            // HOW: LocalDate.parse() with DATE_FORMATTER
            dateAdded = LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            // WHAT: If primary format fails, try alternative format (MM/dd/yyyy)
            // WHY: H2 Console may have dates in MM/dd/yyyy format
            // HOW: LocalDate.parse() with ALT_DATE_FORMATTER
            try {
                dateAdded = LocalDate.parse(dateString, ALT_DATE_FORMATTER);
            } catch (Exception e2) {
                // WHAT: If both formats fail, use current date as fallback
                // WHY: Application should not crash on invalid date format
                // HOW: LocalDate.now() provides current date
                System.err.println("⚠ Warning: Could not parse date '" + dateString + "'. Using current date as fallback.");
                dateAdded = LocalDate.now();
            }
        }
        
        String status = rs.getString("status"); // Get status/condition (used for both HarvestLot status and EquipmentItem condition)
        String notes = rs.getString("notes"); // Get notes
        Double pricePerUnit = null; // Initialize price as null
        try {
            // WHAT: Get price per unit from ResultSet
            // WHY: HarvestLot items may have price per unit
            // HOW: getDouble() reads price column, returns 0.0 if null
            double price = rs.getDouble("price_per_unit");
            // WHAT: Check if price was actually null (getDouble returns 0.0 for null)
            // WHY: Need to distinguish between 0.0 and null
            // HOW: wasNull() checks if last getDouble() returned null
            if (!rs.wasNull()) {
                pricePerUnit = price;
            }
        } catch (SQLException e) {
            // Column may not exist in old databases, ignore
        }
        
        // WHAT: Create appropriate FarmItem subclass based on item_type (Polymorphism)
        // WHY: Different item types need different subclasses (HarvestLot vs EquipmentItem)
        // HOW: if-else statements check itemType string, create corresponding subclass
        if ("HARVEST".equals(itemType)) {
            // WHAT: Create HarvestLot object for harvest items
            // WHY: Harvest items need HarvestLot subclass with status and price fields
            // HOW: new HarvestLot() constructor with all extracted values including price
            return new HarvestLot(id, name, quantity, unit, dateAdded, notes, status, pricePerUnit);
        } else if ("EQUIPMENT".equals(itemType)) {
            // WHAT: Create EquipmentItem object for equipment items
            // WHY: Equipment items need EquipmentItem subclass with condition field
            // HOW: new EquipmentItem() constructor with all extracted values (status becomes condition)
            return new EquipmentItem(id, name, quantity, unit, dateAdded, notes, status);
        } else {
            // WHAT: Default to HarvestLot for backward compatibility
            // WHY: Old records or unknown types should still work
            // HOW: Default case creates HarvestLot (most common type)
            return new HarvestLot(id, name, quantity, unit, dateAdded, notes, status, pricePerUnit);
        }
    }
    
    /**
     * getRecordById() - Retrieves single inventory item by ID
     * WHAT: Queries database to find inventory item with specific item_id
     * WHY: Needed for editing records - must retrieve existing data first
     * HOW: SELECT query with WHERE clause matching item_id, returns FarmItem if found
     * @param id Unique item identifier
     * @return FarmItem object if found, null if not found
     * @throws Exception If database error occurs
     */
    public FarmItem getRecordById(int id) throws Exception {
        // WHAT: SQL SELECT statement to get item by ID
        // WHY: Need to retrieve specific item for editing
        // HOW: SELECT with WHERE clause matching item_id
        String sql = "SELECT item_id, name, quantity, unit, item_type, date_added, status, notes, price_per_unit FROM INVENTORY_ITEM WHERE item_id = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set parameter (?) to item ID
            // WHY: WHERE clause needs item ID to find specific record
            // HOW: setInt(1, id) sets first ? parameter to id
            pstmt.setInt(1, id);
            
            // WHAT: Execute query and process results
            // WHY: Need to read the item data
            // HOW: executeQuery() returns ResultSet, try-with-resources closes ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // WHAT: Check if query returned a row (item found)
                // WHY: next() returns true if row exists
                // HOW: if statement checks return value
                if (rs.next()) {
                    // WHAT: Create FarmItem object from database row
                    // WHY: Return FarmItem object instead of raw database data
                    // HOW: createFarmItemFromResultSet() uses polymorphism to create correct subclass
                    return createFarmItemFromResultSet(rs);
                }
            }
        }
        // WHAT: Return null if item not found
        // WHY: Indicates item with that ID doesn't exist
        // HOW: return statement
        return null;
    }
}
