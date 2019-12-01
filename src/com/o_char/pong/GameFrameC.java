package com.o_char.pong;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.StringTokenizer;
import javax.swing.JPanel;

// PongClient向けゲーム画面用クラス
public final class GameFrameC extends GameFrame {
  private Container container;
  private JPanel panel;

  public GameFrameC(int n, int nid, PongController npc) {
    super(n, npc);

    // パネル
    this.panel = new JPanel();
    this.panel.setLayout(null);

    // コンテントペイン
    this.container = this.getContentPane();
    this.container.add(this.panel, BorderLayout.CENTER);

    id = nid + 2;

    this.init();
  }

  public synchronized void receivePoint(String s) {
    StringTokenizer st = new StringTokenizer(s, " ");
    st.nextToken();
    int nid = Integer.parseInt(st.nextToken());
    int newPoint = Integer.parseInt(st.nextToken());
    if (nid != id) {
      this.point[nid - 1] = newPoint;
    }
  }

  public synchronized void sendPoint(int id, int point) {
    this.pongController.sendPoint(0, "Point: " + id + " " + point);
  }
}
