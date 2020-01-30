package com.lzhlyle.poker.terrapin.domain.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleMall {
    private static SimpleMall ourInstance = new SimpleMall();

    public static SimpleMall getInstance() {
        return ourInstance;
    }

    private Map<String, SimpleGame> map;

    private SimpleMall() {
        map = new HashMap<>();
    }

    public SimpleGame intoRoom(String room, String code) {
        SimpleGame res;
        if (map.containsKey(room) && (res = map.get(room)) != null) {
            if (Objects.equals(res.getCode(), code)) return res; // 成功进入
            return null; // 邀请码错误
        }

        // 新开房间
        res = new SimpleGame(code);
        map.put(room, res);
        System.out.println("================= room: " + room + ", code: " + code + ", total: " + map.size());
        return res;
    }

    public SimpleGame intoRoom(String room) {
        if (map.containsKey(room)) return map.get(room);
        return null;
    }

    public Map<String, SimpleGame> getMap() {
        return map;
    }
}
