package io.github.zimoyin.qqbot.event.events.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.BotReadyHandler
import io.github.zimoyin.qqbot.net.bean.BotUser


/**
 * 机器人准备就绪 / 首次上线事件（READY）。
 *
 * 当机器人完成鉴权并首次上线成功时，QQ 服务器会下发 READY 事件。
 * 这标志着机器人已完全连接到服务器，可以正常收发消息和事件。
 *
 * 与 {@link BotResumedEvent} 的区别：
 * - {@code BotReadyEvent}：首次上线或会话无法恢复时的全新连接
 * - {@code BotResumedEvent}：断开重连后成功恢复了之前的会话状态
 *
 * **重要说明：**
 * - 收到此事件后，机器人应进行 session 相关信息的记录（如 {@code sessionID}），
 *   以便在后续断开重连时进行会话恢复（Resume）。
 * - {@code shard} 数组格式为 {@code [当前分片索引, 总分片数量]}，
 *   用于多实例部署场景下的分片管理。
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "READY"
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @param version 协议版本号，标识当前使用的 QQ Bot API 协议版本
 * @param sessionID WebSocket 会话的唯一标识符，用于断线重连时的会话恢复（Resume）
 * @param user 机器人用户信息，包含用户 ID、用户名、头像等基本信息
 * @param shard 分片信息，格式为 {@code [shardId, totalShards]}。
 *              {@code shardId} 为当前实例的分片索引（从 0 开始），
 *              {@code totalShards} 为总分片数量
 * @author : zimo
 * @date : 2023/12/20
 * @see BotResumedEvent
 * @see BotStatusEvent
 */
@EventAnnotation.EventMetaType("READY")
@EventAnnotation.EventHandler(BotReadyHandler::class)
data class BotReadyEvent(
    override val metadata: String,
    override val metadataType: String = "READY",
    override val botInfo: BotInfo,
    override val eventID: String ="",
    /**
     * 协议版本号。
     */
    val version: Int,
    /**
     * WebSocket 会话的唯一标识符，用于断线重连时的会话恢复。
     */
    val sessionID: String,
    /**
     * 机器人用户信息。
     */
    val user: BotUser,
    /**
     * 分片信息，格式为 [shardId, totalShards]。
     * shardId 为当前分片索引，totalShards 为总分片数量。
     */
    val shard: List<Int>
) : BotStatusEvent
