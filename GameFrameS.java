import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

// PongServer向けゲーム画面用クラス
public class GameFrameS extends GameFrame implements ActionListener {
	PongServer pongServer;
	Container cont;
	JPanel p;
	JButton btn;

	public GameFrameS(PongServer ps) {
		super();

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

		this.pongServer = ps;

		// ボールの 左上角の座標, 幅, 高さ, 速度の設定
		ball[0] = new Ball(185, 1, 30, 30);
		ball[0].setV(new Dimension((int) Math.ceil(3 * Math.random() - 1), 1));
		ball[0].setVisible(false);

		// 最初はボールがある。
		ball[0].setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		btn.setEnabled(false);
		btn.setVisible(false);
		init();
	}

	public void init() {
		count = 0;
		j = 1;
		Random rnd = new Random();

		for (int i = 0; i < this.pongServer.number - 1; i++) {
			int r = rnd.nextInt(155) + 50;
			int g = rnd.nextInt(155) + 50;
			int b = rnd.nextInt(155) + 50;
			pongServer.sendBall(i, new Ball("Ball: 185 1 30 30 1 1" + " " + r + " " + g + " " + b));
		}
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
							pongServer.sendBall(0, ball[i]);
							ball[i].setVisible(false);
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
							if (ball[i].getVY() > 8)
								ball[i].setVY(8);
							if (ball[i].getVY() < -8)
								ball[i].setVY(-8);
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
}
