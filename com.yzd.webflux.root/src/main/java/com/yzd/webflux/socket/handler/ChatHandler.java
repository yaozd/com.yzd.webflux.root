package com.yzd.webflux.socket.handler;

import cn.hutool.core.date.DateUtil;
import com.yzd.webflux.socket.inf.MyWebSocketHandler;
import com.yzd.webflux.socket.utils.ChatModel;
import com.yzd.webflux.socket.utils.FastJsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * 聊天
 */
@Component
public class ChatHandler implements MyWebSocketHandler {

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
        ChatModel model=new ChatModel();
        //type:system;usermsg
        model.setType("usermsg");
        model.setMessage("hello "+in);
        model.setName("yzd");

        return FastJsonUtil.serialize(model);
    };
}

