import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.StringTokenizer;

// ボール
public class Ball extends Rectangle {
	private Dimension v;
	private boolean isVisible;

	public Ball(int nx, int ny, int nwidth, int nheight) {
		super(nx, ny, nwidth, nheight);
		this.v = new Dimension(0, 0);
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
				+ this.v.height;
	}
}