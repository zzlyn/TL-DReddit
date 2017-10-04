package control;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import webInterface.CustomHttpHandler;

public class MainRun {

	public static void main(String[] args) {
		
		// Establish a local HTTP server
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error establishing local HTTP server.");
			return;
		}
		
		
		server.createContext("/analyze", new CustomHttpHandler());
		server.start();

	}

}
