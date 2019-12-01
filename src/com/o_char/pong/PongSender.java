package com.o_char.pong;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/* 送信用クラス */
public class PongSender {
  private PongController pongController;
  private Socket socket;
  private BufferedWriter bfw;
  private int i;
  private PrintWriter out;

  private PongSender(PongController npc, Socket ns, BufferedWriter nbfw, int ni) {
    this.pongController = npc;
    this.socket = ns;
    this.bfw = nbfw;
    this.i = ni;
    this.out = new PrintWriter(this.bfw, true);
  }

  public static PongSender createSender(PongController pc, Socket socket, int i)
          throws IOException {
    OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
    BufferedWriter bfw = new BufferedWriter(osw); // データ送信用バッファの設定

    PongSender pongSender = new PongSender(pc, socket, bfw, i);
    if (i != -1) {
      System.out.println("Complete setting : Sender[" + i + "] = " + pongSender);
      System.out.println("Complete setting : Sending Buffered Writer[" + i + "] = " + bfw);
    } else {
      System.out.println("Complete setting : Sender = " + pongSender);
      System.out.println("Complete setting : Sending Buffered Writer = " + bfw);
    }
    return pongSender;
  }

  public static PongSender createSender(PongController pc, Socket socket)
          throws IOException {
    return PongSender.createSender(pc, socket, -1);
  }

  // 送信する
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
      if (this.i == -1) {
        System.out.println("Closing : Sending Buffered Writer = " + this.bfw);
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