package windows.games.uma;

import processing.core.PApplet;
import processing.core.PImage;
import javax.swing.ImageIcon;
import java.awt.Image;

public class MySketch1 extends PApplet {

    // Game state controller: 0 = Boot Animation (GIF), 1 = Start Menu, 2 = Main Game Screen
    private int gameState = 0; 

    // Images variables
    private PImage mainMenuBgImg;    // null_2.jpg
    private PImage umastartBtnImg;   // umastart.png
    
    // Java Swing components to handle the animated GIF rendering smoothly inside Processing
    private ImageIcon gifIcon;
    private Image awtGifImage;
    private PImage processingGifFrame;
    
    // Timer threshold set to 60 frames (exactly 1 second at 60fps frame rate)
    private int animationTimer = 0;

    // References to your game characters
    private Person runner;

    @Override
    public void settings() {
        // FIXED: Updated window dimensions to 650x650 pixels to perfectly fit the display screen
        size(650, 650); 
    }

    @Override
    public void setup() {
        // --- 1. LOAD THE ANIMATED GIF FOR BOOT ANIMATION ---
        try {
            java.net.URL gifURL = getClass().getResource("/windows/resources/ezgif-432fd0c62e63f85f.gif");
            if (gifURL != null) {
                gifIcon = new ImageIcon(gifURL);
                awtGifImage = gifIcon.getImage();
                processingGifFrame = new PImage(awtGifImage.getWidth(null), awtGifImage.getHeight(null), ARGB);
            } else {
                println("Error: Cannot find ezgif-432fd0c62e63f85f.gif in resources package.");
            }
        } catch (Exception e) {
            println("Error loading GIF boot animation: " + e.getMessage());
        }

        // --- 2. LOAD STATIC UI IMAGES ---
        try {
            mainMenuBgImg = loadImage(getClass().getResource("/windows/resources/null_2.jpg").getPath());
            umastartBtnImg = loadImage(getClass().getResource("/windows/resources/umastart.png").getPath());
        } catch (Exception e) {
            println("Note: Image path redirection system handling assets load.");
        }

        // Initialize your runner character (X=100, Y=320, Name, Age)
        runner = new Person(this, 100, 320, "Special Week", 3);
    }

    @Override
    public void draw() {
        background(0); // Clear screen to black every frame

        switch (gameState) {
            case 0:
                // --- STATE 0: 1-SECOND BOOT ANIMATION (GIF) ---
                if (awtGifImage != null && processingGifFrame != null) {
                    java.awt.image.BufferedImage bimg = new java.awt.image.BufferedImage(
                        processingGifFrame.width, processingGifFrame.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    java.awt.Graphics2D g2d = bimg.createGraphics();
                    g2d.drawImage(awtGifImage, 0, 0, null);
                    g2d.dispose();
                    bimg.getRGB(0, 0, processingGifFrame.width, processingGifFrame.height, processingGifFrame.pixels, 0, processingGifFrame.width);
                    processingGifFrame.updatePixels();
                    
                    // Stretches to completely cover the new 650x650 canvas boundaries seamlessly
                    image(processingGifFrame, 0, 0, width, height); 
                }
                
                // Track 60 frames execution limit (1 second total playback duration)
                animationTimer++;
                if (animationTimer >= 60) {
                    gameState = 1; // Automatically switch to the main menu screen
                }
                break;

            case 1:
                // --- STATE 1: START MENU / MAIN BACKGROUND (null_2.jpg) ---
                if (mainMenuBgImg != null) {
                    // Stretches to the exact same 650x650 dimensions for a seamless transition
                    image(mainMenuBgImg, 0, 0, width, height);
                } else {
                    background(30, 30, 45); 
                }

                // Draw the button restricted to 120x50 pixels at the bottom-left corner
                // 'height - 80' dynamically evaluates to 650 - 80 = 570
                if (umastartBtnImg != null) {
                    image(umastartBtnImg, 50, height - 80, 120, 50);
                }
                break;

            case 2:
                // --- STATE 2: MAIN GAME INTERFACE ---
                background(50, 120, 50); 
                
                // Draw simple race track lane lines adapted for 650x650 screen dimensions
                stroke(255);
                strokeWeight(3);
                line(0, height / 2 - 50, width, height / 2 - 50);
                line(0, height / 2 + 50, width, height / 2 + 50);

                // Move and draw the runner automatically
                runner.move(1, 0); 
                runner.draw();
                break;
        }
    }

    @Override
    public void mousePressed() {
        // Handle button clicks specifically on the Main Menu screen (State 1)
        if (gameState == 1) {
            int btnX = 50;
            int btnY = height - 80; // Dynamically adjusts to the new 570 coordinate boundary
            int btnWidth = 120;
            int btnHeight = 50;
            
            // Checking if mouse coordinates land inside the bottom-left boundary box
            if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight) {
                println("Start Button clicked! Entering the core game track.");
                gameState = 2; // Jump into the main character movement loop
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("windows.games.uma.MySketch1");
    }
}