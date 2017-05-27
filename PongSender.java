import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/* 送信用クラス */
public class PongSender {
	private Socket socket;
	private BufferedWriter bfw;
	private int i;
	private PrintWriter out;

	protected PongSender(Socket ns, BufferedWriter nbfw, int ni) {
		this.socket = ns;
		this.bfw = nbfw;
		this.i = ni;
		this.out = new PrintWriter(this.bfw, true);
	}

	public boolean send(String string) {
		if (this.bfw == null) {
			return false;
		}

		boolean isNormalWork = true;

		System.out.println("Send: \"" + string + "\" to " + this.socket.getRemoteSocketAddress());
		out.println(string); // データの送信

		return isNormalWork;
	}

	// 送信用バッファを閉じる
	public void terminate() {
		if (this.bfw == null) {
			return;
		}
		try {
			if (this instanceof PongSenderC) {
				System.out.println("Closing : Sending Buffered Writer[] = " + this.bfw);
			} else {
				System.out.println("Closing : Sending Buffered Writer[" + i + "] = " + this.bfw);
			}
			this.bfw.close();
		} catch (IOException ioe) {
			// Do Nothing.
		} finally {
			this.bfw = null;
		}
	}
}