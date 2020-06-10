package org.kframe.scheduling;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;

public class WesocketClient {
	public static void main(String[] args) throws InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();
		WebSocket socket = httpClient.newWebSocketBuilder().buildAsync(URI.create(
				"ws://localhost:10510/ws/wsgame?appagent=skywar%2F0.0.6%3B%20web%2F1.0%3B%20chrome%2F83.0.4103.97%3B%20360*640*1%3B&bean=%7B%22autologin%22%3Atrue%2C%22agencyid%22%3A0%2C%22cookieinfo%22%3A%2242440bd667590524d06c3f2684a66d725370d760dba68f74a6f62e2aea7e002af2731f750f0db46fdb896ac2e423ebd42b30f08df427db017175e0106f84ed437bdb6d91afa882093eab2d94f96c6b5c82a60dd8fdd188668109690cc82719f7b67d337ecd921e7ed4abd0f5754ea27d%22%2C%22account%22%3A%22%22%2C%22password%22%3A%22%22%2C%22netmode%22%3A%22web%22%2C%22appos%22%3A%22web%22%2C%22apptoken%22%3A%2249deee7a-7d08-4247-8e31-ba005296b474%22%7D"),
				new Listener() {
					@Override
					public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
						// TODO Auto-generated method stub
						System.out.println(data);
						return Listener.super.onText(webSocket, data, last);
					}
				}).join();
		// 发送 数据
		socket.sendText("{\"message\":{\"module\":\"duty\",\"action\":\"query\",\"params\":\"\",\"http_seqno\":\"hsn15917833289953829\"}}", true);
		Thread.sleep(10000 * 10);
	}
}
