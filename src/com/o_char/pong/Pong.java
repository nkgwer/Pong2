package com.o_char.pong;

public final class Pong {
  private Pong() {
  }

  /**
   * main function.
   *
   * @param args 実行時の引数.
   */
  public static void main(final String[] args) {
    SelectFrame selectFrame = new SelectFrame();
    selectFrame.setVisible(true);
  }
}
