package org.chaoweilanmao;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.eventbus.MessageConsumer;

public class ChatVertical extends AbstractVerticle {
    private HttpServer httpServer;

    public void start() {
        System.out.println("hello!!!");
        EventBus eb = vertx.eventBus();
        MessageConsumer<String> consumer = eb.consumer("welcome.to.chat");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
            // message.reply("how interesting!");
        });
    }

    public void stop() {
        System.out.println("world!!!");
    }
}
