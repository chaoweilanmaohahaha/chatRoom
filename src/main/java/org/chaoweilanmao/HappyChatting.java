package org.chaoweilanmao;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;

import java.util.HashMap;
import java.util.Map;


public class HappyChatting {
    public static void main(String args[]) {
        Vertx vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
        HttpServer server = vertx.createHttpServer(options);
        Map<String, ServerWebSocket> userMap = new HashMap<>();

        server.webSocketHandler(webSocket->{
            System.out.println("Connected!");
            if(webSocket.path().equals("/wudi")) {
                String url = webSocket.uri();
                String username = url.substring(url.indexOf('='), url.length());
                if(userMap.get(username) == null) {
                    userMap.put(username, webSocket);
                }
                webSocket.handler(Buffer->{
                   String input = Buffer.getString(0, Buffer.length());
                   for(String entry: userMap.keySet()) {
                       ServerWebSocket ws = userMap.get(entry);
                       ws.writeTextMessage(input);
                   }
                });
            } else {
                webSocket.reject();
            }
        });

        server.listen(12345, "localhost", res->{
            if(res.succeeded()) {
                System.out.println("successfully");
            } else {
                System.out.println("failed");
            }
        });
    }
}
