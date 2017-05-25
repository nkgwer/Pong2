import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/* 受信用クラス */
final class PongReceiverS implements Runnable {
	private PongServer ps;
	private Socket socket;
	private BufferedReader bfr;
	private boolean isReading;
	private int i;

	private PongReceiverS (PongServer nps, Socket nsocket, BufferedReader nbfr, int ni) {
		this.ps = nps;
		this.socket = nsocket;
		this.bfr = nbfr;
		this.i = ni;
		this.isReading = true;
	}

	public static PongReceiverS createReceiver(PongServer s, Socket socket, int i)
	throws IOException {
		InputStream inputStream = socket.getInputStream();
		BufferedReader in = new BufferedReader(
		    new InputStreamReader(inputStream)); // データ受信用バッファの設定
		PongReceiverS pongReceiver = new PongReceiverS(s, socket, in, i);
		System.out.println("Complete setting : Receiver[" + i + "] = " + pongReceiver);
		System.out.println("Complete setting : Receiving Buffer[" + i + "] = " + in);
		return pongReceiver;
	}

	public void run() {

		String line = null;

		while (this.getReading()) {
			if (this.bfr == null) {
				this.ps.terminateConnection(i);
				break;
			}

			try {
				line = this.bfr.readLine(); // データの受信
				System.out.println("Receive: \"" + line + "\" from " + socket.getRemoteSocketAddress());
			} catch (IOException ioe) {
				// 異常終了
				ioe.printStackTrace();
				this.ps.terminateConnection(i);
				break;
			}

			// 相手から接続が切れた場合
			if (line == null) {
				this.ps.terminateConnection(i);
				break;
			}

			// "END"を受信した場合
			if (line.equals("END")) {
				this.ps.terminateConnection(i);
				break;
			}

			this.ps.receive(line, this.i);
		}

		// 終了処理
		if (this.bfr != null) {
			try {
				System.out.println("Closing : Receiving Buffer[" + this.i + "] = " + this.bfr);
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