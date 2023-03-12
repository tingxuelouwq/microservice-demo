package com.kevin.microservice.websocket;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 记录当前在线连接数
     */
    public static AtomicInteger online = new AtomicInteger();

    /**
     * 用来存放每个客户端对应的WebSocketServer对象
     */
    public static Map<String, List<Session>> sessionPools = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session,  @PathParam("userId") String userId) {
        List<Session> sessions = sessionPools.get(userId);
        if (sessions == null) {
            sessions = new ArrayList<>();
            sessions.add(session);
            sessionPools.put(userId, sessions);
        } else {
            sessions.add(session);
        }
        online.incrementAndGet();
        logger.info("===新的连接加入！当前的链接数为：{}，新加入的用户id为：{}", online.get(), userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessionPools.remove(userId);
        online.decrementAndGet();
        logger.info("==={}连接关闭！当前的链接数为：{}", userId, online.get());
    }

    @OnMessage
    public void onMessage(String message) {
        JSONObject msg = JSONObject.parseObject(message);
        String userId = msg.getString("loginUserId");
        List<Session> sessions = sessionPools.get(userId);
        if (!CollectionUtils.isEmpty(sessions)) {
            logger.info("推送到用户:{}, 推送消息:{}", userId, message);
            for (Session session : sessions) {
                session.getAsyncRemote().sendText(message);
            }
        } else {
            logger.info("未获取到{}对应的会话, 可能长连接未成功建立", userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("===发生未知错误----", throwable);
    }
}
