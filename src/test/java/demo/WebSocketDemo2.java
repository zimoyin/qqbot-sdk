package demo;

import io.github.zimoyin.qqbot.Config;
import io.github.zimoyin.qqbot.LocalLogger;
import io.github.zimoyin.qqbot.bot.Bot;
import io.github.zimoyin.qqbot.event.events.Event;
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus;
import io.github.zimoyin.qqbot.net.Intents;
import io.github.zimoyin.qqbot.net.Token;
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient;

/**
 * @author : zimo
 * &#064;date : 2025/04/24
 */
public class WebSocketDemo2 {
    static LocalLogger logger = LocalLogger.getLogger();
    static String appId = "xxx";
    static String appSecret = "xxx";
    static String token = "xxx";
    static int useTokenVersion = 2;

    public static void main(String[] args) {
        // 设置沙盒环境
        TencentOpenApiHttpClient.setSandBox(true);

        // 配置转发服务器地址
        TencentOpenApiHttpClient.setHost("zimoyin.xyz",8080);
        // 配置客户端使用上面定义的服务器地址，如果不配置的话也能根据情况自动选择
        TencentOpenApiHttpClient.setUseCustomHost(true);
        // 配置服务器转发地址，默认为 /websocket
        TencentOpenApiHttpClient.setWebSocketForwardingPath("/websocket");

        // 获取 Token
        Token token = Token.create(appId, WebSocketDemo2.token, appSecret).version(useTokenVersion);

        // 监听全局事件（该事件在所有的 Vertx 上传播）
        // 第二个参数可以省略，设置为 true 后则事件处理逻辑运行在工作线程
        GlobalEventBus.INSTANCE.onEvent(Event.class, false, event -> {
            logger.info("收到事件：" + event);
        });

        // 创建Bot
        Bot bot = Bot.createBot(config -> {
            config.setToken(token);
            config.setIntents(Intents.Presets.PUBLIC_GROUP_INTENTS);
        });

        // 监听在当前机器人总线上传播的事件
        // 第二个参数可以省略，设置为 true 后则事件处理逻辑运行在工作线程
        bot.onEvent(Event.class, false, event -> {
            logger.info("收到事件：" + event.toString());
        });

        // 登录 设置 false 则不验证主机 ssl
        bot.login(false).onSuccess(ws -> {
            logger.info("登录成功");
        }).onFailure(err -> {
            logger.error("登录失败", err);
            bot.close();
            Config.GLOBAL_VERTX_INSTANCE().close();
        });
    }
}
