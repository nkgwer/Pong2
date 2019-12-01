package com.o-char.pong;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

// PongServer向けスタート画面: 自分の名前と、対戦人数を指定する。
public class StartFrameS extends StartFrame implements ActionListener {
	PongServer pongServer;
	JPanel p1, p2, p3;
	JComboBox<String> textField2;
	private final String[] COMBO_DATA = { "2", "3", "4", "5", "6", "7", "8" }; // コンボボックスの選択肢
	boolean isAccept; // 対戦相手を受付中

	public StartFrameS(PongServer nps) {
		super();
		this.pongServer = nps;
		this.isAccept = false;

		this.menuItem[0].setEnabled(false);

		// Setting labels
		this.upperLabel.setText("Input Your Name and Number of Players:");
		this.label2.setText("Number of Players:");

		// Setting combobox of number of players
		this.textField2 = new JComboBox<String>(this.COMBO_DATA); // 自分も含めた対戦人数のコンボボックス
		this.textField2.setPreferredSize(this.LABEL_SIZE);

		// JPanel p3: setting 2 labels, a textfield and a combobox
		this.p3 = new JPanel();
		this.p3.setLayout(new GridLayout(2, 2));
		this.p3.add(this.label1);
		this.p3.add(this.textField1);
		this.p3.add(this.label2);
		this.p3.add(this.textField2);

		// JPanel p2: setting p3 and log's scrollpane
		this.p2 = new JPanel();
		this.p2.setLayout(null);
		this.p2.add(this.p3);
		this.p2.add(this.scrollpane);
		this.p3.setBounds(0, 0, 400, 60);
		this.scrollpane.setBounds(0, 60, 400, 200);

		// JPanel p1: setting upper label, p2 and button
		this.p1 = new JPanel();
		this.p1.setLayout(null);
		this.p1.add(this.upperLabel);
		this.p1.add(this.p2);
		this.p1.add(this.btn);
		this.upperLabel.setBounds(120, 0, 400, 40);
		this.p2.setBounds(120, 30, 400, 260);
		this.btn.setBounds(290, 300, 60, 30);

		// container: setting p1
		this.container.add(this.p1, BorderLayout.CENTER);
	}

	// ボタンが押されたときの動作
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		Object obj = e.getSource();
		if (obj == this.btn) {
			this.textField2.setEnabled(false);
			this.upperLabel.setText("Waiting for players...");
		}
	}
}