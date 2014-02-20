import java.net.*;
import java.io.*;
import java.util.regex.*;

class Client {
	public static void main(String[] args) {
		try {
			//リクエストを取得
			System.out.println("Request:");
			String C_request = new BufferedReader(new InputStreamReader(System.in)).readLine();
			Browse b = new Browse();
			b.SetRequest(C_request);
			while(true){
				b.ShowContents();
				b.ShowLinks();
				b.GoToLinks();
			}
		} catch (UnknownHostException e) {
			System.out.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("IO exception.");
			System.exit(1);
		}
	}
}


class Browse {
	static String[] links = new String[4096];
	static int linkSize = 1;
	static String request;
	
	public static void SetRequest(String C_request){
		request = C_request;
	}	

	public static void ShowContents(){
		try {
			int port = 80;
			String hostname;
			//最初の/で分割
			String str2 = request.substring(7);
			int index = str2.indexOf("/");
			//ホスト名とポート番号取得
			String str3 = str2.substring(0, index);
			String[] str4 = str3.split(":");
			if (str4.length == 1) {
				hostname = str4[0];
			} else {
				hostname = str4[0];
				port = Integer.parseInt(str4[1]);
			}
			//ファイル名取得
			String filename = str2.substring(index);
			//現在地取得
			int cur_index = filename.lastIndexOf("/");
			String current = filename.substring(0, cur_index+1);

			//サーバーに送るコマンドを作成
			String command = "GET " + filename + " HTTP/1.1\nHost: " + hostname + ":" + String.valueOf(port) + "\n";
			//コマンドを出力
			System.out.println(command);


			Socket s = new Socket(hostname, port);

			//サーバーに対してリクエストを送信
			PrintStream toServer = new PrintStream(s.getOutputStream(), true); 
			toServer.println(command);			

			//sに受信したデータが入っているから、それを読み込んでinに代入
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			//aタグにマッチする正規表現
			Pattern pat = Pattern.compile("<a href=" + ".*?" + "</a>");
			//String型のlineとリンクの番号を記憶するiを定義
			linkSize= 1;
			String line;
			//inを一行読み込んで出力する
			while (null != (line = in.readLine())){
				//一行を出力
				System.out.println(line);
				//matを定義する
				Matcher mat = pat.matcher(line);
				//正規表現にマッチするものがあるかどうかを調べる
				while(mat.find()) {
					//リンクを保持する
					links[linkSize] = mat.group(0);
					linkSize++;
				}
			}
			in.close();

			//リンクの編集
			String[] t_links = new String[256];
			for(int l=1; l<linkSize; l++){
				//aタグの中からファイル名だけを取り出す
				t_links = links[l].split("\"");
				links[l] = t_links[1];
				//http://を付け加える
				if(!(links[l].startsWith("http://"))){
					links[l] = "http://" + hostname + current + links[l];	
				}
			}
		} catch (IOException e) {}
	}

	public static void ShowLinks(){	
		//リンクを出力
		System.out.println("\n[List Of links]");
		for(int l=1; l<linkSize; l++){
			System.out.println(l + ". " + links[l]);
		}
	}

	public static void GoToLinks(){
		try{
			//リンク先を指定
			System.out.println("リンクの番号を入力:");
			//入力を読み込む
			String req_link = new BufferedReader(new InputStreamReader(System.in)).readLine();
			request = null;
			for(int l=1; l<linkSize; l++){
				if(l == Integer.parseInt(req_link)){
					request = links[l];
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception.");
			System.exit(1);
		}
	}
}



