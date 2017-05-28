import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

// PongClient向けゲーム画面用クラス
public class GameFrameC extends GameFrame {
	PongClient pongClient;
	Container cont;
	JPanel p;

	public GameFrameC(PongClient pc) {
		super();

		this.pongClient = pc;

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

	public void init() {
		count = 0;
		j = 1;
		new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// barの動く方向の設定
				bar.setVX(0);

				if (left && !right && bar.getX() >= 0)
					bar.setVX(-BAR_V);
				else if (right && !left && bar.getX() <= 300)
					bar.setVX(BAR_V);

				for (int i = 0; i < ball.length; i++) {
					if (ball[i] != null) {
						if (isCeiling(ball[i])) {
							ball[i].setVisible(false);
							sendBall(ball[i]);
							ball[i] = null;
						} else {
							if (isReboundLeft(ball[i]))
								ball[i].setVX(Math.abs(ball[i].getVX()));
							if (isReboundRight(ball[i]))
								ball[i].setVX(-Math.abs(ball[i].getVX()));
							if (isReboundx(ball[i]))
								ball[i].BoundX();
							if (isReboundy(ball[i]))
								ball[i].BoundY();
							for (int j = i + 1; j < ball.length; j++) {
								if (ball[j] != null)
									if (isCollide(ball[i], ball[j])) {
										collide(ball[i], ball[j]);
									}
							}
							// バーに5回当たると縦の速さが1段階速くなる
							if (count >= 5 * j && Math.abs(ball[i].getVY()) < 8) {
								ball[i].setVY((int) Math.signum(ball[i].getVY()) * (Math.abs(ball[i].getVY()) + 1));
								j++;
							}
							if (ball[i].getVX() > 8)
								ball[i].setVX(8);
							if (ball[i].getVX() < -8)
								ball[i].setVX(-8);
							ball[i].translate();
						}
					}
				}
				bar.translate();
				repaint();
			}
		}).start();
	}

	public synchronized void paint(Graphics g) {
		super.paint(g);
	}

	public void sendBall(Ball bl) {
		this.pongClient.pongSender.send(bl.toString());
	}
}