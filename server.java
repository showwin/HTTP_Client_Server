import java.net.*;
import java.io.*;

class Server {
	public static void main(String[] args) {
		if (args.length < 1) return;
		try {
			//ソケットを開く
			//ここでは接続待ち状態
			ServerSocket serverS = new ServerSocket(Integer.parseInt(args[0]));
			while (true) {
				//クライアントからの接続が行われればserverS.accept()によっ確立される
				//Threadクラスのインスタンスを生成し、新たなスレッド上で一つのサーバーを実行する
				new ServerThread(serverS.accept()).start();
				System.out.println("New connection.");
				//このあともとのスレッドでは再び待ち状態になる
			}
		} catch  (IOException e) {
			System.out.println("IO exception.");
			System.exit(1);
		}
	}
}

class ServerThread extends Thread {
	Socket clientS;

	//clientSの設定
	public ServerThread(Socket acceptedS) {
		clientS = acceptedS;
	}

	//startメソッドによってrunが実行される
	public void run() {
		try {
			//サーバーからリクエストを受け取る
			BufferedReader in = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
			//リクエストを分解する
			String[] str = (in.readLine()).split(" /");
			String[] str1 = str[1].split(" ");
			String command = str[0];
			String filename = str1[0];
			String[] str2 = filename.split("\\.");
			String ext = str2[1];

			//commandがGETかどうか判断する
			if (!(command.equals("GET"))){
				System.out.println("正しくないクエリです。\n");	
			}

			//接続が確立したソケットclientSへのデータ送信を行うストリームPrintStreamを定義
			PrintStream toClient = new PrintStream(clientS.getOutputStream(), true);
	
			//ヘッダを送信
			String header = "HTTP/1.1 200 OK\nContent-Type: text/" + ext + "; charset=iso-2022-jp\n";
			toClient.println(header);

			//送る文字を保存するためのlineを定義する
			String line;
			//指定されたファイルの中身をfileinとする
			BufferedReader filein = new BufferedReader(new FileReader(filename));
			//fileinを一行ずつ読んでlineに代入する
			while (null != (line = filein.readLine())) {
				//内容を出力
				toClient.println(line);
			}
			filein.close();
			System.out.println(filename + " was sent.\n");
		} catch  (IOException e) {
			System.out.println("IO exception.");
			System.exit(1);
		}
	}
}
