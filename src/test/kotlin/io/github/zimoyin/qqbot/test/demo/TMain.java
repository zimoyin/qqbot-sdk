package io.github.zimoyin.qqbot.test.demo;

import io.github.zimoyin.qqbot.Config;
import io.github.zimoyin.qqbot.LocalLogger;
import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.bot.message.type.*;
import io.github.zimoyin.qqbot.event.events.Event;
import io.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent;
import io.github.zimoyin.qqbot.event.events.message.MessageEvent;
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient;
import io.github.zimoyin.qqbot.net.http.api.API;

/**
 * @author : zimo
 * @date : 2024/11/26
 */
public class TMain {
    public static void run(Token token) {
        token.version(1);
        long start = System.currentTimeMillis();
        long start2 = start;
        String url = "http://ts1.cn.mm.bing.net/th/id/R-C.23034dbcaded6ab4169b9514f76f51b5?rik=mSGADwV9o/teUA&riu=http://pic.bizhi360.com/bbpic/40/9640_1.jpg&ehk=RYei4n5qyNCPVysJmE2a3WhxSOXqGQMGJcvWBmFyfdg=&risl=&pid=ImgRaw&r=0";

        LocalLogger logger = new LocalLogger("Main");

        //全局事件监听
        GlobalEventBus.INSTANCE.onEvent(Event.class, false, event -> {
            logger.info("收到事件：" + event.toString());
        });

        System.out.println("Vertx 与组件初始化耗时: " + (System.currentTimeMillis() - start));
        start2 = System.currentTimeMillis();

        TencentOpenApiHttpClient.setSandBox(false);

        Token finalToken = token;
        Bot bot = Bot.createBot(config -> {
            config.setToken(finalToken);
            config.setIntents(Intents.Presets.PUBLIC_GROUP_INTENTS);
        });


        System.out.println("Bot 创建耗时: " + (System.currentTimeMillis() - start));
        System.out.println("Bot 创建耗时: " + (System.currentTimeMillis() - start2));
        start2 = System.currentTimeMillis();

        bot.getConfig().setRetry(99);
//        bot.getContext().set("SESSION_ID", "d5141070-a591-47fa-b334-8ed1eff92ec6");
        API.setDebug(true);
        bot.getContext().set("internal.isAbnormalCardiacArrest", true);
        bot.getContext().set("internal.headerCycle", 5 * 1000);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_LOG", true);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_MATA_DATA_LOG", false);
        bot.getContext().set("PAYLOAD_CMD_HANDLER_DEBUG_HEART_BEAT", false);

        bot.onEvent(OpenGroupBotEvent.class, false, event -> {
            event.reply("你好");
        });

        bot.onEvent(MessageEvent.class, false, event -> {
//            event.reply(AudioMessage.create(new File("./out/your_audio.silk")));
//            event.reply(ImageMessage.create(new File("./out/E]5DVT{`HRO)Y6}3L)5]D{X_x4_Av.jpg")));
//            event.reply(VideoMessage.create(new File("./out/redpandacompress_美爆！为你在嘉应学院下的第一场雪.mp4")));
            System.out.println(event.getMessageChain());
            for (ImageMessage image : event.getMessageChain().getImages()) {
                System.out.println(image.getAttachment().getURL());
            }
//            event.reply(new At(event.getSender().getId()));
        });

        long finalStart = start2;
        long finalStart1 = start2;
        bot.login().onSuccess(ws -> {
            logger.info("登录成功");
            System.out.println("OK");
            System.out.println("启动耗时: " + (System.currentTimeMillis() - start));
            System.out.println("启动耗时: " + (System.currentTimeMillis() - finalStart));
        }).onFailure(e -> {
            logger.error("登录失败", e);
            bot.close();
            Config.GLOBAL_VERTX_INSTANCE().close();
            System.out.println("启动耗时: " + (System.currentTimeMillis() - start));
            System.out.println("启动耗时: " + (System.currentTimeMillis() - finalStart1));
        });
    }

}
