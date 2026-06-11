package windows.games.uma;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

public class MySketch1 extends PApplet {

    // 0 = 开场AVG剧情, 2 = 核心赛跑关卡, 3 = 结局AVG剧情
    private int gameState = 0; 

    // --- 文本AVG叙事底层变量 ---
    private String[] storyTexts;
    private String[] storySpeakers;
    private int storyIndex = 0;
    
    // --- 视觉资源映射 ---
    private PImage officeBgImg;      // 对应：对话角色.jpg
    private PImage mapNormal;        // 对应：map.png
    private PImage mapFinish;        // 对应：map2.png
    private PFont myFont;

    // --- 赛马实体引擎参数 ---
    private Person playerRunner;       
    private Person npcRunner1;       
    private Person npcRunner2;       
    
    private float bgX = 0;            
    private float scrollSpeed = 9;    
    private int bgLoopCount = 1;      
    private final int MAX_LOOPS = 10; // 跑10圈背景
    private float finishLineX = -1;   
    
    private boolean isPlayerWin = false;
    private int playerSpeedBonus = 0;

    @Override
    public void settings() {
        size(650, 650); 
    }

    @Override
    public void setup() {
        // 载入支持中文环境的字体，杜绝乱码
        myFont = createFont("Microsoft YaHei", 16);
        textFont(myFont);

        // 使用稳定的流加载方式，防止乱码崩溃
        try {
            officeBgImg    = loadImage(createInput("windows/resources/对话角色.jpg"));
            mapNormal      = loadImage(createInput("windows/resources/map.png"));
            mapFinish      = loadImage(createInput("windows/resources/map2.png"));
        } catch (Exception e) {
            println("核心UI或地图资源加载失败，请检查 windows/resources 文件夹。");
        }

        // 装载前置关卡剧本数据
        initOpeningStory();
        
        // 生成人物实例
        resetEntireMatch();
    }

    public void resetEntireMatch() {
        bgX = 0;
        bgLoopCount = 1;
        finishLineX = -1;
        
        // 参数配置：(Sketch, 初始X, 初始Y, 姓名, 图片名前缀)
        playerRunner = new Person(this, 150, 320, "Special Week", "特别周");
        playerRunner.baseSpeed = 6 + playerSpeedBonus;
        
        npcRunner1   = new Person(this, 120, 410, "Grass Wonder", "小草");
        npcRunner2   = new Person(this, 90,  500, "Silence Suzuka", "铃鹿");
    }

    @Override
    public void draw() {
        background(0); 

        switch (gameState) {
            case 0: // ====== 状态 0: 开场 AVG 剧情 ======
                drawStoryScene();
                break;

            case 2: // ====== 状态 2: 核心赛跑核心关卡 (剧情播完直接无缝切入) ======
                boolean isFinalSpurt = (bgLoopCount >= MAX_LOOPS);
                PImage activeMap = isFinalSpurt ? mapFinish : mapNormal;

                if (activeMap != null) {
                    float renderWidth = ((float)activeMap.width / activeMap.height) * height;
                    bgX -= scrollSpeed; // 背景循环滚动

                    if (bgLoopCount < MAX_LOOPS) {
                        // 1至9圈使用 map.png 无缝无限循环
                        if (bgX <= -renderWidth + width) {
                            bgX = 0;
                            bgLoopCount++;
                        }
                        image(activeMap, bgX, 0, renderWidth, height);
                        image(activeMap, bgX + renderWidth, 0, renderWidth, height);
                    } else {
                        // 第10圈加载含有白线终点的 map2.png，到头后卡住
                        if (bgX < -renderWidth + width) {
                            bgX = -renderWidth + width; 
                        }
                        image(activeMap, bgX, 0, renderWidth, height);
                        
                        // 定位终点白线在图像右侧前 250 像素的位置
                        finishLineX = bgX + renderWidth - 250;
                    }
                }

                // 检查是否到了白线冲刺阶段
                if (bgLoopCount >= MAX_LOOPS && finishLineX < width) {
                    // 解除防卡死墙束缚：全速冲刺！
                    if (keyPressed && keyCode == RIGHT) playerRunner.x += playerRunner.baseSpeed;
                    if (keyPressed && keyCode == LEFT)  playerRunner.x -= 3;
                    npcRunner1.x += (int)random(4, 9);
                    npcRunner2.x += (int)random(4, 9);
                    
                    checkRaceEnd(); // 判定谁越过了白线
                } else {
                    // 平常拉锯位移阶段
                    if (keyPressed) {
                        if (keyCode == LEFT)  playerRunner.move(-1, 0);
                        if (keyCode == RIGHT) playerRunner.move(1, 0);
                        if (keyCode == UP)    playerRunner.move(0, -5);
                        if (keyCode == DOWN)  playerRunner.move(0, 5);
                    }
                    npcRunner1.autoMove();
                    npcRunner2.autoMove();
                }

                // 一秒两帧绘制马娘
                playerRunner.draw();
                npcRunner1.draw();
                npcRunner2.draw();
                
                // 打印HUD头部看板
                drawHUD();
                break;

            case 3: // ====== 状态 3: 双结局剧情结算 ======
                drawStoryScene();
                break;
        }
    }

    void drawStoryScene() {
        // 学生会场景图判断
        if (storySpeakers[storyIndex].equals("鲁道夫象征") && officeBgImg != null) {
            image(officeBgImg, 0, 0, width, height);
        } else {
            background(25, 25, 35);
        }

        // 绘制对话底框
        fill(0, 0, 0, 210);
        stroke(120); strokeWeight(2);
        rect(40, height - 210, width - 80, 170, 8);
        noStroke();

        // 讲话者名字
        fill(255, 215, 0); textAlign(LEFT); textSize(18);
        text("【" + storySpeakers[storyIndex] + "】", 60, height - 175);

        // 剧情文本
        fill(240); textSize(15);
        text(storyTexts[storyIndex], 60, height - 140, width - 120, 100);

        fill(160); textAlign(RIGHT); textSize(12);
        text("▼ 点击鼠标继续...", width - 60, height - 55);
    }

    void drawHUD() {
        fill(0, 0, 0, 160); rect(15, 15, 330, 85, 8);
        fill(255); textAlign(LEFT); textSize(14);
        text("当前赛程进度: 循环跑道第 " + bgLoopCount + " / " + MAX_LOOPS + " 圈", 25, 40);
        if (bgLoopCount >= MAX_LOOPS) {
            fill(255, 70, 70); text("【终点白线已切入！快按[→]全速冲刺！！】", 25, 70);
        } else {
            fill(120, 255, 120); text("特周控制位: " + (int)playerRunner.x + " (使用方向键盘控位)", 25, 70);
        }
    }

    void checkRaceEnd() {
        boolean pFinish = (playerRunner.x + 80) >= finishLineX;
        boolean gFinish = (npcRunner1.x + 80) >= finishLineX;
        boolean sFinish = (npcRunner2.x + 80) >= finishLineX;

        if (pFinish || gFinish || sFinish) {
            if (pFinish && (playerRunner.x >= npcRunner1.x) && (playerRunner.x >= npcRunner2.x)) {
                isPlayerWin = true;
                initEndingStory(true);
            } else {
                isPlayerWin = false;
                playerSpeedBonus += 2; // 输了获得下次的速度保底加成
                initEndingStory(false);
            }
            gameState = 3; // 切入大结局剧情文本
        }
    }

    @Override
    public void mousePressed() {
        if (gameState == 0) {
            // 如果还没到最后一句，点击就看下一句
            if (storyIndex < storyTexts.length - 1) {
                storyIndex++;
            } else {
                // ====== 核心修改：当玩家看完第 20 句话并点击时，瞬间直接开始比赛！ ======
                resetEntireMatch(); 
                gameState = 2; 
            }
        } 
        else if (gameState == 3) {
            if (storyIndex < storyTexts.length - 1) {
                storyIndex++;
            } else {
                // 结局看完了，重置剧本并回到最初重新游戏
                storyIndex = 0;
                initOpeningStory();
                gameState = 0; 
            }
        }
    }

    void initOpeningStory() {
        storyIndex = 0;
        storySpeakers = new String[]{
            "系统提示", "训练员(玩家)", "系统提示", "训练员(玩家)",
            "鲁道夫象征", "训练员(玩家)", "鲁道夫象征", "鲁道夫象征",
            "系统提示", "训练员(玩家)", "神秘西装男子", "训练员(玩家)", "神秘西装男子", "神秘西装男子",
            "训练员(玩家)", "鲁道夫象征", "鲁道夫象征", "训练员(玩家)", "鲁道夫象征", "鲁道夫象征"
        };
        storyTexts = new String[]{
            "你通过了极其严格的笔试，今天正式成为中央特雷森学园的一员。前方正在进行新人选拔赛。",
            "这就是特雷森吗？大家都跑得好快……等等，那个在队伍最后方、眼神却死死盯着终点线的孩子是……？",
            "在沙尘的边缘，一个并不显眼的少女正在默默调整呼吸。虽然目前的排名很靠后，但你注意到了她腿部肌肉惊人的爆发力，以及眼中从未熄灭的对胜利的执念。",
            "就是她了！我要成为她的指引者。",
            "哦？刚入职就提交了契约申请吗，新来的训练员。让我看看你挑选的孩子……居然是她。",
            "会长，她的潜力很大，只是目前的训练方式不适合她，我相信她能站上闪耀系列赛的顶点！",
            "（看着报告，眼神深邃，沉默了片刻）……这个孩子的血统很特别，但也背负着极大的压力。训练员，你挑了一条最难走的路。",
            "（在合同上签下名字，递给你）既然你执意如此，那就好好照顾她吧。不要让她眼里的光芒熄灭，特雷森会作为你们的后盾。",
            "经过数周的特训，在刚刚结束的出道战中，那个孩子在最后弯道一跃而出，斩获了首胜！",
            "太棒了！我们做到了！",
            "（突然从走廊的阴影里走出，拍了拍手）精彩的比赛。初任训练员就能有这种成果，确实值得称赞。",
            "请问你是……？",
            "我们是社台（Shadai）集团。这个孩子的血统属于我们最新的繁衍计划。把她交给我们管理吧，我们会用最精密的资本和最残酷的淘汰机制来‘压榨’她的极限。",
            "跟着你这种新人只会浪费她的基因。开个价吧，如果你拒绝，我们会动用董事会的权力强行干预。",
            "会长！社台的人居然在出道战后威胁我，想要强行带走她！学园难道不管吗？！",
            "……他们终于还是来了。训练员，冷静点。社台垄断了赛马界半壁江山，连特雷森的资金都由他们提供。",
            "三女神立下了‘日蚀第一，其余无足轻重’的铁血枷锁。在社台眼里，马娘不是生命，而是商品。如果无法持续带来最高荣誉，就会被剥夺名字，沦为血统繁衍的工具。",
            "那我们要怎么做才能保护她？！",
            "只有一个办法——无败三冠（Undefeated Triple Crown）。",
            "在这个世界里，战绩就是一切。只要你带她以全胜的姿态拿下皋月赏、日本德比和菊花赏，成为绝对的传说。强如社台，也无法在聚光灯下对一个活着的传奇动手。去证明你们的价值吧，我会替你挡下高层所有的政治压力。"
        };
    }

    void initEndingStory(boolean win) {
        storyIndex = 0;
        if (win) {
            storySpeakers = new String[]{"系统提示", "神秘西装男子", "鲁道夫象征", "马娘(特别周)", "系统提示"};
            storyTexts = new String[]{
                "随着最后一场G1比赛的冲线，解说员的声音响彻全场：‘奇迹诞生了！无败三冠！！她打破了血统的诅咒！’",
                "（在贵宾席面色铁青，默默撕掉了回收合同）……哼，竟然真的做到了这种地步。这个商品，已经超越了我们的控制范围。",
                "（在看台上露出了久违的欣慰笑容）干得漂亮，新来的。你们用绝对的力量，为自己赢得了真正的自由。",
                "（扑进你的怀里，眼里闪烁着泪光）训练员！谢谢你……如果没有你，我可能早就迷失在黑暗里了。现在，我们可以永远纯粹地跑下去了！",
                "你们向着阳光的方向，彻底摆脱了枷锁，迎来了只属于你们的、充满希望的未来。【GOOD END - 向光而生】"
            };
        } else {
            storySpeakers = new String[]{"系统提示", "鲁道夫象征", "系统提示", "马娘(特别周)", "神秘西装男子", "系统提示"};
            storyTexts = new String[]{
                "由于关键战役的失利，那个孩子没能完成“无败”的试炼。社台集团的董事会强制启动了收回程序。",
                "（坐在办公桌前，深深地埋下头）……抱歉，训练员。我已经尽力帮你压制高层了。但在冷酷的体制面前，失败者是没有话语权的。",
                "在学园的大门口，一辆黑色的高级轿车停在雨中。那个孩子手里拿着行李，眼神已经失去了往日的光芒，变得像木偶一样空洞。",
                "（在上车前，隔着车窗对你露出了一个凄惨的微笑）训练员……对不起，是我不够强。谢谢你曾带我看过那一瞬间的太阳。再见……",
                "走吧，她的基因还有利用价值。至于你，新人训练员，你被解雇了。",
                "黑色轿车消失在夜幕中，她最终没能逃脱沦为资本商品的命运。而你站在暴雨中，手里只剩下一张空荡荡的契约书。游戏结束。【BAD END】"
            };
        }
    }

    public static void main(String[] args) {
        PApplet.main("windows.games.uma.MySketch1");
    }
}