package windows;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.net.URL;

public class DesktopPanel extends JPanel {
    
    public DesktopPanel() {
        // Use Absolute Layout (null) so we can place game icons freely later
        this.setLayout(null); 
    }

    // This method automatically draws the background when the panel is displayed
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            // Load the Windows 11 wallpaper image from the resources package
            URL imgURL = getClass().getResource("/windows/resources/windows11 t.jpg");
            if (imgURL != null) {
                ImageIcon wallpaper = new ImageIcon(imgURL);
                // Resize and draw the image to cover 100% of the panel width and height
                g.drawImage(wallpaper.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                System.out.println("Error: Cannot find windows11 t.jpg in windows.resources package.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}