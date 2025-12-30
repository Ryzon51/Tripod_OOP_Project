package model; // Package declaration: Groups this class with other model/data classes

/**
 * User - User Data Model Class
 * WHAT: Represents a user account in the AgriTrack system with authentication and role information
 * WHY: Encapsulates user data in a single object, following OOP encapsulation principles
 * HOW: Uses private fields with public getters/setters to control data access
 * 
 * OOP CONCEPT: Encapsulation - Private fields are accessed only through controlled methods
 */
public class User {
    // WHAT: Unique identifier for each user record in the database
    // WHY: Primary key needed for database operations (update, delete, retrieve)
    // HOW: Assigned by database AUTO_INCREMENT when user is created
    private int id; // Encapsulation: Private field prevents direct external access
    
    // WHAT: Unique username for login authentication
    // WHY: Users need a unique identifier to log into the system
    // HOW: Stored in database and used for login verification
    private String username;
    
    // WHAT: Password for authentication (stored in plain text - for educational purposes)
    // WHY: Required for secure login verification
    // HOW: Compared with user input during login process
    // NOTE: In production, passwords should be hashed (e.g., bcrypt, SHA-256)
    private String password; 
    
    // WHAT: Full name of the user for display purposes
    // WHY: Provides human-readable identification beyond username
    // HOW: Displayed in GUI headers and user information panels
    private String name;
    
    // WHAT: User's role in the system (ADMIN, BUYER, or SELLER)
    // WHY: Determines what features and permissions the user has access to
    // HOW: Used in role-based access control throughout the application
    private String role; // Possible values: "ADMIN", "BUYER", "SELLER"
    
    // WHAT: User's location/warehouse address
    // WHY: Needed for delivery options and transaction coordination
    // HOW: Stored as VARCHAR in database, displayed in GUI for buyers/sellers
    private String location; // Location/warehouse address (e.g., "123 Main St, City, Province")

    /**
     * Constructor - Creates a new User object
     * WHAT: Initializes all user fields with provided values
     * WHY: Ensures User objects are always created with complete data
     * HOW: Assigns parameter values to corresponding instance fields
     * @param id Unique user identifier from database
     * @param username Login username
     * @param password Login password
     * @param name User's full name
     * @param role User's role (ADMIN/BUYER/SELLER)
     * @param location User's location/warehouse address (can be null)
     */
    public User(int id, String username, String password, String name, String role, String location) {
        // WHAT: Assign constructor parameter to instance field
        // WHY: Initialize the object's id field with provided value
        // HOW: 'this.id' refers to instance field, 'id' is the parameter
        this.id = id;
        this.username = username; // Assign username parameter to instance field
        this.password = password; // Assign password parameter to instance field
        this.name = name; // Assign name parameter to instance field
        this.role = role; // Assign role parameter to instance field
        this.location = location; // Assign location parameter to instance field
    }
    
    /**
     * Constructor - Creates a new User object (backward compatibility)
     * WHAT: Initializes user without location (for backward compatibility)
     * WHY: Existing code may call constructor without location parameter
     * HOW: Calls main constructor with null location
     */
    public User(int id, String username, String password, String name, String role) {
        this(id, username, password, name, role, null);
    }

    // --- Getters (Encapsulation: Accessors) ---
    // WHAT: Public methods that allow read access to private fields
    // WHY: Encapsulation principle - external code can read but not directly modify private fields
    // HOW: Return the value of the corresponding private field
    
    /**
     * WHAT: Returns the user's unique ID
     * WHY: Needed for database operations and user identification
     * HOW: Simply returns the private id field value
     */
    public int getId() { return id; }
    
    /**
     * WHAT: Returns the username
     * WHY: Used for display and authentication purposes
     * HOW: Returns the private username field value
     */
    public String getUsername() { return username; }
    
    /**
     * WHAT: Returns the password (for login verification)
     * WHY: Needed to compare with user input during authentication
     * HOW: Returns the private password field value
     */
    public String getPassword() { return password; }
    
    /**
     * WHAT: Returns the user's full name
     * WHY: Displayed in GUI to show who is logged in
     * HOW: Returns the private name field value
     */
    public String getName() { return name; }
    
    /**
     * WHAT: Returns the user's role
     * WHY: Determines access permissions and available features
     * HOW: Returns the private role field value
     */
    public String getRole() { return role; }

    // --- Setters (Encapsulation: Mutators) ---
    // WHAT: Public methods that allow controlled modification of private fields
    // WHY: Encapsulation - allows updates while maintaining data integrity
    // HOW: Accept a new value and assign it to the corresponding private field
    
    /**
     * WHAT: Updates the username
     * WHY: Allows username changes (e.g., user profile updates)
     * HOW: Assigns new username value to private field
     * @param username New username value
     */
    public void setUsername(String username) { this.username = username; }
    
    /**
     * WHAT: Updates the password
     * WHY: Allows password changes for security
     * HOW: Assigns new password value to private field
     * @param password New password value
     */
    public void setPassword(String password) { this.password = password; }
    
    /**
     * WHAT: Updates the user's name
     * WHY: Allows name corrections or changes
     * HOW: Assigns new name value to private field
     * @param name New name value
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * WHAT: Updates the user's role
     * WHY: Allows role changes (e.g., admin promoting user to seller)
     * HOW: Assigns new role value to private field
     * @param role New role value (ADMIN/BUYER/SELLER)
     */
    public void setRole(String role) { this.role = role; }
    
    /**
     * WHAT: Returns the user's location/warehouse address
     * WHY: Needed for delivery options and transaction coordination
     * HOW: Returns private location field value
     */
    public String getLocation() { return location; }
    
    /**
     * WHAT: Updates the user's location/warehouse address
     * WHY: Allows users to set or update their location information
     * HOW: Assigns new location value to private field
     * @param location New location/warehouse address
     */
    public void setLocation(String location) { this.location = location; }
}
