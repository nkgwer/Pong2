import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/* PongServer向け送信用クラス */
public final class PongSenderS extends PongSender {
	private PongServer pongServer;

	private PongSenderS(PongServer nps, Socket ns, BufferedWriter nbfw, int ni) {
		super(ns, nbfw, ni);
		this.pongServer = nps;
	}

	public static PongSenderS createSender(PongServer ps, Socket socket, int i) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
		BufferedWriter bfw = new BufferedWriter(osw); // データ送信用バッファの設定

		PongSenderS pongSenderS = new PongSenderS(ps, socket, bfw, i);
		System.out.println("Complete setting : Sender[" + i + "] = " + pongSenderS);
		System.out.println("Complete setting : Sending Buffered Writer[" + i + "] = " + bfw);
		return pongSenderS;
	}
}