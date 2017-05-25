import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;


// PongClient向けゲーム画面用クラス
public class GameFrameC extends GameFrame {
    Container cont;
    JPanel p;

    public GameFrameC () {
        super();

        // パネル
        p = new JPanel();
        p.setLayout(null);

        // コンテントペイン
        cont = this.getContentPane();
        cont.add(p, BorderLayout.CENTER);

        // ボール 左上角の座標, 幅, 高さ, 速度の設定
        // 最初は見えないので, 画面外, 速度0に。
        ball = new Ball(-30, -30, 30, 30);
        ball.setV(new Dimension(0, 0));

        this.init();
    }

    public void init() {
        super.init();
    }

    public synchronized void paint(Graphics g) {
        super.paint(g);
    }
}