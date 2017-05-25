import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// スタート画面用クラス
public class StartFrame extends JFrame implements ActionListener {
    /* スタート画面のタイトル */
    private static final String FRAME_TITLE = "Pong!";
    /* スタート画面のサイズ */
    private static final Dimension FRAME_SIZE = new Dimension(640, 360);
    static final Dimension LABEL_SIZE = new Dimension(120, 30);
    static final Dimension BUTTON_SIZE = new Dimension(60, 30);

    Container container;
    JLabel upperLabel, label1, label2;
    final JTextField textField1, textField2;
    final JTextArea log;
    final JScrollPane scrollpane;
    JButton btn;
    boolean isBtnPushed; // ボタンが押されたかどうか

    private MenuBar menuBar;
    private Menu[] menu;
    private final String[] menuString = {"Window"};
    MenuItem[] menuItem;
    private final String[] menuItemString = {"Create Server", "Create Client"};

    public StartFrame () {
        this.setTitle(FRAME_TITLE); // Settting Title
        this.setSize(FRAME_SIZE); // サイズの設定
        this.setResizable(false); //サイズを固定
        this.setLocationRelativeTo(null); // 位置を中央に設定
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ×を押すとプログラムが終了
        this.isBtnPushed = false;

        // Menu bar
        this.menuBar = new MenuBar();

        // Menu
        this.menu = new Menu[this.menuString.length];
        for (int i = 0; i < this.menuString.length; i++) {
            this.menu[i] = new Menu(menuString[i]);
        }

        // menu item
        this.menuItem = new MenuItem[this.menuItemString.length];
        for (int i = 0; i < this.menuItemString.length; i++) {
            this.menuItem[i] = new MenuItem(menuItemString[i]);
            this.menuItem[i].addActionListener(this);
            this.menu[0].add(this.menuItem[i]);
        }

        for (int i = 0; i < this.menuString.length; i++) {
            this.menuBar.add(this.menu[i]);
        }

        setMenuBar(this.menuBar);

        // Setting labels
        this.upperLabel = new JLabel();
        this.label1 = new JLabel("Player's Name:");
        this.label2 = new JLabel();
        this.label1.setPreferredSize(LABEL_SIZE);
        this.label2.setPreferredSize(LABEL_SIZE);

        // 名前のテキストフィールド
        this.textField1 = new JTextField(); // Text Field of User Name
        this.textField2 = new JTextField(); // 自分も含めた対戦人数のコンボボックス
        this.textField1.setPreferredSize(LABEL_SIZE);
        this.textField2.setPreferredSize(LABEL_SIZE);

        // ログ: 通信状態, 参加者を表示, スクロール可能
        this.log = new JTextArea();
        this.log.setEditable(false);
        this.scrollpane = new JScrollPane(log);
        this.scrollpane.setPreferredSize(new Dimension(400, 180));

        // 入力完了ボタン
        this.btn = new JButton("Start!");
        this.btn.setPreferredSize(BUTTON_SIZE);
        this.btn.addActionListener(this);

        this.container = this.getContentPane();
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == this.btn) {
            this.btn.setEnabled(false);
            this.textField1.setEditable(false);
            this.textField2.setEditable(false);
            this.isBtnPushed = true;
        } else if (obj == this.menuItem[0]) {
            PongServer server = new PongServer();
            if (server.sf != null) {
                Thread thread = new Thread(server);
                thread.start();
            }
        } else if (obj == this.menuItem[1]) {
            PongClient client = new PongClient();
            Thread thread = new Thread(client);
            thread.start();
        }
    }

    // ログに文字列を表示する
    public void logAppendln (String str) {
        System.out.println("ログに表示: " + str);
        this.log.append(str + "\n");
    }
}
