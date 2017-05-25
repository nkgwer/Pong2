import java.awt.Dimension;
import java.awt.Rectangle;

// バー
public class Bar extends Rectangle {
	private Dimension v;

	public Bar (int nx, int ny, int nwidth, int nheight) {
		super(nx, ny, nwidth, nheight);
		v = new Dimension(0, 0);
	}

	public void setV (Dimension nv) {
		v.width = nv.width;
		v.height = nv.height;
	}

	public void setV (int nwidth, int nheight) {

		v.width = nwidth;
		v.height = nheight;
	}
	
	public void setVX (int nwidth) {
		v.width = nwidth;
	}
	
	public void setVY (int nheight) {
		v.height = nheight;
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

	public void translate() {
		translate(v.width, v.height);
	}

	// 次の時間でのバーの位置
	public Bar Next() {
		return new Bar(x + v.width, y + v.height, width, height);
		
	}

	public void BoundX() {
		v.width = -v.width;
	}

	public void BoundY() {
		v.height = -v.height;
	}
}