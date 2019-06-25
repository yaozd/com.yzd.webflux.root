package com.yzd.webflux.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebfluxWebsocketApplication {

	/**
	 * 测试入口
	 * http://localhost:8080/client
	 * http://localhost:8080/heart
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(WebfluxWebsocketApplication.class, args);
	}



}
