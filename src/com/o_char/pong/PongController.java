package com.o_char.pong;

public abstract class PongController {
  String userName;
  Integer number; // number of player

  // 初期化
  abstract void initialize();

  // スタート画面のボタンが押されるまで待つ
  abstract void waitBtnPushed();

  // pongReceiverで受信した文字列に対する処理
  abstract void receive(String s, int ri);

  // ボールの情報を送信する
  abstract void sendBall(int n, Ball bl);

  // 得点の情報を送信する
  abstract void sendPoint(int i, String points);

  // 勝敗を送信する
  public void sendIsWin(int i, String s) {
  }

  // 接続を終了する
  abstract void terminateConnection(int i);
}
