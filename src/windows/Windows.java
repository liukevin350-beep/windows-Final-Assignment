package windows;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Windows extends javax.swing.JFrame {

    private int mouseX;
    private int mouseY;

    public Windows() {
        initComponents(); 
        
        DesktopPanel desktop = new DesktopPanel();
        this.setContentPane(desktop);
        
        // --- 1. CLICKABLE GAME SHORTCUT ICON (RESIZED TO 64x64) ---
        try {
            URL iconURL = getClass().getResource("/windows/resources/umagame.jpg");
            if (iconURL != null) {
                ImageIcon originalIcon = new ImageIcon(iconURL);
                
                // Scale the image smoothly to 64x64 pixels
                java.awt.Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
                ImageIcon gameIcon = new ImageIcon(scaledImage);
                
                JLabel iconLabel = new JLabel(gameIcon);
                iconLabel.setBounds(30, 30, 64, 64);
                
                // Add mouse listener to handle clicking into the game
                iconLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Shortcut clicked! Launching game...");
                        
                        // LAUNCH THE GAME FROM THE NEW PACKAGE: windows.games.uma
                        processing.core.PApplet.main("windows.games.uma.MySketch1"); 
                    }
                });
                
                desktop.add(iconLabel);
            } else {
                System.out.println("Error: Cannot find umagame.jpg in resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // --- 2. DESKTOP PET WITH MOUSE DRAG FEATURE ---
        try {
            URL petURL = getClass().getResource("/windows/resources/mambo-matikanetannhauser.gif");
            if (petURL != null) {
                ImageIcon petIcon = new ImageIcon(petURL);
                JLabel petLabel = new JLabel(petIcon);
                
                petLabel.setBounds(150, 400, petIcon.getIconWidth(), petIcon.getIconHeight());
                
                petLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        mouseX = e.getX();
                        mouseY = e.getY();
                    }
                });

                petLabel.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int x = petLabel.getX() + e.getX() - mouseX;
                        int y = petLabel.getY() + e.getY() - mouseY;
                        petLabel.setLocation(x, y);
                    }
                });
                
                desktop.add(petLabel);
            } else {
                System.out.println("Error: Cannot find mambo-matikanetannhauser.gif in resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Window settings
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        // Audio execution sequence
        AudioManager.playSound("/windows/resources/openwindows.wav");
        
        Timer bgmTimer = new Timer(3500, e -> {
            AudioManager.playSound("/windows/resources/Matikanetannhausergifmu.wav");
        });
        bgmTimer.setRepeats(false);
        bgmTimer.start();
        
        startClock();
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            if (lblClock != null) {
                lblClock.setText(now.format(formatter)); 
            }
        });
        timer.start(); 
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Retro Windows OS");
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );

        pack();
    }
    // </editor-fold>                        

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Windows().setVisible(true); 
        });
    }

    private javax.swing.JLabel lblClock;
}