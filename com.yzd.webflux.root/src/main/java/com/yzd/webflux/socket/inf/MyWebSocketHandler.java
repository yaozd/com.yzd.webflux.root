package com.yzd.webflux.socket.inf;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MyWebSocketHandler extends WebSocketHandler {

    //Map<String, Timer> timer = new HashMap<>();// 定时器

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

        String sessionId=session.getId();
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
                if(HeartMapSingleton.getInstance().getCount().containsKey(sessionId)){
                    HeartMapSingleton.getInstance().getCount().get(sessionId).set(0);
                }
            }
            // 如果是TEXT，调用用户接口并将结果回复给client
            if (m.getType().equals(WebSocketMessage.Type.TEXT)){
                hotSource.onNext(session.textMessage(messageHandle.apply(msg)));
            }
        }).then();
        // 开启PING。
        // 每发送一个PING，则必须等待一个PONG。如果client超时，则中断连接，并调用用户的close handle
        ExecutorSingleton.getInstance().getThreadPool().execute(() -> {
            AtomicBoolean isClose = new AtomicBoolean(false);
            while (!isClose.get()) {
               ThreadUtil.sleep(1000);
               if(!HeartMapSingleton.getInstance().getCount().containsKey(sessionId)){
                   HeartMapSingleton.getInstance().getCount().put(sessionId,new AtomicInteger(0));
               }
               //超时规则：心跳超过5次，关闭连接
               if(HeartMapSingleton.getInstance().getCount().get(sessionId).get()>4){
                   HeartMapSingleton.getInstance().getCount().remove(sessionId);
                   hotSource.onComplete();
                   closeHandle.get();
                   isClose.set(true);
                   break;
               }
                HeartMapSingleton.getInstance().getCount().get(sessionId).incrementAndGet();
                // 发送PING，client回复PONG后，再重新计数。
                String uuid = UUID.randomUUID().toString();
                hotSource.onNext(session.pingMessage(dbf -> { DataBuffer db = dbf.allocateBuffer(uuid.length());return db.write(uuid.getBytes()); }));
/*                // 如果数据已经被清空，说明是第一次或client已经回复PONG，则发送下一次PING
                if (timer.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
//					System.out.println("ping id=" + uuid);

                    // 将定时器记录下来
                    timer.put(uuid,
                            TimeUtils.countDown(endTime, t -> {
//								System.out.println("倒计时时间到，关闭连接 uuid=" + uuid);
                                hotSource.onComplete();
                                timer.remove(uuid);
                                isClose.set(true);
                                closeHandle.get();
                            }));
                    if (isClose.get())
                        break;
                    // 发送PING
                    hotSource.onNext(
                            session.pingMessage(dbf -> {
                                DataBuffer db = dbf
                                        .allocateBuffer(uuid.length());
                                return db.write(uuid.getBytes());
                            }));
                }*/
            }
        });

        return Mono.zip(input, output).then();
    }
}
