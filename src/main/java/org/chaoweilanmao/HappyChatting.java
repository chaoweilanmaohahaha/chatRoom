package org.chaoweilanmao;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class HappyChatting {
    private static Logger logger = Logger.getLogger(HappyChatting.class);

    public static void main(String args[]) {
        Vertx vertx = Vertx.vertx();

        HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
        HttpServer server = vertx.createHttpServer(options);
        Map<String, ServerWebSocket> userMap = new HashMap<>();

        server.webSocketHandler(webSocket->{
            logger.info("a host is connecting");
            if(webSocket.path().equals("/wudi")) {
                String url = webSocket.uri();
                String username = url.substring(url.indexOf('=')+1);
//                if(userMap.get(username) == null) {
//                    userMap.put(username, webSocket);
//                }

                // 这里应该每有一个用户上线之后，服务器会推送信息给所有的用户，在这里面也可以检测是否已经有用户下线
//                Iterator<Map.Entry<String, ServerWebSocket>> iter = userMap.entrySet().iterator();
//                while(iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry)iter.next();
//                    ServerWebSocket ws = userMap.get(entry.getKey());
//
//                    if(ws.isClosed()) {
//                        logger.info(entry.getKey() + "关闭了连接！");
//                        iter.remove();
//                        continue;
//                    }
//                }
//                iter = userMap.entrySet().iterator();
//                while(iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry)iter.next();
//                    ServerWebSocket ws = userMap.get(entry.getKey());
//                    JsonObject json = new JsonObject();
//                    json.put("users", userMap.keySet());
//                }

                // 如果前端刷新了页面，那么也需要刷新这个用户的 websocket
                userMap.put(username, webSocket);
                webSocket.handler(Buffer->{
                   String input = Buffer.getString(0, Buffer.length());
                   Iterator<Map.Entry<String, ServerWebSocket>> useriter = userMap.entrySet().iterator();
                   while(useriter.hasNext()) {
                       Map.Entry entry = (Map.Entry)useriter.next();
                       ServerWebSocket ws = userMap.get(entry.getKey());

                       // 如果前端关闭了连接
                       if(ws.isClosed()) {
                           logger.info(entry.getKey() + "关闭了连接！");
                           useriter.remove();
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
                logger.info("server start successfully");
            } else {
                logger.error("server start faild");
            }
        });
    }
}
