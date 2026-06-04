package windows.games.uma;

import processing.core.PApplet;

public class Person {
    private int x;
    private int y;
    private String name;
    private int age;
    private PApplet app;

    public Person(PApplet p, int x, int y, String name, int age) {
        this.app = p;
        this.x = x;
        this.y = y;
        this.name = name;
        this.age = age;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        // Keep inside window bounds wrapping around
        if (this.x > app.width) {
            this.x = 0;
        }
        this.y += dy;
    }

    public void draw() {
        app.fill(255, 100, 100);
        app.ellipse(x, y, 40, 40);
        
        app.fill(255);
        app.textSize(14);
        app.text(name, x - 30, y - 25);
    }
}