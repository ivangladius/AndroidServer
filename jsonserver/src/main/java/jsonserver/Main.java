package jsonserver;


public class Main {

	public static void main(String[] args) {

		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

		server.start();

		server.loop();

		server.stop();
	}

}
