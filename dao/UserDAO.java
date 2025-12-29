package dao; // Package declaration: Groups this class with other Data Access Object classes

import java.sql.Connection; // Import: Connection interface for database connections
import java.sql.PreparedStatement; // Import: PreparedStatement for parameterized SQL queries
import java.sql.ResultSet; // Import: ResultSet for reading query results
import java.sql.SQLException; // Import: SQLException for database error handling
import model.User; // Import: User model class
import util.DBConnection; // Import: DBConnection utility for getting database connections

/**
 * UserDAO - Data Access Object for User Operations
 * WHAT: Handles all database operations related to users (authentication, registration, retrieval)
 * WHY: Separates database logic from business logic (DAO pattern), makes code more maintainable
 * HOW: Uses JDBC PreparedStatement for SQL queries, returns User objects or throws exceptions
 * 
 * DESIGN PATTERN: DAO (Data Access Object) - abstracts database operations from business logic
 */
public class UserDAO {
    /**
     * login() - Authenticates user with username and password
     * WHAT: Queries database to find user matching username and password
     * WHY: Required for user authentication - verifies credentials
     * HOW: SELECT query with WHERE clause matching username and password, returns User if found
     * @param username Username to authenticate
     * @param password Password to verify
     * @return User object if authentication successful, null if credentials invalid
     * @throws SQLException If database error occurs
     */
    public User login(String username, String password) throws SQLException {
        // WHAT: SQL query to select user by username and password (case-insensitive username)
        // WHY: Need to find user record matching both username and password, username should be case-insensitive for better UX
        // HOW: SELECT statement with WHERE clause using UPPER() for case-insensitive comparison, "USER" in quotes because it's H2 reserved keyword
        String sql = "SELECT user_id, username, password, name, role, location FROM \"USER\" WHERE UPPER(username) = UPPER(?) AND password = ?";
        
        // WHAT: Try-with-resources block to automatically close database resources
        // WHY: Ensures Connection and PreparedStatement are closed even if exception occurs
        // HOW: try (resource) syntax automatically calls close() when block exits
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set first parameter (?) to username value
            // WHY: PreparedStatement uses ? placeholders to prevent SQL injection
            // HOW: setString(1, username) sets first ? parameter to username
            pstmt.setString(1, username);
            
            // WHAT: Set second parameter (?) to password value
            // WHY: Password must match database record for authentication
            // HOW: setString(2, password) sets second ? parameter to password
            pstmt.setString(2, password);
            
            // WHAT: Execute query and process results
            // WHY: Need to read query results to get user data
            // HOW: executeQuery() returns ResultSet, try-with-resources closes ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // WHAT: Check if query returned a row (user found)
                // WHY: next() returns true if row exists, false if no match
                // HOW: if statement checks return value of next()
                if (rs.next()) {
                    // WHAT: Create User object from database row data
                    // WHY: Return User object instead of raw database data
                    // HOW: new User() constructor with values from ResultSet
                    return new User(
                        rs.getInt("user_id"), // Get user ID column value
                        rs.getString("username"), // Get username column value
                        rs.getString("password"), // Get password column value
                        rs.getString("name"), // Get name column value
                        rs.getString("role"), // Get role column value
                        rs.getString("location") // Get location column value (can be null)
                    );
                }
            }
        }
        // WHAT: Return null if no user found (authentication failed)
        // WHY: Indicates credentials were invalid
        // HOW: return statement returns null
        return null;
    }
    
    /**
     * getUserById() - Retrieves user by unique ID
     * WHAT: Queries database to find user with specific user_id
     * WHY: Needed to retrieve user information when only ID is known
     * HOW: SELECT query with WHERE clause matching user_id, returns User if found
     * @param userId Unique user identifier
     * @return User object if found, null if not found
     * @throws SQLException If database error occurs
     */
    public User getUserById(int userId) throws SQLException {
        // WHAT: SQL query to select user by ID
        // WHY: Need to find user record matching user_id
        // HOW: SELECT statement with WHERE clause, "USER" in quotes for H2 compatibility
        String sql = "SELECT user_id, username, password, name, role, location FROM \"USER\" WHERE user_id = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set parameter (?) to userId value
            // WHY: PreparedStatement uses ? placeholder for user ID
            // HOW: setInt(1, userId) sets first ? parameter to userId
            pstmt.setInt(1, userId);
            
            // WHAT: Execute query and process results
            // WHY: Need to read query results
            // HOW: executeQuery() returns ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                // WHAT: Check if query returned a row
                // WHY: next() returns true if user found
                // HOW: if statement checks return value
                if (rs.next()) {
                    // WHAT: Create User object from database row
                    // WHY: Return User object with all user data
                    // HOW: new User() constructor with ResultSet values
                    return new User(
                        rs.getInt("user_id"), // Get user ID
                        rs.getString("username"), // Get username
                        rs.getString("password"), // Get password
                        rs.getString("name"), // Get name
                        rs.getString("role"), // Get role
                        rs.getString("location") // Get location (can be null)
                    );
                }
            }
        }
        // WHAT: Return null if user not found
        // WHY: Indicates user with that ID doesn't exist
        // HOW: return statement
        return null;
    }
    
    /**
     * usernameExists() - Checks if username is already taken
     * WHAT: Queries database to count users with matching username
     * WHY: Prevents duplicate usernames during registration
     * HOW: SELECT COUNT(*) query, returns true if count > 0
     * @param username Username to check
     * @return true if username exists, false if available
     * @throws SQLException If database error occurs
     */
    public boolean usernameExists(String username) throws SQLException {
        // WHAT: SQL query to count users with matching username
        // WHY: Need to check if username is already in use
        // HOW: SELECT COUNT(*) counts matching rows, "USER" in quotes for H2
        String sql = "SELECT COUNT(*) FROM \"USER\" WHERE username = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set parameter (?) to username value
            // WHY: PreparedStatement uses ? placeholder
            // HOW: setString(1, username) sets first ? parameter
            pstmt.setString(1, username);
            
            // WHAT: Execute query and process results
            // WHY: Need to read count result
            // HOW: executeQuery() returns ResultSet with count
            try (ResultSet rs = pstmt.executeQuery()) {
                // WHAT: Check if query returned a row
                // WHY: COUNT(*) always returns one row with count value
                // HOW: if statement checks if row exists
                if (rs.next()) {
                    // WHAT: Return true if count > 0 (username exists)
                    // WHY: Count > 0 means at least one user has this username
                    // HOW: getInt(1) gets first column (count), > 0 comparison
                    return rs.getInt(1) > 0;
                }
            }
        }
        // WHAT: Return false if no users found (username available)
        // WHY: Indicates username is not taken
        // HOW: return statement
        return false;
    }
    
    /**
     * signUp() - Creates new user account
     * WHAT: Inserts new user record into database after checking username availability
     * WHY: Required for user registration - creates new accounts
     * HOW: Checks username exists, inserts new record, returns created User object
     * @param username Desired username (must be unique)
     * @param password User's password
     * @param name User's full name
     * @param role User's role (ADMIN/BUYER/SELLER)
     * @return User object representing newly created user
     * @throws SQLException If username exists or database error occurs
     */
    public User signUp(String username, String password, String name, String role) throws SQLException {
        // WHAT: Check if username already exists before creating account
        // WHY: Prevents duplicate usernames - each username must be unique
        // HOW: usernameExists() queries database for matching username
        if (usernameExists(username)) {
            // WHAT: Throw exception if username is taken
            // WHY: Cannot create account with duplicate username
            // HOW: throw new SQLException() with error message
            throw new SQLException("Username already exists. Please choose a different username.");
        }
        
        // WHAT: SQL INSERT statement to create new user record
        // WHY: Need to insert new user data into database
        // HOW: INSERT INTO statement with column names and ? placeholders, "USER" in quotes for H2
        String sql = "INSERT INTO \"USER\" (username, password, name, role, location) VALUES (?, ?, ?, ?, ?)";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set first parameter (?) to username
            // WHY: PreparedStatement uses ? placeholders
            // HOW: setString(1, username) sets first ? parameter
            pstmt.setString(1, username);
            
            // WHAT: Set second parameter (?) to password
            // WHY: Password must be stored in database
            // HOW: setString(2, password) sets second ? parameter
            pstmt.setString(2, password);
            
            // WHAT: Set third parameter (?) to name
            // WHY: User's name must be stored
            // HOW: setString(3, name) sets third ? parameter
            pstmt.setString(3, name);
            
            // WHAT: Set fourth parameter (?) to role
            // WHY: User's role determines permissions
            // HOW: setString(4, role) sets fourth ? parameter
            pstmt.setString(4, role);
            
            // WHAT: Set fifth parameter (?) to location (null for new users)
            // WHY: Location is optional, new users may not have location set yet
            // HOW: setString(5, null) sets fifth ? parameter to null
            pstmt.setString(5, null);
            
            // WHAT: Execute INSERT statement and get number of rows affected
            // WHY: Need to verify that insert was successful
            // HOW: executeUpdate() returns number of rows inserted (should be 1)
            int rowsAffected = pstmt.executeUpdate();
            
            // WHAT: Check if insert was successful (at least one row affected)
            // WHY: rowsAffected > 0 means record was created
            // HOW: if statement checks rowsAffected value
            if (rowsAffected > 0) {
                // WHAT: Return the newly created user by logging in
                // WHY: Caller needs User object with database-assigned ID
                // HOW: login() method queries database and returns User object with all fields including ID
                return login(username, password);
            }
        }
        // WHAT: Return null if insert failed (shouldn't happen if no exception)
        // WHY: Indicates insert was unsuccessful
        // HOW: return statement
        return null;
    }
    
    /**
     * updateUserLocation() - Updates user's location/warehouse address
     * WHAT: Updates the location field for a specific user
     * WHY: Allows users to set or update their location information
     * HOW: UPDATE SQL statement with WHERE clause matching user_id
     * @param userId Unique user identifier
     * @param location New location/warehouse address
     * @throws SQLException If database error occurs
     */
    public void updateUserLocation(int userId, String location) throws SQLException {
        // WHAT: SQL UPDATE statement to modify user location
        // WHY: Need to update location field in database
        // HOW: UPDATE SET with location column, WHERE clause for user_id
        String sql = "UPDATE \"USER\" SET location = ? WHERE user_id = ?";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // WHAT: Set first parameter (?) to location value
            // WHY: PreparedStatement uses ? placeholder
            // HOW: setString(1, location) sets first ? parameter
            pstmt.setString(1, location);
            
            // WHAT: Set second parameter (?) to userId value
            // WHY: WHERE clause needs user ID to identify which record to update
            // HOW: setInt(2, userId) sets second ? parameter
            pstmt.setInt(2, userId);
            
            // WHAT: Execute UPDATE statement
            // WHY: Actually updates the record in database
            // HOW: executeUpdate() executes the SQL statement
            pstmt.executeUpdate();
        }
    }
}
