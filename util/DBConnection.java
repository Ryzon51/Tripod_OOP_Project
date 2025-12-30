package util; // Package declaration: Groups this class with other utility classes

import java.io.File; // Import: File class for file operations
import java.sql.Connection; // Import: Connection interface for database connections
import java.sql.DriverManager; // Import: DriverManager for creating database connections
import java.sql.PreparedStatement; // Import: PreparedStatement for parameterized SQL queries
import java.sql.ResultSet; // Import: ResultSet for reading query results
import java.sql.SQLException; // Import: SQLException for database error handling
import java.sql.Statement; // Import: Statement for executing SQL DDL statements
// Import: H2 Server for enabling web console access (optional - only if H2 tools available)
// Note: org.h2.tools.Server is part of h2-2.4.240.jar

/**
 * DBConnection - Database Connection and Initialization Utility
 * WHAT: Manages H2 database connection, creates tables, initializes default users, and starts H2 server
 * WHY: Centralizes database setup - ensures tables exist, default users are available, and H2 Console is accessible
 * HOW: Uses static block for initialization, starts H2 server for web console access, provides getConnection() method
 * 
 * DATABASE: H2 Database (Server mode) - stores data in agritrack.mv.db file, accessible via H2 Console
 * H2 CONSOLE: Accessible at http://localhost:8082 (JDBC URL: jdbc:h2:tcp://localhost:9092/./agritrack)
 */
public class DBConnection {
    // WHAT: H2 Server instance for web console access
    // WHY: Enables H2 Console web interface to connect to the same database
    // HOW: Server.createTcpServer() creates TCP server, Server.createWebServer() creates web console
    // Note: Using Object type to avoid compilation issues if H2 tools not in classpath during linting
    private static Object tcpServer;
    private static Object webServer;
    
    // WHAT: JDBC connection URL for H2 database (TCP server mode)
    // WHY: Server mode allows H2 Console web interface to connect to the same database
    // HOW: "jdbc:h2:tcp://localhost:9092/./agritrack" connects to H2 TCP server on port 9092
    // URL pointing to the H2 database via TCP server (enables H2 Console access)
    private static final String URL = "jdbc:h2:tcp://localhost:9092/./agritrack";
    
    // WHAT: Alternative embedded URL (fallback if server fails to start)
    // WHY: Application can still work if server mode fails
    // HOW: "jdbc:h2:./agritrack" creates embedded H2 database file
    private static final String EMBEDDED_URL = "jdbc:h2:./agritrack";
    
    // WHAT: User's H2 Console database URL (from H2 Console settings)
    // WHY: Allows connection to existing H2 database configured in H2 Console
    // HOW: "jdbc:h2:~/test" connects to test database in user home directory
    // Note: Using AUTO_SERVER mode to allow multiple connections and prevent lock issues
    private static final String USER_DB_URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";
    
    // WHAT: Database username for connection
    // WHY: H2 requires username for authentication
    // HOW: Default "sa" (system administrator) for H2
    private static final String DB_USERNAME = "sa";
    
    // WHAT: Database password for connection
    // WHY: H2 requires password (can be empty)
    // HOW: Empty string for default H2 setup
    private static final String DB_PASSWORD = "";
    
    // WHAT: Current connection URL being used
    // WHY: Tracks whether we're using server mode or embedded mode
    // HOW: Set to URL (server) or EMBEDDED_URL (fallback) or USER_DB_URL (user's database)
    // Default to user's H2 Console database (jdbc:h2:~/test) to match H2 Console settings
    private static String currentURL = USER_DB_URL;
    
    // WHAT: Flag to use user's database from H2 Console
    // WHY: Allows switching between application database and user's H2 Console database
    // HOW: Set to true to use USER_DB_URL instead of default URLs
    // Default to true to use user's H2 Console database
    private static boolean useUserDatabase = true;
    
    /**
     * Static Initialization Block
     * WHAT: Executes automatically when class is first loaded
     * WHY: Ensures database is initialized before any database operations occur
     * HOW: Static block runs once when class is loaded by JVM
     */
    // Static block to start H2 server, register driver, create tables, and create default users
    static {
        // WHAT: Try block to handle initialization errors
        // WHY: Database initialization can fail (driver not found, SQL errors, server startup issues)
        // HOW: try-catch block wraps initialization code
        try {
            // WHAT: Load H2 JDBC driver class
            // WHY: JDBC requires driver to be loaded before creating connections
            // HOW: Class.forName() loads driver class, triggers driver registration
            Class.forName("org.h2.Driver");
            
            // WHAT: Start H2 TCP server for database access
            // WHY: Enables H2 Console web interface to connect to the same database
            // HOW: Uses reflection to call Server.createTcpServer() (avoids compilation dependency)
            try {
                // WHAT: Use reflection to access H2 Server class
                // WHY: Avoids compilation errors if H2 tools not in classpath during linting
                // HOW: Class.forName() loads Server class, getMethod() gets createTcpServer method
                Class<?> serverClass = Class.forName("org.h2.tools.Server");
                Object server = serverClass.getMethod("createTcpServer", String[].class)
                    .invoke(null, (Object) new String[]{"-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-baseDir", "./"});
                tcpServer = serverClass.getMethod("start").invoke(server);
                System.out.println("H2 TCP Server started on port 9092");
                System.out.println("H2 Console JDBC URL: jdbc:h2:tcp://localhost:9092/./agritrack");
            } catch (Exception e) {
                // WHAT: If TCP server fails, try to use existing server or fallback to embedded
                // WHY: Server might already be running or port might be in use
                // HOW: Check if server is already running, otherwise use embedded mode
                System.out.println("H2 TCP Server may already be running or port 9092 is in use.");
                System.out.println("Attempting to use existing server or falling back to embedded mode...");
                currentURL = EMBEDDED_URL; // Fallback to embedded mode
            }
            
            // WHAT: Start H2 Web Console server
            // WHY: Provides web-based database management interface
            // HOW: Uses reflection to call Server.createWebServer() on port 8082
            try {
                // WHAT: Use reflection to access H2 Server class for web console
                // WHY: Avoids compilation errors if H2 tools not in classpath during linting
                // HOW: Class.forName() loads Server class, getMethod() gets createWebServer method
                Class<?> serverClass = Class.forName("org.h2.tools.Server");
                // WHAT: Create web server with network access enabled
                // WHY: -webAllowOthers allows connections from other machines on the network
                // HOW: -webPort specifies the port, -webBindAddress 0.0.0.0 binds to all network interfaces
                Object server = serverClass.getMethod("createWebServer", String[].class)
                    .invoke(null, (Object) new String[]{
                        "-web", 
                        "-webAllowOthers", 
                        "-webPort", "8082",
                        "-webBindAddress", "0.0.0.0"  // Bind to all network interfaces
                    });
                webServer = serverClass.getMethod("start").invoke(server);
                System.out.println("========================================");
                System.out.println("H2 Web Console started successfully!");
                System.out.println("========================================");
                System.out.println("Access H2 Console at:");
                System.out.println("  Local: http://localhost:8082");
                System.out.println("  Network: http://10.53.192.28:8082");
                System.out.println("========================================");
            } catch (Exception e) {
                // WHAT: If web server fails, continue without console (not critical)
                // WHY: Application can work without web console
                // HOW: Print warning but continue initialization
                System.err.println("Warning: H2 Web Console could not be started.");
                System.err.println("Error: " + e.getMessage());
                System.err.println("Possible causes:");
                System.err.println("  - Port 8082 may already be in use");
                System.err.println("  - Firewall may be blocking port 8082");
                System.err.println("  - H2 tools JAR may not be in classpath");
                System.err.println("You can manually start H2 Console or use embedded mode.");
                e.printStackTrace();
            }
            
            // WHAT: Create database connection to user's H2 Console database
            // WHY: H2 creates database file on first connection, need to establish connection first
            // HOW: Directly connect to USER_DB_URL with credentials to create database file
            // Note: Using AUTO_SERVER mode allows multiple connections (H2 Console + Application)
            try (Connection testConn = DriverManager.getConnection(USER_DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String dbPath = USER_DB_URL.replace("jdbc:h2:", "").replace("~", System.getProperty("user.home"));
                System.out.println("========================================");
                System.out.println("✓ DATABASE CONNECTION ESTABLISHED");
                System.out.println("========================================");
                System.out.println("Database URL: " + USER_DB_URL);
                System.out.println("Database File: " + dbPath + ".mv.db");
                System.out.println("Username: " + DB_USERNAME);
                System.out.println("Password: (empty)");
                System.out.println("");
                System.out.println("H2 Console Connection Settings:");
                System.out.println("  Driver Class: org.h2.Driver");
                System.out.println("  JDBC URL: " + USER_DB_URL);
                System.out.println("  User Name: " + DB_USERNAME);
                System.out.println("  Password: (leave empty)");
                System.out.println("");
                System.out.println("✓ All data from the application will be saved to this database");
                System.out.println("✓ When you add items in the application, they will appear in H2 Console");
                System.out.println("========================================");
            } catch (SQLException e) {
                // WHAT: Check if error is due to database being locked (in use)
                // WHY: Database might be locked by H2 Console, but we still want to use it
                // HOW: Check error message for lock-related keywords
                String errorMsg = e.getMessage();
                if (errorMsg != null && (errorMsg.contains("locked") || errorMsg.contains("in use") || errorMsg.contains("90020"))) {
                    System.out.println("Note: User's database is currently in use (likely by H2 Console).");
                    System.out.println("Application will attempt to connect when database is available.");
                    System.out.println("Please close H2 Console or wait a moment, then restart the application.");
                    // Keep useUserDatabase = true so it will try again on next connection
                } else {
                    System.err.println("Note: Could not connect to user's database during initialization: " + e.getMessage());
                    System.err.println("Application will use default database. You can switch databases later.");
                    useUserDatabase = false;
                    currentURL = EMBEDDED_URL;
                }
            }
            
            // WHAT: Create database tables if they don't exist
            // WHY: Tables must exist before application can store/retrieve data
            // HOW: createTables() executes CREATE TABLE IF NOT EXISTS statements
            createTables();
            
            // WHAT: Create default users if no users exist
            // WHY: Provides test accounts (admin, seller, buyer) for immediate use
            // HOW: createDefaultUsers() checks user count, inserts defaults if empty
            createDefaultUsers();
            
        } catch (ClassNotFoundException e) {
            // WHAT: Handle case where H2 driver JAR is not in classpath
            // WHY: Application cannot connect to database without driver
            // HOW: catch block executes when driver class not found
            // WHAT: Print error message to console
            // WHY: Developer needs to know driver is missing
            // HOW: System.err.println() writes to error output stream
            System.err.println("Error: H2 JDBC Driver not found. " + e.getMessage());
            System.err.println("Make sure h2-2.4.240.jar is in the classpath.");
        } catch (SQLException e) {
            // WHAT: Handle database initialization errors
            // WHY: Table creation or user creation can fail
            // HOW: catch block executes when SQLException occurs
            // WHAT: Print error message to console
            // WHY: Developer needs to know what went wrong
            // HOW: System.err.println() writes error message
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * getConnection() - Gets database connection
     * WHAT: Creates and returns a Connection object to the H2 database
     * WHY: All database operations need a connection object
     * HOW: DriverManager.getConnection() creates connection using current URL (server or embedded or user's database)
     * @return Connection object for database operations
     * @throws SQLException If connection cannot be established
     */
    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        // WHAT: Check if user wants to use their H2 Console database
        // WHY: User may want to connect to their existing database
        // HOW: If useUserDatabase flag is true, use USER_DB_URL with credentials
        if (useUserDatabase) {
            // WHAT: Try to connect with retry logic for locked databases
            // WHY: Database might be temporarily locked by H2 Console
            // HOW: Try connection up to 3 times with short delay
            int maxRetries = 3;
            int retryDelay = 500; // milliseconds
            SQLException lastException = null;
            
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    // WHAT: Connect to user's H2 Console database with credentials
                    // WHY: Matches H2 Console settings (jdbc:h2:~/test, sa, empty password)
                    // HOW: DriverManager.getConnection() with URL, username, and password
                    // H2 will automatically create the database file if it doesn't exist
                    Connection conn = DriverManager.getConnection(USER_DB_URL, DB_USERNAME, DB_PASSWORD);
                    // WHAT: Verify connection is working by checking database metadata
                    // WHY: Ensures connection is valid and database is accessible
                    // HOW: getMetaData() returns database metadata, confirms connection
                    String dbUrl = conn.getMetaData().getURL();
                    if (attempt > 1) {
                        System.out.println("✓ Successfully connected after retry attempt " + attempt);
                    } else {
                        System.out.println("✓ Successfully connected to H2 database: " + dbUrl);
                    }
                    System.out.println("✓ Database file location: " + dbUrl.replace("jdbc:h2:", "").replace("~", System.getProperty("user.home")));
                    return conn;
                } catch (SQLException e) {
                    lastException = e;
                    // WHAT: Check if error is due to database being locked (in use by H2 Console)
                    // WHY: If database is locked, we should retry, not immediately fallback
                    // HOW: Check error message for lock-related keywords
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && (errorMsg.contains("locked") || errorMsg.contains("in use") || errorMsg.contains("90020"))) {
                        if (attempt < maxRetries) {
                            // WHAT: Wait before retrying (longer delay for later attempts)
                            // WHY: Database might need more time to release lock
                            // HOW: Thread.sleep() pauses execution, longer delay for later attempts
                            try {
                                int delay = retryDelay * attempt; // Increase delay with each attempt
                                Thread.sleep(delay);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break; // Exit retry loop if interrupted
                            }
                            System.out.println("⚠ Database locked, retrying... (attempt " + (attempt + 1) + "/" + maxRetries + ")");
                            // Continue to next iteration (retry connection)
                        } else {
                            // WHAT: All retries failed, provide detailed instructions
                            // WHY: User needs clear steps to resolve the lock issue
                            // HOW: Throw SQLException with comprehensive message
                            // WHAT: Try to automatically clean up lock file before giving up
                            // WHY: Lock file might be stale and can be safely deleted
                            // HOW: Call cleanupLockFile() to attempt deletion
                            boolean lockCleaned = cleanupLockFile();
                            
                            System.err.println("⚠ Database is still locked after " + maxRetries + " attempts.");
                            if (lockCleaned) {
                                System.err.println("✓ Attempted to clean up lock file. Please try connecting again.");
                                // Try one more time after cleanup
                                try {
                                    Thread.sleep(1000);
                                    Connection conn = DriverManager.getConnection(USER_DB_URL, DB_USERNAME, DB_PASSWORD);
                                    System.out.println("✓ Successfully connected after lock file cleanup!");
                                    return conn;
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    System.err.println("✗ Interrupted during retry after cleanup.");
                                } catch (SQLException retryEx) {
                                    System.err.println("✗ Still locked after cleanup. Manual intervention needed.");
                                }
                            }
                            
                            System.err.println("Possible solutions:");
                            System.err.println("  1. Close ALL H2 Console browser tabs");
                            System.err.println("  2. Wait 5-10 seconds for lock to release");
                            System.err.println("  3. Manually delete lock file: " + getLockFilePath());
                            System.err.println("  4. Restart the application");
                            throw new SQLException("Database locked: Database may be already in use. Please close H2 Console, wait a few seconds, and try again.", e);
                        }
                    } else {
                        // WHAT: Different error, don't retry
                        // WHY: Other errors won't be fixed by retrying
                        // HOW: Break out of retry loop
                        break;
                    }
                }
            }
            
            // WHAT: If we get here, connection failed for non-lock reasons
            // WHY: Need to handle other connection errors
            // HOW: Check if we should fallback or throw exception
            if (lastException != null) {
                System.err.println("✗ Failed to connect to user's database (" + USER_DB_URL + "): " + lastException.getMessage());
                System.err.println("Error details: " + lastException.getSQLState() + " - " + lastException.getMessage());
                System.err.println("Falling back to application database...");
                useUserDatabase = false; // Fallback to application database
                currentURL = EMBEDDED_URL; // Switch to embedded mode
            }
        }
        
        // WHAT: Try to create connection using current URL
        // WHY: Application should work with fallback database
        // HOW: Try current URL, fallback to embedded if needed
        try {
            // WHAT: If using embedded or server mode, connect without credentials
            // WHY: Application database may not require credentials
            // HOW: DriverManager.getConnection() with URL only
            Connection conn = DriverManager.getConnection(currentURL);
            System.out.println("✓ Connected to database: " + currentURL);
            return conn;
        } catch (SQLException e) {
            // WHAT: If server mode fails, try embedded mode
            // WHY: Application should work even if server didn't start
            // HOW: Try embedded URL as fallback
            if (currentURL.equals(URL)) {
                System.out.println("Server mode connection failed, trying embedded mode...");
                currentURL = EMBEDDED_URL;
                Connection conn = DriverManager.getConnection(EMBEDDED_URL);
                System.out.println("✓ Connected to embedded database: " + EMBEDDED_URL);
                return conn;
            }
            throw e; // Re-throw if embedded also fails
        }
    }
    
    /**
     * setUseUserDatabase() - Sets whether to use user's H2 Console database
     * WHAT: Enables/disables connection to user's H2 Console database (jdbc:h2:~/test)
     * WHY: Allows user to view their existing database data in the application
     * HOW: Sets useUserDatabase flag to true/false
     * @param useUserDb true to use user's database, false to use application database
     */
    public static void setUseUserDatabase(boolean useUserDb) {
        useUserDatabase = useUserDb;
        if (useUserDb) {
            System.out.println("Switched to user's H2 Console database: " + USER_DB_URL);
        } else {
            System.out.println("Switched to application database: " + currentURL);
        }
    }
    
    /**
     * isUsingUserDatabase() - Checks if currently using user's database
     * WHAT: Returns whether the connection is using user's H2 Console database
     * WHY: Useful for displaying current connection status
     * HOW: Returns useUserDatabase flag value
     * @return true if using user's database, false if using application database
     */
    public static boolean isUsingUserDatabase() {
        return useUserDatabase;
    }
    
    /**
     * getUserDatabaseURL() - Returns user's H2 Console database URL
     * WHAT: Returns the JDBC URL for user's H2 Console database (with AUTO_SERVER)
     * WHY: Useful for displaying connection info
     * HOW: Returns USER_DB_URL constant
     * @return String JDBC URL for user's database
     */
    public static String getUserDatabaseURL() {
        return USER_DB_URL;
    }
    
    /**
     * getUserDatabaseURLClean() - Returns clean H2 Console database URL (without AUTO_SERVER)
     * WHAT: Returns the JDBC URL without AUTO_SERVER parameter for H2 Console display
     * WHY: H2 Console can use either URL, but clean URL is simpler for users
     * HOW: Removes AUTO_SERVER parameter from URL
     * @return String Clean JDBC URL for H2 Console
     */
    public static String getUserDatabaseURLClean() {
        return "jdbc:h2:~/test";
    }
    
    /**
     * getConnectionURL() - Returns the current database connection URL
     * WHAT: Returns the JDBC URL being used for database connections
     * WHY: Useful for displaying connection info to users or for H2 Console
     * HOW: Returns currentURL field value
     * @return String JDBC connection URL
     */
    public static String getConnectionURL() {
        return currentURL;
    }
    
    /**
     * getH2ConsoleURL() - Returns H2 Console web interface URL
     * WHAT: Returns the web URL for accessing H2 Console
     * WHY: Users need to know how to access the web console
     * HOW: Returns hardcoded localhost URL
     * @return String H2 Console web URL
     */
    public static String getH2ConsoleURL() {
        return "http://localhost:8082";
    }
    
    /**
     * getH2ConsoleConnectionInfo() - Returns formatted connection information for H2 Console
     * WHAT: Returns a formatted string with all connection details needed for H2 Console
     * WHY: Users need to know exact connection settings to use in H2 Console
     * HOW: Formats connection information into readable string
     * @return String with formatted connection information
     */
    public static String getH2ConsoleConnectionInfo() {
        StringBuilder info = new StringBuilder();
        info.append("H2 Console Connection Settings:\n");
        info.append("===============================\n");
        info.append("Driver Class: org.h2.Driver\n");
        info.append("JDBC URL: jdbc:h2:~/test (or jdbc:h2:~/test;AUTO_SERVER=TRUE)\n");
        info.append("User Name: ").append(DB_USERNAME).append("\n");
        info.append("Password: (leave empty)\n");
        info.append("\n");
        info.append("Web Console URL: ").append(getH2ConsoleURL()).append("\n");
        info.append("\n");
        info.append("Note: AUTO_SERVER mode is enabled to allow multiple connections.\n");
        return info.toString();
    }
    
    /**
     * getLockFilePath() - Returns the path to the H2 database lock file
     * WHAT: Constructs the full path to the lock file for the user's database
     * WHY: Needed for lock file cleanup operations
     * HOW: Replaces ~ with user home directory and appends .lock.db extension
     * @return String Full path to the lock file
     */
    public static String getLockFilePath() {
        String dbPath = USER_DB_URL.replace("jdbc:h2:", "").replace("~", System.getProperty("user.home"));
        // Remove AUTO_SERVER parameter if present
        dbPath = dbPath.split(";")[0];
        return dbPath + ".lock.db";
    }
    
    /**
     * cleanupLockFile() - Attempts to delete H2 database lock file
     * WHAT: Deletes the lock file if it exists and is stale
     * WHY: Stale lock files can prevent database access even when no connections are active
     * HOW: Checks if lock file exists, attempts to delete it
     * @return true if lock file was deleted or didn't exist, false if deletion failed
     */
    public static boolean cleanupLockFile() {
        try {
            String lockFilePath = getLockFilePath();
            File lockFile = new File(lockFilePath);
            
            if (lockFile.exists()) {
                // WHAT: Try to delete the lock file
                // WHY: Stale lock files can be safely deleted if no active connections
                // HOW: delete() method removes the file
                boolean deleted = lockFile.delete();
                if (deleted) {
                    System.out.println("✓ Deleted stale lock file: " + lockFilePath);
                    return true;
                } else {
                    System.err.println("✗ Could not delete lock file (may be in use): " + lockFilePath);
                    return false;
                }
            } else {
                // WHAT: Lock file doesn't exist
                // WHY: No lock file means no lock issue from file system
                // HOW: Return true since there's nothing to clean
                return true;
            }
        } catch (Exception e) {
            System.err.println("✗ Error cleaning up lock file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * shutdownServers() - Stops H2 servers gracefully
     * WHAT: Shuts down TCP and Web servers when application exits
     * WHY: Clean shutdown prevents resource leaks
     * HOW: Uses reflection to call stop() on server instances if they exist
     */
    public static void shutdownServers() {
        // WHAT: Stop web server if it exists
        // WHY: Clean shutdown prevents resource leaks
        // HOW: Use reflection to call stop() method
        if (webServer != null) {
            try {
                webServer.getClass().getMethod("stop").invoke(webServer);
                System.out.println("H2 Web Console stopped.");
            } catch (Exception e) {
                // Ignore errors during shutdown
            }
        }
        
        // WHAT: Stop TCP server if it exists
        // WHY: Clean shutdown prevents resource leaks
        // HOW: Use reflection to call stop() method
        if (tcpServer != null) {
            try {
                tcpServer.getClass().getMethod("stop").invoke(tcpServer);
                System.out.println("H2 TCP Server stopped.");
            } catch (Exception e) {
                // Ignore errors during shutdown
            }
        }
    }

    /**
     * createTables() - Creates database tables if they don't exist
     * WHAT: Executes CREATE TABLE IF NOT EXISTS statements for INVENTORY_ITEM and USER tables
     * WHY: Tables must exist before application can store data
     * HOW: Uses Statement.execute() to run DDL (Data Definition Language) SQL statements
     * @throws SQLException If table creation fails
     */
    // Creates the required tables: Inventory and User
    private static void createTables() throws SQLException {
        // WHAT: Try-with-resources block to automatically close database resources
        // WHY: Ensures Connection and Statement are closed even if exception occurs
        // HOW: try (resource) syntax automatically calls close() when block exits
        // Note: getConnection() is called here, which will create the database file if it doesn't exist
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // WHAT: SQL statement to create INVENTORY_ITEM table
            // WHY: Stores all inventory items (harvests, equipment) in this table
            // HOW: CREATE TABLE IF NOT EXISTS creates table only if it doesn't already exist
            // 1. INVENTORY_ITEM Table
            // NOTE: Using IDENTITY for H2 compatibility (simplified syntax)
            String sqlInventory = "CREATE TABLE IF NOT EXISTS INVENTORY_ITEM (" +
                                  "item_id INTEGER PRIMARY KEY AUTO_INCREMENT," + // Auto-incrementing primary key (H2 compatible)
                                  "name VARCHAR(255) NOT NULL," + // Item name, required, max 255 characters
                                  "quantity DOUBLE NOT NULL," + // Quantity as decimal number, required
                                  "unit VARCHAR(50) NOT NULL," + // Unit of measurement, required, max 50 characters
                                  "item_type VARCHAR(50) NOT NULL," + // Item type (HARVEST or EQUIPMENT), required
                                  "date_added VARCHAR(50) NOT NULL," + // Date as string (yyyy-MM-dd), required
                                  "status VARCHAR(50)," + // Status/condition field, optional
                                  "notes VARCHAR(1000)," + // Optional notes, max 1000 characters
                                  "price_per_unit DOUBLE" + // Price per unit (e.g., per kg), optional
                                  ")";
            
            // WHAT: Execute CREATE TABLE statement
            // WHY: Actually creates the table in database
            // HOW: execute() method runs DDL statement
            // Note: If table already exists with wrong syntax (AUTO_INCREMENT), it won't be recreated
            // User may need to manually drop and recreate the table in H2 Console if errors occur
            stmt.execute(sqlInventory);
            
            // WHAT: Add price_per_unit column if it doesn't exist (for existing databases)
            // WHY: Existing databases may not have the price column
            // HOW: ALTER TABLE with try-catch (H2 doesn't support IF NOT EXISTS in ALTER TABLE)
            try {
                stmt.execute("ALTER TABLE INVENTORY_ITEM ADD COLUMN price_per_unit DOUBLE");
            } catch (SQLException e) {
                // Column may already exist, ignore error
            }
            
            // WHAT: Fix AUTO_INCREMENT sequence to prevent primary key violations
            // WHY: When records are manually inserted via H2 Console with explicit IDs, the AUTO_INCREMENT sequence doesn't update
            // HOW: Find the maximum existing ID and set the sequence to max+1
            try {
                // Check if table has any records
                ResultSet rs = stmt.executeQuery("SELECT MAX(item_id) FROM INVENTORY_ITEM");
                if (rs.next()) {
                    int maxId = rs.getInt(1);
                    if (maxId > 0) {
                        // Set the sequence to max+1 to avoid conflicts
                        // H2 uses ALTER TABLE to reset the sequence
                        try {
                            stmt.execute("ALTER TABLE INVENTORY_ITEM ALTER COLUMN item_id RESTART WITH " + (maxId + 1));
                            System.out.println("✓ Fixed AUTO_INCREMENT sequence: next ID will be " + (maxId + 1));
                        } catch (SQLException e) {
                            // If RESTART WITH doesn't work, try alternative H2 syntax
                            try {
                                stmt.execute("ALTER TABLE INVENTORY_ITEM ALTER COLUMN item_id RESTART " + (maxId + 1));
                                System.out.println("✓ Fixed AUTO_INCREMENT sequence: next ID will be " + (maxId + 1));
                            } catch (SQLException e2) {
                                // If both fail, log warning but continue
                                System.out.println("⚠ Could not reset AUTO_INCREMENT sequence. If you get primary key errors, manually fix the sequence in H2 Console.");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                // Table might be empty or error occurred, continue anyway
                System.out.println("Note: Could not check/fix AUTO_INCREMENT sequence: " + e.getMessage());
            }

            // WHAT: SQL statement to create USER table
            // WHY: Stores user accounts (admin, seller, buyer) in this table
            // HOW: CREATE TABLE IF NOT EXISTS, "USER" in quotes because USER is H2 reserved keyword
            // 2. USER Table (quoted because USER is a reserved keyword in H2)
            // NOTE: Using AUTO_INCREMENT for H2 compatibility
            String sqlUser = "CREATE TABLE IF NOT EXISTS \"USER\" (" +
                             "user_id INTEGER PRIMARY KEY AUTO_INCREMENT," + // Auto-incrementing primary key (H2 compatible)
                             "username VARCHAR(100) NOT NULL UNIQUE," + // Username, required, must be unique
                             "password VARCHAR(255) NOT NULL," + // Password, required, max 255 characters
                             "name VARCHAR(255)," + // User's full name, optional, max 255 characters
                             "role VARCHAR(50) NOT NULL," + // User role (ADMIN, BUYER, SELLER), required
                             "location VARCHAR(500)" + // User's location/warehouse address, optional
                             ")"; // "ADMIN", "BUYER", "SELLER"
            
            // WHAT: Add location column if it doesn't exist (for existing databases)
            // WHY: Existing databases may not have the location column
            // HOW: ALTER TABLE with try-catch (H2 doesn't support IF NOT EXISTS in ALTER TABLE)
            try {
                stmt.execute("ALTER TABLE \"USER\" ADD COLUMN location VARCHAR(500)");
            } catch (SQLException e) {
                // Column may already exist, ignore error
            }
            
            // WHAT: Execute CREATE TABLE statement
            // WHY: Actually creates the table in database
            // HOW: execute() method runs DDL statement
            stmt.execute(sqlUser);
            
            // WHAT: Print success message to console
            // WHY: Confirms tables were created successfully
            // HOW: System.out.println() writes to standard output
            System.out.println("Database tables checked/created successfully.");
        }
    }
    
    /**
     * createDefaultUsers() - Creates default user accounts for testing
     * WHAT: Checks if USER table is empty, inserts admin, seller, and buyer accounts if empty
     * WHY: Provides test accounts so application can be used immediately without manual user creation
     * HOW: Counts existing users, inserts three default users if count is zero
     * @throws SQLException If user creation fails
     */
    // Adds default users for immediate testing
    private static void createDefaultUsers() throws SQLException {
        // WHAT: SQL query to count existing users
        // WHY: Only create defaults if no users exist (avoid duplicates)
        // HOW: SELECT COUNT(*) counts all rows in USER table, "USER" in quotes for H2
        String countSql = "SELECT COUNT(*) FROM \"USER\"";
        
        // WHAT: SQL INSERT statement to create new user
        // WHY: Need to insert user records into database
        // HOW: INSERT INTO with column names and ? placeholders, "USER" in quotes for H2
        String insertSql = "INSERT INTO \"USER\" (username, password, name, role) VALUES (?, ?, ?, ?)";
        
        // WHAT: Try-with-resources block for database operations
        // WHY: Ensures resources are properly closed
        // HOW: try (resource) syntax
        try (Connection conn = getConnection();
             Statement countStmt = conn.createStatement();
             ResultSet rs = countStmt.executeQuery(countSql)) {
            
            // WHAT: Check if user count is zero (no users exist)
            // WHY: Only create defaults if table is empty
            // HOW: rs.next() moves to first row, getInt(1) gets count value, == 0 checks if empty
            if (rs.next() && rs.getInt(1) == 0) { // If no users exist, insert defaults
                // WHAT: Try-with-resources block for PreparedStatement
                // WHY: Ensures PreparedStatement is closed
                // HOW: try (resource) syntax
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    
                    // WHAT: Create Admin user account
                    // WHY: Admin account needed for administrative functions
                    // HOW: Set parameters and execute INSERT statement
                    // Admin User (User for Role-Based Access)
                    insertStmt.setString(1, "admin"); // Username
                    insertStmt.setString(2, "admin123"); // Password
                    insertStmt.setString(3, "Admin User"); // Name
                    insertStmt.setString(4, "ADMIN"); // Role
                    insertStmt.executeUpdate(); // Execute INSERT

                    // WHAT: Create Seller/Farmer user account
                    // WHY: Seller account needed for adding/managing harvest records
                    // HOW: Set parameters and execute INSERT statement
                    // Seller/Farmer User (User for CRUD)
                    insertStmt.setString(1, "seller"); // Username
                    insertStmt.setString(2, "seller123"); // Password
                    insertStmt.setString(3, "Juan Dela Cruz (Farmer)"); // Name
                    insertStmt.setString(4, "SELLER"); // Role
                    insertStmt.executeUpdate(); // Execute INSERT
                    
                    // WHAT: Create Buyer user account
                    // WHY: Buyer account needed for browsing and purchasing harvests
                    // HOW: Set parameters and execute INSERT statement
                    // Buyer User (User for Viewing/Buying Harvests)
                    insertStmt.setString(1, "buyer"); // Username (lowercase for consistency)
                    insertStmt.setString(2, "buyer123"); // Password
                    insertStmt.setString(3, "John Buyer"); // Name
                    insertStmt.setString(4, "BUYER"); // Role
                    insertStmt.executeUpdate(); // Execute INSERT
                    
                    // WHAT: Print success message to console
                    // WHY: Confirms default users were created
                    // HOW: System.out.println() writes to standard output
                    System.out.println("Default users created: admin, seller, buyer.");
                }
            }
            // If users already exist, skip creation (no action needed)
        }
    }
}
