package com.o_char.pong;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

// 外部からの接続(MAX_SOCKET_NUMBER 個以下のクライアントソケット)を受け付けるサーバーソケット
public final class SocketConnector implements Runnable {
	private PongServer ps;
	private ServerSocket s;
	private final int MAX_SOCKET_NUMBER;
	private int socketNumber;
	private final int TIME_OUT = 1000;

	// 受信中かどうか
	private boolean isReceivedNow;

	// 終了要求があったか
	private boolean isTermination;

	private SocketConnector(PongServer nps, int n) {
		this.ps = nps;
		this.MAX_SOCKET_NUMBER = n;
		this.socketNumber = 0;
	}

	public static SocketConnector createConnector(PongServer ps, int portNumber, int n) throws IOException {
		SocketConnector sc = new SocketConnector(ps, n);
		sc.initialize(portNumber);
		return sc;
	}

	public void run() {

		// 終了要求がない間
		while (this.isTermination() == false) {
			// 新たな接続を受信する
			Socket socket = this.acceptConnection();

			// 接続受信していない場合は再受信
			if (socket == null) {
				continue;
			}

			System.out.println("Connection accepted: " + socket);
			this.ps.acceptSocket(socket);
		}

		// サーバーソケットsを閉じる
		try {
			System.out.println("Closing serversocket: " + this.s);
			this.s.close();
		} catch (IOException ioe) {
			// Do Nothing.
		} finally {
			this.s = null;
		}
	}

	// 受信中かどうか取得
	public synchronized boolean isReceivedNow() {
		return this.isReceivedNow;
	}

	// 受信中かどうかの設定
	public synchronized void setReceivedNow(boolean b) {
		this.isReceivedNow = b;
	}

	public synchronized int getNumberOfSocket() {
		return this.socketNumber;
	}

	public synchronized void transNumberOfSocket(int n) {
		if (n == -1) {
			this.socketNumber--;
			if (this.socketNumber <= 0)
				this.setReceivedNow(false);
		} else if (n == 1)
			this.socketNumber++;
	}

	public synchronized void terminate() {
		this.isTermination = true;
	}

	public synchronized boolean isTermination() {
		return isTermination;
	}

	// サーバーソケットの初期化
	public void initialize(int portNumber) throws IOException {
		this.s = new ServerSocket(portNumber);
		this.s.setSoTimeout(this.TIME_OUT);
		System.out.println("Started: " + this.s);
	}

	private Socket acceptConnection() {
		Socket cSocket = null;

		try {
			cSocket = this.s.accept(); // コネクション設定要求を待つ
		} catch (SocketTimeoutException toe) {
			// Do Nothing.
		} catch (IOException ioe) {
			System.err.println("ソケット受信に異常");
		}

		if ((cSocket != null) && (this.getNumberOfSocket() >= this.MAX_SOCKET_NUMBER)) {
			System.err.println("Cannot connect: Connecting socket is Full.");
			cSocket = null;
		}
		return cSocket;
	}
}