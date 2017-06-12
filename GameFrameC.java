import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JPanel;

// PongClient向けゲーム画面用クラス
public class GameFrameC extends GameFrame {
	Container cont;
	JPanel p;

	public GameFrameC(PongController npc) {
		super(npc);

		// パネル
		p = new JPanel();
		p.setLayout(null);

		// コンテントペイン
		cont = this.getContentPane();
		cont.add(p, BorderLayout.CENTER);

		this.init();
	}
}