package io.github.zimoyin.qqbot.test.demo;

import io.github.zimoyin.qqbot.Config;
import io.github.zimoyin.qqbot.LocalLogger;
import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.bot.message.type.ImageMessage;
import io.github.zimoyin.qqbot.event.events.Event;
import io.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent;
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent;
import io.github.zimoyin.qqbot.event.events.message.MessageEvent;
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient;
import io.github.zimoyin.qqbot.net.http.api.API;
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig;

/**
 * @author : zimo
 * @date : 2024/11/26
 */
public class TMain2 {
    public static void run(Token token) {
        String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

        LocalLogger logger = new LocalLogger("Main");

        //全局事件监听
        GlobalEventBus.INSTANCE.onEvent(Event.class, true, event -> {
            logger.info("收到事件：" + event.toString());
        });

        TencentOpenApiHttpClient.setSandBox(false);

        Bot bot = Bot.createBot(token);

        API.setDebug(true);
        bot.getContext().set("internal.isAbnormalCardiacArrest", true);
        bot.getContext().set("internal.headerCycle", 5 * 1000);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", true);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false);


        // 监听事件
        bot.onEvent(OpenGroupBotEvent.class, true, event -> {
            event.reply("你好");
        });

        bot.onEvent(MessageEvent.class, true, event -> {
            if (event instanceof ChannelMessageEvent) System.out.println("ChannelMessageEvent 事件");
            else event.reply("你好");
        });

        bot.onEvent(ChannelMessageEvent.class, true, event -> {
            event.reply(ImageMessage.create(url));
        });

        WebHookConfig webHookConfig = WebHookConfig
            .builder()
            .sslPath("./127.0.0.1")
            .isSSL(true)
            .port(8080)
            .password("")
            .enableWebSocketForwarding(true)
            .enableWebSocketForwardingLoginVerify(true)
            .build();

        bot.start(webHookConfig).onSuccess(ws -> {
            logger.info("服务器启动成功,端口: " + ws.getServer().actualPort());
            System.out.println("OK");
        }).onFailure(e -> {
            logger.error("登录失败", e);
            bot.close();
            Config.GLOBAL_VERTX_INSTANCE().close();
        });

    }
}
