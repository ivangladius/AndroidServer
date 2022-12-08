
package jsonserver;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

import org.json.JSONObject;

public class Server {

	private int port;

	private ServerSocket serverSocket;

	private ThreadPoolExecutor executor;

	public Server(int port, int threadPoolSize) {

		this.port = port;

		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started...");
		} catch (IOException e) {
			stop();
			e.printStackTrace();
		}
	}

	public void loop() {
		for (;;) {
			try {
				System.out.println("Waiting for Connections..");
				Socket clientSocket = serverSocket.accept();
				executor.execute(new MyRunnable(this, clientSocket));
			} catch (IOException ignore) {
			}
		}
	}

	public void handle_connection(Socket cSocket) {

		System.out.println("\nClient connected: " + cSocket.getRemoteSocketAddress().toString());

		PrintWriter out;
		BufferedReader in;
		try {
			// out = new ObjectOutputStream(cSocket.getOutputStream());
			// in = new ObjectInputStream(cSocket.getInputStream());

			// out = new OutputStreamWriter(cSocket.getOutputStream(),
			// StandardCharsets.UTF_8);
			out = new PrintWriter(cSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

			String result = in.readLine();

			int i = result.indexOf("{");
			result = result.substring(i);
			JSONObject json = new JSONObject(result.trim());
			System.out.println(json.toString(4));

//			JSONObject jsonRequest = new JSONObject(jsonRequestString);
//			JSONObject jsonReply = new JSONObject();
			System.out.println("operation: " + json.get("operation"));

			// answer
			JSONObject reply = new JSONObject();
			reply.put("key", json.get("key"));
			reply.put("operation", json.get("operation"));

			String operation = json.get("operation").toString();
			if (operation.equals("getUsername"))
				reply.put("payload", getUsername(json.get("key").toString()));
			else
				reply.put("payload", "");

			out.println(reply.toString());
			System.out.println("data send back");

			out.close();
			in.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeClient(cSocket);
	}

	private void closeClient(Socket cSocket) {
		try {
			cSocket.close();
		} catch (IOException ignore) {
		}
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public String getUsername(String key) {

		System.out.println("Username requested\n");

		return "Hans Peter";
	}

	public void replyError(PrintWriter out) {
		out.print("Error");
	}
}

class MyRunnable implements Runnable {

	private Server server;
	private Socket client;

	// passing original server object to this constructor
	public MyRunnable(Server server, Socket client) {
		this.server = server;
		this.client = client;
	}

	public void run() {
		this.server.handle_connection(this.client);
		// long threadId = Thread.currentThread().getId();
		// System.out.print("ID: " + threadId + " ");
	}
}
