package util; // Package declaration: Groups this class with other utility classes

import java.awt.Color; // Import: Color class for theme colors

/**
 * ThemeColors - Application Theme Color Constants
 * WHAT: Centralized color definitions for consistent theming across the application
 * WHY: Ensures all GUI components use the same color scheme matching the logo (lime green and black)
 * HOW: Static final Color constants that can be imported and used throughout the application
 * 
 * THEME: Minimal and professional design with lime green (#8BC34A) and black color scheme
 */
public class ThemeColors {
    // WHAT: Primary brand color - lime green matching the logo
    // WHY: Creates visual consistency with the application logo
    // HOW: Color(139, 195, 74) creates lime green (RGB values for #8BC34A)
    public static final Color PRIMARY = new Color(139, 195, 74); // Lime green #8BC34A
    
    // WHAT: Darker shade of primary for hover states and accents
    // WHY: Provides visual feedback and depth
    // HOW: Color(104, 159, 56) creates darker green (RGB values for #689F38)
    public static final Color PRIMARY_DARK = new Color(104, 159, 56); // Darker green #689F38
    
    // WHAT: Lighter shade of primary for backgrounds and highlights
    // WHY: Subtle accent without overwhelming
    // HOW: Color(200, 230, 201) creates light green (RGB values for #C8E6C9)
    public static final Color PRIMARY_LIGHT = new Color(200, 230, 201); // Light green #C8E6C9
    
    // WHAT: Background color - pure white for clean minimal look
    // WHY: White provides clean, professional appearance
    // HOW: Color.WHITE is standard white
    public static final Color BACKGROUND = Color.WHITE;
    
    // WHAT: Secondary background - very light gray for panels
    // WHY: Subtle contrast for nested panels
    // HOW: Color(250, 250, 250) creates off-white gray
    public static final Color BACKGROUND_SECONDARY = new Color(250, 250, 250);
    
    // WHAT: Primary text color - dark gray/black
    // WHY: High contrast for readability
    // HOW: Color(33, 33, 33) creates dark gray (almost black)
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33); // Dark gray/black
    
    // WHAT: Secondary text color - medium gray
    // WHY: For less important text and labels
    // HOW: Color(100, 100, 100) creates medium gray
    public static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    
    // WHAT: Tertiary text color - light gray
    // WHY: For hints and disabled text
    // HOW: Color(150, 150, 150) creates light gray
    public static final Color TEXT_TERTIARY = new Color(150, 150, 150);
    
    // WHAT: Border color - light gray for subtle borders
    // WHY: Minimal borders that don't distract
    // HOW: Color(220, 220, 220) creates light gray
    public static final Color BORDER = new Color(220, 220, 220);
    
    // WHAT: Error color - red for error messages
    // WHY: Standard error indication
    // HOW: Color(244, 67, 54) creates red
    public static final Color ERROR = new Color(244, 67, 54); // Red
    
    // WHAT: Success color - darker green for success messages
    // WHY: Positive feedback color
    // HOW: Color(76, 175, 80) creates green
    public static final Color SUCCESS = new Color(76, 175, 80); // Green
    
    // WHAT: Warning color - orange for warnings
    // WHY: Standard warning indication
    // HOW: Color(255, 152, 0) creates orange
    public static final Color WARNING = new Color(255, 152, 0); // Orange
    
    // WHAT: Info color - blue for informational messages
    // WHY: Standard info indication
    // HOW: Color(33, 150, 243) creates blue
    public static final Color INFO = new Color(33, 150, 243); // Blue
    
    // WHAT: Table header background - primary color
    // WHY: Consistent with theme
    // HOW: Uses PRIMARY color constant
    public static final Color TABLE_HEADER_BG = PRIMARY;
    
    // WHAT: Table header text - white for contrast
    // WHY: White text on green background
    // HOW: Color.WHITE for contrast
    public static final Color TABLE_HEADER_TEXT = Color.WHITE;
    
    // WHAT: Sidebar background - very dark gray (almost black)
    // WHY: Creates contrast with main content
    // HOW: Color(30, 30, 30) creates dark gray
    public static final Color SIDEBAR_BG = new Color(30, 30, 30); // Dark gray/black
    
    // WHAT: Sidebar text - white for contrast
    // WHY: White text on dark background
    // HOW: Color.WHITE for contrast
    public static final Color SIDEBAR_TEXT = Color.WHITE;
    
    // WHAT: Sidebar button hover - primary color
    // WHY: Interactive feedback
    // HOW: Uses PRIMARY color constant
    public static final Color SIDEBAR_BUTTON_HOVER = PRIMARY;
    
    // WHAT: Header background color - dark blue for better logo visibility
    // WHY: Dark blue provides better contrast for logo visibility
    // HOW: Color(36, 60, 102) creates dark blue (RGB values for #243C66)
    public static final Color HEADER_BG = new Color(36, 60, 102); // Dark blue #243C66
    
    // WHAT: Header text color - white for contrast on dark blue
    // WHY: White text contrasts well with dark blue background
    // HOW: Color.WHITE for maximum contrast
    public static final Color HEADER_TEXT = Color.WHITE;
    
    // WHAT: Header subtitle color - light gray-blue for subtle contrast
    // WHY: Subtle contrast for subtitle text on dark blue
    // HOW: Color(200, 200, 220) creates light gray-blue
    public static final Color HEADER_SUBTITLE = new Color(200, 200, 220); // Light gray-blue
}




