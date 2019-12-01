package com.o_char.pong;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JPanel;

// PongServer向けゲーム画面用クラス
public final class GameFrameS extends GameFrame implements ActionListener {
  JButton btn;

  public GameFrameS(int n, PongController npc) {
    super(n, npc);

    // スタートボタン
    btn = new JButton("Start!");
    btn.setPreferredSize(new Dimension(60, 30));
    btn.addActionListener(this);

    // スタートボタンが乗るパネル
    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.add(this.btn);
    this.btn.setBounds(110, 200, 180, 120);

    // コンテントペイン.
    this.getContentPane().add(panel, BorderLayout.CENTER);

    // ボールの 左上角の座標, 幅, 高さ, 速度の設定
    this.ball[0] = new Ball(185, 1, 30, 30);
    this.ball[0].setVelocity(new Dimension((int) Math.ceil(3 * Math.random() - 1), 2));
    this.ball[0].setVisible(false);

    // 最初はボールがある。
    this.ball[0].setVisible(true);

    this.id = 1;
  }

  public void actionPerformed(ActionEvent e) {
    this.btn.setEnabled(false);
    this.btn.setVisible(false);
    init();
  }

  public void init() {
    Random rnd = new Random();
    for (int i = 0; i < this.pongController.number - 1; i++) {
      int vx = (int) Math.ceil(3 * Math.random() - 1);
      int r = rnd.nextInt(155) + 50;
      int g = rnd.nextInt(155) + 50;
      int b = rnd.nextInt(155) + 50;
      this.pongController.sendBall(i, new Ball("Ball: 185 1 30 30 " + vx + " 2" + " " + r + " " + g + " " + b));
    }
    super.init();
  }

  public void receivePoint(String s) {
    StringTokenizer st = new StringTokenizer(s, " ");
    st.nextToken();
    int nid = Integer.parseInt(st.nextToken());
    int npoint = Integer.parseInt(st.nextToken());
    point[nid - 1] = npoint;
    sendPoint(nid, npoint);
  }

  public synchronized void sendPoint(int id, int point) {
    for (int i = 0; i < this.pongController.number - 1; i++) {
      this.pongController.sendPoint(i, "Point: " + id + " " + point);
    }
  }

  public synchronized void terminateGame() {
    for (int i = 1; i < point.length; i++) {
      if (point[i] >= MAX_POINT) {
        this.pongController.sendIsWin(i - 1, "Win");
      } else {
        this.pongController.sendIsWin(i - 1, "Lose");
      }
    }
    if (point[0] >= MAX_POINT) {
      this.win();
    } else {
      this.lose();
    }
    for (int i = 0; i < this.pongController.number - 1; i++) {
      this.pongController.terminateConnection(i);
    }
  }
}
