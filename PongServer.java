import java.io.IOException;
import java.net.Socket;

public class PongServer extends PongController implements Runnable {
	public static final int PORT = 8080; // ポート番号を設定する．
	private static final int MIN_PORT = 1024; // 設定できる最小のポート番号
	private static final int MAX_PORT = 65535; // 設定できる最大のポート番号
	private static final int INVALID_PORT_NUMBER = -1;
	private static boolean isServer = false;
	StartFrameS sFrame;
	GameFrameS gFrame;
	private boolean isInitialized;
	private boolean isStartFrame;
	private boolean isGameFrame;

	private SocketConnector sConnector;
	private Socket[] socket;
	private PongReceiver[] pongReceiver;
	private PongSender[] pongSender;
	private String[] str;

	public PongServer() {
		if (isServer) {
			System.out.println("PongServer already exists.");
			return;
		}
		isServer = true;
		this.sFrame = new StartFrameS(this);
		this.isInitialized = false;
	}

	public void run() {
		this.initialize();
		this.waitBtnPushed();

		this.userName = this.sFrame.textField1.getText(); // user name
		this.number = Integer.parseInt((String) this.sFrame.textField2.getSelectedItem()); // number of players

		// create server socket
		String[] args = new String[0];
		this.startServer(args, this.number - 1);
		while (this.sConnector.getNumberOfSocket() < this.number - 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}
		this.sFrame.isAccept = false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException ire) {
			// Do Nothing.
		}
		System.out.println("Waiting for " + 0 + " players.");
		this.sFrame.upperLabel.setText("Preparing game...");
		this.sFrame.logAppendln("Preparing game...");

		for (int i = 0; i < this.number - 1; i++) {
			this.pongSender[i].send("Number of Player: " + this.number.toString());
			this.pongSender[i].send("Your ID: " + i);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ire) {
			// Do Nothing.
		}

		for (int i = 0; i < this.number - 1; i++) {
			this.pongSender[i].send("Close Start Frame");
		}

		this.changeFrameStoG();

		// スタートボタンが押されて無効になるまで待つ
		while (this.gFrame.btn.isEnabled()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}

		this.str = new String[this.number - 1];

		// ゲームが終わるまで待つ。
		while (this.isGameFrame) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}
		System.out.println("closing...");
		for (int i = 0; i < this.number - 1; i++)
			this.closeSocketStream(i);
		this.sConnector.terminate();

		isServer = false;
	}

	// 初期化
	public void initialize() {
		if (this.isInitialized) {
			return;
		}
		this.isInitialized = true;
		System.out.println("Opening: Start Frame");
		this.sFrame.setVisible(true);
		this.isStartFrame = true;
	}

	// スタート画面のボタンが押されるまで待つ
	public void waitBtnPushed() {
		try {
			while (!this.sFrame.isBtnPushed) {
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
		this.pongReceiver = new PongReceiver[n];
		this.pongSender = new PongSender[n];
		Thread thread = new Thread(this.sConnector);
		thread.start();

		this.sFrame.isAccept = true;
	}

	// ボールの位置と速度をThread[n % Number]へ送信する。
	public synchronized void sendBall(int n, Ball bl) {
		n = n % number;
		if (n == number - 1) {
			return;
		}
		pongSender[n].send(bl.toString());
	}

	public synchronized void sendPoint(int i, String points) {
		pongSender[i].send(points);
	}

	public synchronized void sendIsWin(int i, String s) {
		pongSender[i].send(s);
	}

	public synchronized void terminateConnection(int i) {
		this.closeSocketStream(i);

		if (this.sConnector != null) {
			this.sConnector.transNumberOfSocket(-1);
		}
	}

	// PongReceiverで受信した文字列に対する処理
	public synchronized void receive(String s, int i) {
		if (this.isStartFrame) {
			if (s.startsWith("Joined: ")) {
				this.sFrame.logAppendln(s.replaceFirst("Joined: ", "") + " joined.");
			}
		} else if (this.isGameFrame) {
			if (s.startsWith("Ball: ")) {
				if (i + 1 < this.number - 1)
					this.pongSender[i + 1].send(s);
				else this.gFrame.receiveBall(s);
			} else if (s.startsWith("Point: ")) {
				this.gFrame.receivePoint(s);
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
			while (i < this.number) {
				if (this.socket[i] == null)
					break;
				i++;
			}
			if (i >= this.number) {
				System.err.println("Cannot connect: Connecting socket is Full.");
				isNormalWork = false;
				return false;
			}
			this.socket[i] = nsocket;
			this.pongReceiver[i] = PongReceiver.createReceiver(this, nsocket, i); // 受信の設定
			this.pongSender[i] = PongSender.createSender(this, nsocket, i); // 送信の設定
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

		this.pongSender[i].send("Server's Name: " + this.userName);

		return true;
	}

	private void changeFrameStoG() {
		this.gFrame = new GameFrameS(this.number, this);
		System.out.println("Closing: Start Frame");
		this.isGameFrame = true;
		this.isStartFrame = false;
		this.sFrame.setVisible(false);
		System.out.println("Opening: Game Frame");
		this.gFrame.setVisible(true);
	}
}
