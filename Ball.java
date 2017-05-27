import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Random;
// ボール
public class Ball extends Rectangle {
	private Dimension v;
	private boolean isVisible;
	private Color c;
	private int r,g,b;
	public Ball(int nx, int ny, int nwidth, int nheight) {
		super(nx, ny, nwidth, nheight);
		this.v = new Dimension(0, 0);
		Random rnd = new Random();
		this.r = rnd.nextInt(155)+100;
		this.g = rnd.nextInt(155)+100;
		this.b = rnd.nextInt(155)+100;
		this.c = new Color(this.r,this.g,this.b);
	}

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
		this.v = new Dimension(nvwidth, nvheight);
		this.r = Integer.parseInt(st.nextToken());
		this.g = Integer.parseInt(st.nextToken());
		this.b = Integer.parseInt(st.nextToken());
		this.c = new Color(r,g,b);
	}

	public void setV(Dimension nv) {
		v.width = nv.width;
		v.height = nv.height;
	}

	public void setV(int nwidth, int nheight) {
		v.width = nwidth;
		v.height = nheight;
	}

	public void setVX(int nwidth) {
		v.width = nwidth;
	}

	public void setVY(int nheight) {
		v.height = nheight;
	}

	public void setVisible(boolean b) {
		this.isVisible = b;
	}

	public Dimension getV() {
		return v;
	}

	public int getVX() {
		return v.width;
	}

	public int getVY() {
		return v.height;
	}
	public int getR(){
		return this.r;
	}
	public int getG(){
		return this.g;
	}
	public int getB(){
		return this.b;
	}

	public boolean getVisible() {
		return this.isVisible;
	}

	public void translate() {
		translate(v.width, v.height);
	}

	// 次の時間でのボールの位置
	public Ball Next() {
		return new Ball(x + v.width, y + v.height, width, height);
	}

	public void BoundX() {
		v.width = -v.width;
	}

	public void BoundY() {
		v.height = -v.height;
	}

	public String toString() {
		return "Ball: " + this.x + " " + this.y + " " + this.width + " " + this.height + " " + this.v.width + " "
				+ this.v.height + " "+ this.r + " " + this.g + " " + this.b;
	}
}