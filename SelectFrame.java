import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// 選択画面用クラス
public class SelectFrame extends JFrame implements ActionListener {
	/* スタート画面のタイトル */
	private static final String FRAME_TITLE = "Pong!";
	/* スタート画面のサイズ */
	private static final Dimension FRAME_SIZE = new Dimension(640, 480);
	static final Dimension BUTTON_SIZE = new Dimension(120, 60);

	private Container container;
	// private JLabel upperLabel;
	private JLabel label;
	private JPanel lowerPanel;
	private JButton[] btn;
	private MenuBar menuBar;
	private Menu[] menu;
	private final String[] menuString = { "Window" };
	private MenuItem[] menuItem;
	private final String[] menuItemString = { "Create Server", "Create Client" };

	public SelectFrame() {
		this.setTitle(FRAME_TITLE); // Settting Title
		this.setSize(FRAME_SIZE); // サイズの設定
		this.setResizable(false); // サイズを固定
		this.setLocationRelativeTo(null); // 位置を中央に設定
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ×を押すとプログラムが終了

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

		// upper label
		// this.upperLabel = new JLabel("Select which to start.");
		// this.upperLabel.setFont(new Font("", Font.PLAIN, 28));

		this.btn = new JButton[2];

		// Button to start server
		this.btn[0] = new JButton("Server");
		this.btn[0].setPreferredSize(BUTTON_SIZE);
		this.btn[0].addActionListener(this);

		// Button to start client
		this.btn[1] = new JButton("Client");
		this.btn[1].setPreferredSize(BUTTON_SIZE);
		this.btn[1].addActionListener(this);

		for (int i = 0; i < this.btn.length; i++) {
			this.btn[i].setFont(new Font("", Font.PLAIN, 40));
		}

		ClassLoader cl = this.getClass().getClassLoader();
		ImageIcon icon = new ImageIcon(cl.getResource("./image/title.jpg"));
		// ImageIcon icon = new ImageIcon("./image/title.jpg");
        this.label = new JLabel(icon);

        this.lowerPanel = new JPanel();
        this.lowerPanel.setLayout(new GridLayout(1, this.btn.length));
		for (int i = 0; i < this.btn.length; i++) {
			this.lowerPanel.add(this.btn[i]);
		}

		// container
		this.container = this.getContentPane();
		// this.container.add(this.upperLabel, BorderLayout.NORTH);
		this.container.add(this.lowerPanel, BorderLayout.SOUTH);
		this.container.add(this.label, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		for (int i = 0; i < this.btn.length; i++) {
			this.btn[i].setEnabled(false);
		}
		System.out.println("Closing: Select Frame");
		this.setVisible(false);
		if (obj == this.btn[0]) {
			PongServer server = new PongServer();
			if (server.sFrame != null) {
				Thread thread = new Thread(server);
				thread.start();
			}
		} else if (obj == this.btn[1]) {
			PongClient client = new PongClient();
			Thread thread = new Thread(client);
			thread.start();
		} else if (obj == this.menuItem[0]) {
			PongServer server = new PongServer();
			if (server.sFrame != null) {
				Thread thread = new Thread(server);
				thread.start();
			}
		} else if (obj == this.menuItem[1]) {
			PongClient client = new PongClient();
			Thread thread = new Thread(client);
			thread.start();
		}
	}

}
