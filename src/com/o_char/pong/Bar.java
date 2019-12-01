package com.o_char.pong;

import java.awt.Dimension;
import java.awt.Rectangle;

// バー
public final class Bar extends Rectangle {
  private Dimension velocity;

  public Bar(final int nx, final int ny, final int newWidth, final int newHeight) {
    super(nx, ny, newWidth, newHeight);
    velocity = new Dimension(0, 0);
  }

  public void setVelocity(Dimension newVelocity) {
    velocity.width = newVelocity.width;
    velocity.height = newVelocity.height;
  }

  public void setVelocity(int newX, int newY) {
    velocity.width = newX;
    velocity.height = newY;
  }

  public void setVelocityX(int nwidth) {
    velocity.width = nwidth;
  }

  public void setVelocityY(int nheight) {
    velocity.height = nheight;
  }

  public Dimension getVelocity() {
    return velocity;
  }

  public int getVelocityX() {
    return velocity.width;
  }

  public int getVelocityY() {
    return velocity.height;
  }

  public void translate() {
    translate(velocity.width, velocity.height);
  }

  // 次の時間でのバーの位置
  public Bar next() {
    return new Bar(x + velocity.width, y + velocity.height, width, height);

  }

  public void boundX() {
    velocity.width = -velocity.width;
  }

  public void boundY() {
    velocity.height = -velocity.height;
  }
}
