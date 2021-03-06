package com.lzhlyle.poker.terrapin.web.controller;

import com.lzhlyle.poker.terrapin.domain.game.*;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyMessage;
import com.lzhlyle.poker.terrapin.domain.websocket.WiselyResponse;
import com.lzhlyle.poker.terrapin.utility.TerrapinException;
import com.lzhlyle.poker.terrapin.utility.TerrapinNotFoundPlayerException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class GameController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        return "game/index";
    }

    // 进入房间
    @MessageMapping("/login")
    @SendTo({"/topic/log", "/topic/players", "/topic/login"})
    public WiselyResponse login(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room, message.getCode());
        if (game == null) return new WiselyResponse(room, name, null);
        if (message.isPlayer()) game.addPlayer(name);
        else game.addObserver(name);
        return new WiselyResponse(room, name, "已加入");
    }

    // 退出房间
    @MessageMapping("/logout")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse logout(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        if (message.isPlayer()) game.removePlayer(name);
        else game.removeObserver(name);
        return new WiselyResponse(room, name, "已退出");
    }

    // 庄家发牌
    @MessageMapping("/start")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse start(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        game.start(name);
        return new WiselyResponse(room, name, "已发牌，牌局已开始 ========");
    }

    // 摆牌
    @MessageMapping("/adjust")
    @SendToUser("/topic/player")
    public WiselyResponse adjust(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        AbstractPlayer player = game.findAbstractPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.adjust();

        return new WiselyResponse(room, name, null);
    }

    @MessageMapping("/rec")
    @SendTo({"/topic/log"})
    public WiselyResponse rec(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        return new WiselyResponse(room, name, message.getMsg(), true);
    }

    // 闲家求走
    @MessageMapping("/pass")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse pass(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.pass()) return new WiselyResponse(room, name, player.getHandCardStr() + " 求走");

        return new WiselyResponse(room, name, null);
    }

    // 闲家盖牌
    @MessageMapping("/cover")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse cover(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.cover()) return new WiselyResponse(room, name, "盖牌");

        return new WiselyResponse(room, name, null);
    }

    // 闲家强攻
    @MessageMapping("/force")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse force(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.force()) return new WiselyResponse(room, name, player.getHandCardStr() + " 强攻");

        return new WiselyResponse(room, name, null);
    }

    // 庄家对闲家走过
    @MessageMapping("/banker/pass")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPass(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Banker banker = game.findBanker(name);
        if (banker == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = game.findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.pass(player))
            return new WiselyResponse(room, name, "对【" + playerName + "】" + player.getHandCardStr() + " 走过");

        return new WiselyResponse(room, name, null);
    }

    // 闲家信走
    @MessageMapping("/banker/pass/believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPassBelieve(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerPassBelieve()) return new WiselyResponse(room, name, player.getHandCardStr() + " 信走");

        return new WiselyResponse(room, name, null);
    }

    // 闲家不信走
    @MessageMapping("/banker/pass/not-believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerPassNotBelieve(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerPassNotBelieve()) return new WiselyResponse(room, name, player.getHandCardStr() + " 不信走");

        return new WiselyResponse(room, name, null);
    }

    // 闲家信杀
    @MessageMapping("/banker/kill/believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKillBelieve(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerKillBelieve()) return new WiselyResponse(room, name, player.getHandCardStr() + " 信杀");

        return new WiselyResponse(room, name, null);
    }

    // 闲家不信杀
    @MessageMapping("/banker/kill/not-believe")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKillNotBelieve(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Player player = game.findPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (player.bankerKillNotBelieve()) return new WiselyResponse(room, name, player.getHandCardStr() + " 不信杀");

        return new WiselyResponse(room, name, null);
    }

    // 庄家对闲家杀牌
    @MessageMapping("/banker/kill")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerKill(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Banker banker = game.findBanker(name);
        if (banker == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = game.findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.kill(player))
            return new WiselyResponse(room, name, "对【" + playerName + "】" + player.getHandCardStr() + " 杀牌");

        return new WiselyResponse(room, name, null);
    }

    // 庄家对闲家开牌
    @MessageMapping("/banker/turn-over")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerTurnOver(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Banker banker = game.findBanker(name);
        if (banker == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = game.findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.turnOver(player))
            return new WiselyResponse(room, name, "对【" + playerName + "】" + player.getHandCardStr() + " 开牌");

        return new WiselyResponse(room, name, null);
    }

    // 庄家对闲家不开牌
    @MessageMapping("/banker/not-turn-over")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse bankerNotTurnOver(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Banker banker = game.findBanker(name);
        if (banker == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        String playerName = message.getPlayer();
        Player player = game.findPlayer(playerName);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(playerName).getMesssage());

        if (banker.notTurnOver(player))
            return new WiselyResponse(room, name, "对【" + playerName + "】不开牌");

        return new WiselyResponse(room, name, null);
    }

    // 庄家开牌
    @MessageMapping("/open")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse open(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        Banker banker = game.findBanker(name);
        if (banker == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        if (banker.open())
            return new WiselyResponse(room, name, "庄家开牌 " + banker.getHandCardStr());

        return new WiselyResponse(room, name, null);
    }

    // 赢
    @MessageMapping("/win")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse win(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        AbstractPlayer player = game.findAbstractPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.increaseScore(1);

        return new WiselyResponse(room, name, "计分 +1，总计 " + player.getScore().getValue());
    }

    // 输
    @MessageMapping("/lose")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse lose(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        AbstractPlayer player = game.findAbstractPlayer(name);
        if (player == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException(name).getMesssage());

        player.decreaseScore(1);

        return new WiselyResponse(room, name, "计分 -1，总计 " + player.getScore().getValue());
    }

    // 记分清零
    @MessageMapping("/rescore")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse rescore(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        List<AbstractPlayer> players = game.getAbstractPlayers();
        if (players == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException("所有").getMesssage());

        for (AbstractPlayer p : players) {
            p.clearScore();
        }

        return new WiselyResponse(room, name, "清零了所有记分！点错了？赶紧撤销吧", false, true);
    }

    // 撤销记分清零
    @MessageMapping("/unclear")
    @SendTo({"/topic/log", "/topic/players"})
    public WiselyResponse unclear(WiselyMessage message) {
        String name = message.getName();
        String room = message.getRoom();
        SimpleGame game = SimpleMall.getInstance().intoRoom(room);
        if (game == null) return new WiselyResponse(room, name, "找不到房间");
        List<AbstractPlayer> players = game.getAbstractPlayers();
        if (players == null)
            return new WiselyResponse(room, name, new TerrapinNotFoundPlayerException("所有").getMesssage());

        for (AbstractPlayer p : players) {
            p.unClearScore();
        }

        return new WiselyResponse(room, name, "恢复了所有记分", false, false);
    }
}
