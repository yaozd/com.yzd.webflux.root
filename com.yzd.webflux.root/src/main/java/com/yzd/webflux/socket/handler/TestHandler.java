package com.yzd.webflux.socket.handler;

import cn.hutool.core.date.DateUtil;
import com.yzd.webflux.socket.inf.MyWebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class TestHandler implements MyWebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String id= session.getId();
        //通过请求地址中带参数，来传递token
        String token= session.getHandshakeInfo().getUri().getQuery();
        //System.out.println(id);
        //WebSocketHandler.handle
        return handle(session, processor, () ->
        {
            System.out.println("关闭了,id="+id);
            return null;
        }, 5);
    }

    /**
     *
     */
    private Function<String, String> processor = in -> {
        return "服务响应："+in+"；时间："+ DateUtil.now();
    };
}

