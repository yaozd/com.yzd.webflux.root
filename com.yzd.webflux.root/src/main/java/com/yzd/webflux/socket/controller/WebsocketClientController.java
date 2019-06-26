package com.yzd.webflux.socket.controller;

import com.yzd.webflux.socket.inf.HeartMapSingleton;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by jack 2018/6/2
 */
@Controller
public class WebsocketClientController {
    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public String websocketClient() {
        return "websocket-client";
    }
    @RequestMapping(value = "/heart", method = RequestMethod.GET)
    public String heart() {
        return "heart";
    }

    /**
     * 在线人数
     * @return
     */
    @RequestMapping(value = "/online", method = RequestMethod.GET)
    public String online(Model model) {
        //测试使用 begin
        Map<String, AtomicInteger> m=HeartMapSingleton.getInstance().getCount();
        for (Map.Entry<String,AtomicInteger> entry : m.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        //测试使用 end
        model.addAttribute("size",HeartMapSingleton.getInstance().getCount().size());
        return "online";
    }
}
