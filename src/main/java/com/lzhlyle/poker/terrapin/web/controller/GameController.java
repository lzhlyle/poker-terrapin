package com.lzhlyle.poker.terrapin.web.controller;

import com.lzhlyle.poker.terrapin.domain.game.AbstractPlayer;
import com.lzhlyle.poker.terrapin.domain.game.Banker;
import com.lzhlyle.poker.terrapin.domain.game.Player;
import com.lzhlyle.poker.terrapin.domain.game.SimpleGame;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyMessage;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyResponse;
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
        return new WiselyResponse(name, "已加入");
    }

    @MessageMapping("/logout")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse logout(WiselyMessage message) {
        String name = message.getName();
        SimpleGame.getInstance().removePlayer(name);
        return new WiselyResponse(name, "已退出");
    }

    @MessageMapping("/start")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse start(WiselyMessage message) {
        String name = message.getName();
        SimpleGame.getInstance().start(name);
        return new WiselyResponse(name, "已发牌，牌局已开始 =========");
    }

    @MessageMapping("/adjust")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse adjust(WiselyMessage message) {
        String name = message.getName();
        AbstractPlayer player = SimpleGame.getInstance().findAbstractPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.adjust();

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/rec")
    @SendTo({"/topic/log"})
    public WiselyResponse rec(WiselyMessage message) {
        return new WiselyResponse(message.getName(), message.getMsg(), true);
    }

    @MessageMapping("/pass")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse pass(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.pass()) return new WiselyResponse(name, player.getHandCardStr() + " 求走");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/cover")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse cover(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.cover()) return new WiselyResponse(name, "盖牌");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/force")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse force(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.force()) return new WiselyResponse(name, player.getHandCardStr() + " 强攻");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/pass")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPass(WiselyMessage message) {
        String name = message.getName();
        Banker banker = SimpleGame.getInstance().findBanker(name);
        if (banker == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = SimpleGame.getInstance().findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.pass(player))
            return new WiselyResponse(name, "对【" + playerName + "】" + player.getHandCardStr() + " 走过");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/pass/believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPassBelieve(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerPassBelieve()) return new WiselyResponse(name, player.getHandCardStr() + " 信走");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/pass/not-believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPassNotBelieve(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerPassNotBelieve()) return new WiselyResponse(name, player.getHandCardStr() + " 不信走");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/kill/believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKillBelieve(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerKillBelieve()) return new WiselyResponse(name, player.getHandCardStr() + " 信杀");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/kill/not-believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKillNotBelieve(WiselyMessage message) {
        String name = message.getName();
        Player player = SimpleGame.getInstance().findPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerKillNotBelieve()) return new WiselyResponse(name, player.getHandCardStr() + " 不信杀");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/kill")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKill(WiselyMessage message) {
        String name = message.getName();
        Banker banker = SimpleGame.getInstance().findBanker(name);
        if (banker == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = SimpleGame.getInstance().findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.kill(player))
            return new WiselyResponse(name, "对【" + playerName + "】" + player.getHandCardStr() + " 杀牌");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/turn-over")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerTurnOver(WiselyMessage message) {
        String name = message.getName();
        Banker banker = SimpleGame.getInstance().findBanker(name);
        if (banker == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = SimpleGame.getInstance().findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.turnOver(player))
            return new WiselyResponse(name, "对【" + playerName + "】" + player.getHandCardStr() + " 开牌");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/banker/not-turn-over")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerNotTurnOver(WiselyMessage message) {
        String name = message.getName();
        Banker banker = SimpleGame.getInstance().findBanker(name);
        if (banker == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = SimpleGame.getInstance().findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.notTurnOver(player))
            return new WiselyResponse(name, "对【" + playerName + "】不开牌");

        return new WiselyResponse(name, null);
    }

    @MessageMapping("/open")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse open(WiselyMessage message) {
        String name = message.getName();
        Banker banker = SimpleGame.getInstance().findBanker(name);
        if (banker == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (banker.open())
            return new WiselyResponse(name, "庄家开牌 " + banker.getHandCardStr());

        return new WiselyResponse(name, null);
    }


    @MessageMapping("/win")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse win(WiselyMessage message) {
        String name = message.getName();
        AbstractPlayer player = SimpleGame.getInstance().findAbstractPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.increaseScore(1);

        return new WiselyResponse(name, "计分 +1，总计 " + player.getScore().getValue());
    }

    @MessageMapping("/lose")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse lose(WiselyMessage message) {
        String name = message.getName();
        AbstractPlayer player = SimpleGame.getInstance().findAbstractPlayer(name);
        if (player == null) return new WiselyResponse(name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.decreaseScore(1);

        return new WiselyResponse(name, "计分 -1，总计 " + player.getScore().getValue());
    }
}
