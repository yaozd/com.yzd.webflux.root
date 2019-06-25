package com.yzd.webflux.socket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
