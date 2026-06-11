package windows.games.uma;

import processing.core.PApplet;
import processing.core.PImage;

public class Person {
    private PApplet parent;
    public float x, y;
    public String name; 
    
    // 渲染与大小参数
    public int pWidth = 120;  
    public int pHeight = 120; 
    
    // 一秒两帧动画控制变量
    private PImage[] runFrames; 
    private int currentFrame = 0;
    private int animationTimer = 0;
    private int animationSpeed = 30; // 60fps下，每30帧换一张图正好是一秒两帧

    // 核心竞速参数
    public int baseSpeed = 6;
    public float speedMultiplier; 

    public Person(PApplet parent, float x, float y, String name, String filePrefix) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.name = name;
        this.speedMultiplier = parent.random(1.1f, 1.5f);
        
        // 动态加载 4 张独立动作图片
        runFrames = new PImage[4];
        for (int i = 0; i < 4; i++) {
            String path = "windows/resources/" + filePrefix + "动作" + (i + 1) + ".png";
            try {
                // 使用父级的 createInput 流加载，完美解决 NetBeans 工作路径和中文编码问题
                runFrames[i] = parent.loadImage(parent.createInput(path));
            } catch (Exception e) {
                parent.println("无法加载精灵图帧，请确认路径: " + path);
            }
        }
    }

    public void draw() {
        // 驱动一秒两帧动画时钟
        animationTimer++;
        if (animationTimer >= animationSpeed) {
            currentFrame = (currentFrame + 1) % 4;
            animationTimer = 0;
        }

        if (runFrames[currentFrame] != null) {
            parent.image(runFrames[currentFrame], x, y, pWidth, pHeight);
        } else {
            // 资源失效时的纯色图形降级保底
            if(name.equals("Special Week")) parent.fill(138, 43, 226);
            else if(name.equals("Grass Wonder")) parent.fill(30, 144, 255);
            else parent.fill(46, 139, 87);
            parent.rect(x, y, pWidth, pHeight);
        }
    }

    // 玩家控制相对位移逻辑（带防撞墙机制）
    public void move(float dx, float dy) {
        this.x += dx * baseSpeed;
        this.y += dy;
        
        // 限制在屏幕左半侧可视区域
        if (this.x < 30) this.x = 30; 
        if (this.x > parent.width / 2 - 40) this.x = parent.width / 2 - 40; 
        if (this.y < 300) this.y = 300;
        if (this.y > parent.height - pHeight - 20) this.y = parent.height - pHeight - 20;
    }
    
    // AI 自动拉锯错位逻辑（带防撞墙机制）
    public void autoMove() {
        float roll = parent.random(0, 100);
        if (roll < 3) { 
            this.x += parent.random(8, 20) * speedMultiplier; 
        } else if (roll > 97) { 
            this.x -= parent.random(8, 18) * speedMultiplier; 
        } else {
            this.x += parent.random(-1.5f, 1.5f); 
        }
        
        // 小概率进行位移拉锯
        if (parent.random(0, 100) < 5) {
            this.y += (parent.random(0, 10) > 5) ? 15 : -15;
        }
        
        // 限制在屏幕左半侧可视区域
        if (this.x < 30) this.x = 30;
        if (this.x > parent.width / 2 - 40) this.x = parent.width / 2 - 40;
        if (this.y < 300) this.y = 300;
        if (this.y > parent.height - pHeight - 20) this.y = parent.height - pHeight - 20;
    }

    public void resetPosition(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }
    
    public void randomizeSpeed() {
        this.speedMultiplier = parent.random(1.1f, 1.6f);
    }
}