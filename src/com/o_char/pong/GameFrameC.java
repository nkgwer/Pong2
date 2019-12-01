package com.o_char.pong;

import java.awt.BorderLayout;
import java.util.StringTokenizer;
import javax.swing.JPanel;

/**
 * PongClient 向けゲーム画面用クラス.
 */
public final class GameFrameC extends GameFrame {
  /**
   * Constructor.
   *
   * @param n   設定する n.
   * @param nid 設定する id.
   * @param npc 設定する PongController.
   */
  public GameFrameC(int n, int nid, PongController npc) {
    super(n, npc);
    // パネルを設定して追加する.
    JPanel panel = new JPanel();
    panel.setLayout(null);
    this.getContentPane().add(panel, BorderLayout.CENTER);

    this.id = nid + 2;
    this.init();
  }

  /**
   * 点を受け取る.
   * @param s 受け取る情報が含まれた文字列.
   */
  public synchronized void receivePoint(String s) {
    StringTokenizer st = new StringTokenizer(s, " ");
    st.nextToken();
    int nid = Integer.parseInt(st.nextToken());
    int newPoint = Integer.parseInt(st.nextToken());
    if (nid != this.id) {
      this.point[nid - 1] = newPoint;
    }
  }

  public synchronized void sendPoint(int id, int point) {
    this.pongController.sendPoint(0, "Point: " + id + " " + point);
  }
}
