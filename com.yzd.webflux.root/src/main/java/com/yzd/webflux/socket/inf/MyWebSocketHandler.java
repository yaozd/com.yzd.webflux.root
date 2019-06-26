package com.yzd.webflux.socket.inf;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MyWebSocketHandler extends WebSocketHandler {

    /**
     * 通用ws方法
     *
     * @param session       WebSocketSession
     * @param messageHandle 消息处理器
     * @param closeHandle   连接关闭处理器
     * @param endTime       超时次数（目前每一次是1秒）
     * @return Mono<Void>
     */
    default Mono<Void> handle(WebSocketSession session, Function<String, String> messageHandle, Supplier closeHandle, int endTime) {

        String sessionId = session.getId();
        if (StrUtil.isBlank(sessionId)) {
            System.out.println("is blank");
        }
        // 开启热源
        UnicastProcessor<WebSocketMessage> hotSource = UnicastProcessor.create();
        Flux<WebSocketMessage> hotFlux = hotSource.publish().autoConnect();

        // 向client发送数据
        Mono<Void> output = session.send(hotFlux);

        // 接收处理数据
        Mono<Void> input = session.receive().doOnNext(m -> {
            String msg = m.getPayloadAsText();
            //System.out.println("收到类型：" + m.getType() + " 收到的数据："+ msg);
            // 如果是client回复的PONG，，再重新计数。
            if (m.getType().equals(WebSocketMessage.Type.PONG)) {
                //System.out.println(sessionId);
                if (HeartMapSingleton.getInstance().getCount().containsKey(sessionId)) {
                    HeartMapSingleton.getInstance().getCount().get(sessionId).set(0);
                }
            }
            // 如果是TEXT，调用用户接口并将结果回复给client
            if (m.getType().equals(WebSocketMessage.Type.TEXT)) {
                hotSource.onNext(session.textMessage(messageHandle.apply(msg)));
            }
        }).then();
        // 开启PING。
        // 每发送一个PING，则必须等待一个PONG。如果client超时，则中断连接，并调用用户的close handle
        ExecutorSingleton.getInstance().getThreadPool().execute(() -> {
            AtomicBoolean isClose = new AtomicBoolean(false);
            while (!isClose.get()) {
                //
                if (!HeartMapSingleton.getInstance().getCount().containsKey(sessionId)) {
                    HeartMapSingleton.getInstance().getCount().put(sessionId, new AtomicInteger(0));
                }
                //超时规则：心跳超过5次，关闭连接
                if (HeartMapSingleton.getInstance().getCount().get(sessionId).incrementAndGet() > endTime) {
                    HeartMapSingleton.getInstance().getCount().remove(sessionId);
                    hotSource.onComplete();
                    closeHandle.get();
                    isClose.set(true);
                    break;
                }
                ThreadUtil.sleep(1000);
                // 发送PING，client回复PONG后，再重新计数。
                try {
                    hotSource.onNext(session.pingMessage(dbf -> {
                        String uuid = UUID.randomUUID().toString();
                        DataBuffer db = dbf.allocateBuffer(uuid.length());
                        return db.write(uuid.getBytes());
                    }));
                } catch (Exception e) {
                    System.out.println("hotSource.onNext-出错了。");
                    e.printStackTrace();
                }

            }
        });

        return Mono.zip(input, output).then();
    }
}
