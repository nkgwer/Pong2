import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

// PongServer向けゲーム画面用クラス
public class GameFrameS extends GameFrame implements ActionListener {
	Container cont;
	JPanel p;
	JButton btn;
	int point[];

	public GameFrameS(int n, PongController npc) {
		super(npc);

		// スタートボタン
		btn = new JButton("Start!");
		btn.setPreferredSize(new Dimension(60, 30));
		btn.addActionListener(this);

		// スタートボタンが乗るパネル
		p = new JPanel();
		p.setLayout(null);
		p.add(btn);
		btn.setBounds(110, 200, 180, 120);

		// コンテントペイン
		cont = getContentPane();
		cont.add(p, BorderLayout.CENTER);

		// ボールの 左上角の座標, 幅, 高さ, 速度の設定
		ball[0] = new Ball(185, 1, 30, 30);
		ball[0].setV(new Dimension((int) Math.ceil(3 * Math.random() - 1), 1));
		ball[0].setVisible(false);

		// 最初はボールがある。
		ball[0].setVisible(true);

		point = new int[n];
		for (int i = 0; i < point.length; i++) {
			point[i] = 0;
		}
	}

	public void actionPerformed(ActionEvent e) {
		btn.setEnabled(false);
		btn.setVisible(false);
		init();
	}

	public void init() {
		Random rnd = new Random();

		for (int i = 0; i < this.pongController.number - 1; i++) {
			int vx = (int) Math.ceil(3 * Math.random() - 1);
			int r = rnd.nextInt(155) + 50;
			int g = rnd.nextInt(155) + 50;
			int b = rnd.nextInt(155) + 50;
			this.pongController.sendBall(i, new Ball("Ball: 185 1 30 30 " + vx + " 1" + " " + r + " " + g + " " + b));
		}
		super.init();
	}
}
