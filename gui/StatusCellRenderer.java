package gui; // Package declaration: Groups this class with other GUI classes

import java.awt.*; // Import: AWT classes for Component, Color, Font
import javax.swing.*; // Import: Swing classes for JTable, SwingConstants, BorderFactory
import javax.swing.table.DefaultTableCellRenderer; // Import: Base class for custom table cell rendering

/**
 * StatusCellRenderer - Custom Table Cell Renderer for Status Column
 * WHAT: Custom renderer that colors and formats the Status column in JTable based on status value
 * WHY: Provides visual feedback to users - color-coding makes status immediately recognizable
 * HOW: Extends DefaultTableCellRenderer and overrides getTableCellRendererComponent() to customize appearance
 * 
 * OOP CONCEPT: Inheritance - extends DefaultTableCellRenderer to customize behavior
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {
    /**
     * getTableCellRendererComponent() - Customizes how status cells are displayed
     * WHAT: Called by JTable for each cell in the Status column to determine how to render it
     * WHY: Allows custom colors, fonts, and icons based on status value (Available, Interested, Sold Out)
     * HOW: Checks status value, applies appropriate colors and icons, returns configured component
     * @param table The JTable that is asking the renderer to draw
     * @param value The value of the cell to be rendered (status string)
     * @param isSelected True if cell is currently selected
     * @param hasFocus True if cell has focus
     * @param row Row index of the cell
     * @param column Column index of the cell
     * @return Component configured to display the status cell
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        // WHAT: Call parent class method to get default rendering
        // WHY: Parent class handles basic rendering (selection colors, focus indicators)
        // HOW: super.getTableCellRendererComponent() returns base component with default styling
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // WHAT: Check if cell value is not null before processing
        // WHY: Null values would cause NullPointerException when calling toString()
        // HOW: if statement checks if value exists
        if (value != null) {
            // WHAT: Convert cell value to string (status text)
            // WHY: Need string to compare with status values and display
            // HOW: toString() converts Object to String
            String status = value.toString();
            
            // WHAT: Set font to Segoe UI, bold, size 12
            // WHY: Bold font makes status text more prominent and readable
            // HOW: Font constructor creates font, setFont() applies it to component
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            // WHAT: Switch statement to handle different status values
            // WHY: Each status needs different colors and icons for visual distinction
            // HOW: switch statement matches status string and applies appropriate styling
            switch (status) {
                // WHAT: Handle "Sold Out" status
                // WHY: Sold out items should be visually distinct (red indicates unavailability)
                // HOW: case statement matches "Sold Out" string
                case "Sold Out":
                    // WHAT: Set background color to light red
                    // WHY: Red background indicates item is unavailable
                    // HOW: Color(255, 235, 238) creates light red/pink background
                    setBackground(new Color(255, 235, 238)); // Light red
                    
                    // WHAT: Set text color to dark red
                    // WHY: Dark red text contrasts with light red background
                    // HOW: Color(198, 40, 40) creates dark red text color
                    setForeground(new Color(198, 40, 40)); // Dark red
                    
                    // WHAT: Prepend red circle emoji to status text
                    // WHY: Emoji provides additional visual cue for sold out status
                    // HOW: String concatenation adds emoji before status text
                    setText(status);
                    break; // Exit switch statement
                
                // WHAT: Handle "Interested" status
                // WHY: Interested items should be visually distinct (yellow/orange indicates pending interest)
                // HOW: case statement matches "Interested" string
                case "Interested":
                    // WHAT: Set background color to light yellow
                    // WHY: Yellow background indicates item has buyer interest but not sold yet
                    // HOW: Color(255, 248, 225) creates light yellow background
                    setBackground(new Color(255, 248, 225)); // Light yellow
                    
                    // WHAT: Set text color to dark orange
                    // WHY: Dark orange text contrasts with light yellow background
                    // HOW: Color(245, 124, 0) creates dark orange text color
                    setForeground(new Color(245, 124, 0)); // Dark orange
                    
                    // WHAT: Prepend star emoji to status text
                    // WHY: Star emoji indicates interest/attention
                    // HOW: String concatenation adds emoji before status text
                    setText(status);
                    break; // Exit switch statement
                
                // WHAT: Handle "Available" status
                // WHY: Available items should be visually distinct (green indicates availability)
                // HOW: case statement matches "Available" string
                case "Available":
                    // WHAT: Set background color to light green
                    // WHY: Green background indicates item is available for purchase
                    // HOW: Color(232, 245, 233) creates light green background
                    setBackground(new Color(232, 245, 233)); // Light green
                    
                    // WHAT: Set text color to dark green
                    // WHY: Dark green text contrasts with light green background
                    // HOW: Color(27, 94, 32) creates dark green text color
                    setForeground(new Color(27, 94, 32)); // Dark green
                    
                    // WHAT: Prepend checkmark emoji to status text
                    // WHY: Checkmark emoji indicates availability/ready status
                    // HOW: String concatenation adds emoji before status text
                    setText(status);
                    break; // Exit switch statement
                
                // WHAT: Handle any other status values (default case)
                // WHY: Unknown statuses should still display but with default styling
                // HOW: default case handles all unmatched status values
                default:
                    // WHAT: Set background based on selection state
                    // WHY: Selected cells should have blue background, unselected should be white
                    // HOW: Ternary operator checks isSelected, applies blue if selected, white otherwise
                    setBackground(isSelected ? new Color(33, 150, 243) : Color.WHITE);
                    
                    // WHAT: Set text color based on selection state
                    // WHY: White text on blue background when selected, black on white when not
                    // HOW: Ternary operator checks isSelected, applies white if selected, black otherwise
                    setForeground(isSelected ? Color.WHITE : Color.BLACK);
                    break; // Exit switch statement
            }
        } else {
            // WHAT: Handle null value case
            // WHY: Null values need default styling to prevent errors
            // HOW: else block handles when value is null
            // WHAT: Set background based on selection state (null value)
            // WHY: Null cells should still respect selection highlighting
            // HOW: Ternary operator applies blue if selected, white otherwise
            setBackground(isSelected ? new Color(33, 150, 243) : Color.WHITE);
            
            // WHAT: Set text color based on selection state (null value)
            // WHY: Null cells should still have readable text color
            // HOW: Ternary operator applies white if selected, black otherwise
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
        }
        
        // WHAT: Center-align the status text horizontally
        // WHY: Centered text looks more professional in table cells
        // HOW: SwingConstants.CENTER aligns text in center of cell
        setHorizontalAlignment(SwingConstants.CENTER);
        
        // WHAT: Add padding around cell content (5px top/bottom, 10px left/right)
        // WHY: Padding prevents text from touching cell edges, improves readability
        // HOW: createEmptyBorder() creates border with specified padding, setBorder() applies it
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // WHAT: Return this component (configured renderer)
        // WHY: JTable needs the component to display in the cell
        // HOW: return statement returns the configured JLabel (this class extends DefaultTableCellRenderer which extends JLabel)
        return this;
    }
}
