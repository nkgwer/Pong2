import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JPanel;

// PongServer向けゲーム画面用クラス
public class GameFrameS extends GameFrame implements ActionListener {
    Container cont;
    JPanel p;
    JButton btn;

    public GameFrameS () {
        super();

        // スタートボタン
        btn = new JButton("Start!");
        btn.setPreferredSize(new Dimension(60, 30));
        btn.addActionListener(this);

        // スタートボタンが乗るパネル
        p = new JPanel();
        p.setLayout(null);
        p.add(btn);
        btn.setBounds(110, 200, 180, 120);

        // コンテントペイン
        cont = getContentPane();
        cont.add(p, BorderLayout.CENTER);

        // 最初はボールがある。
        isBallHere = true;
    }

    public void actionPerformed(ActionEvent e) {
        btn.setEnabled(false);
        btn.setVisible(false);
        init();
    }

    public void init() {
        super.init();
    }

    public synchronized void paint(Graphics g) {
        super.paint(g);
    }
}
