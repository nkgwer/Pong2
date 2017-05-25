import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.Timer;


// ゲーム画面用クラス
public class GameFrame extends JFrame {
    static final String TITLE_STRING = "Pong!";
    static final Dimension FRAME_SIZE = new Dimension(400, 566);

    /* フレーム中の各壁: ボールが跳ね返る。 */
    static final Rectangle CEILING = new Rectangle(-FRAME_SIZE.width, -FRAME_SIZE.height, 3 * FRAME_SIZE.width, FRAME_SIZE.height);
    static final Rectangle FLOOR = new Rectangle(-FRAME_SIZE.width, FRAME_SIZE.height, 3 * FRAME_SIZE.width, FRAME_SIZE.height);
    static final Rectangle LEFT = new Rectangle(-FRAME_SIZE.width, -FRAME_SIZE.height, FRAME_SIZE.width, 3 * FRAME_SIZE.height);
    static final Rectangle RIGHT = new Rectangle(FRAME_SIZE.width, -FRAME_SIZE.height, FRAME_SIZE.width, 3 * FRAME_SIZE.height);

    static final int BAR_V = 2; // barV: バーの横移動の速さ
    static final int nKEY_LEFT = KeyEvent.VK_LEFT;
    static final int nKEY_RIGHT = KeyEvent.VK_RIGHT;

    protected Graphics g;

    protected Ball ball; // Rectangle型のサブクラス
    protected Bar bar; // Rectangle型のサブクラス
    protected boolean left, right, kz, kx;

    // count: ボールがbarとぶつかった回数
    int count = 0, j;

    protected boolean isBallHere = false;

    public GameFrame () {
        this.setTitle(TITLE_STRING); // タイトルの設定
        this.setSize(FRAME_SIZE); // サイズの設定
        this.setResizable(false); //サイズを固定
        this.setLocationRelativeTo(null); // 位置を中央に設定
        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ×を押すとプログラムが終了
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setBackground(Color.WHITE); // 背景は白

        // ボールの 左上角の座標, 幅, 高さ, 速度の設定
        ball = new Ball(185, 268, 30, 30);
        ball.setV(new Dimension(1, 1));

        // バー
        bar = new Bar(150, 461, 100, 10);

        try {
            g = getGraphics();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addKeyListener(new KeyAdapter() {
            public void keyPressed (KeyEvent e) {
                switch (e.getKeyCode()) {
                case nKEY_LEFT : left = true; break;
                case nKEY_RIGHT : right = true; break;
                }
            }
            public void keyReleased (KeyEvent e) {
                switch (e.getKeyCode()) {
                case nKEY_LEFT : left = false; break;
                case nKEY_RIGHT : right = false; break;
                }
            }
        });
    }

    public void init() {
        count = 0; j = 1;
        new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // barの動く方向の設定
                bar.setVX(0);

                if (left && !right && bar.getX() >= 0) bar.setVX(-BAR_V);
                else if (right && !left && bar.getX() <= 300) bar.setVX(BAR_V);
                
                if (isBallHere) {
                    isBallHere = !isCeiling(ball);
                    if (isReboundLeft(ball)) ball.setVX(Math.abs(ball.getVX()));
                    if (isReboundRight(ball)) ball.setVX(- Math.abs(ball.getVX()));
                    if (isReboundx(ball)) ball.BoundX();
                    if (isReboundy(ball)) ball.BoundY();
                    // バーに5回当たると縦の速さが1段階速くなる
                    if (count >= 5 * j && Math.abs(ball.getVY()) < 8) {
                        ball.setVY((int) Math.signum(ball.getVY()) * (Math.abs(ball.getVY()) + 1));
                        j++;
                    }
                    if (ball.getVX() > 8) ball.setVX(8);
                    if (ball.getVX() < -8) ball.setVX(-8);
                    ball.translate();
                }
                
                bar.translate();
                repaint();
            }
        }).start();
    }

    public synchronized void paint(Graphics g) {
        // 描画
        g.setColor(new Color(255,255,255,100));
        g.fillRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);

        //g.clearRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);
        if (isBallHere) {
            g.setColor(Color.RED);
            g.fillOval(ball.x, ball.y, ball.width, ball.height);
        }
        g.setColor(Color.BLACK);
        g.fillRect(bar.x, bar.y, bar.width, bar.height);
    }

    protected boolean isCeiling (Ball bl) {
        return bl.Next().intersects(CEILING);
    }

    protected boolean isFloor (Ball bl) {
        return bl.Next().intersects(FLOOR);
    }

    protected boolean isReboundLeft (Ball bl) {
        return bl.Next().intersects(LEFT);
    }

    protected boolean isReboundRight (Ball bl) {
        return bl.Next().intersects(RIGHT);
    }

    protected boolean isReboundx (Ball bl) {
        boolean b = false;
        if (bl.Next().intersects(bar.Next()) && (bl.x + bl.width <= bar.x || bl.x >= bar.x + bar.width)) {
            b = true; ball.setVX(ball.getVX() - bar.getVX()); count++;
        }
        return b;
    }

    protected boolean isReboundy (Ball bl) {
        boolean b = (bl.y + bl.height >= FRAME_SIZE.height - 1);
        if (bl.Next().intersects(bar.Next()) && (bl.y + bl.height <= bar.y || bl.y >= bar.y + bar.height)) {
            b = true; ball.setVX(ball.getVX() + (int) Math.signum(bar.getVX())); count++;
        }
        return b;
    }
}