import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.Timer;

// ゲーム画面用クラス
public abstract class GameFrame extends JFrame {
	static final String TITLE_STRING = "Pong!";
	static final Dimension FRAME_SIZE = new Dimension(400, 566);

	/* フレーム中の各壁: ボールが跳ね返る。 */
	static final Rectangle CEILING = new Rectangle(-FRAME_SIZE.width, -FRAME_SIZE.height, 3 * FRAME_SIZE.width,
	        FRAME_SIZE.height);
	static final Rectangle FLOOR = new Rectangle(-FRAME_SIZE.width, FRAME_SIZE.height, 3 * FRAME_SIZE.width,
	        FRAME_SIZE.height);
	static final Rectangle LEFT = new Rectangle(-FRAME_SIZE.width, -FRAME_SIZE.height, FRAME_SIZE.width,
	        3 * FRAME_SIZE.height);
	static final Rectangle RIGHT = new Rectangle(FRAME_SIZE.width, -FRAME_SIZE.height, FRAME_SIZE.width,
	        3 * FRAME_SIZE.height);

	static final int BAR_V = 2; // barV: バーの横移動の速さ
	static final int nKEY_LEFT = KeyEvent.VK_LEFT;
	static final int nKEY_RIGHT = KeyEvent.VK_RIGHT;
	static final int BALL_N = 10;

	public static final Font font = new Font("Sans-Serif", Font.PLAIN, 20);

	protected PongController pongController;
	protected Graphics g;

	protected Ball[] ball; // Rectangle型のサブクラス
	protected Bar bar; // Rectangle型のサブクラス
	protected boolean left, right, kz, kx;
	int mypoint;

	// count: ボールがbarとぶつかった回数
	int count = 0, j;

	public GameFrame(PongController npc) {
		this.pongController = npc;

		this.setTitle(TITLE_STRING); // タイトルの設定
		this.setSize(FRAME_SIZE); // サイズの設定
		this.setResizable(false); // サイズを固定
		this.setLocationRelativeTo(null); // 位置を中央に設定
		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ×を押すとプログラムが終了
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setBackground(Color.WHITE); // 背景は白

		ball = new Ball[BALL_N];

		// Bar
		bar = new Bar(150, 461, 100, 10);

		// Point
		mypoint = 0;

		try {
			g = getGraphics();
		} catch (Exception e) {
			e.printStackTrace();
		}

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case nKEY_LEFT:
					left = true;
					break;
				case nKEY_RIGHT:
					right = true;
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case nKEY_LEFT:
					left = false;
					break;
				case nKEY_RIGHT:
					right = false;
					break;
				}
			}
		});
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
							pongController.sendBall(0, ball[i]);
							ball[i].setVisible(false);
							ball[i] = null;
						} else if (isFloor(ball[i])) {
							ball[i].boundY();
							mypoint -= 10;
						} else {
							if (isReboundLeft(ball[i]))
								ball[i].setVX(Math.abs(ball[i].getVX()));
							if (isReboundRight(ball[i]))
								ball[i].setVX(-Math.abs(ball[i].getVX()));
							if (isReboundx(ball[i]))
								ball[i].boundX();
							if (isReboundy(ball[i]) && ball[i].getVY() > 0) {
								ball[i].boundY();
								mypoint += 10;
							}
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
		// 描画
		g.setColor(new Color(255, 255, 255, 100));
		g.fillRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);

		// g.clearRect(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);

		for (int i = 0; i < ball.length; i++) {
			if (ball[i] != null) {
				g.setColor(new Color(ball[i].getR(), ball[i].getG(), ball[i].getB()));
				g.fillOval(ball[i].x, ball[i].y, ball[i].width, ball[i].height);
			}
		}
		g.setColor(Color.BLACK);
		g.fillRect(bar.x, bar.y, bar.width, bar.height);

		// ポイント表示
		g.setFont(font);
		g.drawString("Point: " + mypoint, 30, 50);
	}

	protected boolean isCeiling(Ball bl) {
		return bl.next().intersects(CEILING);
	}

	protected boolean isFloor(Ball bl) {
		return bl.next().intersects(FLOOR);
	}

	protected boolean isReboundLeft(Ball bl) {
		return bl.next().intersects(LEFT);
	}

	protected boolean isReboundRight(Ball bl) {
		return bl.next().intersects(RIGHT);
	}

	protected boolean isReboundx(Ball bl) {
		boolean b = false;
		if (bl.next().intersects(bar.next()) && (bl.x + bl.width <= bar.x || bl.x >= bar.x + bar.width)) {
			b = true;
			bl.setVX(bl.getVX() - bar.getVX());
			count++;
		}
		return b;
	}

	protected boolean isReboundy(Ball bl) {
		// boolean b = (bl.y + bl.height >= FRAME_SIZE.height - 1);
		boolean b = false;
		if (bl.next().intersects(bar.next()) && (bl.y + bl.height <= bar.y || bl.y >= bar.y + bar.height)) {
			b = true;
			bl.setVX(bl.getVX() + (int) Math.signum(bar.getVX()));
			count++;
		}
		return b;
	}

	protected boolean isCollide(Ball bl1, Ball bl2) {
		return bl1.next().intersects(bl2.next());
	}

	// ball[]のなかの空いている(nullの)要素にballを生成する。
	protected void receiveBall(String s) {
		int i = 0;
		while (this.ball[i] != null) {
			i++;
		}
		this.ball[i] = new Ball(s);
		this.ball[i].setLocation(this.FRAME_SIZE.width - this.ball[i].width - this.ball[i].x, 1);
		this.ball[i].setV(-this.ball[i].getVX(), (int) Math.abs(this.ball[i].getVY()));
		this.ball[i].setVisible(true);
	}

	protected void collide(Ball bl1, Ball bl2) {
		Dimension v1, v2, nv1, nv2;
		final double e = 1; // 反発係数
		v1 = bl1.getV();
		v2 = bl2.getV();
		nv1 = new Dimension((int) Math.floor(((1 - e) * v1.width + (1 + e) * v2.width) / 2),
		                    (int) Math.floor(((1 - e) * v1.height + (1 + e) * v2.height) / 2));
		nv2 = new Dimension((int) Math.floor(((1 + e) * v1.width + (1 - e) * v2.width) / 2),
		                    (int) Math.floor(((1 + e) * v1.height + (1 - e) * v2.height) / 2));
		bl1.setV(nv1);
		bl2.setV(nv2);
	}
}