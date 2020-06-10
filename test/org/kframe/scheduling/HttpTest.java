package org.kframe.scheduling;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;



public class HttpTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		HttpServerProvider provider = HttpServerProvider.provider();
		
		HttpServer server = provider.createHttpServer(new InetSocketAddress("127.0.0.1", 90), 1024);
		
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				String requestMethod = exchange.getRequestMethod();
				if (requestMethod.equalsIgnoreCase("GET")) {
					Headers responseHeaders = exchange.getResponseHeaders();
					responseHeaders.set("Content-Type", "text/plain;charset=UTF-8");
					exchange.sendResponseHeaders(200, 0);
					OutputStream responseBody = exchange.getResponseBody();
					Headers requestHeaders = exchange.getRequestHeaders();
					Set<String> keySet = requestHeaders.keySet();
					Iterator<String> iter = keySet.iterator();
					while (iter.hasNext()) {
						String key = iter.next();
						List values = requestHeaders.get(key);
						String s = key + " = " + values.toString() + "\n";
						responseBody.write(s.getBytes());
					}
					responseBody.write("jdk自带轻量级http server例子".getBytes());
					responseBody.close();
				}

			}
		});
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

	}
}
