package com.o-char.pong;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/* 受信用クラス */
final class PongReceiver implements Runnable {
	private PongController pongController;
	private Socket socket;
	private BufferedReader bfr;
	private boolean isReading;
	private int i;

	private PongReceiver (PongController npc, Socket nsocket, BufferedReader nbfr, int ni) {
		this.pongController = npc;
		this.socket = nsocket;
		this.bfr = nbfr;
		this.i = ni;
		this.isReading = true;
	}

	public static PongReceiver createReceiver(PongController pc, Socket socket, int i)
	throws IOException {
		InputStream inputStream = socket.getInputStream();
		BufferedReader in = new BufferedReader(
		    new InputStreamReader(inputStream)); // データ受信用バッファの設定
		PongReceiver pongReceiver = new PongReceiver(pc, socket, in, i);
		if (i != -1) { // i != -1 : pongServerなどソケットが2つ以上あるとき
			System.out.println("Complete setting : Receiver[" + i + "] = " + pongReceiver);
			System.out.println("Complete setting : Receiving Buffer[" + i + "] = " + in);
		} else { // i == -1 : pongClientなどソケットが1つしかないとき
			System.out.println("Complete setting : Receiver = " + pongReceiver);
			System.out.println("Complete setting : Receiving Buffer = " + in);
		}
		return pongReceiver;
	}

	public static PongReceiver createReceiver(PongController pc, Socket socket) throws IOException {
		return PongReceiver.createReceiver(pc, socket, -1);
	}

	public void run() {

		String line = null;

		while (this.getReading()) {
			if (this.bfr == null) {
				this.pongController.terminateConnection(i);
				break;
			}

			try {
				line = this.bfr.readLine(); // データの受信
				System.out.println("Receive: \"" + line + "\" from " + socket.getRemoteSocketAddress());
			} catch (IOException ioe) {
				// 異常終了
				ioe.printStackTrace();
				this.pongController.terminateConnection(i);
				break;
			}

			// 相手から接続が切れた場合
			if (line == null) {
				this.pongController.terminateConnection(i);
				break;
			}

			// "END"を受信した場合
			if (line.equals("END")) {
				this.pongController.terminateConnection(i);
				break;
			}

			this.pongController.receive(line, this.i);
		}

		// 終了処理
		if (this.bfr != null) {
			try {
				if (this.i != -1) System.out.println("Closing : Receiving Buffer[" + this.i + "] = " + this.bfr);
				else System.out.println("Closing : Receiving Buffer = " + this.bfr);
				this.bfr.close();
			} catch (IOException ioe) {
				// Do nothing.
			} finally {
				this.bfr = null;
			}
		}
	}

	// 終了状態に設定する。
	public synchronized void terminate() {
		this.isReading = false;
	}

	// 受信しているか(true)終了状態か(false)調べる。
	public synchronized boolean getReading() {
		return this.isReading;
	}
}