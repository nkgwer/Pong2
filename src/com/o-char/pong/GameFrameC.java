package com.o-char.pong;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.StringTokenizer;
import javax.swing.JPanel;

// PongClient向けゲーム画面用クラス
public final class GameFrameC extends GameFrame {
	private Container cont;
	private JPanel p;

	public GameFrameC(int n, int nid, PongController npc) {
		super(n, npc);

		// パネル
		p = new JPanel();
		p.setLayout(null);

		// コンテントペイン
		cont = this.getContentPane();
		cont.add(p, BorderLayout.CENTER);

		id = nid + 2;

		this.init();
	}

	public synchronized void receivePoint(String s) {
		StringTokenizer st = new StringTokenizer(s, " ");
		st.nextToken();
		int nid = Integer.parseInt(st.nextToken());
		int npoint = Integer.parseInt(st.nextToken());
		if (nid != id) point[nid - 1] = npoint;
	}
	
	public synchronized void sendPoint(int id, int point) {
		this.pongController.sendPoint(0, "Point: " + id + " " + point);
	}
}