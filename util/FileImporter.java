package util; // Package declaration: Groups this class with other utility classes

import dao.InventoryDAO; // Import: InventoryDAO for adding imported records to database
import java.io.BufferedReader; // Import: BufferedReader for efficient text file reading
import java.io.File; // Import: File class for file operations
import java.io.FileReader; // Import: FileReader for reading from files
import java.io.IOException; // Import: IOException for file I/O error handling
import java.time.LocalDate; // Import: LocalDate for date parsing
import java.time.format.DateTimeFormatter; // Import: DateTimeFormatter for date string parsing
import java.util.ArrayList; // Import: ArrayList for storing parsed items
import java.util.List; // Import: List interface for collections
import javax.swing.JFileChooser; // Import: JFileChooser for file selection dialog
import javax.swing.JFrame; // Import: JFrame for parent window reference
import javax.swing.JOptionPane; // Import: JOptionPane for user feedback dialogs
import model.FarmItem; // Import: FarmItem base class
import model.HarvestLot; // Import: HarvestLot class for creating imported items

/**
 * FileImporter - Utility Class for Importing Data from CSV Files
 * WHAT: Imports inventory records from CSV (Comma-Separated Values) file format
 * WHY: Allows users to restore data from backup or import data from external sources
 * HOW: Reads CSV file line by line, parses data, creates FarmItem objects, inserts into database
 */
public class FileImporter {
    // WHAT: Date formatter for parsing date strings from CSV
    // WHY: CSV stores dates as strings (yyyy-MM-dd format), need to convert to LocalDate
    // HOW: DateTimeFormatter.ofPattern() creates formatter matching CSV date format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * importFromCSV() - Imports inventory records from CSV file
     * WHAT: Shows file chooser dialog, reads CSV file, parses records, adds to database
     * WHY: Allows users to import data from CSV files (backup restore, bulk import)
     * HOW: Uses JFileChooser for file selection, parses CSV, creates items, inserts via DAO
     * @param parent Parent JFrame for centering file chooser dialog
     */
    public static void importFromCSV(JFrame parent) {
        // WHAT: Try block to handle file I/O and database exceptions
        // WHY: File operations and database operations can throw exceptions
        // HOW: try-catch block wraps all operations
        try {
            // WHAT: Create file chooser dialog for user to select CSV file
            // WHY: User needs to choose which file to import
            // HOW: JFileChooser creates native file selection dialog
            JFileChooser fileChooser = new JFileChooser();
            
            // WHAT: Set dialog title text
            // WHY: Identifies the purpose of the file chooser dialog
            // HOW: setDialogTitle() sets text displayed in dialog title bar
            fileChooser.setDialogTitle("Import CSV File");
            
            // WHAT: Set file filter to show only CSV files
            // WHY: User should only select CSV files, not other file types
            // HOW: FileNameExtensionFilter restricts visible files to .csv extension
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            
            // WHAT: Show file chooser dialog and get user's selection
            // WHY: User needs to select which CSV file to import
            // HOW: showOpenDialog() displays dialog, returns APPROVE_OPTION if user selects file
            int userSelection = fileChooser.showOpenDialog(parent);
            
            // WHAT: Check if user selected a file (didn't cancel)
            // WHY: Only proceed if user actually selected a file
            // HOW: APPROVE_OPTION constant returned when user clicks Open/OK
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // WHAT: Get the file object that user selected
                // WHY: Need file path to read CSV data
                // HOW: getSelectedFile() returns File object representing selected file
                File fileToImport = fileChooser.getSelectedFile();
                
                // WHAT: Parse CSV file and convert to list of FarmItem objects
                // WHY: CSV data must be converted to Java objects before database insertion
                // HOW: parseCSVFile() reads file, parses each line, creates FarmItem objects
                List<FarmItem> importedItems = parseCSVFile(fileToImport.getAbsolutePath());
                
                // WHAT: Check if any items were successfully parsed
                // WHY: Cannot import if file is empty or contains no valid records
                // HOW: isEmpty() checks if list has no elements
                if (!importedItems.isEmpty()) {
                    // WHAT: Create InventoryDAO object for database operations
                    // WHY: DAO handles database insertion of imported items
                    // HOW: new InventoryDAO() creates instance
                    InventoryDAO dao = new InventoryDAO();
                    
                    // WHAT: Counter for successfully imported records
                    // WHY: Track how many records were imported for user feedback
                    // HOW: Integer variable incremented on each successful import
                    int importedCount = 0;
                    
                    // WHAT: Counter for failed import attempts
                    // WHY: Track errors for user feedback
                    // HOW: Integer variable incremented on each exception
                    int errorCount = 0;
                    
                    // WHAT: Loop through each parsed item
                    // WHY: Each item must be inserted into database individually
                    // HOW: Enhanced for loop iterates through list
                    for (FarmItem item : importedItems) {
                        // WHAT: Try block for individual item insertion
                        // WHY: One failed item shouldn't stop entire import
                        // HOW: try-catch around each addRecord() call
                        try {
                            // WHAT: Insert item into database
                            // WHY: Imported items must be saved to database
                            // HOW: addRecord() executes INSERT SQL statement
                            dao.addRecord(item);
                            
                            // WHAT: Increment success counter
                            // WHY: Track number of successful imports
                            // HOW: ++ operator increments counter
                            importedCount++;
                        } catch (Exception e) {
                            // WHAT: Handle exception for this item
                            // WHY: Log error but continue with next item
                            // HOW: catch block executes when exception occurs
                            // WHAT: Increment error counter
                            // WHY: Track number of failed imports
                            // HOW: ++ operator increments counter
                            errorCount++;
                            
                            // WHAT: Print error message to console
                            // WHY: Developer needs to see what went wrong
                            // HOW: System.err.println() writes to error output stream
                            System.err.println("Error importing record: " + e.getMessage());
                        }
                    }
                    
                    // WHAT: Show success dialog with import statistics
                    // WHY: User needs feedback about import results
                    // HOW: showMessageDialog() displays modal dialog with message
                    JOptionPane.showMessageDialog(parent, 
                        "Import completed!\n" + // Message text
                        "Successfully imported: " + importedCount + " records\n" + // Success count
                        "Errors: " + errorCount, // Error count
                        "Import Result", // Dialog title
                        JOptionPane.INFORMATION_MESSAGE); // Information icon
                } else {
                    // WHAT: Show warning dialog if no valid records found
                    // WHY: User needs to know why import didn't work
                    // HOW: showMessageDialog() with WARNING_MESSAGE type
                    JOptionPane.showMessageDialog(parent, 
                        "No valid records found in the file.", // Message text
                        "Import Error", // Dialog title
                        JOptionPane.WARNING_MESSAGE); // Warning icon
                }
            }
            // If user cancelled file chooser, no action needed (silently return)
        } catch (Exception ex) {
            // WHAT: Handle any exceptions during import process
            // WHY: File reading, parsing, or database errors must be shown to user
            // HOW: catch block executes when exception occurs
            // WHAT: Show error dialog with exception message
            // WHY: User needs to know what went wrong
            // HOW: showMessageDialog() with ERROR_MESSAGE type
            JOptionPane.showMessageDialog(parent, 
                "Error importing file: " + ex.getMessage(), // Error message with details
                "Import Error", // Dialog title
                JOptionPane.ERROR_MESSAGE); // Error icon
        }
    }
    
    /**
     * parseCSVFile() - Parses CSV file and creates FarmItem objects
     * WHAT: Reads CSV file line by line, parses comma-separated values, creates HarvestLot objects
     * WHY: CSV data must be converted to Java objects before database insertion
     * HOW: Reads file, skips header, parses each line, creates objects, returns list
     * @param filePath Full path to CSV file to parse
     * @return List of FarmItem objects parsed from CSV file
     * @throws IOException If file cannot be read
     */
    private static List<FarmItem> parseCSVFile(String filePath) throws IOException {
        // WHAT: Create empty list to store parsed items
        // WHY: Need collection to hold FarmItem objects as they're created
        // HOW: new ArrayList<>() creates empty list
        List<FarmItem> items = new ArrayList<>();
        
        // WHAT: Try-with-resources block to automatically close file reader
        // WHY: Ensures file is properly closed even if exception occurs
        // HOW: try (resource) syntax automatically calls close() when block exits
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // WHAT: Variable to hold each line read from file
            // WHY: CSV files are line-based, need to process one line at a time
            // HOW: String variable stores line content
            String line;
            
            // WHAT: Flag to track if we're reading the first line (header)
            // WHY: First line contains column names, should be skipped
            // HOW: Boolean flag set to true initially, set to false after first line
            boolean firstLine = true;
            
            // WHAT: Loop to read file line by line until end of file
            // WHY: Must process every line in CSV file
            // HOW: readLine() returns null when end of file reached
            while ((line = reader.readLine()) != null) {
                // WHAT: Skip header line (first line)
                // WHY: Header contains column names, not data
                // HOW: if statement checks firstLine flag
                if (firstLine) {
                    // WHAT: Mark that we've processed first line
                    // WHY: Subsequent lines are data, not header
                    // HOW: Set flag to false
                    firstLine = false;
                    // WHAT: Skip to next iteration without processing this line
                    // WHY: Header line should not be parsed as data
                    // HOW: continue statement jumps to next loop iteration
                    continue;
                }
                
                // WHAT: Skip empty lines
                // WHY: Empty lines don't contain data and would cause parsing errors
                // HOW: trim() removes whitespace, isEmpty() checks if empty
                if (line.trim().isEmpty()) {
                    // WHAT: Skip to next iteration without processing empty line
                    // WHY: Empty lines should be ignored
                    // HOW: continue statement jumps to next loop iteration
                    continue;
                }
                
                // WHAT: Try block to handle parsing errors for individual lines
                // WHY: One bad line shouldn't stop entire import
                // HOW: try-catch around parsing logic
                try {
                    // WHAT: Parse CSV line into array of string values
                    // WHY: CSV values are comma-separated, need to split them
                    // HOW: parseCSVLine() handles quoted values and commas correctly
                    String[] parts = parseCSVLine(line);
                    
                    // WHAT: Check if line has minimum required fields (at least 6)
                    // WHY: Need enough fields to create valid FarmItem object
                    // HOW: parts.length checks array size
                    if (parts.length >= 6) {
                        // WHAT: Parse CSV line fields (ID,Name,Quantity,Unit,Date_Added,Notes,Status)
                        // WHY: Each field must be extracted and converted to appropriate type
                        // HOW: Array indexing and type conversion
                        // Note: We skip ID as it will be auto-generated
                        
                        // WHAT: Extract name field (index 1, skip ID at index 0)
                        // WHY: Name is required field for FarmItem
                        // HOW: parts[1] gets second element, trim() removes whitespace, empty string if missing
                        String name = parts.length > 1 ? parts[1].trim() : "";
                        
                        // WHAT: Extract and parse quantity field (index 2)
                        // WHY: Quantity is required numeric field
                        // HOW: Double.parseDouble() converts string to double, 0.0 if missing
                        double quantity = parts.length > 2 ? Double.parseDouble(parts[2].trim()) : 0.0;
                        
                        // WHAT: Extract unit field (index 3), default to "kg" if missing
                        // WHY: Unit is required field, "kg" is reasonable default
                        // HOW: parts[3] gets fourth element, trim() removes whitespace, "kg" if missing
                        String unit = parts.length > 3 ? parts[3].trim() : "kg";
                        
                        // WHAT: Extract and parse date field (index 4)
                        // WHY: Date is required field for FarmItem
                        // HOW: LocalDate.parse() converts string to LocalDate, LocalDate.now() if missing
                        LocalDate dateAdded = parts.length > 4 && !parts[4].trim().isEmpty() 
                            ? LocalDate.parse(parts[4].trim(), DATE_FORMATTER) // Parse date string
                            : LocalDate.now(); // Use current date if missing
                        
                        // WHAT: Extract notes field (index 5), empty string if missing
                        // WHY: Notes is optional field
                        // HOW: parts[5] gets sixth element, trim() removes whitespace, empty string if missing
                        String notes = parts.length > 5 ? parts[5].trim() : "";
                        
                        // WHAT: Extract status field (index 6), default to "Fresh" if missing
                        // WHY: Status is optional field for HarvestLot
                        // HOW: parts[6] gets seventh element, trim() removes whitespace, "Fresh" if missing
                        String status = parts.length > 6 ? parts[6].trim() : "Fresh";
                        
                        // WHAT: Validate that name is not empty and quantity is positive
                        // WHY: Invalid data should not be imported
                        // HOW: if statement checks both conditions
                        if (!name.isEmpty() && quantity > 0) {
                            // WHAT: Create HarvestLot object from parsed data
                            // WHY: Need FarmItem object to insert into database
                            // HOW: new HarvestLot() constructor with parsed values, ID=0 (auto-generated)
                            HarvestLot item = new HarvestLot(0, name, quantity, unit, dateAdded, notes, status);
                            
                            // WHAT: Add created item to list
                            // WHY: List will be returned and items inserted into database
                            // HOW: add() method adds item to ArrayList
                            items.add(item);
                        }
                    }
                } catch (Exception e) {
                    // WHAT: Handle parsing errors for this line
                    // WHY: Bad line shouldn't stop entire import
                    // HOW: catch block executes when exception occurs
                    // WHAT: Print error message to console
                    // WHY: Developer needs to see what went wrong
                    // HOW: System.err.println() writes to error output stream
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        }
        // WHAT: File reader automatically closed here (try-with-resources)
        // WHY: Prevents resource leaks
        // HOW: BufferedReader.close() is called automatically when try block exits
        
        // WHAT: Return list of parsed FarmItem objects
        // WHY: Caller needs the items to insert into database
        // HOW: return statement returns the ArrayList
        return items;
    }
    
    /**
     * parseCSVLine() - Parses a single CSV line handling quoted values
     * WHAT: Splits CSV line by commas, but respects quoted values that may contain commas
     * WHY: CSV values can be quoted (e.g., "Smith, John") and may contain commas
     * HOW: Iterates through characters, tracks quote state, splits on commas outside quotes
     * @param line Single line from CSV file to parse
     * @return Array of string values extracted from CSV line
     */
    private static String[] parseCSVLine(String line) {
        // WHAT: Create list to store parsed values
        // WHY: Need collection to hold values as they're extracted
        // HOW: new ArrayList<>() creates empty list
        List<String> result = new ArrayList<>();
        
        // WHAT: Flag to track if we're currently inside quoted value
        // WHY: Commas inside quotes should not split the value
        // HOW: Boolean flag toggles when quote character encountered
        boolean inQuotes = false;
        
        // WHAT: StringBuilder to accumulate current value's characters
        // WHY: Values are built character by character
        // HOW: StringBuilder is efficient for string concatenation
        StringBuilder current = new StringBuilder();
        
        // WHAT: Loop through each character in the line
        // WHY: Need to process each character to detect quotes and commas
        // HOW: Enhanced for loop iterates through character array
        for (char c : line.toCharArray()) {
            // WHAT: Check if current character is a quote
            // WHY: Quotes mark the start/end of quoted values
            // HOW: == operator compares character
            if (c == '"') {
                // WHAT: Toggle quote state (inside quotes <-> outside quotes)
                // WHY: Quote character switches between quoted and unquoted mode
                // HOW: ! operator negates boolean value
                inQuotes = !inQuotes;
            } 
            // WHAT: Check if character is comma and we're not inside quotes
            // WHY: Commas outside quotes separate values, commas inside quotes are part of value
            // HOW: && operator checks both conditions
            else if (c == ',' && !inQuotes) {
                // WHAT: Add current value to result list
                // WHY: Comma outside quotes marks end of current value
                // HOW: toString() converts StringBuilder to String, add() adds to list
                result.add(current.toString());
                
                // WHAT: Reset StringBuilder for next value
                // WHY: Next value starts fresh
                // HOW: new StringBuilder() creates empty builder
                current = new StringBuilder();
            } else {
                // WHAT: Add character to current value
                // WHY: Character is part of current value (not a separator)
                // HOW: append() adds character to StringBuilder
                current.append(c);
            }
        }
        
        // WHAT: Add final value to result list (after last comma or end of line)
        // WHY: Last value doesn't end with comma, must be added separately
        // HOW: toString() converts StringBuilder to String, add() adds to list
        result.add(current.toString());
        
        // WHAT: Convert list to array and return
        // WHY: Caller expects array format
        // HOW: toArray() converts ArrayList to String array
        return result.toArray(new String[0]);
    }
}











