package util; // Package declaration: Groups this class with other utility classes

import java.awt.*; // Import: AWT classes for graphics and image creation
import java.awt.image.BufferedImage; // Import: BufferedImage for creating icons
import java.io.File; // Import: File for path operations
import javax.swing.ImageIcon; // Import: ImageIcon for Swing icon support

/**
 * IconUtil - Icon Management Utility
 * WHAT: Provides methods to create and load application icons for GUI windows
 * WHY: Centralizes icon creation/loading, provides fallback if image files don't exist
 * HOW: Creates programmatic icons or loads from image files, returns ImageIcon objects
 */
public class IconUtil {
    // WHAT: Default icon size for application windows
    // WHY: Standard icon size for Windows taskbar and title bar
    // HOW: 32x32 pixels is standard for window icons
    private static final int ICON_SIZE = 32;
    
    /**
     * getApplicationIcon() - Creates or loads application icon
     * WHAT: Returns an ImageIcon for the application, creating programmatically if file doesn't exist
     * WHY: All windows should have a consistent icon
     * HOW: Tries to load from file, falls back to programmatic creation
     * @return ImageIcon object for application windows
     */
    public static ImageIcon getApplicationIcon() {
        // WHAT: Try to load icon from file if it exists
        // WHY: Prefer external image file if available
        // HOW: Check if file exists, load if found
        try {
            // WHAT: Try multiple possible paths for the icon file
            // WHY: Icon file might be in different locations depending on how app is run
            // HOW: Check relative path first, then try absolute path
            String userDir = System.getProperty("user.dir");
            String[] possiblePaths = {
                "images/agritrack_icon.png",
                "./images/agritrack_icon.png",
                userDir + "/images/agritrack_icon.png",
                userDir + "\\images\\agritrack_icon.png",
                userDir + File.separator + "images" + File.separator + "agritrack_icon.png"
            };
            
            for (String path : possiblePaths) {
                File iconFile = new File(path);
                if (iconFile.exists() && iconFile.isFile()) {
                    ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                    // WHAT: Verify icon was loaded successfully
                    // WHY: ImageIcon might be created but image not loaded
                    // HOW: Check if icon has valid image
                    if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                        // WHAT: Scale icon to standard size
                        // WHY: Ensure consistent icon size
                        // HOW: getScaledInstance() resizes image
                        Image scaledImage = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            }
        } catch (Exception e) {
            // File doesn't exist or can't be loaded, continue to programmatic creation
            System.out.println("Icon file not found, using programmatic icon: " + e.getMessage());
        }
        
        // WHAT: Create programmatic icon as fallback
        // WHY: Application should have icon even without image file
        // HOW: Create BufferedImage and draw icon graphics
        return createProgrammaticIcon();
    }
    
    /**
     * getApplicationImage() - Gets Image object for setIconImage()
     * WHAT: Returns Image object suitable for JFrame.setIconImage()
     * WHY: JFrame.setIconImage() requires Image, not ImageIcon
     * HOW: Gets ImageIcon and extracts Image from it
     * @return Image object for window icon
     */
    public static Image getApplicationImage() {
        // WHAT: Get ImageIcon and extract Image
        // WHY: setIconImage() needs Image object
        // HOW: getImage() method extracts Image from ImageIcon
        return getApplicationIcon().getImage();
    }
    
    /**
     * createProgrammaticIcon() - Creates icon programmatically
     * WHAT: Creates a simple agricultural-themed icon using Java Graphics
     * WHY: Provides icon when image file is not available
     * HOW: Draws icon on BufferedImage with agricultural symbols
     * @return ImageIcon with programmatic icon
     */
    private static ImageIcon createProgrammaticIcon() {
        // WHAT: Create buffered image for icon
        // WHY: Need canvas to draw icon
        // HOW: BufferedImage with RGB color model
        BufferedImage icon = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = icon.createGraphics();
        
        // WHAT: Enable anti-aliasing for smooth graphics
        // WHY: Better visual quality
        // HOW: setRenderingHint() enables anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // WHAT: Fill background with green (agricultural theme)
        // WHY: Green represents agriculture/nature
        // HOW: setColor() and fillRect() fill background
        g.setColor(new Color(46, 125, 50)); // Dark green
        g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
        
        // WHAT: Draw wheat/plant symbol in center
        // WHY: Agricultural symbol for the application
        // HOW: Draw lines and shapes to create plant symbol
        g.setColor(new Color(255, 215, 0)); // Gold/yellow for wheat
        g.setStroke(new BasicStroke(2.0f));
        
        // Draw wheat stalk
        g.setColor(new Color(139, 195, 74)); // Light green for stalk
        g.fillRect(ICON_SIZE / 2 - 2, ICON_SIZE / 2, 4, ICON_SIZE / 2 - 4);
        
        // Draw wheat grains (circles)
        g.setColor(new Color(255, 215, 0)); // Gold for grains
        int centerX = ICON_SIZE / 2;
        int centerY = ICON_SIZE / 2 - 4;
        g.fillOval(centerX - 6, centerY - 6, 4, 4);
        g.fillOval(centerX + 2, centerY - 6, 4, 4);
        g.fillOval(centerX - 2, centerY - 10, 4, 4);
        
        // Draw leaves
        g.setColor(new Color(76, 175, 80)); // Green for leaves
        g.fillOval(centerX - 8, centerY + 2, 6, 8);
        g.fillOval(centerX + 2, centerY + 2, 6, 8);
        
        // WHAT: Dispose graphics context
        // WHY: Free resources
        // HOW: dispose() releases graphics resources
        g.dispose();
        
        // WHAT: Return ImageIcon with created icon
        // WHY: Return format needed for Swing
        // HOW: new ImageIcon() with BufferedImage
        return new ImageIcon(icon);
    }
    
    /**
     * getSmallIcon() - Gets smaller icon for buttons or labels
     * WHAT: Returns a smaller version of the application icon (16x16)
     * WHY: Buttons and labels need smaller icons
     * HOW: Scales application icon to smaller size
     * @return ImageIcon with 16x16 icon
     */
    public static ImageIcon getSmallIcon() {
        // WHAT: Get application icon and scale to 16x16
        // WHY: Smaller size for buttons/labels
        // HOW: getScaledInstance() resizes image
        ImageIcon icon = getApplicationIcon();
        Image scaledImage = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}






