package com.o_char.pong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PongClient extends PongController implements Runnable {
	StartFrameC sFrameC;
	GameFrame gFrame;
	String hostName, sPlayerName;
	int id;
	private boolean isInitialized;
	private boolean isStartFrame;
	private boolean isGameFrame;

	private Socket socket;
	private PongReceiver pongReceiver;
	private PongSender pongSender;

	public PongClient() {
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
			try {
				Thread.sleep(10);
			} catch (InterruptedException ire) {
				// Do Nothing.
			}
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
		System.exit(0);
	}

	public void initialize() {
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
		int c = 0;
		while (this.socket == null && c < 100) {
			try {
				this.socket = new Socket(addr, portNumber);
				break;
			} catch (IOException ioe) {
				this.socket = null;
				c++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException ire) {
					// Do Nothing.
				}
			}
		}
		if (c >= 100 || this.socket == null) {
			String msg = "Failed to connect.";
			System.err.println(msg);
			return;
		}
		System.out.println("socket = " + this.socket);

		// 受信の設定
		try {
			this.pongReceiver = PongReceiver.createReceiver(this, this.socket);
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
			this.pongSender = PongSender.createSender(this, this.socket);
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
	public synchronized void receive(String s, int ri) {
		if (this.isStartFrame) {
			if (s.startsWith("Number of Player: ")) {
				this.number = Integer.parseInt(s.replaceFirst("Number of Player: ", ""));
				System.out.println("Number of players: " + this.number + "人");
			} else if (s.startsWith("Server's Name: ")) {
				String str = s.replaceFirst("Server's Name: ", "");
				this.sPlayerName = str;
				this.sFrameC.logAppendln("Connected to " + str + ".");
			} else if (s.startsWith("Your ID: ")) {
				String str = s.replaceFirst("Your ID: ", "");
				this.id = Integer.parseInt(str);
				this.sFrameC.logAppendln("My ID: " + str + ".");
				this.sFrameC.logAppendln("Waiting...");
			} else if (s.equals("Close Start Frame")) {
				this.changeFrameStoG();
			}
		} else if (this.isGameFrame) {
			if (s.startsWith("Ball:")) {
				this.gFrame.receiveBall(s);
			} else if (s.startsWith("Point:")) {
				this.gFrame.receivePoint(s);
			} else if (s.equals("Win")) {
				this.gFrame.win();
			} else if (s.equals("Lose")) {
				this.gFrame.lose();
			}
		}
	}

	public synchronized void sendBall(int n, Ball bl) {
		pongSender.send(bl.toString());
	}

	public synchronized void sendPoint(int i, String points) {
		pongSender.send(points);
	}

	public synchronized void terminateConnection(int i) {
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

	private void changeFrameStoG() {
		this.gFrame = new GameFrameC(this.number, this.id, this);
		System.out.println("Closing: Start Frame");
		this.isGameFrame = true;
		this.isStartFrame = false;
		this.sFrameC.setVisible(false);
		System.out.println("Opening: Game Frame");
		this.gFrame.setVisible(true);
	}
}
