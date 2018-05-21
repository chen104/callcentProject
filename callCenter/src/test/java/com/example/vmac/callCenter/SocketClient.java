package com.example.vmac.house;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends Thread {
	private int i;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		for (int i = 0; i < 1; i++) {
			new SocketClient(i).start();
		}
	}

	public SocketClient(int i) {
		this.i = i;
	}

	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Socket socket;
		try {
			// socket = new Socket("127.0.0.1", 7999);
			socket = new Socket("chen-104", 8081);
			OutputStream out = socket.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			if (i % 3 == 0) {
				writer.write("713375GM109");
				writer.write("\n");
			} else {
				writer.write("713375GM108");
				writer.write("\n");
			}
			writer.flush();
			InputStream in = socket.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String re = read.readLine();
			System.out.println(i + "  收到  " + re);
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doRequest() throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1", 8001);
		OutputStream out = socket.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write("713375GM108");
		writer.flush();
		InputStream in = socket.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(in));
		String re = read.readLine();
		System.out.println("返回" + re);
		socket.close();
	}
}
