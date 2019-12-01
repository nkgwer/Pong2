package com.o_char.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Random;
import java.util.StringTokenizer;

// ボール
public class Ball extends Rectangle {
  /**
   * 速度.
   */
  private Dimension velocity;
  private boolean isVisible;
  private Color color;

  /**
   * Constructs a new <code>Rectangle</code> whose upper-left corner is
   * specified as
   * {@code (x,y)} and whose width and height
   * are specified by the arguments of the same name.
   *
   * @param nx      the specified X coordinate
   * @param ny      the specified Y coordinate
   * @param nwidth  the width of the <code>Rectangle</code>
   * @param nheight the height of the <code>Rectangle</code>
   * @since 1.0
   */
  public Ball(int nx, int ny, int nwidth, int nheight) {
    super(nx, ny, nwidth, nheight);
    this.velocity = new Dimension(0, 0);
    Random rnd = new Random();
    this.color = new Color(rnd.nextInt(155) + 50,
            rnd.nextInt(155) + 50,
            rnd.nextInt(155) + 50);
  }

  /**
   * Constructor.
   *
   * @param s ボールの情報が入っている文字列.
   */
  public Ball(String s) {
    StringTokenizer st = new StringTokenizer(s, " ");
    st.nextToken();
    int nx = Integer.parseInt(st.nextToken());
    int ny = Integer.parseInt(st.nextToken());
    int nwidth = Integer.parseInt(st.nextToken());
    int nheight = Integer.parseInt(st.nextToken());
    int nvwidth = Integer.parseInt(st.nextToken());
    int nvheight = Integer.parseInt(st.nextToken());
    this.setRect(nx, ny, nwidth, nheight);
    this.velocity = new Dimension(nvwidth, nvheight);
    this.color = new Color(Integer.parseInt(st.nextToken()),
            Integer.parseInt(st.nextToken()),
            Integer.parseInt(st.nextToken()));
  }

  public void setVelocity(Dimension nv) {
    velocity.width = nv.width;
    velocity.height = nv.height;
  }

  public void setVelocity(int nwidth, int nheight) {
    velocity.width = nwidth;
    velocity.height = nheight;
  }

  public void setVX(int nwidth) {
    velocity.width = nwidth;
  }

  public void setVY(int nheight) {
    velocity.height = nheight;
  }

  public void setVisible(boolean b) {
    this.isVisible = b;
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

  public int getR() {
    return this.color.getRed();
  }

  public int getG() {
    return this.color.getGreen();
  }

  public int getB() {
    return this.color.getBlue();
  }

  public boolean getVisible() {
    return this.isVisible;
  }

  public void translate() {
    translate(velocity.width, velocity.height);
  }

  /**
   * 次の時間でのボールの位置.
   */
  public Ball next() {
    return new Ball(x + velocity.width, y + velocity.height, width, height);
  }

  public void boundX() {
    velocity.width = -velocity.width;
  }

  public void boundY() {
    velocity.height = -velocity.height;
  }

  /**
   * Ball を表す文字列を返す.
   * @return Ball を表す文字列.
   */
  public String toString() {
    Color color = this.color;
    return "Ball: " + this.x + " " + this.y + " " + this.width + " " + this.height + " " + this.velocity.width + " "
            + this.velocity.height + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue();
  }
}
