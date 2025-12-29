package util; // Package declaration: Groups this class with other utility classes

import java.io.BufferedWriter; // Import: BufferedWriter for efficient text file writing
import java.io.FileWriter; // Import: FileWriter for writing to files
import java.io.IOException; // Import: IOException for file I/O error handling
import java.util.List; // Import: List interface for collections of FarmItem objects
import model.FarmItem; // Import: FarmItem base class for inventory items

/**
 * CSVExporter - Utility Class for Exporting Data to CSV Files
 * WHAT: Exports inventory records to CSV (Comma-Separated Values) file format
 * WHY: Allows users to backup data or import into spreadsheet programs (Excel, Google Sheets)
 * HOW: Writes each FarmItem to CSV file using polymorphic toCSVString() method
 * 
 * OOP CONCEPT: Polymorphism - Uses toCSVString() method that each subclass implements differently
 */
public class CSVExporter {
    // WHAT: CSV file header row defining column names
    // WHY: Header row makes CSV file readable and identifies columns
    // HOW: String constant with comma-separated column names, \n for newline
    private static final String CSV_HEADER = "ID,Name,Quantity,Unit,Date_Added,Notes,Status\n";
    
    /**
     * exportToCSV() - Exports inventory records to CSV file
     * WHAT: Writes all inventory items to a CSV file with header row
     * WHY: Allows data backup and import into spreadsheet applications
     * HOW: Opens file, writes header, then writes each item using toCSVString() method
     * @param filePath Full path where CSV file should be created
     * @param records List of FarmItem objects to export
     * @throws IOException If file cannot be written (permissions, disk full, etc.)
     */
    public static void exportToCSV(String filePath, List<FarmItem> records) throws IOException {
        // WHAT: Try-with-resources block to automatically close file writer
        // WHY: Ensures file is properly closed even if exception occurs
        // HOW: try (resource) syntax automatically calls close() when block exits
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // WHAT: Write CSV header row to file
            // WHY: Header identifies columns for spreadsheet programs
            // HOW: write() method writes string to file
            writer.write(CSV_HEADER);
            
            // WHAT: Loop through each inventory item in the list
            // WHY: Each item must be written as a separate row in CSV file
            // HOW: Enhanced for loop iterates through List<FarmItem>
            for (FarmItem item : records) {
                // WHAT: Write item data as CSV row using polymorphic method
                // WHY: Each subclass (HarvestLot, EquipmentItem) formats its data differently
                // HOW: toCSVString() is abstract method implemented by each subclass - polymorphism
                // Uses Polymorphism via toCSVString() method defined in model classes
                writer.write(item.toCSVString() + "\n"); // Write CSV row and newline character
            }
        }
        // WHAT: File writer automatically closed here (try-with-resources)
        // WHY: Prevents resource leaks and ensures data is flushed to disk
        // HOW: BufferedWriter.close() is called automatically when try block exits
    }
}
