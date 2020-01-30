package com.lzhlyle.poker.terrapin.web.api;

import com.lzhlyle.poker.terrapin.domain.game.SimpleMall;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping("/api/game")
public class GameRest {

    @RequestMapping(value = "/rooms/query", method = RequestMethod.GET)
    public Set<String> queryRooms(HttpServletRequest request) {
        return SimpleMall.getInstance().getMap().keySet();
    }
}
