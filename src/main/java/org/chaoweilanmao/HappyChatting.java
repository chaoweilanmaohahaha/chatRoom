package org.chaoweilanmao;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;

import java.util.HashMap;
import java.util.Iterator;
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
                String username = url.substring(url.indexOf('=')+1);
//                if(userMap.get(username) == null) {
//                    userMap.put(username, webSocket);
//                }

                // 如果前端刷新了页面，那么也需要刷新这个用户的 websocket
                userMap.put(username, webSocket);
                webSocket.handler(Buffer->{
                   String input = Buffer.getString(0, Buffer.length());
                   Iterator<Map.Entry<String, ServerWebSocket>> iter = userMap.entrySet().iterator();
                   while(iter.hasNext()) {
                       Map.Entry entry = (Map.Entry)iter.next();
                       ServerWebSocket ws = userMap.get(entry.getKey());

                       // 如果前端关闭了连接
                       if(ws.isClosed()) {
                           System.out.print(entry.getKey() + "关闭了连接！");
                           iter.remove();
                           continue;
                       }
                       ws.writeTextMessage(input);
                   }
                });
            } else {
                webSocket.reject();
            }
        });

        server.listen(12345, res->{
            if(res.succeeded()) {
                System.out.println("successfully");
            } else {
                System.out.println("failed");
            }
        });
    }
}
