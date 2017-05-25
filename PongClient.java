import java.util.StringTokenizer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PongClient implements Runnable {
	StartFrameC sFrameC;
	GameFrameC gFrameC;
	String userName, hostName, sPlayerName;
	private boolean isInitialized;
	private Socket socket;
	private PongReceiverC pongReceiver;
	private PongSenderC pongSender;
	int Number;
	boolean isStartFrame, isGameFrame;

	PongClient() {
		this.sFrameC = new StartFrameC(this);
		this.isInitialized = false;
	}

	public void run() {
		String[] args = new String[0];
		this.initialize();
		this.waitBtnPushed();

		this.userName = this.sFrameC.textField1.getText(); // ユーザーネーム
		this.hostName = this.sFrameC.textField2.getText(); // 10.9.81.128 など

		this.accessServer(args); // サーバーに接続, 受信の設定

		this.pongSender.send("Joined: " + this.userName); // データ送信

		// ゲームフレームに切り替わるまで待つ。
		while (this.isStartFrame || !this.isGameFrame) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}

		while (this.isGameFrame) {
			// ボールが自分のフィールドに来るまで待つ。
			while (!this.gFrameC.isBallHere) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}

			// ボールが自分のフィールドから出ない間待つ。
			while (this.gFrameC.isBallHere) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}

			// サーバーに、上に行ったボールの位置と速度を送信する。
			this.pongSender.send(this.gFrameC.ball.x + " " + this.gFrameC.ball.getVX() + " " + this.gFrameC.ball.getVY());
		}

		this.pongSender.send("END");

		try {
			if (this.socket != null) {
				System.out.println("closing...");
				this.socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Closed: " + this.socket.getRemoteSocketAddress());
		// this.sf.setVisible(false);
		// System.exit(0);
	}


	public static void main(String[] args) throws Exception {
		PongClient pc = new PongClient();
		pc.initialize();
		pc.waitBtnPushed();

		pc.userName = pc.sFrameC.textField1.getText(); // ユーザーネーム
		pc.hostName = pc.sFrameC.textField2.getText(); // 10.9.81.128 など

		pc.accessServer(args); // サーバーに接続, 受信の設定

		pc.pongSender.send("Joined: " + pc.userName); // データ送信

		// ゲームフレームに切り替わるまで待つ。
		while (pc.isStartFrame || !pc.isGameFrame) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
		}

		while (pc.isGameFrame) {
			// ボールが自分のフィールドに来るまで待つ。
			while (!pc.gFrameC.isBallHere) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}

			// ボールが自分のフィールドから出ない間待つ。
			while (pc.gFrameC.isBallHere) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}

			// サーバーに、上に行ったボールの位置と速度を送信する。
			pc.pongSender.send(pc.gFrameC.ball.x + " " + pc.gFrameC.ball.getVX() + " " + pc.gFrameC.ball.getVY());
		}

		pc.pongSender.send("END");

		try {
			if (pc.socket != null) {
				System.out.println("closing...");
				pc.socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Closed: " + pc.socket.getRemoteSocketAddress());
		// pc.sf.setVisible(false);
		// System.exit(0);
	}

	public void initialize () {
		if (this.isInitialized) {
			return;
		}
		this.isInitialized = true;
		System.out.println("Opening: Start Frame");
		this.sFrameC.setVisible(true);
		this.isStartFrame = true;
	}

	// スタート画面のボタンが押されるまで待つ
	public void waitBtnPushed() {
		try {
			while (!this.sFrameC.isBtnPushed) {
				Thread.sleep(10);
			}
		} catch (InterruptedException ire) {
			// Do Nothing.
		}
	}

	// 指定したホスト名のアドレス, ポート番号に接続する。
	public void accessServer(String[] args) {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(this.hostName); // IPアドレスへの変換
		} catch (UnknownHostException uhe) {
			String msg = "Failed to find the host.";
			System.err.println(msg);
			uhe.printStackTrace();
			return;
		} catch (SecurityException se) {
			System.err.println("Permission was denied by security manager.");
			se.printStackTrace();
			return;
		}
		System.out.println("addr = " + addr);

		// ソケットの作成
		int portNumber = PongServer.PORT;
		if (args.length > 0) {
			try {
				portNumber = Integer.parseInt(args[0]); // 引数のポート番号を指定する。
			} catch (NumberFormatException nfe) {
				System.err.println("Failed to change to integer.");
			}
		}
		try {
			this.socket = new Socket(addr, portNumber);
		} catch (IOException ioe) {
			String msg = "Failed to connect.";
			System.err.println(msg);
			this.socket = null;
			return;
		}
		System.out.println("socket = " + this.socket);

		// 受信の設定
		try {
			this.pongReceiver = PongReceiverC.createReceiver(this, this.socket);
		} catch (IOException ioe) {
			String msg = "Failed setting receiver.";
			System.err.println(msg);
			this.socket = null;
			this.pongReceiver = null;
			return;
		}
		Thread thread = new Thread(this.pongReceiver);
		thread.start();

		// 送信の設定
		try {
			this.pongSender = PongSenderC.createSender(this, this.socket);
		} catch (IOException ioe) {
			String msg = "Failed setting sender.";
			System.err.println(msg);
			this.socket = null;
			this.pongReceiver = null;
			this.pongSender = null;
			return;
		}

		System.out.println("Connection accepted: " + this.socket);
	}

	// pongReceiverで受信した文字列に対する処理
	public synchronized void receive(String s) {
		if (this.isStartFrame) {
			if (s.startsWith("Number of Player: ")) {
				this.Number = Integer.parseInt(s.replaceFirst("Number of Player: ", ""));
				System.out.println("Number of players: " + this.Number + "人");
			} else if (s.startsWith("Server's Name: ")) {
				String str = s.replaceFirst("Server's Name: ", "");
				this.sPlayerName = str;
				this.sFrameC.logAppendln("Connected to " + str + ".");
				this.sFrameC.logAppendln("Waiting...");
			} else if (s.equals("Close Start Frame")) {
				this.changeFrameStoG();
			}
		} else if (this.isGameFrame) {
			if (s.startsWith("Place:")) {
				int[] place = new int[3];
				StringTokenizer st = new StringTokenizer(s, " ");
				st.nextToken();
				for (int i = 0; i < 3; i++) {
					place[i] = Integer.parseInt(st.nextToken());
				}
				this.gFrameC.ball.setLocation(this.gFrameC.FRAME_SIZE.width - this.gFrameC.ball.width - place[0], 1);
				this.gFrameC.ball.setVX(- place[1]);
				this.gFrameC.ball.setVY((int) Math.abs(place[2]));
				this.gFrameC.isBallHere = true;
			}
		}
	}

	public synchronized void terminateConnection () {
		this.closeSocketStream();
	}

	private void closeSocketStream() {
		if (this.pongSender != null) {
			this.pongSender.send("END");
			System.out.println("Closing : Sender = " + this.pongSender);
			this.pongSender.terminate();
			this.pongSender = null;
		}

		if (this.pongReceiver != null) {
			System.out.println("Closing : Receiver = " + this.pongReceiver);
			this.pongReceiver.terminate();
			this.pongReceiver = null;
		}

		if (this.socket != null) {
			try {
				System.out.println("Closing : socket = " + this.socket);
				this.socket.close();
			} catch (IOException ioe) {
				// Do Nothing.
			} finally {
				this.socket = null;
			}
		}
	}

	private void changeFrameStoG () {
		this.gFrameC = new GameFrameC();
		System.out.println("Closing: Start Frame");
		this.isGameFrame = true;
		this.isStartFrame = false;
		this.sFrameC.setVisible(false);
		System.out.println("Opening: Game Frame");
		this.gFrameC.setVisible(true);
	}
}
