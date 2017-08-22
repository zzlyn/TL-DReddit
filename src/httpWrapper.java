package src;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class httpWrapper {
	
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/analyze", new MyHandler());
		server.start();
	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response ="";
			String query = t.getRequestURI().getQuery();
			
			HashMap<String, String> components = (HashMap<String, String>) queryToMap(query);
			
			String postUrl = components.get("postUrl");
			
			System.out.println(postUrl);
		
			try {
				response = Analyzer.urlListen(postUrl);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

			if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
				t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
				t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
				t.sendResponseHeaders(204, -1);
				return;
			}

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
	
	public static Map<String, String> queryToMap(String query){
	    Map<String, String> result = new HashMap<String, String>();
	    for (String param : query.split("&")) {
	        String pair[] = param.split("=");
	        if (pair.length>1) {
	            result.put(pair[0], pair[1]);
	        }else{
	            result.put(pair[0], "");
	        }
	    }
	    return result;
	}

}