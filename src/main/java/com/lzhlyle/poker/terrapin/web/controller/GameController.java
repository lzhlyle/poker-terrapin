package com.lzhlyle.poker.terrapin.web.controller;

import com.lzhlyle.poker.terrapin.domain.game.Player;
import com.lzhlyle.poker.terrapin.domain.game.SimpleGame;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyMessage;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyResponse;
import com.lzhlyle.poker.terrapin.utility.TerrapinException;
import com.lzhlyle.poker.terrapin.utility.TerrapinNotFoundPlayerException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GameController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        return "game/index";
    }

    @MessageMapping("/login") // 路径映射
    @SendTo({"/topic/log", "/topic/players"}) // 当服务端有消息时，会对订阅了@SendTo中的路径的浏览器发送消息
    public WiselyResponse login(WiselyMessage message) {
        String name = message.getName();
        SimpleGame.getInstance().addPlayer(name);
        return new WiselyResponse(name + " 已加入");
    }

    @MessageMapping("/logout")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse logout(WiselyMessage message) {
        String name = message.getName();
        SimpleGame.getInstance().removePlayer(name);
        return new WiselyResponse(name + " 已退出");
    }

    @MessageMapping("/start")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse start(WiselyMessage message) {
        SimpleGame.getInstance().start();
        return new WiselyResponse("【" + message.getName() + "】已发牌，牌局已开始 =========");
    }

    @MessageMapping("/adjust")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse adjust(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.getHandCard().isLock()) {
            String msg = "【" + name + "】想重新摆牌？手牌已锁定，不可再摆！";
            return new WiselyResponse(new TerrapinException(msg).getMesssage());
        }

        player.adjust();
        return new WiselyResponse("【" + name + "】已重新摆牌");
    }

    @MessageMapping("/turnOver")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse turnOver(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(new TerrapinNotFoundPlayerException(name).getMesssage());

        player.turnOver();
        return new WiselyResponse("【" + name + "】选择开牌：" + player.getHandCardStr());
    }

    @MessageMapping("/rec")
    @SendTo({"/topic/log"})
    public WiselyResponse rec(WiselyMessage message) {
        return new WiselyResponse("【" + message.getName() + "】记录：" + message.getMsg());
    }
}
