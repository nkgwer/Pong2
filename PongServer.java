import java.util.StringTokenizer;
import java.io.IOException;
import java.net.Socket;

public class PongServer implements Runnable {
	public static final int PORT = 8080; // ポート番号を設定する．
	private static final int MIN_PORT = 1024; // 設定できる最小のポート番号
	private static final int MAX_PORT = 65535; // 設定できる最大のポート番号
	private static final int INVALID_PORT_NUMBER = -1;
	private static boolean isServer = false;
	private boolean isInitialized, isStartFrame, isGameFrame;
	StartFrameS sf;
	GameFrameS gf;
	String usrname;
	Integer Number;
	private SocketConnector sConnector;
	private Socket[] socket;
	private PongReceiverS[] pongReceiver;
	private PongSenderS[] pongSender;
	private String[] str;

	public PongServer() {
		if (isServer) {
			System.out.println("PongServer already exists.");
			return;
		}
		isServer = true;
		this.sf = new StartFrameS(this);
		this.isInitialized = false;
	}

	public void run() {
		this.initialize();
		this.waitBtnPushed();

		this.usrname = this.sf.textField1.getText(); // user name
		this.Number = Integer.parseInt((String) this.sf.textField2.getSelectedItem()); // number of players

		// create server socket
		String[] args = new String[0];
		this.startServer(args, this.Number - 1);
		while (this.sConnector.getNumberOfSocket() < this.Number - 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}
		this.sf.isAccept = false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException ire) {
			// Do Nothing.
		}
		System.out.println("Waiting for " + 0 + " players.");
		this.sf.upperLabel.setText("Preparing game...");
		this.sf.logAppendln("Preparing game...");

		for (int i = 0; i < this.Number - 1; i++) {
			this.pongSender[i].send("Number of Player: " + this.Number.toString());
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ire) {
			// Do Nothing.
		}

		for (int i = 0; i < this.Number - 1; i++) {
			this.pongSender[i].send("Close Start Frame");
		}

		this.changeFrameStoG();

		// スタートボタンが押されて無効になるまで待つ
		while (this.gf.btn.isEnabled()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}

		this.str = new String[this.Number - 1];

		while (this.isGameFrame) {
			int x = 0, vx = 0, vy = 0;

			// ボールが自分のフィールドから出ない間待つ。
			while (this.gf.isBallHere) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}

			x = this.gf.ball.x;
			vx = this.gf.ball.getVX();
			vy = this.gf.ball.getVY();

			for (int i = 0; i < this.Number - 1; i++) {
				int[] place = new int[3];
				// 上に行ったボールの位置と速度を送信する。
				this.sendPlaceVelocity(i, x, vx, vy);

				this.str[i] = null;
				while ( this.str[i] == null ) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException ire) {
						// Do Nothing.
					}
				}

				StringTokenizer st = new StringTokenizer(this.str[i], " ");
				for (int j = 0; j < 3; j++) {
					place[j] = Integer.parseInt(st.nextToken());
				}

				x = place[0];
				vx = place[1];
				vy = place[2];
			}
			this.gf.ball.setLocation(this.gf.FRAME_SIZE.width - this.gf.ball.width - x, 1);
			this.gf.ball.setVX(- vx);
			this.gf.ball.setVY((int) Math.abs(vy));
			this.gf.isBallHere = true;
		}
		System.out.println("closing...");
		for (int i = 0; i < this.Number - 1; i++)
			this.closeSocketStream(i);
		this.sConnector.terminate();

		isServer = false;
	}

	// public static void main(String[] args) throws Exception {
	// 	PongServer ps = new PongServer();
	// 	ps.initialize();
	// 	ps.waitBtnPushed();

	// 	ps.usrname = ps.sf.textField1.getText(); // ユーザーネーム
	// 	ps.Number = Integer.parseInt((String) ps.sf.textField2.getSelectedItem()); // 対戦人数

	// 	// サーバーソケットの作成
	// 	ps.startServer(args, ps.Number - 1);

	// 	while (ps.sConnector.getNumberOfSocket() < ps.Number - 1) {
	// 		try {
	// 			Thread.sleep(10);
	// 		} catch (InterruptedException ire) {
	// 			// Do Nothing.
	// 		}
	// 	}
	// 	ps.sf.isAccept = false;
	// 	try {
	// 		Thread.sleep(100);
	// 	} catch (InterruptedException ire) {
	// 		// Do Nothing.
	// 	}
	// 	System.out.println("Waiting for " + 0 + " players.");
	// 	ps.sf.upperLabel.setText("Preparing game...");
	// 	ps.sf.logAppendln("Preparing game...");

	// 	for (int i = 0; i < ps.Number - 1; i++) {
	// 		ps.pongSender[i].send("Number of Player: " + ps.Number.toString());
	// 	}

	// 	try {
	// 		Thread.sleep(1000);
	// 	} catch (InterruptedException ire) {
	// 		// Do Nothing.
	// 	}

	// 	for (int i = 0; i < ps.Number - 1; i++) {
	// 		ps.pongSender[i].send("Close Start Frame");
	// 	}

	// 	ps.changeFrameStoG();

	// 	// スタートボタンが押されて無効になるまで待つ
	// 	while (ps.gf.btn.isEnabled()) {
	// 		try {
	// 			Thread.sleep(10);
	// 		} catch (InterruptedException ire) {
	// 			// Do Nothing.
	// 		}
	// 	}

	// 	ps.str = new String[ps.Number - 1];

	// 	while (ps.isGameFrame) {
	// 		int x = 0, vx = 0, vy = 0;

	// 		// ボールが自分のフィールドから出ない間待つ。
	// 		while (ps.gf.isBallHere) {
	// 			try {
	// 				Thread.sleep(10);
	// 			} catch (InterruptedException ire) {
	// 				// Do Nothing.
	// 			}
	// 		}

	// 		x = ps.gf.ball.x;
	// 		vx = ps.gf.ball.getVX();
	// 		vy = ps.gf.ball.getVY();

	// 		for (int i = 0; i < ps.Number - 1; i++) {
	// 			int[] place = new int[3];
	// 			// 上に行ったボールの位置と速度を送信する。
	// 			ps.sendPlaceVelocity(i, x, vx, vy);

	// 			ps.str[i] = null;
	// 			while ( ps.str[i] == null ) {
	// 				try {
	// 					Thread.sleep(10);
	// 				} catch (InterruptedException ire) {
	// 					// Do Nothing.
	// 				}
	// 			}

	// 			StringTokenizer st = new StringTokenizer(ps.str[i], " ");
	// 			for (int j = 0; j < 3; j++) {
	// 				place[j] = Integer.parseInt(st.nextToken());
	// 			}

	// 			x = place[0];
	// 			vx = place[1];
	// 			vy = place[2];
	// 		}
	// 		ps.gf.ball.setLocation(ps.gf.FRAME_SIZE.width - ps.gf.ball.width - x, 1);
	// 		ps.gf.ball.setVX(- vx);
	// 		ps.gf.ball.setVY((int) Math.abs(vy));
	// 		ps.gf.isBallHere = true;
	// 	}
	// 	System.out.println("closing...");
	// 	for (int i = 0; i < ps.Number - 1; i++)
	// 		ps.closeSocketStream(i);
	// 	ps.sConnector.terminate();
	// 	// System.exit(0);
	// }

	// 初期化
	public void initialize () {
		if (this.isInitialized) {
			return;
		}
		this.isInitialized = true;
		System.out.println("Opening: Start Frame");
		this.sf.setVisible(true);
		this.isStartFrame = true;
	}

	// スタート画面のボタンが押されるまで待つ
	public void waitBtnPushed() {
		try {
			while (!this.sf.isBtnPushed) {
				Thread.sleep(10);
			}
		} catch (InterruptedException ire) {
			// Do Nothing.
		}
	}

	// 文字列portSからポート番号を取得
	private int getPortNumber(String portS) {
		int portNumber = INVALID_PORT_NUMBER;
		try {
			portNumber = Integer.parseInt(portS);

			if ((portNumber < MIN_PORT) || (portNumber > MAX_PORT)) {
				portNumber = INVALID_PORT_NUMBER;
			}
		} catch (NumberFormatException nfe) {
			System.err.println("Failed to change to integer.");
		}
		return portNumber;
	}

	// n個以下のクライアントソケットと接続できるサーバーソケットを起動する
	public void startServer(String[] args, int n) {
		int portNumber = PORT;
		if (args.length > 0) {
			portNumber = this.getPortNumber(args[0]); // 引数のポート番号を取得する
		}

		if (portNumber == INVALID_PORT_NUMBER) {
			String msg = "Port: Integer " + MIN_PORT + " ~ " + MAX_PORT;
			System.err.println(msg);
			return;
		}

		try {
			this.sConnector = SocketConnector.createConnector(this, portNumber, n);
		} catch (IOException ioe) {
			String msg = "Failed to ensuring port.";
			System.out.println(msg);
			return;
		}
		this.socket = new Socket[n];
		this.pongReceiver = new PongReceiverS[n];
		this.pongSender = new PongSenderS[n];
		Thread thread = new Thread(this.sConnector);
		thread.start();

		this.sf.isAccept = true;
	}

	// ボールの位置と速度をThread[n % Number]へ送信する。
	public synchronized void sendPlaceVelocity (int n, int x, int vx, int vy) {
		n = n % Number;
		if (n == Number - 1) {
			return;
		}
		pongSender[n].send("Place: " + x + " " + vx + " " + vy);
	}

	public synchronized void terminateConnection (int i) {
		this.closeSocketStream(i);

		if (this.sConnector != null) {
			this.sConnector.transNumberOfSocket(-1);
		}
	}

	// PongReceiverで受信した文字列に対する処理
	public synchronized void receive(String s, int i) {
		if (this.isStartFrame) {
			if (s.startsWith("Joined: ")) {
				this.sf.logAppendln(s.replaceFirst("Joined: ", "") + " joined.");
			}
		} else if (this.isGameFrame) {
			if (!this.gf.isBallHere) {
				try {
					str[i] = s;
				} catch (NullPointerException npe) {
					// Do Nothing.
				}
			}
		}

	}

	private void closeSocketStream(int i) {
		if (this.pongSender[i] != null) {
			this.pongSender[i].send("END");
			System.out.println("Closing : Sender[" + i + "] = " + this.pongSender[i]);
			this.pongSender[i].terminate();
			this.pongSender[i] = null;
		}

		if (this.pongReceiver[i] != null) {
			System.out.println("Closing : Receiver[" + i + "] = " + this.pongReceiver[i]);
			this.pongReceiver[i].terminate();
			this.pongReceiver[i] = null;
		}

		if (this.socket[i] != null) {
			try {
				System.out.println("Closing : socket[" + i + "] = " + this.socket[i]);
				this.socket[i].close();
				this.sConnector.transNumberOfSocket(-1);
			} catch (IOException ioe) {
				// Do Nothing.
			} finally {
				this.socket[i] = null;
			}
		}
	}

	public boolean acceptSocket(Socket nsocket) {
		boolean isNormalWork = true;
		int i = 0;
		try {
			while (i < this.Number) {
				if (this.socket[i] == null) break;
				i++;
			}
			if (i >= this.Number) {
				System.err.println("Cannot connect: Connecting socket is Full.");
				isNormalWork = false;
				return false;
			}
			this.socket[i] = nsocket;
			this.pongReceiver[i] = PongReceiverS.createReceiver(this, nsocket, i); // 受信の設定
			this.pongSender[i] = PongSenderS.createSender(this, nsocket, i); // 送信の設定
		} catch (IOException ioe) {
			this.socket[i] = null;
			this.pongReceiver[i] = null;
			this.pongSender[i] = null;
			isNormalWork = false;

			return false;
		}

		Thread thread = new Thread(this.pongReceiver[i]);
		thread.start();

		// 受信中に設定
		this.sConnector.setReceivedNow(true);
		this.sConnector.transNumberOfSocket(1);

		this.pongSender[i].send("Server's Name: " + this.usrname);

		return true;
	}

	private void changeFrameStoG () {
		this.gf = new GameFrameS();
		System.out.println("Closing: Start Frame");
		this.isGameFrame = true;
		this.isStartFrame = false;
		this.sf.setVisible(false);
		System.out.println("Opening: Game Frame");
		this.gf.setVisible(true);
	}
}

