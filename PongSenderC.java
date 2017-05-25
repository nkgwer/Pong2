import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/* PongClient向け送信用クラス */
public final class PongSenderC extends PongSender {
    private PongClient pongClient;

    private PongSenderC (PongClient npc, Socket ns, BufferedWriter nbfw) {
        super(ns, nbfw, 0);
        this.pongClient = npc;
    }

    public static PongSenderC createSender(PongClient pc, Socket socket)
    throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bfw = new BufferedWriter(osw); // データ送信用バッファの設定

        PongSenderC pongSenderC = new PongSenderC(pc, socket, bfw);
        System.out.println("Complete setting : Sender = " + pongSenderC);
        System.out.println("Complete setting : Sending Buffered Writer = " + bfw);
        return pongSenderC;
    }
}