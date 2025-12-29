package main; // Package declaration: Groups this class with other main classes

import gui.LoginFrame; // Import: LoginFrame class for user authentication GUI
import javax.swing.SwingUtilities; // Import: Utility class to ensure thread-safe GUI operations
import util.DBConnection; // Import: DBConnection for database server shutdown

/**
 * AgriTrackApp - Main Entry Point
 * WHAT: This is the main application class that serves as the entry point for the AgriTrack application
 * WHY: Java applications require a main() method as the starting point for execution
 * HOW: Initializes database, then launches the GUI on the Event Dispatch Thread (EDT)
 */
public class AgriTrackApp {
    /**
     * main() - Application Entry Point
     * WHAT: Static method that Java runtime calls to start the application
     * WHY: Required by Java - the JVM looks for this exact method signature to begin execution
     * HOW: First initializes database, then safely launches GUI on EDT thread
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // WHAT: Initialize database connection and create tables
        // WHY: Database must be ready before any GUI operations that need data access
        // HOW: Loading the DBConnection class triggers its static block which creates tables/users
        try {
            // WHAT: Force class loading to trigger static initialization block
            // WHY: DBConnection has a static block that creates database tables and default users
            // HOW: Class.forName() loads the class, executing static blocks before returning
            Class.forName("util.DBConnection");
        } catch (ClassNotFoundException e) {
            // WHAT: Handle error if DBConnection class cannot be found
            // WHY: Application cannot function without database, so we must exit gracefully
            // HOW: Print error message and return to prevent further execution
            System.err.println("DBConnection Class not found. Application cannot start.");
            return; // Exit main method if database initialization fails
        }
        
        // WHAT: Launch the GUI application on the Event Dispatch Thread (EDT)
        // WHY: Swing components are NOT thread-safe and must run on EDT to prevent race conditions
        // HOW: invokeLater() schedules the Runnable (lambda) to run on EDT, ensuring thread safety
        SwingUtilities.invokeLater(() -> {
            // WHAT: Create and display the LoginFrame window
            // WHY: LoginFrame is the first window users see - handles authentication
            // HOW: Constructor automatically sets up GUI components and makes window visible
            LoginFrame loginFrame = new LoginFrame();
            
            // WHAT: Add shutdown hook to stop H2 servers when application exits
            // WHY: Ensures H2 servers are stopped gracefully when application closes
            // HOW: Runtime.getRuntime().addShutdownHook() registers shutdown handler
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DBConnection.shutdownServers();
            }));
        });
    }
}
