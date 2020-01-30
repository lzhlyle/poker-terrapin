package com.lzhlyle.poker.terrapin.web.controller;

import com.lzhlyle.poker.terrapin.domain.websocket.WiselyMessage;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WcController {
//    @MessageMapping("/welcome") // 路径映射
//    @SendTo("/topic/getResponse") // 当服务端有消息时，会对订阅了@SendTo中的路径的浏览器发送消息
//    public WiselyResponse say(WiselyMessage message) throws InterruptedException {
//        Thread.sleep(500);
//        return new WiselyResponse(-99894385, message.getName(), "已加入");
//    }
}
