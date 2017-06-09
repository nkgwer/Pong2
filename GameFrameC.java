import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

// PongClient向けゲーム画面用クラス
public class GameFrameC extends GameFrame {
	Container cont;
	JPanel p;

	public GameFrameC(PongController npc) {
		super(npc);

		// パネル
		p = new JPanel();
		p.setLayout(null);

		// コンテントペイン
		cont = this.getContentPane();
		cont.add(p, BorderLayout.CENTER);

		// ボール 左上角の座標, 幅, 高さ, 速度の設定
		// 最初は見えないので, 画面外, 速度0に。
		// ball[0] = new Ball(-30, -30, 30, 30);
		// ball[0].setV(new Dimension(0, 0));

		this.init();
	}
}