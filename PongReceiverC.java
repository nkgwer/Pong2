import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/* PongClient向け受信用クラス */
final class PongReceiverC implements Runnable {
	private PongClient pongClient;
	private Socket socket;
	private BufferedReader bfr;
	private boolean isReading;

	private PongReceiverC (PongClient npc, Socket nsocket, BufferedReader nbfr) {
		this.pongClient = npc;
		this.socket = nsocket;
		this.bfr = nbfr;
		this.isReading = true;
	}

	public static PongReceiverC createReceiver(PongClient pc, Socket socket)
	throws IOException {
		InputStream inputStream = socket.getInputStream();
		BufferedReader in = new BufferedReader(
		    new InputStreamReader(inputStream)); // データ受信用バッファの設定
		PongReceiverC pongReceiver = new PongReceiverC(pc, socket, in);
		System.out.println("Complete setting : Receiver = " + pongReceiver);
		System.out.println("Complete setting : Receiving Buffer = " + in);
		return pongReceiver;
	}

	public void run() {

		String line = null;

		while (this.getReading()) {
			if (this.bfr == null) {
				this.pongClient.terminateConnection();
				break;
			}

			try {
				line = this.bfr.readLine(); // データの受信
				System.out.println("Receive: \"" + line + "\" from " + this.socket.getRemoteSocketAddress());
			} catch (IOException ioe) {
				// 異常終了
				ioe.printStackTrace();
				this.pongClient.terminateConnection();
				break;
			}

			// 相手から接続が切れた場合
			if (line == null) {
				this.pongClient.terminateConnection();
				break;
			}

			// "END"を受信した場合
			if (line.equals("END")) {
				this.pongClient.terminateConnection();
				break;
			}

			this.pongClient.receive(line);
		}

		// 終了処理
		if (this.bfr != null) {
			try {
				System.out.println("Closing : Receiving Buffer = " + this.bfr);
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