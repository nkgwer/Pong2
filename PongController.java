public abstract class PongController {
	String userName;
	Integer number;

	// 初期化
	abstract void initialize();

	// スタート画面のボタンが押されるまで待つ
	abstract void waitBtnPushed();

	// pongReceiverで受信した文字列に対する処理
	abstract void receive(String s, int ri);

	// ボールの情報を送信する
	abstract void sendBall(int n, Ball bl);

	// 接続を終了する
	abstract void terminateConnection(int i);
}
